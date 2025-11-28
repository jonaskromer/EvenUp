package de.htwg.swe.evenup.view.tui

import org.scalatest.wordspec.AnyWordSpec
import org.scalatest.matchers.should.Matchers

class TuiComponentSpec extends AnyWordSpec with Matchers {

  "A TextComponent" should {
    "render its text" in {
      val text = new TextComponent("Hello")
      text.render shouldBe "Hello"
    }
  }

  "A BorderDecorator" should {
    "add borders around text" in {
      val text = new TextComponent("Hi")
      val bordered = new BorderDecorator(text, "#")
      val rendered = bordered.render
      val lines = rendered.split("\n")
      
      lines.head shouldBe "######"
      lines.last shouldBe "######"
      lines(1).trim should startWith("#")
      lines(1).trim should endWith("#")
      lines(1) should include("Hi")
    }
  }

  "A ColorDecorator" should {
    "wrap the text in color codes" in {
      val text = new TextComponent("Colored")
      val colored = new ColorDecorator(text, "\u001b[31m") // red
      colored.render shouldBe s"\u001b[31mColored${Console.RESET}"
    }
  }

  "A HeaderFooterDecorator" should {
    "add header and footer correctly" in {
      val text = new TextComponent("Body")
      val decorated = new HeaderFooterDecorator(text, header = Some("Header"), footer = Some("Footer"))
      val lines = decorated.render.split("\n")
      lines.head shouldBe "Header"
      lines(1) shouldBe "Body"
      lines(2) shouldBe "Footer"
    }

    "work with only header or only footer" in {
      val text = new TextComponent("Content")
      val headerOnly = new HeaderFooterDecorator(text, header = Some("H"))
      headerOnly.render.split("\n") should contain theSameElementsInOrderAs Seq("H", "Content")

      val footerOnly = new HeaderFooterDecorator(text, footer = Some("F"))
      footerOnly.render.split("\n") should contain theSameElementsInOrderAs Seq("Content", "F")
    }
  }

  "Multiple decorators" should {
    "stack correctly" in {
      val text = new TextComponent("Stacked")
      val decorated = new BorderDecorator(new PaddingDecorator(new HeaderFooterDecorator(text, header = Some("H"), footer = Some("F"))))
      val rendered = decorated.render
      rendered should include("H")
      rendered should include("Stacked")
      rendered should include("F")
      rendered.split("\n").head should startWith("*")
      rendered.split("\n").last should startWith("*")
    }
  }

  "A ComponentDecorator" should {

    "delegate render to the wrapped component" in {
      // Create a simple TuiComponent
      val textComponent = new TextComponent("Hello World")

      // Create a concrete subclass of ComponentDecorator for testing
      val decorator = new ComponentDecorator(textComponent) {}

      // It should delegate render to the wrapped component
      decorator.render shouldBe "Hello World"
    }

    "reflect changes if the wrapped component changes" in {
      var dynamicText = "Initial"
      val textComponent = new TextComponent(dynamicText)
      val decorator = new ComponentDecorator(textComponent) {}

      decorator.render shouldBe "Initial"

      // Simulate changing the wrapped component
      val newTextComponent = new TextComponent("Updated")
      val newDecorator = new ComponentDecorator(newTextComponent) {}

      newDecorator.render shouldBe "Updated"
    }

  }
}
