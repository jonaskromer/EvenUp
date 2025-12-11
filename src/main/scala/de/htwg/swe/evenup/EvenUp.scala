package de.htwg.swe.evenup

import de.htwg.swe.evenup.model.App
import de.htwg.swe.evenup.control.Controller
import de.htwg.swe.evenup.view.tui.Tui
import de.htwg.swe.evenup.view.gui.GuiApp
import de.htwg.swe.evenup.model.state.MainMenuState

import scala.io.StdIn.readLine
import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

@main def main(args: String*): Unit =
  val app        = App(Nil, None, None, MainMenuState())
  val controller = new Controller(app)
  
  val useGui = args.contains("--gui") || args.contains("-g")
  val useTui = args.contains("--tui") || args.contains("-t") || (!useGui && args.isEmpty)
  
  if (useGui && useTui) {
    Future {
      GuiApp.startGui(controller)
    }
    
    val tui = new Tui(controller)
    var input = ""
    
    while (input != "exit") {
      input = readLine()
      if (input != null) {
        tui.processInput(input)
      }
    }
  } else if (useGui) {
    GuiApp.startGui(controller)
  } else {
    val tui = new Tui(controller)
    var input = ""
    
    while (input != "exit") {
      input = readLine()
      if (input != null) {
        tui.processInput(input)
      }
    }
  }