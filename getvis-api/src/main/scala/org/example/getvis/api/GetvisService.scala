package org.example.getvis.api

import akka.NotUsed
import akka.util.ByteString
import com.lightbend.lagom.scaladsl.api.deser.MessageSerializer
import com.lightbend.lagom.scaladsl.api.transport.Method
import com.lightbend.lagom.scaladsl.api.{Descriptor, Service, ServiceAcl, ServiceCall}

object GetvisService  {
  val TOPIC_NAME = "greetings"
}

trait GetvisService extends Service {





  override final def descriptor: Descriptor = {
    import Service._
    // @formatter:off
    named("getvis")
      .withCalls(
        pathCall("/report/:id", generatePdf _)(
          requestSerializer = implicitly[MessageSerializer[NotUsed, ByteString]],
          responseSerializer = new ByteStringMessageSerializer)
      )

      .withAutoAcl(true)
      .withAcls(
        ServiceAcl.forMethodAndPathRegex(Method.OPTIONS, "/.*")
      )
    // @formatter:on
  }

  def generatePdf(id: String): ServiceCall[NotUsed, ByteString]
}






