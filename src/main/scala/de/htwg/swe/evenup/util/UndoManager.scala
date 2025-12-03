package de.htwg.swe.evenup.util

class UndoManager {
  private var undoStack: List[Command] = Nil
  private var redoStack: List[Command] = Nil
  def canUndo                          = undoStack.nonEmpty
  def canRedo                          = redoStack.nonEmpty
  def getUndoStackSize                 = undoStack.length
  def getRedoStackSize                 = redoStack.length

  def doStep(command: Command) = {
    undoStack = command :: undoStack
    command.doStep
    redoStack = Nil
  }

  def undoStep = {
    undoStack match {
      case Nil           =>
      case head :: stack => {
        head.undoStep
        undoStack = stack
        redoStack = head :: redoStack
      }
    }
  }

  def redoStep = {
    redoStack match {
      case Nil           =>
      case head :: stack => {
        head.redoStep
        redoStack = stack
        undoStack = head :: undoStack
      }
    }
  }

}
