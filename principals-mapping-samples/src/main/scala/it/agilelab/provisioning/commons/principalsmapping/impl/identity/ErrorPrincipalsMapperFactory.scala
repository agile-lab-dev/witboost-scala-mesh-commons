package it.agilelab.provisioning.commons.principalsmapping.impl.identity

import com.typesafe.config.Config
import it.agilelab.provisioning.commons.principalsmapping.{
  CdpIamPrincipals,
  PrincipalsMapper,
  PrincipalsMapperFactory
}

import scala.util.{ Success, Try }

class ErrorPrincipalsMapperFactory extends PrincipalsMapperFactory[CdpIamPrincipals] {

  override def create(config: Config): Try[PrincipalsMapper[CdpIamPrincipals]] =
    Success(new ErrorPrincipalsMapper)

  override def configIdentifier: String = "error"
}
