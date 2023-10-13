package it.agilelab.provisioning.aws.s3.gateway

import cats.implicits._
import it.agilelab.provisioning.aws.s3.gateway.S3GatewayError.{
  CreateFileErr,
  CreateFolderErr,
  GetObjectContentErr,
  ListDeleteMarkersErr,
  ListObjectsErr,
  ListVersionsErr,
  ObjectExistsErr
}
import software.amazon.awssdk.core.ResponseInputStream
import software.amazon.awssdk.core.sync.RequestBody
import software.amazon.awssdk.services.s3.S3Client
import software.amazon.awssdk.services.s3.model._

import java.io.{ ByteArrayInputStream, File }
import scala.jdk.CollectionConverters._
import scala.util.Using

/** A Default S3Gateway implementation
  *
  * Provide a default interaction with AWS S3
  */
class DefaultS3Gateway(s3Client: S3Client) extends S3Gateway {

  override def objectExists(bucket: String, key: String): Either[S3GatewayError, Boolean] =
    getHeadObject(bucket, key)
      .map(ho => Option(ho.contentType()).isDefined)
      .leftMap((error: Throwable) => ObjectExistsErr(bucket, key, error))

  override def createFolder(bucket: String, folder: String): Either[S3GatewayError, Unit] =
    try {
      val request     = PutObjectRequest
        .builder()
        .bucket(bucket)
        .key(sanitizeFolder(folder))
        .metadata(Map("Content-Type" -> "application/x-directory").asJava)
        .build()
      val requestBody = RequestBody.fromInputStream(new ByteArrayInputStream(Array.empty[Byte]), 0L)
      s3Client.putObject(request, requestBody)
      Right()
    } catch {
      case t: Throwable => Left(CreateFolderErr(bucket, folder, t))
    }

  override def createFile(bucket: String, key: String, file: File): Either[S3GatewayError, Unit] =
    try {
      val request = PutObjectRequest
        .builder()
        .bucket(bucket)
        .key(key)
        .build()
      s3Client.putObject(request, RequestBody.fromFile(file))
      Right()
    } catch {
      case t: Throwable => Left(CreateFileErr(bucket, key, t))
    }

  override def getObjectContent(bucket: String, key: String): Either[S3GatewayError, Array[Byte]] =
    for {
      o        <- getObject(bucket, key)
      oContent <- Using(o)(_.readAllBytes()).toEither.leftMap(e => GetObjectContentErr(bucket, key, e))
    } yield oContent

  override def listObjects(
    bucket: String,
    prefix: Option[String]
  ): Either[S3GatewayError, Iterator[S3Object]] =
    try {
      val iterator = s3Client
        .listObjectsV2Paginator(
          ListObjectsV2Request
            .builder()
            .bucket(bucket)
            .prefix(prefix.getOrElse(""))
            .build()
        )
        .iterator()
      Right(Iterator.continually(iterator).takeWhile(_.hasNext).flatMap(a => a.next().contents().asScala))
    } catch {
      case t: Throwable => Left(ListObjectsErr(bucket, prefix.getOrElse(""), t))
    }

  override def listVersions(
    bucket: String,
    prefix: Option[String]
  ): Either[S3GatewayError, Iterator[ObjectVersion]] =
    try {
      val iterator = s3Client
        .listObjectVersionsPaginator(
          ListObjectVersionsRequest
            .builder()
            .bucket(bucket)
            .prefix(prefix.getOrElse(""))
            .build()
        )
        .iterator()
      Right(Iterator.continually(iterator).takeWhile(_.hasNext).flatMap(a => a.next().versions().asScala))
    } catch {
      case t: Throwable => Left(ListVersionsErr(bucket, prefix.getOrElse(""), t))
    }

  override def listDeleteMarkers(
    bucket: String,
    prefix: Option[String]
  ): Either[S3GatewayError, Iterator[DeleteMarkerEntry]] =
    try {
      val iterator = s3Client
        .listObjectVersionsPaginator(
          ListObjectVersionsRequest
            .builder()
            .bucket(bucket)
            .prefix(prefix.getOrElse(""))
            .build()
        )
        .iterator()
      Right(Iterator.continually(iterator).takeWhile(_.hasNext).flatMap(a => a.next().deleteMarkers().asScala))
    } catch {
      case t: Throwable => Left(ListDeleteMarkersErr(bucket, prefix.getOrElse(""), t))
    }

  private def getObject(bucket: String, key: String): Either[S3GatewayError, ResponseInputStream[GetObjectResponse]] =
    try Right(s3Client.getObject(GetObjectRequest.builder().bucket(bucket).key(key).build()))
    catch {
      case t: Throwable => Left(GetObjectContentErr(bucket, key, t))
    }

  private def getHeadObject(bucket: String, key: String): Either[Throwable, HeadObjectResponse] =
    try {
      val request = HeadObjectRequest.builder().bucket(bucket).key(key).build()
      Right(s3Client.headObject(request))
    } catch {
      case t: Throwable => Left(t)
    }

  private def sanitizeFolder(value: String): String =
    if (value.endsWith("/")) value
    else value + "/"

}
