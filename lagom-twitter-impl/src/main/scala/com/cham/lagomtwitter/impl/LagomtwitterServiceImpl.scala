package com.cham.lagomtwitter.impl

import com.cham.lagomtwitter.api
import com.cham.lagomtwitter.api.{LagomtwitterService}
import com.lightbend.lagom.scaladsl.api.ServiceCall
import com.lightbend.lagom.scaladsl.api.broker.Topic
import com.lightbend.lagom.scaladsl.broker.TopicProducer
import com.lightbend.lagom.scaladsl.persistence.{EventStreamElement, PersistentEntityRegistry}

/**
  * Implementation of the LagomtwitterService.
  */
class LagomtwitterServiceImpl(persistentEntityRegistry: PersistentEntityRegistry) extends LagomtwitterService {

  override def hello(id: String) = ServiceCall { _ =>
    // Look up the lagom-twitter entity for the given ID.
    val ref = persistentEntityRegistry.refFor[LagomtwitterEntity](id)
    // Ask the entity the Hello command.
    ref.ask(Hello(id))
  }

  override def useGreeting(id: String) = ServiceCall { request =>
    // Look up the lagom-twitter entity for the given ID.
    printf("Inside LagomtwitterServiceImpl.useGreeting");
    val ref = persistentEntityRegistry.refFor[LagomtwitterEntity](id)
    // Tell the entity to use the greeting message specified.
    ref.ask(UseGreetingMessage(request.message))
  }


  override def greetingsTopic(): Topic[api.GreetingMessageChanged] =
    TopicProducer.singleStreamWithOffset {
      fromOffset =>
        persistentEntityRegistry.eventStream(LagomtwitterEvent.Tag, fromOffset)
          .map(ev => (convertEvent(ev), ev.offset))
    }

  private def convertEvent(helloEvent: EventStreamElement[LagomtwitterEvent]): api.GreetingMessageChanged = {
    helloEvent.event match {
      case GreetingMessageChanged(msg) => api.GreetingMessageChanged(helloEvent.entityId, msg)
    }
  }
}
