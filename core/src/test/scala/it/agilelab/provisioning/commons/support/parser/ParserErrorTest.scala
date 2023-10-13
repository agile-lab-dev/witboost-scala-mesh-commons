package it.agilelab.provisioning.commons.support.parser

import cats.implicits._
import io.circe.DecodingFailure
import it.agilelab.provisioning.commons.support.ParserError
import it.agilelab.provisioning.commons.support.ParserError.{ DecodeErr, EncodeErr }
import org.scalamock.scalatest.MockFactory
import org.scalatest.funsuite.AnyFunSuite

class ParserErrorTest extends AnyFunSuite with MockFactory {

  test("show DecodeErr") {
    val circeError: io.circe.Error = DecodingFailure("x", List.empty)
    val decodeErr: ParserError     = DecodeErr(circeError)
    val actual                     = show"$decodeErr"
    assert(actual == "DecodeErr(DecodingFailure at : x)")
  }

  test("show EncodeErr") {
    val encodeErr: ParserError = EncodeErr(new Exception(""))
    val actual                 = show"$encodeErr"
    assert(actual.startsWith("EncodeErr(java.lang.Exception"))
  }
}
