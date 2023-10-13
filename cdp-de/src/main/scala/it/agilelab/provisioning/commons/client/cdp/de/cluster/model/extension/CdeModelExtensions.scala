package it.agilelab.provisioning.commons.client.cdp.de.cluster.model.extension

import com.cloudera.cdp.de.model.ServiceDescription

object CdeModelExtensions {

  implicit class ServiceDescriptionOps(serviceDescription: ServiceDescription) {

    def getServiceUrl: String =
      s"https://${serviceDescription.getClusterFqdn}"

  }
}
