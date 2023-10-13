package it.agilelab.provisioning.commons.config

import it.agilelab.provisioning.commons.audit.Audit
import it.agilelab.provisioning.commons.config.ConfError.ConfKeyNotFoundErr
import org.scalamock.scalatest.MockFactory
import org.scalatest.funsuite.AnyFunSuite

class DefaultConfWithAuditTest extends AnyFunSuite with MockFactory {

  val audit: Audit               = mock[Audit]
  val configGateway: DefaultConf = stub[DefaultConf]
  val configGatewayWithAudit     = new DefaultConfWithAudit(configGateway, audit)

  test(s"get return Right() and call audit") {
    (configGateway.get _).when("key").returns(Right("value"))
    (audit.info _).expects(s"Retrieving config: key")
    (audit.info _).expects(s"Config key retrieved successfully")
    assert(configGatewayWithAudit.get("key") == Right("value"))
  }

  test(s"get return Left(ConfigNotFound) and call audit") {
    (configGateway.get _).when("key").returns(Left(ConfKeyNotFoundErr("key")))
    (audit.info _).expects(s"Retrieving config: key")
    (audit.error _).expects(s"Config key retrieve failed. Details: ConfKeyNotFoundErr(key)")
    assert(configGatewayWithAudit.get("key") == Left(ConfKeyNotFoundErr("key")))
  }

}
