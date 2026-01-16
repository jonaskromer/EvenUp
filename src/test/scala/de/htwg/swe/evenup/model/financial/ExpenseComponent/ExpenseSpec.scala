package de.htwg.swe.evenup.model.financial.ExpenseComponent

import org.scalatest._
import org.scalatest.matchers.should.Matchers
import org.scalatest.wordspec.AnyWordSpec
import de.htwg.swe.evenup.model.PersonComponent.BasePersonImpl.Person
import de.htwg.swe.evenup.model.DateComponent.BaseDateImpl.Date
import de.htwg.swe.evenup.model.financial.ShareComponent.BaseShareImpl.Share
import de.htwg.swe.evenup.model.financial.ExpenseComponent.BaseExpenseImpl.Expense
import de.htwg.swe.evenup.model.financial.ExpenseComponent.BaseExpenseImpl.ExpenseFactory

class ExpenseSpec extends AnyWordSpec with Matchers:

  "An Expense" should {

    val alice   = Person("Alice")
    val bob     = Person("Bob")
    val charlie = Person("Charlie")
    val date    = Date(15, 6, 2025)
    val shares  = List(Share(bob, 15.0), Share(charlie, 15.0))

    "store all fields correctly" in:
      val expense = Expense("Dinner", 30.0, date, alice, shares)
      expense.name shouldBe "Dinner"
      expense.amount shouldBe 30.0
      expense.date shouldBe date
      expense.paid_by shouldBe alice
      expense.shares shouldBe shares

    "format toString correctly" in:
      val expense = Expense("Lunch", 20.0, date, alice, List(Share(bob, 10.0)))
      expense.toString() should include("Alice paid 20.00€ for Lunch")
      expense.toString() should include("15.06.2025")
      expense.toString() should include("Bob owes 10.00€")

    "update name correctly" in:
      val expense = Expense("Dinner", 30.0, date, alice, shares)
      val updated = expense.updateName("Breakfast")
      updated.name shouldBe "Breakfast"

    "update amount correctly" in:
      val expense = Expense("Dinner", 30.0, date, alice, shares)
      val updated = expense.updateAmount(45.0)
      updated.amount shouldBe 45.0

    "update date correctly" in:
      val newDate = Date(20, 12, 2025)
      val expense = Expense("Dinner", 30.0, date, alice, shares)
      val updated = expense.updateDate(newDate)
      updated.date shouldBe newDate

    "update paid_by correctly" in:
      val expense = Expense("Dinner", 30.0, date, alice, shares)
      val updated = expense.updatePaidBy(bob)
      updated.paid_by shouldBe bob

    "update shares correctly" in:
      val newShares = List(Share(alice, 20.0))
      val expense   = Expense("Dinner", 30.0, date, alice, shares)
      val updated   = expense.updateShares(newShares)
      updated.shares shouldBe newShares

    "be equal to another expense with the same values" in:
      val expense1 = Expense("Dinner", 30.0, date, alice, shares)
      val expense2 = Expense("Dinner", 30.0, date, alice, shares)
      expense1 shouldBe expense2
  }

  "ExpenseFactory" should {

    "create an Expense with the given values" in:
      val alice   = Person("Alice")
      val bob     = Person("Bob")
      val date    = Date(15, 6, 2025)
      val shares  = List(Share(bob, 25.0))
      val expense = ExpenseFactory("Coffee", 5.0, date, alice, shares)
      expense.name shouldBe "Coffee"
      expense.amount shouldBe 5.0
      expense.date shouldBe date
      expense.paid_by shouldBe alice
      expense.shares shouldBe shares
      expense shouldBe a[IExpense]
  }
