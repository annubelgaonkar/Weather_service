## API Documentation
Swagger UI available at:  
👉 http://localhost:8085/swagger-ui.html

### Example Request
POST /api/weather
```json
{
  "pincode": "560012",
  "forDate": "2025-08-20"
}
```
### Example Response
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
