package de.htwg.swe.evenup.model.DateComponent

import de.htwg.swe.evenup.model.DateComponent.BaseDateImpl.Date
import de.htwg.swe.evenup.util.Serializable
import de.htwg.swe.evenup.util.Deserializer
import de.htwg.swe.evenup.modules.Default.given

import scala.xml.Elem
import play.api.libs.json.Json
import play.api.libs.json.JsObject

trait IDate extends Serializable:
  val day: Int
  val month: Int
  val year: Int

  override def toXml: Elem =
    <Date>
      <Day>{day}</Day>
      <Month>{month}</Month>
      <Year>{year}</Year>
    </Date>

  override def toJson: JsObject = Json.obj(
    "day"   -> day,
    "month" -> month,
    "year"  -> year
  )

object DateDeserializer extends Deserializer[IDate]:
  val factory: IDateFactory = summon[IDateFactory]

  def fromXml(xml: Elem): IDate =
    val dateElem = (xml \ "Date").headOption.map(_.asInstanceOf[Elem]).getOrElse(xml)
    val day      = (dateElem \ "Day").text.toInt
    val month    = (dateElem \ "Month").text.toInt
    val year     = (dateElem \ "Year").text.toInt
    factory(day, month, year)

  def fromJson(json: JsObject): IDate =
    val day   = (json \ "day").as[Int]
    val month = (json \ "month").as[Int]
    val year  = (json \ "year").as[Int]
    factory(day, month, year)

trait IDateFactory:
  def apply(day: Int, month: Int, year: Int): IDate
