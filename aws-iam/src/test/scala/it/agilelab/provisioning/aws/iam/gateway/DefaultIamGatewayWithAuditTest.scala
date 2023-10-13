package it.agilelab.provisioning.aws.iam.gateway

import it.agilelab.provisioning.aws.iam.gateway.IamGatewayError.{
  DeleteRolePolicyErr,
  ExistsRolePolicyErr,
  PutRolePolicyErr
}
import it.agilelab.provisioning.commons.audit.Audit
import org.scalamock.scalatest.MockFactory
import org.scalatest.funsuite.AnyFunSuite
import software.amazon.awssdk.core.exception.SdkClientException

class DefaultIamGatewayWithAuditTest extends AnyFunSuite with MockFactory with IamGatewayTestSupport {

  val audit: Audit                  = mock[Audit]
  val defaultIamGateway: IamGateway = mock[IamGateway]
  val iamGateway: IamGateway        = new DefaultIamGatewayWithAudit(defaultIamGateway, audit)

  test(s"putRolePolicy call audit on success") {
    inSequence(
      (defaultIamGateway.putRolePolicy _)
        .expects("r", "pn", "pd")
        .once()
        .returns(Right()),
      (audit.info _)
        .expects(s"PutRolePolicy(role=r,policy=pn,policyDocument=pd) completed successfully")
        .once()
    )

    assert(iamGateway.putRolePolicy("r", "pn", "pd") == Right())
  }

  test(s"putRolePolicy call audit on failure") {
    inSequence(
      (defaultIamGateway.putRolePolicy _)
        .expects("r", "pn", "pd")
        .once()
        .returns(Left(PutRolePolicyErr("r", "pn", "pd", SdkClientException.create("x")))),
      (audit.error _)
        .expects(where { s: String =>
          s.startsWith(
            "PutRolePolicy(role=r,policy=pn,policyDocument=pd) failed. Details: PutRolePolicyErr(r,pn,pd,software.amazon.awssdk.core.exception.SdkClientException: x"
          )
        })
        .once()
    )

    val actual = iamGateway.putRolePolicy("r", "pn", "pd")
    assertPutRolePolicyErr(actual, "r", "pn", "pd", "x")
  }

  test(s"deleteRolePolicy call audit on success") {
    inSequence(
      (defaultIamGateway.deleteRolePolicy _)
        .expects(*, *)
        .once()
        .returns(Right()),
      (audit.info _)
        .expects(s"DeleteRolePolicy(role=r,policy=pn) completed successfully")
        .once()
    )

    assert(iamGateway.deleteRolePolicy("r", "pn") == Right())
  }

  test(s"deleteRolePolicy call audit on failure") {
    inSequence(
      (defaultIamGateway.deleteRolePolicy _)
        .expects(*, *)
        .once()
        .returns(Left(DeleteRolePolicyErr("r", "pn", SdkClientException.create("x")))),
      (audit.error _)
        .expects(where { s: String =>
          s.startsWith(
            s"DeleteRolePolicy(role=r,policy=pn) failed. Details: DeleteRolePolicyErr(r,pn,software.amazon.awssdk.core.exception.SdkClientException: x"
          )
        })
        .once()
    )

    assertDeleteRolePolicyErr(iamGateway.deleteRolePolicy("r", "pn"), "r", "pn", "x")
  }

  test(s"safeDeleteRolePolicy call audit on success") {
    inSequence(
      (defaultIamGateway.safeDeleteRolePolicy _)
        .expects(*, *)
        .once()
        .returns(Right()),
      (audit.info _)
        .expects(s"SafeDeleteRolePolicy(role=r,policy=pn) completed successfully")
        .once()
    )

    assert(iamGateway.safeDeleteRolePolicy("r", "pn") == Right())
  }

  test(s"safeDeleteRolePolicy call audit on failure") {
    inSequence(
      (defaultIamGateway.safeDeleteRolePolicy _)
        .expects(*, *)
        .once()
        .returns(Left(ExistsRolePolicyErr("r", "pn", SdkClientException.create("x")))),
      (audit.error _)
        .expects(where { s: String =>
          s.startsWith(
            "SafeDeleteRolePolicy(role=r,policy=pn) failed. Details: ExistsRolePolicyErr(r,pn,software.amazon.awssdk.core.exception.SdkClientException: x"
          )
        })
        .once()
    )
    assertExistsRolePolicyErr(iamGateway.safeDeleteRolePolicy("r", "pn"), "r", "pn", "x")
  }

  test(s"existsRolePolicy call audit on success") {
    inSequence(
      (defaultIamGateway.existsRolePolicy _)
        .expects(*, *)
        .once()
        .returns(Right(true)),
      (audit.info _)
        .expects(s"ExistsRolePolicy(role=r,policy=pn) completed successfully")
        .once()
    )

    assert(iamGateway.existsRolePolicy("r", "pn") == Right(true))
  }

  test(s"existsRolePolicy call audit on failure") {
    inSequence(
      (defaultIamGateway.existsRolePolicy _)
        .expects(*, *)
        .once()
        .returns(Left(ExistsRolePolicyErr("r", "pn", SdkClientException.create("x")))),
      (audit.error _)
        .expects(where { s: String =>
          s.startsWith(
            "ExistsRolePolicy(role=r,policy=pn) failed. Details: ExistsRolePolicyErr(r,pn,software.amazon.awssdk.core.exception.SdkClientException: x"
          )
        })
        .once()
    )
    assertExistsRolePolicyErr(iamGateway.existsRolePolicy("r", "pn"), "r", "pn", "x")
  }
}
