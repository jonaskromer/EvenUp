package de.htwg.swe.evenup

final case class AppState(groups: List[Group]):

    def allGroups: List[Group] = groups

    def allUsers: List[Person] =
        groups.flatMap(_.members).distinct

    def addGroup(group: Group): AppState =
        copy(groups = groups :+ group)

    def updateGroup(updatedGroup: Group): AppState =
        copy(groups = groups.map(g => if g.name == updatedGroup.name then updatedGroup else g))

    def findGroupByName(name: String): Option[Group] =
        groups.find(_.name == name)
