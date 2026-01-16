package de.htwg.swe.evenup.view.tui

import org.scalatest._
import org.scalatest.matchers.should.Matchers
import org.scalatest.wordspec.AnyWordSpec

class ComponentDecoratorSpec extends AnyWordSpec with Matchers:

  // Concrete implementation to test abstract ComponentDecorator
  class SimpleDecorator(component: TuiComponent) extends ComponentDecorator(component)

  "ComponentDecorator" should {

    "delegate render to wrapped component by default" in:
      val textComponent = TextComponent("Hello World")
      val decorator     = new SimpleDecorator(textComponent)

      decorator.render shouldBe "Hello World"

    "pass through multi-line content" in:
      val textComponent = TextComponent("Line 1\nLine 2\nLine 3")
      val decorator     = new SimpleDecorator(textComponent)

      decorator.render shouldBe "Line 1\nLine 2\nLine 3"

    "handle empty string" in:
      val textComponent = TextComponent("")
      val decorator     = new SimpleDecorator(textComponent)

      decorator.render shouldBe ""

    "handle special characters" in:
      val textComponent = TextComponent("Special: !@#$%^&*()")
      val decorator     = new SimpleDecorator(textComponent)

      decorator.render shouldBe "Special: !@#$%^&*()"

    "handle unicode characters" in:
      val textComponent = TextComponent("Unicode: äöü 日本語 🎉")
      val decorator     = new SimpleDecorator(textComponent)

      decorator.render shouldBe "Unicode: äöü 日本語 🎉"

    "be chainable with other decorators" in:
      val textComponent = TextComponent("Content")
      val decorator1    = new SimpleDecorator(textComponent)
      val decorator2    = new SimpleDecorator(decorator1)

      decorator2.render shouldBe "Content"

    "work with BorderDecorator as wrapped component" in:
      val textComponent = TextComponent("Test")
      val bordered      = BorderDecorator(textComponent, "=")
      val decorator     = new SimpleDecorator(bordered)

      decorator.render should include("Test")
      decorator.render should include("=")

    "work with ColorDecorator as wrapped component" in:
      val textComponent = TextComponent("Colored")
      val colored       = ColorDecorator(textComponent, ConsoleColors.RED)
      val decorator     = new SimpleDecorator(colored)

      decorator.render should startWith(ConsoleColors.RED)
      decorator.render should include("Colored")

    "work with PaddingDecorator as wrapped component" in:
      val textComponent = TextComponent("Padded")
      val padded        = PaddingDecorator(textComponent, 1)
      val decorator     = new SimpleDecorator(padded)

      decorator.render should include("Padded")
      decorator.render.split("\n", -1).length shouldBe 3

    "work with HeaderFooterDecorator as wrapped component" in:
      val textComponent = TextComponent("Body")
      val headerFooter  = HeaderFooterDecorator(textComponent, Some("Header"), Some("Footer"))
      val decorator     = new SimpleDecorator(headerFooter)

      decorator.render should include("Header")
      decorator.render should include("Body")
      decorator.render should include("Footer")

    "be usable as TuiComponent" in:
      val textComponent           = TextComponent("Test")
      val decorator: TuiComponent = new SimpleDecorator(textComponent)

      decorator.render shouldBe "Test"
  }

  "Multiple SimpleDecorators chained" should {

    "preserve content through chain" in:
      val text = TextComponent("Original Content")
      val d1   = new SimpleDecorator(text)
      val d2   = new SimpleDecorator(d1)
      val d3   = new SimpleDecorator(d2)
      val d4   = new SimpleDecorator(d3)

      d4.render shouldBe "Original Content"
  }
