package de.htwg.swe.evenup.view.tui

import scala.util.Try
import scala.util.Success
import scala.util.Failure

class Parser:

  def validSharePattern(input: String): Boolean =
    val pattern = """^([A-Za-z]+:\d+(\.\d+)?)(_[A-Za-z]+:\d+(\.\d+)?)*$""".r
    pattern.matches(input)

  def decorateErrorMessage(key: TuiKeys) =
    new ColorDecorator(
      new BorderDecorator(
        new TextComponent(
          s"${if key.description != "unsupportedKey" then s"Wrong usage of ${key.key}!\n" else ""}> ${key.key} ${key.usage}"
        ),
        "="
      ),
      ConsoleColors.BRIGHT_RED
    ).render

  def parseInput(input: String): Try[List[String]] =
    val tokens = input.split(" ").toList
    tokens.head match
      case TuiKeys.help.key           => Success(tokens)
      case TuiKeys.quit.key           => Success(tokens)
      case TuiKeys.undo.key           => Success(tokens)
      case TuiKeys.redo.key           => Success(tokens)
      case TuiKeys.MainMenu.key       => Success(tokens)
      case TuiKeys.calculateDebts.key => Success(tokens)
      case TuiKeys.newGroup.key       =>
        if tokens.length != 2 then Failure(new Exception(decorateErrorMessage(TuiKeys.newGroup)))
        else Success(tokens)
      case TuiKeys.addUserToGroup.key =>
        if tokens.length < 2 then Failure(new Exception(decorateErrorMessage(TuiKeys.addUserToGroup)))
        else Success(tokens)
      case TuiKeys.gotoGroup.key =>
        if tokens.length != 2 then Failure(new Exception(decorateErrorMessage(TuiKeys.gotoGroup)))
        else Success(tokens)
      case TuiKeys.setStrategy.key =>
        if tokens.length != 2 then Failure(new Exception(decorateErrorMessage(TuiKeys.setStrategy)))
        else Success(tokens)
      case TuiKeys.addExpense.key =>
        tokens.length match
          case 4 => // no shares and no date given
            if tokens(3).toDoubleOption == None then Failure(new Exception(decorateErrorMessage(TuiKeys.addExpense)))
            else Success(tokens)
          case 5 => // share given
            if tokens(3).toDoubleOption == None then Failure(new Exception(decorateErrorMessage(TuiKeys.addExpense)))
            else if !validSharePattern(tokens(4)) then Failure(new Exception(decorateErrorMessage(TuiKeys.addExpense)))
            else Success(tokens)
          // TODO: add case for date given
          case _ => Failure(new Exception(decorateErrorMessage(TuiKeys.addExpense)))

      case _ => Failure(new Exception(decorateErrorMessage(TuiKeys.unsupportedKey)))
