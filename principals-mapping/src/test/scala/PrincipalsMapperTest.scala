import cats.implicits.showInterpolator
import it.agilelab.provisioning.commons.principalsmapping.ErrorMoreInfo
import it.agilelab.provisioning.commons.principalsmapping.PrincipalsMapperError.PrincipalMappingError
import org.scalatest.funsuite.AnyFunSuite

class PrincipalsMapperTest extends AnyFunSuite {

  test("error should add the cause if exists") {
    val error = new RuntimeException("This is some error")

    val mapperError = PrincipalMappingError(ErrorMoreInfo(List("Error"), List("Solutions")), Some(error))
    val errorString = show"$mapperError"
    assert(
      errorString.startsWith(
        "PrincipalMappingError(ErrorMoreInfo(problems = [Error], solutions = [Solutions]),java.lang.RuntimeException"
      )
    )
  }

  test("error should not have cause if there is no one available") {
    val mapperError = PrincipalMappingError(ErrorMoreInfo(List("Error"), List("Solutions")), None)
    val errorString = show"$mapperError"
    assert(
      errorString.equals(
        "PrincipalMappingError(ErrorMoreInfo(problems = [Error], solutions = [Solutions]),)"
      )
    )
  }
}
