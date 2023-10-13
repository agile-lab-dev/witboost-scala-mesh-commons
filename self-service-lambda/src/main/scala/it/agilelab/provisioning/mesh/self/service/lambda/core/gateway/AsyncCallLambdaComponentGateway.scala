package it.agilelab.provisioning.mesh.self.service.lambda.core.gateway

import cats.implicits.{ showInterpolator, toBifunctorOps }
import it.agilelab.provisioning.aws.lambda.gateway.LambdaGateway
import it.agilelab.provisioning.aws.lambda.gateway.LambdaGatewayError._
import it.agilelab.provisioning.mesh.self.service.core.gateway.{ ComponentGateway, ComponentGatewayError }
import it.agilelab.provisioning.mesh.self.service.core.model.ProvisionCommand
import it.agilelab.provisioning.mesh.self.service.lambda.core.model.ComponentOperation._
import it.agilelab.provisioning.mesh.self.service.lambda.core.model.ComponentServiceCommand
import io.circe.Encoder

/** A ComponentGateway implementation that is used from the LambdaApi.
  * It basically invoke another lambda function with the provided [[ProvisionCommand]].
  * @param lambdaGateway: A [[LambdaGateway]] instance
  * @param provisionLambdaFunction: A lambda function to invoke
  * @param encoder: implicit [[Encoder]] of [[ProvisionCommand]]
  * @tparam DP_SPEC: DataProduct type parameter
  * @tparam COMPONENT_SPEC: Component type paramenter
  */
class AsyncCallLambdaComponentGateway[DP_SPEC, COMPONENT_SPEC](
  lambdaGateway: LambdaGateway,
  provisionLambdaFunction: String,
  unprovisionLambdaFunction: String
)(implicit
  encoder: Encoder[ComponentServiceCommand[DP_SPEC, COMPONENT_SPEC]]
) extends ComponentGateway[DP_SPEC, COMPONENT_SPEC, Unit] {

  /** Invoke the lambda function configured as async lambda function provisioning service.
    * @param provisionCommand: A ProvisionCommand that should be send to the invoked lambda
    * @return Right(Unit)
    *          Left(ComponentGatewayError)
    */
  override def create(
    provisionCommand: ProvisionCommand[DP_SPEC, COMPONENT_SPEC]
  ): Either[ComponentGatewayError, Unit] =
    lambdaGateway
      .asyncCall(provisionLambdaFunction, ComponentServiceCommand(Create, provisionCommand))
      .leftMap {
        case LambdaGatewayInitErr(_)  =>
          ComponentGatewayError("Unable to initialize LambdaGateway")
        case PayloadSerErr(_)         =>
          ComponentGatewayError("Unable to serialize the provided provision command")
        case InvokeErr(l, p, _)       =>
          ComponentGatewayError(show"Unable to run lambda service $l with payload $p. An exception was raised")
        case InvokeResultErr(l, p, s) =>
          ComponentGatewayError(show"Unable to run lambda service $l with payload $p. Unexpected status: $s")
      }

  /** Invoke the lambda function configured as async lambda function unprovisioning service.
    * @param provisionCommand: A ProvisionCommand that should be send to the invoked lambda
    * @return Right(Unit)
    *          Left(ComponentGatewayError)
    */
  override def destroy(
    provisionCommand: ProvisionCommand[DP_SPEC, COMPONENT_SPEC]
  ): Either[ComponentGatewayError, Unit] =
    lambdaGateway
      .asyncCall(unprovisionLambdaFunction, ComponentServiceCommand(Destroy, provisionCommand))
      .leftMap {
        case LambdaGatewayInitErr(_)  =>
          ComponentGatewayError("Unable to initialize LambdaGateway")
        case PayloadSerErr(_)         =>
          ComponentGatewayError("Unable to serialize the provided provision command")
        case InvokeErr(l, p, _)       =>
          ComponentGatewayError(show"Unable to run lambda service $l with payload $p. An exception was raised")
        case InvokeResultErr(l, p, s) =>
          ComponentGatewayError(show"Unable to run lambda service $l with payload $p. Unexpected status: $s")
      }
}
