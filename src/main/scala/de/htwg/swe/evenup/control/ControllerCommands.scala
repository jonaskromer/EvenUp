package de.htwg.swe.evenup.control

import de.htwg.swe.evenup.util.Command
import de.htwg.swe.evenup.model.Group
import de.htwg.swe.evenup.model.financial.debt.NormalDebtStrategy
import de.htwg.swe.evenup.model.state.MainMenuState
import de.htwg.swe.evenup.model.Person
import de.htwg.swe.evenup.model.financial.Expense
import de.htwg.swe.evenup.model.state.InGroupState
import de.htwg.swe.evenup.model.financial.debt.DebtCalculationStrategy
import de.htwg.swe.evenup.model.financial.debt.Debt

case class AddGroupCommand(controller: Controller, group: Group) extends Command(controller):

  def doStep: Unit =
    controller.app = controller.app.addGroup(group)
    controller.app = controller.app.updateActiveGroup(Some(group))
    controller.app = controller.app.updateAppState(InGroupState())
    controller.notifyObservers(
      EventResponse.AddGroup(AddGroupResult.Success, group)
    )

case class GotoMainMenuCommand(controller: Controller) extends Command(controller):

  def doStep: Unit =
    controller.app = controller.app.updateActiveGroup(None)
    controller.app = controller.app.updateAppState(MainMenuState())
    controller.notifyObservers(EventResponse.MainMenu)

case class GotoGroupCommand(controller: Controller, group: Group) extends Command(controller):

  def doStep: Unit =
    controller.app = controller.app.updateActiveGroup(Some(group))
    controller.notifyObservers(EventResponse.GotoGroup(GotoGroupResult.Success, group))

case class GotoEmptyGroupCommand(controller: Controller, group: Group) extends Command(controller):

  def doStep: Unit =
    controller.app = controller.app.updateActiveGroup(Some(group))
    controller.notifyObservers(EventResponse.GotoGroup(GotoGroupResult.SuccessEmptyGroup, group))

case class AddUserToGroupCommand(controller: Controller, user: Person) extends Command(controller):

  def doStep: Unit =
    val updated_group = controller.app.active_group.get.addMember(user)
    controller.app = controller.app.updateGroup(updated_group)
    controller.app = controller.app.updateActiveGroup(Some(updated_group)) // <-- fix
    controller.notifyObservers(EventResponse.AddUserToGroup(AddUserToGroupResult.Success, user, updated_group))

case class AddExpenseToGroupCommand(controller: Controller, expense: Expense) extends Command(controller):

  def doStep: Unit =
    controller.app = controller.app.updateGroup(controller.app.active_group.get.addExpense(expense))
    controller.app = controller.app.updateActiveGroup(Some(controller.app.active_group.get.addExpense(expense)))
    controller.notifyObservers(EventResponse.AddExpenseToGroup(AddExpenseToGroupResult.Success, expense))

case class SetDebtCalculationStrategy(controller: Controller, strategy: DebtCalculationStrategy)
    extends Command(controller):

  def doStep: Unit =
    controller.app = controller.app.updateGroup(controller.app.active_group.get.updateDebtCalculationStrategy(strategy))
    controller.app = controller.app.updateActiveGroup(
      Some(controller.app.active_group.get.updateDebtCalculationStrategy(strategy))
    )
    controller.notifyObservers(EventResponse.SetDebtStrategy(SetDebtStrategyResult.Success, strategy))

case class CalculateDebtsCommand(controller: Controller, debts: List[Debt]) extends Command(controller):
  def doStep: Unit = controller.notifyObservers(EventResponse.CalculateDebts(CalculateDebtsResult.Success, debts))
