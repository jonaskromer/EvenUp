package de.htwg.swe.evenup.view.gui

import scalafx.scene.layout.{BorderPane, VBox, HBox, GridPane}
import scalafx.scene.control.Separator
import scalafx.scene.control._
import scalafx.scene.paint.Color
import scalafx.scene.text.{Font, FontWeight}
import scalafx.geometry.{Insets, Pos}
import scalafx.collections.ObservableBuffer
import de.htwg.swe.evenup.control.Controller
import de.htwg.swe.evenup.model.{Date, Group}

class GroupDetailPage(controller: Controller, gui: Gui, group: Group) extends BorderPane:
  private val membersList   = ObservableBuffer[String]()
  private val expensesList  = ObservableBuffer[String]()
  private val debtsList     = ObservableBuffer[String]()

  updateMembersAndExpenses()

  private val statusLabel = new Label {
    textFill = Color.Green
    font = Font.font("Arial", FontWeight.Bold, 14)
  }

  private val backButton = new Button("← Back to Groups") {
    onAction = _ => gui.showGroupsPage()
  }

  private val groupTitle = new Label(group.name) {
    font = Font.font("Arial", FontWeight.Bold, 24)
  }

  private val header = new HBox(20) {
    padding = Insets(10)
    alignment = Pos.CenterLeft
    children = List(backButton, groupTitle)
  }

  private val memberNameField = new TextField {
    promptText = "Member name"
    prefWidth = 200
  }

  private val addMemberButton = new Button("Add Member") {
    onAction = _ =>
      val name = memberNameField.text.value.trim
      if name.nonEmpty then
        controller.addUserToGroup(name)
        memberNameField.text = ""
        updateMembersAndExpenses()
        statusLabel.text = s"Added member: $name"
        statusLabel.textFill = Color.Green
  }

  private val membersListView = new ListView[String] {
    items = membersList
    prefHeight = 150
  }

  private val membersSection = new VBox(10) {
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

  private val expenseNameField = new TextField {
    promptText = "Expense name"
  }
  private val paidByField = new TextField {
    promptText = "Paid by"
  }
  private val amountField = new TextField {
    promptText = "Amount"
    prefWidth = 100
  }

  private val addExpenseButton = new Button("Add Expense") {
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
          updateMembersAndExpenses()
          statusLabel.text = s"Added expense: $name"
          statusLabel.textFill = Color.Green
        catch
          case _: NumberFormatException =>
            statusLabel.text = "Invalid amount format!"
            statusLabel.textFill = Color.Red
  }

  private val expensesListView = new ListView[String] {
    items = expensesList
    prefHeight = 200
  }

  private val expensesSection = new VBox(10) {
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

  private val calculateDebtsButton = new Button("Calculate Debts") {
    prefWidth = 150
    onAction = _ =>
      controller.calculateDebts()
      // updateDebtsView(debts) wird vom Observer aufgerufen
  }

  private val strategyToggle = new ToggleGroup()

  private val normalRadio = new RadioButton("Normal") {
    toggleGroup = strategyToggle
    selected = true
    onAction = _ => controller.setDebtStrategy("normal")
  }
  private val simplifiedRadio = new RadioButton("Simplified") {
    toggleGroup = strategyToggle
    onAction = _ => controller.setDebtStrategy("simplified")
  }

  private val debtsListView = new ListView[String] {
    items = debtsList
    prefHeight = 150
  }

  private val debtsSection = new VBox(10) {
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

  private val contentArea = new VBox(20) {
    padding = Insets(20)
    children = List(
      membersSection,
      new Separator(),
      expensesSection,
      new Separator(),
      debtsSection
    )
  }

  private val scrollPane = new ScrollPane {
    content = contentArea
    fitToWidth = true
  }

  top = new VBox {
    children = List(
      header
    )
  }
  center = scrollPane
  bottom = new VBox {
    children = List(
      statusLabel
    )
  }

  private def updateMembersAndExpenses(): Unit =
    membersList.clear()
    expensesList.clear()
    debtsList.clear()
    group.members.foreach(m => membersList += m.name)
    group.expenses.foreach(e =>
      expensesList += s"${e.name}: ${e.amount}€ (paid by ${e.paid_by.name})"
    )