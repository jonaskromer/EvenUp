package de.htwg.swe.evenup.model.financial.ExpenseComponent

import org.scalatest._
import org.scalatest.matchers.should.Matchers
import org.scalatest.wordspec.AnyWordSpec
import de.htwg.swe.evenup.model.PersonComponent.BasePersonImpl.Person
import de.htwg.swe.evenup.model.DateComponent.BaseDateImpl.Date
import de.htwg.swe.evenup.model.financial.ShareComponent.BaseShareImpl.Share
import de.htwg.swe.evenup.model.financial.ExpenseComponent.BaseExpenseImpl.Expense
import play.api.libs.json.Json

class IExpenseSpec extends AnyWordSpec with Matchers:

  "IExpense" should {

    val alice   = Person("Alice")
    val bob     = Person("Bob")
    val date    = Date(15, 6, 2025)
    val shares  = List(Share(bob, 15.0))
    val expense = Expense("Dinner", 30.0, date, alice, shares)

    "serialize to XML correctly" in:
      val xml = expense.toXml
      xml.label shouldBe "Expense"
      (xml \ "Name").text shouldBe "Dinner"
      (xml \ "Amount").text shouldBe "30.0"
      (xml \ "PaidBy" \ "Person" \ "Name").text shouldBe "Alice"

    "serialize to JSON correctly" in:
      val json = expense.toJson
      (json \ "name").as[String] shouldBe "Dinner"
      (json \ "amount").as[Double] shouldBe 30.0
      (json \ "paidBy" \ "name").as[String] shouldBe "Alice"
  }

  "ExpenseDeserializer" should {

    "deserialize from XML correctly" in:
      val xml =
        <Expense>
        <Name>Lunch</Name>
        <Amount>20.0</Amount>
        <Date><Date><Day>10</Day><Month>5</Month><Year>2025</Year></Date></Date>
        <PaidBy><Person><Name>Charlie</Name></Person></PaidBy>
        <Shares>
          <Share>
            <Person><Person><Name>David</Name></Person></Person>
            <Amount>10.0</Amount>
          </Share>
        </Shares>
      </Expense>
      val expense = ExpenseDeserializer.fromXml(xml)
      expense.name shouldBe "Lunch"
      expense.amount shouldBe 20.0
      expense.paid_by.name shouldBe "Charlie"
      expense.shares.length shouldBe 1

    "deserialize from JSON correctly" in:
      val json = Json.obj(
        "name"   -> "Coffee",
        "amount" -> 5.0,
        "date"   -> Json.obj("day" -> 1, "month" -> 1, "year" -> 2025),
        "paidBy" -> Json.obj("name" -> "Eve"),
        "shares" -> Json.arr(
          Json.obj("person" -> Json.obj("name" -> "Frank"), "amount" -> 2.50)
        )
      )
      val expense = ExpenseDeserializer.fromJson(json)
      expense.name shouldBe "Coffee"
      expense.amount shouldBe 5.0
      expense.paid_by.name shouldBe "Eve"
      expense.shares.head.person.name shouldBe "Frank"

    "roundtrip XML serialization correctly" in:
      val original = Expense("Groceries", 50.0, Date(20, 8, 2025), Person("Grace"), List(Share(Person("Henry"), 25.0)))
      val xml      = original.toXml
      val deserialized = ExpenseDeserializer.fromXml(xml)
      deserialized.name shouldBe original.name
      deserialized.amount shouldBe original.amount
      deserialized.paid_by.name shouldBe original.paid_by.name

    "roundtrip JSON serialization correctly" in:
      val original = Expense("Tickets", 100.0, Date(15, 12, 2024), Person("Ivan"), List(Share(Person("Julia"), 50.0)))
      val json     = original.toJson
      val deserialized = ExpenseDeserializer.fromJson(json)
      deserialized.name shouldBe original.name
      deserialized.amount shouldBe original.amount
      deserialized.paid_by.name shouldBe original.paid_by.name
  }
