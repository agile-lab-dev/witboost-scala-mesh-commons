package it.agilelab.provisioning.aws.s3.gateway

import cats.implicits._
import it.agilelab.provisioning.commons.audit.Audit
import software.amazon.awssdk.services.s3.model.{ DeleteMarkerEntry, ObjectVersion, S3Object }

import java.io.File

/** A Default S3Gateway implementation with Audit
  * @param s3Gateway: an [[S3Gateway]] instance
  * @param audit: an [[Audit]] instance
  */
class DefaultS3GatewayWithAudit(s3Gateway: S3Gateway, audit: Audit) extends S3Gateway {

  /** Check if a specified object exists
    *
    * @param bucket  : The provided bucket name as [[String]]
    * @param key        : The object key
    * @return Right(true) if object exists
    *         Right(false) if object does not exists
    *         Left(S3GatewayError) otherwise
    */
  override def objectExists(bucket: String, key: String): Either[S3GatewayError, Boolean] = {
    val result = s3Gateway.objectExists(bucket, key)
    auditWithinResult(result, s"ObjectExists(bucket=$bucket,key=$key)")
    result
  }

  /** Create a bucket folder
    *
    * call Audit.info if create bucket folder process are completed successfully
    * otherwise call Audit.error with error message
    *
    * @param bucket: The provided bucket name as [[String]]
    * @param folder: The folder path as [[String]]
    * @return Right() if create bucket folder process works fine
    *         Left(Error) otherwise
    */
  override def createFolder(bucket: String, folder: String): Either[S3GatewayError, Unit] = {
    val result = s3Gateway.createFolder(bucket, folder)
    auditWithinResult(result, s"CreateFolder(bucket=$bucket,folder=$folder)")
    result
  }

  /** Create a file inside the specified bucket with the specified key and content
    *
    * call Audit.info if create file process is completed successfully
    * otherwise call Audit.error with error message
    *
    * @param bucket: The provided bucket name as [[String]]
    * @param key: The file key as [[String]]
    * @return Right() if create file process works fine
    *         Left(Error) otherwise
    */
  override def createFile(bucket: String, key: String, file: File): Either[S3GatewayError, Unit] = {
    val result = s3Gateway.createFile(bucket, key, file)
    auditWithinResult(result, s"CreateFile(bucket=$bucket,key=$key,file=${file.getPath})")
    result
  }

  /** Get bucket object content as Byte Array
    *
    * call Audit.info if get object content process is completed successfully
    * otherwise call Audit.error with error message
    *
    * @param bucket the bucket name as [[String]]
    * @param key the object key as [[String]]
    * @return Right(Array[Byte]) if create bucket folder process works fine
    *         Left(Error) otherwise
    */
  override def getObjectContent(
    bucket: String,
    key: String
  ): Either[S3GatewayError, Array[Byte]] = {
    val result = s3Gateway.getObjectContent(bucket, key)
    auditWithinResult(result, s"GetObjectContent(bucket=$bucket,key=$key)")
    result
  }

  /** list objects from a bucket
    *
    * call Audit.info if list objects process is completed successfully
    * otherwise call Audit.error with error message
    *
    * @param bucket the bucket name
    * @param prefix optional prefix
    * @return Right(Iterator[S3Object]) if list objects process works fine
    *         Left(Error) otherwise
    */
  override def listObjects(
    bucket: String,
    prefix: Option[String]
  ): Either[S3GatewayError, Iterator[S3Object]] = {
    val result = s3Gateway.listObjects(bucket, prefix)
    auditWithinResult(result, s"ListObjects(bucket=$bucket,prefix=${prefix.getOrElse("")})")
    result
  }

  /** list versions from a bucket
    *
    * call Audit.info if list versions process is completed successfully
    * otherwise call Audit.error with error message
    *
    * @param bucket the bucket name
    * @param prefix optional prefix
    * @return Right(Iterator[ObjectVersion]) if list versions process works fine
    *         Left(Error) otherwise
    */
  override def listVersions(
    bucket: String,
    prefix: Option[String]
  ): Either[S3GatewayError, Iterator[ObjectVersion]] = {
    val result = s3Gateway.listVersions(bucket, prefix)
    auditWithinResult(result, s"ListVersions(bucket=$bucket,prefix=${prefix.getOrElse("")})")
    result
  }

  /** list delete markers from a bucket
    *
    * call Audit.info if list versions process is completed successfully
    * otherwise call Audit.error with error message
    *
    * @param bucket the bucket name
    * @param prefix optional prefix
    * @return Right(Iterator[DeleteMarkerEntry]) if list delete markers process works fine
    *         Left(Error) otherwise
    */
  override def listDeleteMarkers(
    bucket: String,
    prefix: Option[String]
  ): Either[S3GatewayError, Iterator[DeleteMarkerEntry]] = {
    val result = s3Gateway.listDeleteMarkers(bucket, prefix)
    auditWithinResult(result, s"ListDeleteMarkers(bucket=$bucket,prefix=${prefix.getOrElse("")})")
    result
  }

  private def auditWithinResult[A](
    result: Either[S3GatewayError, A],
    action: String
  ): Unit =
    result match {
      case Right(_) => audit.info(show"$action completed successfully")
      case Left(l)  => audit.error(show"$action failed. Details: $l")
    }

}
