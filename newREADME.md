<!-- Improved compatibility of back to top link: See: https://github.com/othneildrew/Best-README-Template/pull/73 -->
<a id="readme-top"></a>
<!--
*** Thanks for checking out the Best-README-Template. If you have a suggestion
*** that would make this better, please fork the repo and create a pull request
*** or simply open an issue with the tag "enhancement".
*** Don't forget to give the project a star!
*** Thanks again! Now go create something AMAZING! :D
-->



<!-- PROJECT SHIELDS -->
<!--
*** I'm using markdown "reference style" links for readability.
*** Reference links are enclosed in brackets [ ] instead of parentheses ( ).
*** See the bottom of this document for the declaration of the reference variables
*** for contributors-url, forks-url, etc. This is an optional, concise syntax you may use.
*** https://www.markdownguide.org/basic-syntax/#reference-style-links
-->

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
    <a href="https://github.com/jonaskromer/EvenUp"><strong>Explore the docs »</strong></a>
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
    <li><a href="#contact">Contact</a></li>
    <li><a href="#acknowledgments">Acknowledgments</a></li>
  </ol>
</details>



<!-- ABOUT THE PROJECT -->
## About The Project

EvenUp is a simple, console-based (TUI) application designed to help you manage shared expenses and group finances.
With EvenUp, you can:

* Create and manage groups of people.
* Track expenses within groups, including shared expenses.
* Manage transactions between group members.
* Navigate through groups and menus easily using simple text commands.

EvenUp follows an MVC architecture, keeping the domain model (`App`, `Group`, `Person`) immutable, while the TUI provides an interactive interface.

<p align="right">(<a href="#readme-top">back to top</a>)</p>



### Built With

* [![Scala][Scala]][scala-url]
* [![Docker][Docker]][docker-url]
* [![Coveralls][Coveralls]][coveralls-url]
* [![Sonar-Cloud][Sonar-Cloud]][sonar-cloud-url]

<p align="right">(<a href="#readme-top">back to top</a>)</p>



<!-- GETTING STARTED -->
## Getting Started

This is an example of how you may give instructions on setting up your project locally.
To get a local copy up and running follow these simple example steps.

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
4. For the TUI to work, run this command in any terminal
   ```sh
   docker attach evenup-evenup-1
   ```

<p align="right">(<a href="#readme-top">back to top</a>)</p>



<!-- USAGE EXAMPLES -->
## Usage

Use this space to show useful examples of how a project can be used. Additional screenshots, code examples and demos work well in this space. You may also link to more resources.

_For more examples, please refer to the [Documentation](https://example.com)_

<p align="right">(<a href="#readme-top">back to top</a>)</p>



<!-- ROADMAP -->
## Roadmap

- [ ] Feature 1
- [ ] Feature 2
- [ ] Feature 3
    - [ ] Nested Feature

See the [open issues](https://github.com/jonaskromer/EvenUp/issues) for a full list of proposed features (and known issues).

<p align="right">(<a href="#readme-top">back to top</a>)</p>



<!-- CONTRIBUTING -->
## Contributing

Contributions are what make the open source community such an amazing place to learn, inspire, and create. Any contributions you make are **greatly appreciated**.

If you have a suggestion that would make this better, please fork the repo and create a pull request. You can also simply open an issue with the tag "enhancement".
Don't forget to give the project a star! Thanks again!

1. Fork the Project
2. Create your Feature Branch (`git checkout -b feature/AmazingFeature`)
3. Commit your Changes (`git commit -m 'Add some AmazingFeature'`)
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



<!-- CONTACT -->
## Contact

Your Name - [@twitter_handle](https://twitter.com/twitter_handle) - email@email_client.com

Project Link: [https://github.com/jonaskromer/EvenUp](https://github.com/jonaskromer/EvenUp)

<p align="right">(<a href="#readme-top">back to top</a>)</p>



<!-- ACKNOWLEDGMENTS -->
## Acknowledgments

* []()
* []()
* []()

<p align="right">(<a href="#readme-top">back to top</a>)</p>


<!-- MARKDOWN LINKS & IMAGES -->
<!-- https://www.markdownguide.org/basic-syntax/#reference-style-links -->
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
[product-screenshot]: images/screenshot.png
<!-- Shields.io badges. You can a comprehensive list with many more badges at: https://github.com/inttter/md-badges -->
[Scala]: https://img.shields.io/badge/scala-000000?style=for-the-badge&logo=scala&logoColor=DC322F
[scala-url]: https://scala-lang.org
[Docker]: https://img.shields.io/badge/docker-000000?style=for-the-badge&logo=docker&logoColor=2496ED
[docker-url]: https://scala-lang.org
[Coveralls]: https://img.shields.io/badge/Coveralls-3F5767?style=for-the-badge&logo=coveralls&logoColor=fff
[Sonar-Cloud]: https://img.shields.io/badge/SonarQube%20Cloud-126ED3?style=for-the-badge&logo=sonarqubecloud&logoColor=fff
[sonar-cloud-url]: https://sonarcloud.io/project/overview?id=jonaskromer_EvenUp

