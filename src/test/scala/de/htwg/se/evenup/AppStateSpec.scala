package de.htwg.se.evenup

import org.scalatest._

class AppStateSpec extends AnyWordSpec with Matchers:
    "allGroups should return all groups in list of groups" in:
        val p1 = Person("Monica")
        val p2 = Person("Erica")
        val p3 = Person("Rita")
        val g1 = Group("Vacation", List(p1, p2))
        val g2 = Group("Event", List(p2, p3))
        val as = AppState(List(g1, g2))
        as.allGroups shouldBe List(g1, g2)
    
    "allUser should return all users in list of groups" in:
        val p1 = Person("Monica")
        val p2 = Person("Erica")
        val p3 = Person("Rita")
        val g1 = Group("Vacation", List(p1, p2))
        val g2 = Group("Event", List(p2, p3))
        val as = AppState(List(g1, g2))
        as.allUsers should contain theSameElementsAs List(p1, p2, p3)

    "addGroup should add a Group to an AppState" in:
        val p1 = Person("Monica")
        val p2 = Person("Erica")
        val p3 = Person("Rita")
        val g1 = Group("Vacation", List(p1, p2))
        val g2 = Group("Event", List(p2, p3))
        val g3 = Group("Construction", List(p1, p3))
        val as = AppState(List(g1, g2))
        as.addGroup(g3)
        as.allGroups shouldBe List(g1, g2, g3)