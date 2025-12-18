package de.htwg.swe.evenup

import de.htwg.swe.evenup.model.AppComponent.BaseAppImpl.App
import de.htwg.swe.evenup.control.BaseControllerImpl.Controller

import scala.io.StdIn.readLine
import de.htwg.swe.evenup.view.tui.Tui
import de.htwg.swe.evenup.view.gui.Gui
import de.htwg.swe.evenup.model.StateComponent.BaseAppStateImpl.MainMenuState

@main def main(): Unit =

  val app        = App(Nil, None, None, MainMenuState())
  val controller = new Controller(app)
  val tui        = new Tui(controller)
  val gui        = new Gui(controller)

  new Thread(() => gui.main(Array.empty)).start()

  var input = ""

  while (input != "exit")
    input = readLine()
    tui.processInput(input)