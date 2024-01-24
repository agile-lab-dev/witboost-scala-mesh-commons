package it.agilelab.provisioning.commons.http

import io.circe.CursorOp.DownField
import io.circe.generic.auto._
import io.circe.{ DecodingFailure, ParsingFailure }
import it.agilelab.provisioning.commons.MockServerSuite
import it.agilelab.provisioning.commons.http.Auth._
import it.agilelab.provisioning.commons.http.HttpErrors._
import it.agilelab.provisioning.commons.support.ParserError.DecodeErr
import org.scalatest.funsuite.AnyFunSuite
import org.scalatest.{ Assertion, EitherValues }
import sttp.client3.okhttp.OkHttpSyncBackend

import java.net.UnknownHostException
import java.util.Base64._

class DefaultHttpTest extends AnyFunSuite with MockServerSuite with EitherValues {

  //Used as model to send and retrieve from our api
  case class Sample(id: String, x: Int)

  val http: Http = new DefaultHttp(OkHttpSyncBackend())

  override protected def beforeAll(): Unit =
    super.beforeAll()

  override protected def afterAll(): Unit =
    super.afterAll()

  test("get return ConnectionError") {
    val actual = http.get[Sample]("https://wrong:8080/", Map.empty, NoAuth())
    assertConnectionErr(actual, "Exception when sending request: GET https://wrong:8080/")
  }

  test("get with NoAuth() return Simple(1,1)") {
    mockGetReq(
      path = "/200-b-ok",
      headers = Map.empty,
      responseCode = 200,
      responseBody = Some("""{"id":"1","x":1}""")
    )
    val actual   = http.get[Sample](s"$srvUri/200-b-ok", Map.empty, NoAuth())
    val expected = Right(Sample("1", 1))

    assert(actual == expected)
  }

  test("get with BasicCredential() return Simple(1,1)") {
    mockGetReq(
      path = "/bc/200-b-ok",
      headers = Map("Authorization" -> s"Basic ${getEncoder.encodeToString("usr:pwd".getBytes())}"),
      responseCode = 200,
      responseBody = Some("""{"id":"1","x":1}""")
    )
    val actual   = http.get[Sample](s"$srvUri/bc/200-b-ok", Map.empty, BasicCredential("usr", "pwd"))
    val expected = Right(Sample("1", 1))

    assert(actual == expected)
  }

  test("get with BearerToken() return Simple(1,1)") {
    mockGetReq(
      path = "/bt/200-b-ok",
      headers = Map("Authorization" -> s"Bearer t"),
      responseCode = 200,
      responseBody = Some("""{"id":"1","x":1}""")
    )
    val actual   = http.get[Sample](s"$srvUri/bt/200-b-ok", Map.empty, BearerToken("t", "", "", "", "", 1L))
    val expected = Right(Sample("1", 1))

    assert(actual == expected)
  }

  test("get with NoAuth() return Left(BodyError) with not parsable body") {
    mockGetReq(
      path = "/200-b-ko",
      headers = Map.empty,
      responseCode = 200,
      responseBody = Some("""{"y":"1"}""")
    )
    val expected = Left(
      UnexpectedBodyErr(
        body = """{"y":"1"}""",
        error = DecodeErr(DecodingFailure("Attempt to decode value on failed cursor", List(DownField("id"))))
      )
    )
    assert(http.get[Sample](s"$srvUri/200-b-ko", Map.empty, NoAuth()) == expected)
  }

  test("get with NoAuth() return Left(BodyError) with null body") {
    mockGetReq(
      path = "/200-b-null",
      headers = Map.empty,
      responseCode = 200,
      responseBody = None
    )
    val actual = http.get[Sample](s"$srvUri/200-b-null", Map.empty, NoAuth())
    assertUnexpectedBodyWithParsingFailureErr(actual, "")
  }

  test("get with NoAuth() return Left(ClientError) with 4xx status code") {
    mockGetReq(
      path = "/400",
      headers = Map.empty,
      responseCode = 400,
      responseBody = Some("It's a 400 error")
    )
    val expected = Left(ClientErr(400, "It's a 400 error"))
    assert(http.get[Sample](s"$srvUri/400", Map.empty, NoAuth()) == expected)
  }

  test("get with NoAuth() return Left(ServerError) with 5xx status code") {
    mockGetReq(
      path = "/500",
      headers = Map.empty,
      responseCode = 500,
      responseBody = Some("It's a 500 error")
    )
    val expected = Left(ServerErr(500, "It's a 500 error"))
    assert(http.get[Sample](s"$srvUri/500", Map.empty, NoAuth()) == expected)
  }

  test("get with NoAuth() return Left(GenericError) with xxx status code") {
    mockGetReq(
      path = "/300",
      headers = Map.empty,
      responseCode = 300,
      responseBody = Some("It's a 300 error")
    )
    val expected = Left(GenericErr(300, "It's a 300 error"))
    assert(http.get[Sample](s"$srvUri/300", Map.empty, NoAuth()) == expected)
  }

  test("post return ConnectionError") {
    val actual = http.post[Sample, Unit]("https://wrong:8080/", Map.empty, Sample("1", 1), NoAuth())
    assertConnectionErr(actual, "Exception when sending request: POST https://wrong:8080/")
  }

  test("post with NoAuth() return Some(Simple(1,1))") {
    mockPostReq(
      path = "/200-b-ok",
      headers = Map.empty,
      body = """{"id":"1","x":1}""",
      responseCode = 200,
      responseBody = Some("""{"id":"2","x":2}""")
    )
    val expected = Right(Some(Sample("2", 2)))
    val actual   = http.post[Sample, Sample](s"$srvUri/200-b-ok", Map.empty, Sample("1", 1), NoAuth())
    assert(actual == expected)
  }

  test("post with BasicCredential() return Some(Simple(1,1))") {
    mockPostReq(
      path = "/bc/200-b-ok",
      headers = Map(
        "Authorization" ->
          s"Basic ${getEncoder.encodeToString("usr:pwd".getBytes())}"
      ),
      body = """{"id":"1","x":1}""",
      responseCode = 200,
      responseBody = Some("""{"id":"2","x":2}""")
    )
    val actual   = http.post[Sample, Sample](
      s"$srvUri/bc/200-b-ok",
      Map.empty,
      Sample("1", 1),
      BasicCredential("usr", "pwd")
    )
    val expected = Right(Some(Sample("2", 2)))

    assert(actual == expected)
  }

  test("post with BearerToken() return Some(Simple(1,1))") {
    mockPostReq(
      path = "/bt/200-b-ok",
      headers = Map("Authorization" -> s"Bearer myToken"),
      body = """{"id":"1","x":1}""",
      responseCode = 200,
      responseBody = Some("""{"id":"2","x":2}""")
    )
    val auth     = BearerToken("myToken", "", "", "", "", 1L)
    val actual   = http.post[Sample, Sample](s"$srvUri/bt/200-b-ok", Map.empty, Sample("1", 1), auth)
    val expected = Right(Some(Sample("2", 2)))

    assert(actual == expected)
  }

  test("post with NoAuth() return None with null body") {
    mockPostReq(
      path = "/200-b-null",
      headers = Map.empty,
      body = """{"id":"1","x":1}""",
      responseCode = 200,
      responseBody = None
    )
    val expected = Right(None)
    val actual   = http.post[Sample, Sample](s"$srvUri/200-b-null", Map.empty, Sample("1", 1), NoAuth())
    assert(actual == expected)
  }

  test("post with NoAuth() return None with empty body") {
    mockPostReq(
      path = "/200-b-null",
      headers = Map.empty,
      body = """{"id":"1","x":1}""",
      responseCode = 200,
      responseBody = Some("")
    )
    val expected = Right(None)
    val actual   = http.post[Sample, Sample](s"$srvUri/200-b-null", Map.empty, Sample("1", 1), NoAuth())
    assert(actual == expected)
  }

  test("post with NoAuth() return Left(BodyError) with not parsable body") {
    mockPostReq(
      path = "/200-b-ko",
      headers = Map.empty,
      body = """{"id":"1","x":1}""",
      responseCode = 200,
      responseBody = Some("""{"y":"1"}""")
    )
    val expected = Left(
      UnexpectedBodyErr(
        body = """{"y":"1"}""",
        error = DecodeErr(DecodingFailure("Attempt to decode value on failed cursor", List(DownField("id"))))
      )
    )
    val actual   = http.post[Sample, Sample](s"$srvUri/200-b-ko", Map.empty, Sample("1", 1), NoAuth())

    assert(actual == expected)
  }

  test("post with NoAuth() return Left(ClientError) with 4xx status code") {
    mockPostReq(
      path = "/400",
      headers = Map.empty,
      body = """{"id":"1","x":1}""",
      responseCode = 400,
      responseBody = Some("It's a 400 error")
    )

    val expected = Left(ClientErr(400, "It's a 400 error"))
    assert(http.post[Sample, Sample](s"$srvUri/400", Map.empty, Sample("1", 1), NoAuth()) == expected)
  }

  test("post with NoAuth() return Left(ServerError) with 5xx status code") {
    mockPostReq(
      path = "/500",
      headers = Map.empty,
      body = """{"id":"1","x":1}""",
      responseCode = 500,
      responseBody = Some("It's a 500 error")
    )

    val expected = Left(ServerErr(500, "It's a 500 error"))
    assert(http.post[Sample, Sample](s"$srvUri/500", Map.empty, Sample("1", 1), NoAuth()) == expected)
  }

  test("post with NoAuth() return Left(GenericError) with xxx status code") {
    mockPostReq(
      path = "/300",
      headers = Map.empty,
      body = """{"id":"1","x":1}""",
      responseCode = 300,
      responseBody = Some("It's a 300 error")
    )

    val expected = Left(GenericErr(300, "It's a 300 error"))
    assert(http.post[Sample, Sample](s"$srvUri/300", Map.empty, Sample("1", 1), NoAuth()) == expected)
  }

  test("put return ConnectionError") {
    val actual = http.put[Sample, Unit]("https://wrong:8080/", Map.empty, Sample("1", 1), NoAuth())
    assertConnectionErr(actual, "Exception when sending request: PUT https://wrong:8080/")
  }

  test("put with NoAuth() return Some(Simple(1,1))") {
    mockPutReq(
      path = "/200-b-ok",
      headers = Map.empty,
      body = """{"id":"1","x":1}""",
      responseCode = 200,
      responseBody = Some("""{"id":"2","x":2}""")
    )
    val expected = Right(Some(Sample("2", 2)))
    val actual   = http.put[Sample, Sample](s"$srvUri/200-b-ok", Map.empty, Sample("1", 1), NoAuth())
    assert(actual == expected)
  }

  test("put with BasicCredential() return Some(Simple(1,1))") {
    mockPutReq(
      path = "/bc/200-b-ok",
      headers = Map("Authorization" -> s"Basic ${getEncoder.encodeToString("usr:pwd".getBytes())}"),
      body = """{"id":"1","x":1}""",
      responseCode = 200,
      responseBody = Some("""{"id":"2","x":2}""")
    )
    val actual   = http.put[Sample, Sample](
      s"$srvUri/bc/200-b-ok",
      Map.empty,
      Sample("1", 1),
      BasicCredential("usr", "pwd")
    )
    val expected = Right(Some(Sample("2", 2)))

    assert(actual == expected)
  }

  test("put with BearerToken() return Some(Simple(1,1))") {
    mockPutReq(
      path = "/bt/200-b-ok",
      headers = Map("Authorization" -> s"Bearer myToken"),
      body = """{"id":"1","x":1}""",
      responseCode = 200,
      responseBody = Some("""{"id":"2","x":2}""")
    )
    val auth     = BearerToken("myToken", "", "", "", "", 1L)
    val actual   = http.put[Sample, Sample](s"$srvUri/bt/200-b-ok", Map.empty, Sample("1", 1), auth)
    val expected = Right(Some(Sample("2", 2)))

    assert(actual == expected)
  }

  test("put with NoAuth() return None with null body") {
    mockPutReq(
      path = "/200-b-null",
      headers = Map.empty,
      body = """{"id":"1","x":1}""",
      responseCode = 200,
      responseBody = None
    )
    val expected = Right(None)
    val actual   = http.put[Sample, Sample](s"$srvUri/200-b-null", Map.empty, Sample("1", 1), NoAuth())
    assert(actual == expected)
  }

  test("put with NoAuth() return None with empty body") {
    mockPutReq(
      path = "/200-b-null",
      headers = Map.empty,
      body = """{"id":"1","x":1}""",
      responseCode = 200,
      responseBody = Some("")
    )
    val expected = Right(None)
    val actual   = http.put[Sample, Sample](s"$srvUri/200-b-null", Map.empty, Sample("1", 1), NoAuth())
    assert(actual == expected)
  }

  test("put with NoAuth() return Left(BodyError) with not parsable body") {
    mockPutReq(
      path = "/200-b-ko",
      headers = Map.empty,
      body = """{"id":"1","x":1}""",
      responseCode = 200,
      responseBody = Some("""{"y":"1"}""")
    )
    val expected = Left(
      UnexpectedBodyErr(
        body = """{"y":"1"}""",
        error = DecodeErr(DecodingFailure("Attempt to decode value on failed cursor", List(DownField("id"))))
      )
    )
    val actual   = http.put[Sample, Sample](s"$srvUri/200-b-ko", Map.empty, Sample("1", 1), NoAuth())

    assert(actual == expected)
  }

  test("put with NoAuth() return Left(ClientError) with 4xx status code") {
    mockPutReq(
      path = "/400",
      headers = Map.empty,
      body = """{"id":"1","x":1}""",
      responseCode = 400,
      responseBody = Some("It's a 400 error")
    )

    val expected = Left(ClientErr(400, "It's a 400 error"))
    assert(http.put[Sample, Sample](s"$srvUri/400", Map.empty, Sample("1", 1), NoAuth()) == expected)
  }

  test("put with NoAuth() return Left(ServerError) with 5xx status code") {
    mockPutReq(
      path = "/500",
      headers = Map.empty,
      body = """{"id":"1","x":1}""",
      responseCode = 500,
      responseBody = Some("It's a 500 error")
    )

    val expected = Left(ServerErr(500, "It's a 500 error"))
    assert(http.put[Sample, Sample](s"$srvUri/500", Map.empty, Sample("1", 1), NoAuth()) == expected)
  }

  test("put with NoAuth() return Left(GenericError) with xxx status code") {
    mockPutReq(
      path = "/300",
      headers = Map.empty,
      body = """{"id":"1","x":1}""",
      responseCode = 300,
      responseBody = Some("It's a 300 error")
    )

    val expected = Left(GenericErr(300, "It's a 300 error"))
    assert(http.put[Sample, Sample](s"$srvUri/300", Map.empty, Sample("1", 1), NoAuth()) == expected)
  }

  test("patch return ConnectionError") {
    val actual = http.patch[Sample, Unit]("https://wrong:8080/", Map.empty, Sample("1", 1), NoAuth())
    assertConnectionErr(actual, "Exception when sending request: PATCH https://wrong:8080/")
  }

  test("patch with NoAuth() return Some(Simple(1,1))") {
    mockPatchReq(
      path = "/200-b-ok",
      headers = Map.empty,
      body = """{"id":"1","x":1}""",
      responseCode = 200,
      responseBody = Some("""{"id":"2","x":2}""")
    )
    val expected = Right(Some(Sample("2", 2)))
    val actual   = http.patch[Sample, Sample](s"$srvUri/200-b-ok", Map.empty, Sample("1", 1), NoAuth())
    assert(actual == expected)
  }

  test("patch with BasicCredential() return Some(Simple(1,1))") {
    mockPatchReq(
      path = "/bc/200-b-ok",
      headers = Map("Authorization" -> s"Basic ${getEncoder.encodeToString("usr:pwd".getBytes())}"),
      body = """{"id":"1","x":1}""",
      responseCode = 200,
      responseBody = Some("""{"id":"2","x":2}""")
    )
    val actual   = http.patch[Sample, Sample](
      s"$srvUri/bc/200-b-ok",
      Map.empty,
      Sample("1", 1),
      BasicCredential("usr", "pwd")
    )
    val expected = Right(Some(Sample("2", 2)))

    assert(actual == expected)
  }

  test("patch with BearerToken() return Some(Simple(1,1))") {
    mockPatchReq(
      path = "/bt/200-b-ok",
      headers = Map("Authorization" -> s"Bearer myToken"),
      body = """{"id":"1","x":1}""",
      responseCode = 200,
      responseBody = Some("""{"id":"2","x":2}""")
    )
    val auth     = BearerToken("myToken", "", "", "", "", 1L)
    val actual   = http.patch[Sample, Sample](s"$srvUri/bt/200-b-ok", Map.empty, Sample("1", 1), auth)
    val expected = Right(Some(Sample("2", 2)))

    assert(actual == expected)
  }

  test("patch with NoAuth() return None with null body") {
    mockPatchReq(
      path = "/200-b-null",
      headers = Map.empty,
      body = """{"id":"1","x":1}""",
      responseCode = 200,
      responseBody = None
    )
    val expected = Right(None)
    val actual   = http.patch[Sample, Sample](s"$srvUri/200-b-null", Map.empty, Sample("1", 1), NoAuth())
    assert(actual == expected)
  }

  test("patch with NoAuth() return None with empty body") {
    mockPatchReq(
      path = "/200-b-null",
      headers = Map.empty,
      body = """{"id":"1","x":1}""",
      responseCode = 200,
      responseBody = Some("")
    )
    val expected = Right(None)
    val actual   = http.patch[Sample, Sample](s"$srvUri/200-b-null", Map.empty, Sample("1", 1), NoAuth())
    assert(actual == expected)
  }

  test("patch with NoAuth() return Left(BodyError) with not parsable body") {
    mockPatchReq(
      path = "/200-b-ko",
      headers = Map.empty,
      body = """{"id":"1","x":1}""",
      responseCode = 200,
      responseBody = Some("""{"y":"1"}""")
    )
    val expected = Left(
      UnexpectedBodyErr(
        body = """{"y":"1"}""",
        error = DecodeErr(DecodingFailure("Attempt to decode value on failed cursor", List(DownField("id"))))
      )
    )
    val actual   = http.patch[Sample, Sample](s"$srvUri/200-b-ko", Map.empty, Sample("1", 1), NoAuth())

    assert(actual == expected)
  }

  test("patch with NoAuth() return Left(ClientError) with 4xx status code") {
    mockPatchReq(
      path = "/400",
      headers = Map.empty,
      body = """{"id":"1","x":1}""",
      responseCode = 400,
      responseBody = Some("It's a 400 error")
    )

    val expected = Left(ClientErr(400, "It's a 400 error"))
    assert(http.patch[Sample, Sample](s"$srvUri/400", Map.empty, Sample("1", 1), NoAuth()) == expected)
  }

  test("patch with NoAuth() return Left(ServerError) with 5xx status code") {
    mockPatchReq(
      path = "/500",
      headers = Map.empty,
      body = """{"id":"1","x":1}""",
      responseCode = 500,
      responseBody = Some("It's a 500 error")
    )

    val expected = Left(ServerErr(500, "It's a 500 error"))
    assert(http.patch[Sample, Sample](s"$srvUri/500", Map.empty, Sample("1", 1), NoAuth()) == expected)
  }

  test("patch with NoAuth() return Left(GenericError) with xxx status code") {
    mockPatchReq(
      path = "/300",
      headers = Map.empty,
      body = """{"id":"1","x":1}""",
      responseCode = 300,
      responseBody = Some("It's a 300 error")
    )

    val expected = Left(GenericErr(300, "It's a 300 error"))
    assert(http.patch[Sample, Sample](s"$srvUri/300", Map.empty, Sample("1", 1), NoAuth()) == expected)
  }

  test("putFileMultiPart return ConnectionError") {
    val actual = http.putFileMultiPart(
      endpoint = "https://wrong:8080/",
      Map.empty,
      name = "file",
      fileName = "my/file/path/file.txt",
      contentType = "multipart/form-data",
      data = "x".getBytes,
      NoAuth()
    )
    assertConnectionErr(actual, "Exception when sending request: PUT https://wrong:8080/")
  }

  test("putFileMultiPart with NoAuth() return Right()") {
    mockPutMultiReq(
      path = "/file/200",
      headers = Map("Content-Type" -> "multipart/form-data; boundary=.*"),
      substringBody = "Content-Disposition: form-data; name=\"file\"; filename=\"my/file/path/file.txt\"",
      responseCode = 200,
      responseBody = None
    )
    val actual = http.putFileMultiPart(
      endpoint = s"$srvUri/file/200",
      Map.empty,
      name = "file",
      fileName = "my/file/path/file.txt",
      contentType = "multipart/form-data",
      data = "x".getBytes,
      NoAuth()
    )
    assert(actual == Right())
  }

  test("putFileMultiPart with BasicCredentials() return Right()") {
    mockPutMultiReq(
      path = "/bc/file/200",
      headers = Map(
        "Authorization" -> s"Basic ${getEncoder.encodeToString("usr:pwd".getBytes())}",
        "Content-Type"  -> "multipart/form-data; boundary=.*"
      ),
      substringBody = "Content-Disposition: form-data; name=\"file\"; filename=\"my/file/path/file.txt\"",
      responseCode = 200,
      responseBody = None
    )
    val actual = http.putFileMultiPart(
      endpoint = s"$srvUri/bc/file/200",
      Map.empty,
      name = "file",
      fileName = "my/file/path/file.txt",
      contentType = "multipart/form-data",
      data = "x".getBytes,
      BasicCredential("usr", "pwd")
    )
    assert(actual == Right())
  }

  test("putFileMultiPart with BearerToken() return Right()") {
    mockPutMultiReq(
      path = "/bt/file/200",
      headers = Map(
        "Authorization" -> s"Bearer myToken",
        "Content-Type"  -> "multipart/form-data; boundary=.*"
      ),
      substringBody = "Content-Disposition: form-data; name=\"file\"; filename=\"my/file/path/file.txt\"",
      responseCode = 200,
      responseBody = None
    )
    val actual = http.putFileMultiPart(
      endpoint = s"$srvUri/bt/file/200",
      Map.empty,
      name = "file",
      fileName = "my/file/path/file.txt",
      contentType = "multipart/form-data",
      data = "x".getBytes,
      BearerToken("myToken", "", "", "", "", 1L)
    )
    assert(actual == Right())
  }

  test("putFileMultiPart with NoAuth() return ClientError on 4xx") {
    mockPutMultiReq(
      path = "/file/400",
      headers = Map("Content-Type" -> "multipart/form-data; boundary=.*"),
      substringBody = "Content-Disposition: form-data; name=\"file\"; filename=\"my/file/path/file.txt\"",
      responseCode = 400,
      responseBody = Some("It's 400 error")
    )
    val actual = http.putFileMultiPart(
      endpoint = s"$srvUri/file/400",
      Map.empty,
      name = "file",
      fileName = "my/file/path/file.txt",
      contentType = "multipart/form-data",
      data = "x".getBytes,
      NoAuth()
    )
    assert(actual == Left(ClientErr(400, "It's 400 error")))
  }

  test("putFileMultiPart with NoAuth() return ServerError on 5xx") {
    mockPutMultiReq(
      path = "/file/500",
      headers = Map("Content-Type" -> "multipart/form-data; boundary=.*"),
      substringBody = "Content-Disposition: form-data; name=\"file\"; filename=\"my/file/path/file.txt\"",
      responseCode = 500,
      responseBody = Some("It's 500 error")
    )
    val actual = http.putFileMultiPart(
      endpoint = s"$srvUri/file/500",
      Map.empty,
      name = "file",
      fileName = "my/file/path/file.txt",
      contentType = "multipart/form-data",
      data = "x".getBytes,
      NoAuth()
    )
    assert(actual == Left(ServerErr(500, "It's 500 error")))
  }

  test("putFileMultiPart with NoAuth() return GenericError on xxx") {
    mockPutMultiReq(
      path = "/file/300",
      headers = Map("Content-Type" -> "multipart/form-data; boundary=.*"),
      substringBody = "Content-Disposition: form-data; name=\"file\"; filename=\"my/file/path/file.txt\"",
      responseCode = 300,
      responseBody = Some("It's 300 error")
    )
    val actual = http.putFileMultiPart(
      endpoint = s"$srvUri/file/300",
      Map.empty,
      name = "file",
      fileName = "my/file/path/file.txt",
      contentType = "multipart/form-data",
      data = "x".getBytes,
      NoAuth()
    )

    assert(actual == Left(GenericErr(300, "It's 300 error")))
  }

  test("delete return ConnectionError") {
    val actual = http.delete[Unit]("https://wrong:8080/", Map.empty, NoAuth())
    assertConnectionErr(actual, "Exception when sending request: DELETE https://wrong:8080/")
  }

  test("delete with NoAuth() return Some(Simple(1,1))") {
    mockDeleteReq(
      path = "/200-b-ok",
      headers = Map.empty,
      responseCode = 200,
      responseBody = Some("""{"id":"2","x":2}""")
    )
    val expected = Right(Some(Sample("2", 2)))
    val actual   = http.delete[Sample](s"$srvUri/200-b-ok", Map.empty, NoAuth())
    assert(actual == expected)
  }

  test("delete with BasicCredential() return Some(Simple(1,1))") {
    mockDeleteReq(
      path = "/bc/200-b-ok",
      headers = Map("Authorization" -> s"Basic ${getEncoder.encodeToString("usr:pwd".getBytes())}"),
      responseCode = 200,
      responseBody = Some("""{"id":"2","x":2}""")
    )
    val actual   = http.delete[Sample](
      s"$srvUri/bc/200-b-ok",
      Map.empty,
      BasicCredential("usr", "pwd")
    )
    val expected = Right(Some(Sample("2", 2)))

    assert(actual == expected)
  }

  test("delete with BearerToken() return Some(Simple(1,1))") {
    mockDeleteReq(
      path = "/bt/200-b-ok",
      headers = Map("Authorization" -> s"Bearer myToken"),
      responseCode = 200,
      responseBody = Some("""{"id":"2","x":2}""")
    )
    val auth     = BearerToken("myToken", "", "", "", "", 1L)
    val actual   = http.delete[Sample](s"$srvUri/bt/200-b-ok", Map.empty, auth)
    val expected = Right(Some(Sample("2", 2)))

    assert(actual == expected)
  }

  test("delete with NoAuth() return None with null body") {
    mockDeleteReq(
      path = "/204-b-null",
      headers = Map.empty,
      responseCode = 204,
      responseBody = None
    )
    val expected = Right(None)
    val actual   = http.delete[Sample](s"$srvUri/204-b-null", Map.empty, NoAuth())
    assert(actual == expected)
  }

  test("delete with NoAuth() return None with empty body") {
    mockDeleteReq(
      path = "/200-b-null",
      headers = Map.empty,
      responseCode = 200,
      responseBody = Some("")
    )
    val expected = Right(None)
    val actual   = http.delete[Sample](s"$srvUri/200-b-null", Map.empty, NoAuth())
    assert(actual == expected)
  }

  test("delete with NoAuth() return Left(BodyError) with not parsable body") {
    mockDeleteReq(
      path = "/200-b-ko",
      headers = Map.empty,
      responseCode = 200,
      responseBody = Some("""{"y":"1"}""")
    )
    val expected = Left(
      UnexpectedBodyErr(
        body = """{"y":"1"}""",
        error = DecodeErr(DecodingFailure("Attempt to decode value on failed cursor", List(DownField("id"))))
      )
    )
    val actual   = http.delete[Sample](s"$srvUri/200-b-ko", Map.empty, NoAuth())

    assert(actual == expected)
  }

  test("delete with NoAuth() return Left(ClientError) with 4xx status code") {
    mockDeleteReq(
      path = "/400",
      headers = Map.empty,
      responseCode = 400,
      responseBody = Some("It's a 400 error")
    )

    val expected = Left(ClientErr(400, "It's a 400 error"))
    assert(http.delete[Sample](s"$srvUri/400", Map.empty, NoAuth()) == expected)
  }

  test("delete with NoAuth() return Left(ServerError) with 5xx status code") {
    mockDeleteReq(
      path = "/500",
      headers = Map.empty,
      responseCode = 500,
      responseBody = Some("It's a 500 error")
    )

    val expected = Left(ServerErr(500, "It's a 500 error"))
    assert(http.delete[Sample](s"$srvUri/500", Map.empty, NoAuth()) == expected)
  }

  test("delete with NoAuth() return Left(GenericError) with xxx status code") {
    mockDeleteReq(
      path = "/300",
      headers = Map.empty,
      responseCode = 300,
      responseBody = Some("It's a 300 error")
    )

    val expected = Left(GenericErr(300, "It's a 300 error"))
    assert(http.delete[Sample](s"$srvUri/300", Map.empty, NoAuth()) == expected)
  }

  private def assertConnectionErr[A](actual: Either[HttpErrors, A], error: String): Assertion = {
    assert(actual.isLeft)
    assert(actual.left.value.isInstanceOf[ConnectionErr])
    assert(actual.left.value.asInstanceOf[ConnectionErr].err == error)
  }

  private def assertUnexpectedBodyWithParsingFailureErr[A](actual: Either[HttpErrors, A], body: String) = {
    assert(actual.isLeft)
    assert(actual.left.value.isInstanceOf[UnexpectedBodyErr])
    assert(actual.left.value.asInstanceOf[UnexpectedBodyErr].body == body)
  }
}
