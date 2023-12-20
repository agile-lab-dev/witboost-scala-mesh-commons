package it.agilelab.provisioning.commons.principalsmapping

sealed trait CdpIamPrincipals

/** Representation of a user of CDP IAM
  * @param userId UUID of the user inside CDP
  * @param workloadUsername Workload username used by the user to perform actions on Cloudera environments. This username is synced on Ranger for access control
  * @param crn Cloudera Resource Name of the user
  */
final case class CdpIamUser(userId: String, workloadUsername: String, crn: String) extends CdpIamPrincipals

/** Representation of a group of CDP IAM
  * @param groupName Group name. The name is synced on Ranger for access control
  * @param crn Cloudera Resource Name of the group
  */
final case class CdpIamGroup(groupName: String, crn: String) extends CdpIamPrincipals

trait PrincipalsMapper[PRINCIPAL <: CdpIamPrincipals] {

  /** This method defines the main mapping logic
    *
    * @param subjects Set of subjects, i.e. witboost users and groups
    * @return the mapping. For each subject, either an PrincipalMappingError, or the successfully mapped principal is returned
    */
  def map(subjects: Set[String]): Map[String, Either[PrincipalsMapperError, PRINCIPAL]]
}
