package it.agilelab.provisioning.aws.iam.gateway

import org.scalamock.scalatest.MockFactory
import org.scalatest.funsuite.AnyFunSuite
import software.amazon.awssdk.core.exception.SdkClientException
import software.amazon.awssdk.services.iam.IamClient
import software.amazon.awssdk.services.iam.model._

class DefaultIamGatewayTest extends AnyFunSuite with MockFactory with IamGatewayTestSupport {

  val iamClient: IamClient   = mock[IamClient]
  val iamGateway: IamGateway = new DefaultIamGateway(iamClient)

  Seq(
    ("rl1", "pn1", "pd1"),
    ("rl2", "pn2", "pd2"),
    ("rl3", "pn3", "pd3")
  ) foreach { case (roleName: String, policyName: String, policyDocument: String) =>
    test(s"putRolePolicy return Right() with $roleName, $policyName, $policyDocument") {
      (iamClient
        .putRolePolicy(_: PutRolePolicyRequest))
        .expects(
          PutRolePolicyRequest
            .builder()
            .roleName(roleName)
            .policyName(policyName)
            .policyDocument(policyDocument)
            .build()
        )
        .once()
        .returns(PutRolePolicyResponse.builder().build())

      assert(iamGateway.putRolePolicy(roleName, policyName, policyDocument) == Right())
    }

    test(s"putRolePolicy return Left(Error) with $roleName, $policyName, $policyDocument") {
      (iamClient
        .putRolePolicy(_: PutRolePolicyRequest))
        .expects(*)
        .once()
        .throws(SdkClientException.create("x"))

      val actual = iamGateway.putRolePolicy(roleName, policyName, policyDocument)
      assertPutRolePolicyErr(actual, roleName, policyName, policyDocument, "x")
    }

    test(s"deleteRolePolicy return Right() with $roleName, $policyName") {
      (iamClient
        .deleteRolePolicy(_: DeleteRolePolicyRequest))
        .expects(
          DeleteRolePolicyRequest
            .builder()
            .roleName(roleName)
            .policyName(policyName)
            .build()
        )
        .once()
        .returns(DeleteRolePolicyResponse.builder().build())

      assert(iamGateway.deleteRolePolicy(roleName, policyName) == Right())
    }

    test(s"deleteRolePolicy return Left(Error) with $roleName, $policyName") {
      (iamClient
        .deleteRolePolicy(_: DeleteRolePolicyRequest))
        .expects(*)
        .once()
        .throws(SdkClientException.create("x"))

      assertDeleteRolePolicyErr(iamGateway.deleteRolePolicy(roleName, policyName), roleName, policyName, "x")
    }

    test(s"safeDeleteRolePolicy return Right() with existing $roleName, $policyName") {
      inSequence(
        (iamClient
          .listRolePolicies(_: ListRolePoliciesRequest))
          .expects(ListRolePoliciesRequest.builder().roleName(roleName).build())
          .once()
          .returns(
            ListRolePoliciesResponse
              .builder()
              .marker("m1")
              .isTruncated(true)
              .policyNames(policyName)
              .build()
          ),
        (iamClient
          .deleteRolePolicy(_: DeleteRolePolicyRequest))
          .expects(
            DeleteRolePolicyRequest
              .builder()
              .roleName(roleName)
              .policyName(policyName)
              .build()
          )
          .once()
          .returns(DeleteRolePolicyResponse.builder().build())
      )
      assert(iamGateway.safeDeleteRolePolicy(roleName, policyName) == Right())
    }

    test(s"safeDeleteRolePolicy return Right() with not existing $roleName, $policyName") {
      inSequence(
        (iamClient
          .listRolePolicies(_: ListRolePoliciesRequest))
          .expects(ListRolePoliciesRequest.builder().roleName(roleName).build())
          .once()
          .returns(
            ListRolePoliciesResponse
              .builder()
              .marker("m1")
              .isTruncated(false)
              .policyNames("p")
              .build()
          )
      )
      assert(iamGateway.safeDeleteRolePolicy(roleName, policyName) == Right())
    }

    test(s"existsRolePolicy fullPagination return Right(true) with $roleName, $policyName") {
      inSequence(
        (iamClient
          .listRolePolicies(_: ListRolePoliciesRequest))
          .expects(ListRolePoliciesRequest.builder().roleName(roleName).build())
          .once()
          .returns(
            ListRolePoliciesResponse
              .builder()
              .marker("m1")
              .isTruncated(true)
              .policyNames("pnA", "pnB")
              .build()
          ),
        (iamClient
          .listRolePolicies(_: ListRolePoliciesRequest))
          .expects(
            ListRolePoliciesRequest.builder().roleName(roleName).marker("m1").build()
          )
          .once()
          .returns(
            ListRolePoliciesResponse
              .builder()
              .marker("m2")
              .isTruncated(true)
              .policyNames("pnC", "pnD")
              .build()
          ),
        (iamClient
          .listRolePolicies(_: ListRolePoliciesRequest))
          .expects(
            ListRolePoliciesRequest.builder().roleName(roleName).marker("m2").build()
          )
          .once()
          .returns(
            ListRolePoliciesResponse.builder().policyNames("pn1", "pn2", "pn3").build()
          )
      )

      val actual   = iamGateway.existsRolePolicy(roleName, policyName)
      val expected = Right(true)

      assert(actual == expected)
    }

    test(s"existsRolePolicy fullPagination return Right(false) with $roleName, $policyName") {
      inSequence(
        (iamClient
          .listRolePolicies(_: ListRolePoliciesRequest))
          .expects(ListRolePoliciesRequest.builder().roleName(roleName).build())
          .once()
          .returns(
            ListRolePoliciesResponse
              .builder()
              .marker("m1")
              .isTruncated(true)
              .policyNames("pnA", "pnB")
              .build()
          ),
        (iamClient
          .listRolePolicies(_: ListRolePoliciesRequest))
          .expects(
            ListRolePoliciesRequest
              .builder()
              .roleName(roleName)
              .marker("m1")
              .build()
          )
          .once()
          .returns(
            ListRolePoliciesResponse
              .builder()
              .marker("m2")
              .isTruncated(true)
              .policyNames("pnC", "pnD")
              .build()
          ),
        (iamClient
          .listRolePolicies(_: ListRolePoliciesRequest))
          .expects(
            ListRolePoliciesRequest
              .builder()
              .roleName(roleName)
              .marker("m2")
              .build()
          )
          .once()
          .returns(
            ListRolePoliciesResponse
              .builder()
              .policyNames("pnX", "pnY", "pnZ")
              .build()
          )
      )
      val actual = iamGateway.existsRolePolicy(roleName, policyName)
      assert(actual == Right(false))
    }

    test(s"existsRolePolicy noPagination return Right(true) with $roleName, $policyName") {
      (iamClient
        .listRolePolicies(_: ListRolePoliciesRequest))
        .expects(ListRolePoliciesRequest.builder().roleName(roleName).build())
        .once()
        .returns(
          ListRolePoliciesResponse
            .builder()
            .marker("m1")
            .isTruncated(true)
            .policyNames("pn1", "pn2", "pn3")
            .build()
        )

      val actual = iamGateway.existsRolePolicy(roleName, policyName)
      assert(actual == Right(true))
    }

    test(s"existsRolePolicy return Left(Error) with $roleName, $policyName") {
      (iamClient
        .listRolePolicies(_: ListRolePoliciesRequest))
        .expects(ListRolePoliciesRequest.builder().roleName(roleName).build())
        .once()
        .throws(SdkClientException.create("x"))

      assertExistsRolePolicyErr(iamGateway.existsRolePolicy(roleName, policyName), roleName, policyName, "x")
    }

  }

}
