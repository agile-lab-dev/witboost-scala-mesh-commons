package it.agilelab.provisioning.mesh.self.service.lambda.core.model

import it.agilelab.provisioning.commons.support.ParserSupport
import it.agilelab.provisioning.mesh.self.service.lambda.core.model.ComponentOperation._
import org.scalatest.funsuite.AnyFunSuite

class ComponentOperationTest extends AnyFunSuite with ParserSupport {
  Seq(
    ("\"CREATE\"", Create),
    ("\"DESTROY\"", Destroy)
  ) foreach { case (json: String, cmpOp: ComponentOperation) =>
    test(s"toJson with $cmpOp return $json") {
      assert(toJson(cmpOp) == json)
    }

    test(s"fromJson with $json return $cmpOp") {
      assert(fromJson[ComponentOperation](json) == Right(cmpOp))
    }
  }

  Seq(
    ("\"Create\"", Create),
    ("\"create\"", Create),
    ("\"Destroy\"", Destroy),
    ("\"destroy\"", Destroy)
  ) foreach { case (json: String, cmpOp: ComponentOperation) =>
    test(s"fromJson with $json return $cmpOp") {
      assert(fromJson[ComponentOperation](json) == Right(cmpOp))
    }
  }

}
