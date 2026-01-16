package de.htwg.swe.evenup

import de.htwg.swe.evenup.view.tui.Tui
import de.htwg.swe.evenup.view.gui.Gui
import de.htwg.swe.evenup.control.IController
import de.htwg.swe.evenup.modules.Default.given

import scala.io.StdIn.readLine

object EvenUp:
  val controller: IController = summon[IController]
  val tui                     = new Tui(controller)
  val gui                     = new Gui(controller)

  try {
      controller.load()
    } catch {
      case e: Exception => 
        println(s"No previous state found or error loading: ${e.getMessage}")
        println("Starting with empty state")
    }

  def main(args: Array[String]): Unit =
    new Thread(() => gui.main(Array.empty)).start()

    var input: String = ""

    while (input != "exit")
      input = readLine()
      tui.processInput(input)
