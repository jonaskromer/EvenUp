package de.htwg.swe.evenup.model.financial.ExpenseComponent

import org.scalatest._
import org.scalatest.matchers.should.Matchers
import org.scalatest.wordspec.AnyWordSpec
import de.htwg.swe.evenup.model.PersonComponent.BasePersonImpl.Person
import de.htwg.swe.evenup.model.DateComponent.BaseDateImpl.Date
import de.htwg.swe.evenup.model.DateComponent.IDate
import de.htwg.swe.evenup.model.PersonComponent.IPerson
import de.htwg.swe.evenup.model.financial.ShareComponent.IShare
import de.htwg.swe.evenup.model.financial.ShareComponent.BaseShareImpl.Share
import de.htwg.swe.evenup.model.financial.ExpenseComponent.BaseExpenseImpl.ExpenseBuilder

class IExpenseBuilderSpec extends AnyWordSpec with Matchers:

  "IExpenseBuilder" should {

    "define withName method returning IExpenseBuilder" in:
      val builder: IExpenseBuilder = ExpenseBuilder()
      val result                   = builder.withName("Test")
      result shouldBe a[IExpenseBuilder]

    "define withAmount method returning IExpenseBuilder" in:
      val builder: IExpenseBuilder = ExpenseBuilder()
      val result                   = builder.withAmount(10.0)
      result shouldBe a[IExpenseBuilder]

    "define onDate method returning IExpenseBuilder" in:
      val builder: IExpenseBuilder = ExpenseBuilder()
      val date: IDate              = Date(1, 1, 2025)
      val result                   = builder.onDate(date)
      result shouldBe a[IExpenseBuilder]

    "define paidBy method returning IExpenseBuilder" in:
      val builder: IExpenseBuilder = ExpenseBuilder()
      val person: IPerson          = Person("Test")
      val result                   = builder.paidBy(person)
      result shouldBe a[IExpenseBuilder]

    "define withShares method returning IExpenseBuilder" in:
      val builder: IExpenseBuilder = ExpenseBuilder()
      val shares: List[IShare]     = List(Share(Person("Test"), 10.0))
      val result                   = builder.withShares(shares)
      result shouldBe a[IExpenseBuilder]

    "define build method returning IExpense" in:
      val builder: IExpenseBuilder = ExpenseBuilder()
      val result                   = builder.build()
      result shouldBe a[IExpense]

    "support full builder pattern through interface" in:
      val builder: IExpenseBuilder = ExpenseBuilder()
      val expense                  = builder
        .withName("Dinner")
        .withAmount(50.0)
        .onDate(Date(15, 6, 2025))
        .paidBy(Person("Alice"))
        .withShares(List(Share(Person("Bob"), 25.0)))
        .build()

      expense.name shouldBe "Dinner"
      expense.amount shouldBe 50.0
      expense.paid_by.name shouldBe "Alice"
      expense.shares.head.person.name shouldBe "Bob"
  }
