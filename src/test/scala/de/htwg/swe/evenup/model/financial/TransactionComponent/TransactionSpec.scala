package de.htwg.swe.evenup.model.financial.TransactionComponent

import org.scalatest._
import org.scalatest.matchers.should.Matchers
import org.scalatest.wordspec.AnyWordSpec
import de.htwg.swe.evenup.model.PersonComponent.BasePersonImpl.Person
import de.htwg.swe.evenup.model.DateComponent.BaseDateImpl.Date
import de.htwg.swe.evenup.model.financial.TransactionComponent.BaseTransactionImpl.Transaction
import de.htwg.swe.evenup.model.financial.TransactionComponent.BaseTransactionImpl.TransactionFactory

class TransactionSpec extends AnyWordSpec with Matchers:

  "A Transaction" should {

    val alice = Person("Alice")
    val bob   = Person("Bob")
    val date  = Date(15, 6, 2025)

    "store from, to, amount, and date correctly" in:
      val transaction = Transaction(alice, bob, 100.0, date)
      transaction.from shouldBe alice
      transaction.to shouldBe bob
      transaction.amount shouldBe 100.0
      transaction.date shouldBe date

    "format toString correctly" in:
      val transaction = Transaction(alice, bob, 50.50, date)
      transaction.toString() shouldBe "Alice paid 50.50 to Bob on 15.06.2025."

    "update from correctly" in:
      val charlie     = Person("Charlie")
      val transaction = Transaction(alice, bob, 100.0, date)
      val updated     = transaction.updateFrom(charlie)
      updated.from shouldBe charlie
      updated.to shouldBe bob

    "update to correctly" in:
      val charlie     = Person("Charlie")
      val transaction = Transaction(alice, bob, 100.0, date)
      val updated     = transaction.updateTo(charlie)
      updated.from shouldBe alice
      updated.to shouldBe charlie

    "update amount correctly" in:
      val transaction = Transaction(alice, bob, 100.0, date)
      val updated     = transaction.updateAmount(200.0)
      updated.amount shouldBe 200.0

    "update date correctly" in:
      val newDate     = Date(20, 12, 2025)
      val transaction = Transaction(alice, bob, 100.0, date)
      val updated     = transaction.updateDate(newDate)
      updated.date shouldBe newDate

    "be equal to another transaction with the same values" in:
      val transaction1 = Transaction(alice, bob, 100.0, date)
      val transaction2 = Transaction(alice, bob, 100.0, date)
      transaction1 shouldBe transaction2
  }

  "TransactionFactory" should {

    "create a Transaction with the given values" in:
      val alice       = Person("Alice")
      val bob         = Person("Bob")
      val date        = Date(15, 6, 2025)
      val transaction = TransactionFactory(alice, bob, 75.0, date)
      transaction.from shouldBe alice
      transaction.to shouldBe bob
      transaction.amount shouldBe 75.0
      transaction.date shouldBe date
      transaction shouldBe a[ITransaction]
  }
