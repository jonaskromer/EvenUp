package de.htwg.swe.evenup.view.tui

import org.scalatest._
import org.scalatest.matchers.should.Matchers
import org.scalatest.wordspec.AnyWordSpec

class ConsoleColorsSpec extends AnyWordSpec with Matchers:

  "ConsoleColors" should {

    "have correct standard color codes" in:
      ConsoleColors.RED shouldBe "\u001b[31m"
      ConsoleColors.GREEN shouldBe "\u001b[32m"
      ConsoleColors.BLUE shouldBe "\u001b[34m"

    "have correct bright color codes" in:
      ConsoleColors.BRIGHT_RED shouldBe "\u001b[91m"
      ConsoleColors.BRIGHT_GREEN shouldBe "\u001b[92m"

    "have correct background color codes" in:
      ConsoleColors.BG_RED shouldBe "\u001b[41m"
      ConsoleColors.BG_BLUE shouldBe "\u001b[44m"

    "colorize text correctly" in:
      val text    = "Hello"
      val colored = ConsoleColors.colorize(text, ConsoleColors.RED)
      colored shouldBe s"${ConsoleColors.RED}$text${ConsoleColors.RESET}"

    "colorize text with bright background correctly" in:
      val text    = "World"
      val colored = ConsoleColors.colorize(text, ConsoleColors.BG_BRIGHT_YELLOW)
      colored shouldBe s"${ConsoleColors.BG_BRIGHT_YELLOW}$text${ConsoleColors.RESET}"
  }
