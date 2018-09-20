package com.cham.lagomtwitterstream.api

import akka.NotUsed
import akka.stream.scaladsl.Source
import com.lightbend.lagom.scaladsl.api.{Service, ServiceCall}

/**
  * The lagom-twitter stream interface.
  *
  * This describes everything that Lagom needs to know about how to serve and
  * consume the LagomtwitterStream service.
  */
trait LagomtwitterStreamService extends Service {

  def stream: ServiceCall[Source[String, NotUsed], Source[String, NotUsed]]

  override final def descriptor = {
    import Service._

    named("lagom-twitter-stream")
      .withCalls(
        namedCall("stream", stream)
      ).withAutoAcl(true)
  }
}

