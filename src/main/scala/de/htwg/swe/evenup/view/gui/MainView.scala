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

import de.htwg.swe.evenup.control.IController
import de.htwg.swe.evenup.control.EventResponse
import de.htwg.swe.evenup.model.GroupComponent.IGroup
import de.htwg.swe.evenup.util.Observer
import de.htwg.swe.evenup.util.ObservableEvent

class MainView(controller: IController) extends Observer {
  
  private val tabPane = new TabPane()
  private var groupTabs = Map[String, Tab]()
  private var groupListView: ListView[String] = _
  private var searchField: TextField = _
  private var loadingIndicator: ProgressIndicator = _
  
  createLoadingIndicator()
  
  // Top Menu Bar
  private val menuBar = new HBox {
    padding = Insets(10)
    spacing = 20
    alignment = Pos.CenterLeft
    style = "-fx-background-color: #2c3e50;"
    
    children = Seq(
      createLogo(),
      createUndoButton(),
      createRedoButton(),
      new Region { hgrow = Priority.Always },
      loadingIndicator
    )
  }
  
  // Home Tab (unclosable)
  private val homeTab = new Tab {
    text = "Home"
    closable = false
    content = createHomeView()
    onSelectionChanged = _ => {
      if (selected.value && searchField != null) {
        updateGroupList(searchField.text.value)
      }
    }
  }
  
  tabPane.tabs = Seq(homeTab)
  
  private val root = new BorderPane {
    top = menuBar
    center = tabPane
  }
  
  def getRoot: BorderPane = root
  
  private def createLogo(): HBox = {
    new HBox {
      spacing = 10
      alignment = Pos.CenterLeft
      children = Seq(
        new ImageView {
          image = new Image(getClass.getResource("/images/title_image.png").toString)
          fitWidth = 100
          fitHeight = 40
          preserveRatio = true
        }
      )
    }
  }
  
  private def createUndoButton(): Button = {
    new Button {
      text = "↶ Undo"
      style = "-fx-background-color: #34495e; -fx-text-fill: white;"
      onAction = _ => {
        loadingIndicator.visible = true
        controller.undo()
      }
    }
  }
  
  private def createRedoButton(): Button = {
    new Button {
      text = "↷ Redo"
      style = "-fx-background-color: #34495e; -fx-text-fill: white;"
      onAction = _ => {
        loadingIndicator.visible = true
        controller.redo()
      }
    }
  }
  
  private def createLoadingIndicator(): Unit = {
    loadingIndicator = new ProgressIndicator {
    style = "-fx-accent: white;"
      prefWidth = 25
      prefHeight = 25
      visible = false
    }
  }
  
  private def createHomeView(): VBox = {
    searchField = new TextField {
      promptText = "Search groups..."
      prefWidth = 400
    }
    
    val searchBtn = new Button {
      text = "Search"
      style = "-fx-background-color: #3498db; -fx-text-fill: white;"
      onAction = _ => updateGroupList(searchField.text.value)
    }
    
    val addGroupBtn = new Button {
      text = "+"
      style = "-fx-font-size: 24px; -fx-background-color: #27ae60; -fx-text-fill: white; -fx-background-radius: 25;"
      prefWidth = 50
      prefHeight = 50
      onAction = _ => showAddGroupDialog()
    }
    
    val searchBox = new HBox {
      padding = Insets(20)
      spacing = 10
      alignment = Pos.CenterLeft
      children = Seq(searchField, searchBtn, addGroupBtn)
    }
    
    groupListView = new ListView[String] {
      items = ObservableBuffer[String]()
      prefHeight = 600
    }
    
    val openGroupBtn = new Button {
      text = "Open Group"
      style = "-fx-background-color: #3498db; -fx-text-fill: white; -fx-font-size: 14px;"
      prefWidth = 150
      prefHeight = 40
      onAction = _ => {
        val selected = groupListView.selectionModel().selectedItem.value
        if (selected != null && !selected.isEmpty) {
          val groupName = selected.split(" - ")(0)
          openGroupTab(groupName)
        }
      }
    }
    
    val listContainer = new HBox {
      padding = Insets(20)
      spacing = 10
      children = Seq(
        new VBox {
          children = Seq(groupListView)
          hgrow = Priority.Always
        },
        new VBox {
          spacing = 10
          alignment = Pos.TopCenter
          children = Seq(openGroupBtn)
        }
      )
    }
    
    // Update list when groups change
    updateGroupList()
    
    new VBox {
      children = Seq(searchBox, listContainer)
    }
  }
  
  private def updateGroupList(filter: String = ""): Unit = {
    Platform.runLater {
      val allGroups = controller.app.allGroups
      val filteredGroups = if (filter.isEmpty) allGroups else allGroups.filter(_.name.toLowerCase.contains(filter.toLowerCase))
      val items = filteredGroups.map { group =>
        s"${group.name} - ${group.members.length} users - ${group.expenses.length} expenses"
      }
      groupListView.items = ObservableBuffer(items*)
    }
  }
  
  private def showAddGroupDialog(): Unit = {
    val dialog = new Stage {
      initModality(Modality.ApplicationModal)
      title = "Add New Group"
      width = 400
      height = 200
      onCloseRequest = _ => loadingIndicator.visible = false
    }
    
    val nameField = new TextField {
      promptText = "Group name"
      prefWidth = 300
    }
    
    val addBtn = new Button {
      text = "Add Group"
      style = "-fx-background-color: #27ae60; -fx-text-fill: white;"
      onAction = _ => {
        if (!nameField.text.value.isEmpty) {
          loadingIndicator.visible = true
          controller.addGroup(nameField.text.value)
          dialog.close()
        }
      }
    }
    
    val cancelBtn = new Button {
      text = "Cancel"
      style = "-fx-background-color: #95a5a6; -fx-text-fill: white;"
      onAction = _ => {
        loadingIndicator.visible = false
        dialog.close()
      }
    }
    
    dialog.scene = new Scene {
      content = new VBox {
        padding = Insets(20)
        spacing = 20
        alignment = Pos.Center
        children = Seq(
          new Label("Enter group name:") {
            style = "-fx-font-size: 14px;"
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
      val groupTab = new Tab {
        text = groupName
        closable = true
        onClosed = _ => {
          groupTabs -= groupName
          controller.gotoMainMenu
        }
        onSelectionChanged = _ => {
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
    
    // Select the tab
    groupTabs.get(groupName).foreach { tab =>
      tabPane.selectionModel().select(tab)
    }
  }
  
  override def update(event: ObservableEvent): Unit = {
    Platform.runLater {
      event match {
        case EventResponse.AddGroup(result, group) =>
          // Refresh home view
          homeTab.content = createHomeView()
          loadingIndicator.visible = false
          
        case EventResponse.GotoGroup(result, group) =>
          // Update active group tab if exists
          groupTabs.get(group.name).foreach { tab =>
            tab.content = new GroupView(controller, group, loadingIndicator).getRoot
          }
          
        case EventResponse.AddUserToGroup(result, user, updatedGroup) =>
          // Refresh the group tab
          groupTabs.get(updatedGroup.name).foreach { tab =>
            tab.content = new GroupView(controller, updatedGroup, loadingIndicator).getRoot
          }
          loadingIndicator.visible = false
          
        case EventResponse.AddExpenseToGroup(result, expense) =>
          // Refresh the active group tab
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
          // Refresh all views
          homeTab.content = createHomeView()
          controller.app.active_group.foreach { group =>
            groupTabs.get(group.name).foreach { tab =>
              tab.content = new GroupView(controller, group, loadingIndicator).getRoot
            }
          }
          loadingIndicator.visible = false
          
        case EventResponse.Redo(result, stackSize) =>
          // Refresh all views
          homeTab.content = createHomeView()
          controller.app.active_group.foreach { group =>
            groupTabs.get(group.name).foreach { tab =>
              tab.content = new GroupView(controller, group, loadingIndicator).getRoot
            }
          }
          loadingIndicator.visible = false
          
        case EventResponse.MainMenu =>
          // Close all group tabs
          tabPane.tabs.clear()
          tabPane.tabs.add(homeTab)
          groupTabs = Map.empty
          homeTab.content = createHomeView()
          tabPane.selectionModel().select(homeTab)
          
        case _ =>
          println(s"Unhandled event: $event")
      }
    }
  }
}