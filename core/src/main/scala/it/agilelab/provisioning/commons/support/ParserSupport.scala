package it.agilelab.provisioning.commons.support

import cats.implicits._
import io.circe.jawn.parse
import io.circe.syntax._
import io.circe.{ Decoder, Encoder, yaml, _ }
import it.agilelab.provisioning.commons.support.ParserError.DecodeErr

/** ParserSupport
  *
  * Provide a set of function based to circe.io library for serializing
  * and deserializing object to and from JSON and YML
  */
trait ParserSupport {

  /** Generate an instance of type [[A]] starting from an input json.
    *
    * Example usage
    *
    * {{{
    * case class MyCaseClass(id:Int, desc:String)
    * class MyServiceClass extends ParserSupport{
    *
    *   def exec(): Unit = {
    *     val myCaseClassJson = """{"id":1,"desc":"my desc"}"""
    *     fromJson[MyCaseClass](myCaseClassJson).map(println)
    *   }
    * }
    * }}}
    *
    * @param json the input json
    * @param decoder an implicit decoder of type A
    * @tparam A a Type parameter
    * @return Either[ParserError,A]
    *         Right([[A]]) if parse works fine
    *         Left([[ParserError]]) otherwise
    */
  def fromJson[A](json: String)(implicit decoder: Decoder[A]): Either[ParserError, A] =
    castType[A](parse(json))

  /** Generate an instance of type [[A]] starting from an input yml.
    *
    * Example usage
    *
    * {{{
    * case class MyCaseClass(id:Int, seq:Seq[String])
    * class MyServiceClass extends ParserSupport{
    *
    *   def exec(): Unit = {
    *     val myCaseClassYml = """id: id1
    *                             |seq:
    *                             | - '1'
    *                             |""".stripMargin
    *     fromYml[MyCaseClass](myCaseClassYml).map(println)
    *   }
    * }
    * }}}
    *
    * @param yml the input yml
    * @param decoder an Implicit [[Decoder]] of type [[A]]
    * @tparam A a Type parameter
    * @return Either[ParserError,A]
    *         Right([[A]]) if parse works fine
    *         Left([[ParserError]]) otherwise
    */
  def fromYml[A](yml: String)(implicit decoder: Decoder[A]): Either[ParserError, A] =
    castType[A](yaml.parser.parse(yml))

  /** Serialize an instance of A into a json string
    * @param a input class
    * @param encoder implicit encoder for the InputClass
    * @tparam A the Type A
    * @return Either[Error,String]
    *         Right(String) if the serialization works fine
    *         Left(Error) otherwise
    */
  def toJson[A](a: A)(implicit encoder: Encoder[A]): String =
    a.asJson.noSpaces

  private def castType[A](
    json: Either[ParsingFailure, Json]
  )(implicit decoder: Decoder[A]): Either[ParserError, A] =
    json
      .flatMap(_.as[A])
      .leftMap((e: Error) => DecodeErr(e))
}
