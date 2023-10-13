package it.agilelab.provisioning.aws.s3.gateway

import it.agilelab.provisioning.aws.s3.gateway.S3GatewayError.{
  CreateFileErr,
  CreateFolderErr,
  GetObjectContentErr,
  ListDeleteMarkersErr,
  ListObjectsErr,
  ListVersionsErr,
  ObjectExistsErr
}
import it.agilelab.provisioning.commons.audit.Audit
import org.scalamock.scalatest.MockFactory
import org.scalatest.funsuite.AnyFunSuite
import software.amazon.awssdk.core.exception.SdkClientException
import software.amazon.awssdk.services.s3.model.{ DeleteMarkerEntry, ObjectVersion, S3Object }

import java.io.File

class DefaultS3GatewayWithAuditTest extends AnyFunSuite with MockFactory with S3GatewayTestSupport {

  val audit: Audit                       = mock[Audit]
  val defaultS3Gateway: DefaultS3Gateway = mock[DefaultS3Gateway]
  val s3Gateway                          = new DefaultS3GatewayWithAudit(defaultS3Gateway, audit)

  test(s"objectExists call logger on success") {
    inSequence(
      (defaultS3Gateway.objectExists _)
        .expects("bk", "k")
        .once()
        .returns(Right(true)),
      (audit
        .info(_: String))
        .expects(s"ObjectExists(bucket=bk,key=k) completed successfully")
        .once()
    )
    assert(s3Gateway.objectExists("bk", "k") == Right(true))
  }

  test(s"objectExists call logger on failure") {
    inSequence(
      (defaultS3Gateway.objectExists _)
        .expects("bk", "k")
        .once()
        .returns(Left(ObjectExistsErr("bk", "k", SdkClientException.create("x")))),
      (audit
        .error(_: String))
        .expects(where { s: String =>
          s.startsWith(
            "ObjectExists(bucket=bk,key=k) failed. Details: ObjectExistsErr(bk,k,software.amazon.awssdk.core.exception.SdkClientException: x"
          )
        })
        .once()
    )
    assert(s3Gateway.objectExists("bk", "k").isLeft)
  }

  test(s"createFolder call logger on success") {
    inSequence(
      (defaultS3Gateway.createFolder _)
        .expects("bk", "k")
        .once()
        .returns(Right()),
      (audit
        .info(_: String))
        .expects(s"CreateFolder(bucket=bk,folder=k) completed successfully")
        .once()
    )
    assert(s3Gateway.createFolder("bk", "k") == Right())
  }

  test(s"createFolder call logger on failure") {
    inSequence(
      (defaultS3Gateway.createFolder _)
        .expects("bk", "k")
        .once()
        .returns(Left(CreateFolderErr("bk", "k", SdkClientException.create("x")))),
      (audit
        .error(_: String))
        .expects(where { s: String =>
          s.startsWith(
            "CreateFolder(bucket=bk,folder=k) failed. Details: CreateFolderErr(bk,k,software.amazon.awssdk.core.exception.SdkClientException: x"
          )
        })
        .once()
    )
    val actual = s3Gateway.createFolder("bk", "k")
    assertCreateFolderErr(actual, "bk", "k", "x")
  }

  test("createFile call logger on success") {
    val sep = System.getProperty("file.separator")
    inSequence(
      (defaultS3Gateway.createFile _)
        .expects("bk", "k", *)
        .once()
        .returns(Right()),
      (audit
        .info(_: String))
        .expects(
          s"CreateFile(bucket=bk,key=k,file=src${sep}test${sep}resources${sep}my-test-file) completed successfully"
        )
        .once()
    )

    val actual = s3Gateway.createFile(
      "bk",
      "k",
      new File(s"src${sep}test${sep}resources${sep}my-test-file")
    )
    assert(actual == Right())
  }

  test("createFile call logger on failure") {
    val sep = System.getProperty("file.separator")
    inSequence(
      (defaultS3Gateway.createFile _)
        .expects("bk", "k", *)
        .once()
        .returns(Left(CreateFileErr("bk", "k", SdkClientException.create("x")))),
      (audit
        .error(_: String))
        .expects(where { s: String =>
          s.startsWith(
            s"CreateFile(bucket=bk,key=k,file=src${sep}test${sep}resources${sep}my-test-file) failed. Details: CreateFileErr(bk,k,software.amazon.awssdk.core.exception.SdkClientException: x"
          )
        })
        .once()
    )

    val actual = s3Gateway.createFile(
      "bk",
      "k",
      new File(s"src${sep}test${sep}resources${sep}my-test-file")
    )
    assertCreateFileErr(actual, "bk", "k", "x")
  }

  test("getObjectContentAsByteArray call logger on success") {
    inSequence(
      (defaultS3Gateway.getObjectContent _)
        .expects("bk", "k")
        .once()
        .returns(Right(Array(1.toByte))),
      (audit
        .info(_: String))
        .expects(s"GetObjectContent(bucket=bk,key=k) completed successfully")
        .once()
    )

    val actual   = s3Gateway.getObjectContent("bk", "k")
    val expected = Right(Array(1.toByte))
    assert(actual.getOrElse(fail()) sameElements expected.getOrElse(fail()))
  }

  test("getObjectContentAsByteArray call logger on failure") {
    inSequence(
      (defaultS3Gateway.getObjectContent _)
        .expects("bk", "k")
        .once()
        .returns(Left(GetObjectContentErr("bk", "k", SdkClientException.create("x")))),
      (audit
        .error(_: String))
        .expects(where { s: String =>
          s.startsWith(
            s"GetObjectContent(bucket=bk,key=k) failed. Details: GetObjectContentErr(bk,k,software.amazon.awssdk.core.exception.SdkClientException: x"
          )
        })
        .once()
    )

    val actual = s3Gateway.getObjectContent("bk", "k")
    assertGetObjectContentErr(actual, "bk", "k", "x")
  }

  test("listObjects call logger on success") {
    inSequence(
      (defaultS3Gateway.listObjects _)
        .expects("b", Some("p"))
        .once()
        .returns(Right(new Iterator[S3Object] {
          def hasNext        = true
          def next: S3Object = getO(1)
        })),
      (audit
        .info(_: String))
        .expects(s"ListObjects(bucket=b,prefix=p) completed successfully")
        .once()
    )

    val actual = s3Gateway.listObjects("b", Some("p"))
    assert(actual.isRight)
    assert(actual.getOrElse(fail()).next() == getO(1))
  }

  test("listObjects call logger on failure") {
    inSequence(
      (defaultS3Gateway.listObjects _)
        .expects("b", Some("p"))
        .once()
        .returns(Left(ListObjectsErr("b", "p", SdkClientException.create("x")))),
      (audit
        .error(_: String))
        .expects(where { s: String =>
          s.startsWith(
            s"ListObjects(bucket=b,prefix=p) failed. Details: ListObjectsErr(b,p,software.amazon.awssdk.core.exception.SdkClientException: x"
          )
        })
        .once()
    )

    val actual = s3Gateway.listObjects("b", Some("p"))
    assertListObjectsErr(actual, "b", "p", "x")
  }

  test("listVersions call logger on success") {
    inSequence(
      (defaultS3Gateway.listVersions _)
        .expects("b", Some("p"))
        .once()
        .returns(Right(new Iterator[ObjectVersion] {
          def hasNext             = true
          def next: ObjectVersion = getV(1)
        })),
      (audit
        .info(_: String))
        .expects(s"ListVersions(bucket=b,prefix=p) completed successfully")
        .once()
    )

    val actual = s3Gateway.listVersions("b", Some("p"))
    assert(actual.isRight)
    assert(actual.getOrElse(fail()).next() == getV(1))
  }

  test("listVersions call logger on failure") {
    inSequence(
      (defaultS3Gateway.listVersions _)
        .expects("b", Some("p"))
        .once()
        .returns(Left(ListVersionsErr("b", "p", SdkClientException.create("x")))),
      (audit
        .error(_: String))
        .expects(where { s: String =>
          s.startsWith(
            s"ListVersions(bucket=b,prefix=p) failed. Details: ListVersionsErr(b,p,software.amazon.awssdk.core.exception.SdkClientException: x"
          )
        })
        .once()
    )

    val actual = s3Gateway.listVersions("b", Some("p"))
    assertListVersionsErr(actual, "b", "p", "x")
  }

  test("listDeleteMarkers call logger on success") {
    inSequence(
      (defaultS3Gateway.listDeleteMarkers _)
        .expects("b", Some("p"))
        .once()
        .returns(Right(new Iterator[DeleteMarkerEntry] {
          def hasNext                 = true
          def next: DeleteMarkerEntry = getD(1)
        })),
      (audit
        .info(_: String))
        .expects(s"ListDeleteMarkers(bucket=b,prefix=p) completed successfully")
        .once()
    )

    val actual = s3Gateway.listDeleteMarkers("b", Some("p"))
    assert(actual.isRight)
    assert(actual.getOrElse(fail()).next() == getD(1))
  }

  test("listDeleteMarkers call logger on failure") {
    inSequence(
      (defaultS3Gateway.listDeleteMarkers _)
        .expects("b", Some("p"))
        .once()
        .returns(Left(ListDeleteMarkersErr("b", "p", SdkClientException.create("x")))),
      (audit
        .error(_: String))
        .expects(where { s: String =>
          s.startsWith(
            s"ListDeleteMarkers(bucket=b,prefix=p) failed. Details: ListDeleteMarkersErr(b,p,software.amazon.awssdk.core.exception.SdkClientException: x"
          )
        })
        .once()
    )

    val actual = s3Gateway.listDeleteMarkers("b", Some("p"))
    assertListDeleteMarkersErr(actual, "b", "p", "x")
  }
}
