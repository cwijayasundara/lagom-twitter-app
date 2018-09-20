package com.cham.lagomtwitterstream.impl

import com.lightbend.lagom.scaladsl.api.ServiceLocator.NoServiceLocator
import com.lightbend.lagom.scaladsl.server._
import com.lightbend.lagom.scaladsl.devmode.LagomDevModeComponents
import play.api.libs.ws.ahc.AhcWSComponents
import com.cham.lagomtwitterstream.api.LagomtwitterStreamService
import com.cham.lagomtwitter.api.LagomtwitterService
import com.softwaremill.macwire._

class LagomtwitterStreamLoader extends LagomApplicationLoader {

  override def load(context: LagomApplicationContext): LagomApplication =
    new LagomtwitterStreamApplication(context) {
      override def serviceLocator = NoServiceLocator
    }

  override def loadDevMode(context: LagomApplicationContext): LagomApplication =
    new LagomtwitterStreamApplication(context) with LagomDevModeComponents

  override def describeService = Some(readDescriptor[LagomtwitterStreamService])
}

abstract class LagomtwitterStreamApplication(context: LagomApplicationContext)
  extends LagomApplication(context)
    with AhcWSComponents {

  // Bind the service that this server provides
  override lazy val lagomServer = serverFor[LagomtwitterStreamService](wire[LagomtwitterStreamServiceImpl])

  // Bind the LagomtwitterService client
  lazy val lagomtwitterService = serviceClient.implement[LagomtwitterService]
}
