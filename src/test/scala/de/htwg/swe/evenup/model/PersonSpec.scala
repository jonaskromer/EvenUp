package de.htwg.swe.evenup.model

import org.scalatest._
import org.scalatest.matchers.should.Matchers
import org.scalatest.wordspec.AnyWordSpec

import de.htwg.swe.evenup._
import de.htwg.swe.evenup.model.Person

class PersonSpec extends AnyWordSpec with Matchers:

  "When updating name the correct name has to be shown" in:
    val p_1      = Person("John")
    val new_name = "Peter"
    val p_2      = p_1.updateName(new_name)
    p_2.name shouldBe new_name

  "The String of a person should print as follows" in:
    val p_1 = Person("John")
    p_1.toString() shouldBe "John"
