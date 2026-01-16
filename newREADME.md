<a id="readme-top"></a>

<br />
<div align="center">
  <p align="center">
  
[![Coveralls][coveralls-shield]][coveralls-url]
[![Build][actions-shield]][actions-url]
[![Commits][commits-shield]][commits-url]
[![Forks][forks-shield]][forks-url]
[![Stargazers][stars-shield]][stars-url]
[![Issues][issues-shield]][issues-url]
[![MIT][license-shield]][license-url]

  </p>
</div>

<!-- PROJECT LOGO -->
<br />
<div align="center">
  <a href="https://github.com/jonaskromer/EvenUp">
    <img src="src/main/resources/images/title_image.png" alt="Logo" width="800">
  </a>

<h3 align="center">EvenUp</h3>

  <p align="center">
    An Expense Management App
    <br />
    <br />
    <a href="https://github.com/jonaskromer/EvenUp">View Demo</a>
    &middot;
    <a href="https://github.com/jonaskromer/EvenUp/issues/new?labels=bug&template=bug-report---.md">Report Bug</a>
    &middot;
    <a href="https://github.com/jonaskromer/EvenUp/issues/new?labels=enhancement&template=feature-request---.md">Request Feature</a>
  </p>
</div>



<!-- TABLE OF CONTENTS -->
<details>
  <summary>Table of Contents</summary>
  <ol>
    <li>
      <a href="#about-the-project">About The Project</a>
      <ul>
        <li><a href="#built-with">Built With</a></li>
      </ul>
    </li>
    <li>
      <a href="#getting-started">Getting Started</a>
      <ul>
        <li><a href="#prerequisites">Prerequisites</a></li>
        <li><a href="#installation">Installation</a></li>
      </ul>
    </li>
    <li><a href="#usage">Usage</a></li>
    <li><a href="#roadmap">Roadmap</a></li>
    <li><a href="#contributing">Contributing</a></li>
    <li><a href="#license">License</a></li>
  </ol>
</details>



<!-- ABOUT THE PROJECT -->

## About The Project

EvenUp is a console and gui based application for managing shared expenses and group finances. Create groups, track shared expenses, and manage transactions between members with an intuitive text-based interface. The immutable domain model ensures data integrity while the TUI provides interactive navigation through simple commands.

### Key Features
- Group Management: Create and organize groups of people
- Expense Tracking: Monitor shared expenses within groups
- Transaction Management: Handle financial transactions between group members
- User Interface: Navigate through intuitive text-based commands

### Architecture
EvenUp implements the Model-View-Controller (MVC) pattern with:
- **Model Layer**: Immutable domain objects (`App`, `Group`, `Person`)
- **View Layer**: TUI (Terminal User Interface) and GUI for interactive user engagement
- **Controller Layer**: Command-based architecture with state management
- **Utilities**: Memento pattern for undo/redo, observable pattern for state synchronization


### Built With

* [![Scala][Scala]][scala-url]
* [![Docker][Docker]][docker-url]
* [![Coveralls][Coveralls]][coveralls-url]
* [![Sonar-Cloud][Sonar-Cloud]][sonar-cloud-url]

<p align="right">(<a href="#readme-top">back to top</a>)</p>



<!-- GETTING STARTED -->
## Getting Started

This guide will help you set up EvenUp locally and get it running. Follow the prerequisites for your system and then proceed with the installation steps.

### Prerequisites

Setup on MacOS
* XQuartz Setup
  1. Install XQuartz via Homebrew
      ```sh
      brew install --cask xquartz
      open -a XQuartz
      ```
  2. Enable network client connections
   
      In XQuartz:
      * Open **Settings**
      * Go to **Security**
      * Enable **“Allow connections from network clients”**
  3. Allow local connections
  
      ```sh
      xhost + 127.0.0.1
      ```

Setup on Windows


### Installation

1. Clone the repo
   ```sh
   git clone https://github.com/jonaskromer/EvenUp.git
   ```
2. Build Docker Image
   ```sh
   docker compose -f 'docker-compose.yaml' up -d --build 'evenup'
   ```
3. Rerun the App
   ```sh
   docker start evenup-evenup-1
   ```
4. For the TUI to work, attach any terminal to the container
   ```sh
   docker attach evenup-evenup-1
   ```

<p align="right">(<a href="#readme-top">back to top</a>)</p>



<!-- USAGE EXAMPLES -->
## Usage

Use this space to show useful examples of how a project can be used. Additional screenshots, code examples and demos work well in this space. You may also link to more resources.

### TUI Usage

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
| `:debts`    |                                                                            | Calculate debts for group                  |
| `:strategy` | `<simplified\|normal>`                                                     | Set debt calculation strategy              |
| `:undo`     |                                                                            | Undo the latest action                     |
| `:redo`     |                                                                            | Redo the latest undo action                |
| `:h`        |                                                                            | Show help                                  |
| `:q`        |                                                                            | Quit the app                               |
| `:m`        |                                                                            | Go back to the main menu                   |
| `:l`        | `<user name>`                                                              | Login as a specific user                   |

### GUI Usage


<p align="right">(<a href="#readme-top">back to top</a>)</p>



<!-- ROADMAP -->
## Roadmap

- [x] Core expense tracking functionality
- [x] Group management system
- [x] Transaction tracking
- [x] TUI
- [x] Share-based expense splitting
- [x] Debt calculation strategies
    - [x] Normal debt calculation
    - [x] Simplified debt strategy (minimizes transactions)
- [x] Undo/Redo support
- [x] GUI implementation
- [x] Data persistence
- [ ] Expense categories and tags
- [ ] Export reports (PDF, CSV)
- [ ] Receipt attachments
- [ ] Cloud synchronization

See the [open issues](https://github.com/jonaskromer/EvenUp/issues) for a full list of proposed features (and known issues).

<p align="right">(<a href="#readme-top">back to top</a>)</p>



<!-- CONTRIBUTING -->
## Contributing

Contributions are what make the open source community such an amazing place to learn, inspire, and create. Any contributions you make are **greatly appreciated**.

If you have a suggestion that would make this better, please fork the repo and create a pull request. You can also simply open an issue with the tag "enhancement".
Don't forget to give the project a star! Thanks again!

1. Fork the Project
2. Create your Feature Branch (`git checkout -b feature/AmazingFeature`)
3. Commit your Changes (`git commit -m 'feat: Add some AmazingFeature'`)
4. Push to the Branch (`git push origin feature/AmazingFeature`)
5. Open a Pull Request

<p align="right">(<a href="#readme-top">back to top</a>)</p>

### Top contributors:

<a href="https://github.com/jonaskromer/EvenUp/graphs/contributors">
  <img src="https://contrib.rocks/image?repo=jonaskromer/EvenUp" alt="contrib.rocks image" />
</a>



<!-- LICENSE -->
## License

Distributed under the MIT. See `LICENSE` for more information.

<p align="right">(<a href="#readme-top">back to top</a>)</p>


<!-- MARKDOWN LINKS & IMAGES -->
[coveralls-shield]: https://img.shields.io/coverallsCoverage/github/jonaskromer/EvenUp?style=for-the-badge
[coveralls-url]: https://coveralls.io/github/jonaskromer/EvenUp?branch=main
[actions-shield]: https://img.shields.io/github/actions/workflow/status/jonaskromer/EvenUp/scala.yml?style=for-the-badge
[actions-url]: https://github.com/jonaskromer/EvenUp/actions/workflows/scala.yml
[commits-shield]: https://img.shields.io/github/last-commit/jonaskromer/EvenUp?style=for-the-badge
[commits-url]: https://github.com/jonaskromer/EvenUp/commits/main/
[forks-shield]: https://img.shields.io/github/forks/jonaskromer/EvenUp.svg?style=for-the-badge
[forks-url]: https://github.com/jonaskromer/EvenUp/network/members
[stars-shield]: https://img.shields.io/github/stars/jonaskromer/EvenUp.svg?style=for-the-badge
[stars-url]: https://github.com/jonaskromer/EvenUp/stargazers
[issues-shield]: https://img.shields.io/github/issues/jonaskromer/EvenUp.svg?style=for-the-badge
[issues-url]: https://github.com/jonaskromer/EvenUp/issues
[license-shield]: https://img.shields.io/github/license/jonaskromer/EvenUp.svg?style=for-the-badge
[license-url]: https://github.com/jonaskromer/EvenUp/blob/main/LICENSE

[Scala]: https://img.shields.io/badge/scala-000000?style=for-the-badge&logo=scala&logoColor=DC322F
[scala-url]: https://scala-lang.org
[Docker]: https://img.shields.io/badge/docker-000000?style=for-the-badge&logo=docker&logoColor=2496ED
[docker-url]: https://scala-lang.org
[Coveralls]: https://img.shields.io/badge/Coveralls-3F5767?style=for-the-badge&logo=coveralls&logoColor=fff
[Sonar-Cloud]: https://img.shields.io/badge/SonarQube%20Cloud-126ED3?style=for-the-badge&logo=sonarqubecloud&logoColor=fff
[sonar-cloud-url]: https://sonarcloud.io/project/overview?id=jonaskromer_EvenUp

[product-screenshot]: images/screenshot.png
