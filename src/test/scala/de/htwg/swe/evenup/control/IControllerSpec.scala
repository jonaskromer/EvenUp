package de.htwg.swe.evenup.control

import org.scalatest._
import org.scalatest.matchers.should.Matchers
import org.scalatest.wordspec.AnyWordSpec
import de.htwg.swe.evenup.model.AppComponent.IApp
import de.htwg.swe.evenup.model.AppComponent.BaseAppImpl.App
import de.htwg.swe.evenup.model.StateComponent.BaseAppStateImpl.MainMenuState
import de.htwg.swe.evenup.model.DateComponent.IDate
import de.htwg.swe.evenup.model.DateComponent.BaseDateImpl.Date
import de.htwg.swe.evenup.control.BaseControllerImpl.ArgsHandler
import de.htwg.swe.evenup.util.Observer
import de.htwg.swe.evenup.util.ObservableEvent

class IControllerSpec extends AnyWordSpec with Matchers:

  // Test implementation of IController trait
  class TestController extends IController:
    var app: IApp   = App(Nil, None, None, MainMenuState())
    val argsHandler = new ArgsHandler

    var undoCalled                                                              = false
    var redoCalled                                                              = false
    var quitCalled                                                              = false
    var loadCalled                                                              = false
    var gotoMainMenuCalled                                                      = false
    var gotoGroupName: Option[String]                                           = None
    var addGroupName: Option[String]                                            = None
    var addUserName: Option[String]                                             = None
    var addExpenseData: Option[(String, String, Double, IDate, Option[String])] = None
    var setStrategyName: Option[String]                                         = None
    var calculateDebtsCalled                                                    = false

    def undo(): Unit                            = undoCalled = true
    def redo(): Unit                            = redoCalled = true
    def quit: Unit                              = quitCalled = true
    def load(): Unit                            = loadCalled = true
    def gotoMainMenu: Unit                      = gotoMainMenuCalled = true
    def gotoGroup(group_name: String): Unit     = gotoGroupName = Some(group_name)
    def addGroup(group_name: String): Unit      = addGroupName = Some(group_name)
    def addUserToGroup(user_name: String): Unit = addUserName = Some(user_name)

    def addExpenseToGroup(
      expense_name: String,
      paid_by: String,
      amount: Double,
      date: IDate,
      shares: Option[String] = None
    ): Unit = addExpenseData = Some((expense_name, paid_by, amount, date, shares))

    def setDebtStrategy(strategy: String): Unit = setStrategyName = Some(strategy)
    def calculateDebts(): Unit                  = calculateDebtsCalled = true

  class TestObserver extends Observer:
    var lastEvent: Option[ObservableEvent]   = None
    def update(event: ObservableEvent): Unit = lastEvent = Some(event)

  "IController" should {

    "extend Observable" in:
      val controller = new TestController
      val observer   = new TestObserver

      controller.add(observer)
      controller.notifyObservers(EventResponse.Success)

      observer.lastEvent shouldBe Some(EventResponse.Success)

    "have app property" in:
      val controller = new TestController
      controller.app shouldBe a[IApp]

    "have argsHandler property" in:
      val controller = new TestController
      controller.argsHandler shouldBe a[ArgsHandler]

    "define undo method" in:
      val controller = new TestController
      controller.undo()
      controller.undoCalled shouldBe true

    "define redo method" in:
      val controller = new TestController
      controller.redo()
      controller.redoCalled shouldBe true

    "define quit method" in:
      val controller = new TestController
      controller.quit
      controller.quitCalled shouldBe true

    "define load method" in:
      val controller = new TestController
      controller.load()
      controller.loadCalled shouldBe true

    "define gotoMainMenu method" in:
      val controller = new TestController
      controller.gotoMainMenu
      controller.gotoMainMenuCalled shouldBe true

    "define gotoGroup method with group name" in:
      val controller = new TestController
      controller.gotoGroup("TestGroup")
      controller.gotoGroupName shouldBe Some("TestGroup")

    "define addGroup method with group name" in:
      val controller = new TestController
      controller.addGroup("NewGroup")
      controller.addGroupName shouldBe Some("NewGroup")

    "define addUserToGroup method with user name" in:
      val controller = new TestController
      controller.addUserToGroup("Alice")
      controller.addUserName shouldBe Some("Alice")

    "define addExpenseToGroup method with all parameters" in:
      val controller = new TestController
      val date       = Date(15, 6, 2025)
      controller.addExpenseToGroup("Dinner", "Alice", 50.0, date, Some("Alice:25_Bob:25"))
      controller.addExpenseData shouldBe Some(("Dinner", "Alice", 50.0, date, Some("Alice:25_Bob:25")))

    "define addExpenseToGroup method with default shares" in:
      val controller = new TestController
      val date       = Date(15, 6, 2025)
      controller.addExpenseToGroup("Lunch", "Bob", 30.0, date)
      controller.addExpenseData shouldBe Some(("Lunch", "Bob", 30.0, date, None))

    "define setDebtStrategy method with strategy name" in:
      val controller = new TestController
      controller.setDebtStrategy("simplified")
      controller.setStrategyName shouldBe Some("simplified")

    "define calculateDebts method" in:
      val controller = new TestController
      controller.calculateDebts()
      controller.calculateDebtsCalled shouldBe true

    "allow updating app property" in:
      val controller = new TestController
      val newApp     = App(Nil, None, None, MainMenuState())
      controller.app = newApp
      controller.app shouldBe newApp
  }
