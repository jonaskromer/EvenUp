package de.htwg.swe.evenup.util

import org.scalatest._
import org.scalatest.matchers.should.Matchers
import org.scalatest.wordspec.AnyWordSpec

import de.htwg.swe.evenup.control.BaseControllerImpl.*
import de.htwg.swe.evenup.model._
import de.htwg.swe.evenup.model.financial.debt.NormalDebtStrategy
import de.htwg.swe.evenup.model.state.MainMenuState
import de.htwg.swe.evenup.control.BaseControllerImpl.{AddGroupCommand, AddUserToGroupCommand, GotoGroupCommand}

class UndoManagerSpec extends AnyWordSpec with Matchers:

  "UndoManager" should {

    "initially have empty stacks" in {
      val undoManager = new UndoManager

      undoManager.canUndo shouldBe false
      undoManager.canRedo shouldBe false
      undoManager.getUndoStackSize shouldBe 0
      undoManager.getRedoStackSize shouldBe 0
    }

    "add command to undo stack when doStep is called" in {
      val undoManager = new UndoManager
      val app         = App(Nil, None, None, MainMenuState())
      val controller  = new Controller(app)
      val group       = Group("TestGroup", Nil, Nil, Nil, NormalDebtStrategy())
      val command     = AddGroupCommand(controller, group)

      undoManager.doStep(command)

      undoManager.canUndo shouldBe true
      undoManager.getUndoStackSize shouldBe 1
      controller.app.allGroups should contain(group)
    }

    "execute the command when doStep is called" in {
      val undoManager = new UndoManager
      val app         = App(Nil, None, None, MainMenuState())
      val controller  = new Controller(app)
      val group       = Group("TestGroup", Nil, Nil, Nil, NormalDebtStrategy())
      val command     = AddGroupCommand(controller, group)

      controller.app.allGroups shouldBe empty

      undoManager.doStep(command)

      controller.app.allGroups should contain(group)
    }

    "clear redo stack when new command is executed" in {
      val undoManager = new UndoManager
      val app         = App(Nil, None, None, MainMenuState())
      val controller  = new Controller(app)

      val group1   = Group("Group1", Nil, Nil, Nil, NormalDebtStrategy())
      val group2   = Group("Group2", Nil, Nil, Nil, NormalDebtStrategy())
      val command1 = AddGroupCommand(controller, group1)
      val command2 = AddGroupCommand(controller, group2)

      undoManager.doStep(command1)
      undoManager.undoStep
      undoManager.getRedoStackSize shouldBe 1

      undoManager.doStep(command2)
      undoManager.getRedoStackSize shouldBe 0
    }

    "undo the last command" in {
      val undoManager = new UndoManager
      val app         = App(Nil, None, None, MainMenuState())
      val controller  = new Controller(app)
      val group       = Group("TestGroup", Nil, Nil, Nil, NormalDebtStrategy())
      val command     = AddGroupCommand(controller, group)

      undoManager.doStep(command)
      controller.app.allGroups should contain(group)

      undoManager.undoStep

      controller.app.allGroups should not contain group
      undoManager.canUndo shouldBe false
      undoManager.canRedo shouldBe true
    }

    "move command from undo stack to redo stack on undo" in {
      val undoManager = new UndoManager
      val app         = App(Nil, None, None, MainMenuState())
      val controller  = new Controller(app)
      val group       = Group("TestGroup", Nil, Nil, Nil, NormalDebtStrategy())
      val command     = AddGroupCommand(controller, group)

      undoManager.doStep(command)
      undoManager.getUndoStackSize shouldBe 1
      undoManager.getRedoStackSize shouldBe 0

      undoManager.undoStep

      undoManager.getUndoStackSize shouldBe 0
      undoManager.getRedoStackSize shouldBe 1
    }

    "do nothing when undoStep is called on empty stack" in {
      val undoManager = new UndoManager
      val app         = App(Nil, None, None, MainMenuState())
      val controller  = new Controller(app)

      undoManager.canUndo shouldBe false

      noException should be thrownBy undoManager.undoStep

      undoManager.canUndo shouldBe false
      undoManager.canRedo shouldBe false
    }

    "redo the last undone command" in {
      val undoManager = new UndoManager
      val app         = App(Nil, None, None, MainMenuState())
      val controller  = new Controller(app)
      val group       = Group("TestGroup", Nil, Nil, Nil, NormalDebtStrategy())
      val command     = AddGroupCommand(controller, group)

      undoManager.doStep(command)
      undoManager.undoStep
      controller.app.allGroups should not contain group

      undoManager.redoStep

      controller.app.allGroups should contain(group)
      undoManager.canRedo shouldBe false
      undoManager.canUndo shouldBe true
    }

    "move command from redo stack to undo stack on redo" in {
      val undoManager = new UndoManager
      val app         = App(Nil, None, None, MainMenuState())
      val controller  = new Controller(app)
      val group       = Group("TestGroup", Nil, Nil, Nil, NormalDebtStrategy())
      val command     = AddGroupCommand(controller, group)

      undoManager.doStep(command)
      undoManager.undoStep
      undoManager.getUndoStackSize shouldBe 0
      undoManager.getRedoStackSize shouldBe 1

      undoManager.redoStep

      undoManager.getUndoStackSize shouldBe 1
      undoManager.getRedoStackSize shouldBe 0
    }

    "do nothing when redoStep is called on empty stack" in {
      val undoManager = new UndoManager
      val app         = App(Nil, None, None, MainMenuState())
      val controller  = new Controller(app)

      undoManager.canRedo shouldBe false

      noException should be thrownBy undoManager.redoStep

      undoManager.canUndo shouldBe false
      undoManager.canRedo shouldBe false
    }

    "handle multiple redo operations in sequence" in {
      val undoManager = new UndoManager
      val app         = App(Nil, None, None, MainMenuState())
      val controller  = new Controller(app)

      val group1 = Group("Group1", Nil, Nil, Nil, NormalDebtStrategy())
      val group2 = Group("Group2", Nil, Nil, Nil, NormalDebtStrategy())
      val group3 = Group("Group3", Nil, Nil, Nil, NormalDebtStrategy())

      val command1 = AddGroupCommand(controller, group1)
      val command2 = AddGroupCommand(controller, group2)
      val command3 = AddGroupCommand(controller, group3)

      undoManager.doStep(command1)
      undoManager.doStep(command2)
      undoManager.doStep(command3)

      undoManager.undoStep
      undoManager.undoStep
      undoManager.undoStep

      controller.app.allGroups shouldBe empty
      undoManager.getRedoStackSize shouldBe 3

      undoManager.redoStep
      controller.app.allGroups should have size 1
      controller.app.allGroups should contain(group1)

      undoManager.redoStep
      controller.app.allGroups should have size 2
      controller.app.allGroups should contain(group2)

      undoManager.redoStep
      controller.app.allGroups should have size 3
      controller.app.allGroups should contain(group3)

      undoManager.getUndoStackSize shouldBe 3
      undoManager.getRedoStackSize shouldBe 0
    }

    "handle undo-redo-undo sequence" in {
      val undoManager = new UndoManager
      val app         = App(Nil, None, None, MainMenuState())
      val controller  = new Controller(app)
      val group       = Group("TestGroup", Nil, Nil, Nil, NormalDebtStrategy())
      val command     = AddGroupCommand(controller, group)

      undoManager.doStep(command)
      controller.app.allGroups should contain(group)

      undoManager.undoStep
      controller.app.allGroups should not contain group

      undoManager.redoStep
      controller.app.allGroups should contain(group)

      undoManager.undoStep
      controller.app.allGroups should not contain group
    }

    "maintain correct stack sizes through complex operations" in {
      val undoManager = new UndoManager
      val app         = App(Nil, None, None, MainMenuState())
      val controller  = new Controller(app)

      val group1 = Group("Group1", Nil, Nil, Nil, NormalDebtStrategy())
      val group2 = Group("Group2", Nil, Nil, Nil, NormalDebtStrategy())

      val command1 = AddGroupCommand(controller, group1)
      val command2 = AddGroupCommand(controller, group2)

      undoManager.doStep(command1)
      undoManager.doStep(command2)
      undoManager.getUndoStackSize shouldBe 2
      undoManager.getRedoStackSize shouldBe 0

      undoManager.undoStep
      undoManager.undoStep
      undoManager.getUndoStackSize shouldBe 0
      undoManager.getRedoStackSize shouldBe 2

      undoManager.redoStep
      undoManager.getUndoStackSize shouldBe 1
      undoManager.getRedoStackSize shouldBe 1

      val group3   = Group("Group3", Nil, Nil, Nil, NormalDebtStrategy())
      val command3 = AddGroupCommand(controller, group3)
      undoManager.doStep(command3)
      undoManager.getUndoStackSize shouldBe 2
      undoManager.getRedoStackSize shouldBe 0
    }

    "work correctly with different command types" in {
      val undoManager = new UndoManager
      val app         = App(Nil, None, None, MainMenuState())
      val controller  = new Controller(app)

      val group = Group("TestGroup", Nil, Nil, Nil, NormalDebtStrategy())
      val alice = Person("Alice")

      val addGroupCmd = AddGroupCommand(controller, group)
      undoManager.doStep(addGroupCmd)
      controller.app.containsGroup("TestGroup") shouldBe true

      val gotoGroupCmd = GotoGroupCommand(controller, group)
      undoManager.doStep(gotoGroupCmd)
      controller.app.active_group shouldBe defined

      val addUserCmd = AddUserToGroupCommand(controller, alice)
      undoManager.doStep(addUserCmd)
      controller.app.getGroup("TestGroup").get.members should contain(alice)
      undoManager.getUndoStackSize shouldBe 3

      undoManager.undoStep
      controller.app.getGroup("TestGroup").get.members should not contain alice

      undoManager.undoStep
      controller.app.containsGroup("TestGroup") shouldBe true

      undoManager.undoStep
      controller.app.containsGroup("TestGroup") shouldBe false
      undoManager.getUndoStackSize shouldBe 0
      undoManager.getRedoStackSize shouldBe 3

      undoManager.redoStep
      controller.app.containsGroup("TestGroup") shouldBe true

      undoManager.redoStep
      controller.app.active_group shouldBe defined

      undoManager.redoStep
      val finalGroup = controller.app.getGroup("TestGroup").get
      finalGroup.members should contain(alice)
      undoManager.getUndoStackSize shouldBe 3
      undoManager.getRedoStackSize shouldBe 0
    }
  }
