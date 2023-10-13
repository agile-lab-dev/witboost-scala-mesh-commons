package it.agilelab.provisioning.commons.http

import cats.Show

sealed trait Auth

object Auth {

  /** NoAuth mode
    */
  final case class NoAuth() extends Auth

  /** BasicCredential Auth mode
    */
  final case class BasicCredential(
    username: String,
    password: String
  ) extends Auth

  /** BearerToken Auth model
    */
  final case class BearerToken(
    access_token: String,
    token_id: String,
    managed: String,
    endpoint_public_cert: String,
    token_type: String,
    expires_in: Long,
    passcode: String
  ) extends Auth

  implicit val showAuth: Show[Auth] = Show.show {
    case NoAuth()                         => "NoAuth()"
    case BasicCredential(_, _)            => "BasicCredential(*,*)"
    case BearerToken(_, _, _, _, _, _, _) => "BearerToken(*,*,*,*,*,*,*)"
  }
}
