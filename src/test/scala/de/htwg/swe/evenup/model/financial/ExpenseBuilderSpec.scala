package de.htwg.swe.evenup.model.financial

import org.scalatest.wordspec.AnyWordSpec
import org.scalatest.matchers.should.Matchers
import de.htwg.swe.evenup.model.{Person}
import de.htwg.swe.evenup.model.DateComponent.BaseDateImpl.Date

class ExpenseBuilderSpec extends AnyWordSpec with Matchers:

  "An ExpenseBuilder" should {

    val alice  = Person("Alice")
    val bob    = Person("Bob")
    val date   = Date(1, 1, 2025)
    val shares = List(Share(alice, 10.0), Share(bob, 20.0))

    "build an Expense with all fields set correctly" in {
      val expense = ExpenseBuilder()
        .withName("Lunch")
        .withAmount(30.0)
        .onDate(date)
        .paidBy(alice)
        .withShares(shares)
        .build()

      expense.name shouldBe "Lunch"
      expense.amount shouldBe 30.0
      expense.date shouldBe date
      expense.paid_by shouldBe alice
      expense.shares shouldBe shares
    }

    "allow method chaining" in {
      val builder = ExpenseBuilder()
        .withName("Dinner")
        .withAmount(50.0)
        .onDate(date)
        .paidBy(bob)
        .withShares(shares)

      val expense = builder.build()
      expense.name shouldBe "Dinner"
      expense.amount shouldBe 50.0
      expense.paid_by shouldBe bob
    }

    "use default values if not all fields are set" in {
      val expense = ExpenseBuilder().build()
      expense.name shouldBe "noexpensenameset"
      expense.amount shouldBe 0.0
      expense.date shouldBe Date(1, 1, 2000)
      expense.paid_by shouldBe Person("nopersonnameset")
      expense.shares shouldBe Nil
    }
  }
