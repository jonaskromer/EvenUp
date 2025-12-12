package de.htwg.swe.evenup.view.gui

import scalafx.scene.layout.{BorderPane, VBox, HBox}
import scalafx.scene.control.{ListView, TextField, Button, Label}
import scalafx.scene.text.{Font, FontWeight}
import scalafx.scene.paint.Color
import scalafx.Includes.*
import scalafx.geometry.{Insets, Pos}
import scalafx.collections.ObservableBuffer
import de.htwg.swe.evenup.control.Controller

class GroupsPage(controller: Controller, gui: Gui) extends BorderPane:
  private val groupsList = ObservableBuffer[String]()
  updateGroupsList()

  private val statusLabel = new Label {
    text = "Welcome to EvenUp!"
    textFill = Color.Green
    font = Font.font("Arial", FontWeight.Bold, 14)
  }

  private val groupsListView = new ListView[String] {
    items = groupsList
    prefHeight = 400
    onMouseClicked = event =>
      if event.clickCount == 2 && selectionModel().selectedItem.value != null then
        val groupName = selectionModel().selectedItem.value
        controller.gotoGroup(groupName)
  }

  private val groupNameField = new TextField {
    promptText = "Enter group name"
    prefWidth = 300
  }

  private val addButton = new Button("Add Group") {
    onAction = _ =>
      val name = groupNameField.text.value.trim
      if name.nonEmpty then
        controller.addGroup(name)
        groupNameField.text = ""
        updateGroupsList()
        statusLabel.text = s"Added group: $name"
        statusLabel.textFill = Color.Green
  }

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
        children = List(groupNameField, addButton)
      },
      new HBox {
        padding = Insets(5)
        style = "-fx-background-color: #f0f0f0;"
        children = List(statusLabel)
      }
    )
  }

  private def updateGroupsList(): Unit =
    groupsList.clear()
    controller.app.allGroups.foreach(g => groupsList += g.name)