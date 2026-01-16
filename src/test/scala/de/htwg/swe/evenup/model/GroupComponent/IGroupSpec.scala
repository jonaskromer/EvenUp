package de.htwg.swe.evenup.model.GroupComponent

import org.scalatest._
import org.scalatest.matchers.should.Matchers
import org.scalatest.wordspec.AnyWordSpec
import de.htwg.swe.evenup.model.PersonComponent.BasePersonImpl.Person
import de.htwg.swe.evenup.model.DateComponent.BaseDateImpl.Date
import de.htwg.swe.evenup.model.financial.ShareComponent.BaseShareImpl.Share
import de.htwg.swe.evenup.model.financial.ExpenseComponent.BaseExpenseImpl.Expense
import de.htwg.swe.evenup.model.financial.TransactionComponent.BaseTransactionImpl.Transaction
import de.htwg.swe.evenup.model.financial.DebtComponent.BaseDebtImpl.NormalDebtStrategy
import de.htwg.swe.evenup.model.GroupComponent.BaseGroupImpl.Group
import play.api.libs.json.Json

class IGroupSpec extends AnyWordSpec with Matchers:

  val alice    = Person("Alice")
  val bob      = Person("Bob")
  val strategy = NormalDebtStrategy()
  val date     = Date(15, 6, 2025)

  "IGroup" should {

    "serialize to XML correctly" in:
      val group = Group("Trip", List(alice, bob), Nil, Nil, strategy)
      val xml   = group.toXml
      xml.label shouldBe "Group"
      (xml \ "Name").text shouldBe "Trip"
      (xml \\ "Person" \\ "Name").map(_.text) should contain allOf ("Alice", "Bob")

    "serialize to JSON correctly" in:
      val group = Group("Vacation", List(alice), Nil, Nil, strategy)
      val json  = group.toJson
      (json \ "name").as[String] shouldBe "Vacation"
      (json \ "members").as[Seq[play.api.libs.json.JsObject]].length shouldBe 1

    "serialize expenses to XML" in:
      val expense = Expense("Dinner", 30.0, date, alice, List(Share(bob, 15.0)))
      val group   = Group("Trip", List(alice, bob), List(expense), Nil, strategy)
      val xml     = group.toXml
      (xml \ "Expenses" \ "Expense" \ "Name").text shouldBe "Dinner"

    "serialize expenses to JSON" in:
      val expense = Expense("Lunch", 20.0, date, alice, List(Share(bob, 10.0)))
      val group   = Group("Trip", List(alice, bob), List(expense), Nil, strategy)
      val json    = group.toJson
      val expenses = (json \ "expenses").as[Seq[play.api.libs.json.JsObject]]
      expenses.length shouldBe 1
      (expenses.head \ "name").as[String] shouldBe "Lunch"

    "serialize transactions to XML" in:
      val transaction = Transaction(alice, bob, 50.0, date)
      val group       = Group("Trip", List(alice, bob), Nil, List(transaction), strategy)
      val xml         = group.toXml
      (xml \ "Transactions" \ "Transaction" \ "Amount").text shouldBe "50.0"

    "serialize transactions to JSON" in:
      val transaction = Transaction(alice, bob, 75.0, date)
      val group       = Group("Trip", List(alice, bob), Nil, List(transaction), strategy)
      val json        = group.toJson
      val transactions = (json \ "transactions").as[Seq[play.api.libs.json.JsObject]]
      transactions.length shouldBe 1
      (transactions.head \ "amount").as[Double] shouldBe 75.0

    "serialize debt strategy to XML" in:
      val group = Group("Trip", List(alice), Nil, Nil, strategy)
      val xml   = group.toXml
      (xml \ "DebtCalculationStrategy" \ "DebtCalculationStrategy" \ "Type").text should include("NormalDebtStrategy")

    "serialize debt strategy to JSON" in:
      val group = Group("Trip", List(alice), Nil, Nil, strategy)
      val json  = group.toJson
      (json \ "debtCalculationStrategy" \ "type").as[String] should include("NormalDebtStrategy")
  }

  "GroupDeserializer" should {

    "deserialize from JSON correctly" in:
      val json = Json.obj(
        "name"    -> "Party",
        "members" -> Json.arr(Json.obj("name" -> "Charlie")),
        "expenses" -> Json.arr(),
        "transactions" -> Json.arr(),
        "debtCalculationStrategy" -> Json.obj("type" -> "NormalDebtStrategy")
      )
      val group = GroupDeserializer.fromJson(json)
      group.name shouldBe "Party"
      group.members.head.name shouldBe "Charlie"

    "roundtrip JSON serialization correctly" in:
      val expense  = Expense("Coffee", 10.0, date, alice, List(Share(bob, 5.0)))
      val original = Group("Friends", List(alice, bob), List(expense), Nil, strategy)
      val json     = original.toJson
      val deserialized = GroupDeserializer.fromJson(json)
      deserialized.name shouldBe original.name
      deserialized.members.length shouldBe original.members.length
      deserialized.expenses.length shouldBe original.expenses.length
  }
