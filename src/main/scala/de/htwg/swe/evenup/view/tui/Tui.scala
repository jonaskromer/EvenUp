package de.htwg.swe.evenup.view.tui

import de.htwg.swe.evenup.util.{ObservableEvent, Observer}
import de.htwg.swe.evenup.model.Person
import de.htwg.swe.evenup.model.Group
import de.htwg.swe.evenup.model.financial.Share
import de.htwg.swe.evenup.control.*
import de.htwg.swe.evenup.model.financial.debt.NormalDebtStrategy
import de.htwg.swe.evenup.model.state.AppState
import de.htwg.swe.evenup.model.Date

enum PromptState {
  case None
  case GetOrCreateGroup
  case InGroup
  case SetupExpense
  case MainMenu
}

class Tui(controller: Controller) extends Observer {
  var promptState: PromptState = PromptState.None

  val parser   = new Parser
  val prompter = new Prompter

  controller.add(this)

  def spacer = "_" * 40

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

  def buildFullOverviewString: String = Seq(
    spacer,
    controller.app.allGroups.map(_.toString).mkString("\n"),
    spacer
  ).mkString("\n")

  def getAvailableGroupsString: String = Seq(
    spacer,
    "Available Groups:",
    spacer,
    controller.app.allGroups.map(_.name).mkString("\n"),
    spacer
  ).mkString("\n")

  def getActiveGroupString: String =
    controller.app.active_group match
      case Some(group) =>
        val decoratedHeader =
          new ColorDecorator(
            new TextComponent(s"Active Group: ${group.name}"),
            ConsoleColors.BRIGHT_CYAN
          )
        val decoratedGroup =
          new HeaderFooterDecorator(
            new ColorDecorator(
              new BorderDecorator(
                new TextComponent(group.toString)
              ),
              ConsoleColors.BRIGHT_GREEN
            ),
            header = Some(decoratedHeader.render)
          )
        decoratedGroup.render
      case None => ""

  val welcomeText = "Welcome to EvenUp!"
  val instructionText = s"Start by adding a group with => ${TuiKeys.newGroup.key} <group name>"

  val decoratedWelcome =
    new HeaderFooterDecorator(
      new ColorDecorator(
        new BorderDecorator(
          new TextComponent(welcomeText)
        ),
        ConsoleColors.BRIGHT_GREEN
      ),
      footer = Some(instructionText)
    )

  println(decoratedWelcome.render)
  print(">")

  val tuiStringBuilder = new TuiStringBuilder(controller)

  override def update(event: ObservableEvent): Unit =
    event match
      case e: EventResponse if tuiStringBuilder.isDefined(e) =>
        print(
          Seq(
            "\n",
            tuiStringBuilder.handle(e),
            getActiveGroupString,
            ">"
          ).mkString("\n")
        )
      case e: EventResponse.UncoveredFailure => print(s"##ERROR## =${e._1}")
      case _                                 => println(s"Unhandled event...\n${event}")

  def processInput(input: String): Unit =
    val in = input.split(" ").toList
    in.head match
      case TuiKeys.help.key           => printHelp(controller.app.state)
      case TuiKeys.quit.key           => controller.quit
      case TuiKeys.undo.key           => controller.undo()
      case TuiKeys.redo.key           => controller.redo()
      case TuiKeys.newGroup.key       => controller.addGroup(in(1))
      case TuiKeys.addUserToGroup.key =>
        in.drop(1)
          .foreach(user_name => controller.addUserToGroup(user_name))
      case TuiKeys.addExpense.key =>
        controller.addExpenseToGroup(
          in(1),
          in(2),
          in(3).toDouble,
          date = Date(1, 1, 2000), // TODO: fix this
          shares = in.lift(4)
        )
      case TuiKeys.MainMenu.key       => controller.gotoMainMenu
      case TuiKeys.gotoGroup.key      => controller.gotoGroup(in(1))
      case TuiKeys.calculateDebts.key => controller.calculateDebts()
      case TuiKeys.setStrategy.key    => controller.setDebtStrategy(in.drop(1).mkString(" "))
      case _                          =>
        println("This key is not supported... yet :)")
        print(">")
}
