package it.agilelab.provisioning.commons.principalsmapping.impl.identity

import com.typesafe.config.Config
import it.agilelab.provisioning.commons.principalsmapping.{ CdpIamUser, PrincipalsMapper, PrincipalsMapperFactory }

import scala.util.{ Success, Try }

class IdentityPrincipalsMapperFactory extends PrincipalsMapperFactory[CdpIamUser] {

  override def create(config: Config): Try[PrincipalsMapper[CdpIamUser]] = Success(new IdentityPrincipalsMapper)

  override def configIdentifier: String = "identity"
}
