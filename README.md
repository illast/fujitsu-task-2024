# fujitsu-task-2024

## Technologies
- Java 21
- Spring Boot 3.2.4
- H2 Database 2.2.224
- Measured weather data from [Estonian weather stations](https://www.ilmateenistus.ee/teenused/ilmainfo/eesti-vaatlusandmed-xml/)

## Installation
- Clone the repository
- Wait for Maven to install dependencies
- Run src/main/java/com/example/fujitsu/**FujitsuTaskApplication.java**

## Endpoints
- **GET /api/stations**: Retrieves a list of all stations
- **GET /api/fee**: Calculates the fee for using a specific vehicle in a specific city
