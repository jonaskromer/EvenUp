package de.htwg.swe.evenup.model.financial.debt

import org.scalatest.wordspec.AnyWordSpec
import org.scalatest.matchers.should.Matchers
import de.htwg.swe.evenup.model.Person

class DebtSpec extends AnyWordSpec with Matchers:

  "A Debt" should {

    val alice = Person("Alice")
    val bob   = Person("Bob")

    val debt = Debt(alice, bob, 50.0)

    "store the correct fields" in {
      debt.from shouldBe alice
      debt.to shouldBe bob
      debt.amount shouldBe 50.0
    }

    "convert to string correctly" in {
      val str = debt.toString
      str should include("Alice owes 50.00 to Bob")
    }

    "update the from field correctly" in {
      val charlie = Person("Charlie")
      val updated = debt.updateFrom(charlie)
      updated.from shouldBe charlie
      updated should not be debt
    }

    "update the to field correctly" in {
      val charlie = Person("Charlie")
      val updated = debt.updateTo(charlie)
      updated.to shouldBe charlie
      updated should not be debt
    }

    "update the amount correctly" in {
      val updated = debt.updateAmount(75.0)
      updated.amount shouldBe 75.0
      updated should not be debt
    }
  }
