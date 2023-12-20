package it.agilelab.provisioning.commons.principalsmapping.impl.freeipa

import com.cloudera.cdp.CdpClientException
import com.cloudera.cdp.iam.model.{ Group, User }
import com.typesafe.scalalogging.Logger
import it.agilelab.provisioning.commons.client.cdp.iam.CdpIamClient
import it.agilelab.provisioning.commons.client.cdp.iam.CdpIamClientError.{ GetGroupErr, GetUserErr }
import it.agilelab.provisioning.commons.principalsmapping.PrincipalsMapperError.{
  PrincipalMappingError,
  PrincipalMappingSystemError
}
import it.agilelab.provisioning.commons.principalsmapping.{ CdpIamGroup, CdpIamUser, ErrorMoreInfo }
import org.scalamock.scalatest.MockFactory
import org.scalatest.funsuite.AnyFunSuite

class FreeIpaIdentityPrincipalsMapperTest extends AnyFunSuite with MockFactory {

  val iamMock = mock[CdpIamClient]
  val mapper  = new FreeIpaIdentityPrincipalsMapper(iamMock, Logger("FreeIpaIdentityPrincipalsMapperTest"))

  test("free ipa should return the correct user when receiving email") {
    val user        = "user:sergio.mejia_agilelab.it"
    val expectedRef = CdpIamUser("", "sergio.mejia", "a:urn")

    val expectedReturn = new User()
    expectedReturn.setCrn(expectedRef.crn)
    expectedReturn.setUserId(expectedRef.userId)
    expectedReturn.setWorkloadUsername(expectedRef.workloadUsername)

    (iamMock.getUserByWorkloadUsername _).expects(expectedRef.workloadUsername).returns(Right(Some(expectedReturn)))
    val mappedUser = mapper.map(Set(user))

    mappedUser.foreach { case (_, mappedRef) =>
      assert(mappedRef.isRight)
      mappedRef.foreach(t => assert(t.equals(expectedRef)))
    }
  }

  test("free ipa should return the correct user when receiving email. Use case 2") {
    val user        = "user:sergio_mejia_agilelab.it"
    val expectedRef = CdpIamUser("", "sergio_mejia", "a:urn")

    val expectedReturn = new User()
    expectedReturn.setCrn(expectedRef.crn)
    expectedReturn.setUserId(expectedRef.userId)
    expectedReturn.setWorkloadUsername(expectedRef.workloadUsername)

    (iamMock.getUserByWorkloadUsername _).expects(expectedRef.workloadUsername).returns(Right(Some(expectedReturn)))
    val mappedUser = mapper.map(Set(user))

    mappedUser.foreach { case (_, mappedRef) =>
      assert(mappedRef.isRight)
      mappedRef.foreach(t => assert(t.equals(expectedRef)))
    }
  }

  test("free ipa should return the correct user when receiving group") {
    val group       = "group:bigData"
    val expectedRef = CdpIamGroup("bigData", "a:urn")

    val expectedReturn = new Group()
    expectedReturn.setCrn(expectedRef.crn)
    expectedReturn.setGroupName(expectedRef.groupName)

    (iamMock.getGroup _).expects(expectedRef.groupName).returns(Right(Some(expectedReturn)))
    val mappedGroup = mapper.map(Set(group))

    mappedGroup.foreach { case (_, mappedRef) =>
      assert(mappedRef.isRight)
      mappedRef.foreach(t => assert(t.equals(expectedRef)))
    }
  }

  test("free ipa should return the correct user when receiving another input") {
    val user        = "anotherUser"
    val expectedRef = CdpIamUser("", "anotherUser", "a:urn")

    val expectedReturn = new User()
    expectedReturn.setCrn(expectedRef.crn)
    expectedReturn.setUserId(expectedRef.userId)
    expectedReturn.setWorkloadUsername(expectedRef.workloadUsername)

    (iamMock.getUserByWorkloadUsername _).expects(expectedRef.workloadUsername).returns(Right(Some(expectedReturn)))
    val mappedUser = mapper.map(Set(user))

    mappedUser.foreach { case (_, mappedRef) =>
      assert(mappedRef.isRight)
      mappedRef.foreach(t => assert(t.equals(expectedRef)))
    }
  }

  test("free ipa should return the correct group when receiving another input") {
    val user        = "anotherGroup"
    val expectedRef = CdpIamGroup("anotherGroup", "a:urn")

    val expectedReturn = new Group()
    expectedReturn.setCrn(expectedRef.crn)
    expectedReturn.setGroupName(expectedRef.groupName)

    (iamMock.getUserByWorkloadUsername _).expects(expectedRef.groupName).returns(Right(None))
    (iamMock.getGroup _).expects(expectedRef.groupName).returns(Right(Some(expectedReturn)))
    val mappedGroup = mapper.map(Set(user))

    mappedGroup.foreach { case (_, mappedRef) =>
      assert(mappedRef.isRight)
      mappedRef.foreach(t => assert(t.equals(expectedRef)))
    }
  }

  test("return Left(PrincipalMappingErr()) if user doesn't exist on FreeIPA") {
    val user       = "user:anotherUser"
    (iamMock.getUserByWorkloadUsername _).expects("anotherUser").returns(Right(None))
    val mappedUser = mapper.map(Set(user))

    mappedUser.foreach { case (_, mappedRef) =>
      assert(mappedRef.isLeft)
      mappedRef.left.foreach(t =>
        assert(
          t == PrincipalMappingError(
            ErrorMoreInfo(
              List("Cannot find user 'anotherUser' in CDP system"),
              List(
                s"Check that the user exists on the CDP instance, or try login into CDP with said user to create and sync it into the platform"
              )
            ),
            None
          )
        )
      )
    }
  }

  test("return Left(PrincipalMappingSystemError()) if there was an error while querying a group") {
    val user       = "user:anotherUser"
    val error      = GetUserErr(user, new CdpClientException("x"))
    (iamMock.getUserByWorkloadUsername _).expects("anotherUser").returns(Left(error))
    val mappedUser = mapper.map(Set(user))

    mappedUser.foreach { case (_, mappedRef) =>
      assert(mappedRef.isLeft)
      mappedRef.left.foreach(t =>
        assert(
          t == PrincipalMappingSystemError(
            ErrorMoreInfo(List("Error while querying CDP for user 'anotherUser'"), List.empty),
            error
          )
        )
      )
    }
  }

  test("return Left(PrincipalMappingErr()) if group doesn't exist on FreeIPA") {
    val group       = "group:anotherGroup"
    (iamMock.getGroup _).expects("anotherGroup").returns(Right(None))
    val mappedGroup = mapper.map(Set(group))

    mappedGroup.foreach { case (_, mappedRef) =>
      assert(mappedRef.isLeft)
      mappedRef.left.foreach(t =>
        assert(
          t == PrincipalMappingError(
            ErrorMoreInfo(
              List("Cannot find group 'anotherGroup' in CDP system"),
              List(s"Ensure that the group exists on the CDP instance, otherwise create it and try again")
            ),
            None
          )
        )
      )
    }
  }

  test("return Left(PrincipalMappingSystemError()) if there was an error querying a group") {
    val group       = "group:anotherGroup"
    val error       = GetGroupErr(group, new CdpClientException("x"))
    (iamMock.getGroup _).expects("anotherGroup").returns(Left(error))
    val mappedGroup = mapper.map(Set(group))

    mappedGroup.foreach { case (_, mappedRef) =>
      assert(mappedRef.isLeft)
      mappedRef.left.foreach(t =>
        assert(
          t == PrincipalMappingSystemError(
            ErrorMoreInfo(List("Error while querying CDP for group 'anotherGroup'"), List.empty),
            error
          )
        )
      )
    }
  }

  test("return Left(PrincipalMappingErr()) if received ref doesn't exist on FreeIPA") {
    val principal   = "something"
    (iamMock.getUserByWorkloadUsername _).expects("something").returns(Right(None))
    (iamMock.getGroup _).expects("something").returns(Right(None))
    val mappedGroup = mapper.map(Set(principal))

    mappedGroup.foreach { case (_, mappedRef) =>
      assert(mappedRef.isLeft)
      mappedRef.left.foreach(t =>
        assert(
          t == PrincipalMappingError(
            ErrorMoreInfo(
              List(s"Received principal 'something' which doesn't exists as a group nor as an user on CDP"),
              List.empty
            ),
            None
          )
        )
      )
    }
  }

}
