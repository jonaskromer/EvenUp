# EvenUp Copilot Instructions

## Project Overview
EvenUp is a Scala 3 expense management application with MVC architecture. It provides both TUI (terminal) and GUI (JavaFX) interfaces for tracking shared expenses within groups.

## Architecture & Design Patterns

### MVC with State Machine
- **Model**: Immutable domain model (`App`, `Group`, `Person`, `Expense`, `Transaction`, `Debt`)
- **View**: Two implementations - `Tui` (terminal) and `Gui` (JavaFX) both implement `Observer` pattern
- **Control**: `Controller` orchestrates model updates and notifies views via `Observable`
- **State**: `IAppState` implements State pattern to control valid actions per context (MainMenu, InGroup, InEmptyGroup)

### Component-Based Architecture
Each domain entity follows a pattern:
- **Interface layer**: `I<Component>` trait in `<Component>Component/` directory
- **Implementation**: `Base<Component>Impl/` subfolder with concrete class
- Example: `GroupComponent/IGroup.scala` + `GroupComponent/BaseGroupImpl/Group.scala`

### Key Components
- **AppComponent**: Root state holder (`IApp`) - immutable, contains groups, active user, active group, and current state
- **GroupComponent**: Manages members, expenses, transactions; switches debt calculation strategies
- **Financial Components**: 
  - `ExpenseComponent` - uses **Builder pattern** (`ExpenseBuilder`)
  - `DebtComponent` - uses **Strategy pattern** for debt calculation (`IDebtCalculationStrategy`)
  - `TransactionComponent` - payment tracking between members
  - `ShareComponent` - expense share amounts

### Immutability & Functional Style
- All model objects are immutable (case classes or final classes)
- Operations return new instances: `app.addGroup(g)` returns new `IApp`
- Enables undo/redo via `UndoManager` and `Memento` pattern

## Critical Developer Workflows

### Build & Test
```bash
sbt compile          # Compile only
sbt run              # Runs both TUI and GUI (GUI in separate thread)
sbt clean coverage test  # Run tests with coverage
sbt coverageReport   # Generate coverage HTML report
sbt scalafmtCheck    # Check code formatting (Scala 3)
sbt scalafmtAll      # Auto-format code
```

### Debugging
- **TUI**: Terminal interface processes commands via `Tui.processInput()`
- **GUI**: JavaFX runs in separate thread; starts in `EvenUp.scala` main
- View updates triggered by controller calling `notifyObservers()` on `Observable`

### Code Quality
- Project uses SonarCloud integration (`sonar-project.properties`)
- Coverage tracking via Coveralls badge in README
- Scala 3.7.3; JDK 17+ required

## Project-Specific Patterns & Conventions

### Command Processing (TUI)
All TUI commands start with `:` followed by command name. Controller routes via `ArgsHandler`. Examples:
- `:newgroup "Friends"` → `controller.addGroup()`
- `:addexp "Lunch" Alice 30 Bob:10 Charlie:10 2025-11-21` → `addExpenseToGroup()`
- `:pay 15 Bob Alice` → handled via state-dependent actions

### Debt Calculation Strategies
Strategy pattern switchable at runtime:
- `NormalDebtStrategy` - full expense sharing calculation
- `SimplifiedDebtStrategy` - simplified debt settling
Set via controller: `controller.setDebtStrategy("normal"|"simplified")`

### Observer/Observable Pattern
```scala
// Views register as observers
controller.add(tuiObserver)
controller.add(guiObserver)

// Controller notifies all on model changes
controller.notifyObservers(ObservableEvent())
```

### State-Based Validation
State pattern controls what operations are valid:
- `MainMenuState`: only `canAddGroup`, `canGotoGroup`
- `InGroupState`: `canAddExpense`, `canAddTransaction`, `canCalculateDebts`
- `InEmptyGroupState`: limited operations until members added

Check state permissions before executing: `if (state.canAddExpense) ...`

## Integration Points & Dependencies

### External Libraries
- **ScalaFX 22.0.0** - JavaFX wrapper for Scala GUI (with platform-specific binaries)
- **ScalaTest 3.2.19** - unit testing (`src/test/scala`)
- **Munit 1.0.0** - lightweight test framework

### Package Structure
```
de.htwg.swe.evenup
├── model/          # Immutable domain objects
│   ├── AppComponent/
│   ├── GroupComponent/
│   ├── StateComponent/
│   └── financial/
├── control/        # Controller & command handling
├── view/
│   ├── tui/        # Terminal UI
│   ├── gui/        # JavaFX UI
│   └── util/       # View helpers
└── util/           # Observable, UndoManager, Memento
```

## Important Implementation Details

### Adding a New Expense
Uses Builder pattern for complex object construction:
```scala
val expense = ExpenseBuilder()
  .withName("Dinner")
  .paidBy(alice)
  .withAmount(60.0)
  .onDate(Date(25, 12, 2025))
  .withShares(List(Share(alice, 30), Share(bob, 30)))
  .build()
```

### Adding New State
1. Create new class in `StateComponent/BaseAppStateImpl/`
2. Implement `IAppState` trait
3. Define `canX` permissions and `execute()` method
4. Update controller to transition to new state

### Undo/Redo System
- `UndoManager` maintains command history
- Each action creates `Memento` of app state
- Controller calls `undo()` / `redo()` on manager

## Common Gotchas
- **Immutability**: Don't mutate model objects; return new instances
- **GUI Threading**: GUI runs in background thread; use `Platform.runLater()` for thread-safe updates
- **State Checks**: Always verify state permissions before operations (view should validate based on state)
- **Builder Validation**: `ExpenseBuilder.build()` has TODO for argument validation; add guards if extending
