package de.htwg.swe.evenup.module

import com.google.inject.AbstractModule
import com.google.inject.name.Names
import net.codingwell.scalaguice.ScalaModule

import de.htwg.swe.evenup.model.AppComponent.IApp
import de.htwg.swe.evenup.model.AppComponent.BaseAppImpl.App
import de.htwg.swe.evenup.model.GroupComponent.IGroup
import de.htwg.swe.evenup.model.GroupComponent.BaseGroupImpl.Group
import de.htwg.swe.evenup.model.PersonComponent.IPerson
import de.htwg.swe.evenup.model.PersonComponent.BasePersonImpl.Person
import de.htwg.swe.evenup.model.DateComponent.IDate
import de.htwg.swe.evenup.model.DateComponent.BaseDateImpl.Date
import de.htwg.swe.evenup.model.financial.ExpenseComponent.{IExpense, IExpenseBuilder}
import de.htwg.swe.evenup.model.financial.ExpenseComponent.BaseExpenseImpl.{Expense, ExpenseBuilder}
import de.htwg.swe.evenup.model.financial.ShareComponent.IShare
import de.htwg.swe.evenup.model.financial.ShareComponent.BaseShareImpl.Share
import de.htwg.swe.evenup.model.financial.TransactionComponent.ITransaction
import de.htwg.swe.evenup.model.financial.TransactionComponent.BaseTransactionImpl.Transaction
import de.htwg.swe.evenup.model.financial.DebtComponent.{IDebt, IDebtCalculationStrategy}
import de.htwg.swe.evenup.model.financial.DebtComponent.BaseDebtImpl.{Debt, NormalDebtStrategy, SimplifiedDebtStrategy}
import de.htwg.swe.evenup.model.StateComponent.IAppState
import de.htwg.swe.evenup.model.StateComponent.BaseAppStateImpl.{MainMenuState, InGroupState, InEmptyGroupState}
import de.htwg.swe.evenup.control.IController
import de.htwg.swe.evenup.control.BaseControllerImpl.Controller
import de.htwg.swe.evenup.view.tui.Tui
import de.htwg.swe.evenup.view.gui.Gui

class DefaultModule extends AbstractModule with ScalaModule {

  override def configure(): Unit = 
    bind[IApp].toInstance(
      App(Nil, None, None, new MainMenuState())
    )
    
    bind[IController].to[Controller]

    bind[IApp].to[App]

    bind[IDate].to[Date]

    bind[IDebt].to[Debt]

    bind[IDebtCalculationStrategy].annotatedWith(Names.named("NormalDebtStrategy")).to[NormalDebtStrategy]
    bind[IDebtCalculationStrategy].annotatedWith(Names.named("SimplifiedDebtStrategy")).to[SimplifiedDebtStrategy]

    bind[IExpense].to[Expense]

    bind[IExpenseBuilder].to[ExpenseBuilder]

    bind[IShare].to[Share]

    bind[ITransaction].to[Transaction]

    bind[IGroup].to[Group]

    bind[IPerson].to[Person]

    bind[IAppState].annotatedWith(Names.named("MainMenuState")).to[MainMenuState]
    bind[IAppState].annotatedWith(Names.named("InGroupState")).to[InGroupState]
    bind[IAppState].annotatedWith(Names.named("InEmptyGroupState")).to[InEmptyGroupState]
    
    // Erstellt jeweils ein Singleton von Gui und Tui sobald der Guic Injector startet
    bind[Tui].asEagerSingleton()
    bind[Gui].asEagerSingleton()
}