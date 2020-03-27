package org.example.getvis.impl

import akka.NotUsed
import akka.cluster.sharding.typed.scaladsl.{ClusterSharding, EntityRef}
import akka.util.Timeout
import com.lightbend.lagom.scaladsl.api.ServiceCall
import com.lightbend.lagom.scaladsl.persistence.PersistentEntityRegistry
import org.example.getvis.api.GetvisService
import slick.jdbc.JdbcBackend.Database
import scala.concurrent.ExecutionContext
import scala.concurrent.duration._


class GetvisServiceImpl(
                         clusterSharding: ClusterSharding,
                         persistentEntityRegistry: PersistentEntityRegistry, db:Database
                       )(implicit ec: ExecutionContext)
  extends GetvisService {


  private def entityRef(id: String): EntityRef[GetvisCommand] =
    clusterSharding.entityRefFor(GetvisState.typeKey, id)

  implicit val timeout = Timeout(5.seconds)

  override def hello(id: String): ServiceCall[NotUsed, String] = ServiceCall {
    _ =>
      // Look up the sharded entity (aka the aggregate instance) for the given ID.
      val ref = entityRef(id)

      // Ask the aggregate instance the Hello command.
      ref
        .ask[Greeting](replyTo => Hello(id, replyTo))
        .map(greeting => greeting.message)


  }




}
