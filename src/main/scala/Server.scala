import akka.actor.ActorSystem

import scala.util.{Failure, Success}
//import akka.http.scaladsl.model.StatusCodes.Success
import akka.http.scaladsl.server.Directives._
import akka.http.scaladsl.{Http, server}

import scala.concurrent.ExecutionContext

object Server extends App {

  val host = "0.0.0.0"
  val port = 8080

  implicit val system: ActorSystem = ActorSystem("helloworld")
  implicit val executor: ExecutionContext = system.dispatcher

  def route: server.Route =
    path("hello") {
      get {
        complete("Hello, Dan!")
      }
    }

  val bindingFuture = Http().bindAndHandle(route, host, port)
  bindingFuture.onComplete {
    case Success(serverBinding) =>
    println(s"listening to ${serverBinding.localAddress}")
    case Failure(error)
    => println(s"error: ${error.getMessage}")
  }

}
