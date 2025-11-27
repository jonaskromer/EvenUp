package de.htwg.swe.evenup.view.tui

import de.htwg.swe.evenup.model.state.AppState

enum TuiKeys(val key: String, val usage: String, val description: String, val allowed: AppState => Boolean):
  case newGroup  extends TuiKeys(":newgroup", "<group name>", "Add a group", _.canAddGroup)
  case gotoGroup extends TuiKeys(":group", "<group name>", "Open a group", _.canGotoGroup)

  case addExpense
      extends TuiKeys(
        ":addexp",
        "<name> <paid_by> <amount> <opt:shares as Person:Amount_Person...>  <date>",
        "Add an expense",
        _.canAddExpense
      )

  case editExpense extends TuiKeys(":editexp", "tbd", "Edit an expense", _.canEditExpense)

  case newTransaction
      extends TuiKeys(
        ":pay",
        "<amount> <to> <opt:from>",
        "Add a new transaction",
        _.canAddTransaction
      )

  case editTransaction extends TuiKeys(":editpay", "tbd", "Edit a transaction", _.canEditTransaction)

  case addUserToGroup
      extends TuiKeys(
        ":adduser",
        "<user name> <user name> ...",
        "Add a user to a group",
        _.canAddUser
      )

  case calculateDebts extends TuiKeys(":debts", "", "Calculate debts for group", _.canCalculateDebts)
  case setStrategy extends TuiKeys(":strategy", "<simplified|normal>", "Set debt calculation strategy", _.canSetStrategy)

  case help     extends TuiKeys(":h", "", "Help", _.canShowHelp)
  case quit     extends TuiKeys(":q", "", "Quit", _.canQuit)
  case MainMenu extends TuiKeys(":m", "", "Go into the main menu", _.canGoToMainMenu)
  case login    extends TuiKeys(":l", "<user name>", "Login", _.canLogin)
