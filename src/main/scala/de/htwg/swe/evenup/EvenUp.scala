package de.htwg.swe.evenup

import de.htwg.swe.evenup.model.AppComponent.BaseAppImpl.App
import de.htwg.swe.evenup.control.BaseControllerImpl.Controller
import de.htwg.swe.evenup.modules.Default.given

import scala.io.StdIn.readLine
import de.htwg.swe.evenup.view.tui.Tui
import de.htwg.swe.evenup.view.gui.Gui
import de.htwg.swe.evenup.model.StateComponent.BaseAppStateImpl.MainMenuState
import de.htwg.swe.evenup.model.AppComponent.IApp
import de.htwg.swe.evenup.control.IController

@main def main(): Unit =

  val controller: IController = summon[IController]
  val tui        = new Tui(controller)
  val gui        = new Gui(controller)

  new Thread(() => gui.main(Array.empty)).start()

  var input = ""

  while (input != "exit")
    input = readLine()
    tui.processInput(input)
