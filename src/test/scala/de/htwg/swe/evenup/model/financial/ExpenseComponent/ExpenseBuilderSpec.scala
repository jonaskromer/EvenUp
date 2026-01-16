package de.htwg.swe.evenup.model.financial.ExpenseComponent

import org.scalatest._
import org.scalatest.matchers.should.Matchers
import org.scalatest.wordspec.AnyWordSpec
import de.htwg.swe.evenup.model.PersonComponent.BasePersonImpl.Person
import de.htwg.swe.evenup.model.DateComponent.BaseDateImpl.Date
import de.htwg.swe.evenup.model.financial.ShareComponent.BaseShareImpl.Share
import de.htwg.swe.evenup.model.financial.ExpenseComponent.BaseExpenseImpl.ExpenseBuilder

class ExpenseBuilderSpec extends AnyWordSpec with Matchers:

  "An ExpenseBuilder" should {

    "build an expense with all fields set" in:
      val alice  = Person("Alice")
      val bob    = Person("Bob")
      val date   = Date(15, 6, 2025)
      val shares = List(Share(bob, 25.0))

      val expense = ExpenseBuilder()
        .withName("Dinner")
        .withAmount(50.0)
        .onDate(date)
        .paidBy(alice)
        .withShares(shares)
        .build()

      expense.name shouldBe "Dinner"
      expense.amount shouldBe 50.0
      expense.date shouldBe date
      expense.paid_by shouldBe alice
      expense.shares shouldBe shares

    "build an expense with default values when not set" in:
      val expense = ExpenseBuilder().build()
      expense.name shouldBe "noexpensenameset"
      expense.amount shouldBe 0.0

    "support fluent interface" in:
      val builder = ExpenseBuilder()
      builder.withName("Test") shouldBe a[IExpenseBuilder]
      builder.withAmount(10.0) shouldBe a[IExpenseBuilder]
      builder.onDate(Date(1, 1, 2025)) shouldBe a[IExpenseBuilder]
      builder.paidBy(Person("Test")) shouldBe a[IExpenseBuilder]
      builder.withShares(Nil) shouldBe a[IExpenseBuilder]

    "allow chaining all methods" in:
      val alice   = Person("Alice")
      val bob     = Person("Bob")
      val date    = Date(10, 10, 2025)
      val shares  = List(Share(bob, 30.0))
      val expense = ExpenseBuilder()
        .withName("Groceries")
        .withAmount(60.0)
        .onDate(date)
        .paidBy(alice)
        .withShares(shares)
        .build()

      expense.name shouldBe "Groceries"
      expense.amount shouldBe 60.0
  }
