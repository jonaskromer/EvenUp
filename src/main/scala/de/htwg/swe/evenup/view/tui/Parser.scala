package de.htwg.swe.evenup.view.tui

import scala.util.Try
import scala.util.Success
import scala.util.Failure
import de.htwg.swe.evenup.model.Share
import de.htwg.swe.evenup.model.Person

class Parser {

  def parseNewUser(input: String): Option[String] = {
    val tryParse = Try(input.trim())

    tryParse match
      case Success(user)      => Some(user)
      case Failure(exception) =>
        println("Wrong Usage")
        None
  }

  def parseShares(input: Option[String]): Option[List[Share]] =
    def getShares(input: String) = input.split("_").toList.map { s =>
      s.split(":") match
        case Array(name, amount) => Share(Person(name), amount.toDouble)
        case _ => throw new IllegalArgumentException(s"Invalid share: $s")
    }
    input match
      case Some(in) =>
        val tryParse = Try(getShares(in))
        tryParse match
          case Success(shares)    => Some(shares)
          case Failure(exception) =>
            println("Wrong Usage of shares")
            None
      case None => None

}
