package model

import scala.collection.mutable.ListBuffer

case class Activity(var id: Int, var name: String, var location: String, var cost: Option[Double], var description: Option[String], var complete: Boolean)

object seedData {
  var richmondPark: Activity = Activity(1, "richmondPark", "Richmond Park", None, Some("bike ride"), complete = false)
  var lakeDistrict: Activity = Activity(2, "lakeDistrict", "Lake District", Some(100.00), Some("camping"), complete = false)
  var wimbledon: Activity = Activity(3, "wimbledon", "Wimbledon", Some(80.00), Some("tennis game"), complete = false)
  var bookOfMormon: Activity = Activity(4, "Book of Mormon", "Piccadilly", Some(75.00), None, complete = true)
  var activities: ListBuffer[Activity] = ListBuffer(richmondPark, lakeDistrict, wimbledon, bookOfMormon)
}