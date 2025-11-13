val input = ":addexp gr John:10.0;Peter:2.93"

val (names, sumShares) = input
  .split(" ")(2) //adopt which element is shrares
  .split(";")
  .map(_.split(":"))
  .map { case Array(name, amount) => (name, amount.toDouble) } // builds array of tuples (name, amount)
  .unzip match { // makes one array only names, one only sum Double
    case (nameArray, amountArray) => (nameArray, amountArray.sum)
  }
final case class Share(person: Person, amount: Double)
final case class Person(
  name: String
): // future: mail, friendlist, login, picture

  override def toString(): String = f"$name"

  def updateName(name: String): Person = copy(name = name)

val inpute = "Lennart:40_Bryan:20_Jonas:20"


inpute.split("_").toList.map { s =>
s.split(":") match
  case Array(name, amount) => Share(Person(name), amount.toDouble)
  case _ => throw new IllegalArgumentException(s"Invalid share: $s")
}



// :addexp TBH-Party Bryan 80 Lennart:40_Bryan:20_Jonas:20