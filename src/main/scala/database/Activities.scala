package database

import model.Activity
import slick.jdbc.PostgresProfile.api._

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future
import config.DatabaseConfig.db
import org.slf4j.{Logger, LoggerFactory}


class Activities(tag: Tag) extends Table[Activity](tag, "activities") {
  def id = column[Int]("id")

  def name = column[String]("name")

  def location = column[String]("location")

  def cost = column[Option[Double]]("cost")

  def description = column[Option[String]]("description")

  def complete = column[Boolean]("complete")

  def * = (id, name, location, cost, description, complete) <> (Activity.tupled, Activity.unapply)
}

trait ActivityDAOService {
  def readAll: Future[Seq[Activity]]

  def findById(id: Int): Future[Option[Activity]]

  def findByComplete(complete: Boolean): Future[Seq[Activity]]

  def create(activity: Activity): Future[Activity]

  def deleteById(id: Int): Future[Int]

}

trait ActivityDAODietSlice {
  def activityDAO: ActivityDAOService
}

trait ActivityDAOSlice extends ActivityDAODietSlice {

  override val activityDAO = new ActivityDAOService {
    val logger: Logger = LoggerFactory.getLogger(this.getClass)

    private val activities = TableQuery[Activities]

    override def readAll: Future[Seq[Activity]] = {
      logger.debug("attempting to fetch all activities from database")
      db.run(activities.result)
    }

    override def findById(id: Int): Future[Option[Activity]] = {
      logger.debug(s"attempting to fetch an activity - $id from dabatase")
      db.run(activities.filter(_.id === id).result).map(_.headOption)
    }

    override def findByComplete(complete: Boolean): Future[Seq[Activity]] = {
      logger.debug("attempting to fetch complete/incomplete activities from database")
      db.run(activities.filter(_.complete === complete).result)
    }

    override def create(activity: Activity): Future[Activity] = {
      logger.debug(s"attempting to save an activity, $activity.name in database")
      db.run(activities returning activities.map(_.id) into ((acc, id) => acc.copy(id = id)) += activity)
    }

    override def deleteById(id: Int): Future[Int] = {
      logger.debug(s"deleting an activity - $id")
      db.run(activities.filter(_.id === id).delete)
    }

  }

}
