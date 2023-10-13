package it.agilelab.provisioning.mesh.self.service.api.model

import cats.syntax.functor._
import it.agilelab.provisioning.mesh.self.service.api.model.openmetadata.Column
import io.circe.generic.auto._
import io.circe.syntax._
import io.circe.{ Decoder, Encoder }

/** Component sealed trait
  * A hierarchical representation of all possible component.
  * It is mainly based on the open api specification.
  * @tparam SPECIFIC: Specific type parameter
  */
sealed trait Component[SPECIFIC] extends Product with Serializable

object Component {

  final case class DataContract(schema: Seq[Column])

  final case class OutputPort[SPECIFIC](
    id: String,
    name: String,
    description: String,
    version: String,
    dataContract: DataContract,
    specific: SPECIFIC
  ) extends Component[SPECIFIC]

  final case class StorageArea[SPECIFIC](
    id: String,
    name: String,
    description: String,
    owners: Seq[String],
    specific: SPECIFIC
  ) extends Component[SPECIFIC]

  final case class Workload[SPECIFIC](
    id: String,
    name: String,
    description: String,
    version: String,
    specific: SPECIFIC
  ) extends Component[SPECIFIC]

  /** Implicit circe [[Encoder]] implementation for Component
    * @param ev: implicit [[Encoder]]
    * @tparam SPECIFIC: Specific type parameter
    * @return [[Encoder]]
    */
  implicit def encodeComponent[SPECIFIC](implicit ev: Encoder[SPECIFIC]): Encoder[Component[SPECIFIC]] =
    Encoder.instance {
      case w: Workload[SPECIFIC]    => w.asJson
      case o: OutputPort[SPECIFIC]  => o.asJson
      case s: StorageArea[SPECIFIC] => s.asJson
    }

  /** Implicit circe [[Decoder]] implementation for Component
    * @param dv: implicit [[Decoder]]
    * @tparam SPECIFIC: Specific type parameter
    * @return [[Decoder]]
    */
  implicit def decodeComponent[SPECIFIC](implicit dv: Decoder[SPECIFIC]): Decoder[Component[SPECIFIC]] =
    List[Decoder[Component[SPECIFIC]]](
      Decoder[OutputPort[SPECIFIC]].widen,
      Decoder[Workload[SPECIFIC]].widen,
      Decoder[StorageArea[SPECIFIC]].widen
    ).reduceLeft(_ or _)

}
