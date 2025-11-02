package de.htwg.swe.evenup.model

import de.htwg.swe.evenup.model.Group
import de.htwg.swe.evenup.model.Person

final case class App(groups: List[Group]):

  def allGroups: List[Group] = groups

  def allUsers: List[Person] = groups.flatMap(_.members).distinct

  def addGroup(group: Group): App = copy(groups = groups :+ group)

  def updateGroup(updatedGroup: Group): App = copy(groups =
    groups.map(g => if g.name == updatedGroup.name then updatedGroup else g)
  )

  def findGroupByName(name: String): Option[Group] = groups.find(_.name == name)
