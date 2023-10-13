package it.agilelab.provisioning.commons.support.parser

import io.circe.CursorOp.DownField
import io.circe.DecodingFailure
import io.circe.generic.auto._
import it.agilelab.provisioning.commons.support.ParserError.DecodeErr
import it.agilelab.provisioning.commons.support.{ ParserError, ParserSupport }
import org.scalatest.EitherValues
import org.scalatest.funsuite.AnyFunSuite

class ParserSupportTest extends AnyFunSuite with ParserSupport with EitherValues {

  case class Simple(id: String, seq: Seq[String])
  case class Nested(simple: Simple)

  Seq(
    ("""{"id":"id1","seq":["1","2","3"]}""", Right(Simple("id1", Seq("1", "2", "3")))),
    ("""{"id":"id2","seq":["4","5","6"]}""", Right(Simple("id2", Seq("4", "5", "6")))),
    ("""{"id":"id3","seq":["7","8","9"]}""", Right(Simple("id3", Seq("7", "8", "9"))))
  ) foreach { case (json: String, expected: Either[ParserError, Simple]) =>
    test(s"fromJson with simple case class return $expected") {
      assert(fromJson[Simple](json) == expected)
    }
  }

  test(s"fromJson with simple case class return Left(DecodeError)") {
    val actual = fromJson[Simple]("""{"id":"seq":["1"]}""")
    assert(actual.isLeft)
    assert(
      actual.left.value
        .asInstanceOf[DecodeErr]
        .error
        .getMessage == "expected } or , got ':[\"1\"]...' (line 1, column 12)"
    )
  }

  Seq(
    ("""{"simple":{"id":"id1","seq":["1","2","3"]}}""", Right(Nested(Simple("id1", Seq("1", "2", "3"))))),
    ("""{"simple":{"id":"id2","seq":["4","5","6"]}}""", Right(Nested(Simple("id2", Seq("4", "5", "6"))))),
    ("""{"simple":{"id":"id3","seq":["7","8","9"]}}""", Right(Nested(Simple("id3", Seq("7", "8", "9"))))),
    (
      """{"simple":"x","seq":["1"]}""",
      Left(
        DecodeErr(
          DecodingFailure("Attempt to decode value on failed cursor", List(DownField("id"), DownField("simple")))
        )
      )
    )
  ) foreach { case (json: String, expected: Either[ParserError, Nested]) =>
    test(s"fromJson with nested case class return $expected") {
      assert(fromJson[Nested](json) == expected)
    }
  }

  Seq(
    ("id: id1\nseq:\n  - '1'", Right(Simple("id1", Seq("1")))),
    ("id: id2\nseq:\n  - '2'\n  - '3'", Right(Simple("id2", Seq("2", "3")))),
    ("id: id3\nseq:\n  - '4'\n  - '5'", Right(Simple("id3", Seq("4", "5")))),
    ("id: id3\nseq:", Left(DecodeErr(DecodingFailure("C[A]", List(DownField("seq"))))))
  ) foreach { case (json: String, expected: Either[ParserError, Simple]) =>
    test(s"fromYml with simple case class return $expected") {
      assert(fromYml[Simple](json) == expected)
    }
  }

  Seq(
    ("simple: \n  id: id1 \n  seq: \n   - '1'", Right(Nested(Simple("id1", Seq("1"))))),
    ("simple: \n  id: id2 \n  seq: \n   - '2'", Right(Nested(Simple("id2", Seq("2"))))),
    ("simple: \n  id: id3 \n  seq: \n   - '3'\n   - '4'", Right(Nested(Simple("id3", Seq("3", "4"))))),
    (
      "simple: \n  id: id3",
      Left(
        DecodeErr(
          DecodingFailure("Attempt to decode value on failed cursor", List(DownField("seq"), DownField("simple")))
        )
      )
    )
  ) foreach { case (yml: String, expected: Either[ParserError, Nested]) =>
    test(s"fromYml with nested case class return $expected") {
      assert(fromYml[Nested](yml) == expected)
    }
  }

  Seq(
    (Simple("id1", Seq("1", "2", "3")), """{"id":"id1","seq":["1","2","3"]}"""),
    (Simple("id2", Seq("4", "5", "6")), """{"id":"id2","seq":["4","5","6"]}"""),
    (Simple("id3", Seq("7", "8", "9")), """{"id":"id3","seq":["7","8","9"]}""")
  ) foreach { case (instance: Simple, expected: String) =>
    test(s"toJson with nested case class return $expected") {
      assert(toJson(instance) == expected)
    }
  }

}
