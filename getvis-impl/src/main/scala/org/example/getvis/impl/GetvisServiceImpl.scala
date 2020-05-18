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

import scala.concurrent.{ExecutionContext, Future}


class GetvisServiceImpl(clusterSharding: ClusterSharding, persistentEntityRegistry: PersistentEntityRegistry, db: Database)
                       (implicit ec: ExecutionContext) extends GetvisService {

  /**
    * Create string conatainng database values
    *
    * @param t The employee's name.
    * @return String ready to be converted to pdf
    */
  def writes(t: Seq[(String, String, Int)]): String = {
    val seqLen = t.length

    val result = t.take(seqLen)
    val x = result.mkString(" ").replaceAll("[(())]", " ")

    val result1 = t.filter(_._2.contains(t.head._2)).toString().replaceAll("[(())]", "")
    val result2 = t.filterNot(_._2.contains(t.last._2)).filterNot(_._2.contains(t.head._2)).toString().replaceAll("[(())]", "")
    val result3 = t.filter(_._2.contains(t.last._2)).toString().replaceAll("[(())]", "")


    s"""
       |{
       |"intro" : {
       |             "fieldName" : "Intro",
       |             "fieldValue" : "This is my final project in order to complete my work placement and my
       |             third year of college. I found the project both rewarding an challenging. The biggest issue for me
       |             was my lack of Scala knowledge, however after spending time studying and reading documentation I found
       |             it very manageable.",
       |             "fieldType" : "Header5",
       |           },
       |
       |"Middle" : {
       |             "fieldName" : "Project Structure",
       |             "fieldValue" : "The project structure consists of one service,  the api contains one method called report
       |             this method is implemented in the impl class. In the report method a call is made to my postgress database
       |             the information received is then passed to the write method",
       |             "fieldType" : "Header5",
       |           },
       |
       |"Conclusion" : {
       |             "fieldName" : "Writes Method",
       |             "fieldValue" : "The writes method is responsible for returning a string that can be converted to json. I found this method difficult to
       |              create as I wasnt sure how to convert my information in away that could be interpreted by the pdfConverter. I use a filter to to get entry by file type
       |              this allowed me to display them separately (note that the query actually does this however it is visually easier to read when the mentioned way is used
       |              to display the data. Thank you for taking the time to go through my project",
       |             "fieldType" : "Header5",
       |           },
       |
       |"DBresults" : {
       |             "fieldName" : "Results",
       |             "fieldValue" : ["$result1","$result2","$result3","$x"],
       |             "fieldType" : "OrderedList",
       |           },
       |
       |
       |
       |
       |
       |
       |}
      """.stripMargin

  }

  /**
    * Implemnetd method to called from browser, this
    * method will query our database.
    *
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

//"webSite" : {
//"fieldName" : "Github",
//"fieldValue" : "This is the library used to generate the pdf, this link is to the github
//               which shows demos on implementation ",
//               "fieldType" : {
//"type" : "link",
//"link" : "https://github.com/GrowinScala/Flipper/tree/master/generator#main-methods-/-examples"
//},
//
//},

