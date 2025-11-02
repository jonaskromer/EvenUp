package de.htwg.swe.evenup.view

import de.htwg.swe.evenup.control.Controller
import de.htwg.swe.evenup.util.Observer

class Tui(controller: Controller) extends Observer {
  controller.add(this)

  override def update: Unit = ???

  def processInput(input: String): Unit =
    val in = input.split(" ").toList

}
