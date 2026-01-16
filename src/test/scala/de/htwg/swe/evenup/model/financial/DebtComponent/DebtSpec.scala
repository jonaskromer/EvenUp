package de.htwg.swe.evenup.model.financial.DebtComponent

import org.scalatest._
import org.scalatest.matchers.should.Matchers
import org.scalatest.wordspec.AnyWordSpec
import de.htwg.swe.evenup.model.PersonComponent.BasePersonImpl.Person
import de.htwg.swe.evenup.model.financial.DebtComponent.BaseDebtImpl.Debt

class DebtSpec extends AnyWordSpec with Matchers:

  "A Debt" should {

    "store from, to, and amount correctly" in:
      val alice = Person("Alice")
      val bob   = Person("Bob")
      val debt  = Debt(alice, bob, 50.0)
      debt.from shouldBe alice
      debt.to shouldBe bob
      debt.amount shouldBe 50.0

    "format toString correctly" in:
      val alice = Person("Alice")
      val bob   = Person("Bob")
      val debt  = Debt(alice, bob, 25.50)
      debt.toString() shouldBe "Alice owes 25.50 to Bob."

    "update from correctly" in:
      val alice   = Person("Alice")
      val bob     = Person("Bob")
      val charlie = Person("Charlie")
      val debt    = Debt(alice, bob, 50.0)
      val updated = debt.updateFrom(charlie)
      updated.from shouldBe charlie
      updated.to shouldBe bob
      updated.amount shouldBe 50.0

    "update to correctly" in:
      val alice   = Person("Alice")
      val bob     = Person("Bob")
      val charlie = Person("Charlie")
      val debt    = Debt(alice, bob, 50.0)
      val updated = debt.updateTo(charlie)
      updated.from shouldBe alice
      updated.to shouldBe charlie
      updated.amount shouldBe 50.0

    "update amount correctly" in:
      val alice   = Person("Alice")
      val bob     = Person("Bob")
      val debt    = Debt(alice, bob, 50.0)
      val updated = debt.updateAmount(75.0)
      updated.from shouldBe alice
      updated.to shouldBe bob
      updated.amount shouldBe 75.0

    "be equal to another debt with the same values" in:
      val alice = Person("Alice")
      val bob   = Person("Bob")
      val debt1 = Debt(alice, bob, 50.0)
      val debt2 = Debt(alice, bob, 50.0)
      debt1 shouldBe debt2
  }
