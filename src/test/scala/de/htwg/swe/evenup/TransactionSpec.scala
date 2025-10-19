package de.htwg.swe.evenup

import org.scalatest._
import org.scalatest.matchers.should.Matchers
import org.scalatest.wordspec.AnyWordSpec

import de.htwg.swe.evenup._

class TransactionSpec extends AnyWordSpec with Matchers:
    
    "The String of a Transaction should print as follows" in:
        val p_1 = Person("John")
        val p_2 = Person("Peter")
        val t_1 = Transaction(p_1, p_2, 10.00)
        t_1.toString() shouldBe "John paid 10.00 to Peter."

    "The person owing should be update correctly" in:
        val p_1 = Person("John")
        val p_2 = Person("Peter")
        val p_3 = Person("Frank")
        val t_1 = Transaction(p_1, p_2, 10.00)      
        val t_2 = t_1.updateFrom(p_3)
        t_2.from.name shouldBe "Frank"  
    
    "The person that is owed to should be update correctly" in:
        val p_1 = Person("John")
        val p_2 = Person("Peter")
        val p_3 = Person("Frank")
        val t_1 = Transaction(p_1, p_2, 10.00)      
        val t_2 = t_1.updateTo(p_3)
        t_2.to.name shouldBe "Frank"  

    
    "The amount owed should be update correctly" in:
        val p_1 = Person("John")
        val p_2 = Person("Peter")
        val t_1 = Transaction(p_1, p_2, 10.00)      
        val t_2 = t_1.updateAmount((5.00))
        t_2.amount shouldBe 5.00