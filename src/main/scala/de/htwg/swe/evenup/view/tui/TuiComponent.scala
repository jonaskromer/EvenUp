package de.htwg.swe.evenup.view.tui

trait TuiComponent:
  def render: String

class TextComponent(text: String) extends TuiComponent:
  def render: String = text

abstract class ComponentDecorator(component: TuiComponent) extends TuiComponent:
  def render: String = component.render

class BorderDecorator(component: TuiComponent, borderChar: String = "*") 
    extends ComponentDecorator(component):
  
  override def render: String =
    val lines = component.render.split("\n")
    val width = lines.map(_.length).max
    val border = borderChar * (width + 4)
    val framed = lines.map(line => s"$borderChar ${line.padTo(width, ' ')} $borderChar")
    
    (border +: framed :+ border).mkString("\n")

class ColorDecorator(component: TuiComponent, colorCode: String) 
    extends ComponentDecorator(component):
  
  override def render: String =
    s"$colorCode${component.render}${ConsoleColors.RESET}"

class PaddingDecorator(component: TuiComponent, padding: Int = 1) 
    extends ComponentDecorator(component):
  
  override def render: String =
    val empty = Seq.fill(padding)("")
    (empty ++ Seq(component.render) ++ empty).mkString("\n")

class HeaderFooterDecorator(
    component: TuiComponent,
    header: Option[String] = None,
    footer: Option[String] = None
) extends ComponentDecorator(component):
  
  override def render: String =
    (header.toSeq ++ Seq(component.render) ++ footer.toSeq).mkString("\n")