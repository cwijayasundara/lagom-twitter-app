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

  override def getTweet(id: String) = ServiceCall { _ =>
    // Look up the lagom-twitter entity for the given ID.
    val ref = persistentEntityRegistry.refFor[LagomtwitterEntity](id)
    // Ask the entity the Hello command.
    ref.ask(Tweet(id))
  }

  override def createTweet(id: String) = ServiceCall { request =>
    // Look up the lagom-twitter entity for the given ID.
    printf("Inside LagomtwitterServiceImpl.createTweet");
    val ref = persistentEntityRegistry.refFor[LagomtwitterEntity](id)
    // Tell the entity to use the greeting message specified.
    ref.ask(UserTweetMessage(request.message))
  }


  override def tweetsTopic(): Topic[api.TweetMessageChanged] =
    TopicProducer.singleStreamWithOffset {
      fromOffset =>
        persistentEntityRegistry.eventStream(LagomtwitterEvent.Tag, fromOffset)
          .map(ev => (convertEvent(ev), ev.offset))
    }

  private def convertEvent(helloEvent: EventStreamElement[LagomtwitterEvent]): api.TweetMessageChanged = {
    helloEvent.event match {
      case UserTweetMessageChanged(msg) => api.TweetMessageChanged(helloEvent.entityId, msg)
    }
  }
}
