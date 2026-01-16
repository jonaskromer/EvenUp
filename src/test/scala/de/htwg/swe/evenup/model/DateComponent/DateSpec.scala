package de.htwg.swe.evenup.model.DateComponent

import org.scalatest._
import org.scalatest.matchers.should.Matchers
import org.scalatest.wordspec.AnyWordSpec
import de.htwg.swe.evenup.model.DateComponent.BaseDateImpl.Date
import de.htwg.swe.evenup.model.DateComponent.BaseDateImpl.DateFactory

class DateSpec extends AnyWordSpec with Matchers:

  "A Date" should {

    "store day, month, and year correctly" in:
      val date = Date(15, 6, 2025)
      date.day shouldBe 15
      date.month shouldBe 6
      date.year shouldBe 2025

    "format toString with leading zeros" in:
      val date = Date(1, 3, 2025)
      date.toString() shouldBe "01.03.2025"

    "format double-digit day and month correctly" in:
      val date = Date(25, 12, 2024)
      date.toString() shouldBe "25.12.2024"

    "format year with leading zeros" in:
      val date = Date(1, 1, 500)
      date.toString() shouldBe "01.01.0500"

    "be equal to another date with the same values" in:
      val date1 = Date(10, 5, 2025)
      val date2 = Date(10, 5, 2025)
      date1 shouldBe date2

    "not be equal to another date with different values" in:
      val date1 = Date(10, 5, 2025)
      val date2 = Date(11, 5, 2025)
      date1 should not be date2
  }

  "DateFactory" should {

    "create a Date with the given values" in:
      val date = DateFactory(20, 8, 2025)
      date.day shouldBe 20
      date.month shouldBe 8
      date.year shouldBe 2025
      date shouldBe a[IDate]
  }
