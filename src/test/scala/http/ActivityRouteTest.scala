package http

import akka.http.scaladsl.marshallers.sprayjson.SprayJsonSupport._
import akka.http.scaladsl.model.{ContentTypes, HttpEntity, StatusCodes}
import akka.http.scaladsl.server.Route
import akka.http.scaladsl.testkit.ScalatestRouteTest
import database.{ActivityDAODietSlice, ActivityDAOService}
import model.Activity
import org.mockito.scalatest.IdiomaticMockito
import org.scalatest.BeforeAndAfterEach
import org.scalatest.freespec.AnyFreeSpec
import org.scalatest.matchers.should.Matchers
import spray.json.{DefaultJsonProtocol, _}

import scala.concurrent.Future

class ActivityRouteTest extends AnyFreeSpec with Matchers with ActivityRoute with DefaultJsonProtocol with ScalatestRouteTest
  with IdiomaticMockito with ActivityDAODietSlice with BeforeAndAfterEach {
  override val activityDAO = mock[ActivityDAOService]

  override def beforeEach: Unit = {
    reset(activityDAO)
  }

  val activity1 = Activity(1, "test name1", "test location1", None, None, false)
  val activity2 = Activity(2, "test name2", "test location2", Some(50.50), Some("test description2"), true)
  val updatedActivity2 = Activity(2, "updated test name2", "updated test location2", Some(50.50), Some("updated test description2"), true)
  val activity3 = Activity(2, "test name3", "test location3", None, Some("test description3"), false)
  val activities = List(activity1, activity2)

  "GET /activity" - {
    "return all activities without a query parameter" in {
      activityDAO.readAll returns Future.successful(List(activity1, activity2))
      Get("/activity") ~> Route.seal(activityRoute) ~> check {
        status shouldBe StatusCodes.OK
        responseAs[List[Activity]] shouldBe activities
      }
    }
    "list all activities that have been completed with a complete query param and true value" in {
      activityDAO.findByComplete(*) returns Future.successful(List(activity2))
      Get("/activity?complete=true") ~> Route.seal(activityRoute) ~> check {
        status shouldBe StatusCodes.OK
        responseAs[List[Activity]] shouldBe List(activity2)
      }
    }
    "list all activities that have not been completed with a complete query param and false value" in {
      activityDAO.findByComplete(*) returns Future.successful(List(activity1))
      Get("/activity?complete=false") ~> Route.seal(activityRoute) ~> check {
        status shouldBe StatusCodes.OK
        responseAs[List[Activity]] shouldBe List(activity1)
      }
    }
  }

  "GET /activity/{activitydId}" - {
    "return an activity with a valid ID" in {
      activityDAO.findById(*) returns Future.successful(Some(activity2))
      Get("/activity/2") ~> Route.seal(activityRoute) ~> check {
        status shouldBe StatusCodes.OK
        responseAs[Activity] shouldBe activity2
      }
    }
    "return an error message with a non-valid ID" in {
      activityDAO.findById(*) returns Future.successful(None)
      Get("/activity/5") ~> Route.seal(activityRoute) ~> check {
        responseAs[String] shouldBe "This ID doesn't exist"
      }
    }
  }

  "POST /activity" - {
    "add an activity to the activity list and return the new activity" in {
      activityDAO.create(*) returns Future.successful(activity3)
      val body = HttpEntity(ContentTypes.`application/json`, activity3.toJson.compactPrint)
      Post("/activity", body) ~> Route.seal(activityRoute) ~> check {
        responseAs[Activity] shouldBe activity3
      }
    }
  }

  "PUT /activity/{activityId}" - {
    "update an activity by activity ID and return the new activity" in {
      activityDAO.findById(*) returns Future.successful(Some(activity2))
      activityDAO.deleteById(*) returns Future.successful(2)
      activityDAO.create(*) returns Future.successful(updatedActivity2)
      val body = HttpEntity(ContentTypes.`application/json`, updatedActivity2.toJson.compactPrint)
      Put("/activity/2", body) ~> Route.seal(activityRoute) ~> check {
        status shouldBe StatusCodes.OK
        responseAs[Activity] shouldBe updatedActivity2
      }
    }
    "return an error message with a non-valid ID" in {
      activityDAO.findById(*) returns Future.successful(None)
      val body = HttpEntity(ContentTypes.`application/json`, activity2.toJson.compactPrint)
      Put("/activity/6", body) ~> Route.seal(activityRoute) ~> check {
        responseAs[String] shouldBe "This ID doesn't exist"
      }
    }
  }

  "DELETE /activity/{activityId}" - {
    "delete an activity by activity ID and return the ID of the deleted activity" in {
      activityDAO.deleteById(*) returns Future.successful(3)
      Delete("/activity/3") ~> Route.seal(activityRoute) ~> check {
        responseAs[String] shouldBe "ID - 3 has been deleted"
      }

    }
    "return an error message with a non-valid ID" in {
      activityDAO.deleteById(*) returns Future.successful(2)
      Delete("/activity/10") ~> Route.seal(activityRoute) ~> check {
        responseAs[String] shouldBe "ID - 10 has been deleted"
      }
    }
  }
}
