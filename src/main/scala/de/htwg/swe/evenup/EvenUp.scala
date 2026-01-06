package de.htwg.swe.evenup

import de.htwg.swe.evenup.view.tui.Tui
import de.htwg.swe.evenup.view.gui.Gui
import de.htwg.swe.evenup.module.DefaultModule

import scala.io.StdIn.readLine
import com.google.inject.Guice
import de.htwg.swe.evenup.control.IController

@main def main(): Unit =

  val injector = Guice.createInjector(new DefaultModule)

  val controller = injector.getInstance(classOf[IController])
  val tui        = injector.getInstance(classOf[Tui])
  val gui        = injector.getInstance(classOf[Gui])

  new Thread(() => gui.main(Array.empty)).start()

  var input = ""

  while (input != "exit")
    input = readLine()
    tui.processInput(input)
