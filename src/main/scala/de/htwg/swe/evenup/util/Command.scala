package de.htwg.swe.evenup.util

import de.htwg.swe.evenup.control.IController

abstract class Command(controller: IController):
  val memento: Memento = Memento(controller.app)
  def doStep: Unit
  def undoStep: Unit = controller.app = memento.app
  def redoStep: Unit = doStep
