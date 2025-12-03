package de.htwg.swe.evenup.model

import de.htwg.swe.evenup.model.Group
import de.htwg.swe.evenup.model.Person
import de.htwg.swe.evenup.model.state.AppState

final case class App(
  groups: List[Group],
  active_user: Option[Person],
  active_group: Option[Group],
  state: AppState
):

  def allGroups: List[Group] = groups

  def allUsers: List[Person] = groups.flatMap(_.members).distinct

  def addGroup(group: Group): App = copy(groups = groups :+ group)

  def updateGroup(updatedGroup: Group): App = copy(groups =
    groups.map(g => if g.name == updatedGroup.name then updatedGroup else g)
  )

  def updateActiveGroup(active_group: Option[Group]): App = copy(active_group = active_group)

  def updateAppState(state: AppState): App = copy(state = state)

  def containsGroup(group_name: String): Boolean = groups.exists(_.name == group_name)

  def getGroup(group_name: String): Option[Group] = groups.find(_.name == group_name)
