package de.htwg.swe.evenup.model.GroupComponent

import de.htwg.swe.evenup.model.PersonComponent.IPerson
import de.htwg.swe.evenup.model.PersonComponent.PersonDeserializer
import de.htwg.swe.evenup.model.financial.ExpenseComponent.IExpense
import de.htwg.swe.evenup.model.financial.ExpenseComponent.ExpenseDeserializer
import de.htwg.swe.evenup.model.financial.TransactionComponent.ITransaction
import de.htwg.swe.evenup.model.financial.TransactionComponent.TransactionDeserializer
import de.htwg.swe.evenup.model.financial.DebtComponent.IDebtCalculationStrategy
import de.htwg.swe.evenup.model.financial.DebtComponent.IDebt
import de.htwg.swe.evenup.model.GroupComponent.BaseGroupImpl.Group
import de.htwg.swe.evenup.util.Deserializer
import de.htwg.swe.evenup.model.financial.DebtComponent.BaseDebtImpl.NormalDebtStrategy
import de.htwg.swe.evenup.model.financial.DebtComponent.BaseDebtImpl.SimplifiedDebtStrategy
import de.htwg.swe.evenup.modules.Default.given
import de.htwg.swe.evenup.util.Serializable

import scala.xml.Elem
import play.api.libs.json.Json
import play.api.libs.json.JsObject
import play.api.libs.json.JsValue

trait IGroup extends Serializable:
  val name: String
  val members: List[IPerson]
  val expenses: List[IExpense]
  val transactions: List[ITransaction]
  val debt_strategy: IDebtCalculationStrategy

  override def toXml: Elem =
    <Group>
      <Name>{name}</Name>
      <Members>
        {members.map(_.toXml)}
      </Members>
      <Expenses>
        {expenses.map(_.toXml)}
      </Expenses>
      <Transactions>
        {transactions.map(_.toXml)}
      </Transactions>
      <DebtCalculationStrategy>
        {debt_strategy.toXml}
      </DebtCalculationStrategy>
    </Group>

  override def toJson: JsObject = Json.obj(
    "name"                    -> name,
    "members"                 -> members.map(_.toJson),
    "expenses"                -> expenses.map(_.toJson),
    "transactions"            -> transactions.map(_.toJson),
    "debtCalculationStrategy" -> debt_strategy.toJson
  )

  def addMember(person: IPerson): IGroup
  def removeMember(person: IPerson): IGroup

  def addExpense(expense: IExpense): IGroup
  def removeExpense(expense: IExpense): IGroup

  def updateName(name: String): IGroup

  def addTransaction(transaction: ITransaction): IGroup
  def removeTransaction(transaction: ITransaction): IGroup

  def updateDebtCalculationStrategy(strategy: IDebtCalculationStrategy): IGroup

  def calculateDebt(): List[IDebt]

  def containsUser(user_name: String): Boolean

object GroupDeserializer extends Deserializer[IGroup]:
  val factory: IGroupFactory = summon[IGroupFactory]

  override def fromXml(xml: Elem): IGroup = {
    val name     = (xml \ "Name").text
    val members  = (xml \ "Members" \ "Person").map(node => PersonDeserializer.fromXml(node.asInstanceOf[Elem])).toList
    val expenses =
      (xml \ "Expenses" \ "Expense").map(node => ExpenseDeserializer.fromXml(node.asInstanceOf[Elem])).toList
    val transactions =
      (xml \ "Transactions" \ "Transaction")
        .map(node => TransactionDeserializer.fromXml(node.asInstanceOf[Elem]))
        .toList
    val debtStrategyElem = (xml \ "DebtCalculationStrategy").headOption.map(_.asInstanceOf[Elem]).getOrElse(xml)
    val debtStrategyText = (debtStrategyElem \ "DebtCalculationStrategy" \ "Type").headOption
      .map(_.text)
      .orElse((debtStrategyElem \ "Type").headOption.map(_.text))
      .getOrElse("NormalDebtStrategy")
    val debtStrategy =
      debtStrategyText match {
        case "NormalDebtStrategy"     => NormalDebtStrategy()
        case "SimplifiedDebtStrategy" => SimplifiedDebtStrategy()
        case _                        => NormalDebtStrategy()
      }
    factory(name, members, expenses, transactions, debtStrategy)
  }

  override def fromJson(json: JsObject): IGroup = {
    val name             = (json \ "name").as[String]
    val members          = (json \ "members").as[List[JsObject]].map(obj => PersonDeserializer.fromJson(obj))
    val expenses         = (json \ "expenses").as[List[JsObject]].map(obj => ExpenseDeserializer.fromJson(obj))
    val transactions     = (json \ "transactions").as[List[JsObject]].map(obj => TransactionDeserializer.fromJson(obj))
    val debtStrategyType = ((json \ "debtCalculationStrategy") \ "type").as[String]
    val debtStrategy     =
      debtStrategyType match {
        case "NormalDebtStrategy"     => NormalDebtStrategy()
        case "SimplifiedDebtStrategy" => SimplifiedDebtStrategy()
      }
    factory(name, members, expenses, transactions, debtStrategy)
  }

trait IGroupFactory:

  def apply(
    name: String,
    members: List[IPerson],
    expenses: List[IExpense],
    transactions: List[ITransaction],
    debt_strategy: IDebtCalculationStrategy
  ): IGroup
