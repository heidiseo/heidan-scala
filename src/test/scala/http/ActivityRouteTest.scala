package http

import akka.http.scaladsl.marshallers.sprayjson.SprayJsonSupport._
import akka.http.scaladsl.model.{ContentTypes, HttpEntity, StatusCodes}
import akka.http.scaladsl.server.Route
import akka.http.scaladsl.testkit.ScalatestRouteTest
import model.{Activity, seedData}
import org.scalatest.freespec.AnyFreeSpec
import org.scalatest.matchers.should.Matchers
import spray.json.{DefaultJsonProtocol, _}

class ActivityRouteTest extends AnyFreeSpec with Matchers with ActivityRoute with DefaultJsonProtocol with ScalatestRouteTest {
  "GET /activity" - {
    "return all activities without a query parameter" in {
      Get("/activity") ~> Route.seal(activityRoute) ~> check {
        status shouldBe StatusCodes.OK
        responseAs[List[Activity]] shouldBe seedData.activities
      }
    }
    "list all activities that have been completed with a complete query param and true value" in {
      Get("/activity?complete=true") ~> Route.seal(activityRoute) ~> check {
        status shouldBe StatusCodes.OK
        responseAs[List[Activity]] shouldBe List(seedData.bookOfMormon)
      }
    }
    "list all activities that have not been completed with a complete query param and false value" in {
      Get("/activity?complete=false") ~> Route.seal(activityRoute) ~> check {
        status shouldBe StatusCodes.OK
        responseAs[List[Activity]] shouldBe List(seedData.richmondPark, seedData.lakeDistrict, seedData.wimbledon)
      }
    }
  }

  "GET /activity/{activitydId}" - {
    "return an activity with a valid ID" in {
      Get("/activity/2") ~> Route.seal(activityRoute) ~> check {
        status shouldBe StatusCodes.OK
        responseAs[Activity] shouldBe seedData.lakeDistrict
      }
    }
    "return an error message with a non-valid ID" in {
      Get("/activity/5") ~> Route.seal(activityRoute) ~> check {
        responseAs[String] shouldBe "This ID doesn't exist"
      }
    }
  }

  "POST /activity" - {
    "add an activity to the activity list" in {
      val body = HttpEntity(ContentTypes.`application/json`, Activity(5, "Cornwall", "Cornwall", Some(150.00), None, complete = false).toJson.compactPrint)
      Post("/activity", body) ~> Route.seal(activityRoute) ~> check {
        responseAs[List[Activity]] shouldBe seedData.activities
      }
    }
  }

  "PUT /activity/{activityId}" - {
    "update an activity by activity ID and return all activities" in {
      val body = HttpEntity(ContentTypes.`application/json`, Activity(2, "lakeDistrict", "Lake District", Some(130.00), Some("camping"), complete = false).toJson.compactPrint)
      Put("/activity/2", body) ~> Route.seal(activityRoute) ~> check {
        status shouldBe StatusCodes.OK
        responseAs[Activity] shouldBe seedData.lakeDistrict
      }
    }
    "return an error message with a non-valid ID" in {
      val body = HttpEntity(ContentTypes.`application/json`, Activity(2, "lakeDistrict", "Lake District", Some(130.00), Some("camping"), complete = false).toJson.compactPrint)
      Put("/activity/6", body) ~> Route.seal(activityRoute) ~> check {
        responseAs[String] shouldBe "This ID doesn't exist"
      }
    }
  }

  "DELETE /activity/{activityId}" - {
    "delete an activity by activity ID and return all remaining activities" in {
      Delete("/activity/3") ~> Route.seal(activityRoute) ~> check {
        responseAs[List[Activity]] shouldBe seedData.activities
      }

    }
    "return an error message with a non-valid ID" in {
      Delete("/activity/10") ~> Route.seal(activityRoute) ~> check {
        responseAs[String] shouldBe "This ID doesn't exist"
      }
    }
  }
}
