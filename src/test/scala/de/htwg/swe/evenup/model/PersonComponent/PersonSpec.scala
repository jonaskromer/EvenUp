package de.htwg.swe.evenup.model.PersonComponent

import org.scalatest._
import org.scalatest.matchers.should.Matchers
import org.scalatest.wordspec.AnyWordSpec
import de.htwg.swe.evenup.model.PersonComponent.BasePersonImpl.Person
import de.htwg.swe.evenup.model.PersonComponent.BasePersonImpl.PersonFactory

class PersonSpec extends AnyWordSpec with Matchers:

  "A Person" should {

    "have a name" in:
      val person = Person("Alice")
      person.name shouldBe "Alice"

    "return name as toString" in:
      val person = Person("Bob")
      person.toString() shouldBe "Bob"

    "be equal to another person with the same name" in:
      val person1 = Person("Charlie")
      val person2 = Person("Charlie")
      person1 shouldBe person2

    "not be equal to another person with a different name" in:
      val person1 = Person("Alice")
      val person2 = Person("Bob")
      person1 should not be person2

    "not be equal to a non-Person object" in:
      val person = Person("Alice")
      person.equals("Alice") shouldBe false
      person.equals(123) shouldBe false

    "have the same hashCode for persons with the same name" in:
      val person1 = Person("David")
      val person2 = Person("David")
      person1.hashCode() shouldBe person2.hashCode()

    "update name correctly" in:
      val person        = Person("Eve")
      val updatedPerson = person.updateName("Eva")
      updatedPerson.name shouldBe "Eva"
  }

  "PersonFactory" should {

    "create a Person with the given name" in:
      val person = PersonFactory("Frank")
      person.name shouldBe "Frank"
      person shouldBe a[IPerson]
  }
