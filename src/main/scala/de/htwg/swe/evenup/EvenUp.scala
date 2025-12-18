package de.htwg.swe.evenup

import de.htwg.swe.evenup.model.AppComponent.BaseAppImpl.App
import de.htwg.swe.evenup.control.BaseControllerImpl.Controller

import scala.io.StdIn.readLine
import de.htwg.swe.evenup.view.tui.Tui
import de.htwg.swe.evenup.model.StateComponent.BaseAppStateImpl.MainMenuState

@main def main(): Unit =

  val app        = App(Nil, None, None, MainMenuState())
  val controller = new Controller(app)
  val tui        = new Tui(controller)

  var input = ""

  while (input != "exit")
    input = readLine()
    tui.processInput(input)
