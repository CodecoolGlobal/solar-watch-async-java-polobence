<!-- PROJECT LOGO -->
<br />
<div align="center">
<h3 align="center">SolarWatch Application</h3>

  <p align="center">
    A full-stack application that provides sunrise and sunset times for cities worldwide. Built with Java Spring Boot backend and React frontend, SolarWatch helps you plan your day around natural light conditions in any location.
    <br />
    <a href="https://github.com/CodecoolGlobal/solar-watch-async-java-polobence.git"><strong>Explore the docs »</strong></a>
    <br />
    <br />
    <a href="https://github.com/CodecoolGlobal/solar-watch-async-java-polobence.git">View Demo</a>
    &middot;
    <a href="https://github.com/CodecoolGlobal/solar-watch-async-java-polobence.git/issues/new?labels=bug&template=bug-report---.md">Report Bug</a>
    &middot;
    <a href="https://github.com/CodecoolGlobal/solar-watch-async-java-polobence.git/issues/new?labels=enhancement&template=feature-request---.md">Request Feature</a>
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
  </ol>
</details>



<!-- ABOUT THE PROJECT -->
## About The Project

![Product Name Screen Shot][product-screenshot]

SolarWatch is a comprehensive application that provides accurate sunrise and sunset times for any location worldwide. Whether you're a photographer planning the perfect golden hour shot, a traveler organizing your day, or simply someone who wants to make the most of natural daylight, SolarWatch offers precise solar event information at your fingertips.

This project was an excellent opportunity to work with geolocation services, integrate with external APIs for astronomical data, and implement robust error handling for various edge cases. The application features a secure user authentication system, efficient data caching, and a responsive user interface built with modern web technologies.

<p align="right">(<a href="#readme-top">back to top</a>)</p>



### Built With

#### Frontend
* [![React][React.js]][React-url]
* [![Vite][Vite.js]][Vite-url]
* [![React Router][React-Router.js]][React-Router-url]

#### Backend
* [![Spring Boot][Spring-Boot.js]][Spring-Boot-url]
* [![Spring Data JPA][Spring-Data-JPA.js]][Spring-Data-JPA-url]
* [![Java][Java.js]][Java-url]
* [![PostgreSQL][PostgreSQL.js]][PostgreSQL-url]

#### DevOps
* [![Docker][Docker.js]][Docker-url]
* [![Docker Compose][Docker-Compose.js]][Docker-Compose-url]

<p align="right">(<a href="#readme-top">back to top</a>)</p>



<!-- GETTING STARTED -->
## Getting Started

To get a local copy of SolarWatch up and running, follow these simple steps.

### Prerequisites

Before you begin, ensure you have the following installed on your system:

- [Docker](https://docs.docker.com/get-docker/) (v20.10.0 or higher)
- [Docker Compose](https://docs.docker.com/compose/install/) (v2.0.0 or higher)
- [Node.js](https://nodejs.org/) (v18.0.0 or higher)
- [Git](https://git-scm.com/downloads)

### Installation

1. **Clone the repository**
   ```bash
   git clone https://github.com/CodecoolGlobal/solar-watch-async-java-polobence.git
   cd solar-watch-async-java-polobence
   ```

2. **Set up environment variables**
   - Navigate to the backend directory:
     ```bash
     cd backend/src/main/resources
     ```
   - Create a new file called `application-secret.properties` and add your API keys:
     ```properties
     api.openweathermap.key=your_openweathermap_api_key
     api.timeapi.key=your_timeapi_key
     ```
   - For production, set up these environment variables in your deployment environment

3. **Start the application**
   - From the project root, run:
     ```bash
     docker-compose up --build
     ```
   - The application will be available at:
     - Frontend: http://localhost:5173
     - Backend API: http://localhost:8080
     - PostgreSQL: localhost:5432
     - PGAdmin: http://localhost:5050 (if enabled in docker-compose)

4. **Development Mode**
   - For frontend development:
     ```bash
     cd frontend
     npm install
     npm run dev
     ```
   - For backend development, import the project into your favorite IDE and run the `SolarWatchApplication` class

<p align="right">(<a href="#readme-top">back to top</a>)</p>





<!-- MARKDOWN LINKS & IMAGES -->
<!-- https://www.markdownguide.org/basic-syntax/#reference-style-links -->
[contributors-shield]: https://img.shields.io/github/contributors/github_username/repo_name.svg?style=for-the-badge
[contributors-url]: https://github.com/github_username/repo_name/graphs/contributors
[forks-shield]: https://img.shields.io/github/forks/github_username/repo_name.svg?style=for-the-badge
[forks-url]: https://github.com/github_username/repo_name/network/members
[stars-shield]: https://img.shields.io/github/stars/github_username/repo_name.svg?style=for-the-badge
[stars-url]: https://github.com/github_username/repo_name/stargazers
[issues-shield]: https://img.shields.io/github/issues/github_username/repo_name.svg?style=for-the-badge
[issues-url]: https://github.com/github_username/repo_name/issues
[license-shield]: https://img.shields.io/github/license/github_username/repo_name.svg?style=for-the-badge
[license-url]: https://github.com/github_username/repo_name/blob/master/LICENSE.txt
[linkedin-shield]: https://img.shields.io/badge/-LinkedIn-black.svg?style=for-the-badge&logo=linkedin&colorB=555
[linkedin-url]: https://linkedin.com/in/linkedin_username
[product-screenshot]: app-screenshot.png
[Next.js]: https://img.shields.io/badge/next.js-000000?style=for-the-badge&logo=nextdotjs&logoColor=white
[Next-url]: https://nextjs.org/
[React.js]: https://img.shields.io/badge/React-20232A?style=for-the-badge&logo=react&logoColor=61DAFB
[React-url]: https://reactjs.org/
[Vue.js]: https://img.shields.io/badge/Vue.js-35495E?style=for-the-badge&logo=vuedotjs&logoColor=4FC08D
[Vue-url]: https://vuejs.org/
[Angular.io]: https://img.shields.io/badge/Angular-DD0031?style=for-the-badge&logo=angular&logoColor=white
[Angular-url]: https://angular.io/
[Svelte.dev]: https://img.shields.io/badge/Svelte-4A4A55?style=for-the-badge&logo=svelte&logoColor=FF3E00
[Svelte-url]: https://svelte.dev/
[Laravel.com]: https://img.shields.io/badge/Laravel-FF2D20?style=for-the-badge&logo=laravel&logoColor=white
[Laravel-url]: https://laravel.com
[Bootstrap.com]: https://img.shields.io/badge/Bootstrap-563D7C?style=for-the-badge&logo=bootstrap&logoColor=white
[Bootstrap-url]: https://getbootstrap.com
[JQuery.com]: https://img.shields.io/badge/jQuery-0769AD?style=for-the-badge&logo=jquery&logoColor=white
[JQuery-url]: https://jquery.com
[React.js]: https://img.shields.io/badge/React-20232A?style=for-the-badge&logo=react&logoColor=61DAFB
[React-url]: https://reactjs.org/
[Vite.js]: https://img.shields.io/badge/Vite-646CFF?style=for-the-badge&logo=vite&logoColor=white
[Vite-url]: https://vitejs.dev/
[MUI.js]: https://img.shields.io/badge/MUI-007FFF?style=for-the-badge&logo=mui&logoColor=white
[MUI-url]: https://mui.com/
[React-Router.js]: https://img.shields.io/badge/React_Router-CA4245?style=for-the-badge&logo=react-router&logoColor=white
[React-Router-url]: https://reactrouter.com/
[Framer-Motion.js]: https://img.shields.io/badge/Framer_Motion-0055FF?style=for-the-badge&logo=framer&logoColor=white
[Framer-Motion-url]: https://www.framer.com/motion/
[Date-fns.js]: https://img.shields.io/badge/date--fns-007AFF?style=for-the-badge
[Date-fns-url]: https://date-fns.org/
[Spring-Boot.js]: https://img.shields.io/badge/Spring_Boot-6DB33F?style=for-the-badge&logo=spring&logoColor=white
[Spring-Boot-url]: https://spring.io/projects/spring-boot
[Spring-Security.js]: https://img.shields.io/badge/Spring_Security-6DB33F?style=for-the-badge&logo=spring-security&logoColor=white
[Spring-Security-url]: https://spring.io/projects/spring-security
[Spring-Data-JPA.js]: https://img.shields.io/badge/Spring_Data_JPA-6DB33F?style=for-the-badge&logo=spring&logoColor=white
[Spring-Data-JPA-url]: https://spring.io/projects/spring-data-jpa
[Java.js]: https://img.shields.io/badge/Java-ED8B00?style=for-the-badge&logo=openjdk&logoColor=white
[Java-url]: https://www.oracle.com/java/
[PostgreSQL.js]: https://img.shields.io/badge/PostgreSQL-316192?style=for-the-badge&logo=postgresql&logoColor=white
[PostgreSQL-url]: https://www.postgresql.org/
[Docker.js]: https://img.shields.io/badge/Docker-2496ED?style=for-the-badge&logo=docker&logoColor=white
[Docker-url]: https://www.docker.com/
[Docker-Compose.js]: https://img.shields.io/badge/Docker_Compose-2496ED?style=for-the-badge&logo=docker&logoColor=white
[Docker-Compose-url]: https://docs.docker.com/compose/
[Spring-WebFlux.js]: https://img.shields.io/badge/Spring_WebFlux-6DB33F?style=for-the-badge&logo=spring&logoColor=white
[Spring-WebFlux-url]: https://spring.io/projects/spring-webflux
