package de.htwg.swe.evenup.model.financial.debt

import org.scalatest.wordspec.AnyWordSpec
import org.scalatest.matchers.should.Matchers
import de.htwg.swe.evenup.model.{Person, Date, Share, Group}
import de.htwg.swe.evenup.model.financial.Expense

class NormalDebtStrategySpec extends AnyWordSpec with Matchers:

  "A NormalDebtStrategy" should {

    val alice = Person("Alice")
    val bob = Person("Bob")
    val charlie = Person("Charlie")

    // Expenses setup
    val expense1 = Expense("Expense1", 20.0, Date(1,1,2025), alice, List(Share(bob, 20.0)))
    val expense2 = Expense("Expense2", 20.0, Date(2,1,2025), bob, List(Share(charlie, 20.0)))

    val group = Group("Trip", List(alice, bob, charlie), List(expense1, expense2), List(), NormalDebtStrategy())

    "calculate debts correctly" in {
      val strategy = group.debtstrategy
      val debts = strategy.calculateDebts(group)

      // Debts according to NormalDebtStrategy (direct debts, no simplification)
      debts should contain(Debt(from = bob, to = alice, amount = 20.0))
      debts should contain(Debt(from = charlie, to = bob, amount = 20.0))
      debts should have size 2
    }

    "calculate no debts if all balances cancel out" in {
      val expense3 = Expense("Expense3", 10.0, Date(3,1,2025), alice, List(Share(bob, 10.0)))
      val expense4 = Expense("Expense4", 10.0, Date(4,1,2025), bob, List(Share(alice, 10.0)))
      val balancedGroup = Group("Balanced", List(alice, bob), List(expense3, expense4), List(), NormalDebtStrategy())

      val debts = balancedGroup.debtstrategy.calculateDebts(balancedGroup)
      debts shouldBe empty
    }

    "invert debt when netAmount is negative" in {
      val alice = Person("Alice")
      val bob   = Person("Bob")

      // Alice owes Bob 10, Bob owes Alice 20 => netAmount = -10, should invert
      val expense1 = Expense("Expense1", 10.0, Date(1,1,2025), alice, List(Share(bob, 10.0)))
      val expense2 = Expense("Expense2", 20.0, Date(2,1,2025), bob, List(Share(alice, 20.0)))

      val group = Group("TestGroup", List(alice, bob), List(expense1, expense2), List(), NormalDebtStrategy())

      val strategy = group.debtstrategy
      val debts = strategy.calculateDebts(group)

      debts should have size 1
      val debt = debts.head

      debt.from shouldBe alice
      debt.to shouldBe bob
      debt.amount shouldBe 10.0
    }
  }
