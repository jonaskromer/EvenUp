package de.htwg.swe.evenup.model.PersonComponent

import de.htwg.swe.evenup.modules.Default.given
import de.htwg.swe.evenup.util.Deserializer
import de.htwg.swe.evenup.util.Serializable

import scala.xml.Elem
import play.api.libs.json.Json
import play.api.libs.json.JsObject

trait IPerson extends Serializable:
  val name: String
  def updateName(name: String): IPerson

  override def toXml: Elem = 
    <Person>
      <Name>{name}</Name>
    </Person>

  override def toJson: JsObject =
    Json.obj(
      "name" -> name
    )

object PersonDeserializer extends Deserializer[IPerson]:
  val factory: IPersonFactory = summon[IPersonFactory]
  def fromXml(node: Elem): IPerson =
    // Handle nested Person elements: <Person><Person><Name>...</Name></Person></Person>
    val personElem = if (node \ "Person").isEmpty then node else (node \ "Person").head.asInstanceOf[Elem]
    val name = (personElem \ "Name").text
    factory(name)
  def fromJson(json: JsObject): IPerson =
    val name = (json \ "name").as[String]
    factory(name)

trait IPersonFactory:
  def apply(name: String): IPerson

