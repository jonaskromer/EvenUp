package de.htwg.swe.evenup.view.tui

enum TuiKeys(val key: String):
  case newGroup       extends TuiKeys(":ng")
  case newPerson      extends TuiKeys(":np")
  case newExpense     extends TuiKeys(":ne")
  case newTransaction extends TuiKeys(":nt")
  case addUserToGroup extends TuiKeys(":aug")
  case help           extends TuiKeys(":h")
  case quit           extends TuiKeys(":q")
  case proceed        extends TuiKeys(":p")
  case MainMenu       extends TuiKeys(":m")
