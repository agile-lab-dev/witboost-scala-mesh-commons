package it.agilelab.provisioning.commons.principalsmapping.impl.identity

import it.agilelab.provisioning.commons.principalsmapping.PrincipalsMapperError.PrincipalMappingError
import it.agilelab.provisioning.commons.principalsmapping.{
  CdpIamPrincipals,
  ErrorMoreInfo,
  PrincipalsMapper,
  PrincipalsMapperError
}

class ErrorPrincipalsMapper extends PrincipalsMapper[CdpIamPrincipals] {
  override def map(subjects: Set[String]): Map[String, Either[PrincipalsMapperError, CdpIamPrincipals]] =
    subjects
      .map(s =>
        (s -> Left(PrincipalMappingError(ErrorMoreInfo(List("Received unexpected mapping request"), List.empty), None)))
      )
      .toMap
}
