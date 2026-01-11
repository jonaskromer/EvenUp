# EvenUp Scala3 App
[![Coverage Status](https://coveralls.io/repos/github/jonaskromer/EvenUp/badge.svg?branch=main)](https://coveralls.io/github/jonaskromer/EvenUp?branch=main)
![GitHub Actions Workflow Status](https://img.shields.io/github/actions/workflow/status/jonaskromer/EvenUp/scala.yml)
[![Security Rating](https://sonarcloud.io/api/project_badges/measure?project=jonaskromer_EvenUp&metric=security_rating)](https://sonarcloud.io/summary/new_code?id=jonaskromer_EvenUp)
[![Maintainability Rating](https://sonarcloud.io/api/project_badges/measure?project=jonaskromer_EvenUp&metric=sqale_rating)](https://sonarcloud.io/summary/new_code?id=jonaskromer_EvenUp)
![GitHub commit activity](https://img.shields.io/github/commit-activity/w/jonaskromer/EvenUp)
![GitHub last commit](https://img.shields.io/github/last-commit/jonaskromer/EvenUp)
![Github Created At](https://img.shields.io/github/created-at/jonaskromer/EvenUp)

![Github Titlebanner](src/main/resources/images/title_image.png)

# EvenUp – An Expense Management App

EvenUp is a simple, console-based (TUI) application designed to help you manage shared expenses and group finances.
With EvenUp, you can:

* Create and manage groups of people.
* Track expenses within groups, including shared expenses.
* Manage transactions between group members.
* Navigate through groups and menus easily using simple text commands.

EvenUp follows an MVC architecture, keeping the domain model (`App`, `Group`, `Person`) immutable, while the TUI provides an interactive interface.

---

## Installation

To install and run EvenUp:

1. Clone the repository:

   ```bash
   git clone https://github.com/yourusername/evenup.git
   cd evenup
   ```
2. Build the project using sbt:

   ```bash
   sbt compile
   ```
3. Run the application:

   ```bash
   sbt run
   ```


4. On MacOS install X-Server
   1. brew install --cask xquartz
   2. open -a XQuartz
      1. Allow connections from network clients
   3. xhost + 127.0.0.1
   4. 
5. docker run -it evenup-evenup
6. docker compose up
7. docker attach evenup-evenup-1


Ensure you have Java (JDK 17 or higher) and sbt installed on your system.

---

## TUI Usage

The following commands are available in the TUI:

| Command     | Usage                                                                      | Description                                |
| ----------- | -------------------------------------------------------------------------- | ------------------------------------------ |
| `:newgroup` | `<group name>`                                                             | Add a new group                            |
| `:group`    | `<group name>`                                                             | Open a specific group                      |
| `:addexp`   | `<name> <paid_by> <amount> <opt:shares as Person:Amount_Person...> <date>` | Add an expense                             |
| `:editexp`  | `tbd`                                                                      | Edit an expense                            |
| `:pay`      | `<amount> <to> <opt:from>`                                                 | Add a new transaction                      |
| `:editpay`  | `tbd`                                                                      | Edit a transaction                         |
| `:adduser`  | `<user name> <user name> ...`                                              | Add one or more users to the current group |
| `:h`        |                                                                            | Show help                                  |
| `:q`        |                                                                            | Quit the app                               |
| `:m`        |                                                                            | Go back to the main menu                   |
| `:l`        | `<user name>`                                                              | Login as a specific user                   |

---

## Example Session

```
:newgroup Friends
:group Friends
:adduser Alice Bob Charlie
:addexp "Lunch" Alice 30 Bob:10 Charlie:10 2025-11-21
:pay 15 Bob Alice
:m
:q
```

This example creates a group, adds users, records an expense, adds a transaction, goes back to the main menu, and quits the app.



## Other Commands

```
sbt scalafmtCheck
sbt scalafmtAll

sbt clean run
sbt clean coverage test
sbt coverageReport
```
