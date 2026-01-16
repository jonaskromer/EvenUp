package de.htwg.swe.evenup.model.AppComponent

import org.scalatest._
import org.scalatest.matchers.should.Matchers
import org.scalatest.wordspec.AnyWordSpec
import de.htwg.swe.evenup.model.PersonComponent.BasePersonImpl.Person
import de.htwg.swe.evenup.model.financial.DebtComponent.BaseDebtImpl.NormalDebtStrategy
import de.htwg.swe.evenup.model.GroupComponent.BaseGroupImpl.Group
import de.htwg.swe.evenup.model.AppComponent.BaseAppImpl.App
import de.htwg.swe.evenup.model.StateComponent.BaseAppStateImpl.MainMenuState
import de.htwg.swe.evenup.model.StateComponent.BaseAppStateImpl.InGroupState

class AppSpec extends AnyWordSpec with Matchers:

  "An App" should {

    val alice    = Person("Alice")
    val bob      = Person("Bob")
    val strategy = NormalDebtStrategy()
    val group1   = Group("Trip", List(alice), Nil, Nil, strategy)
    val group2   = Group("Party", List(bob), Nil, Nil, strategy)
    val state    = MainMenuState()

    "return all groups" in:
      val app = App(List(group1, group2), Some(alice), None, state)
      app.allGroups shouldBe List(group1, group2)

    "return all users from all groups" in:
      val group3 = Group("Dinner", List(alice, bob), Nil, Nil, strategy)
      val app    = App(List(group1, group3), Some(alice), None, state)
      val users  = app.allUsers
      users should contain(alice)
      users should contain(bob)
      users.distinct.length shouldBe users.length

    "add a group" in:
      val app     = App(List(group1), Some(alice), None, state)
      val updated = app.addGroup(group2)
      updated.allGroups should contain(group2)
      updated.allGroups.length shouldBe 2

    "update a group" in:
      val updatedGroup = group1.addMember(bob)
      val app          = App(List(group1), Some(alice), None, state)
      val updated      = app.updateGroup(updatedGroup)
      updated.allGroups.head.members should contain(bob)

    "update active group" in:
      val app     = App(List(group1), Some(alice), None, state)
      val updated = app.updateActiveGroup(Some(group1))
      updated.active_group shouldBe Some(group1)

    "update app state" in:
      val app      = App(List(group1), Some(alice), None, state)
      val newState = InGroupState()
      val updated  = app.updateAppState(newState)
      updated.state shouldBe a[InGroupState]

    "check if contains group by name" in:
      val app = App(List(group1, group2), Some(alice), None, state)
      app.containsGroup("Trip") shouldBe true
      app.containsGroup("Unknown") shouldBe false

    "get group by name" in:
      val app = App(List(group1, group2), Some(alice), None, state)
      app.getGroup("Trip") shouldBe Some(group1)
      app.getGroup("Unknown") shouldBe None
  }
