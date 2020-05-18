package org.example.getvis.impl

import slick.jdbc.PostgresProfile.api._
import slick.lifted.Tag
/**
  * Define columns in database base table
  */
class FileContent(tag: Tag) extends Table[(String, String, Int)](tag, "file") {
  def name = column[String]("name")

  def fileType = column[String]("type")

  def length = column[Int]("length")

  def * = (name, fileType, length)
}
