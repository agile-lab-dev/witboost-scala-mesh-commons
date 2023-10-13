package it.agilelab.provisioning.commons.validator

final case class ValidationFail[A](entity: A, message: String)
