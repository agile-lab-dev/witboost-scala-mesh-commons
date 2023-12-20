package it.agilelab.provisioning.commons.principalsmapping.impl.identity

import org.scalatest.funsuite.AnyFunSuite

class ErrorPrincipalsMapperTest extends AnyFunSuite {
  val mapper = new ErrorPrincipalsMapper

  test("ErrorMapper should always return Left") {
    val list = List(
      "user1",
      "user1_agilelab.it",
      "user1@agilelab.it",
      "user_name_agilelab.it",
      "group:bigData",
      "user:user1_agilelab.it",
      "user:user_name_agilelab.it"
    )

    val res = mapper.map(list.toSet)
    assert(res.keys.toList.equals(list.toSet.toList))
    res.foreach { ans =>
      val (_, output) = ans
      assert(output.isLeft)
    }
  }
}
