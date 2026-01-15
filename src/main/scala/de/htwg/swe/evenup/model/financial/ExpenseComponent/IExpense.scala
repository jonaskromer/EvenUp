package de.htwg.swe.evenup.model.financial.ExpenseComponent

import de.htwg.swe.evenup.model.DateComponent.IDate
import de.htwg.swe.evenup.model.PersonComponent.IPerson
import de.htwg.swe.evenup.model.financial.ShareComponent.IShare
import de.htwg.swe.evenup.modules.Default.given
import de.htwg.swe.evenup.util.Deserializer
import de.htwg.swe.evenup.util.Serializable
import de.htwg.swe.evenup.model.DateComponent.DateDeserializer
import de.htwg.swe.evenup.model.PersonComponent.PersonDeserializer
import de.htwg.swe.evenup.model.financial.ShareComponent.ShareDeserializer

import scala.xml.Elem
import play.api.libs.json.Json
import play.api.libs.json.JsObject
import scala.util.control.Exception.Described

trait IExpense extends Serializable:
  val name: String
  val amount: Double
  val date: IDate
  val paid_by: IPerson
  val shares: List[IShare]

  def updateName(name: String): IExpense
  def updateAmount(amount: Double): IExpense
  def updateDate(date: IDate): IExpense
  def updatePaidBy(paid_by: IPerson): IExpense
  def updateShares(shares: List[IShare]): IExpense

  override def toXml: Elem =
    <Expense>
      <Name>{name}</Name>
      <Amount>{amount}</Amount>
      <Date>{date.toXml}</Date>
      <PaidBy>{paid_by.toXml}</PaidBy>
      <Shares>
        {shares.map(_.toXml)}
      </Shares>
    </Expense>

  override def toJson: JsObject = Json.obj(
    "name"   -> name,
    "amount" -> amount,
    "date"   -> date.toJson,
    "paidBy" -> paid_by.toJson,
    "shares" -> shares.map(_.toJson)
  )

object ExpenseDeserializer extends Deserializer[IExpense]:
  val factory: IExpenseFactory = summon[IExpenseFactory]

  def fromXml(xml: Elem): IExpense =
    val name    = (xml \ "Name").text
    val amount  = (xml \ "Amount").text.toDouble
    val date    = DateDeserializer.fromXml((xml \ "Date").head.asInstanceOf[Elem])
    val paid_by = PersonDeserializer.fromXml((xml \ "PaidBy").head.asInstanceOf[Elem])
    val shares  = (xml \ "Shares" \ "Share").map(node => ShareDeserializer.fromXml(node.asInstanceOf[Elem])).toList
    factory(name, amount, date, paid_by, shares)

  def fromJson(json: JsObject): IExpense =
    val name    = (json \ "name").as[String]
    val amount  = (json \ "amount").as[Double]
    val date    = DateDeserializer.fromJson((json \ "date").as[JsObject])
    val paid_by = PersonDeserializer.fromJson((json \ "paidBy").as[JsObject])
    val shares  = (json \ "shares").as[List[JsObject]].map(obj => ShareDeserializer.fromJson(obj))
    factory(name, amount, date, paid_by, shares)

trait IExpenseFactory:
  def apply(name: String, amount: Double, date: IDate, paid_by: IPerson, shares: List[IShare]): IExpense
