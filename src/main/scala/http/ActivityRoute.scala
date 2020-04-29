package http

import akka.http.scaladsl.marshallers.sprayjson.SprayJsonSupport._
import akka.http.scaladsl.server.Directives._
import akka.http.scaladsl.server.Route
import database.ActivityDAO._
import model.Activity
import spray.json.{DefaultJsonProtocol, RootJsonFormat}

trait ActivityRoute extends DefaultJsonProtocol {
  implicit val activityFormat: RootJsonFormat[Activity] = jsonFormat6(Activity)

  val activityRoute: Route =
    (path("activity") & get & parameters("complete".as[Boolean].?)) {
      case Some(completeBool) => complete(findByComplete(completeBool))
      case None => complete(readAll)

    } ~ (path("activity" / Segment) & get) { id =>
      onSuccess(findById(id.toInt)) {
        case Some(activity) => complete(activity)
        case None => complete("This ID doesn't exist")
      }

    } ~ (path("activity") & post) {
      entity(as[Activity]) { activity =>
        complete(create(activity))
      }

    } ~ (path("activity" / Segment) & put) { id =>
      entity(as[Activity]) { updatedActivity =>
        onSuccess(findById(id.toInt)) {
          case Some(_) =>
            deleteById(id.toInt)
            complete(create(updatedActivity))
          case None => complete("This ID doesn't exist")
        }
      }

    } ~ (path("activity" / Segment) & delete) { id =>
      deleteById(id.toInt)
      complete(s"ID - $id has been deleted")
    }
}

