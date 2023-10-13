package it.agilelab.provisioning.mesh.self.service.api.model

import cats.Show

/** ProvisionRequest
  * Define a ProvisionRequest
  *
  * @param dataProduct: [[DataProduct]] entity
  * @param component: [[Component]] entity
  * @tparam DP_SPEC: DataProduct specific type param
  * @tparam COMPONENT_SPEC: Component specific type param
  */
final case class ProvisionRequest[DP_SPEC, COMPONENT_SPEC](
  dataProduct: DataProduct[DP_SPEC],
  component: Option[Component[COMPONENT_SPEC]]
)

object ProvisionRequest {
  implicit def showProvisionRequest[A, B]: Show[ProvisionRequest[A, B]] = Show.fromToString[ProvisionRequest[A, B]]
}
