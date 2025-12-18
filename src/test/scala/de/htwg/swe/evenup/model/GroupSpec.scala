package de.htwg.swe.evenup.model

import org.scalatest.wordspec.AnyWordSpec
import de.htwg.swe.evenup.model.financial.{Expense, Transaction}
import de.htwg.swe.evenup.model.financial.debt.NormalDebtStrategy
import org.scalatest.matchers.should.Matchers
import de.htwg.swe.evenup.model.financial.Share
import de.htwg.swe.evenup.model.DateComponent.BaseDateImpl.Date

class GroupSpec extends AnyWordSpec with Matchers:

  "The String of a group should print as follows" in:
    val p_1    = Person("John")
    val p_2    = Person("Peter")
    val date   = Date(1, 1, 2000)
    val shares = List(Share(p_1, 10.00), Share(p_2, 15.00))
    val e_1    = Expense("Groceries", 25.00, date, p_1, shares)
    val g_1    = Group("Weekend Trip", List(p_1, p_2), List(e_1), List(), NormalDebtStrategy())
    g_1.toString() shouldBe
      "The group Weekend Trip has the following users and expenses.\nUsers: John, Peter\nExpenses:\nJohn paid 25.00€ for Groceries on 01.01.2000. John owes 10.00€, Peter owes 15.00€."

  "When adding a new member to the group it should include the new member" in:
    val p_1 = Person("John")
    val p_2 = Person("Peter")
    val p_3 = Person("Frank")
    val g_1 = Group("Weekend Trip", List(p_1, p_2), List(), List(), NormalDebtStrategy())
    val g_2 = g_1.addMember(p_3)
    g_2.members should contain allOf (p_1, p_2, p_3)

  "When adding a member that already exists it should not duplicate" in:
    val p_1 = Person("John")
    val p_2 = Person("Peter")
    val g_1 = Group("Weekend Trip", List(p_1, p_2), List(), List(), NormalDebtStrategy())
    val g_2 = g_1.addMember(p_2)
    g_2.members shouldBe List(p_1, p_2)

  "When removing a member that exists it should no longer be in the group" in:
    val p_1 = Person("John")
    val p_2 = Person("Peter")
    val g_1 = Group("Weekend Trip", List(p_1, p_2), List(), List(), NormalDebtStrategy())
    val g_2 = g_1.removeMember(p_2)
    g_2.members shouldBe List(p_1)

  "When removing a member that does not exist it should stay the same" in:
    val p_1 = Person("John")
    val p_2 = Person("Peter")
    val p_3 = Person("Frank")
    val g_1 = Group("Weekend Trip", List(p_1, p_2), List(), List(), NormalDebtStrategy())
    val g_2 = g_1.removeMember(p_3)
    g_2 shouldBe g_1

  "When adding an expense it should be included in the list of expenses" in:
    val p_1    = Person("John")
    val date   = Date(1, 1, 2000)
    val shares = List(Share(p_1, 25.00))
    val e_1    = Expense("Groceries", 25.00, date, p_1, shares)
    val g_1    = Group("Weekend Trip", List(p_1), List(), List(), NormalDebtStrategy())
    val g_2    = g_1.addExpense(e_1)
    g_2.expenses should contain(e_1)

  "When removing an expense that exists it should no longer be in the list" in:
    val p_1    = Person("John")
    val date   = Date(1, 1, 2000)
    val shares = List(Share(p_1, 25.00))
    val e_1    = Expense("Groceries", 25.00, date, p_1, shares)
    val g_1    = Group("Weekend Trip", List(p_1), List(e_1), List(), NormalDebtStrategy())
    val g_2    = g_1.removeExpense(e_1)
    g_2.expenses shouldBe List()

  "When removing an expense that does not exist it should stay the same" in:
    val p_1    = Person("John")
    val date   = Date(1, 1, 2000)
    val shares = List(Share(p_1, 25.00))
    val e_1    = Expense("Groceries", 25.00, date, p_1, shares)
    val e_2    = Expense("Drinks", 10.00, date, p_1, shares)
    val g_1    = Group("Weekend Trip", List(p_1), List(e_1), List(), NormalDebtStrategy())
    val g_2    = g_1.removeExpense(e_2)
    g_2 shouldBe g_1

  "When updating the name of a group it should update correctly" in:
    val p_1 = Person("John")
    val g_1 = Group("Weekend Trip", List(p_1), List(), List(), NormalDebtStrategy())
    val g_2 = g_1.updateName("Holiday")
    g_2.name shouldBe "Holiday"

  "When adding a transaction it should be included in the list of transactions" in:
    val p_1  = Person("John")
    val p_2  = Person("Peter")
    val date = Date(1, 1, 2000)
    val t_1  = Transaction(p_1, p_2, 10.00, date)
    val g_1  = Group("Weekend Trip", List(p_1, p_2), List(), List(), NormalDebtStrategy())
    val g_2  = g_1.addTransaction(t_1)
    g_2.transactions should contain(t_1)

  "When removing a transaction that exists it should no longer be in the list" in:
    val p_1  = Person("John")
    val p_2  = Person("Peter")
    val date = Date(1, 1, 2000)
    val t_1  = Transaction(p_1, p_2, 10.00, date)
    val g_1  = Group("Weekend Trip", List(p_1, p_2), List(), List(t_1), NormalDebtStrategy())
    val g_2  = g_1.removeTransaction(t_1)
    g_2.transactions shouldBe List()

  "When removing a transaction that does not exist it should stay the same" in:
    val p_1  = Person("John")
    val p_2  = Person("Peter")
    val date = Date(1, 1, 2000)
    val t_1  = Transaction(p_1, p_2, 10.00, date)
    val t_2  = Transaction(p_2, p_1, 5.00, date)
    val g_1  = Group("Weekend Trip", List(p_1, p_2), List(), List(t_1), NormalDebtStrategy())
    val g_2  = g_1.removeTransaction(t_2)
    g_2 shouldBe g_1

  "A group with no expenses should display correctly" in:
    val p_1 = Person("John")
    val g_1 = Group("Weekend Trip", List(p_1), List(), List(), NormalDebtStrategy())
    g_1.toString() shouldBe "The group Weekend Trip has the following users and expenses.\nUsers: John\nExpenses:\n"

  "A group should be created with correct transactions list" in:
    val p_1  = Person("John")
    val p_2  = Person("Peter")
    val date = Date(1, 1, 2000)
    val t_1  = Transaction(p_1, p_2, 10.00, date)
    val g_1  = Group("Weekend Trip", List(p_1, p_2), List(), List(t_1), NormalDebtStrategy())
    g_1.transactions shouldBe List(t_1)
