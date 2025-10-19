package de.htwg.swe.evenup

import org.scalatest._
import org.scalatest.matchers.should.Matchers
import org.scalatest.wordspec.AnyWordSpec

import de.htwg.swe.evenup._

class ExpenseSpec extends AnyWordSpec with Matchers:

  "The String of an expense should print as follows" in:
    val p_1 = Person("John")
    val p_2 = Person("Peter")
    val p_3 = Person("Frank")
    val e_1 = Expense(
      "Groceries",
      25.00,
      "01.01.2000",
      p_1,
      Map((p_1, 10.00), (p_2, 10.00), (p_3, 5.00))
    )
    e_1.toString() shouldBe "John paid 25.00€ for Groceries on 01.01.2000. John owes 10.00€, Peter owes 10.00€, Frank owes 5.00€."

  "When updating the name of an expense it should have the new value" in:
    val p_1 = Person("John")
    val p_2 = Person("Peter")
    val p_3 = Person("Frank")
    val e_1 = Expense(
      "Groceries",
      25.00,
      "01.01.2000",
      p_1,
      Map((p_1, 10.00), (p_2, 10.00), (p_3, 5.00))
    )
    val e_2 = e_1.updateName("Drinks")
    e_2.name shouldBe "Drinks"

  "When updating the amount of an expense it should update correctly" in:
    val p_1 = Person("John")
    val p_2 = Person("Peter")
    val p_3 = Person("Frank")
    val e_1 = Expense(
      "Groceries",
      25.00,
      "01.01.2000",
      p_1,
      Map((p_1, 10.00), (p_2, 10.00), (p_3, 5.00))
    )
    val e_2 = e_1.updateAmount(10.00)
    e_2.amount shouldBe 10.00

  "When updating the date of an expense it should update correctly" in:
    val p_1 = Person("John")
    val p_2 = Person("Peter")
    val p_3 = Person("Frank")
    val e_1 = Expense(
      "Groceries",
      25.00,
      "01.01.2000",
      p_1,
      Map((p_1, 10.00), (p_2, 10.00), (p_3, 5.00))
    )
    val e_2 = e_1.updateDate("10.10.2010")
    e_2.date shouldBe "10.10.2010"

  "When updating the payee of an expense it should update correctly" in:
    val p_1 = Person("John")
    val p_2 = Person("Peter")
    val p_3 = Person("Frank")
    val e_1 = Expense(
      "Groceries",
      25.00,
      "01.01.2000",
      p_1,
      Map((p_1, 10.00), (p_2, 10.00), (p_3, 5.00))
    )
    val e_2 = e_1.updatePaidBy(p_3)
    e_2.paid_by shouldBe p_3

  "When updating the shares of an expense it should update correctly" in:
    val p_1 = Person("John")
    val p_2 = Person("Peter")
    val p_3 = Person("Frank")
    val e_1 = Expense(
      "Groceries",
      25.00,
      "01.01.2000",
      p_1,
      Map((p_1, 10.00), (p_2, 10.00), (p_3, 5.00))
    )
    val e_2 = e_1.updateShares(Map((p_1, 5.00), (p_2, 5.00), (p_3, 15.00)))
    e_2.shares shouldBe Map((p_1, 5.00), (p_2, 5.00), (p_3, 15.00))
