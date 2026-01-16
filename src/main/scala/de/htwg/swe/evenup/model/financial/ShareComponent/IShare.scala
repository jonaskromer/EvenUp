package de.htwg.swe.evenup.model.financial.ShareComponent

import de.htwg.swe.evenup.model.PersonComponent.IPerson
import de.htwg.swe.evenup.model.PersonComponent.PersonDeserializer
import de.htwg.swe.evenup.modules.Default.given
import de.htwg.swe.evenup.util.Serializable
import de.htwg.swe.evenup.util.Deserializer

import scala.xml.Elem
import play.api.libs.json.Json
import play.api.libs.json.JsObject

trait IShare extends Serializable:
  val person: IPerson
  val amount: Double

  override def toXml: Elem =
    <Share>
      <Person>{person.toXml}</Person>
      <Amount>{amount}</Amount>
    </Share>

  override def toJson: JsObject = Json.obj(
    "person" -> person.toJson,
    "amount" -> amount
  )

object ShareDeserializer extends Deserializer[IShare]:
  val factory: IShareFactory = summon[IShareFactory]

  def fromXml(xml: Elem): IShare =
    val person = PersonDeserializer.fromXml((xml \ "Person").head.asInstanceOf[Elem])
    val amount = (xml \ "Amount").text.toDouble
    factory(person, amount)

  def fromJson(json: JsObject): IShare =
    val person = PersonDeserializer.fromJson((json \ "person").as[JsObject])
    val amount = (json \ "amount").as[Double]
    factory(person, amount)

trait IShareFactory:
  def apply(person: IPerson, amount: Double): IShare
