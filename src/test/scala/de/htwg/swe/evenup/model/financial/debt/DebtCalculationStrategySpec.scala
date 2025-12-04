package de.htwg.swe.evenup.model.financial.debt

import org.scalatest.wordspec.AnyWordSpec
import org.scalatest.matchers.should.Matchers
import de.htwg.swe.evenup.model.{Date, Group, Person}
import de.htwg.swe.evenup.model.financial.Share
import de.htwg.swe.evenup.model.financial.Expense

class DebtCalculationStrategySpec extends AnyWordSpec with Matchers:

  "A DebtCalculationStrategy" should {

    val alice   = Person("Alice")
    val bob     = Person("Bob")
    val charlie = Person("Charlie")

    val expense1 = Expense("Lunch", 30.0, Date(1, 1, 2025), alice, List(Share(alice, 10.0), Share(bob, 20.0)))
    val expense2 = Expense(
      "Dinner",
      60.0,
      Date(2, 1, 2025),
      bob,
      List(Share(alice, 20.0), Share(bob, 20.0), Share(charlie, 20.0))
    )

    val group = Group(
      "Trip",
      List(alice, bob, charlie),
      List(expense1, expense2),
      List(),
      new DebtCalculationStrategy {
        override def calculateDebts(group: Group): List[Debt] = List()
      }
    )

    "calculate balances correctly" in {
      val strategy = group.debt_strategy
      val balances = strategy.calculateBalances(group)

      balances(alice) shouldBe 0.0     // Alice paid 30, owes 10+20=30
      balances(bob) shouldBe 20.0      // Bob paid 60, owes 20+20=40, net 20
      balances(charlie) shouldBe -20.0 // Charlie paid 0, owes 20
    }
  }
