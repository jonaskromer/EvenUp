package de.htwg.swe.evenup.model.financial

import org.scalatest._
import org.scalatest.matchers.should.Matchers
import org.scalatest.wordspec.AnyWordSpec

import de.htwg.swe.evenup.model.Person
import de.htwg.swe.evenup.model.DateComponent.BaseDateImpl.Date

class ExpenseSpec extends AnyWordSpec with Matchers:

  "The String of an expense should print as follows" in:
    val p_1    = Person("John")
    val p_2    = Person("Peter")
    val p_3    = Person("Frank")
    val date   = Date(1, 1, 2000)
    val shares = List(Share(p_1, 10.00), Share(p_2, 10.00), Share(p_3, 5.00))
    val e_1    = Expense("Groceries", 25.00, date, p_1, shares)
    e_1.toString() shouldBe "John paid 25.00€ for Groceries on 01.01.2000. John owes 10.00€, Peter owes 10.00€, Frank owes 5.00€."

  "When updating the name of an expense it should have the new value" in:
    val p_1    = Person("John")
    val p_2    = Person("Peter")
    val date   = Date(1, 1, 2000)
    val shares = List(Share(p_1, 10.00), Share(p_2, 15.00))
    val e_1    = Expense("Groceries", 25.00, date, p_1, shares)
    val e_2    = e_1.updateName("Drinks")
    e_2.name shouldBe "Drinks"

  "When updating the amount of an expense it should update correctly" in:
    val p_1    = Person("John")
    val p_2    = Person("Peter")
    val date   = Date(1, 1, 2000)
    val shares = List(Share(p_1, 10.00), Share(p_2, 15.00))
    val e_1    = Expense("Groceries", 25.00, date, p_1, shares)
    val e_2    = e_1.updateAmount(10.00)
    e_2.amount shouldBe 10.00

  "When updating the date of an expense it should update correctly" in:
    val p_1     = Person("John")
    val p_2     = Person("Peter")
    val date    = Date(1, 1, 2000)
    val newDate = Date(10, 10, 2010)
    val shares  = List(Share(p_1, 10.00), Share(p_2, 15.00))
    val e_1     = Expense("Groceries", 25.00, date, p_1, shares)
    val e_2     = e_1.updateDate(newDate)
    e_2.date shouldBe newDate

  "When updating the payee of an expense it should update correctly" in:
    val p_1    = Person("John")
    val p_2    = Person("Peter")
    val p_3    = Person("Frank")
    val date   = Date(1, 1, 2000)
    val shares = List(Share(p_1, 10.00), Share(p_2, 15.00))
    val e_1    = Expense("Groceries", 25.00, date, p_1, shares)
    val e_2    = e_1.updatePaidBy(p_3)
    e_2.paid_by shouldBe p_3

  "When updating the shares of an expense it should update correctly" in:
    val p_1       = Person("John")
    val p_2       = Person("Peter")
    val p_3       = Person("Frank")
    val date      = Date(1, 1, 2000)
    val shares    = List(Share(p_1, 10.00), Share(p_2, 15.00))
    val newShares = List(Share(p_1, 5.00), Share(p_2, 5.00), Share(p_3, 15.00))
    val e_1       = Expense("Groceries", 25.00, date, p_1, shares)
    val e_2       = e_1.updateShares(newShares)
    e_2.shares shouldBe newShares

  "An expense should be created with correct name" in:
    val p_1    = Person("John")
    val date   = Date(1, 1, 2000)
    val shares = List(Share(p_1, 25.00))
    val e_1    = Expense("Groceries", 25.00, date, p_1, shares)
    e_1.name shouldBe "Groceries"

  "An expense should be created with correct amount" in:
    val p_1    = Person("John")
    val date   = Date(1, 1, 2000)
    val shares = List(Share(p_1, 25.00))
    val e_1    = Expense("Groceries", 25.00, date, p_1, shares)
    e_1.amount shouldBe 25.00

  "An expense should be created with correct paid_by" in:
    val p_1    = Person("John")
    val date   = Date(1, 1, 2000)
    val shares = List(Share(p_1, 25.00))
    val e_1    = Expense("Groceries", 25.00, date, p_1, shares)
    e_1.paid_by shouldBe p_1

  "An expense with empty shares should display correctly" in:
    val p_1  = Person("John")
    val date = Date(1, 1, 2000)
    val e_1  = Expense("Groceries", 25.00, date, p_1, List())
    e_1.toString() shouldBe "John paid 25.00€ for Groceries on 01.01.2000. ."
