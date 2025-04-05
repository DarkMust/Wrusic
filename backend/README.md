# Wrusic Backend

This is the backend service for Wrusic, a web application that helps users discover the latest music releases and charts globally and by country.

## Technologies Used

- Java 24
- Spring Boot 3.4.4
- Spring Data JPA
- Spring Security
- PostgreSQL
- Docker
- Maven

## Features

- Latest global music releases
- Real-time music charts
- Country-specific charts and new music
- RESTful API endpoints
- Docker integration

## Prerequisites

- Java 24 or higher
- Maven 3.9+
- Docker and Docker Compose
- PostgreSQL (if running locally)

## Getting Started

### Running with Docker

1. Clone the repository
2. Navigate to the backend directory
3. Build and run the application using Docker Compose:

```bash
docker-compose up --build
```

The application will be available at `http://localhost:8080/api`

### Running Locally

1. Clone the repository
2. Navigate to the backend directory
3. Install dependencies:

```bash
mvn clean install
```

4. Run the application:

```bash
mvn spring-boot:run
```

## API Documentation

### Artists

- `GET /api/artists` - Get all artists
- `GET /api/artists/{id}` - Get artist by ID
- `POST /api/artists` - Create a new artist
- `PUT /api/artists/{id}` - Update an artist
- `DELETE /api/artists/{id}` - Delete an artist
- `GET /api/artists/search?name={name}` - Search artists by name

### Tracks

- `GET /api/tracks` - Get all tracks
- `GET /api/tracks/{id}` - Get track by ID
- `POST /api/tracks` - Create a new track
- `PUT /api/tracks/{id}` - Update a track
- `DELETE /api/tracks/{id}` - Delete a track
- `GET /api/tracks/artist/{artistId}` - Get tracks by artist
- `GET /api/tracks/genre/{genre}` - Get tracks by genre
- `GET /api/tracks/country/{country}` - Get tracks by country
- `GET /api/tracks/latest` - Get latest releases
- `GET /api/tracks/charts/global` - Get global charts
- `GET /api/tracks/charts/country/{country}` - Get country charts

### Albums

- `GET /api/albums` - Get all albums
- `GET /api/albums/{id}` - Get album by ID
- `POST /api/albums` - Create a new album
- `PUT /api/albums/{id}` - Update an album
- `DELETE /api/albums/{id}` - Delete an album
- `GET /api/albums/artist/{artistId}` - Get albums by artist
- `GET /api/albums/genre/{genre}` - Get albums by genre
- `GET /api/albums/search?title={title}` - Search albums by title
- `GET /api/albums/latest` - Get latest releases

### Charts

- `GET /api/charts/global` - Get global chart
- `GET /api/charts/country/{country}` - Get country chart
- `GET /api/charts/dates` - Get available chart dates
- `GET /api/charts/dates/country/{country}` - Get available chart dates by country
- `POST /api/charts` - Create a new chart entry
- `DELETE /api/charts/{id}` - Delete a chart entry

## Contributing

1. Fork the repository
2. Create your feature branch (`git checkout -b feature/amazing-feature`)
3. Commit your changes (`git commit -m 'Add some amazing feature'`)
4. Push to the branch (`git push origin feature/amazing-feature`)
5. Open a Pull Request

## License

This project is licensed under the MIT License - see the LICENSE file for details. 