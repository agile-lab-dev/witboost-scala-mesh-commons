package it.agilelab.provisioning.commons.principalsmapping.impl.freeipa

import cats.implicits.{ showInterpolator, toBifunctorOps }
import com.typesafe.scalalogging.Logger
import it.agilelab.provisioning.commons.client.cdp.iam.CdpIamClient
import it.agilelab.provisioning.commons.principalsmapping.PrincipalsMapperError.{
  PrincipalMappingError,
  PrincipalMappingSystemError
}
import it.agilelab.provisioning.commons.principalsmapping._

/** Principals mapper which performs a 1-to-1 operation removing user: and group: prefixes.
  * On emails, it also removes the domain.
  *
  * Furthermore, it contacts CDP FreeIPA to validate the mapped principals exist on the platform
  */
class FreeIpaIdentityPrincipalsMapper(cdpIamClient: CdpIamClient, logger: Logger)
    extends PrincipalsMapper[CdpIamPrincipals] {
  override def map(subjects: Set[String]): Map[String, Either[PrincipalsMapperError, CdpIamPrincipals]] =
    subjects.map {
      case ref @ s"user:$user"   =>
        val underscoreIndex = user.lastIndexOf("_")
        val userId          = if (underscoreIndex.equals(-1)) user else user.substring(0, underscoreIndex)
        ref -> getAndMapUser(userId)
      case ref @ s"group:$group" => ref -> getAndMapGroup(group)
      case ref                   =>
        ref -> getAndMapUser(ref).left
          .flatMap(_ => getAndMapGroup(ref))
          .leftMap(_ =>
            PrincipalMappingError(
              ErrorMoreInfo(
                List(s"Received principal '$ref' which doesn't exists as a group nor as an user on CDP"),
                List.empty
              ),
              None
            )
          )
    }.toMap

  private def getAndMapUser(userId: String): Either[PrincipalsMapperError, CdpIamPrincipals] =
    cdpIamClient
      .getUserByWorkloadUsername(userId) match {
      case Right(Some(user)) =>
        logger.info("Mapped successfully ref {} to principal {}", userId, user)
        Right(CdpIamUser(userId = user.getUserId, workloadUsername = user.getWorkloadUsername, crn = user.getCrn))
      case Right(None)       =>
        logger.error("Error while mapping ref {}. Not found", userId)
        Left(
          PrincipalMappingError(
            ErrorMoreInfo(
              List(s"Cannot find user '$userId' in CDP system"),
              List(
                s"Check that the user exists on the CDP instance, or try login into CDP with said user to create and sync it into the platform"
              )
            ),
            None
          )
        )
      case Left(iamErr)      =>
        logger.error("Error while mapping ref {}. Received exception", show"$iamErr")
        Left(
          PrincipalMappingSystemError(
            ErrorMoreInfo(List(s"Error while querying CDP for user '$userId'"), List.empty),
            iamErr
          )
        )
    }

  private def getAndMapGroup(groupName: String): Either[PrincipalsMapperError, CdpIamPrincipals] =
    cdpIamClient
      .getGroup(groupName) match {
      case Right(Some(group)) => Right(CdpIamGroup(groupName = group.getGroupName, crn = group.getCrn))
      case Right(None)        =>
        Left(
          PrincipalMappingError(
            ErrorMoreInfo(
              List(s"Cannot find group '$groupName' in CDP system"),
              List(s"Ensure that the group exists on the CDP instance, otherwise create it and try again")
            ),
            None
          )
        )
      case Left(iamErr)       =>
        Left(
          PrincipalMappingSystemError(
            ErrorMoreInfo(
              List(s"Error while querying CDP for group '$groupName'"),
              List.empty
            ),
            iamErr
          )
        )
    }
}
