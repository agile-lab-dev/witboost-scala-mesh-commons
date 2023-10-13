package it.agilelab.provisioning.commons.validator

import cats.Semigroup
import cats.data.Validated.{ invalidNel, Valid }
import cats.data.{ NonEmptyList, Validated }

/** Validator trait
  * @tparam A: A data type to validate
  */
trait Validator[A] {
  def validate(entity: A): Either[ValidatorError[A], Validated[NonEmptyList[ValidationFail[A]], A]]
}

/** A Validator that
  *
  * Generate some rule against a specific data type.
  * Rule are not executed until validate is called.
  *
  * Start from an empty validator each rule will generate a new validator that combine the
  * existing validator and the newest one with the provided rule
  * Example usage:
  *
  * {{{
  *   case class MyModel(id: Int, desc: String)
  *
  *   Validator[MyModel]
  *    .rule(_.id > 0, "id should be gt 0")
  *    .rule(_.desc.toInt == 4, "desc should be not empty")
  *    .validate(MyModel(1, "x"))
  * }}}
  */
object Validator {

  /** Generate an empty validator that will return valid by default as currently there is no rule to verify
    * @tparam A: A data type to validate
    * @return An empty validator of type `A`
    */
  def apply[A]: Validator[A] = (obj: A) => Right(Valid(obj))

  implicit class ValidatorOps[A](validator: Validator[A]) {

    /** Add a rule to the existing validator
      * @param rule: rule to satisfy
      * @param onFailureMessage: message on failure
      * @return a new Validator of type `A`
      */
    def rule(rule: A => Boolean, onFailureMessage: A => String): Validator[A] =
      compose((obj: A) =>
        try if (rule(obj)) Right(Valid(obj).toValidatedNel)
        else Right(invalidNel(ValidationFail(obj, onFailureMessage(obj))))
        catch { case t: Throwable => Left(ValidatorError(obj, t)) }
      )

    private def compose(otherValidator: Validator[A]): Validator[A] =
      (obj: A) => {
        implicit val semigroup: Semigroup[A] = (x: A, _: A) => x
        for {
          current <- validator.validate(obj)
          other   <- otherValidator.validate(obj)
        } yield current.combine(other)
      }
  }

}
