package database

import model.Activity
import slick.jdbc.PostgresProfile.api._
import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future
import config.DatabaseConfig.db


class Activities(tag: Tag) extends Table[Activity](tag, "activities") {
  def id = column[Int]("id")

  def name = column[String]("name")

  def location = column[String]("location")

  def cost = column[Option[Double]]("cost")

  def description = column[Option[String]]("description")

  def complete = column[Boolean]("complete")

  def * = (id, name, location, cost, description, complete) <> (Activity.tupled, Activity.unapply)
}


object ActivityDAO extends TableQuery(new Activities(_)) {

  def readAll: Future[Seq[Activity]] = {
    db.run(this.result)
  }

  def findById(id: Int): Future[Option[Activity]] = {
    db.run(this.filter(_.id === id).result).map(_.headOption)
  }

  def findByComplete(complete: Boolean): Future[Seq[Activity]] = {
    db.run(this.filter(_.complete === complete).result)
  }

  def create(activity: Activity): Future[Activity] = {
    db.run(this returning this.map(_.id) into ((acc, id) => acc.copy(id = id)) += activity)
  }

  def deleteById(id: Int): Future[Int] = {
    db.run(this.filter(_.id === id).delete)
  }
}
