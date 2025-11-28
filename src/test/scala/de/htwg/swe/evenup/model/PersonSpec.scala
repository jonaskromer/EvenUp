package de.htwg.swe.evenup.model

import org.scalatest.wordspec.AnyWordSpec
import org.scalatest.matchers.should.Matchers

class PersonSpec extends AnyWordSpec with Matchers:

  "A Person" should {

    val person = Person("Alice")

    "store the correct name" in {
      person.name shouldBe "Alice"
    }

    "convert to string correctly" in {
      person.toString shouldBe "Alice"
    }

    "support equality by name" in {
      val samePerson      = Person("Alice")
      val differentPerson = Person("Bob")
      person shouldBe samePerson
      person should not be differentPerson
      person should not be null
    }

    "have hashCode equal to name.hashCode" in {
      person.hashCode shouldBe "Alice".hashCode
    }

    "update the name correctly" in {
      val updated = person.updateName("Bob")
      updated.name shouldBe "Bob"
      updated should not be person
    }
  }
