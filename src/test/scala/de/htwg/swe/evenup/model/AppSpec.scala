package de.htwg.swe.evenup.model

import org.scalatest.wordspec.AnyWordSpec

import de.htwg.swe.evenup.model.{App, Group, Person}
import de.htwg.swe.evenup.model.state.MainMenuState
import de.htwg.swe.evenup.model.financial.debt.NormalDebtStrategy

class AppSpec extends AnyWordSpec with Matchers:

  "The list of all groups should return all contained groups" in:
    val p_1   = Person("John")
    val g_1   = Group("Trip", List(p_1), List(), List(), NormalDebtStrategy())
    val g_2   = Group("Party", List(p_1), List(), List(), NormalDebtStrategy())
    val state = App(List(g_1, g_2), None, None, MainMenuState())
    state.allGroups shouldBe List(g_1, g_2)

  "The list of all users should return all distinct users across all groups" in:
    val p_1   = Person("John")
    val p_2   = Person("Peter")
    val g_1   = Group("Trip", List(p_1, p_2), List(), List(), NormalDebtStrategy())
    val g_2   = Group("Party", List(p_2), List(), List(), NormalDebtStrategy())
    val state = App(List(g_1, g_2), None, None, MainMenuState())
    state.allUsers should contain theSameElementsAs List(p_1, p_2)

  "When adding a new group it should be included in the state" in:
    val p_1     = Person("John")
    val g_1     = Group("Trip", List(p_1), List(), List(), NormalDebtStrategy())
    val g_2     = Group("Party", List(p_1), List(), List(), NormalDebtStrategy())
    val state   = App(List(g_1), None, None, MainMenuState())
    val updated = state.addGroup(g_2)
    updated.groups shouldBe List(g_1, g_2)

  "When updating a group it should replace the old one with the same name" in:
    val p_1         = Person("John")
    val g_1         = Group("Trip", List(p_1), List(), List(), NormalDebtStrategy())
    val g_1_updated = g_1.updateName("Trip") // same name, potentially updated members/expenses
    val state   = App(List(g_1), None, None, MainMenuState())
    val updated = state.updateGroup(g_1_updated)
    updated.groups should contain(g_1_updated)

  "When updating a group that does not exist it should remain unchanged" in:
    val p_1     = Person("John")
    val g_1     = Group("Trip", List(p_1), List(), List(), NormalDebtStrategy())
    val g_2     = Group("Party", List(p_1), List(), List(), NormalDebtStrategy())
    val state   = App(List(g_1), None, None, MainMenuState())
    val updated = state.updateGroup(g_2)
    updated shouldBe state

  "When searching for an existing group it should return Some(group)" in:
    val p_1   = Person("John")
    val g_1   = Group("Trip", List(p_1), List(), List(), NormalDebtStrategy())
    val state = App(List(g_1), None, None, MainMenuState())
    state.findGroup(g_1) shouldBe Some(g_1)

  "When searching for a non-existing group it should return None" in:
    val p_1   = Person("John")
    val g_1   = Group("Trip", List(p_1), List(), List(), NormalDebtStrategy())
    val g_2   = Group("Party", List(p_1), List(), List(), NormalDebtStrategy())
    val state = App(List(g_1), None, None, MainMenuState())
    state.findGroup(g_2) shouldBe None

  "The active group should be updated correctly" in:
    val p_1      = Person("John")
    val g_1      = Group("Trip", List(p_1), List(), List(), NormalDebtStrategy())
    val g_2      = Group("Travel", List(p_1), List(), List(), NormalDebtStrategy())
    val state    = App(List(g_1, g_2), None, None, MainMenuState())
    val newState = state.updateActiveGroup(Some(g_2))
    newState.active_group.get shouldBe g_2
