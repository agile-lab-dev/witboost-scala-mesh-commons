package it.agilelab.provisioning.mesh.self.service.lambda.core.model

import io.circe.{ Decoder, Encoder, Json }

/** Trait which models a Component Operation
  */
sealed trait ComponentOperation extends Product with Serializable
object ComponentOperation {
  case object Create  extends ComponentOperation
  case object Destroy extends ComponentOperation

  implicit val componentOperationEncoder: Encoder[ComponentOperation] = {
    case Create  => Json.fromString("CREATE")
    case Destroy => Json.fromString("DESTROY")
  }

  implicit val componentOperationDecoder: Decoder[ComponentOperation] = Decoder[String].emap {
    case "CREATE" | "Create" | "create"    => Right(Create)
    case "DESTROY" | "Destroy" | "destroy" => Right(Destroy)
    case other                             => Left(s"Invalid component operation: $other")
  }
}
