package http

import akka.http.scaladsl.marshallers.sprayjson.SprayJsonSupport._
import akka.http.scaladsl.server.Directives._
import akka.http.scaladsl.server.Route
import database.{ActivityDAODietSlice, ActivityDAOSlice}
import model.Activity
import spray.json.{DefaultJsonProtocol, RootJsonFormat}

trait ActivityRoute extends DefaultJsonProtocol with ActivityDAODietSlice {
  implicit val activityFormat: RootJsonFormat[Activity] = jsonFormat6(Activity)


  val activityRoute: Route =
    (path("activity") & get & parameters("complete".as[Boolean].?)) {
      case Some(completeBool) => complete(activityDAO.findByComplete(completeBool))
      case None => complete(activityDAO.readAll)

    } ~ (path("activity" / Segment) & get) { id =>
      onSuccess(activityDAO.findById(id.toInt)) {
        case Some(activity) => complete(activity)
        case None => complete("This ID doesn't exist")
      }

    } ~ (path("activity") & post) {
      entity(as[Activity]) { activity =>
        complete(activityDAO.create(activity))
      }

    } ~ (path("activity" / Segment) & put) { id =>
      entity(as[Activity]) { updatedActivity =>
        onSuccess(activityDAO.findById(id.toInt)) {
          case Some(_) =>
            activityDAO.deleteById(id.toInt)
            complete(activityDAO.create(updatedActivity))
          case None => complete("This ID doesn't exist")
        }
      }

    } ~ (path("activity" / Segment) & delete) { id =>
      activityDAO.deleteById(id.toInt)
      complete(s"ID - $id has been deleted")
    }
}

