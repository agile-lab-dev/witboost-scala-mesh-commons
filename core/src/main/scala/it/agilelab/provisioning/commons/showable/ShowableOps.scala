package it.agilelab.provisioning.commons.showable

import cats.Show

import java.io.{ PrintWriter, StringWriter }

object ShowableOps {

  implicit def showThrowableError: Show[Throwable] = Show.show { t: Throwable =>
    val stringWriter = new StringWriter()
    t.printStackTrace(new PrintWriter(stringWriter))
    stringWriter.toString.trim
  }

}
