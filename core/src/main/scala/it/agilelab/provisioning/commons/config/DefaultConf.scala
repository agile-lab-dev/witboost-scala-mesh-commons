package it.agilelab.provisioning.commons.config

import com.typesafe.config.Config
import it.agilelab.provisioning.commons.config.ConfError.ConfKeyNotFoundErr

/** Default Conf Instance that use typesafe Config as underlying config interface
  * @param config: [[Config]]
  */
class DefaultConf(config: Config) extends Conf {

  override def get(key: String): Either[ConfError, String] =
    try Right(config.getString(key))
    catch { case _: Exception => Left(ConfKeyNotFoundErr(key)) }
}
