package de.htwg.swe.evenup.model

import org.scalatest.wordspec.AnyWordSpec
import org.scalatest.matchers.should.Matchers

class ShareSpec extends AnyWordSpec with Matchers:

  "A Share" should {

    val p1 = Person("Alice")
    val share = Share(p1, 12.34)

    "store the correct person and amount" in {
      share.person shouldBe p1
      share.amount shouldBe 12.34
    }

    "support equality" in {
      val sameShare = Share(Person("Alice"), 12.34)
      share shouldBe sameShare
      val differentShare = Share(Person("Alice"), 56.78)
      share should not be differentShare
    }

    "have a reasonable toString implementation" in {
      val str = share.toString
      str should include("Alice")
      str should include("12.34")
    }
  }
