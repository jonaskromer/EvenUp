package de.htwg.swe.evenup.view.gui

import scalafx.Includes._
import scalafx.application.Platform
import scalafx.scene.Scene
import scalafx.scene.control._
import scalafx.scene.layout._
import scalafx.geometry.{Insets, Pos}
import scalafx.scene.paint.Color
import scalafx.scene.shape.Circle
import scalafx.stage.{Modality, Stage}
import scalafx.collections.ObservableBuffer
import scalafx.animation.RotateTransition
import scalafx.util.Duration
import scalafx.scene.text.{Font, FontWeight, Text}
import scalafx.scene.image.{Image, ImageView}
import scala.compiletime.uninitialized

import de.htwg.swe.evenup.control.IController
import de.htwg.swe.evenup.control.EventResponse
import de.htwg.swe.evenup.model.GroupComponent.IGroup
import de.htwg.swe.evenup.util.ObservableEvent

class MainView(controller: IController, loadingIndicator: ProgressIndicator) {

  private val tabPane                         = new TabPane()
  private var groupTabs                       = Map[String, Tab]()
  private var groupListView: ListView[String] = uninitialized
  private var searchField: TextField          = uninitialized

  // HOME
  private val homeTab =
    new Tab {
      text = "Home"
      closable = false
      content = createHomeView()
      onSelectionChanged =
        _ => {
          if (selected.value && searchField != null) {
            updateGroupList(searchField.text.value)
          }
        }
    }

  tabPane.tabs = Seq(homeTab)

  def getRoot: TabPane = tabPane

  private def createHomeView(): VBox = {
    searchField =
      new TextField {
        promptText = "Search groups..."
        prefWidth = 400
        onAction = _ => updateGroupList(text.value)
      }

    val searchBtn =
      new Button {
        text = "Search"
        styleClass += "secondary-button"
        onAction = _ => updateGroupList(searchField.text.value)
      }

    val addGroupBtn =
      new Button {
        text = "+"
        styleClass += "add-button"
        onAction = _ => showAddGroupDialog()
      }

    val searchBox =
      new HBox {
        padding = Insets(20)
        spacing = 10
        alignment = Pos.CenterLeft
        children = Seq(
          searchField,
          searchBtn,
          new Region { hgrow = Priority.Always },
          addGroupBtn
        )
      }

    groupListView =
      new ListView[String] {
        items = ObservableBuffer[String]()
        prefHeight = 600
        onKeyPressed =
          keyEvent => {
            if (keyEvent.code == scalafx.scene.input.KeyCode.Enter) {
              val selected = selectionModel().selectedItem.value
              if (selected != null && !selected.isEmpty) {
                val groupName = selected.split(" - ")(0)
                openGroupTab(groupName)
              }
            }
          }
      }

    val openGroupBtn =
      new Button {
        text = "Open Group"
        styleClass += "secondary-button"
        prefWidth = 150
        prefHeight = 40
        onAction =
          _ => {
            val selected = groupListView.selectionModel().selectedItem.value
            if (selected != null && !selected.isEmpty) {
              val groupName = selected.split(" - ")(0)
              openGroupTab(groupName)
            }
          }
      }

    val listContainer =
      new HBox {
        padding = Insets(20)
        spacing = 10
        children = Seq(
          new VBox {
            children = Seq(groupListView)
            hgrow = Priority.Always
          },
          new VBox {
            spacing = 10
            alignment = Pos.Center
            children = Seq(openGroupBtn)
          }
        )
      }

    updateGroupList()

    new VBox {
      children = Seq(searchBox, listContainer)
    }
  }

  private def updateGroupList(filter: String = ""): Unit = {
    Platform.runLater {
      val allGroups      = controller.app.allGroups
      val filteredGroups =
        if (filter.isEmpty)
          allGroups
        else
          allGroups.filter(_.name.toLowerCase.contains(filter.toLowerCase))
      val items = filteredGroups.map { group =>
        s"${group.name} - ${group.members.length} users - ${group.expenses.length} expenses"
      }
      groupListView.items = ObservableBuffer(items*)
    }
  }

  private def showAddGroupDialog(): Unit = {
    val dialog =
      new Stage {
        initModality(Modality.ApplicationModal)
        title = "Add New Group"
        resizable = true
        onCloseRequest = _ => loadingIndicator.visible = false
      }

    val nameField =
      new TextField {
        promptText = "Group name"
        onAction =
          _ => {
            if (!text.value.isEmpty) {
              loadingIndicator.visible = true
              controller.addGroup(text.value)
              dialog.close()
            }
          }
      }

    val addBtn =
      new Button {
        text = "Add Group"
        styleClass += "primary-button"
        onAction =
          _ => {
            if (!nameField.text.value.isEmpty) {
              loadingIndicator.visible = true
              controller.addGroup(nameField.text.value)
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
              new Label("Enter group name:") {
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

  private def openGroupTab(groupName: String): Unit = {
    controller.gotoGroup(groupName)

    if (!groupTabs.contains(groupName)) {
      val groupTab =
        new Tab {
          text = groupName
          closable = true
          onClosed =
            _ => {
              groupTabs -= groupName
              controller.gotoMainMenu
            }
          onSelectionChanged =
            _ => {
              if (selected.value) {
                controller.gotoGroup(groupName)
              }
            }
        }

      controller.app.getGroup(groupName).foreach { group =>
        groupTab.content = new GroupView(controller, group, loadingIndicator).getRoot
      }

      groupTabs += (groupName -> groupTab)
      tabPane.tabs.add(groupTab)
    }

    groupTabs.get(groupName).foreach { tab =>
      tabPane.selectionModel().select(tab)
    }
  }

  def update(event: ObservableEvent): Unit = {
    Platform.runLater {
      event match {
        case EventResponse.AddGroup(result, group) =>
          homeTab.content = createHomeView()
          loadingIndicator.visible = false

        case EventResponse.GotoGroup(result, group) =>
          groupTabs.get(group.name).foreach { tab =>
            tab.content = new GroupView(controller, group, loadingIndicator).getRoot
          }

        case EventResponse.AddUserToGroup(result, user, updatedGroup) =>
          groupTabs.get(updatedGroup.name).foreach { tab =>
            tab.content = new GroupView(controller, updatedGroup, loadingIndicator).getRoot
          }
          loadingIndicator.visible = false

        case EventResponse.AddExpenseToGroup(result, expense) =>
          controller.app.active_group.foreach { group =>
            groupTabs.get(group.name).foreach { tab =>
              tab.content = new GroupView(controller, group, loadingIndicator).getRoot
            }
          }
          loadingIndicator.visible = false

        case EventResponse.CalculateDebts(result, debts) =>
          controller.app.active_group.foreach { group =>
            groupTabs.get(group.name).foreach { tab =>
              tab.content = new GroupView(controller, group, loadingIndicator).getRoot
            }
          }
          loadingIndicator.visible = false

        case EventResponse.SetDebtStrategy(result, strategy) =>
          controller.app.active_group.foreach { group =>
            groupTabs.get(group.name).foreach { tab =>
              tab.content = new GroupView(controller, group, loadingIndicator).getRoot
            }
          }

        case EventResponse.Undo(result, stackSize) =>
          homeTab.content = createHomeView()
          controller.app.active_group.foreach { group =>
            groupTabs.get(group.name).foreach { tab =>
              tab.content = new GroupView(controller, group, loadingIndicator).getRoot
            }
          }
          loadingIndicator.visible = false

        case EventResponse.Redo(result, stackSize) =>
          homeTab.content = createHomeView()
          controller.app.active_group.foreach { group =>
            groupTabs.get(group.name).foreach { tab =>
              tab.content = new GroupView(controller, group, loadingIndicator).getRoot
            }
          }
          loadingIndicator.visible = false

        case EventResponse.MainMenu =>
          tabPane.tabs.clear()
          tabPane.tabs.add(homeTab)
          groupTabs = Map.empty
          homeTab.content = createHomeView()
          tabPane.selectionModel().select(homeTab)

        case _ => println(s"Unhandled event: $event")
      }
    }
  }

}
