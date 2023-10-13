package it.agilelab.provisioning.commons.http

import io.circe.CursorOp.DownField
import io.circe.{ Decoder, DecodingFailure, Encoder }
import org.scalamock.scalatest.MockFactory
import org.scalatest.funsuite.AnyFunSuite
import io.circe.generic.auto._
import it.agilelab.provisioning.commons.audit.Audit
import it.agilelab.provisioning.commons.http.Auth.{ BasicCredential, BearerToken, NoAuth }
import it.agilelab.provisioning.commons.http.HttpErrors._
import it.agilelab.provisioning.commons.support.ParserError.DecodeErr

class DefaultHttpWithAuditTest extends AnyFunSuite with MockFactory {

  val audit: Audit   = mock[Audit]
  val baseHttp: Http = stub[Http]
  val http           = new DefaultHttpWithAudit(baseHttp, audit)

  case class Sample(id: String, x: Int)

  test("get logs info on Right(Simple(\"1\", 1)) with NoAuth") {
    (baseHttp
      .get(_: String, _: Map[String, String], _: Auth)(_: Decoder[_]))
      .when("http://my-endpoint/", *, NoAuth(), *)
      .returns(Right(Sample("1", 1)))

    inSequence(
      (audit.info _)
        .expects("Executing http GET request to http://my-endpoint/;auth=NoAuth()")
        .once(),
      (audit.info _)
        .expects("Http GET request to http://my-endpoint/ completed successful")
        .once()
    )

    val actual = http.get[Sample]("http://my-endpoint/", Map.empty, NoAuth())
    assert(actual == Right(Sample("1", 1)))
  }

  test("get logs info on Right(Simple(\"1\", 1)) with BasicCredentials") {
    (baseHttp
      .get(_: String, _: Map[String, String], _: Auth)(_: Decoder[_]))
      .when("http://my-endpoint/", *, BasicCredential("usr", "pwd"), *)
      .returns(Right(Sample("1", 1)))

    inSequence(
      (audit.info _)
        .expects("Executing http GET request to http://my-endpoint/;auth=BasicCredential(*,*)")
        .once(),
      (audit.info _).expects("Http GET request to http://my-endpoint/ completed successful").once()
    )

    val actual = http.get[Sample]("http://my-endpoint/", Map.empty, BasicCredential("usr", "pwd"))
    assert(actual == Right(Sample("1", 1)))
  }

  test("get logs info on Right(Simple(\"1\", 1)) with BearerToken") {
    (baseHttp
      .get(_: String, _: Map[String, String], _: Auth)(_: Decoder[_]))
      .when("http://my-endpoint/", *, BearerToken("myToken", "", "", "", "", 1L, ""), *)
      .returns(Right(Sample("1", 1)))

    inSequence(
      (audit.info _)
        .expects("Executing http GET request to http://my-endpoint/;auth=BearerToken(*,*,*,*,*,*,*)")
        .once(),
      (audit.info _).expects("Http GET request to http://my-endpoint/ completed successful").once()
    )

    val actual = http
      .get[Sample]("http://my-endpoint/", Map.empty, BearerToken("myToken", "", "", "", "", 1L, ""))
    assert(actual == Right(Sample("1", 1)))
  }

  test("get logs error on Left(UnexpectedBodyErr) with NoAuth") {
    (baseHttp
      .get(_: String, _: Map[String, String], _: Auth)(_: Decoder[_]))
      .when("http://my-endpoint/", *, NoAuth(), *)
      .returns(
        Left(
          UnexpectedBodyErr(
            "my body",
            DecodeErr(DecodingFailure("Attempt to decode value on failed cursor", List(DownField("id"))))
          )
        )
      )

    inSequence(
      (audit.info _)
        .expects("Executing http GET request to http://my-endpoint/;auth=NoAuth()")
        .once(),
      (audit.error _)
        .expects(where { s: String =>
          s.startsWith(
            "Http GET request to http://my-endpoint/ failed. Details: UnexpectedBodyErr(my body,DecodeErr(DecodingFailure at .id: Attempt to decode value on failed cursor))"
          )
        })
        .once()
    )

    val actual = http.get[Sample]("http://my-endpoint/", Map.empty, NoAuth())
    assert(
      actual == Left(
        UnexpectedBodyErr(
          "my body",
          DecodeErr(DecodingFailure("Attempt to decode value on failed cursor", List(DownField("id"))))
        )
      )
    )
  }

  test("get logs error on Left(ServerErr()) with NoAuth") {
    (baseHttp
      .get(_: String, _: Map[String, String], _: Auth)(_: Decoder[_]))
      .when("http://my-endpoint/", *, NoAuth(), *)
      .returns(Left(ServerErr(1, "x")))

    inSequence(
      (audit.info _)
        .expects("Executing http GET request to http://my-endpoint/;auth=NoAuth()")
        .once(),
      (audit.error _)
        .expects("Http GET request to http://my-endpoint/ failed. Details: ServerErr(1,x)")
        .once()
    )

    val actual =
      http.get[Sample]("http://my-endpoint/", Map.empty, NoAuth())
    assert(actual == Left(ServerErr(1, "x")))
  }

  test("get logs error on Left(ClientErr()) with NoAuth") {
    (baseHttp
      .get(_: String, _: Map[String, String], _: Auth)(_: Decoder[_]))
      .when("http://my-endpoint/", *, NoAuth(), *)
      .returns(Left(ClientErr(1, "x")))

    inSequence(
      (audit.info _)
        .expects("Executing http GET request to http://my-endpoint/;auth=NoAuth()")
        .once(),
      (audit.error _)
        .expects("Http GET request to http://my-endpoint/ failed. Details: ClientErr(1,x)")
        .once()
    )

    val actual =
      http.get[Sample]("http://my-endpoint/", Map.empty, NoAuth())
    assert(actual == Left(ClientErr(1, "x")))
  }

  test("get logs error on Left(GenericErr()) with NoAuth") {
    (baseHttp
      .get(_: String, _: Map[String, String], _: Auth)(_: Decoder[_]))
      .when("http://my-endpoint/", *, NoAuth(), *)
      .returns(Left(GenericErr(1, "x")))

    inSequence(
      (audit.info _)
        .expects("Executing http GET request to http://my-endpoint/;auth=NoAuth()")
        .once(),
      (audit.error _)
        .expects("Http GET request to http://my-endpoint/ failed. Details: GenericErr(1,x)")
        .once()
    )

    val actual =
      http.get[Sample]("http://my-endpoint/", Map.empty, NoAuth())
    assert(actual == Left(GenericErr(1, "x")))
  }

  test("post logs info on Right(Simple(\"2\", 2)) with NoAuth") {
    (baseHttp
      .post[Sample, Sample](_: String, _: Map[String, String], _: Sample, _: Auth)(
        _: Encoder[Sample],
        _: Decoder[Sample]
      ))
      .when("http://my-endpoint/", *, Sample("1", 1), NoAuth(), *, *)
      .returns(Right(Option(Sample("2", 2))))

    inSequence(
      (audit.info _)
        .expects("Executing http POST request to http://my-endpoint/;auth=NoAuth();body=Sample(1,1)")
        .once(),
      (audit.info _).expects("Http POST request to http://my-endpoint/ completed successful").once()
    )

    val actual = http.post[Sample, Sample]("http://my-endpoint/", Map.empty, Sample("1", 1), NoAuth())
    assert(actual == Right(Option(Sample("2", 2))))
  }

  test("post logs info on Right(Simple(\"2\", 2)) with BasicCredential") {
    (baseHttp
      .post[Sample, Sample](_: String, _: Map[String, String], _: Sample, _: Auth)(
        _: Encoder[Sample],
        _: Decoder[Sample]
      ))
      .when("http://my-endpoint/", *, Sample("1", 1), BasicCredential("usr", "pwd"), *, *)
      .returns(Right(Option(Sample("2", 2))))

    inSequence(
      (audit.info _)
        .expects("Executing http POST request to http://my-endpoint/;auth=BasicCredential(*,*);body=Sample(1,1)")
        .once(),
      (audit.info _).expects("Http POST request to http://my-endpoint/ completed successful").once()
    )

    val actual = http.post[Sample, Sample](
      "http://my-endpoint/",
      Map.empty,
      Sample("1", 1),
      BasicCredential("usr", "pwd")
    )
    assert(actual == Right(Option(Sample("2", 2))))
  }

  test("post logs info on Right(Simple(\"2\", 2)) with BearerToken") {
    (baseHttp
      .post[Sample, Sample](_: String, _: Map[String, String], _: Sample, _: Auth)(
        _: Encoder[Sample],
        _: Decoder[Sample]
      ))
      .when("http://my-endpoint/", *, Sample("1", 1), BearerToken("myToken", "", "", "", "", 1L, ""), *, *)
      .returns(Right(Option(Sample("2", 2))))

    inSequence(
      (audit.info _)
        .expects("Executing http POST request to http://my-endpoint/;auth=BearerToken(*,*,*,*,*,*,*);body=Sample(1,1)")
        .once(),
      (audit.info _).expects("Http POST request to http://my-endpoint/ completed successful").once()
    )

    val actual = http.post[Sample, Sample](
      "http://my-endpoint/",
      Map.empty,
      Sample("1", 1),
      BearerToken("myToken", "", "", "", "", 1L, "")
    )
    assert(actual == Right(Option(Sample("2", 2))))
  }

  test("post logs error on Left(UnexpectedBodyErr) with NoAuth") {
    (baseHttp
      .post[Sample, Sample](_: String, _: Map[String, String], _: Sample, _: Auth)(
        _: Encoder[Sample],
        _: Decoder[Sample]
      ))
      .when("http://my-endpoint/", *, Sample("1", 1), NoAuth(), *, *)
      .returns(
        Left(
          UnexpectedBodyErr(
            "my body",
            DecodeErr(DecodingFailure("Attempt to decode value on failed cursor", List(DownField("id"))))
          )
        )
      )

    inSequence(
      (audit.info _)
        .expects("Executing http POST request to http://my-endpoint/;auth=NoAuth();body=Sample(1,1)")
        .once(),
      (audit.error _)
        .expects(where { s: String =>
          s.startsWith(
            "Http POST request to http://my-endpoint/ failed. Details: UnexpectedBodyErr(my body,DecodeErr(DecodingFailure at .id: Attempt to decode value on failed cursor))"
          )
        })
        .once()
    )

    val actual =
      http.post[Sample, Sample]("http://my-endpoint/", Map.empty, Sample("1", 1), NoAuth())
    assert(
      actual == Left(
        UnexpectedBodyErr(
          "my body",
          DecodeErr(DecodingFailure("Attempt to decode value on failed cursor", List(DownField("id"))))
        )
      )
    )
  }

  test("post logs error on Left(ServerErr) with NoAuth") {
    (baseHttp
      .post[Sample, Sample](_: String, _: Map[String, String], _: Sample, _: Auth)(
        _: Encoder[Sample],
        _: Decoder[Sample]
      ))
      .when("http://my-endpoint/", *, Sample("1", 1), NoAuth(), *, *)
      .returns(Left(ServerErr(1, "x")))

    inSequence(
      (audit.info _)
        .expects("Executing http POST request to http://my-endpoint/;auth=NoAuth();body=Sample(1,1)")
        .once(),
      (audit.error _)
        .expects("Http POST request to http://my-endpoint/ failed. Details: ServerErr(1,x)")
        .once()
    )

    val actual =
      http.post[Sample, Sample]("http://my-endpoint/", Map.empty, Sample("1", 1), NoAuth())
    assert(actual == Left(ServerErr(1, "x")))
  }

  test("post logs error on Left(ClientErr) with NoAuth") {
    (baseHttp
      .post[Sample, Sample](_: String, _: Map[String, String], _: Sample, _: Auth)(
        _: Encoder[Sample],
        _: Decoder[Sample]
      ))
      .when("http://my-endpoint/", *, Sample("1", 1), NoAuth(), *, *)
      .returns(Left(ClientErr(1, "x")))

    inSequence(
      (audit.info _)
        .expects("Executing http POST request to http://my-endpoint/;auth=NoAuth();body=Sample(1,1)")
        .once(),
      (audit.error _)
        .expects("Http POST request to http://my-endpoint/ failed. Details: ClientErr(1,x)")
        .once()
    )

    val actual =
      http.post[Sample, Sample]("http://my-endpoint/", Map.empty, Sample("1", 1), NoAuth())
    assert(actual == Left(ClientErr(1, "x")))
  }

  test("post logs error on Left(GenericErr) with NoAuth") {
    (baseHttp
      .post[Sample, Sample](_: String, _: Map[String, String], _: Sample, _: Auth)(
        _: Encoder[Sample],
        _: Decoder[Sample]
      ))
      .when("http://my-endpoint/", *, Sample("1", 1), NoAuth(), *, *)
      .returns(Left(GenericErr(1, "x")))

    inSequence(
      (audit.info _)
        .expects("Executing http POST request to http://my-endpoint/;auth=NoAuth();body=Sample(1,1)")
        .once(),
      (audit.error _)
        .expects("Http POST request to http://my-endpoint/ failed. Details: GenericErr(1,x)")
        .once()
    )

    val actual =
      http.post[Sample, Sample]("http://my-endpoint/", Map.empty, Sample("1", 1), NoAuth())
    assert(actual == Left(GenericErr(1, "x")))
  }

  test("put logs info on Right(Simple(\"2\", 2)) with NoAuth") {
    (baseHttp
      .put[Sample, Sample](_: String, _: Map[String, String], _: Sample, _: Auth)(
        _: Encoder[Sample],
        _: Decoder[Sample]
      ))
      .when("http://my-endpoint/", *, Sample("1", 1), NoAuth(), *, *)
      .returns(Right(Option(Sample("2", 2))))

    inSequence(
      (audit.info _)
        .expects("Executing http PUT request to http://my-endpoint/;auth=NoAuth();body=Sample(1,1)")
        .once(),
      (audit.info _).expects("Http PUT request to http://my-endpoint/ completed successful").once()
    )

    val actual =
      http.put[Sample, Sample]("http://my-endpoint/", Map.empty, Sample("1", 1), NoAuth())
    assert(actual == Right(Option(Sample("2", 2))))
  }

  test("put logs info on Right(Simple(\"2\", 2)) with BasicCredential") {
    (baseHttp
      .put[Sample, Sample](_: String, _: Map[String, String], _: Sample, _: Auth)(
        _: Encoder[Sample],
        _: Decoder[Sample]
      ))
      .when("http://my-endpoint/", *, Sample("1", 1), BasicCredential("usr", "pwd"), *, *)
      .returns(Right(Option(Sample("2", 2))))

    inSequence(
      (audit.info _)
        .expects("Executing http PUT request to http://my-endpoint/;auth=BasicCredential(*,*);body=Sample(1,1)")
        .once(),
      (audit.info _).expects("Http PUT request to http://my-endpoint/ completed successful").once()
    )

    val actual = http.put[Sample, Sample](
      "http://my-endpoint/",
      Map.empty,
      Sample("1", 1),
      BasicCredential("usr", "pwd")
    )
    assert(actual == Right(Option(Sample("2", 2))))
  }

  test("put logs info on Right(Simple(\"2\", 2)) with BearerToken") {
    (baseHttp
      .put[Sample, Sample](_: String, _: Map[String, String], _: Sample, _: Auth)(
        _: Encoder[Sample],
        _: Decoder[Sample]
      ))
      .when("http://my-endpoint/", *, Sample("1", 1), BearerToken("myToken", "", "", "", "", 1L, ""), *, *)
      .returns(Right(Option(Sample("2", 2))))

    inSequence(
      (audit.info _)
        .expects("Executing http PUT request to http://my-endpoint/;auth=BearerToken(*,*,*,*,*,*,*);body=Sample(1,1)")
        .once(),
      (audit.info _).expects("Http PUT request to http://my-endpoint/ completed successful").once()
    )

    val actual = http.put[Sample, Sample](
      "http://my-endpoint/",
      Map.empty,
      Sample("1", 1),
      BearerToken("myToken", "", "", "", "", 1L, "")
    )
    assert(actual == Right(Option(Sample("2", 2))))
  }

  test("put logs error on Left(UnexpectedBodyErr) with NoAuth") {
    (baseHttp
      .put[Sample, Sample](_: String, _: Map[String, String], _: Sample, _: Auth)(
        _: Encoder[Sample],
        _: Decoder[Sample]
      ))
      .when("http://my-endpoint/", *, Sample("1", 1), NoAuth(), *, *)
      .returns(
        Left(
          UnexpectedBodyErr(
            "my body",
            DecodeErr(DecodingFailure("Attempt to decode value on failed cursor", List(DownField("id"))))
          )
        )
      )

    inSequence(
      (audit.info _)
        .expects("Executing http PUT request to http://my-endpoint/;auth=NoAuth();body=Sample(1,1)")
        .once(),
      (audit.error _)
        .expects(where { s: String =>
          s.startsWith(
            "Http PUT request to http://my-endpoint/ failed. Details: UnexpectedBodyErr(my body,DecodeErr(DecodingFailure at .id: Attempt to decode value on failed cursor))"
          )
        })
        .once()
    )

    val actual =
      http.put[Sample, Sample]("http://my-endpoint/", Map.empty, Sample("1", 1), NoAuth())
    assert(
      actual == Left(
        UnexpectedBodyErr(
          "my body",
          DecodeErr(DecodingFailure("Attempt to decode value on failed cursor", List(DownField("id"))))
        )
      )
    )
  }

  test("put logs error on Left(ServerErr) with NoAuth") {
    (baseHttp
      .put[Sample, Sample](_: String, _: Map[String, String], _: Sample, _: Auth)(
        _: Encoder[Sample],
        _: Decoder[Sample]
      ))
      .when("http://my-endpoint/", *, Sample("1", 1), NoAuth(), *, *)
      .returns(Left(ServerErr(1, "x")))

    inSequence(
      (audit.info _)
        .expects("Executing http PUT request to http://my-endpoint/;auth=NoAuth();body=Sample(1,1)")
        .once(),
      (audit.error _)
        .expects("Http PUT request to http://my-endpoint/ failed. Details: ServerErr(1,x)")
        .once()
    )

    val actual =
      http.put[Sample, Sample]("http://my-endpoint/", Map.empty, Sample("1", 1), NoAuth())
    assert(actual == Left(ServerErr(1, "x")))
  }

  test("put logs error on Left(ClientErr) with NoAuth") {
    (baseHttp
      .put[Sample, Sample](_: String, _: Map[String, String], _: Sample, _: Auth)(
        _: Encoder[Sample],
        _: Decoder[Sample]
      ))
      .when("http://my-endpoint/", *, Sample("1", 1), NoAuth(), *, *)
      .returns(Left(ClientErr(1, "x")))

    inSequence(
      (audit.info _)
        .expects("Executing http PUT request to http://my-endpoint/;auth=NoAuth();body=Sample(1,1)")
        .once(),
      (audit.error _)
        .expects("Http PUT request to http://my-endpoint/ failed. Details: ClientErr(1,x)")
        .once()
    )

    val actual =
      http.put[Sample, Sample]("http://my-endpoint/", Map.empty, Sample("1", 1), NoAuth())
    assert(actual == Left(ClientErr(1, "x")))
  }

  test("put logs error on Left(GenericErr) with NoAuth") {
    (baseHttp
      .put[Sample, Sample](_: String, _: Map[String, String], _: Sample, _: Auth)(
        _: Encoder[Sample],
        _: Decoder[Sample]
      ))
      .when("http://my-endpoint/", *, Sample("1", 1), NoAuth(), *, *)
      .returns(Left(GenericErr(1, "x")))

    inSequence(
      (audit.info _)
        .expects("Executing http PUT request to http://my-endpoint/;auth=NoAuth();body=Sample(1,1)")
        .once(),
      (audit.error _)
        .expects("Http PUT request to http://my-endpoint/ failed. Details: GenericErr(1,x)")
        .once()
    )

    val actual =
      http.put[Sample, Sample]("http://my-endpoint/", Map.empty, Sample("1", 1), NoAuth())
    assert(actual == Left(GenericErr(1, "x")))
  }

  test("patch logs info on Right(Simple(\"2\", 2)) with NoAuth") {
    (baseHttp
      .patch[Sample, Sample](_: String, _: Map[String, String], _: Sample, _: Auth)(
        _: Encoder[Sample],
        _: Decoder[Sample]
      ))
      .when("http://my-endpoint/", *, Sample("1", 1), NoAuth(), *, *)
      .returns(Right(Option(Sample("2", 2))))

    inSequence(
      (audit.info _)
        .expects("Executing http PATCH request to http://my-endpoint/;auth=NoAuth();body=Sample(1,1)")
        .once(),
      (audit.info _)
        .expects("Http PATCH request to http://my-endpoint/ completed successful")
        .once()
    )

    val actual =
      http.patch[Sample, Sample]("http://my-endpoint/", Map.empty, Sample("1", 1), NoAuth())
    assert(actual == Right(Option(Sample("2", 2))))
  }

  test("patch logs info on Right(Simple(\"2\", 2)) with BasicCredential") {
    (baseHttp
      .patch[Sample, Sample](_: String, _: Map[String, String], _: Sample, _: Auth)(
        _: Encoder[Sample],
        _: Decoder[Sample]
      ))
      .when("http://my-endpoint/", *, Sample("1", 1), BasicCredential("usr", "pwd"), *, *)
      .returns(Right(Option(Sample("2", 2))))

    inSequence(
      (audit.info _)
        .expects(
          "Executing http PATCH request to http://my-endpoint/;auth=BasicCredential(*,*);body=Sample(1,1)"
        )
        .once(),
      (audit.info _)
        .expects("Http PATCH request to http://my-endpoint/ completed successful")
        .once()
    )

    val actual = http.patch[Sample, Sample](
      "http://my-endpoint/",
      Map.empty,
      Sample("1", 1),
      BasicCredential("usr", "pwd")
    )
    assert(actual == Right(Option(Sample("2", 2))))
  }

  test("patch logs info on Right(Simple(\"2\", 2)) with BearerToken") {
    (baseHttp
      .patch[Sample, Sample](_: String, _: Map[String, String], _: Sample, _: Auth)(
        _: Encoder[Sample],
        _: Decoder[Sample]
      ))
      .when("http://my-endpoint/", *, Sample("1", 1), BearerToken("myToken", "", "", "", "", 1L, ""), *, *)
      .returns(Right(Option(Sample("2", 2))))

    inSequence(
      (audit.info _)
        .expects("Executing http PATCH request to http://my-endpoint/;auth=BearerToken(*,*,*,*,*,*,*);body=Sample(1,1)")
        .once(),
      (audit.info _)
        .expects("Http PATCH request to http://my-endpoint/ completed successful")
        .once()
    )

    val actual = http.patch[Sample, Sample](
      "http://my-endpoint/",
      Map.empty,
      Sample("1", 1),
      BearerToken("myToken", "", "", "", "", 1L, "")
    )
    assert(actual == Right(Option(Sample("2", 2))))
  }

  test("patch logs error on Left(UnexpectedBodyErr) with NoAuth") {
    (baseHttp
      .patch[Sample, Sample](_: String, _: Map[String, String], _: Sample, _: Auth)(
        _: Encoder[Sample],
        _: Decoder[Sample]
      ))
      .when("http://my-endpoint/", *, Sample("1", 1), NoAuth(), *, *)
      .returns(
        Left(
          UnexpectedBodyErr(
            "my body",
            DecodeErr(DecodingFailure("Attempt to decode value on failed cursor", List(DownField("id"))))
          )
        )
      )

    inSequence(
      (audit.info _)
        .expects("Executing http PATCH request to http://my-endpoint/;auth=NoAuth();body=Sample(1,1)")
        .once(),
      (audit.error _)
        .expects(where { s: String =>
          s.startsWith(
            "Http PATCH request to http://my-endpoint/ failed. Details: UnexpectedBodyErr(my body,DecodeErr(DecodingFailure at .id: Attempt to decode value on failed cursor))"
          )
        })
        .once()
    )

    val actual =
      http.patch[Sample, Sample]("http://my-endpoint/", Map.empty, Sample("1", 1), NoAuth())
    assert(
      actual == Left(
        UnexpectedBodyErr(
          "my body",
          DecodeErr(DecodingFailure("Attempt to decode value on failed cursor", List(DownField("id"))))
        )
      )
    )
  }

  test("patch logs error on Left(ServerErr) with NoAuth") {
    (baseHttp
      .patch[Sample, Sample](_: String, _: Map[String, String], _: Sample, _: Auth)(
        _: Encoder[Sample],
        _: Decoder[Sample]
      ))
      .when("http://my-endpoint/", *, Sample("1", 1), NoAuth(), *, *)
      .returns(Left(ServerErr(1, "x")))

    inSequence(
      (audit.info _)
        .expects("Executing http PATCH request to http://my-endpoint/;auth=NoAuth();body=Sample(1,1)")
        .once(),
      (audit.error _)
        .expects("Http PATCH request to http://my-endpoint/ failed. Details: ServerErr(1,x)")
        .once()
    )

    val actual =
      http.patch[Sample, Sample]("http://my-endpoint/", Map.empty, Sample("1", 1), NoAuth())
    assert(actual == Left(ServerErr(1, "x")))
  }

  test("patch logs error on Left(ClientErr) with NoAuth") {
    (baseHttp
      .patch[Sample, Sample](_: String, _: Map[String, String], _: Sample, _: Auth)(
        _: Encoder[Sample],
        _: Decoder[Sample]
      ))
      .when("http://my-endpoint/", *, Sample("1", 1), NoAuth(), *, *)
      .returns(Left(ClientErr(1, "x")))

    inSequence(
      (audit.info _)
        .expects("Executing http PATCH request to http://my-endpoint/;auth=NoAuth();body=Sample(1,1)")
        .once(),
      (audit.error _)
        .expects("Http PATCH request to http://my-endpoint/ failed. Details: ClientErr(1,x)")
        .once()
    )

    val actual =
      http.patch[Sample, Sample]("http://my-endpoint/", Map.empty, Sample("1", 1), NoAuth())
    assert(actual == Left(ClientErr(1, "x")))
  }

  test("patch logs error on Left(GenericErr) with NoAuth") {
    (baseHttp
      .patch[Sample, Sample](_: String, _: Map[String, String], _: Sample, _: Auth)(
        _: Encoder[Sample],
        _: Decoder[Sample]
      ))
      .when("http://my-endpoint/", *, Sample("1", 1), NoAuth(), *, *)
      .returns(Left(GenericErr(1, "x")))

    inSequence(
      (audit.info _)
        .expects("Executing http PATCH request to http://my-endpoint/;auth=NoAuth();body=Sample(1,1)")
        .once(),
      (audit.error _)
        .expects("Http PATCH request to http://my-endpoint/ failed. Details: GenericErr(1,x)")
        .once()
    )

    val actual =
      http.patch[Sample, Sample]("http://my-endpoint/", Map.empty, Sample("1", 1), NoAuth())
    assert(actual == Left(GenericErr(1, "x")))
  }

  test("putFileMultipart logs info on Right() with NoAuth") {
    (baseHttp.putFileMultiPart _)
      .when("http://my-endpoint/", Map.empty[String, String], "nm", "fnm", "cnt", *, NoAuth())
      .returns(Right())

    inSequence(
      (audit.info _)
        .expects(
          "Executing http PUT FILE MULTIPART request to http://my-endpoint/;auth=NoAuth();name=nm;fileName=fnm;contentType=cnt"
        )
        .once(),
      (audit.info _)
        .expects("Http PUT FILE MULTIPART request to http://my-endpoint/ completed successful")
        .once()
    )

    val actual = http.putFileMultiPart(
      "http://my-endpoint/",
      Map.empty,
      "nm",
      "fnm",
      "cnt",
      "x".getBytes,
      NoAuth()
    )
    assert(actual == Right())
  }

  test("putFileMultipart logs info on Right() with BasicCredential") {
    (baseHttp.putFileMultiPart _)
      .when("http://my-endpoint/", Map.empty[String, String], "nm", "fnm", "cnt", *, BasicCredential("usr", "pwd"))
      .returns(Right())

    inSequence(
      (audit.info _)
        .expects(
          "Executing http PUT FILE MULTIPART request to http://my-endpoint/;auth=BasicCredential(*,*);name=nm;fileName=fnm;contentType=cnt"
        )
        .once(),
      (audit.info _)
        .expects("Http PUT FILE MULTIPART request to http://my-endpoint/ completed successful")
        .once()
    )

    val actual = http.putFileMultiPart(
      "http://my-endpoint/",
      Map.empty,
      "nm",
      "fnm",
      "cnt",
      "x".getBytes,
      BasicCredential("usr", "pwd")
    )
    assert(actual == Right())
  }

  test("putFileMultipart logs info on Right() with BearerToken") {
    (baseHttp.putFileMultiPart _)
      .when("http://my-endpoint/", *, "nm", "fnm", "cnt", *, BearerToken("myToken", "", "", "", "", 1L, ""))
      .returns(Right())

    inSequence(
      (audit.info _)
        .expects(
          "Executing http PUT FILE MULTIPART request to http://my-endpoint/;auth=BearerToken(*,*,*,*,*,*,*);name=nm;fileName=fnm;contentType=cnt"
        )
        .once(),
      (audit.info _)
        .expects("Http PUT FILE MULTIPART request to http://my-endpoint/ completed successful")
        .once()
    )

    val actual = http.putFileMultiPart(
      "http://my-endpoint/",
      Map.empty,
      "nm",
      "fnm",
      "cnt",
      "x".getBytes,
      BearerToken("myToken", "", "", "", "", 1L, "")
    )
    assert(actual == Right())
  }

  test("putFileMultipart logs error on Left(ServerErr) with NoAuth") {
    (baseHttp.putFileMultiPart _)
      .when("http://my-endpoint/", Map.empty[String, String], "nm", "fnm", "cnt", *, NoAuth())
      .returns(Left(ServerErr(1, "x")))

    inSequence(
      (audit.info _)
        .expects(
          "Executing http PUT FILE MULTIPART request to http://my-endpoint/;auth=NoAuth();name=nm;fileName=fnm;contentType=cnt"
        )
        .once(),
      (audit.error _)
        .expects("Http PUT FILE MULTIPART request to http://my-endpoint/ failed. Details: ServerErr(1,x)")
        .once()
    )

    val actual = http.putFileMultiPart(
      "http://my-endpoint/",
      Map.empty,
      "nm",
      "fnm",
      "cnt",
      "x".getBytes,
      NoAuth()
    )

    assert(actual == Left(ServerErr(1, "x")))
  }

  test("putFileMultipart logs error on Left(ClientErr) with NoAuth") {
    (baseHttp.putFileMultiPart _)
      .when("http://my-endpoint/", Map.empty[String, String], "nm", "fnm", "cnt", *, NoAuth())
      .returns(Left(ClientErr(1, "x")))

    inSequence(
      (audit.info _)
        .expects(
          "Executing http PUT FILE MULTIPART request to http://my-endpoint/;auth=NoAuth();name=nm;fileName=fnm;contentType=cnt"
        )
        .once(),
      (audit.error _)
        .expects("Http PUT FILE MULTIPART request to http://my-endpoint/ failed. Details: ClientErr(1,x)")
        .once()
    )

    val actual = http.putFileMultiPart(
      "http://my-endpoint/",
      Map.empty,
      "nm",
      "fnm",
      "cnt",
      "x".getBytes,
      NoAuth()
    )

    assert(actual == Left(ClientErr(1, "x")))
  }

  test("putFileMultipart logs error on Left(GenericErr) with NoAuth") {
    (baseHttp.putFileMultiPart _)
      .when("http://my-endpoint/", Map.empty[String, String], "nm", "fnm", "cnt", *, NoAuth())
      .returns(Left(GenericErr(1, "x")))

    inSequence(
      (audit.info _)
        .expects(
          "Executing http PUT FILE MULTIPART request to http://my-endpoint/;auth=NoAuth();name=nm;fileName=fnm;contentType=cnt"
        )
        .once(),
      (audit.error _)
        .expects("Http PUT FILE MULTIPART request to http://my-endpoint/ failed. Details: GenericErr(1,x)")
        .once()
    )

    val actual = http.putFileMultiPart(
      "http://my-endpoint/",
      Map.empty,
      "nm",
      "fnm",
      "cnt",
      "x".getBytes,
      NoAuth()
    )

    assert(actual == Left(GenericErr(1, "x")))
  }

  test("delete logs info on Right(Simple(\"2\", 2)) with NoAuth") {
    (baseHttp
      .delete[Sample](_: String, _: Map[String, String], _: Auth)(
        _: Decoder[Sample]
      ))
      .when("http://my-endpoint/", *, NoAuth(), *)
      .returns(Right(Option(Sample("2", 2))))

    inSequence(
      (audit.info _)
        .expects("Executing http DELETE request to http://my-endpoint/;auth=NoAuth()")
        .once(),
      (audit.info _)
        .expects("Http DELETE request to http://my-endpoint/ completed successful")
        .once()
    )

    val actual =
      http.delete[Sample]("http://my-endpoint/", Map.empty, NoAuth())
    assert(actual == Right(Option(Sample("2", 2))))
  }

  test("delete logs info on Right(Simple(\"2\", 2)) with BasicCredential") {
    (baseHttp
      .delete[Sample](_: String, _: Map[String, String], _: Auth)(
        _: Decoder[Sample]
      ))
      .when("http://my-endpoint/", *, BasicCredential("usr", "pwd"), *)
      .returns(Right(Option(Sample("2", 2))))

    inSequence(
      (audit.info _)
        .expects(
          "Executing http DELETE request to http://my-endpoint/;auth=BasicCredential(*,*)"
        )
        .once(),
      (audit.info _)
        .expects("Http DELETE request to http://my-endpoint/ completed successful")
        .once()
    )

    val actual = http.delete[Sample](
      "http://my-endpoint/",
      Map.empty,
      BasicCredential("usr", "pwd")
    )
    assert(actual == Right(Option(Sample("2", 2))))
  }

  test("delete logs info on Right(Simple(\"2\", 2)) with BearerToken") {
    (baseHttp
      .delete[Sample](_: String, _: Map[String, String], _: Auth)(
        _: Decoder[Sample]
      ))
      .when("http://my-endpoint/", *, BearerToken("myToken", "", "", "", "", 1L, ""), *)
      .returns(Right(Option(Sample("2", 2))))

    inSequence(
      (audit.info _)
        .expects("Executing http DELETE request to http://my-endpoint/;auth=BearerToken(*,*,*,*,*,*,*)")
        .once(),
      (audit.info _)
        .expects("Http DELETE request to http://my-endpoint/ completed successful")
        .once()
    )

    val actual = http.delete[Sample](
      "http://my-endpoint/",
      Map.empty,
      BearerToken("myToken", "", "", "", "", 1L, "")
    )
    assert(actual == Right(Option(Sample("2", 2))))
  }

  test("delete logs error on Left(UnexpectedBodyErr) with NoAuth") {
    (baseHttp
      .delete[Sample](_: String, _: Map[String, String], _: Auth)(
        _: Decoder[Sample]
      ))
      .when("http://my-endpoint/", *, NoAuth(), *)
      .returns(
        Left(
          UnexpectedBodyErr(
            "my body",
            DecodeErr(DecodingFailure("Attempt to decode value on failed cursor", List(DownField("id"))))
          )
        )
      )

    inSequence(
      (audit.info _)
        .expects("Executing http DELETE request to http://my-endpoint/;auth=NoAuth()")
        .once(),
      (audit.error _)
        .expects(where { s: String =>
          s.startsWith(
            "Http DELETE request to http://my-endpoint/ failed. Details: UnexpectedBodyErr(my body,DecodeErr(DecodingFailure at .id: Attempt to decode value on failed cursor))"
          )
        })
        .once()
    )

    val actual =
      http.delete[Sample]("http://my-endpoint/", Map.empty, NoAuth())
    assert(
      actual == Left(
        UnexpectedBodyErr(
          "my body",
          DecodeErr(DecodingFailure("Attempt to decode value on failed cursor", List(DownField("id"))))
        )
      )
    )
  }

  test("delete logs error on Left(ServerErr) with NoAuth") {
    (baseHttp
      .delete[Sample](_: String, _: Map[String, String], _: Auth)(
        _: Decoder[Sample]
      ))
      .when("http://my-endpoint/", *, NoAuth(), *)
      .returns(Left(ServerErr(1, "x")))

    inSequence(
      (audit.info _)
        .expects("Executing http DELETE request to http://my-endpoint/;auth=NoAuth()")
        .once(),
      (audit.error _)
        .expects("Http DELETE request to http://my-endpoint/ failed. Details: ServerErr(1,x)")
        .once()
    )

    val actual =
      http.delete[Sample]("http://my-endpoint/", Map.empty, NoAuth())
    assert(actual == Left(ServerErr(1, "x")))
  }

  test("delete logs error on Left(ClientErr) with NoAuth") {
    (baseHttp
      .delete[Sample](_: String, _: Map[String, String], _: Auth)(
        _: Decoder[Sample]
      ))
      .when("http://my-endpoint/", *, NoAuth(), *)
      .returns(Left(ClientErr(1, "x")))

    inSequence(
      (audit.info _)
        .expects("Executing http DELETE request to http://my-endpoint/;auth=NoAuth()")
        .once(),
      (audit.error _)
        .expects("Http DELETE request to http://my-endpoint/ failed. Details: ClientErr(1,x)")
        .once()
    )

    val actual =
      http.delete[Sample]("http://my-endpoint/", Map.empty, NoAuth())
    assert(actual == Left(ClientErr(1, "x")))
  }

  test("delete logs error on Left(GenericErr) with NoAuth") {
    (baseHttp
      .delete[Sample](_: String, _: Map[String, String], _: Auth)(
        _: Decoder[Sample]
      ))
      .when("http://my-endpoint/", *, NoAuth(), *)
      .returns(Left(GenericErr(1, "x")))

    inSequence(
      (audit.info _)
        .expects("Executing http DELETE request to http://my-endpoint/;auth=NoAuth()")
        .once(),
      (audit.error _)
        .expects("Http DELETE request to http://my-endpoint/ failed. Details: GenericErr(1,x)")
        .once()
    )

    val actual =
      http.delete[Sample]("http://my-endpoint/", Map.empty, NoAuth())
    assert(actual == Left(GenericErr(1, "x")))
  }

}
