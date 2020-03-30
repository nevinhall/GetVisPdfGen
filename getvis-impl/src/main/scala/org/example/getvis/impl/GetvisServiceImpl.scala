package org.example.getvis.impl

import akka.NotUsed
import akka.cluster.sharding.typed.scaladsl.ClusterSharding
import com.lightbend.lagom.scaladsl.api.ServiceCall
import com.lightbend.lagom.scaladsl.persistence.PersistentEntityRegistry
import org.example.getvis.api.GetvisService
import slick.jdbc.JdbcBackend.Database
import slick.jdbc.PostgresProfile.api._
import slick.lifted.Tag

import scala.concurrent.ExecutionContext


class GetvisServiceImpl(
                         clusterSharding: ClusterSharding,
                         persistentEntityRegistry: PersistentEntityRegistry, db: Database
                       )(implicit ec: ExecutionContext)
  extends GetvisService {


  override def hello(id: String): ServiceCall[NotUsed, Seq[(String, String, Int)]] = ServiceCall {
    val file = TableQuery[File]


    _ =>

      db.run(file.filter(_.name === "test").result)

  }


}

class File(tag: Tag) extends Table[(String, String, Int)](tag, "file") {
  def name = column[String]("name")

  def fileType = column[String]("type")

  def length = column[Int]("length")

  def * = (name, fileType, length)


}

