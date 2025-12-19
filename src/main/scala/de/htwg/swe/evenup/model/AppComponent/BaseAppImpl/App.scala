package de.htwg.swe.evenup.model.AppComponent.BaseAppImpl

import de.htwg.swe.evenup.model.AppComponent.IApp
import de.htwg.swe.evenup.model.StateComponent.IAppState
import de.htwg.swe.evenup.model.GroupComponent.IGroup
import de.htwg.swe.evenup.model.PersonComponent.IPerson

final case class App(
  groups: List[IGroup],
  active_user: Option[IPerson],
  active_group: Option[IGroup],
  state: IAppState
) extends IApp:

  def allGroups: List[IGroup] = groups

  def allUsers: List[IPerson] = groups.flatMap(_.members).distinct

  def addGroup(group: IGroup): IApp = copy(groups = groups :+ group)

  def updateGroup(updatedGroup: IGroup): IApp = copy(groups =
    groups.map(g => if g.name == updatedGroup.name then updatedGroup else g)
  )

  def updateActiveGroup(active_group: Option[IGroup]): IApp = copy(active_group = active_group)

  def updateAppState(state: IAppState): IApp = copy(state = state)

  def containsGroup(group_name: String): Boolean = groups.exists(_.name == group_name)

  def getGroup(group_name: String): Option[IGroup] = groups.find(_.name == group_name)
