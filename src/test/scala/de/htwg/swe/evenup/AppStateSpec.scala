package de.htwg.swe.evenup

import org.scalatest._
import org.scalatest.matchers.should.Matchers
import org.scalatest.wordspec.AnyWordSpec

import de.htwg.swe.evenup._

class AppStateSpec extends AnyWordSpec with Matchers:

  "The list of all groups should return all contained groups" in:
    val p_1   = Person("John")
    val g_1   = Group("Trip", List(p_1), List())
    val g_2   = Group("Party", List(p_1), List())
    val state = AppState(List(g_1, g_2))
    state.allGroups shouldBe List(g_1, g_2)

  "The list of all users should return all distinct users" in:
    val p_1   = Person("John")
    val p_2   = Person("Peter")
    val state = AppState(Nil, List(p_1, p_2))
    state.allUsers should contain theSameElementsAs List(p_1, p_2)

  "When adding a new group it should be included in the state" in:
    val p_1     = Person("John")
    val g_1     = Group("Trip", List(p_1), List())
    val g_2     = Group("Party", List(p_1), List())
    val state   = AppState(List(g_1))
    val updated = state.addGroup(g_2)
    updated.groups shouldBe List(g_1, g_2)

  "When updating a group it should replace the old one with the same name" in:
    val p_1         = Person("John")
    val g_1         = Group("Trip", List(p_1), List())
    val g_1_updated = g_1.updateName(
      "Trip"
    ) // same name, but could also modify members or expenses
    val state   = AppState(List(g_1))
    val updated = state.updateGroup(g_1_updated)
    updated.groups should contain(g_1_updated)

  "When updating a group that does not exist it should remain unchanged" in:
    val p_1     = Person("John")
    val g_1     = Group("Trip", List(p_1), List())
    val g_2     = Group("Party", List(p_1), List())
    val state   = AppState(List(g_1))
    val updated = state.updateGroup(g_2)
    updated shouldBe state

  "When searching for an existing group by name it should return Some(group)" in:
    val p_1   = Person("John")
    val g_1   = Group("Trip", List(p_1), List())
    val state = AppState(List(g_1))
    state.findGroupByName("Trip") shouldBe Some(g_1)

  "When searching for a non-existing group by name it should return None" in:
    val p_1   = Person("John")
    val g_1   = Group("Trip", List(p_1), List())
    val state = AppState(List(g_1))
    state.findGroupByName("Holiday") shouldBe None
