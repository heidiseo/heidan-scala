package http

import akka.actor.ActorSystem
import akka.http.scaladsl.Http
import akka.http.scaladsl.server.Directives._
import akka.http.scaladsl.server.Route
import spray.json.{DefaultJsonProtocol, RootJsonFormat}
import akka.http.scaladsl.marshallers.sprayjson.SprayJsonSupport._

import scala.concurrent.ExecutionContext
import scala.util.{Failure, Success}

object Server extends App with DefaultJsonProtocol {

  val host = "0.0.0.0"
  val port = 8080

  implicit val system: ActorSystem = ActorSystem("helloworld")
  implicit val executor: ExecutionContext = system.dispatcher
  implicit val personFormat: RootJsonFormat[Person] = jsonFormat3(Person)

  def route: Route =
    (path("hello") & get & parameters("name".?, "country".?)) { (name, country) =>
      (name, country) match {
        case (Some(name), Some(country)) => complete(s"Hello, ${name.capitalize}! You are from ${country.capitalize}.")
        case (Some(name), None) => complete(s"Hello, ${name.capitalize}!")
        case (None, Some(country)) => complete(s"Hello stranger, you are from ${country.capitalize}.")
        case (None, None) => complete("hello")
      }
    } ~ (path("hello" / Segment) & get) { id =>
      val result = group.find(_.id == id.toInt)
      result match {
        case Some(person) => complete(person.toJson)
        case None => complete("This ID doesn't exist")
      }
    }

  val bindingFuture = Http().bindAndHandle(route, host, port)
  bindingFuture.onComplete {
    case Success(serverBinding) =>
      println(s"listening to ${serverBinding.localAddress}")
    case Failure(error)
    => println(s"error: ${error.getMessage}")
  }

  case class Person(id: Int, name: String, country: String)


  val bokyung: Person = Person(1, "Bokyung aka the mommy", "South Korea")
  val donggi: Person = Person(2, "Donggi aka the brooo", "South Korea")
  val hakyoung: Person = Person(3, "Hakyoung aka the daddy", "South Korea")
  val namsoon: Person = Person(4, "Namsoon aka the granny", "South Korea")
  val anna: Person = Person(5, "Anna aka Dongmin", "South Korea")
  val dan: Person = Person(6, "Dan aka GingerViking", "England")
  val heidi: Person = Person(7, "Heidi aka heiz", "South Korea")
  val joe: Person = Person(8, "Joe aka the fat boiiii", "England")
  val group: List[Person] = List(dan, heidi, bokyung, donggi, hakyoung, namsoon, anna, joe)


}
