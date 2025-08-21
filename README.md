# Weather Service

A Spring Boot backend service to fetch weather information by **pincode** and **date**.  
Optimized with caching in a PostgreSQL database to reduce external API calls.  

---

## 🚀 Features
- Fetch weather data using **OpenWeather APIs**  
- Store **pincode (lat/long)** and weather info in **PostgreSQL**  
- Cache results to reduce API calls  
- RESTful API (`/api/weather`)  
- **Exception handling** with meaningful responses  
- **Swagger UI** for easy testing  
- **JUnit & Mockito tests** for service and controller layers  

---

## 🛠️ Tech Stack
- Java 17  
- Spring Boot  
- Spring Data JPA (Hibernate)  
- PostgreSQL  
- WebClient (for API calls)  
- Swagger (springdoc-openapi)  
- JUnit, Mockito, MockMvc (testing)  

---

## ⚙️ Setup

### 1. Clone repo
```bash
git clone https://github.com/annubelgaonkar/Weather_service.git
cd Weather_service
```

### 2. Configure Database
Update `src/main/resources/application.properties`:
```properties
spring.datasource.url=jdbc:postgresql://<host>:<port>/<database_name>
spring.datasource.username=your_username
spring.datasource.password=your_password

spring.jpa.hibernate.ddl-auto=update

openweather.api.key=YOUR_API_KEY
openweather.geo.url=/geo/1.0/zip
openweather.api.url=/data/2.5/weather
```

> You’ll need a free API key from [OpenWeather](https://openweathermap.org/api).  

### 3. Run App
```bash
mvn spring-boot:run
```

### 4. Swagger UI
Visit 👉 [http://localhost:8085/swagger-ui.html](http://localhost:8080/swagger-ui.html)

---

## 📡 API Usage

### POST `/api/weather`

**Request**
```json
{
  "pincode": "560012",
  "forDate": "2025-08-20"
}
```

**Response**
```json
{
  "success": true,
  "message": "Weather fetched successfully",
  "data": {
    "pincode": "560012",
    "forDate": "2025-08-20",
    "temperature": 28.5,
    "humidity": 70,
    "description": "clear sky",
    "source": "API"
  }
}
```

---

## 🧪 Testing
Run all tests:
```bash
mvn test
```

- Unit tests with **JUnit & Mockito**  
- Controller tests with **MockMvc**  

---

## 📦 Future Improvements
- Add Docker support for one-command setup  
- Add more endpoints (e.g., 5-day forecast)  
- More comprehensive repository tests  

👤 Author

Anuradha Belgaonkar — Backend Developer (Java, Spring Boot, Microservices)