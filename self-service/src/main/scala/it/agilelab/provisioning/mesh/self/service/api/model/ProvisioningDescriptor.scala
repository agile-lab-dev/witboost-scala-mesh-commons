package it.agilelab.provisioning.mesh.self.service.api.model

import cats.Show
import io.circe.Decoder
import cats.implicits._
import it.agilelab.provisioning.commons.support.ParserError
import it.agilelab.provisioning.commons.support.ParserError._

/** Model the class which is sent by the coordinator to specific provisioners
  * inside the descriptor field of a [[ProvisioningRequest]]
  * @param dataProduct an instance of [[DataProduct]]
  * @param componentIdToProvision an optional string which identifies the component to be provisioned from the
  *                               list of components inside the data product descriptor.
  *                               If None, the request is a data product provisioning request
  * @tparam DP_SPEC [[DataProduct]] type parameter
  */
final case class ProvisioningDescriptor[DP_SPEC](
  dataProduct: DataProduct[DP_SPEC],
  componentIdToProvision: Option[String]
)

object ProvisioningDescriptor {

  implicit def showProvisioningDescriptor[A]: Show[ProvisioningDescriptor[A]] =
    Show.fromToString[ProvisioningDescriptor[A]]

  implicit class ProvisioningDescriptorOps[DP_SPEC](instance: ProvisioningDescriptor[DP_SPEC]) {

    def toProvisionRequest[COMPONENT_SPEC]()(implicit
      decoder: Decoder[Component[COMPONENT_SPEC]]
    ): Either[ParserError, ProvisionRequest[DP_SPEC, COMPONENT_SPEC]] =
      instance.componentIdToProvision match {
        case Some(value) =>
          instance.dataProduct.components
            .find(j => j.hcursor.get[String]("id") == Right(value))
            .map(c => c.as[Component[COMPONENT_SPEC]].leftMap(e => DecodeErr(e)))
            .sequence
            .map(cmp => ProvisionRequest[DP_SPEC, COMPONENT_SPEC](dataProduct = instance.dataProduct, component = cmp))
        case None        =>
          Right(ProvisionRequest[DP_SPEC, COMPONENT_SPEC](dataProduct = instance.dataProduct, component = None))
      }

  }

}
