package de.htwg.swe.evenup.view.tui

import scala.util.Try
import scala.util.Success
import scala.util.Failure

class Parser {

  def parseNewUser(input: String): Option[String] = {
    val tryParse = Try(input.trim())

    tryParse match
      case Success(user)      => Some(user)
      case Failure(exception) =>
        println("Wrong Usage")
        None
  }

}
