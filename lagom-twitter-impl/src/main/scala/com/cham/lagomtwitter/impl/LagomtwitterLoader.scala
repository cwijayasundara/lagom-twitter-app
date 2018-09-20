package com.cham.lagomtwitter.impl

import com.lightbend.lagom.scaladsl.api.ServiceLocator
import com.lightbend.lagom.scaladsl.api.ServiceLocator.NoServiceLocator
import com.lightbend.lagom.scaladsl.persistence.cassandra.CassandraPersistenceComponents
import com.lightbend.lagom.scaladsl.server._
import com.lightbend.lagom.scaladsl.devmode.LagomDevModeComponents
import play.api.libs.ws.ahc.AhcWSComponents
import com.cham.lagomtwitter.api.LagomtwitterService
import com.lightbend.lagom.scaladsl.broker.kafka.LagomKafkaComponents
import com.softwaremill.macwire._

class LagomtwitterLoader extends LagomApplicationLoader {

  override def load(context: LagomApplicationContext): LagomApplication =
    new LagomtwitterApplication(context) {
      override def serviceLocator: ServiceLocator = NoServiceLocator
    }

  override def loadDevMode(context: LagomApplicationContext): LagomApplication =
    new LagomtwitterApplication(context) with LagomDevModeComponents

  override def describeService = Some(readDescriptor[LagomtwitterService])
}

abstract class LagomtwitterApplication(context: LagomApplicationContext)
  extends LagomApplication(context)
    with CassandraPersistenceComponents
    with LagomKafkaComponents
    with AhcWSComponents {

  // Bind the service that this server provides
  override lazy val lagomServer = serverFor[LagomtwitterService](wire[LagomtwitterServiceImpl])

  // Register the JSON serializer registry
  override lazy val jsonSerializerRegistry = LagomtwitterSerializerRegistry

  // Register the lagom-twitter persistent entity
  persistentEntityRegistry.register(wire[LagomtwitterEntity])
}
