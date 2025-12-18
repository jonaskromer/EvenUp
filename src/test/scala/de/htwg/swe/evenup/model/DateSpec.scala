package de.htwg.swe.evenup.model

import org.scalatest.wordspec.AnyWordSpec
import org.scalatest.matchers.should.Matchers
import de.htwg.swe.evenup.model.DateComponent.BaseDateImpl.Date

class DateSpec extends AnyWordSpec with Matchers:

  "A Date" should {

    val date = Date(3, 7, 2025)

    "store the correct day, month, and year" in {
      date.day shouldBe 3
      date.month shouldBe 7
      date.year shouldBe 2025
    }

    "convert to string in DD.MM.YYYY format" in {
      date.toString shouldBe "03.07.2025"
    }

    "pad single-digit day and month with leading zeros" in {
      val date2 = Date(1, 2, 2023)
      date2.toString shouldBe "01.02.2023"
    }

    "not pad years unnecessarily" in {
      val date3 = Date(12, 12, 45)
      date3.toString shouldBe "12.12.0045"
    }
  }
