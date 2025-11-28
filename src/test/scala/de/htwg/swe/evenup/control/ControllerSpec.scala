package de.htwg.swe.evenup.control

import org.scalatest._
import org.scalatest.matchers.should.Matchers
import org.scalatest.wordspec.AnyWordSpec

import de.htwg.swe.evenup.model._
import de.htwg.swe.evenup.model.state.MainMenuState
import de.htwg.swe.evenup.model.financial.debt.NormalDebtStrategy
import de.htwg.swe.evenup.control._
import de.htwg.swe.evenup.util._

import scala.collection.mutable.ListBuffer

class ControllerSpec extends AnyWordSpec with Matchers:

  "Controller" should {

    "add a group and notify AddGroup event" in:
      val app        = App(Nil, None, None, MainMenuState())
      val controller = Controller(app)

      val events = ListBuffer.empty[ControllerEvent]
      controller.add(new Observer {
        override def update(event: ObservableEvent): Unit = events.append(
          event.asInstanceOf[ControllerEvent]
        )
      })

      val group = Group("Trip", List(Person("John")), Nil, Nil, NormalDebtStrategy())
      controller.addGroup(group)

      events.head shouldBe ControllerEvent.AddGroup(
        AddGroupResult.Success,
        group
      )
      controller.app.active_group.get shouldBe group

    "goto existing group and notify GotoGroup event" in:
      val john       = Person("John")
      val group      = Group("Trip", List(john), Nil, Nil, NormalDebtStrategy())
      val app        = App(List(group), None, None, MainMenuState())
      val controller = Controller(app)

      val events = ListBuffer.empty[ControllerEvent]
      controller.add(new Observer {
        override def update(event: ObservableEvent): Unit = events.append(
          event.asInstanceOf[ControllerEvent]
        )
      })

      controller.gotoGroup(group)

      events.head shouldBe ControllerEvent.GotoGroup(
        GotoGroupResult.SuccessEmptyGroup,
        group
      )
      controller.app.active_group.get shouldBe group

    "goto non-existing group and notify GroupNotFound" in:
      val app        = App(Nil, None, None, MainMenuState())
      val controller = Controller(app)

      val events = ListBuffer.empty[ControllerEvent]
      controller.add(new Observer {
        override def update(event: ObservableEvent): Unit = events.append(
          event.asInstanceOf[ControllerEvent]
        )
      })

      val group = Group("Trip", Nil, Nil, Nil, NormalDebtStrategy())
      controller.gotoGroup(group)

      events.head shouldBe ControllerEvent.GotoGroup(
        GotoGroupResult.GroupNotFound,
        group
      )
      controller.app.active_group shouldBe None

    "add user to group and notify Success" in:
      val john       = Person("John")
      val jane       = Person("Jane")
      val group      = Group("Trip", List(john), Nil, Nil, NormalDebtStrategy())
      val app        = App(List(group), None, Some(group), MainMenuState())
      val controller = Controller(app)

      val events = ListBuffer.empty[ControllerEvent]
      controller.add(new Observer {
        override def update(event: ObservableEvent): Unit = events.append(
          event.asInstanceOf[ControllerEvent]
        )
      })

      controller.addUserToGroup(jane)

      events.head shouldBe ControllerEvent.AddUserToGroup(
        AddUserToGroupResult.Success,
        jane
      )
      controller.app.active_group.get.members should contain(jane)

    "not add a user twice and notify UserAlreadyAdded" in:
      val john       = Person("John")
      val group      = Group("Trip", List(john), Nil, Nil, NormalDebtStrategy())
      val app        = App(List(group), None, Some(group), MainMenuState())
      val controller = Controller(app)

      val events = ListBuffer.empty[ControllerEvent]
      controller.add(new Observer {
        override def update(event: ObservableEvent): Unit = events.append(
          event.asInstanceOf[ControllerEvent]
        )
      })

      controller.addUserToGroup(john)

      events.head shouldBe ControllerEvent.AddUserToGroup(
        AddUserToGroupResult.UserAlreadyAdded,
        john
      )
      controller.app.active_group.get.members.count(_ == john) shouldBe 1

    "add an expense with shares and notify Success" in:
      val john       = Person("John")
      val jane       = Person("Jane")
      val group      = Group("Trip", List(john, jane), Nil, Nil, NormalDebtStrategy())
      val app        = App(List(group), None, Some(group), MainMenuState())
      val controller = Controller(app)

      val events = ListBuffer.empty[ControllerEvent]
      controller.add(new Observer {
        override def update(event: ObservableEvent): Unit = events.append(
          event.asInstanceOf[ControllerEvent]
        )
      })

      val shares = Some(List(Share(john, 30), Share(jane, 20)))
      controller.addExpenseToGroup("Dinner", john, 50, shares = shares)

      events.head match
        case ControllerEvent.AddExpense(AddExpenseResult.Success, expense) =>
          expense.name shouldBe "Dinner"
          expense.amount shouldBe 50
          expense.shares.map(_.amount) should contain theSameElementsAs List(
            30,
            20
          )
        case _ => fail("Expected AddExpenseSuccess event")

    "add an expense without shares and distribute evenly" in:
      val john       = Person("John")
      val jane       = Person("Jane")
      val group      = Group("Trip", List(john, jane), Nil, Nil, NormalDebtStrategy())
      val app        = App(List(group), None, Some(group), MainMenuState())
      val controller = Controller(app)

      val events = ListBuffer.empty[ControllerEvent]
      controller.add(new Observer {
        override def update(event: ObservableEvent): Unit = events.append(
          event.asInstanceOf[ControllerEvent]
        )
      })

      controller.addExpenseToGroup("Lunch", john, 50)

      events.head match
        case ControllerEvent.AddExpense(AddExpenseResult.Success, expense) =>
          expense.shares.map(_.amount).sum shouldBe 50
          expense.shares.length shouldBe 2
        case _ => fail("Expected AddExpenseSuccess event")

    "return all groups" in:
      val group1     = Group("Trip", Nil, Nil, Nil, NormalDebtStrategy())
      val group2     = Group("Party", Nil, Nil, Nil, NormalDebtStrategy())
      val app        = App(List(group1, group2), None, None, MainMenuState())
      val controller = new Controller(app)
      controller.allGroups should contain theSameElementsAs List(group1, group2)

    "return all distinct users" in:
      val alice      = Person("Alice")
      val bob        = Person("Bob")
      val group1     = Group("Trip", List(alice, bob), Nil, Nil, NormalDebtStrategy())
      val group2     = Group("Party", List(bob), Nil, Nil, NormalDebtStrategy())
      val app        = App(List(group1, group2), None, None, MainMenuState())
      val controller = new Controller(app)
      controller.allUsers should contain theSameElementsAs List(alice, bob)

    "notify observers when going to main menu" in:
      var notified = false
      val observer =
        new de.htwg.swe.evenup.util.Observer:
          override def update(
            e: de.htwg.swe.evenup.util.ObservableEvent
          ): Unit =
            notified =
              e match
                case ControllerEvent.MainMenu => true
                case _                        => false

      val controller = new Controller(App(Nil, None, None, MainMenuState()))
      controller.add(observer)
      controller.gotoMainMenu
      notified shouldBe true

    "notify observers when quitting (without exiting JVM)" in:
      var notified = false
      val observer =
        new de.htwg.swe.evenup.util.Observer:
          override def update(
            e: de.htwg.swe.evenup.util.ObservableEvent
          ): Unit =
            notified =
              e match
                case ControllerEvent.Quit => true
                case _                    => false

      // Subclass Controller to override System.exit
      val controller =
        new Controller(App(Nil, None, None, MainMenuState())) {
          override def quit: Unit = notifyObservers(
            ControllerEvent.Quit
          ) // skip System.exit
        }

      controller.add(observer)
      controller.quit
      notified shouldBe true

    "notify Success when gotoGroup on non-empty group" in:
      val user                                   = Person("Alice")
      val group                                  = Group("Trip", List(user, Person("Bob")), Nil, Nil, NormalDebtStrategy())
      val app                                    = App(List(group), None, None, MainMenuState())
      var notifiedEvent: Option[ControllerEvent] = None

      val controller = new Controller(app)
      val observer   =
        new Observer:
          override def update(e: ObservableEvent): Unit =
            e match
              case ce: ControllerEvent => notifiedEvent = Some(ce)
              case _                   => ()

      controller.add(observer)
      controller.gotoGroup(group)
      notifiedEvent shouldBe Some(
        ControllerEvent.GotoGroup(GotoGroupResult.Success, group)
      )

    "notify NoActiveGroup when adding user without active group" in:
      val user                                   = Person("Alice")
      val controller                             = new Controller(App(Nil, None, None, MainMenuState()))
      var notifiedEvent: Option[ControllerEvent] = None

      val observer =
        new Observer:
          override def update(e: ObservableEvent): Unit =
            e match
              case ce: ControllerEvent => notifiedEvent = Some(ce)
              case _                   => ()

      controller.add(observer)
      controller.addUserToGroup(user)
      notifiedEvent shouldBe Some(
        ControllerEvent.AddUserToGroup(AddUserToGroupResult.NoActiveGroup, user)
      )

    "notify PaidByNotFound when adding expense with invalid payer" in:
      val group                                  = Group("Trip", List(Person("Bob")), Nil, Nil, NormalDebtStrategy())
      val app                                    = App(List(group), None, Some(group), MainMenuState())
      var notifiedEvent: Option[ControllerEvent] = None

      val controller = new Controller(app)
      val observer   =
        new Observer:
          override def update(e: ObservableEvent): Unit =
            e match
              case ce: ControllerEvent => notifiedEvent = Some(ce)
              case _                   => ()

      controller.add(observer)
      controller.addExpenseToGroup("Dinner", Person("Alice"), 50.0)
      notifiedEvent match
        case Some(
              ControllerEvent.AddExpense(
                AddExpenseResult.PaidByNotFound,
                expense
              )
            ) =>
          expense.amount shouldBe 0
        case _ => fail("Expected PaidByNotFound event")

    "correctly adjust remainder shares when dividing expense among members" in:
      val alice                                  = Person("Alice")
      val bob                                    = Person("Bob")
      val group                                  = Group("Trip", List(alice, bob), Nil, Nil, NormalDebtStrategy())
      val app                                    = App(List(group), None, Some(group), MainMenuState())
      var notifiedEvent: Option[ControllerEvent] = None

      val controller = new Controller(app)
      val observer   =
        new Observer:
          override def update(e: ObservableEvent): Unit =
            e match
              case ce: ControllerEvent => notifiedEvent = Some(ce)
              case _                   => ()

      controller.add(observer)
      controller.addExpenseToGroup("Dinner", alice, 100.0, shares = None)

      notifiedEvent match
        case Some(
              ControllerEvent.AddExpense(AddExpenseResult.Success, expense)
            ) =>
          val totalShares = expense.shares.map(_.amount).sum
          totalShares shouldBe 100.0
        case _ => fail("Expected Success expense event")

    "handle no active group on addExpense" in:
      val controller = new Controller(App(Nil, None, None, MainMenuState()))
      controller.addExpenseToGroup("Dinner", Person("Alice"), 50.0)
      succeed

    "correctly assign remainder to first member when dividing expense" in:
      val alice                                  = Person("Alice")
      val bob                                    = Person("Bob")
      val group                                  = Group("Trip", List(alice, bob), Nil, Nil, NormalDebtStrategy())
      val app                                    = App(List(group), None, Some(group), MainMenuState())
      var notifiedEvent: Option[ControllerEvent] = None

      val controller = new Controller(app)
      val observer   =
        new Observer:
          override def update(e: ObservableEvent): Unit =
            e match
              case ce: ControllerEvent => notifiedEvent = Some(ce)
              case _                   => ()

      controller.add(observer)
      // 100.01 split between 2 -> 50.00 + 50.01 (remainder goes to first member)
      controller.addExpenseToGroup("Dinner", alice, 100.01, shares = None)

      notifiedEvent match
        case Some(
              ControllerEvent.AddExpense(AddExpenseResult.Success, expense)
            ) =>
          val aliceShare =
            BigDecimal(expense.shares.find(_.person == alice).get.amount)
              .setScale(2, BigDecimal.RoundingMode.HALF_UP)
              .toDouble
          val bobShare =
            BigDecimal(expense.shares.find(_.person == bob).get.amount)
              .setScale(2, BigDecimal.RoundingMode.HALF_UP)
              .toDouble
          aliceShare shouldBe 50.01
          bobShare shouldBe 50.00
          BigDecimal(expense.shares.map(_.amount).sum)
            .setScale(2, BigDecimal.RoundingMode.HALF_UP)
            .toDouble shouldBe 100.01
        case _ => fail("Expected Success expense event with remainder handled")

  }
