package de.htwg.swe.evenup.view.gui

import scalafx.Includes._
import scalafx.application.Platform
import scalafx.scene.Scene
import scalafx.scene.control._
import scalafx.scene.layout._
import scalafx.geometry.{Insets, Pos}
import scalafx.stage.{Modality, Stage}
import scalafx.collections.ObservableBuffer
import scalafx.scene.chart.{BarChart, CategoryAxis, LineChart, NumberAxis, XYChart}

import de.htwg.swe.evenup.control.IController
import de.htwg.swe.evenup.model.GroupComponent.IGroup
import de.htwg.swe.evenup.model.DateComponent.BaseDateImpl.Date

class GroupView(controller: IController, group: IGroup, loadingIndicator: ProgressIndicator) {

  private val splitPane =
    new SplitPane {
      items.addAll(
        createMembersPane(),
        createExpensesPane(),
        createDebtsPane()
      )
    }

  splitPane.setDividerPositions(0.25, 0.75)

  def getRoot: SplitPane = splitPane

  // MEMBERS
  private def createMembersPane(): VBox = {
    val membersList =
      new ListView[String] {
        items = ObservableBuffer(group.members.map(_.name)*)
        prefHeight = 400
        style = ThemeManager.getSurfaceStyle
      }

    val addMemberBtn =
      new Button {
        text = "+"
        style = ThemeManager.getRoundButtonStyle("secondary")
        prefWidth = 50
        prefHeight = 50
        onAction = _ => showAddMemberDialog()
      }

    val header =
      new HBox {
        padding = Insets(10)
        spacing = 10
        alignment = Pos.CenterLeft
        style = ThemeManager.getBackgroundStyle
        children = Seq(
          new Label("Members") {
            style = ThemeManager.getLargeBoldLabelStyle
          },
          new Region { hgrow = Priority.Always },
          addMemberBtn
        )
      }

    new VBox {
      padding = Insets(10)
      style = ThemeManager.getBackgroundStyle
      children = Seq(header, membersList)
    }
  }

  private def showAddMemberDialog(): Unit = {
    val dialog =
      new Stage {
        initModality(Modality.ApplicationModal)
        title = "Add Member"
        width = 400
        height = 200
        onCloseRequest = _ => loadingIndicator.visible = false
      }

    val nameField =
      new TextField {
        promptText = "Member name"
        prefWidth = 300
        style = ThemeManager.getSurfaceStyle
        onAction =
          _ => {
            if (!text.value.isEmpty) {
              loadingIndicator.visible = true
              controller.addUserToGroup(text.value)
              dialog.close()
            }
          }
      }

    val addBtn =
      new Button {
        text = "Add Member"
        style = ThemeManager.getButtonStyle("secondary")
        onAction =
          _ => {
            if (!nameField.text.value.isEmpty) {
              loadingIndicator.visible = true
              controller.addUserToGroup(nameField.text.value)
              dialog.close()
            }
          }
      }

    val cancelBtn =
      new Button {
        text = "Cancel"
        style = ThemeManager.getButtonStyle("neutral")
        onAction =
          _ => {
            loadingIndicator.visible = false
            dialog.close()
          }
      }

    dialog.scene =
      new Scene {
        root =
          new VBox {
            padding = Insets(20)
            spacing = 20
            alignment = Pos.Center
            style = ThemeManager.getBackgroundStyle
            children = Seq(
              new Label("Enter member name:") {
                style = ThemeManager.getLabelStyle + " -fx-font-size: 14px;"
              },
              nameField,
              new HBox {
                spacing = 10
                alignment = Pos.Center
                children = Seq(addBtn, cancelBtn)
              }
            )
          }
      }

    loadingIndicator.visible = true
    dialog.show()
  }

  // EXPENSES
  private def createExpensesPane(): VBox = {
    val expensesList =
      new ListView[String] {
        items = ObservableBuffer(group.expenses.map(_.toString)*)
        prefHeight = 400
        style = ThemeManager.getSurfaceStyle
      }

    val addExpenseBtn =
      new Button {
        text = "+"
        style = ThemeManager.getRoundButtonStyle("secondary")
        prefWidth = 50
        prefHeight = 50
        onAction = _ => showAddExpenseDialog()
      }

    val header =
      new HBox {
        padding = Insets(10)
        spacing = 10
        alignment = Pos.CenterLeft
        style = ThemeManager.getBackgroundStyle
        children = Seq(
          new Label("Expenses") {
            style = ThemeManager.getLargeBoldLabelStyle
          },
          new Region { hgrow = Priority.Always },
          addExpenseBtn
        )
      }

    // Line chart
    val xAxis =
      new NumberAxis {
        label = "Month"
        autoRanging = true
      }
    val yAxis =
      new NumberAxis {
        label = "Total Amount (€)"
        autoRanging = true
      }

    val lineChart =
      new LineChart(xAxis, yAxis) {
        title = "Expenses Over Time"
        prefHeight = 250
        legendVisible = true
        style = ThemeManager.getSurfaceStyle
      }

    lineChart.data = createExpenseChartData()

    new VBox {
      padding = Insets(10)
      spacing = 10
      style = ThemeManager.getBackgroundStyle
      children = Seq(header, expensesList, lineChart)
    }
  }

  private def createExpenseChartData(): ObservableBuffer[javafx.scene.chart.XYChart.Series[Number, Number]] = {
    val series = new javafx.scene.chart.XYChart.Series[Number, Number]()
    series.setName("Total Expenses")

    if (group.expenses.nonEmpty) {
      // Grouped Expenses by Month
      val expensesByMonth = group.expenses.groupBy(_.date.month).toSeq.sortBy(_._1)

      expensesByMonth.foreach { case (month, expenses) =>
        val total = expenses.map(_.amount).sum
        series.getData.add(new javafx.scene.chart.XYChart.Data[Number, Number](month, total))
      }
    } else {
      // Dummy Data if no Expense
      series.getData.add(new javafx.scene.chart.XYChart.Data[Number, Number](1, 0))
    }

    ObservableBuffer(series)
  }

  private def showAddExpenseDialog(): Unit = {
    val dialog =
      new Stage {
        initModality(Modality.ApplicationModal)
        title = "Add Expense"
        width = 500
        height = 650
        onCloseRequest = _ => loadingIndicator.visible = false
      }

    val nameField =
      new TextField {
        promptText = "Expense name"
        prefWidth = 400
        style = ThemeManager.getSurfaceStyle
      }

    val paidByCombo =
      new ComboBox[String] {
        items = ObservableBuffer(group.members.map(_.name)*)
        promptText = "Select payer"
        prefWidth = 400
        style = ThemeManager.getSurfaceStyle
      }

    val amountField =
      new TextField {
        promptText = "Amount (e.g., 50.00)"
        prefWidth = 400
        style = ThemeManager.getSurfaceStyle
      }

    val dayField =
      new TextField {
        promptText = "DD"
        prefWidth = 125
        style = ThemeManager.getSurfaceStyle
      }
    val monthField =
      new TextField {
        promptText = "MM"
        prefWidth = 125
        style = ThemeManager.getSurfaceStyle
      }
    val yearField =
      new TextField {
        promptText = "YYYY"
        prefWidth = 125
        style = ThemeManager.getSurfaceStyle
      }

    val dateBox =
      new HBox {
        spacing = 5
        alignment = Pos.Center
        children = Seq(dayField, monthField, yearField)
      }

    val sharesArea =
      new TextArea {
        promptText =
          "Shares (format: Person:Amount_Person:Amount)\nExample: Alice:25.0_Bob:25.0\nLeave empty for equal split"
        prefRowCount = 4
        wrapText = true
        prefWidth = 400
        style = ThemeManager.getSurfaceStyle
      }

    val addBtn =
      new Button {
        text = "Add Expense"
        style = ThemeManager.getButtonStyle("secondary")
        prefWidth = 120
        onAction =
          _ => {
            try {
              val name   = nameField.text.value
              val paidBy = paidByCombo.value.value
              val amount = amountField.text.value.toDouble
              val day    = dayField.text.value.toInt
              val month  = monthField.text.value.toInt
              val year   = yearField.text.value.toInt
              val date   = Date(day, month, year)
              val shares =
                if (sharesArea.text.value.trim.isEmpty)
                  None
                else
                  Some(sharesArea.text.value)

              if (name.isEmpty || paidBy == null || amount <= 0) {
                showErrorAlert("Please fill all required fields correctly")
              } else {
                loadingIndicator.visible = true
                controller.addExpenseToGroup(name, paidBy, amount, date, shares)
                dialog.close()
              }
            } catch {
              case e: NumberFormatException =>
                showErrorAlert("Invalid number format. Please check amount and date values.")
              case e: Exception => showErrorAlert(s"Error: ${e.getMessage}")
            }
          }
      }

    val cancelBtn =
      new Button {
        text = "Cancel"
        style = ThemeManager.getButtonStyle("neutral")
        prefWidth = 120
        onAction =
          _ => {
            loadingIndicator.visible = false
            dialog.close()
          }
      }

    dialog.scene =
      new Scene {
        root =
          new VBox {
            padding = Insets(20)
            spacing = 15
            alignment = Pos.TopCenter
            style = ThemeManager.getBackgroundStyle
            children = Seq(
              new Label("Expense Name:") { style = ThemeManager.getBoldLabelStyle },
              nameField,
              new Label("Paid By:") { style = ThemeManager.getBoldLabelStyle },
              paidByCombo,
              new Label("Amount:") { style = ThemeManager.getBoldLabelStyle },
              amountField,
              new Label("Date (DD MM YYYY):") { style = ThemeManager.getBoldLabelStyle },
              dateBox,
              new Label("Shares (optional):") { style = ThemeManager.getBoldLabelStyle },
              sharesArea,
              new HBox {
                spacing = 10
                alignment = Pos.Center
                children = Seq(addBtn, cancelBtn)
              }
            )
          }
      }

    loadingIndicator.visible = true
    dialog.show()
  }

  private def showErrorAlert(message: String): Unit = {
    val alert =
      new Alert(Alert.AlertType.Error) {
        title = "Error"
        headerText = "Invalid Input"
        contentText = message
      }
    alert.show()
  }

  // ===== DEBTS PANE =====
  private def createDebtsPane(): VBox = {
    val tGroup = new ToggleGroup()

    val normalRadio =
      new RadioButton {
        text = "Normal"
        selected = group.debt_strategy.toString == "normal"
        style = ThemeManager.getLabelStyle
      }
    normalRadio.toggleGroup = tGroup

    val simplifiedRadio =
      new RadioButton {
        text = "Simplified"
        selected = group.debt_strategy.toString == "simplified"
        style = ThemeManager.getLabelStyle
      }
    simplifiedRadio.toggleGroup = tGroup

    val calculateBtn =
      new Button {
        text = "Calculate Debts"
        style = ThemeManager.getButtonStyle("primary") + " -fx-font-size: 14px;"
        prefWidth = 200
        onAction =
          _ => {
            val strategy =
              if (normalRadio.selected.value)
                "normal"
              else
                "simplified"
            loadingIndicator.visible = true
            controller.setDebtStrategy(strategy)
            controller.calculateDebts()
          }
      }

    val debtsArea =
      new TextArea {
        editable = false
        prefRowCount = 8
        wrapText = true
        style = ThemeManager.getSurfaceStyle
        text = {
          val debts = group.calculateDebt()
          if (debts.nonEmpty)
            debts.map(_.toString).mkString("\n")
          else
            "Click 'Calculate Debts' to see results"
        }
      }

    // Horizontal bar chart for debts per person
    val xAxis =
      new NumberAxis {
        label = "Amount (€)"
        autoRanging = true
      }
    val yAxis = new CategoryAxis()

    val barChart =
      new BarChart(yAxis, xAxis) {
        title = "Debts per Person"
        prefHeight = 250
        legendVisible = false
        style = ThemeManager.getSurfaceStyle
      }

    updateDebtsChart(barChart)

    val header =
      new Label("Debt Calculation") {
        style = ThemeManager.getLargeBoldLabelStyle
      }

    new VBox {
      padding = Insets(10)
      spacing = 10
      alignment = Pos.TopCenter
      style = ThemeManager.getBackgroundStyle
      children = Seq(
        header,
        new HBox {
          spacing = 15
          alignment = Pos.Center
          padding = Insets(10)
          style = ThemeManager.getBackgroundStyle
          children = Seq(normalRadio, simplifiedRadio)
        },
        calculateBtn,
        new Label("Debts:") {
          style = ThemeManager.getBoldLabelStyle + " -fx-font-size: 14px;"
        },
        debtsArea,
        barChart
      )
    }
  }

  private def updateDebtsChart(barChart: BarChart[String, Number]): Unit = {
    val debts = group.calculateDebt()

    if (debts.nonEmpty) {
      val series = new javafx.scene.chart.XYChart.Series[String, Number]()
      series.setName("Debts")

      // Aggregate debts per person
      val debtMap = scala.collection.mutable.Map[String, Double]()

      debts.foreach { debt =>
        debtMap(debt.from.name) = debtMap.getOrElse(debt.from.name, 0.0) + debt.amount
      }

      debtMap.toSeq.sortBy(-_._2).foreach { case (person, amount) =>
        series.getData.add(new javafx.scene.chart.XYChart.Data[String, Number](person, amount))
      }

      barChart.data = ObservableBuffer(series)
    }
  }

}
