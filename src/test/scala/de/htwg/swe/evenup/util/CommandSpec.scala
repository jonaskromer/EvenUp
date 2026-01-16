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

class CommandSpec extends AnyWordSpec with Matchers:

  class TestController extends IController:
    var app: IApp   = App(Nil, None, None, MainMenuState())
    val argsHandler = new ArgsHandler

    def undo(): Unit                            = ()
    def redo(): Unit                            = ()
    def quit: Unit                              = ()
    def load(): Unit                            = ()
    def gotoMainMenu: Unit                      = ()
    def gotoGroup(group_name: String): Unit     = ()
    def addGroup(group_name: String): Unit      = ()
    def addUserToGroup(user_name: String): Unit = ()

    def addExpenseToGroup(
      expense_name: String,
      paid_by: String,
      amount: Double,
      date: IDate,
      shares: Option[String]
    ): Unit = ()

    def setDebtStrategy(strategy: String): Unit = ()
    def calculateDebts(): Unit                  = ()

  class TestCommand(controller: IController) extends Command(controller):
    var doStepCalled = false

    def doStep: Unit =
      doStepCalled = true
      val group = Group("NewGroup", Nil, Nil, Nil, NormalDebtStrategy())
      controller.app = controller.app.addGroup(group)

  "Command" should {

    "store memento on creation" in:
      val controller = new TestController
      val alice      = Person("Alice")
      val group      = Group("Original", List(alice), Nil, Nil, NormalDebtStrategy())
      controller.app = controller.app.addGroup(group)

      val command = new TestCommand(controller)
      command.memento.app.allGroups.length shouldBe 1
      command.memento.app.allGroups.head.name shouldBe "Original"

    "restore app state on undoStep" in:
      val controller  = new TestController
      val originalApp = controller.app

      val command = new TestCommand(controller)
      command.doStep
      controller.app.allGroups.length shouldBe 1

      command.undoStep
      controller.app shouldBe originalApp
      controller.app.allGroups shouldBe empty

    "re-execute doStep on redoStep" in:
      val controller = new TestController

      val command = new TestCommand(controller)
      command.doStep
      command.doStepCalled shouldBe true

      command.undoStep
      controller.app.allGroups shouldBe empty

      command.doStepCalled = false
      command.redoStep
      command.doStepCalled shouldBe true
      controller.app.allGroups.length shouldBe 1
  }
