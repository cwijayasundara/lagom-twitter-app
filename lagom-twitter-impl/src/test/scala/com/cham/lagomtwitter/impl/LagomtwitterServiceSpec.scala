package com.cham.lagomtwitter.impl

import com.lightbend.lagom.scaladsl.server.LocalServiceLocator
import com.lightbend.lagom.scaladsl.testkit.ServiceTest
import org.scalatest.{AsyncWordSpec, BeforeAndAfterAll, Matchers}
import com.cham.lagomtwitter.api._

class LagomtwitterServiceSpec extends AsyncWordSpec with Matchers with BeforeAndAfterAll {

  private val server = ServiceTest.startServer(
    ServiceTest.defaultSetup
      .withCassandra()
  ) { ctx =>
    new LagomtwitterApplication(ctx) with LocalServiceLocator
  }

  val client = server.serviceClient.implement[LagomtwitterService]

  override protected def afterAll() = server.stop()

  "lagom-twitter service" should {

    "say hello" in {
      client.getTweet("Alice").invoke().map { answer =>
        answer should ===("Hello, Alice!")
      }
    }

    "allow responding with a custom message" in {
      for {
        _ <- client.createTweet("Bob").invoke(TweetMessage("Hi"))
        answer <- client.getTweet("Bob").invoke()
      } yield {
        answer should ===("Hi, Bob!")
      }
    }
  }
}
