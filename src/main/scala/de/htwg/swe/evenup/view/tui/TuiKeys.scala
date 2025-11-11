package de.htwg.swe.evenup.view.tui

enum TuiKeys(val key: String, val usage: String, val description: String):
  case newGroup        extends TuiKeys(":newgroup", "<group name>", "Add a group")
  case gotoGroup       extends TuiKeys(":group", "<group name>", "Open a group")
  case addExpense      extends TuiKeys(":addexp", "<name> <amount> <date> <paid_by> <opt:shares>", "Add an expense")
  case editExpense     extends TuiKeys(":editexp", "tbd", "Edit an expense")
  case newTransaction  extends TuiKeys(":pay", "tbd", "Add a new transaction")
  case editTransaction extends TuiKeys(":editpay", "tbd", "Edit a transaction")
  case addUserToGroup  extends TuiKeys(":adduser", "<user name> <user name> ...", "Add a user to a group")
  case help            extends TuiKeys(":h", "", "Help")
  case quit            extends TuiKeys(":q", "", "Quit")
  case MainMenu        extends TuiKeys(":m", "", "Go into the main menu")
