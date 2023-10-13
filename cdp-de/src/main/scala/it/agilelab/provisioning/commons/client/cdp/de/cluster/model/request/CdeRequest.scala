package it.agilelab.provisioning.commons.client.cdp.de.cluster.model.request

import com.cloudera.cdp.de.model.{ ServiceDescription, VcDescription }

/** CdeRequest model
  *
  * Used by cde client to process request on specific virtual cluster within specific service
  * @param service: a [[ServiceDescription]] instance that describe the CDE Service
  * @param vc: a [[VcDescription]] instance that describe the CDE Virtual cluster within the CDE Service provided
  * @param spec: Request details
  * @tparam A A request model
  */
final case class CdeRequest[A](
  service: ServiceDescription,
  vc: VcDescription,
  spec: A
)
