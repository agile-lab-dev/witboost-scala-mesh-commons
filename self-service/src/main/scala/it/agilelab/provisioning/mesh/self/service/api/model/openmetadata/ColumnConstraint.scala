package it.agilelab.provisioning.mesh.self.service.api.model.openmetadata

import io.circe.{ Decoder, Encoder, Json }

/** ColumnConstraint sealed trait defined according to openmetadata standard
  * from https://docs.open-metadata.org/metadata-standard/schemas/entities/table#constraint
  */
sealed trait ColumnConstraint extends Product with Serializable

object ColumnConstraint {
  case object NULL        extends ColumnConstraint
  case object NOT_NULL    extends ColumnConstraint
  case object UNIQUE      extends ColumnConstraint
  case object PRIMARY_KEY extends ColumnConstraint

  implicit val columnConstraintEncoder: Encoder[ColumnConstraint] = {
    case NULL        => Json.fromString("NULL")
    case NOT_NULL    => Json.fromString("NOT_NULL")
    case UNIQUE      => Json.fromString("UNIQUE")
    case PRIMARY_KEY => Json.fromString("PRIMARY_KEY")
  }

  implicit val columnConstraintDecoder: Decoder[ColumnConstraint] = Decoder[String].emap {
    case "NULL" | "null"               => Right(NULL)
    case "NOT_NULL" | "not_null"       => Right(NOT_NULL)
    case "UNIQUE" | "unique"           => Right(UNIQUE)
    case "PRIMARY_KEY" | "primary_key" => Right(PRIMARY_KEY)
    case other                         => Left(s"Invalid column constraint: $other")
  }
}
