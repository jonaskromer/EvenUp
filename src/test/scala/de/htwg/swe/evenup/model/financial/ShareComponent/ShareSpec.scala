package de.htwg.swe.evenup.model.financial.ShareComponent

import org.scalatest._
import org.scalatest.matchers.should.Matchers
import org.scalatest.wordspec.AnyWordSpec
import de.htwg.swe.evenup.model.PersonComponent.BasePersonImpl.Person
import de.htwg.swe.evenup.model.financial.ShareComponent.BaseShareImpl.Share
import de.htwg.swe.evenup.model.financial.ShareComponent.BaseShareImpl.ShareFactory

class ShareSpec extends AnyWordSpec with Matchers:

  "A Share" should {

    "store person and amount correctly" in:
      val person = Person("Alice")
      val share  = Share(person, 25.50)
      share.person shouldBe person
      share.amount shouldBe 25.50

    "be equal to another share with the same values" in:
      val person = Person("Bob")
      val share1 = Share(person, 10.0)
      val share2 = Share(person, 10.0)
      share1 shouldBe share2

    "not be equal to another share with different amount" in:
      val person = Person("Charlie")
      val share1 = Share(person, 10.0)
      val share2 = Share(person, 20.0)
      share1 should not be share2

    "not be equal to another share with different person" in:
      val person1 = Person("Alice")
      val person2 = Person("Bob")
      val share1  = Share(person1, 10.0)
      val share2  = Share(person2, 10.0)
      share1 should not be share2
  }

  "ShareFactory" should {

    "create a Share with the given values" in:
      val person = Person("David")
      val share  = ShareFactory(person, 50.0)
      share.person shouldBe person
      share.amount shouldBe 50.0
      share shouldBe a[IShare]
  }
