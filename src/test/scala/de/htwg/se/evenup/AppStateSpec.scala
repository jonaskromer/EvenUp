package de.htwg.se.evenup

import org.scalatest.wordspec.AnyWordSpec
import org.scalatest.matchers.should.Matchers

class AppStateSpec extends AnyWordSpec with Matchers:
    "allGroups should return all groups in list of groups" in:
        val p1 = Person("Monica")
        val p2 = Person("Erica")
        val p3 = Person("Rita")
        val g1 = Group("Vacation", List(p1, p2), List())
        val g2 = Group("Event", List(p2, p3), List())
        val as = AppState(List(g1, g2))
        as.allGroups shouldBe List(g1, g2)
    
    "allUser should return all users in list of groups" in:
        val p1 = Person("Monica")
        val p2 = Person("Erica")
        val p3 = Person("Rita")
        val g1 = Group("Vacation", List(p1, p2), List())
        val g2 = Group("Event", List(p2, p3), List())
        val as = AppState(List(g1, g2))
        as.allUsers should contain theSameElementsAs List(p1, p2, p3)

    "addGroup should add a Group to an AppState" in:
        val p1 = Person("Monica")
        val p2 = Person("Erica")
        val p3 = Person("Rita")
        val g1 = Group("Vacation", List(p1, p2), List())
        val g2 = Group("Event", List(p2, p3), List())
        val g3 = Group("Construction", List(p1, p3), List())
        val as1 = AppState(List(g1, g2))
        val as2 = as1.addGroup(g3)
        as2.allGroups shouldBe List(g1, g2, g3)

        