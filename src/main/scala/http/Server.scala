package http

import akka.actor.ActorSystem
import akka.http.scaladsl.Http
import akka.http.scaladsl.server.Directives._
import akka.http.scaladsl.server.{Directive, Route}
import scala.concurrent.ExecutionContext
import scala.util.{Failure, Success}

object Server extends App {

  val host = "0.0.0.0"
  val port = 8080

  implicit val system: ActorSystem = ActorSystem("helloworld")
  implicit val executor: ExecutionContext = system.dispatcher

  def route: Route =
    (path("hello") & get & getQueryParams) {
      case Person(Some(name), Some(country)) => complete(s"Hello, $name! You are from $country")
      case Person(Some(name), None) => complete(s"Hello, $name!")
      case Person(None, Some(country)) => complete(s"Hello stranger, you are from $country")
      case Person(None, None) => complete("hello")
    }

  val bindingFuture = Http().bindAndHandle(route, host, port)
  bindingFuture.onComplete {
    case Success(serverBinding) =>
      println(s"listening to ${serverBinding.localAddress}")
    case Failure(error)
    => println(s"error: ${error.getMessage}")
  }

  private def getQueryParams: Directive[Tuple1[Person]] = {
    parameters("name".?, "country".?) tmap (Person.apply _).tupled
  }

  case class Person(name: Option[String], country: Option[String])

  case class Group(people: List[Person])

  //
  //  val dan: Person = Person("Dan", 29)
  //  val heidi: Person = Person("Heidi", 32)
  //  val bokyung: Person = Person("Bokyung", 61)
  //  val group: Group = Group(List(dan, heidi, bokyung))


}
