package de.htwg.swe.evenup.model.financial.debt

import org.scalatest.wordspec.AnyWordSpec
import org.scalatest.matchers.should.Matchers
import de.htwg.swe.evenup.model.{Date, Group, Person}
import de.htwg.swe.evenup.model.financial.Share
import de.htwg.swe.evenup.model.financial.Expense

class SimplifiedDebtStrategySpec extends AnyWordSpec with Matchers:

  "A SimplifiedDebtStrategy" should {

    val alice   = Person("Alice")
    val bob     = Person("Bob")
    val charlie = Person("Charlie")

    val expense1 = Expense("Expense1", 20.0, Date(1, 1, 2025), alice, List(Share(bob, 20.0)))
    val expense2 = Expense("Expense2", 20.0, Date(2, 1, 2025), bob, List(Share(charlie, 20.0)))

    val group = Group("Trip", List(alice, bob, charlie), List(expense1, expense2), List(), SimplifiedDebtStrategy())

    "calculate debts correctly" in {
      val strategy = group.debt_strategy
      val debts    = strategy.calculateDebts(group)

      debts should contain(Debt(from = charlie, to = alice, amount = 20.0))
      debts should have size 1
    }

    "calculate no debts if all balances cancel out" in {
      val expense3      = Expense("Expense3", 10.0, Date(3, 1, 2025), alice, List(Share(bob, 10.0)))
      val expense4      = Expense("Expense4", 10.0, Date(4, 1, 2025), bob, List(Share(alice, 10.0)))
      val balancedGroup = Group(
        "Balanced",
        List(alice, bob),
        List(expense3, expense4),
        List(),
        SimplifiedDebtStrategy()
      )

      val debts = balancedGroup.debt_strategy.calculateDebts(balancedGroup)
      debts shouldBe empty
    }

    "invert debt when netAmount is negative" in {
      val alice = Person("Alice")
      val bob   = Person("Bob")

      val expense1 = Expense("Expense1", 10.0, Date(1, 1, 2025), alice, List(Share(bob, 10.0)))
      val expense2 = Expense("Expense2", 20.0, Date(2, 1, 2025), bob, List(Share(alice, 20.0)))

      val group = Group("TestGroup", List(alice, bob), List(expense1, expense2), List(), SimplifiedDebtStrategy())

      val strategy = group.debt_strategy
      val debts    = strategy.calculateDebts(group)

      debts should have size 1
      val debt = debts.head

      debt.from shouldBe alice
      debt.to shouldBe bob
      debt.amount shouldBe 10.0
    }
    
    "handle multiple creditors and debtors with exact settlements" in {
      val alice = Person("Alice")
      val bob = Person("Bob")
      val charlie = Person("Charlie")
      val dave = Person("Dave")
      
      val expense1 = Expense("Expense1", 100.0, Date(1, 1, 2025), alice, List(Share(bob, 50.0), Share(charlie, 50.0)))
      val expense2 = Expense("Expense2", 100.0, Date(2, 1, 2025), dave, List(Share(bob, 50.0), Share(charlie, 50.0)))
      
      val group = Group(
        "MultiParty",
        List(alice, bob, charlie, dave),
        List(expense1, expense2),
        List(),
        SimplifiedDebtStrategy()
      )
      
      val debts = group.debt_strategy.calculateDebts(group)
      
      debts should have size 2
      debts.map(_.amount).sum shouldBe 200.0
    }
    
    "handle case where creditor amount becomes less than 0.01" in {
      val alice = Person("Alice")
      val bob = Person("Bob")
      val charlie = Person("Charlie")
      
      val expense1 = Expense("Expense1", 10.0, Date(1, 1, 2025), alice, List(Share(bob, 10.0)))
      val expense2 = Expense("Expense2", 5.0, Date(2, 1, 2025), charlie, List(Share(bob, 5.0)))
      
      val group = Group(
        "TestGroup",
        List(alice, bob, charlie),
        List(expense1, expense2),
        List(),
        SimplifiedDebtStrategy()
      )
      
      val debts = group.debt_strategy.calculateDebts(group)
      
      debts should have size 2
      debts.map(_.from).toSet should contain(bob)
    }
    
    "handle case where debtor amount becomes greater than -0.01" in {
      val alice = Person("Alice")
      val bob = Person("Bob")
      val charlie = Person("Charlie")
      val dave = Person("Dave")
      
      val expense1 = Expense("Expense1", 30.0, Date(1, 1, 2025), alice, List(Share(bob, 15.0), Share(charlie, 15.0)))
      val expense2 = Expense("Expense2", 20.0, Date(2, 1, 2025), dave, List(Share(bob, 20.0)))
      
      val group = Group(
        "TestGroup",
        List(alice, bob, charlie, dave),
        List(expense1, expense2),
        List(),
        SimplifiedDebtStrategy()
      )
      
      val debts = group.debt_strategy.calculateDebts(group)
      
      debts.map(_.amount).sum shouldBe 50.0
    }
    
    "handle complex scenario with multiple settlements" in {
      val alice = Person("Alice")
      val bob = Person("Bob")
      val charlie = Person("Charlie")
      val dave = Person("Dave")
      val eve = Person("Eve")
      
      val expense1 = Expense("Expense1", 100.0, Date(1, 1, 2025), alice, List(Share(bob, 50.0), Share(charlie, 25.0), Share(dave, 25.0)))
      val expense2 = Expense("Expense2", 50.0, Date(2, 1, 2025), eve, List(Share(bob, 25.0), Share(charlie, 25.0)))
      
      val group = Group(
        "Complex",
        List(alice, bob, charlie, dave, eve),
        List(expense1, expense2),
        List(),
        SimplifiedDebtStrategy()
      )
      
      val debts = group.debt_strategy.calculateDebts(group)
      
      debts should not be empty
      val totalDebt = debts.map(_.amount).sum
      totalDebt shouldBe 150.0 +- 0.02
    }
  }