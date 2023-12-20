package it.agilelab.provisioning.commons.principalsmapping.impl.identity

import com.typesafe.config.Config
import it.agilelab.provisioning.commons.principalsmapping.{
  CdpIamPrincipals,
  PrincipalsMapper,
  PrincipalsMapperFactory
}

import scala.util.{ Success, Try }

class ErrorPrincipalsMapperFactory extends PrincipalsMapperFactory[CdpIamPrincipals] {

  /** Creates a Mapper object that will be used to map subjects
    *
    * @param config the config object that might be needed to create the Mapper
    * @return the mapper
    */
  override def create(config: Config): Try[PrincipalsMapper[CdpIamPrincipals]] =
    Success(new ErrorPrincipalsMapper)

  /** The [[configIdentifier]] defines the config key that the consumer will use to specify the configuration of this plugin inside the `principalmappingplugin` block.
    * {{{
    *   principals-mapping {
    *     mymapperid {
    *       foo: "bar"
    *     }
    *   }
    * }}}
    *
    * @return the identifier
    */
  override def configIdentifier: String = "error"
}
