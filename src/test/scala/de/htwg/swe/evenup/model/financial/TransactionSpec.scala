package de.htwg.swe.evenup.model.financial

import org.scalatest._
import org.scalatest.matchers.should.Matchers
import org.scalatest.wordspec.AnyWordSpec

import de.htwg.swe.evenup.model.Person
import de.htwg.swe.evenup.model.DateComponent.BaseDateImpl.Date

class TransactionSpec extends AnyWordSpec with Matchers:

  "The String of a Transaction should print as follows" in:
    val date = Date(1, 1, 2000)
    val p_1  = Person("John")
    val p_2  = Person("Peter")
    val t_1  = Transaction(p_1, p_2, 10.00, date)
    t_1.toString() shouldBe "John paid 10.00 to Peter on 01.01.2000."

  "The person owing should be update correctly" in:
    val date = Date(1, 1, 2000)
    val p_1  = Person("John")
    val p_2  = Person("Peter")
    val p_3  = Person("Frank")
    val t_1  = Transaction(p_1, p_2, 10.00, date)
    val t_2  = t_1.updateFrom(p_3)
    t_2.from.name shouldBe "Frank"

  "The person that is owed to should be update correctly" in:
    val date = Date(1, 1, 2000)
    val p_1  = Person("John")
    val p_2  = Person("Peter")
    val p_3  = Person("Frank")
    val t_1  = Transaction(p_1, p_2, 10.00, date)
    val t_2  = t_1.updateTo(p_3)
    t_2.to.name shouldBe "Frank"

  "The amount owed should be update correctly" in:
    val date = Date(1, 1, 2000)
    val p_1  = Person("John")
    val p_2  = Person("Peter")
    val t_1  = Transaction(p_1, p_2, 10.00, date)
    val t_2  = t_1.updateAmount((5.00))
    t_2.amount shouldBe 5.00

  "The date should be change correct" in:
    val date    = Date(1, 1, 2000)
    val newDate = Date(2, 2, 2002)
    val p_1     = Person("John")
    val p_2     = Person("Peter")
    val t_1     = Transaction(p_1, p_2, 10.00, date)
    val t_2     = t_1.updateDate(newDate)
    t_2.date shouldBe newDate
