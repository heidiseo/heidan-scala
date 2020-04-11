package http

import akka.http.scaladsl.marshallers.sprayjson.SprayJsonSupport._
import akka.http.scaladsl.server.Directives._
import akka.http.scaladsl.server.Route
import spray.json.{DefaultJsonProtocol, RootJsonFormat}
import model.{Activity, seedData}


import scala.collection.mutable.ListBuffer

trait ActivityRoute extends DefaultJsonProtocol {
  implicit val activityFormat: RootJsonFormat[Activity] = jsonFormat6(Activity)

  val activityRoute: Route =
    (path("activity") & get & parameters("complete".as[Boolean].?)) {
      case Some(completeBool) =>
        if (completeBool) {
          complete(seedData.activities.filter(_.complete == true).toList)
        }
        else {
          complete(seedData.activities.filter(_.complete == false).toList)
        }
      case None => complete(seedData.activities.toList)

    } ~ (path("activity" / Segment) & get) { id =>
      val result = seedData.activities.find(_.id == id.toInt)
      result match {
        case Some(activity) => complete(activity.toJson)
        case None => complete("This ID doesn't exist")
      }

    } ~ (path("activity") & post) {
      entity(as[Activity]) { activity =>
        seedData.activities += activity
        complete(seedData.activities.toList)
      }

    } ~ (path("activity" / Segment) & put) { id =>
      entity(as[Activity]) { updatedActivity =>
        seedData.activities.find(_.id == id.toInt) match {
          case Some(oldActivity) =>
            seedData.activities -= oldActivity
            seedData.activities += updatedActivity
            complete(seedData.activities.toList)
          case None => complete("This ID doesn't exist")
        }
      }

    } ~ (path("activity" / Segment) & delete) { id =>
      seedData.activities.find(_.id == id.toInt) match {
        case Some(activity) =>
          seedData.activities -= activity
          complete(seedData.activities.toList)
        case None => complete("This ID doesn't exist")
      }
    }
}

