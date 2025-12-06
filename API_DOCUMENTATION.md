# Furniture Backend API Documentation

Complete API reference for the Furniture Backend REST API.

## Table of Contents

- [Base URL](#base-url)
- [Authentication](#authentication)
- [Response Format](#response-format)
- [Error Handling](#error-handling)
- [Endpoints](#endpoints)
  - [Furniture Body Management](#furniture-body-management)
  - [Cut Optimization](#cut-optimization)
- [Data Models](#data-models)
- [Examples](#examples)

## Base URL

```
http://localhost:8081
```

In Docker/Production environments:
```
http://furniture_backend:8080
```

## Authentication

Currently, the API uses Spring Security with basic configuration. Authentication requirements may vary based on deployment configuration.

## Response Format

All API responses follow standard HTTP conventions:

- **Success**: HTTP 2xx status codes with JSON response body
- **Client Error**: HTTP 4xx status codes with error details
- **Server Error**: HTTP 5xx status codes with error details

### Standard Response Structure

#### Success Response
```json
{
  "id": 1,
  "width": 500,
  "height": 300,
  "depth": 50
}
```

#### Error Response
```json
{
  "status": 400,
  "message": "Detailed error message"
}
```

## Error Handling

### HTTP Status Codes

| Status Code | Description | Use Case |
|-------------|-------------|----------|
| 200 OK | Success | Successful GET, POST, UPDATE requests |
| 201 Created | Resource created | Successful POST /add |
| 400 Bad Request | Invalid request | Validation errors, missing parameters |
| 404 Not Found | Resource not found | Entity with given ID doesn't exist |
| 422 Unprocessable Entity | Business logic error | Cutting optimization failed |
| 500 Internal Server Error | Server error | Unexpected server errors |

### Error Response Format

```json
{
  "status": 400,
  "message": "Width must be positive"
}
```

### Common Error Messages

| Error | Status | Message |
|-------|--------|---------|
| Validation Error | 400 | "Width is required", "Height must be positive" |
| Not Found | 404 | "Furniture body not found with id: {id}" |
| Optimization Failed | 422 | "Failed to place all elements. Elements do not fit on sheet" |
| Too Large Element | 422 | "Element {id} ({w}x{h}) is too large to fit on sheet ({W}x{H})" |

## Endpoints

### Furniture Body Management

#### 1. Get All Furniture Bodies

Retrieve all furniture body records from the database.

**Endpoint:** `GET /furniture/all`

**Request:**
```http
GET /furniture/all HTTP/1.1
Host: localhost:8081
```

**Response:** `200 OK`
```json
[
  {
    "id": 1,
    "width": 500,
    "height": 300,
    "depth": 50
  },
  {
    "id": 2,
    "width": 800,
    "height": 400,
    "depth": 60
  }
]
```

**Error Responses:**
- `500 Internal Server Error` - Database connection issues

---

#### 2. Get Furniture Body by ID

Retrieve a specific furniture body by its ID.

**Endpoint:** `GET /furniture/find/{id}`

**Parameters:**
| Name | Type | Location | Required | Description |
|------|------|----------|----------|-------------|
| id | Long | Path | Yes | Furniture body ID |

**Request:**
```http
GET /furniture/find/1 HTTP/1.1
Host: localhost:8081
```

**Response:** `200 OK`
```json
{
  "id": 1,
  "width": 500,
  "height": 300,
  "depth": 50
}
```

**Error Responses:**
- `404 Not Found` - Furniture body with given ID doesn't exist
```json
{
  "status": 404,
  "message": "Furniture body not found with id: 1"
}
```

---

#### 3. Create Furniture Body

Create a new furniture body record.

**Endpoint:** `POST /furniture/add`

**Request Body:**
```json
{
  "width": 500,
  "height": 300,
  "depth": 50
}
```

**Request:**
```http
POST /furniture/add HTTP/1.1
Host: localhost:8081
Content-Type: application/json

{
  "width": 500,
  "height": 300,
  "depth": 50
}
```

**Response:** `201 Created`
```json
{
  "id": 3,
  "width": 500,
  "height": 300,
  "depth": 50
}
```

**Error Responses:**
- `400 Bad Request` - Invalid input data
```json
{
  "status": 400,
  "message": "Width must be positive"
}
```

**Validation Rules:**
- `width`: Required, must be positive integer
- `height`: Required, must be positive integer
- `depth`: Required, must be positive integer

---

#### 4. Update Furniture Body

Update an existing furniture body record.

**Endpoint:** `POST /furniture/update`

**Request Body:**
```json
{
  "id": 1,
  "width": 600,
  "height": 350,
  "depth": 55
}
```

**Request:**
```http
POST /furniture/update HTTP/1.1
Host: localhost:8081
Content-Type: application/json

{
  "id": 1,
  "width": 600,
  "height": 350,
  "depth": 55
}
```

**Response:** `200 OK`
```json
{
  "id": 1,
  "width": 600,
  "height": 350,
  "depth": 55
}
```

**Error Responses:**
- `400 Bad Request` - Invalid input data
- `404 Not Found` - Furniture body with given ID doesn't exist

---

#### 5. Delete Furniture Body

Delete a furniture body by ID.

**Endpoint:** `GET /furniture/delete/{id}`

**Parameters:**
| Name | Type | Location | Required | Description |
|------|------|----------|----------|-------------|
| id | Long | Path | Yes | Furniture body ID to delete |

**Request:**
```http
GET /furniture/delete/1 HTTP/1.1
Host: localhost:8081
```

**Response:** `200 OK`
```
(Empty response body)
```

**Error Responses:**
- `404 Not Found` - Furniture body with given ID doesn't exist

---

### Cut Optimization

#### 6. Optimize Cutting Plan

Calculate optimal placement of furniture elements on a cutting sheet to minimize waste.

**Algorithm:** First Fit Decreasing Height (FFDH) bin packing algorithm
- Sorts elements by height (descending)
- Creates horizontal levels on the sheet
- Attempts both normal and rotated orientations for best fit
- Minimizes material waste

**Endpoint:** `POST /furniture/cut`

**Request Body:**
```json
{
  "sheetWidth": 2000,
  "sheetHeight": 1000,
  "elements": [
    {
      "id": 1,
      "width": 500,
      "height": 300,
      "depth": 50
    },
    {
      "id": 2,
      "width": 400,
      "height": 250,
      "depth": 40
    }
  ]
}
```

**Request:**
```http
POST /furniture/cut HTTP/1.1
Host: localhost:8081
Content-Type: application/json

{
  "sheetWidth": 2000,
  "sheetHeight": 1000,
  "elements": [
    {
      "id": 1,
      "width": 500,
      "height": 300,
      "depth": 50
    },
    {
      "id": 2,
      "width": 400,
      "height": 250,
      "depth": 40
    }
  ]
}
```

**Response:** `200 OK`
```json
{
  "placements": [
    {
      "id": 1,
      "x": 0,
      "y": 0,
      "width": 500,
      "height": 300
    },
    {
      "id": 2,
      "x": 500,
      "y": 0,
      "width": 400,
      "height": 250
    }
  ]
}
```

**Response Fields:**
| Field | Type | Description |
|-------|------|-------------|
| placements | Array | List of placed elements with their positions |
| placements[].id | Long | Original element ID |
| placements[].x | Integer | X-coordinate on sheet (top-left corner) |
| placements[].y | Integer | Y-coordinate on sheet (top-left corner) |
| placements[].width | Integer | Width of placed element (may be rotated) |
| placements[].height | Integer | Height of placed element (may be rotated) |

**Error Responses:**

- `400 Bad Request` - Invalid input data
```json
{
  "status": 400,
  "message": "Sheet width is required"
}
```

- `400 Bad Request` - Missing elements
```json
{
  "status": 400,
  "message": "Elements list cannot be empty"
}
```

- `422 Unprocessable Entity` - Elements don't fit
```json
{
  "status": 422,
  "message": "Failed to place all elements. 1 of 2 elements were placed."
}
```

- `422 Unprocessable Entity` - Element too large
```json
{
  "status": 422,
  "message": "Element 1 (2500x1500) is too large to fit on the sheet (2000x1000)"
}
```

**Validation Rules:**
- `sheetWidth`: Required, must be positive integer (≥1)
- `sheetHeight`: Required, must be positive integer (≥1)
- `elements`: Required, cannot be empty array
- `elements[].width`: Required, must be positive integer
- `elements[].height`: Required, must be positive integer

**Business Rules:**
1. All elements must fit on the sheet (either normal or rotated orientation)
2. Elements cannot overlap
3. All elements must be within sheet boundaries
4. Algorithm attempts to minimize wasted material
5. Elements may be rotated 90° if it improves placement

---

## Data Models

### FurnitureBodyDTO

Represents a furniture element/body.

```json
{
  "id": 1,
  "width": 500,
  "height": 300,
  "depth": 50
}
```

| Field | Type | Required | Constraints | Description |
|-------|------|----------|-------------|-------------|
| id | Long | No (auto-generated) | - | Unique identifier |
| width | Integer | Yes | > 0 | Width in millimeters |
| height | Integer | Yes | > 0 | Height in millimeters |
| depth | Integer | Yes | > 0 | Depth in millimeters |

### CutRequestDTO

Request for cutting optimization.

```json
{
  "sheetWidth": 2000,
  "sheetHeight": 1000,
  "elements": [...]
}
```

| Field | Type | Required | Constraints | Description |
|-------|------|----------|-------------|-------------|
| sheetWidth | Integer | Yes | ≥ 1 | Sheet width in millimeters |
| sheetHeight | Integer | Yes | ≥ 1 | Sheet height in millimeters |
| elements | Array<FurnitureBodyDTO> | Yes | Not empty | Elements to place |

### CutResponseDTO

Response from cutting optimization.

```json
{
  "placements": [...]
}
```

| Field | Type | Description |
|-------|------|-------------|
| placements | Array<PlacedElementDTO> | Optimized element placements |

### PlacedElementDTO

Placed element with coordinates.

```json
{
  "id": 1,
  "x": 0,
  "y": 0,
  "width": 500,
  "height": 300
}
```

| Field | Type | Description |
|-------|------|-------------|
| id | Long | Original element ID |
| x | Integer | X-coordinate (left edge) in mm |
| y | Integer | Y-coordinate (top edge) in mm |
| width | Integer | Element width in mm (possibly rotated) |
| height | Integer | Element height in mm (possibly rotated) |

### ErrorResponse

Standard error response format.

```json
{
  "status": 400,
  "message": "Error description"
}
```

| Field | Type | Description |
|-------|------|-------------|
| status | Integer | HTTP status code |
| message | String | Human-readable error message |

---

## Examples

### Example 1: Complete CRUD Flow

#### 1. Create a furniture body
```bash
curl -X POST http://localhost:8081/furniture/add \
  -H "Content-Type: application/json" \
  -d '{
    "width": 500,
    "height": 300,
    "depth": 50
  }'
```

Response:
```json
{
  "id": 1,
  "width": 500,
  "height": 300,
  "depth": 50
}
```

#### 2. Get the furniture body
```bash
curl http://localhost:8081/furniture/find/1
```

Response:
```json
{
  "id": 1,
  "width": 500,
  "height": 300,
  "depth": 50
}
```

#### 3. Update the furniture body
```bash
curl -X POST http://localhost:8081/furniture/update \
  -H "Content-Type: application/json" \
  -d '{
    "id": 1,
    "width": 600,
    "height": 350,
    "depth": 55
  }'
```

Response:
```json
{
  "id": 1,
  "width": 600,
  "height": 350,
  "depth": 55
}
```

#### 4. Get all furniture bodies
```bash
curl http://localhost:8081/furniture/all
```

Response:
```json
[
  {
    "id": 1,
    "width": 600,
    "height": 350,
    "depth": 55
  }
]
```

#### 5. Delete the furniture body
```bash
curl http://localhost:8081/furniture/delete/1
```

Response: `200 OK`

---

### Example 2: Cutting Optimization - Simple Case

**Scenario:** Place 2 small elements on a 2000x1000mm sheet

```bash
curl -X POST http://localhost:8081/furniture/cut \
  -H "Content-Type: application/json" \
  -d '{
    "sheetWidth": 2000,
    "sheetHeight": 1000,
    "elements": [
      {
        "id": 1,
        "width": 500,
        "height": 300,
        "depth": 50
      },
      {
        "id": 2,
        "width": 400,
        "height": 250,
        "depth": 40
      }
    ]
  }'
```

Response:
```json
{
  "placements": [
    {
      "id": 1,
      "x": 0,
      "y": 0,
      "width": 500,
      "height": 300
    },
    {
      "id": 2,
      "x": 500,
      "y": 0,
      "width": 400,
      "height": 250
    }
  ]
}
```

**Visualization:**
```
Sheet: 2000x1000mm
┌────────────────────────────────────────┐
│ [1: 500x300]  [2: 400x250]             │
│                                        │
│                                        │
│              (waste area)              │
└────────────────────────────────────────┘
```

---

### Example 3: Cutting Optimization - With Rotation

**Scenario:** Elements may be rotated for better fit

```bash
curl -X POST http://localhost:8081/furniture/cut \
  -H "Content-Type: application/json" \
  -d '{
    "sheetWidth": 2000,
    "sheetHeight": 600,
    "elements": [
      {
        "id": 1,
        "width": 800,
        "height": 400,
        "depth": 50
      },
      {
        "id": 2,
        "width": 600,
        "height": 500,
        "depth": 40
      }
    ]
  }'
```

Response (element 2 might be rotated):
```json
{
  "placements": [
    {
      "id": 1,
      "x": 0,
      "y": 0,
      "width": 800,
      "height": 400
    },
    {
      "id": 2,
      "x": 800,
      "y": 0,
      "width": 500,
      "height": 600
    }
  ]
}
```

---

### Example 4: Error - Element Too Large

**Scenario:** Element larger than sheet in both dimensions

```bash
curl -X POST http://localhost:8081/furniture/cut \
  -H "Content-Type: application/json" \
  -d '{
    "sheetWidth": 2000,
    "sheetHeight": 1000,
    "elements": [
      {
        "id": 1,
        "width": 2500,
        "height": 1500,
        "depth": 50
      }
    ]
  }'
```

Response: `422 Unprocessable Entity`
```json
{
  "status": 422,
  "message": "Element 1 (2500x1500) is too large to fit on the sheet (2000x1000)"
}
```

---

### Example 5: Error - Validation Failure

**Scenario:** Missing required fields

```bash
curl -X POST http://localhost:8081/furniture/cut \
  -H "Content-Type: application/json" \
  -d '{
    "sheetWidth": 2000,
    "elements": []
  }'
```

Response: `400 Bad Request`
```json
{
  "status": 400,
  "message": "Sheet height is required; Elements list cannot be empty"
}
```

---

### Example 6: Error - Elements Don't Fit

**Scenario:** Too many elements for the sheet

```bash
curl -X POST http://localhost:8081/furniture/cut \
  -H "Content-Type: application/json" \
  -d '{
    "sheetWidth": 1000,
    "sheetHeight": 500,
    "elements": [
      {"id": 1, "width": 500, "height": 400, "depth": 50},
      {"id": 2, "width": 500, "height": 400, "depth": 50},
      {"id": 3, "width": 500, "height": 400, "depth": 50},
      {"id": 4, "width": 500, "height": 400, "depth": 50}
    ]
  }'
```

Response: `422 Unprocessable Entity`
```json
{
  "status": 422,
  "message": "Failed to place all elements. 2 of 4 elements were placed."
}
```

---

## Testing the API

### Using cURL

All examples above use cURL. Install cURL:
- Windows: Download from https://curl.se/
- Linux/Mac: Usually pre-installed

### Using Postman

1. Import the API collection
2. Set base URL: `http://localhost:8081`
3. Test each endpoint with provided examples

### Using Browser (GET requests only)

```
http://localhost:8081/furniture/all
http://localhost:8081/furniture/find/1
```

### Using HTTPie

```bash
# Install: pip install httpie

# GET request
http GET localhost:8081/furniture/all

# POST request
http POST localhost:8081/furniture/add width:=500 height:=300 depth:=50
```

---

## Performance Considerations

### Cutting Optimization

- **Time Complexity**: O(n² × m) where n = number of elements, m = number of levels
- **Space Complexity**: O(n + m)
- **Recommended Limits**: 
  - Max elements per request: 100
  - Max sheet size: 10000x10000mm
  - Max element size: Must fit within sheet

### Database Queries

- All furniture body queries use indexed ID lookups (O(log n))
- Batch operations should be limited to 1000 records

---

## Rate Limiting

Currently, no rate limiting is implemented. For production deployments, consider:
- Adding Spring rate limiting
- Using API Gateway with rate limits
- Implementing request throttling

---

## Versioning

Current API Version: **v1** (implicit)

Future versions may be accessed via:
- URL path: `/api/v2/furniture/...`
- Header: `Accept: application/vnd.furniture.v2+json`

---

## Support

For API questions or issues:
1. Check this documentation
2. Review error messages carefully
3. Check application logs
4. Contact the development team

---

## Changelog

### Version 0.0.1-SNAPSHOT (Current)
- Initial API implementation
- Furniture body CRUD operations
- Cut optimization with FFDH algorithm
- Comprehensive error handling
- Input validation

