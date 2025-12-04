package de.htwg.swe.evenup.view.tui

import de.htwg.swe.evenup.util.{ObservableEvent, Observer}
import de.htwg.swe.evenup.model.Person
import de.htwg.swe.evenup.model.Group
import de.htwg.swe.evenup.model.financial.Share
import de.htwg.swe.evenup.control.*
import de.htwg.swe.evenup.model.financial.debt.NormalDebtStrategy
import de.htwg.swe.evenup.model.state.AppState
import de.htwg.swe.evenup.model.Date

import scala.util.{Try, Success, Failure}

class Tui(controller: Controller) extends Observer:
  
  controller.add(this)

  val parser           = new Parser
  val tuiStringBuilder = new TuiStringBuilder(controller)

  val welcome_message= new ColorDecorator(
          new BorderDecorator(
            new TextComponent(s"""|Welcome to EvenUp!
                                  |Start by adding a group with => ${TuiKeys.newGroup.key} <group name>""".stripMargin),
            "#"
          ),
          ConsoleColors.BRIGHT_BLUE
        ).render

  print(welcome_message + "\n>")

  def printHelp(state: AppState): Unit =
    val keys              = TuiKeys.values.filter(_.allowed(state))
    val maxDescriptionLen = keys.map(_.description.length).max
    val maxKeyLen         = keys.map(_.key.length).max

    val helpText = keys
      .map { key =>
        String.format(
          s"%-${maxDescriptionLen}s ==> %-${maxKeyLen}s %s",
          key.description,
          key.key,
          key.usage
        )
      }
      .mkString("\n")

    val decoratedHelp =
      new HeaderFooterDecorator(
        new ColorDecorator(
          new BorderDecorator(
            new TextComponent(helpText),
            "="
          ),
          ConsoleColors.BRIGHT_YELLOW
        ),
        header = Some("Available Commands"),
        footer = Some("Type a command to get started")
      )

    println(decoratedHelp.render)

  override def update(event: ObservableEvent): Unit =
    event match
      case e: EventResponse if tuiStringBuilder.isDefined(e) =>
        print(
          Seq(
            "\n",
            tuiStringBuilder.handle(e),
            ">"
          ).mkString("\n")
        )
      case e: EventResponse.UncoveredFailure => print(s"##ERROR## =${e._1}")
      case _                                 => println(s"Unhandled event...\n${event}")

  def processInput(input: String): Unit =
    parser.parseInput(input) match
      case Success(tokens) =>
        tokens.head match
          case TuiKeys.help.key           => printHelp(controller.app.state)
          case TuiKeys.MainMenu.key       => controller.gotoMainMenu
          case TuiKeys.calculateDebts.key => controller.calculateDebts()
          case TuiKeys.quit.key           => controller.quit
          case TuiKeys.undo.key           => controller.undo()
          case TuiKeys.redo.key           => controller.redo()
          case TuiKeys.newGroup.key       => controller.addGroup(tokens(1))
          case TuiKeys.gotoGroup.key      => controller.gotoGroup(tokens(1))
          case TuiKeys.setStrategy.key    => controller.setDebtStrategy(tokens(1))
          case TuiKeys.addUserToGroup.key =>
            tokens.drop(1)
              .foreach(user_name => controller.addUserToGroup(user_name))
          case TuiKeys.addExpense.key =>
            controller.addExpenseToGroup(
              tokens(1),
              tokens(2),
              tokens(3).toDouble,
              date = Date(1, 1, 2000), // TODO: fix this
              shares = tokens.lift(4)
            )
      case Failure(error) =>
        print(s"${error.getMessage()}\n>")

