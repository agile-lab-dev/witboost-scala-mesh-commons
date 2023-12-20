package it.agilelab.provisioning.commons.principalsmapping.impl.identity

import it.agilelab.provisioning.commons.principalsmapping.{ CdpIamUser, PrincipalsMapper, PrincipalsMapperError }

/** Principals mapper which performs a no-op operation for each subject, returning always Right
  */
class IdentityPrincipalsMapper extends PrincipalsMapper[CdpIamUser] {
  override def map(subjects: Set[String]): Map[String, Either[PrincipalsMapperError, CdpIamUser]] =
    subjects.map(s => (s -> Right(CdpIamUser(s, s, s)))).toMap
}
