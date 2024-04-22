package it.agilelab.provisioning.mesh.self.service.api.model.openmetadata

import io.circe.{ Decoder, Encoder, Json }

final case class Tag(
  tagFQN: String,
  description: Option[String],
  source: String,
  labelType: TagLabelType,
  state: TagState,
  href: Option[String]
)

sealed trait TagLabelType extends Product with Serializable
object TagLabelType {
  case object Manual     extends TagLabelType
  case object Propagated extends TagLabelType
  case object Automated  extends TagLabelType
  case object Derived    extends TagLabelType

  implicit val columnConstraintEncoder: Encoder[TagLabelType] = {
    case Manual     => Json.fromString("Manual")
    case Propagated => Json.fromString("Propagated")
    case Automated  => Json.fromString("Automated")
    case Derived    => Json.fromString("Derived")
  }

  implicit val columnConstraintDecoder: Decoder[TagLabelType] = Decoder[String].emap { constraint =>
    constraint.capitalize match {
      case "Manual"     => Right(Manual)
      case "Propagated" => Right(Propagated)
      case "Automated"  => Right(Automated)
      case "Derived"    => Right(Derived)
      case other        => Left(s"Invalid tag source: $other")
    }
  }
}

sealed trait TagState extends Product with Serializable
object TagState {
  case object Suggested extends TagState
  case object Confirmed extends TagState

  implicit val columnConstraintEncoder: Encoder[TagState] = {
    case Suggested => Json.fromString("Suggested")
    case Confirmed => Json.fromString("Confirmed")
  }

  implicit val columnConstraintDecoder: Decoder[TagState] = Decoder[String].emap { constraint =>
    constraint.capitalize match {
      case "Suggested" => Right(Suggested)
      case "Confirmed" => Right(Confirmed)
      case other       => Left(s"Invalid tag source: $other")
    }
  }
}
