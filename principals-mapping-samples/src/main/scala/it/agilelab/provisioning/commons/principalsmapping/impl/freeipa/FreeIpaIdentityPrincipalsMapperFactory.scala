package it.agilelab.provisioning.commons.principalsmapping.impl.freeipa

import com.typesafe.config.Config
import com.typesafe.scalalogging.Logger
import it.agilelab.provisioning.commons.client.cdp.iam.CdpIamClient
import it.agilelab.provisioning.commons.principalsmapping.{
  CdpIamPrincipals,
  PrincipalsMapper,
  PrincipalsMapperFactory
}

import scala.util.Try

class FreeIpaIdentityPrincipalsMapperFactory extends PrincipalsMapperFactory[CdpIamPrincipals] {

  override def create(config: Config): Try[PrincipalsMapper[CdpIamPrincipals]] =
    CdpIamClient
      .default()
      .map(client => new FreeIpaIdentityPrincipalsMapper(client, Logger("FreeIpaIdentityPrincipalsMapper")))
      .toTry

  override def configIdentifier: String = "freeipa-identity"
}
