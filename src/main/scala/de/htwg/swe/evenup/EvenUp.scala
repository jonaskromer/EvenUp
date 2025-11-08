package de.htwg.swe.evenup

import de.htwg.swe.evenup.model.App
import de.htwg.swe.evenup.control.Controller

import scala.io.StdIn.readLine
import de.htwg.swe.evenup.view.tui.Tui

@main def hello(): Unit =

  val app        = App(Nil, None, None)
  val controller = new Controller(app)
  val tui        = new Tui(controller)

  var input = ""

  while (input != "exit")
    input = readLine()
    tui.processInput(input)
