package it.agilelab.provisioning.commons.config

import com.typesafe.config.Config
import it.agilelab.provisioning.commons.config.ConfError.ConfKeyNotFoundErr
import org.scalamock.scalatest.MockFactory
import org.scalatest.funsuite.AnyFunSuite

class DefaultConfTest extends AnyFunSuite with MockFactory {

  val config: Config = stub[Config]
  val configGateway  = new DefaultConf(config)

  Seq(
    ("key1", "value1"),
    ("key2", "value2")
  ) foreach { case (key: String, value: String) =>
    test(s"get return $value with $key") {
      (config.getString _).when(key).returns(value)
      assert(configGateway.get(key) == Right(value))
    }

    test(s"get return Left(ConfigNotFound($key)") {
      (config.getString _).when(*).throws(new IllegalArgumentException())
      assert(configGateway.get(key) == Left(ConfKeyNotFoundErr(key)))
    }
  }

}
