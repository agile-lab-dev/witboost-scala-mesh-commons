package it.agilelab.provisioning.commons.validator

import cats.data.NonEmptyList
import cats.data.Validated.{ invalidNel, Invalid, Valid }
import org.scalatest.funsuite.AnyFunSuite

class ValidatorTest extends AnyFunSuite {

  test("validate a string return invalid") {
    val actual =
      Validator[String]
        .rule(_ == "my-test-string", m => s"should be my-test-string, actual value is: $m")
        .validate("my-string")

    val expected = invalidNel(ValidationFail("my-string", "should be my-test-string, actual value is: my-string"))
    assert(actual == Right(expected))
  }

  test("validate a string return valid") {
    val actual =
      Validator[String]
        .rule(_ == "my-string", m => s"should be my-test-string, actual value is: $m")
        .validate("my-string")

    val expected = Right(Valid("my-string"))
    assert(actual == expected)
  }

  test("validate an int return Invalid") {
    val actual =
      Validator[Int]
        .rule(_ == 3, m => s"should be 3, actual value is $m")
        .validate(4)

    val expected = invalidNel(ValidationFail(4, s"should be 3, actual value is 4"))
    assert(actual == Right(expected))
  }

  test("validate an int return Valid") {
    val actual = Validator[Int]
      .rule(_ == 3, m => s"should be 3, actual value is $m")
      .validate(3)

    assert(actual == Right(Valid(3)))
  }

  test("validate a case class return invalid") {
    case class MyModel(id: Int, desc: String)
    val actual =
      Validator[MyModel]
        .rule(_.id > 0, m => s"id should be gt 0, actual value is ${m.id}")
        .rule(_.desc.nonEmpty, m => s"desc should be not empty, actual value is ${m.desc}")
        .validate(MyModel(0, ""))

    val expected = Invalid(
      NonEmptyList(
        ValidationFail(MyModel(0, ""), "id should be gt 0, actual value is 0"),
        List(ValidationFail(MyModel(0, ""), "desc should be not empty, actual value is "))
      )
    )
    assert(actual == Right(expected))
  }

  test("validate a case class return valid") {
    case class MyModel(id: Int, desc: String)
    val actual = Validator[MyModel]
      .rule(_.id > 0, m => s"id should be gt 0, actual value is ${m.id}")
      .rule(_.desc.nonEmpty, m => s"desc should be not empty, actual value is ${m.desc}")
      .validate(MyModel(1, "x"))
    assert(actual == Right(Valid(MyModel(1, "x"))))
  }

  test("validate with exception") {
    case class MyModel(id: Int, desc: String)
    val actual = Validator[MyModel]
      .rule(_.id > 0, m => s"id should be gt 0, actual value is ${m.id}")
      .rule(_.desc.toInt == 4, m => s"desc should be not empty, actual value is ${m.desc}")
      .validate(MyModel(1, "x"))

    assert(actual.isLeft)
    assert(actual.left.exists(l => l.entity == MyModel(1, "x") && l.throwable.isInstanceOf[NumberFormatException]))
  }
}
