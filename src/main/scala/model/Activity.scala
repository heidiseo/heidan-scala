package model

import scala.collection.mutable.ListBuffer

case class Activity(id: Int, name: String, location: String, cost: Option[Double], description: Option[String], complete: Boolean)

object seedData {
  val richmondPark: Activity = Activity(1, "richmondPark", "Richmond Park", None, Some("bike ride"), false)
  val lakeDistrict: Activity = Activity(2, "lakeDistrict", "Lake District", Some(100.00), Some("camping"), false)
  val wimbledon: Activity = Activity(3, "wimbledon", "Wimbledon", Some(80.00), Some("tennis game"), false)
  val bookOfMormon: Activity = Activity(4, "Book of Mormon", "Piccadilly", Some(75.00), None, true)
  var activities: ListBuffer[Activity] = ListBuffer(richmondPark, lakeDistrict, wimbledon, bookOfMormon)
}