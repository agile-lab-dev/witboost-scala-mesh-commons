package it.agilelab.provisioning.commons.config

import com.typesafe.config.ConfigFactory
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

  test(s"getConfig return Right() and call audit") {
    val c = ConfigFactory.empty()
    (configGateway.getConfig _).when("key").returns(Right(c))
    (audit.info _).expects(s"Retrieving config object: key")
    (audit.info _).expects(s"Config object key retrieved successfully")
    assert(configGatewayWithAudit.getConfig("key") == Right(c))
  }

  test(s"getConfig return Left(ConfigNotFound) and call audit") {
    (configGateway.getConfig _).when("key").returns(Left(ConfKeyNotFoundErr("key")))
    (audit.info _).expects(s"Retrieving config object: key")
    (audit.error _).expects(s"Config object key retrieve failed. Details: ConfKeyNotFoundErr(key)")
    assert(configGatewayWithAudit.getConfig("key") == Left(ConfKeyNotFoundErr("key")))
  }

}
