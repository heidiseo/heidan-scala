package http

import akka.http.scaladsl.server.Route
import akka.http.scaladsl.testkit.ScalatestRouteTest
import org.scalatest.freespec.AnyFreeSpec
import org.scalatest.matchers.should.Matchers
import spray.json.{DefaultJsonProtocol, RootJsonFormat}
import akka.http.scaladsl.model.StatusCodes

class ActivityRouteTest extends AnyFreeSpec with Matchers with ActivityRoute with DefaultJsonProtocol with ScalatestRouteTest {
//    implicit val activityJson: RootJsonFormat[Activity] = jsonFormat6(Activity.apply)
  "GET /activity" - {
    "return all activities without a query parameter" in {
      Get("/activity") ~> Route.seal(activityRoute) ~> check {
        status shouldBe StatusCodes.OK
        responseAs[Activity] shouldBe List(richmondPark, lakeDistrict, wimbledon, bookOfMormon)
      }
    }
    "list all activities that have been completed with a complete query param and true value" in {

    }
    "list all activities that have not been completed with a complete query param and false value" in {

    }
  }

  "GET /activity/{activitydId}" - {
    "return an activity with a valid ID" in {

    }
    "return an error message with a non-valid ID" in {
      Get("/activity/5") ~> Route.seal(activityRoute) ~> check {
        responseAs[String] shouldBe "This ID doesn't exist"
      }
    }
  }

  "POST /activity" - {
    "add an activity to the activity list" in {

    }
  }

  "PUT /activity/{activityId}" - {
    "update an activity by activity ID and return all activities" in {

    }
    "return an error message with a non-valid ID" in {

    }
  }

  "DELETE /activity/{activityId}" - {
    "delete an activity by activity ID and return all remaining activities" in {

    }
    "return an error message with a non-valid ID" in {

    }
  }

}
