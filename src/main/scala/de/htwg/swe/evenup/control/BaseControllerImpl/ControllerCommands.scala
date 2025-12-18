package de.htwg.swe.evenup.control.BaseControllerImpl

import de.htwg.swe.evenup.util.Command
import de.htwg.swe.evenup.model.StateComponent.BaseAppStateImpl.InEmptyGroupState
import de.htwg.swe.evenup.model.StateComponent.BaseAppStateImpl.InGroupState
import de.htwg.swe.evenup.model.StateComponent.BaseAppStateImpl.MainMenuState
import de.htwg.swe.evenup.control.*
import de.htwg.swe.evenup.model.GroupComponent.IGroup
import de.htwg.swe.evenup.model.PersonComponent.IPerson
import de.htwg.swe.evenup.model.financial.ExpenseComponent.IExpense
import de.htwg.swe.evenup.model.financial.DebtComponent.IDebt
import de.htwg.swe.evenup.model.financial.DebtComponent.IDebtCalculationStrategy

case class AddGroupCommand(controller: IController, group: IGroup) extends Command(controller):

  def doStep: Unit =
    controller.app = controller.app.addGroup(group)
    controller.app = controller.app.updateActiveGroup(Some(group))
    controller.app = controller.app.updateAppState(InGroupState())
    controller.notifyObservers(
      EventResponse.AddGroup(AddGroupResult.Success, group)
    )

case class GotoMainMenuCommand(controller: IController) extends Command(controller):

  def doStep: Unit =
    controller.app = controller.app.updateActiveGroup(None)
    controller.app = controller.app.updateAppState(MainMenuState())
    controller.notifyObservers(EventResponse.MainMenu)

case class GotoGroupCommand(controller: IController, group: IGroup) extends Command(controller):

  def doStep: Unit =
    controller.app = controller.app.updateActiveGroup(Some(group))
    controller.notifyObservers(EventResponse.GotoGroup(GotoGroupResult.Success, group))

case class GotoEmptyGroupCommand(controller: IController, group: IGroup) extends Command(controller):

  def doStep: Unit =
    controller.app = controller.app.updateActiveGroup(Some(group))
    controller.notifyObservers(EventResponse.GotoGroup(GotoGroupResult.SuccessEmptyGroup, group))

case class AddUserToGroupCommand(controller: IController, user: IPerson) extends Command(controller):

  def doStep: Unit =
    val updated_group = controller.app.active_group.get.addMember(user)
    controller.app = controller.app.updateGroup(updated_group)
    controller.app = controller.app.updateActiveGroup(Some(updated_group)) // <-- fix
    controller.notifyObservers(EventResponse.AddUserToGroup(AddUserToGroupResult.Success, user, updated_group))

case class AddExpenseToGroupCommand(controller: IController, expense: IExpense) extends Command(controller):

  def doStep: Unit =
    controller.app = controller.app.updateGroup(controller.app.active_group.get.addExpense(expense))
    controller.app = controller.app.updateActiveGroup(Some(controller.app.active_group.get.addExpense(expense)))
    controller.notifyObservers(EventResponse.AddExpenseToGroup(AddExpenseToGroupResult.Success, expense))

case class SetDebtCalculationStrategy(controller: IController, strategy: IDebtCalculationStrategy)
    extends Command(controller):

  def doStep: Unit =
    controller.app = controller.app.updateGroup(controller.app.active_group.get.updateDebtCalculationStrategy(strategy))
    controller.app = controller.app.updateActiveGroup(
      Some(controller.app.active_group.get.updateDebtCalculationStrategy(strategy))
    )
    controller.notifyObservers(EventResponse.SetDebtStrategy(SetDebtStrategyResult.Success, strategy))

case class CalculateDebtsCommand(controller: IController, debts: List[IDebt]) extends Command(controller):
  def doStep: Unit = controller.notifyObservers(EventResponse.CalculateDebts(CalculateDebtsResult.Success, debts))
