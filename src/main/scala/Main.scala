import akka.actor.ActorSystem
import akka.http.scaladsl.Http
import database.ActivityDAOSlice
import http.ActivityRoute

import scala.concurrent.ExecutionContext
import scala.util.{Failure, Success}


object Main extends App with ActivityRoute with ActivityDAOSlice {

  implicit val activitySystem: ActorSystem = ActorSystem("activities")
  implicit val activityExecutor: ExecutionContext = activitySystem.dispatcher

  val bindingFuture = Http().bindAndHandle(activityRoute, "192.168.0.23", 9000)
  bindingFuture.onComplete {
    case Success(serverBinding) =>
      println(s"listening to ${serverBinding.localAddress}")
    case Failure(error)

    => println(s"error: ${error.getMessage}")
  }
}
