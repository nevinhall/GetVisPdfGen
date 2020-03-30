package org.example.getvis.api

import akka.NotUsed
import com.lightbend.lagom.scaladsl.api.{Descriptor, Service, ServiceCall}

object GetvisService  {
  val TOPIC_NAME = "greetings"
}

trait GetvisService extends Service {


  def hello(id: String): ServiceCall[NotUsed, Seq[(String,String,Int)]]





  override final def descriptor: Descriptor = {
    import Service._
    // @formatter:off
    named("getvis")
      .withCalls(
        pathCall("/report/:id", hello _)
      )
      .withAutoAcl(true)
    // @formatter:on
  }
}






