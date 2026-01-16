package de.htwg.swe.evenup.util

import org.scalatest._
import org.scalatest.matchers.should.Matchers
import org.scalatest.wordspec.AnyWordSpec
import de.htwg.swe.evenup.control.IController
import de.htwg.swe.evenup.model.AppComponent.IApp
import de.htwg.swe.evenup.model.AppComponent.BaseAppImpl.App
import de.htwg.swe.evenup.model.StateComponent.BaseAppStateImpl.MainMenuState
import de.htwg.swe.evenup.model.GroupComponent.BaseGroupImpl.Group
import de.htwg.swe.evenup.model.PersonComponent.BasePersonImpl.Person
import de.htwg.swe.evenup.model.financial.DebtComponent.BaseDebtImpl.NormalDebtStrategy
import de.htwg.swe.evenup.control.BaseControllerImpl.ArgsHandler
import de.htwg.swe.evenup.model.DateComponent.IDate

class UndoManagerSpec extends AnyWordSpec with Matchers:

  class TestController extends IController:
    var app: IApp = App(Nil, None, None, MainMenuState())
    val argsHandler = new ArgsHandler

    def undo(): Unit = ()
    def redo(): Unit = ()
    def quit: Unit = ()
    def load(): Unit = ()
    def gotoMainMenu: Unit = ()
    def gotoGroup(group_name: String): Unit = ()
    def addGroup(group_name: String): Unit = ()
    def addUserToGroup(user_name: String): Unit = ()
    def addExpenseToGroup(expense_name: String, paid_by: String, amount: Double, date: IDate, shares: Option[String]): Unit = ()
    def setDebtStrategy(strategy: String): Unit = ()
    def calculateDebts(): Unit = ()

  class TestCommand(controller: IController, doAction: () => Unit) extends Command(controller):
    var doStepCalled = false
    def doStep: Unit =
      doStepCalled = true
      doAction()

  "UndoManager" should {

    "start with empty stacks" in:
      val manager = new UndoManager
      manager.canUndo shouldBe false
      manager.canRedo shouldBe false
      manager.getUndoStackSize shouldBe 0
      manager.getRedoStackSize shouldBe 0

    "execute command on doStep" in:
      val controller = new TestController
      val manager = new UndoManager
      var executed = false
      val command = new TestCommand(controller, () => executed = true)

      manager.doStep(command)
      executed shouldBe true
      command.doStepCalled shouldBe true

    "add command to undo stack after doStep" in:
      val controller = new TestController
      val manager = new UndoManager
      val command = new TestCommand(controller, () => ())

      manager.doStep(command)
      manager.canUndo shouldBe true
      manager.getUndoStackSize shouldBe 1

    "clear redo stack after new command" in:
      val controller = new TestController
      val manager = new UndoManager
      val command1 = new TestCommand(controller, () => ())
      val command2 = new TestCommand(controller, () => ())

      manager.doStep(command1)
      manager.undoStep
      manager.canRedo shouldBe true

      manager.doStep(command2)
      manager.canRedo shouldBe false
      manager.getRedoStackSize shouldBe 0

    "undo a step" in:
      val controller = new TestController
      val alice = Person("Alice")
      val group = Group("Test", List(alice), Nil, Nil, NormalDebtStrategy())
      controller.app = controller.app.addGroup(group)

      val manager = new UndoManager
      val command = new TestCommand(controller, () => ())

      manager.doStep(command)
      manager.undoStep

      manager.canUndo shouldBe false
      manager.canRedo shouldBe true
      manager.getUndoStackSize shouldBe 0
      manager.getRedoStackSize shouldBe 1

    "redo a step" in:
      val controller = new TestController
      val manager = new UndoManager
      var executeCount = 0
      val command = new TestCommand(controller, () => executeCount += 1)

      manager.doStep(command)
      executeCount shouldBe 1

      manager.undoStep
      manager.redoStep

      executeCount shouldBe 2
      manager.canUndo shouldBe true
      manager.canRedo shouldBe false

    "handle multiple undo/redo operations" in:
      val controller = new TestController
      val manager = new UndoManager
      val command1 = new TestCommand(controller, () => ())
      val command2 = new TestCommand(controller, () => ())
      val command3 = new TestCommand(controller, () => ())

      manager.doStep(command1)
      manager.doStep(command2)
      manager.doStep(command3)

      manager.getUndoStackSize shouldBe 3
      manager.getRedoStackSize shouldBe 0

      manager.undoStep
      manager.undoStep

      manager.getUndoStackSize shouldBe 1
      manager.getRedoStackSize shouldBe 2

      manager.redoStep

      manager.getUndoStackSize shouldBe 2
      manager.getRedoStackSize shouldBe 1

    "do nothing when undoing empty stack" in:
      val manager = new UndoManager
      manager.undoStep
      manager.canUndo shouldBe false
      manager.canRedo shouldBe false

    "do nothing when redoing empty stack" in:
      val manager = new UndoManager
      manager.redoStep
      manager.canUndo shouldBe false
      manager.canRedo shouldBe false
  }
