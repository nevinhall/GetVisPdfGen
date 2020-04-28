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
import play.api.libs.json._
import slick.jdbc.JdbcBackend.Database
import slick.jdbc.PostgresProfile.api._
import slick.lifted.Tag

import scala.concurrent.{ExecutionContext, Future}


class GetvisServiceImpl(clusterSharding: ClusterSharding, persistentEntityRegistry: PersistentEntityRegistry, db: Database)
                       (implicit ec: ExecutionContext) extends GetvisService {


  def writes(t: Seq[(String, String, Int)]): String = {
    val i = t.head._1
    val e = t.head._2
    val h = t.head._3


    s"""
       |{
       |   "name"  : {
       |             "fieldName" :  "$i" ,
       |             "fieldValue" : "u",
       |             "fieldType" : "Header1",
       |             "formattingID" : "bigHeader"
       |            },
       |  "phones" : {
       |             "fieldName" : "$e",
       |             "fieldValue" : [12345, 54321],
       |             "fieldType" : "UnorderedList",
       |             "formattingID" : "list"
       |           },
       |  "webSite" : {
       |             "fieldName" : "HELLO ",
       |             "fieldValue" : "DOMI",
       |             "fieldType" : {
       |                               "type" : "link",
       |                               "link" : "www.growin.pt"
       |                           },
       |             "formattingID" : "link"
       |           }
       |}
      """.stripMargin

  }


  override def hello(id: String): ServiceCall[NotUsed, ByteString] = ServiceCall {
    val file = TableQuery[FileContent]
    _ =>


      val query = db.run(file.sortBy(f => (f.fileType.asc, f.length.asc)).result)

      query.map(x => {
        convertJSONtoPDF(writes(x))
        writes(x)
      })

      val pdfFile = Paths.get("C:/Users/Nevin Hall/IdeaProjects/lagom-scala-sbt/html.pdf")

      Files.exists(pdfFile) match {
        case true => Future(ByteString(Files.readAllBytes(pdfFile)))
        case  _ =>  throw BadRequest("No File has been Generated ")
      }




    //      val query = db.run(file.groupBy(f => f.fileType)
    //        .map { case (fileType, group) => (fileType, group.map(_.length).avg, group.map( .name))
    //        }.result)

    //      db.run(file.groupBy(p => p.fileType)
    //        .map{ case (fileType, group) => (fileType, group.map(_.length).avg) }.result
    //      )


  }

}


class FileContent(tag: Tag) extends Table[(String, String, Int)](tag, "file") {
  def name = column[String]("name")

  def fileType = column[String]("type")

  def length = column[Int]("length")

  def * = (name, fileType, length)


}


case class PdfInfo(name: String, fileType: String, length: Int)

object PdfInfo {

  implicit object PdfFormat extends Format[PdfInfo] {

    // convert from Tweet object to JSON (serializing to JSON)
    def writes(pdfInfo: PdfInfo): JsValue = {
      val pdfInfoSeq = Seq(
        "name" -> JsString(pdfInfo.name),
        "fileType" -> JsString(pdfInfo.fileType),
        "length" -> JsNumber(pdfInfo.length)
      )
      JsObject(pdfInfoSeq)
    }

    def reads(json: JsValue): JsResult[PdfInfo] = {
      JsSuccess(PdfInfo("", "", 0))
    }
  }

}
