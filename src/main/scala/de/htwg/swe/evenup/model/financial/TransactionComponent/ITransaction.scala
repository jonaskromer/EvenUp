package de.htwg.swe.evenup.model.financial.TransactionComponent

import de.htwg.swe.evenup.model.PersonComponent.IPerson
import de.htwg.swe.evenup.model.DateComponent.IDate
import de.htwg.swe.evenup.modules.Default.given
import de.htwg.swe.evenup.util.Serializable
import de.htwg.swe.evenup.util.Deserializer

import scala.xml.Elem
import play.api.libs.json.Json
import play.api.libs.json.JsObject

trait ITransaction extends Serializable:
  val from: IPerson
  val to: IPerson
  val amount: Double
  val date: IDate

  def updateFrom(from: IPerson): ITransaction
  def updateTo(to: IPerson): ITransaction
  def updateAmount(amount: Double): ITransaction
  def updateDate(date: IDate): ITransaction

  override def toXml: Elem =
    <Transaction>
      <From>{from.toXml}</From>
      <To>{to.toXml}</To>
      <Amount>{amount}</Amount>
      <Date>{date.toXml}</Date>
    </Transaction>

  override def toJson: JsObject = Json.obj(
    "from"   -> from.toJson,
    "to"     -> to.toJson,
    "amount" -> amount,
    "date"   -> date.toJson
  )

object TransactionDeserializer extends Deserializer[ITransaction]:
  val factory: ITransactionFactory = summon[ITransactionFactory]

  def fromXml(xml: Elem): ITransaction =
    val from = de.htwg.swe.evenup.model.PersonComponent.PersonDeserializer
      .fromXml((xml \ "From").head.asInstanceOf[Elem])
    val to = de.htwg.swe.evenup.model.PersonComponent.PersonDeserializer.fromXml((xml \ "To").head.asInstanceOf[Elem])
    val amount = (xml \ "Amount").text.toDouble
    val date   = de.htwg.swe.evenup.model.DateComponent.DateDeserializer.fromXml((xml \ "Date").head.asInstanceOf[Elem])
    factory(from, to, amount, date)

  def fromJson(json: JsObject): ITransaction =
    val from   = de.htwg.swe.evenup.model.PersonComponent.PersonDeserializer.fromJson((json \ "from").as[JsObject])
    val to     = de.htwg.swe.evenup.model.PersonComponent.PersonDeserializer.fromJson((json \ "to").as[JsObject])
    val amount = (json \ "amount").as[Double]
    val date   = de.htwg.swe.evenup.model.DateComponent.DateDeserializer.fromJson((json \ "date").as[JsObject])
    factory(from, to, amount, date)

trait ITransactionFactory:
  def apply(from: IPerson, to: IPerson, amount: Double, date: IDate): ITransaction
