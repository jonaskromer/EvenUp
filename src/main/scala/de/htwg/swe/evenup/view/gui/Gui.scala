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

class Gui(controller: Controller) extends Observer with JFXApp3:
  
  controller.add(this)
  
  // Observable buffers for UI components
  private val groupsList = ObservableBuffer[String]()
  private val membersList = ObservableBuffer[String]()
  private val expensesList = ObservableBuffer[String]()
  private val debtsList = ObservableBuffer[String]()
  
  // Status message text component
  private val statusMessage = new Text {
    text = "Welcome to EvenUp!"
    fill = Color.Green
    font = Font.font("Arial", FontWeight.Bold, 14)
  }
  
  override def start(): Unit =
    stage = new JFXApp3.PrimaryStage {
      title = "EvenUp - Expense Tracker"
      width = 1000
      height = 700
      scene = new Scene {
        content = createMainLayout()
      }
    }
    updateGroupsList()
  
  override def update(event: ObservableEvent): Unit =
    Platform.runLater {
      event match
        case EventResponse.AddGroup(AddGroupResult.Success, group) =>
          updateStatus(s"Added group: ${group.name}", Color.Green)
          updateGroupsList()
          updateActiveGroupView()
        
        case EventResponse.AddGroup(AddGroupResult.GroupExists, group) =>
          updateStatus(s"Group ${group.name} already exists!", Color.Red)
        
        case EventResponse.GotoGroup(GotoGroupResult.Success, group) =>
          updateStatus(s"Switched to group: ${group.name}", Color.Green)
          updateActiveGroupView()
        
        case EventResponse.GotoGroup(GotoGroupResult.SuccessEmptyGroup, group) =>
          updateStatus(s"Group ${group.name} is empty. Add some users!", Color.Orange)
          updateActiveGroupView()
        
        case EventResponse.GotoGroup(GotoGroupResult.GroupNotFound, group) =>
          updateStatus(s"Group ${group.name} not found!", Color.Red)
        
        case EventResponse.AddUserToGroup(AddUserToGroupResult.Success, user, group) =>
          updateStatus(s"Added ${user.name} to group", Color.Green)
          updateActiveGroupView()
        
        case EventResponse.AddUserToGroup(AddUserToGroupResult.UserAlreadyAdded, user, _) =>
          updateStatus(s"User ${user.name} already in group!", Color.Red)
        
        case EventResponse.AddUserToGroup(AddUserToGroupResult.NoActiveGroup, _, _) =>
          updateStatus("No active group selected!", Color.Red)
        
        case EventResponse.AddExpenseToGroup(AddExpenseToGroupResult.Success, expense) =>
          updateStatus(s"Added expense: ${expense.name}", Color.Green)
          updateActiveGroupView()
        
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
          updateGroupsList()
          updateActiveGroupView()
        
        case EventResponse.Undo(UndoResult.EmptyStack, _) =>
          updateStatus("Nothing to undo", Color.Orange)
        
        case EventResponse.Redo(RedoResult.Success, _) =>
          updateStatus("Redo successful", Color.Green)
          updateGroupsList()
          updateActiveGroupView()
        
        case EventResponse.Redo(RedoResult.EmptyStack, _) =>
          updateStatus("Nothing to redo", Color.Orange)
        
        case EventResponse.MainMenu =>
          updateStatus("Returned to main menu", Color.Green)
          updateActiveGroupView()
        
        case _ =>
    }
  
  private def createMainLayout(): BorderPane =
    new BorderPane {
      padding = Insets(10)
      top = createMenuBar()
      center = createCenterPane()
      bottom = createStatusBar()
    }
  
  private def createMenuBar(): MenuBar =
    new MenuBar {
      menus = List(
        new Menu("File") {
          items = List(
            new MenuItem("Main Menu") {
              onAction = handle { controller.gotoMainMenu }
            },
            new SeparatorMenuItem(),
            new MenuItem("Exit") {
              onAction = handle { controller.quit }
            }
          )
        },
        new Menu("Edit") {
          items = List(
            new MenuItem("Undo") {
              onAction = handle { controller.undo() }
            },
            new MenuItem("Redo") {
              onAction = handle { controller.redo() }
            }
          )
        },
        new Menu("Strategy") {
          items = List(
            new MenuItem("Normal") {
              onAction = handle { controller.setDebtStrategy("normal") }
            },
            new MenuItem("Simplified") {
              onAction = handle { controller.setDebtStrategy("simplified") }
            }
          )
        }
      )
    }
  
  private def createCenterPane(): SplitPane =
    new SplitPane {
      items.addAll(
        createLeftPanel(),
        createRightPanel()
      )
      dividerPositions = 0.3
    }
  
  private def createLeftPanel(): VBox =
    new VBox(10) {
      padding = Insets(10)
      children = List(
        new Label("Groups") {
          font = Font.font("Arial", FontWeight.Bold, 16)
        },
        createGroupsView(),
        createNewGroupSection()
      )
    }
  
  private def createGroupsView(): VBox =
    val listView = new ListView[String] {
      items = groupsList
      prefHeight = 300
      onMouseClicked = event => {
        if (event.clickCount == 2 && selectionModel().selectedItem.value != null) {
          controller.gotoGroup(selectionModel().selectedItem.value)
        }
      }
    }
    
    new VBox(5) {
      children = List(
        listView,
        new Label("Double-click to open group") {
          font = Font.font(10)
          textFill = Color.Gray
        }
      )
    }
  
  private def createNewGroupSection(): VBox =
    val groupNameField = new TextField {
      promptText = "Group name"
    }
    
    val addButton = new Button("Create Group") {
      maxWidth = Double.MaxValue
      onAction = handle {
        val name = groupNameField.text.value.trim
        if (name.nonEmpty) {
          controller.addGroup(name)
          groupNameField.text = ""
        }
      }
    }
    
    new VBox(5) {
      children = List(
        new Separator(),
        groupNameField,
        addButton
      )
    }
  
  private def createRightPanel(): VBox =
    new VBox(10) {
      padding = Insets(10)
      children = List(
        createActiveGroupSection(),
        new Separator(),
        createMembersSection(),
        new Separator(),
        createExpensesSection(),
        new Separator(),
        createDebtsSection()
      )
    }
  
  private def createActiveGroupSection(): VBox =
    val activeGroupLabel = new Label {
      text = controller.app.active_group match
        case Some(group) => group.name
        case None => "No group selected"
      font = Font.font("Arial", FontWeight.Bold, 18)
    }
    
    new VBox(5) {
      children = List(
        activeGroupLabel,
        new Button("Calculate Debts") {
          onAction = handle {
            controller.calculateDebts()
          }
        }
      )
    }
  
  private def createMembersSection(): VBox =
    val memberNameField = new TextField {
      promptText = "Member name"
    }
    
    val addMemberButton = new Button("Add Member") {
      onAction = handle {
        val name = memberNameField.text.value.trim
        if (name.nonEmpty) {
          controller.addUserToGroup(name)
          memberNameField.text = ""
        }
      }
    }
    
    val membersListView = new ListView[String] {
      items = membersList
      prefHeight = 100
    }
    
    new VBox(5) {
      children = List(
        new Label("Members") {
          font = Font.font("Arial", FontWeight.Bold, 14)
        },
        membersListView,
        new HBox(5) {
          children = List(memberNameField, addMemberButton)
        }
      )
    }
  
  private def createExpensesSection(): VBox =
    val expenseNameField = new TextField {
      promptText = "Expense name"
    }
    
    val paidByField = new TextField {
      promptText = "Paid by"
    }
    
    val amountField = new TextField {
      promptText = "Amount"
    }
    
    val addExpenseButton = new Button("Add Expense") {
      onAction = handle {
        val name = expenseNameField.text.value.trim
        val paidBy = paidByField.text.value.trim
        val amountStr = amountField.text.value.trim
        
        if (name.nonEmpty && paidBy.nonEmpty && amountStr.nonEmpty) {
          try {
            val amount = amountStr.toDouble
            controller.addExpenseToGroup(name, paidBy, amount, Date(1, 1, 2025))
            expenseNameField.text = ""
            paidByField.text = ""
            amountField.text = ""
          } catch {
            case _: NumberFormatException =>
              updateStatus("Invalid amount format!", Color.Red)
          }
        }
      }
    }
    
    val expensesListView = new ListView[String] {
      items = expensesList
      prefHeight = 150
    }
    
    new VBox(5) {
      children = List(
        new Label("Expenses") {
          font = Font.font("Arial", FontWeight.Bold, 14)
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
  
  private def createDebtsSection(): VBox =
    val debtsListView = new ListView[String] {
      items = debtsList
      prefHeight = 100
    }
    
    new VBox(5) {
      children = List(
        new Label("Debts") {
          font = Font.font("Arial", FontWeight.Bold, 14)
        },
        debtsListView
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
  
  private def updateActiveGroupView(): Unit =
    controller.app.active_group match
      case Some(group) =>
        // Update members
        membersList.clear()
        group.members.foreach(m => membersList += m.name)
        
        // Update expenses
        expensesList.clear()
        group.expenses.foreach(e => 
          expensesList += s"${e.name}: ${e.amount}€ (paid by ${e.paid_by.name})"
        )
        
        // Clear debts when group changes
        debtsList.clear()
      
      case None =>
        membersList.clear()
        expensesList.clear()
        debtsList.clear()
  
  private def updateDebtsView(debts: List[de.htwg.swe.evenup.model.financial.debt.Debt]): Unit =
    debtsList.clear()
    if (debts.isEmpty) {
      debtsList += "No debts - Everyone is even!"
    } else {
      debts.foreach(d => 
        debtsList += s"${d.from.name} owes ${d.amount}€ to ${d.to.name}"
      )
    }

object GuiApp:
  def apply(controller: Controller): Gui = new Gui(controller)
  
  def startGui(controller: Controller): Unit =
    val gui = new Gui(controller)
    gui.main(Array())