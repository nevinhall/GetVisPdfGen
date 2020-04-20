//package org.example.getvis.impl
//
//import akka.http.scaladsl.model.HttpHeader.ParsingResult.Ok
//import generator.generate.Generator.convertJSONtoPDF
//import play.api.mvc._
//import play.api.libs.json._
//import play.mvc.Controller
//import akka.NotUsed
//import akka.cluster.sharding.typed.scaladsl.ClusterSharding
//import com.lightbend.lagom.scaladsl.api.ServiceCall
//import com.lightbend.lagom.scaladsl.persistence.PersistentEntityRegistry
//import org.example.getvis.api.GetvisService
//import play.api.libs.json.{Format, JsNumber, JsObject, JsResult, JsString, JsSuccess, JsValue, Json}
//import slick.jdbc.JdbcBackend.Database
//import slick.jdbc.PostgresProfile.api._
//import slick.lifted.Tag
//import generator.generate.Generator.convertJSONtoPDF
//
//import scala.concurrent.{ExecutionContext, Future}
//
//
//class structureData(db: Database) {
//
//  object Twitter extends Controller {
//
//
//    def peeps = Action {
//      val file = TableQuery[File]
//      val query = db.run(file.sortBy(f => (f.fileType.asc, f.length.asc)).result)
//      query match {
//        case Seq(String,String,Int) => Ok(query.map(x =>
//
//          convertJSONtoPDF((convertTweetsToJson(Seq(PdfInfo.apply(x.head._1, x.head._2, x.head._3))).as[JsString].value)
//
//        case _ => Ok("Bummer, technical error")
//    }
//
//    // this works because reads/writes are defined in Format[Tweet]
//
//    def convertTweetsToJson(tweets: Seq[PdfInfo]): JsValue = Json.toJson(tweets)
//
//    // more code ...
//
//
//  }
//
//}
//
//class File(tag: Tag) extends Table[(String, String, Int)](tag, "file") {
//  def name = column[String]("name")
//
//  def fileType = column[String]("type")
//
//  def length = column[Int]("length")
//
//  def * = (name, fileType, length)
//
//
//}
//
//
//case class PdfInfo(name: String, fileType: String, length: Int)
//
//object PdfInfo {
//
//  implicit object PdfFormat extends Format[PdfInfo] {
//
//    // convert from Tweet object to JSON (serializing to JSON)
//    def writes(pdfInfo: PdfInfo): JsValue = {
//      val pdfInfoSeq = Seq(
//        "name" -> JsString(pdfInfo.name),
//        "fileType" -> JsString(pdfInfo.fileType),
//        "length" -> JsNumber(pdfInfo.length)
//      )
//      JsObject(pdfInfoSeq)
//    }
//
//    def reads(json: JsValue): JsResult[PdfInfo] = {
//      JsSuccess(PdfInfo("", "", 0))
//    }
//  }
//
//}