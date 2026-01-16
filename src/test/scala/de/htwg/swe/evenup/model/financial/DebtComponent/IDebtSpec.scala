package de.htwg.swe.evenup.model.financial.DebtComponent

import org.scalatest._
import org.scalatest.matchers.should.Matchers
import org.scalatest.wordspec.AnyWordSpec
import de.htwg.swe.evenup.model.PersonComponent.BasePersonImpl.Person
import de.htwg.swe.evenup.model.financial.DebtComponent.BaseDebtImpl.Debt

class IDebtSpec extends AnyWordSpec with Matchers:

  "IDebt" should {

    val alice = Person("Alice")
    val bob   = Person("Bob")
    val debt  = Debt(alice, bob, 50.0)

    "have a from property" in:
      val d: IDebt = debt
      d.from shouldBe alice

    "have a to property" in:
      val d: IDebt = debt
      d.to shouldBe bob

    "have an amount property" in:
      val d: IDebt = debt
      d.amount shouldBe 50.0

    "define updateFrom method" in:
      val d: IDebt   = debt
      val charlie    = Person("Charlie")
      val updated    = d.updateFrom(charlie)
      updated.from shouldBe charlie
      updated shouldBe a[IDebt]

    "define updateTo method" in:
      val d: IDebt   = debt
      val charlie    = Person("Charlie")
      val updated    = d.updateTo(charlie)
      updated.to shouldBe charlie
      updated shouldBe a[IDebt]

    "define updateAmount method" in:
      val d: IDebt   = debt
      val updated    = d.updateAmount(100.0)
      updated.amount shouldBe 100.0
      updated shouldBe a[IDebt]

    "support chained updates through interface" in:
      val d: IDebt = debt
      val charlie  = Person("Charlie")
      val david    = Person("David")
      val updated  = d.updateFrom(charlie).updateTo(david).updateAmount(200.0)
      updated.from shouldBe charlie
      updated.to shouldBe david
      updated.amount shouldBe 200.0
  }
