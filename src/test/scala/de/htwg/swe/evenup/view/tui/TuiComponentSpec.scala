package de.htwg.swe.evenup.view.tui

import org.scalatest._
import org.scalatest.matchers.should.Matchers
import org.scalatest.wordspec.AnyWordSpec

class TuiComponentSpec extends AnyWordSpec with Matchers:

  "TextComponent" should {

    "render text as is" in:
      val component = TextComponent("Hello World")
      component.render shouldBe "Hello World"

    "handle multi-line text" in:
      val component = TextComponent("Line 1\nLine 2")
      component.render shouldBe "Line 1\nLine 2"
  }

  "BorderDecorator" should {

    "wrap text with border" in:
      val component = TextComponent("Test")
      val bordered = BorderDecorator(component, "*")
      val result = bordered.render

      result should include("*")
      result should include("Test")

    "use custom border character" in:
      val component = TextComponent("Test")
      val bordered = BorderDecorator(component, "#")
      val result = bordered.render

      result should include("#")

    "handle multi-line content" in:
      val component = TextComponent("Line 1\nLine 2")
      val bordered = BorderDecorator(component, "=")
      val lines = bordered.render.split("\n")

      lines.length should be >= 4
  }

  "ColorDecorator" should {

    "wrap text with color codes" in:
      val component = TextComponent("Colored")
      val colored = ColorDecorator(component, ConsoleColors.RED)
      val result = colored.render

      result should startWith(ConsoleColors.RED)
      result should include("Colored")
      result should endWith(ConsoleColors.RESET)

    "apply different colors" in:
      val component = TextComponent("Blue")
      val colored = ColorDecorator(component, ConsoleColors.BLUE)
      val result = colored.render

      result should startWith(ConsoleColors.BLUE)
  }

  "PaddingDecorator" should {

    "add padding around content" in:
      val component = TextComponent("Content")
      val padded = PaddingDecorator(component, 2)
      val result = padded.render
      
      // The result should have: empty, empty, content, empty, empty
      result should include("Content")
      result.split("\n", -1).length shouldBe 5 // Use -1 to include trailing empty strings

    "use default padding of 1" in:
      val component = TextComponent("Content")
      val padded = PaddingDecorator(component)
      val result = padded.render
      
      // The result should have: empty, content, empty
      result should include("Content")
      result.split("\n", -1).length shouldBe 3 // Use -1 to include trailing empty strings
  }

  "HeaderFooterDecorator" should {

    "add header to content" in:
      val component = TextComponent("Body")
      val decorated = HeaderFooterDecorator(component, header = Some("Header"))
      val result = decorated.render

      result should startWith("Header")
      result should include("Body")

    "add footer to content" in:
      val component = TextComponent("Body")
      val decorated = HeaderFooterDecorator(component, footer = Some("Footer"))
      val result = decorated.render

      result should include("Body")
      result should endWith("Footer")

    "add both header and footer" in:
      val component = TextComponent("Body")
      val decorated = HeaderFooterDecorator(component, header = Some("Header"), footer = Some("Footer"))
      val result = decorated.render

      result should startWith("Header")
      result should include("Body")
      result should endWith("Footer")

    "work without header and footer" in:
      val component = TextComponent("Body")
      val decorated = HeaderFooterDecorator(component)
      val result = decorated.render

      result shouldBe "Body"
  }

  "Decorator composition" should {

    "allow chaining decorators" in:
      val component = TextComponent("Text")
      val decorated = ColorDecorator(
        BorderDecorator(component, "="),
        ConsoleColors.GREEN
      )
      val result = decorated.render

      result should startWith(ConsoleColors.GREEN)
      result should include("Text")
      result should include("=")
      result should endWith(ConsoleColors.RESET)

    "allow complex compositions" in:
      val component = TextComponent("Hello")
      val decorated = HeaderFooterDecorator(
        ColorDecorator(
          BorderDecorator(component, "*"),
          ConsoleColors.CYAN
        ),
        header = Some("Welcome"),
        footer = Some("Goodbye")
      )
      val result = decorated.render

      result should include("Welcome")
      result should include("Hello")
      result should include("Goodbye")
  }
