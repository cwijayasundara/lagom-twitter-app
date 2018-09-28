package com.cham.lagomtwitter.impl

import akka.actor.ActorSystem
import akka.testkit.TestKit
import com.lightbend.lagom.scaladsl.testkit.PersistentEntityTestDriver
import com.lightbend.lagom.scaladsl.playjson.JsonSerializerRegistry
import org.scalatest.{BeforeAndAfterAll, Matchers, WordSpec}

class LagomtwitterEntitySpec extends WordSpec with Matchers with BeforeAndAfterAll {

  private val system = ActorSystem("LagomtwitterEntitySpec",
    JsonSerializerRegistry.actorSystemSetupFor(LagomtwitterSerializerRegistry))

  override protected def afterAll(): Unit = {
    TestKit.shutdownActorSystem(system)
  }

  private def withTestDriver(block: PersistentEntityTestDriver[LagomtwitterCommand[_], LagomtwitterEvent, LagomtwitterState] => Unit): Unit = {
    val driver = new PersistentEntityTestDriver(system, new LagomtwitterEntity, "lagom-twitter-1")
    block(driver)
    driver.getAllIssues should have size 0
  }

  "lagom-twitter entity" should {

    "say hello by default" in withTestDriver { driver =>
      val outcome = driver.run(Tweet("Alice"))
      outcome.replies should contain only "Hello, Alice!"
    }

    "allow updating the greeting message" in withTestDriver { driver =>
      val outcome1 = driver.run(UserTweetMessage("Hi"))
      outcome1.events should contain only UserTweetMessageChanged("Hi")
      val outcome2 = driver.run(Tweet("Alice"))
      outcome2.replies should contain only "Hi, Alice!"
    }

  }
}
