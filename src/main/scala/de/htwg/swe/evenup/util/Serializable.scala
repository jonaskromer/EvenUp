package de.htwg.swe.evenup.util

import play.api.libs.json.JsObject

import scala.xml.Elem

trait Serializable:
  def toJson: JsObject
  def toXml: Elem

trait Deserializer[T]:
  def fromXml(xml: Elem): T
  def fromJson(json: JsObject): T
