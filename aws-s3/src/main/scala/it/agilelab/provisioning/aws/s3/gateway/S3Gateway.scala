package it.agilelab.provisioning.aws.s3.gateway

import it.agilelab.provisioning.aws.s3.gateway.S3GatewayError.S3GatewayInitError
import it.agilelab.provisioning.commons.audit.Audit
import software.amazon.awssdk.services.s3.S3Client
import software.amazon.awssdk.services.s3.model.{ DeleteMarkerEntry, ObjectVersion, S3Object }

import java.io.File

/** S3Gateway
  * Provide set of functionalities to easily interact with AWS S3 Service
  */
trait S3Gateway {

  /** Check if a specified object exists
    * @param bucket: The provided bucket name as [[String]]
    * @param key: The object key
    * @return Right(true) if object exists
    *         Right(false) if object does not exists
    *         Left(S3GatewayError) otherwise
    */
  def objectExists(bucket: String, key: String): Either[S3GatewayError, Boolean]

  /** Create a bucket folder
    * @param bucket: The provided bucket name as [[String]]
    * @param folder: The folder path as [[String]]
    * @return Right() if create bucket folder process works fine
    *         Left(Error) otherwise
    */
  def createFolder(bucket: String, folder: String): Either[S3GatewayError, Unit]

  /** Create a file inside the specified bucket with the specified key and content
    * @param bucket: the destination bucket
    * @param key: key of the file
    * @param file: content of the file
    * @return Right() if create file process works fine
    *         Left(Error) otherwise
    */
  def createFile(bucket: String, key: String, file: File): Either[S3GatewayError, Unit]

  /** Get bucket object content as Byte Array
    * @param bucket the bucket name
    * @param key the object key
    * @return Right(Array[Byte]) if get object content process works fine
    *         Left(Error) otherwise
    */
  def getObjectContent(bucket: String, key: String): Either[S3GatewayError, Array[Byte]]

  /** List objects in a bucket
    * @param bucket the bucket name
    * @param prefix optional prefix
    * @return Right(Iterator[S3Object]) if list objects succeeded
    *         Left(Error) otherwise
    */
  def listObjects(bucket: String, prefix: Option[String]): Either[S3GatewayError, Iterator[S3Object]]

  /** List versions in a bucket
    * @param bucket the bucket name
    * @param prefix optional prefix
    * @return Right(Iterator[ObjectVersion]) if list versions succeeded
    *         Left(Error) otherwise
    */
  def listVersions(bucket: String, prefix: Option[String]): Either[S3GatewayError, Iterator[ObjectVersion]]

  /** List delete markers in a bucket
    * @param bucket the bucket name
    * @param prefix optional prefix
    * @return Right(Iterator[DeleteMarkerEntry]) if list delete markers succeeded
    *         Left(Error) otherwise
    */
  def listDeleteMarkers(bucket: String, prefix: Option[String]): Either[S3GatewayError, Iterator[DeleteMarkerEntry]]

}

/** S3Gateway Companion Object
  * Create an S3Gateway instance
  */
object S3Gateway {

  /** Create a [[DefaultS3Gateway]] instance
    * Automatically create an instance of [[S3Client]]
    * @return Right(S3Gateway)
    *         Left(S3GatewayError)
    */
  def default(): Either[S3GatewayError, S3Gateway]          =
    try {
      val s3Client = S3Client.builder().build()
      Right(new DefaultS3Gateway(s3Client))
    } catch { case t: Throwable => Left(S3GatewayInitError(t)) }

  /** Create a [[DefaultS3GatewayWithAudit]] instance
    * Automatically create an instance of [[S3Client]]
    * @return Right(S3Gateway)
    *         Left(S3GatewayError)
    */
  def defaultWithAudit(): Either[S3GatewayError, S3Gateway] =
    default().map(new DefaultS3GatewayWithAudit(_, Audit.default("S3Gateway")))

}
