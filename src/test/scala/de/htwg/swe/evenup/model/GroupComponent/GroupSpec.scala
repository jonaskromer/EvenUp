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
import de.htwg.swe.evenup.model.financial.DebtComponent.BaseDebtImpl.SimplifiedDebtStrategy
import de.htwg.swe.evenup.model.GroupComponent.BaseGroupImpl.Group
import de.htwg.swe.evenup.model.GroupComponent.BaseGroupImpl.GroupFactory

class GroupSpec extends AnyWordSpec with Matchers:

  "A Group" should {

    val alice    = Person("Alice")
    val bob      = Person("Bob")
    val charlie  = Person("Charlie")
    val strategy = NormalDebtStrategy()

    "store all fields correctly" in:
      val group = Group("Trip", List(alice, bob), Nil, Nil, strategy)
      group.name shouldBe "Trip"
      group.members shouldBe List(alice, bob)
      group.expenses shouldBe Nil
      group.transactions shouldBe Nil

    "add a member" in:
      val group   = Group("Trip", List(alice), Nil, Nil, strategy)
      val updated = group.addMember(bob)
      updated.members should contain(bob)
      updated.members.length shouldBe 2

    "not add a duplicate member" in:
      val group   = Group("Trip", List(alice, bob), Nil, Nil, strategy)
      val updated = group.addMember(alice)
      updated.members.length shouldBe 2

    "remove a member" in:
      val group   = Group("Trip", List(alice, bob), Nil, Nil, strategy)
      val updated = group.removeMember(bob)
      updated.members should not contain bob
      updated.members.length shouldBe 1

    "not fail when removing a non-existent member" in:
      val group   = Group("Trip", List(alice), Nil, Nil, strategy)
      val updated = group.removeMember(bob)
      updated.members shouldBe List(alice)

    "add an expense" in:
      val date    = Date(15, 6, 2025)
      val expense = Expense("Dinner", 30.0, date, alice, List(Share(bob, 15.0)))
      val group   = Group("Trip", List(alice, bob), Nil, Nil, strategy)
      val updated = group.addExpense(expense)
      updated.expenses should contain(expense)

    "remove an expense" in:
      val date    = Date(15, 6, 2025)
      val expense = Expense("Dinner", 30.0, date, alice, List(Share(bob, 15.0)))
      val group   = Group("Trip", List(alice, bob), List(expense), Nil, strategy)
      val updated = group.removeExpense(expense)
      updated.expenses should not contain expense

    "not fail when removing a non-existent expense" in:
      val date     = Date(15, 6, 2025)
      val expense1 = Expense("Dinner", 30.0, date, alice, List(Share(bob, 15.0)))
      val expense2 = Expense("Lunch", 20.0, date, alice, List(Share(bob, 10.0)))
      val group    = Group("Trip", List(alice, bob), List(expense1), Nil, strategy)
      val updated  = group.removeExpense(expense2)
      updated.expenses shouldBe List(expense1)

    "update name" in:
      val group   = Group("Trip", List(alice), Nil, Nil, strategy)
      val updated = group.updateName("Vacation")
      updated.name shouldBe "Vacation"

    "add a transaction" in:
      val date        = Date(15, 6, 2025)
      val transaction = Transaction(alice, bob, 50.0, date)
      val group       = Group("Trip", List(alice, bob), Nil, Nil, strategy)
      val updated     = group.addTransaction(transaction)
      updated.transactions should contain(transaction)

    "remove a transaction" in:
      val date        = Date(15, 6, 2025)
      val transaction = Transaction(alice, bob, 50.0, date)
      val group       = Group("Trip", List(alice, bob), Nil, List(transaction), strategy)
      val updated     = group.removeTransaction(transaction)
      updated.transactions should not contain transaction

    "not fail when removing a non-existent transaction" in:
      val date         = Date(15, 6, 2025)
      val transaction1 = Transaction(alice, bob, 50.0, date)
      val transaction2 = Transaction(bob, alice, 25.0, date)
      val group        = Group("Trip", List(alice, bob), Nil, List(transaction1), strategy)
      val updated      = group.removeTransaction(transaction2)
      updated.transactions shouldBe List(transaction1)

    "update debt calculation strategy" in:
      val group       = Group("Trip", List(alice), Nil, Nil, strategy)
      val newStrategy = SimplifiedDebtStrategy()
      val updated     = group.updateDebtCalculationStrategy(newStrategy)
      updated.debt_strategy shouldBe a[SimplifiedDebtStrategy]

    "check if contains user by name" in:
      val group = Group("Trip", List(alice, bob), Nil, Nil, strategy)
      group.containsUser("Alice") shouldBe true
      group.containsUser("Charlie") shouldBe false

    "format toString correctly" in:
      val group = Group("Trip", List(alice, bob), Nil, Nil, strategy)
      group.toString() should include("Trip")
      group.toString() should include("Alice")
      group.toString() should include("Bob")
  }

  "GroupFactory" should {

    "create a Group with the given values" in:
      val alice    = Person("Alice")
      val strategy = NormalDebtStrategy()
      val group    = GroupFactory("Test Group", List(alice), Nil, Nil, strategy)
      group.name shouldBe "Test Group"
      group.members shouldBe List(alice)
      group shouldBe a[IGroup]
  }
