package it.agilelab.provisioning.commons.principalsmapping.impl.identity

import it.agilelab.provisioning.commons.principalsmapping.CdpIamUser
import org.scalatest.funsuite.AnyFunSuite

class IdentityPrincipalsMapperTest extends AnyFunSuite {

  val mapper = new IdentityPrincipalsMapper

  test("IdentityMapper should output the exact same input") {
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
      val (input, output) = ans
      assert(output.isRight)
      output.foreach(out => assert(out.equals(CdpIamUser(input, input, input))))
    }
  }

}
