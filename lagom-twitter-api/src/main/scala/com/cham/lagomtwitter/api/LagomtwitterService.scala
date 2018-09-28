package com.cham.lagomtwitter.api

import akka.{Done, NotUsed}
import com.lightbend.lagom.scaladsl.api.broker.Topic
import com.lightbend.lagom.scaladsl.api.broker.kafka.{KafkaProperties, PartitionKeyStrategy}
import com.lightbend.lagom.scaladsl.api.{Service, ServiceCall}
import play.api.libs.json.{Format, Json}

object LagomtwitterService  {
  val TOPIC_NAME = "tweets"
}

/**
  * The lagom-twitter service interface.
  * <p>
  * This describes everything that Lagom needs to know about how to serve and
  * consume the LagomtwitterService.
  */
trait LagomtwitterService extends Service {

  /**
    * Example: curl http://localhost:9000/api/tweet/Chaminda
    */
  def getTweet(id: String): ServiceCall[NotUsed, String]

  /**
    * Example: curl -H "Content-Type: application/json" -X POST -d '{"message":
    * "Hi"}' http://localhost:9000/api/tweet/Chaminda
    */
  def createTweet(id: String): ServiceCall[TweetMessage, Done]


  /**
    * This gets published to Kafka.
    */
  def tweetsTopic(): Topic[TweetMessageChanged]

  override final def descriptor = {
    import Service._
    // @formatter:off
    named("lagom-twitter")
      .withCalls(
        pathCall("/api/tweet/:id", getTweet _),
        pathCall("/api/tweet/:id", createTweet _)
      )
      .withTopics(
        topic(LagomtwitterService.TOPIC_NAME, tweetsTopic)
          // Kafka partitions messages, messages within the same partition will
          // be delivered in order, to ensure that all messages for the same user
          // go to the same partition (and hence are delivered in order with respect
          // to that user), we configure a partition key strategy that extracts the
          // name as the partition key.
          .addProperty(
            KafkaProperties.partitionKeyStrategy,
            PartitionKeyStrategy[TweetMessageChanged](_.name)
          )
      )
      .withAutoAcl(true)
    // @formatter:on
  }
}

/**
  * The greeting message class.
  */
case class TweetMessage(message: String)

object TweetMessage {
  /**
    * Format for converting greeting messages to and from JSON.
    *
    * This will be picked up by a Lagom implicit conversion from Play's JSON format to Lagom's message serializer.
    */
  implicit val format: Format[TweetMessage] = Json.format[TweetMessage]
}



/**
  * The greeting message class used by the topic stream.
  * Different than [[TweetMessage]], this message includes the name (id).
  */
case class TweetMessageChanged(name: String, message: String)

object TweetMessageChanged {
  /**
    * Format for converting greeting messages to and from JSON.
    *
    * This will be picked up by a Lagom implicit conversion from Play's JSON format to Lagom's message serializer.
    */
  implicit val format: Format[TweetMessageChanged] = Json.format[TweetMessageChanged]
}
