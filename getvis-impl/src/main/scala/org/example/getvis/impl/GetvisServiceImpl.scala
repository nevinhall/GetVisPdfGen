package org.example.getvis.impl

import java.nio.file.{Files, Paths}

import akka.NotUsed
import akka.cluster.sharding.typed.scaladsl.ClusterSharding
import akka.util.ByteString
import com.lightbend.lagom.scaladsl.api.ServiceCall
import com.lightbend.lagom.scaladsl.api.transport.BadRequest
import com.lightbend.lagom.scaladsl.persistence.PersistentEntityRegistry
import generator.generate.Generator.convertJSONtoPDF
import org.example.getvis.api.GetvisService
import slick.jdbc.JdbcBackend.Database
import slick.jdbc.PostgresProfile.api._
import slick.lifted.Tag

import scala.concurrent.{ExecutionContext, Future}


class GetvisServiceImpl(clusterSharding: ClusterSharding, persistentEntityRegistry: PersistentEntityRegistry, db: Database)
                       (implicit ec: ExecutionContext) extends GetvisService {

  /**
    *Create string conatainng database values
    * @param t The employee's name.
    * @return String ready to be converted to pdf
    */
  def writes(t: Seq[(String, String, Int)]): String = {
    val seqLen = t.length

    val result = t.take(seqLen)
    val x = result.toString().replaceAll("[(())]", "\r\n" )


    s"""
       |{
       |
       |  "phones" : {
       |             "fieldName" : "Results",
       |             "fieldValue" : ["$x"],
       |             "fieldType" : "UnorderedList",
       |             "formattingID" : "list"
       |           }
       |
       |
       |
       |}
      """.stripMargin

  }

  /**
    * Implemnetd method to called from browser, this
    * method will query our database.
    * @return pdf is succesful bad request of not
    */
  override def generatePdf(id: String): ServiceCall[NotUsed, ByteString] = ServiceCall {
    val file = TableQuery[FileContent]
    _ =>


      val query = db.run(file.sortBy(f => (f.fileType.asc, f.length.asc)).result)

      query.map(x => {
        convertJSONtoPDF(writes(x))
        writes(x)
      })

      Thread.sleep(5000)
      val pdfFile = Paths.get("C:/Users/Nevin Hall/IdeaProjects/lagom-scala-sbt/html.pdf")

      Files.exists(pdfFile) match {
        case true => Future(ByteString(Files.readAllBytes(pdfFile)))
        case _ => throw BadRequest("No File has been Generated ")
      }




  }

}

/**
  * Define columns in database base table
  */
class FileContent(tag: Tag) extends Table[(String, String, Int)](tag, "file") {
  def name = column[String]("name")

  def fileType = column[String]("type")

  def length = column[Int]("length")

  def * = (name, fileType, length)
}


