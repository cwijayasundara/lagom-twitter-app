package com.cham.lagomtwitterstream.impl

import com.lightbend.lagom.scaladsl.api.ServiceCall
import com.cham.lagomtwitterstream.api.LagomtwitterStreamService
import com.cham.lagomtwitter.api.LagomtwitterService

import scala.concurrent.Future

/**
  * Implementation of the LagomtwitterStreamService.
  */
class LagomtwitterStreamServiceImpl(lagomtwitterService: LagomtwitterService) extends LagomtwitterStreamService {
  def stream = ServiceCall { hellos =>
    Future.successful(hellos.mapAsync(8)(lagomtwitterService.getTweet(_).invoke()))
  }
}
