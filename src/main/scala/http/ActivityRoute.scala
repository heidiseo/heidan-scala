package http

import akka.http.scaladsl.marshallers.sprayjson.SprayJsonSupport._
import akka.http.scaladsl.server.Directives._
import akka.http.scaladsl.server.Route
import model.{Activity, seedData}
import spray.json.{DefaultJsonProtocol, RootJsonFormat}

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
        case Some(activity) => complete(activity)
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
            oldActivity.id = updatedActivity.id
            oldActivity.name = updatedActivity.name
            oldActivity.location = updatedActivity.location
            oldActivity.cost = updatedActivity.cost
            oldActivity.description = updatedActivity.description
            oldActivity.complete = updatedActivity.complete
            complete(oldActivity)
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

