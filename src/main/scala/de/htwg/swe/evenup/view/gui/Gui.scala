package de.htwg.swe.evenup.view.gui

import scalafx.application.{JFXApp3, Platform}
import scalafx.scene.Scene
import scalafx.scene.control.*
import scalafx.scene.layout.*
import scalafx.geometry.{Insets, Pos}
import scalafx.scene.paint.Color
import scalafx.scene.text.{Font, FontWeight, Text}
import scalafx.collections.ObservableBuffer
import scalafx.Includes.*
import de.htwg.swe.evenup.control.*
import de.htwg.swe.evenup.util.{Observer, ObservableEvent}
import de.htwg.swe.evenup.model.{Date, Person, Group}
import de.htwg.swe.evenup.model.state.*
import scala.compiletime.uninitialized

class Gui(controller: Controller) extends Observer with JFXApp3:

  controller.add(this)

  private val groupsList  = ObservableBuffer[String]()
  private val membersList = ObservableBuffer[String]()
  private val expensesList = ObservableBuffer[String]()
  private val debtsList   = ObservableBuffer[String]()

  private val statusMessage = new Text {
    text = "Welcome to EvenUp!"
    fill = Color.Green
    font = Font.font("Arial", FontWeight.Bold, 14)
  }

  private var currentScene: Scene = uninitialized
  private var mainStage: JFXApp3.PrimaryStage = uninitialized

  override def start(): Unit =
    mainStage = new JFXApp3.PrimaryStage {
      title = "EvenUp - split expenses with friends"
      width = 800
      height = 600
    }
    showGroupsPage()
    stage = mainStage

  override def update(event: ObservableEvent): Unit =
    Platform.runLater {
      event match
        case EventResponse.AddGroup(AddGroupResult.Success, group) =>
          updateStatus(s"Added group: ${group.name}", Color.Green)
          updateGroupsList()

        case EventResponse.AddGroup(AddGroupResult.GroupExists, group) =>
          updateStatus(s"Group ${group.name} already exists!", Color.Red)

        case EventResponse.GotoGroup(GotoGroupResult.Success, group) =>
          updateStatus(s"Switched to group: ${group.name}", Color.Green)
          showGroupDetailPage(group)

        case EventResponse.GotoGroup(GotoGroupResult.SuccessEmptyGroup, group) =>
          updateStatus(s"Group ${group.name} is empty. Add some users!", Color.Orange)
          showGroupDetailPage(group)

        case EventResponse.GotoGroup(GotoGroupResult.GroupNotFound, group) =>
          updateStatus(s"Group ${group.name} not found!", Color.Red)

        case EventResponse.AddUserToGroup(AddUserToGroupResult.Success, user, group) =>
          updateStatus(s"Added ${user.name} to group", Color.Green)
          updateMembersAndExpenses()

        case EventResponse.AddUserToGroup(AddUserToGroupResult.UserAlreadyAdded, user, _) =>
          updateStatus(s"User ${user.name} already in group!", Color.Red)

        case EventResponse.AddUserToGroup(AddUserToGroupResult.NoActiveGroup, _, _) =>
          updateStatus("No active group selected!", Color.Red)

        case EventResponse.AddExpenseToGroup(AddExpenseToGroupResult.Success, expense) =>
          updateStatus(s"Added expense: ${expense.name}", Color.Green)
          updateMembersAndExpenses()

        case EventResponse.AddExpenseToGroup(AddExpenseToGroupResult.NoActiveGroup, _) =>
          updateStatus("No active group selected!", Color.Red)

        case EventResponse.AddExpenseToGroup(AddExpenseToGroupResult.PaidByNotFound, expense) =>
          updateStatus(s"User ${expense.paid_by.name} not in group!", Color.Red)

        case EventResponse.AddExpenseToGroup(AddExpenseToGroupResult.InvalidAmount, _) =>
          updateStatus("Invalid amount! Must be positive.", Color.Red)

        case EventResponse.AddExpenseToGroup(AddExpenseToGroupResult.SharesPersonNotFound, _) =>
          updateStatus("Person in shares not found in group!", Color.Red)

        case EventResponse.AddExpenseToGroup(AddExpenseToGroupResult.SharesSumWrong, _) =>
          updateStatus("Share amounts don't match total expense!", Color.Red)

        case EventResponse.CalculateDebts(CalculateDebtsResult.Success, debts) =>
          updateDebtsView(debts)
          updateStatus(s"Calculated ${debts.length} debts", Color.Green)

        case EventResponse.SetDebtStrategy(SetDebtStrategyResult.Success, strategy) =>
          updateStatus(s"Switched to ${strategy} strategy", Color.Green)

        case EventResponse.Undo(UndoResult.Success, _) =>
          updateStatus("Undo successful", Color.Green)
          refreshCurrentPage()

        case EventResponse.Undo(UndoResult.EmptyStack, _) =>
          updateStatus("Nothing to undo", Color.Orange)

        case EventResponse.Redo(RedoResult.Success, _) =>
          updateStatus("Redo successful", Color.Green)
          refreshCurrentPage()

        case EventResponse.Redo(RedoResult.EmptyStack, _) =>
          updateStatus("Nothing to redo", Color.Orange)

        case EventResponse.MainMenu =>
          updateStatus("Returned to main menu", Color.Green)
          showGroupsPage()

        case _ =>
    }

  private def showGroupsPage(): Unit =
    updateGroupsList()

    val groupsListView = new ListView[String] {
      items = groupsList
      prefHeight = 400
      onMouseClicked = event =>
        if event.clickCount == 2 && selectionModel().selectedItem.value != null then
          val groupName = selectionModel().selectedItem.value
          controller.gotoGroup(groupName)
    }

    val groupNameField = new TextField {
      promptText = "Enter group name"
      prefWidth = 300
    }

    val addButton = new Button("Add Group") {
      onAction = _ =>
        val name = groupNameField.text.value.trim
        if name.nonEmpty then
          controller.addGroup(name)
          groupNameField.text = ""
    }

    val layout = new BorderPane {
      padding = Insets(20)
      top = new VBox(10) {
        alignment = Pos.Center
        children = List(
          new Label("Groups") {
            font = Font.font("Arial", FontWeight.Bold, 24)
          },
          new Label("Double-click a group to open it") {
            font = Font.font("Arial", 12)
            textFill = Color.Gray
          }
        )
      }
      center = new VBox(10) {
        padding = Insets(20, 0, 20, 0)
        children = List(groupsListView)
      }
      bottom = new VBox(10) {
        children = List(
          new HBox(10) {
            alignment = Pos.Center
            children = List(
              groupNameField,
              addButton,
            )
          },
          createStatusBar()
        )
      }
    }

    mainStage.scene = new Scene {
      content = layout
    }

  private def showGroupDetailPage(group: Group): Unit =
    updateMembersAndExpenses()
    debtsList.clear()

    val backButton = new Button("← Back to Groups") {
      onAction = _ => controller.gotoMainMenu
    }

    val groupTitle = new Label(group.name) {
      font = Font.font("Arial", FontWeight.Bold, 24)
    }

    val header = new HBox(20) {
      padding = Insets(10)
      alignment = Pos.CenterLeft
      children = List(backButton, groupTitle)
    }

    val memberNameField = new TextField {
      promptText = "Member name"
      prefWidth = 200
    }

    val addMemberButton = new Button("Add Member") {
      onAction = _ =>
        val name = memberNameField.text.value.trim
        if name.nonEmpty then
          controller.addUserToGroup(name)
          memberNameField.text = ""
    }

    val membersListView = new ListView[String] {
      items = membersList
      prefHeight = 150
    }

    val membersSection = new VBox(10) {
      children = List(
        new Label("Members") {
          font = Font.font("Arial", FontWeight.Bold, 16)
        },
        membersListView,
        new HBox(10) {
          children = List(memberNameField, addMemberButton)
        }
      )
    }

    val expenseNameField = new TextField {
      promptText = "Expense name"
    }

    val paidByField = new TextField {
      promptText = "Paid by"
    }

    val amountField = new TextField {
      promptText = "Amount"
      prefWidth = 100
    }

    val addExpenseButton = new Button("Add Expense") {
      onAction = _ =>
        val name      = expenseNameField.text.value.trim
        val paidBy    = paidByField.text.value.trim
        val amountStr = amountField.text.value.trim

        if name.nonEmpty && paidBy.nonEmpty && amountStr.nonEmpty then
          try
            val amount = amountStr.toDouble
            controller.addExpenseToGroup(name, paidBy, amount, Date(1, 1, 2025))
            expenseNameField.text = ""
            paidByField.text = ""
            amountField.text = ""
          catch
            case _: NumberFormatException =>
              updateStatus("Invalid amount format!", Color.Red)
    }

    val expensesListView = new ListView[String] {
      items = expensesList
      prefHeight = 200
    }

    val expensesSection = new VBox(10) {
      children = List(
        new Label("Expenses") {
          font = Font.font("Arial", FontWeight.Bold, 16)
        },
        expensesListView,
        new GridPane {
          hgap = 5
          vgap = 5
          add(expenseNameField, 0, 0)
          add(paidByField, 1, 0)
          add(amountField, 2, 0)
          add(addExpenseButton, 3, 0)
        }
      )
    }

    val calculateDebtsButton = new Button("Calculate Debts") {
      prefWidth = 150
      onAction = _ => controller.calculateDebts()
    }

    val strategyToggle = new ToggleGroup()

    val normalRadio = new RadioButton("Normal") {
      toggleGroup = strategyToggle
      selected = true
      onAction = _ => controller.setDebtStrategy("normal")
    }

    val simplifiedRadio = new RadioButton("Simplified") {
      toggleGroup = strategyToggle
      onAction = _ => controller.setDebtStrategy("simplified")
    }

    val debtsListView = new ListView[String] {
      items = debtsList
      prefHeight = 150
    }

    val debtsSection = new VBox(10) {
      children = List(
        new Label("Debts") {
          font = Font.font("Arial", FontWeight.Bold, 16)
        },
        new HBox(10) {
          alignment = Pos.CenterLeft
          children = List(
            calculateDebtsButton,
            new Label("Strategy:"),
            normalRadio,
            simplifiedRadio
          )
        },
        debtsListView
      )
    }

    val contentArea = new VBox(20) {
      padding = Insets(20)
      children = List(
        membersSection,
        new Separator(),
        expensesSection,
        new Separator(),
        debtsSection
      )
    }

    val scrollPane = new ScrollPane {
      content = contentArea
      fitToWidth = true
    }

    val layout = new BorderPane {
      top = new VBox {
        children = List(
          createMenuBar(),
          header
        )
      }
      center = scrollPane
      bottom = createStatusBar()
    }

    mainStage.scene = new Scene {
      content = layout
    }

  private def createMenuBar(): MenuBar =
    new MenuBar {
      menus = List(
        new Menu("File") {
          items = List(
            new MenuItem("Main Menu") {
              onAction = _ => controller.gotoMainMenu
            },
            new SeparatorMenuItem(),
            new MenuItem("Exit") {
              onAction = _ => controller.quit
            }
          )
        },
        new Menu("Edit") {
          items = List(
            new MenuItem("Undo") {
              onAction = _ => controller.undo()
            },
            new MenuItem("Redo") {
              onAction = _ => controller.redo()
            }
          )
        }
      )
    }

  private def createStatusBar(): HBox =
    new HBox {
      padding = Insets(5)
      style = "-fx-background-color: #f0f0f0;"
      children = List(statusMessage)
    }

  private def updateStatus(message: String, color: Color): Unit =
    statusMessage.text = message
    statusMessage.fill = color

  private def updateGroupsList(): Unit =
    groupsList.clear()
    controller.app.allGroups.foreach(g => groupsList += g.name)

  private def updateMembersAndExpenses(): Unit =
    controller.app.active_group match
      case Some(group) =>
        membersList.clear()
        group.members.foreach(m => membersList += m.name)

        expensesList.clear()
        group.expenses.foreach(e =>
          expensesList += s"${e.name}: ${e.amount}€ (paid by ${e.paid_by.name})"
        )

      case None =>
        membersList.clear()
        expensesList.clear()

  private def updateDebtsView(debts: List[de.htwg.swe.evenup.model.financial.debt.Debt]): Unit =
    debtsList.clear()
    if debts.isEmpty then
      debtsList += "No debts - Everyone is even!"
    else
      debts.foreach(d =>
        debtsList += s"${d.from.name} owes ${d.amount}€ to ${d.to.name}"
      )

  private def refreshCurrentPage(): Unit =
    controller.app.active_group match
      case Some(group) =>
        updateMembersAndExpenses()
        updateGroupsList()
      case None =>
        updateGroupsList()

object GuiApp:
  def apply(controller: Controller): Gui = new Gui(controller)

  def startGui(controller: Controller): Unit =
    val gui = new Gui(controller)
    gui.main(Array())