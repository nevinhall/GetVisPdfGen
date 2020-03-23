package org.example.getvis.impl

import com.lightbend.lagom.scaladsl.server.LocalServiceLocator
import com.lightbend.lagom.scaladsl.testkit.ServiceTest
import org.scalatest.{AsyncWordSpec, BeforeAndAfterAll, Matchers}
import org.example.getvis.api._

class GetvisServiceSpec extends AsyncWordSpec with Matchers with BeforeAndAfterAll {

  private val server = ServiceTest.startServer(
    ServiceTest.defaultSetup
      .withCassandra()
  ) { ctx =>
    new GetvisApplication(ctx) with LocalServiceLocator
  }

  val client: GetvisService = server.serviceClient.implement[GetvisService]

  override protected def afterAll(): Unit = server.stop()

  "GetVis service" should {

    "say hello" in {
      client.hello("Alice").invoke().map { answer =>
        answer should ===("Hello, Alice!")
      }
    }


  }
}
