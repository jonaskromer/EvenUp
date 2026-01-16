package de.htwg.swe.evenup.model.financial.DebtComponent

import org.scalatest._
import org.scalatest.matchers.should.Matchers
import org.scalatest.wordspec.AnyWordSpec
import de.htwg.swe.evenup.model.PersonComponent.BasePersonImpl.Person
import de.htwg.swe.evenup.model.DateComponent.BaseDateImpl.Date
import de.htwg.swe.evenup.model.financial.ShareComponent.BaseShareImpl.Share
import de.htwg.swe.evenup.model.financial.ExpenseComponent.BaseExpenseImpl.Expense
import de.htwg.swe.evenup.model.financial.DebtComponent.BaseDebtImpl.NormalDebtStrategy
import de.htwg.swe.evenup.model.financial.DebtComponent.BaseDebtImpl.SimplifiedDebtStrategy
import de.htwg.swe.evenup.model.GroupComponent.BaseGroupImpl.Group

class DebtCalculationStrategySpec extends AnyWordSpec with Matchers:

  val alice   = Person("Alice")
  val bob     = Person("Bob")
  val charlie = Person("Charlie")
  val date    = Date(15, 6, 2025)

  "NormalDebtStrategy" should {

    "return 'normal' as toString" in:
      val strategy = NormalDebtStrategy()
      strategy.toString() shouldBe "normal"

    "calculate no debts when there are no expenses" in:
      val strategy = NormalDebtStrategy()
      val group    = Group("Trip", List(alice, bob), Nil, Nil, strategy)
      val debts    = strategy.calculateDebts(group)
      debts shouldBe empty

    "calculate debts for a single expense" in:
      val strategy = NormalDebtStrategy()
      val expense  = Expense("Dinner", 30.0, date, alice, List(Share(bob, 15.0), Share(charlie, 15.0)))
      val group    = Group("Trip", List(alice, bob, charlie), List(expense), Nil, strategy)
      val debts    = strategy.calculateDebts(group)
      debts.length shouldBe 2

    "net debts between two people" in:
      val strategy = NormalDebtStrategy()
      val expense1 = Expense("Dinner", 20.0, date, alice, List(Share(bob, 10.0)))
      val expense2 = Expense("Lunch", 10.0, date, bob, List(Share(alice, 5.0)))
      val group    = Group("Trip", List(alice, bob), List(expense1, expense2), Nil, strategy)
      val debts    = strategy.calculateDebts(group)

      debts.length shouldBe 1
      val debt = debts.head
      debt.amount shouldBe 5.0

    "not create debt when expense payer is the share owner" in:
      val strategy = NormalDebtStrategy()
      val expense  = Expense("Solo", 10.0, date, alice, List(Share(alice, 10.0)))
      val group    = Group("Trip", List(alice), List(expense), Nil, strategy)
      val debts    = strategy.calculateDebts(group)
      debts shouldBe empty
  }

  "SimplifiedDebtStrategy" should {

    "return 'simplified' as toString" in:
      val strategy = SimplifiedDebtStrategy()
      strategy.toString() shouldBe "simplified"

    "calculate no debts when there are no expenses" in:
      val strategy = SimplifiedDebtStrategy()
      val group    = Group("Trip", List(alice, bob), Nil, Nil, strategy)
      val debts    = strategy.calculateDebts(group)
      debts shouldBe empty

    "calculate debts for a single expense" in:
      val strategy = SimplifiedDebtStrategy()
      val expense  = Expense("Dinner", 30.0, date, alice, List(Share(bob, 15.0), Share(charlie, 15.0)))
      val group    = Group("Trip", List(alice, bob, charlie), List(expense), Nil, strategy)
      val debts    = strategy.calculateDebts(group)
      debts should not be empty

    "simplify chain debts" in:
      // Alice pays for Bob (Bob owes Alice 10)
      // Bob pays for Charlie (Charlie owes Bob 10)
      // Simplified: Charlie owes Alice 10
      val strategy = SimplifiedDebtStrategy()
      val expense1 = Expense("E1", 10.0, date, alice, List(Share(bob, 10.0)))
      val expense2 = Expense("E2", 10.0, date, bob, List(Share(charlie, 10.0)))
      val group    = Group("Trip", List(alice, bob, charlie), List(expense1, expense2), Nil, strategy)
      val debts    = strategy.calculateDebts(group)

      // Should result in fewer transactions than normal strategy
      debts.length should be <= 2

    "minimize number of transactions" in:
      val strategy = SimplifiedDebtStrategy()
      // Complex scenario with multiple expenses
      val expense1 = Expense("E1", 30.0, date, alice, List(Share(bob, 10.0), Share(charlie, 10.0)))
      val expense2 = Expense("E2", 20.0, date, bob, List(Share(alice, 10.0), Share(charlie, 10.0)))
      val group    = Group("Trip", List(alice, bob, charlie), List(expense1, expense2), Nil, strategy)
      val debts    = strategy.calculateDebts(group)

      // Simplified strategy should produce minimal transactions
      debts.length should be <= 3
  }
