import akka.actor.ActorSystem
import akka.http.scaladsl.Http
import http.ActivityRoute

import scala.concurrent.ExecutionContext
import scala.util.{Failure, Success}

object Main extends App with ActivityRoute {

  implicit val activitySystem: ActorSystem = ActorSystem("activities")
  implicit val activityExecutor: ExecutionContext = activitySystem.dispatcher

  val bindingFuture = Http().bindAndHandle(activityRoute, "0.0.0.0", 8080)
  bindingFuture.onComplete {
    case Success(serverBinding) =>
      println(s"listening to ${serverBinding.localAddress}")
    case Failure(error)
    => println(s"error: ${error.getMessage}")
  }
}
