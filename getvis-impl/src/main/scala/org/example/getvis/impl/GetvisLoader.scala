package org.example.getvis.impl

import akka.cluster.sharding.typed.scaladsl.Entity
import com.lightbend.lagom.scaladsl.api.ServiceLocator
import com.lightbend.lagom.scaladsl.api.ServiceLocator.NoServiceLocator
import com.lightbend.lagom.scaladsl.persistence.cassandra.CassandraPersistenceComponents
import com.lightbend.lagom.scaladsl.server._
import com.lightbend.lagom.scaladsl.devmode.LagomDevModeComponents
import play.api.libs.ws.ahc.AhcWSComponents
import org.example.getvis.api.GetvisService
import com.lightbend.lagom.scaladsl.broker.kafka.LagomKafkaComponents
import com.lightbend.lagom.scaladsl.playjson.JsonSerializerRegistry
import com.softwaremill.macwire._

class GetvisLoader extends LagomApplicationLoader {

  override def load(context: LagomApplicationContext): LagomApplication =
    new GetvisApplication(context) {
      override def serviceLocator: ServiceLocator = NoServiceLocator
    }

  override def loadDevMode(context: LagomApplicationContext): LagomApplication =
    new GetvisApplication(context) with LagomDevModeComponents

  override def describeService = Some(readDescriptor[GetvisService])
}

abstract class GetvisApplication(context: LagomApplicationContext)
  extends LagomApplication(context)
    with CassandraPersistenceComponents
    with LagomKafkaComponents
    with AhcWSComponents {

  // Bind the service that this server provides
  override lazy val lagomServer: LagomServer = serverFor[GetvisService](wire[GetvisServiceImpl])

  // Register the JSON serializer registry
  override lazy val jsonSerializerRegistry: JsonSerializerRegistry = GetvisSerializerRegistry

  // Initialize the sharding of the Aggregate. The following starts the aggregate Behavior under
  // a given sharding entity typeKey.
  clusterSharding.init(
    Entity(GetvisState.typeKey)(
      entityContext => GetvisBehavior.create(entityContext)
    )
  )

}
