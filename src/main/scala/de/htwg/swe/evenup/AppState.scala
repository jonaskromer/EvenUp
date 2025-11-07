package de.htwg.swe.evenup

final case class AppState(groups: List[Group], users: List[Person] = Nil):

  def allGroups: List[Group] = groups

  def allUsers: List[Person] = users

  def addUser(person: Person): AppState = copy(users = users :+ person)

  def addGroup(group: Group): AppState = copy(groups = groups :+ group)

  def updateGroup(updatedGroup: Group): AppState = copy(groups =
    groups.map(g => if g.name == updatedGroup.name then updatedGroup else g)
  )

  def findGroupByName(name: String): Option[Group] = groups.find(_.name == name)

  def findUserByName(name: String): Option[Person] = users.find(_.name == name)
