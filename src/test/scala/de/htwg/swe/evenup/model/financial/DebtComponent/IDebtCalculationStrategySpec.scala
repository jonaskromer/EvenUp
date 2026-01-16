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
import play.api.libs.json.Json

class IDebtCalculationStrategySpec extends AnyWordSpec with Matchers:

  val alice   = Person("Alice")
  val bob     = Person("Bob")
  val charlie = Person("Charlie")
  val date    = Date(15, 6, 2025)

  "IDebtCalculationStrategy" should {

    "define calculateDebts method" in:
      val strategy: IDebtCalculationStrategy = NormalDebtStrategy()
      val group                              = Group("Trip", List(alice, bob), Nil, Nil, strategy)
      val debts                              = strategy.calculateDebts(group)
      debts shouldBe a[List[?]]

    "define calculateBalances method" in:
      val strategy: IDebtCalculationStrategy = NormalDebtStrategy()
      val expense                            = Expense("Dinner", 30.0, date, alice, List(Share(bob, 15.0), Share(charlie, 15.0)))
      val group                              = Group("Trip", List(alice, bob, charlie), List(expense), Nil, strategy)
      val balances                           = strategy.calculateBalances(group)
      balances shouldBe a[Map[?, ?]]
      balances(alice) shouldBe 30.0  // Alice paid 30
      balances(bob) shouldBe -15.0   // Bob owes 15
      balances(charlie) shouldBe -15.0 // Charlie owes 15

    "serialize to XML correctly" in:
      val strategy: IDebtCalculationStrategy = NormalDebtStrategy()
      val xml                                = strategy.toXml
      xml.label shouldBe "DebtCalculationStrategy"
      (xml \ "Type").text should include("NormalDebtStrategy")

    "serialize to JSON correctly" in:
      val strategy: IDebtCalculationStrategy = NormalDebtStrategy()
      val json                               = strategy.toJson
      (json \ "type").as[String] should include("NormalDebtStrategy")

    "serialize SimplifiedDebtStrategy to XML correctly" in:
      val strategy: IDebtCalculationStrategy = SimplifiedDebtStrategy()
      val xml                                = strategy.toXml
      (xml \ "Type").text should include("SimplifiedDebtStrategy")

    "serialize SimplifiedDebtStrategy to JSON correctly" in:
      val strategy: IDebtCalculationStrategy = SimplifiedDebtStrategy()
      val json                               = strategy.toJson
      (json \ "type").as[String] should include("SimplifiedDebtStrategy")

    "calculate correct balances for multiple expenses" in:
      val strategy: IDebtCalculationStrategy = NormalDebtStrategy()
      val expense1                           = Expense("E1", 30.0, date, alice, List(Share(bob, 15.0), Share(alice, 15.0)))
      val expense2                           = Expense("E2", 20.0, date, bob, List(Share(alice, 10.0), Share(bob, 10.0)))
      val group                              = Group("Trip", List(alice, bob), List(expense1, expense2), Nil, strategy)
      val balances                           = strategy.calculateBalances(group)

      // Alice: paid 30, owes 15 (to herself) + 10 (to Bob) = net +5
      // Bob: paid 20, owes 15 (to Alice) + 10 (to himself) = net -5
      balances(alice) shouldBe 5.0
      balances(bob) shouldBe -5.0
  }
