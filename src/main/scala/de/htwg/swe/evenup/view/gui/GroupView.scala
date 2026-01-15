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
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import javafx.util.StringConverter

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
      }

    val addMemberBtn =
      new Button {
        text = "+"
        styleClass += "add-button"
        prefWidth = 50
        prefHeight = 50
        onAction = _ => showAddMemberDialog()
      }

    val header =
      new HBox {
        padding = Insets(10)
        spacing = 10
        alignment = Pos.CenterLeft
        children = Seq(
          new Label("Members") {
            styleClass += "section-header"
          },
          new Region { hgrow = Priority.Always },
          addMemberBtn
        )
      }

    new VBox {
      padding = Insets(10)
      children = Seq(header, membersList)
    }
  }

  private def showAddMemberDialog(): Unit = {
    val dialog =
      new Stage {
        initModality(Modality.ApplicationModal)
        title = "Add Member"
        resizable = true
        onCloseRequest = _ => loadingIndicator.visible = false
      }

    val nameField =
      new TextField {
        promptText = "Member name"
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
        styleClass += "primary-button"
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
        styleClass += "cancel-button"
        onAction =
          _ => {
            loadingIndicator.visible = false
            dialog.close()
          }
      }

    dialog.scene =
      new Scene {
        stylesheets.add(getClass.getResource("/styles.css").toExternalForm)
        content =
          new VBox {
            padding = Insets(20)
            spacing = 20
            alignment = Pos.Center
            vgrow = Priority.Always
            children = Seq(
              new Label("Enter member name:") {
                styleClass += "dialog-label"
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
      }

    val addExpenseBtn =
      new Button {
        text = "+"
        styleClass += "add-button"
        prefWidth = 50
        prefHeight = 50
        onAction = _ => showAddExpenseDialog()
      }

    val header =
      new HBox {
        padding = Insets(10)
        spacing = 10
        alignment = Pos.CenterLeft
        children = Seq(
          new Label("Expenses") {
            styleClass += "section-header"
          },
          new Region { hgrow = Priority.Always },
          addExpenseBtn
        )
      }

    // Line chart
    val xAxis =
      new CategoryAxis {
        label = "Month"
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
      }

    lineChart.data = createExpenseChartData()

    new VBox {
      padding = Insets(10)
      spacing = 10
      children = Seq(header, expensesList, lineChart)
    }
  }

  private def createExpenseChartData(): ObservableBuffer[javafx.scene.chart.XYChart.Series[String, Number]] = {
    val series = new javafx.scene.chart.XYChart.Series[String, Number]()
    series.setName("Total Expenses")

    val expensesByYearMonth = group.expenses.groupBy(e => (e.date.year, e.date.month)).toSeq.sortBy(_._1)

    expensesByYearMonth.foreach { case ((year, month), expenses) =>
      val total     = expenses.map(_.amount).sum
      val dateLabel = f"${month}%02d.${year}"
      series.getData.add(new javafx.scene.chart.XYChart.Data[String, Number](dateLabel, total))
    }

    ObservableBuffer(series)
  }

  private def showAddExpenseDialog(): Unit = {
    val dialog =
      new Stage {
        initModality(Modality.ApplicationModal)
        title = "Add Expense"
        resizable = true
        onCloseRequest = _ => loadingIndicator.visible = false
      }

    val nameField =
      new TextField {
        promptText = "Expense name"
        hgrow = Priority.Always
      }

    val paidByCombo =
      new ComboBox[String] {
        items = ObservableBuffer(group.members.map(_.name)*)
        promptText = "Select payer"
        hgrow = Priority.Always
      }

    val amountField =
      new TextField {
        promptText = "Amount (e.g., 50.00)"
        hgrow = Priority.Always
      }

    val datePicker =
      new DatePicker {
        hgrow = Priority.Always
        val germanFormatter = DateTimeFormatter.ofPattern("dd.MM.yyyy")
        converter =
          new StringConverter[LocalDate] {
            override def toString(date: LocalDate): String = {
              if (date == null)
                ""
              else
                germanFormatter.format(date)
            }
            override def fromString(string: String): LocalDate = {
              if (string == null || string.isEmpty)
                null
              else
                try {
                  LocalDate.parse(string, germanFormatter)
                } catch {
                  case _: Exception => null
                }
            }
          }
      }

    val shareRowsContainer =
      new VBox {
        spacing = 8
        hgrow = Priority.Always
      }

    def getAvailableMembers(excludeNames: Set[String] = Set.empty): ObservableBuffer[String] = {
      ObservableBuffer(group.members.map(_.name).filterNot(excludeNames.contains)*)
    }

    val shareRows = scala.collection.mutable.ListBuffer[(ComboBox[String], TextField, HBox)]()

    var isUpdating = false

    def updateAvailableMembers(): Unit = {
      if (isUpdating)
        return
      isUpdating = true

      try {
        val selectedNames = shareRows.map(_._1.value.value).filter(_ != null).toSet
        shareRows.foreach { case (combo, _, _) =>
          val currentValue = combo.value.value
          val newItems     = getAvailableMembers(selectedNames - currentValue)

          if (combo.items.value.toSet != newItems.toSet) {
            combo.items = newItems
            if (currentValue != null && newItems.contains(currentValue)) {
              combo.value = currentValue
            }
          }
        }
      } finally {
        isUpdating = false
      }
    }

    def createShareRow(): HBox = {
      val personCombo =
        new ComboBox[String] {
          items = getAvailableMembers()
          promptText = "Select person"
          onAction =
            _ => {
              if (!isUpdating) {
                updateAvailableMembers()
              }
            }
        }

      val amountTextField =
        new TextField {
          promptText = "Amount"
        }

      val rowBox =
        new HBox {
          spacing = 8
          alignment = Pos.Center
        }

      val removeBtn =
        new Button {
          text = "−"
          styleClass += "cancel-button"
          prefWidth = 40
          prefHeight = 30
          onAction =
            _ => {
              shareRows.find(_._3.equals(rowBox)).foreach { row =>
                shareRows -= row
                shareRowsContainer.children.remove(rowBox)
                updateAvailableMembers()
              }
            }
        }

      rowBox.children = Seq(personCombo, amountTextField, removeBtn)

      shareRows += ((personCombo, amountTextField, rowBox))
      rowBox
    }

    val addShareRowBtn =
      new Button {
        text = "+"
        styleClass += "add-button"
        prefWidth = 50
        prefHeight = 50
        onAction =
          _ => {
            if (shareRows.size < group.members.size) {
              shareRowsContainer.children.add(createShareRow())
              updateAvailableMembers()
            } else {
              showErrorAlert("All members already have shares assigned.")
            }
          }
      }

    val sharesSection =
      new VBox {
        spacing = 10
        alignment = Pos.Center
        children = Seq(
          new Label("Shares (optional):") { 
            styleClass += "form-label"
            alignment = Pos.Center
          },
          shareRowsContainer,
          new HBox {
            spacing = 10
            alignment = Pos.Center
            children = Seq(addShareRowBtn)
          }
        )
      }

    val errorLabel =
      new Label {
        styleClass += "error-label"
        visible = false
        wrapText = true
        hgrow = Priority.Always
      }

    val addBtn =
      new Button {
        text = "Add Expense"
        styleClass += "primary-button"
        prefWidth = 120
        onAction =
          _ => {
            try {
              errorLabel.visible = false
              val name         = nameField.text.value
              val paidBy       = paidByCombo.value.value
              val amount       = amountField.text.value.toDouble
              val selectedDate = datePicker.value.value
              val date         = Date(selectedDate.getDayOfMonth, selectedDate.getMonthValue, selectedDate.getYear)

              val shares: Option[String] =
                if (shareRows.isEmpty) {
                  None
                } else {
                  val sharesString = shareRows
                    .filter { case (combo, textField, _) =>
                      combo.value.value != null && !textField.text.value.trim.isEmpty
                    }
                    .map { case (combo, textField, _) => s"${combo.value.value}:${textField.text.value}" }
                    .mkString("_")

                  if (sharesString.isEmpty)
                    None
                  else
                    Some(sharesString)
                }

              if (name.isEmpty || paidBy == null || amount <= 0) {
                errorLabel.text = "Please fill all required fields correctly"
                errorLabel.visible = true
              } else {
                var isValid = true
                if (shares.isDefined) {
                  val shareSum =
                    shareRows
                      .filter { case (combo, textField, _) =>
                        combo.value.value != null && !textField.text.value.trim.isEmpty
                      }
                      .map { case (_, textField, _) => textField.text.value.toDouble }
                      .sum

                  if (Math.abs(shareSum - amount) > 0.01) {
                    errorLabel.text = f"Share sum ($shareSum%.2f€) does not match total amount ($amount%.2f€)"
                    errorLabel.visible = true
                    isValid = false
                  }
                }

                if (isValid) {
                  loadingIndicator.visible = true
                  controller.addExpenseToGroup(name, paidBy, amount, date, shares)
                  dialog.close()
                }
              }
            } catch {
              case e: NumberFormatException =>
                errorLabel.text = "Invalid number format. Please check amount and date values."
                errorLabel.visible = true
              case e: Exception =>
                errorLabel.text = s"Error: ${e.getMessage}"
                errorLabel.visible = true
            }
          }
      }

    val cancelBtn =
      new Button {
        text = "Cancel"
        styleClass += "cancel-button"
        prefWidth = 120
        onAction =
          _ => {
            loadingIndicator.visible = false
            dialog.close()
          }
      }

    val scrollPane =
      new ScrollPane {
        fitToWidth = true
        content =
          new VBox {
            padding = Insets(20)
            spacing = 15
            prefWidth = 300
            alignment = Pos.Center
            children = Seq(
              new Label("Expense Name:") { 
                styleClass += "form-label"
                alignment = Pos.Center
              },
              nameField,
              new Label("Paid By:") { 
                styleClass += "form-label"
                alignment = Pos.Center
              },
              paidByCombo,
              new Label("Amount:") { 
                styleClass += "form-label"
                alignment = Pos.Center
              },
              amountField,
              new Label("Date:") { 
                styleClass += "form-label"
                alignment = Pos.Center
              },
              datePicker,
              sharesSection,
              errorLabel,
              new HBox {
                spacing = 10
                alignment = Pos.Center
                children = Seq(addBtn, cancelBtn)
              }
            )
          }
        fitToWidth = true
      }

    dialog.scene =
      new Scene {
        stylesheets.add(getClass.getResource("/styles.css").toExternalForm)
        content = scrollPane
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

  private def createDebtsPane(): VBox = {
    val tGroup = new ToggleGroup()

    val normalRadio =
      new RadioButton {
        text = "Normal"
        selected = group.debt_strategy.toString == "normal"
      }
    normalRadio.toggleGroup = tGroup

    val simplifiedRadio =
      new RadioButton {
        text = "Simplified"
        selected = group.debt_strategy.toString == "simplified"
      }
    simplifiedRadio.toggleGroup = tGroup

    val calculateBtn =
      new Button {
        text = "Calculate Debts"
        styleClass += "secondary-button"
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
        text = {
          val debts = group.calculateDebt()
          if (debts.nonEmpty)
            debts.map(_.toString).mkString("\n")
          else
            "Click 'Calculate Debts' to see results"
        }
      }

    // Bar chart
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
      }

    updateDebtsChart(barChart)

    val header =
      new Label("Debt Calculation") {
        styleClass += "section-header"
      }

    new VBox {
      padding = Insets(10)
      spacing = 10
      alignment = Pos.TopCenter
      children = Seq(
        header,
        new HBox {
          spacing = 15
          alignment = Pos.Center
          padding = Insets(10)
          children = Seq(normalRadio, simplifiedRadio)
        },
        calculateBtn,
        new Label("Debts:") {
          styleClass += "form-label"
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
