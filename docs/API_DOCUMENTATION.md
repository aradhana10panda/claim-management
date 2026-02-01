# API Documentation - Claim Management Microservice

## 📋 Overview

The Claim Management API provides comprehensive functionality for managing insurance claims through RESTful endpoints. This API follows REST principles and uses JSON for data exchange.

## 🌐 Base Information

- **Base URL**: `http://localhost:8080/api/v1`
- **Content Type**: `application/json`
- **Authentication**: None (for development)
- **API Version**: v1

## 📚 Interactive Documentation

- **Swagger UI**: `http://localhost:8080/swagger-ui.html`
- **OpenAPI JSON**: `http://localhost:8080/api-docs`

## 🔗 Endpoints Overview

| Category | Endpoint | Method | Description |
|----------|----------|--------|-------------|
| **Health** | `/actuator/health` | GET | Application health check |
| **Claims** | `/claims` | POST | Create new claim |
| **Claims** | `/claims/{id}` | GET | Get claim by ID |
| **Claims** | `/claims/number/{claimNumber}` | GET | Get claim by number |
| **Claims** | `/claims` | GET | Get all claims (paginated) |
| **Claims** | `/claims/{id}` | PUT | Update claim |
| **Claims** | `/claims/{id}/status` | PATCH | Update claim status |
| **Claims** | `/claims/{id}` | DELETE | Delete claim |
| **Search** | `/claims/search` | GET | Search claims |
| **Search** | `/claims/policy/{policyNumber}` | GET | Get claims by policy |
| **Search** | `/claims/claimant/{email}` | GET | Get claims by claimant |
| **Analytics** | `/claims/high-value` | GET | Get high-value claims |
| **Analytics** | `/claims/count` | GET | Get claim count by status |

## 🏥 Health Check Endpoints

### Get Application Health

```http
GET /actuator/health
```

**Response (200 OK):**
```json
{
  "status": "UP",
  "components": {
    "db": {
      "status": "UP",
      "details": {
        "database": "H2",
        "validationQuery": "isValid()"
      }
    },
    "diskSpace": {
      "status": "UP",
      "details": {
        "total": 499963174912,
        "free": 91943821312,
        "threshold": 10485760,
        "exists": true
      }
    }
  }
}
```

### Get Application Info

```http
GET /actuator/info
```

**Response (200 OK):**
```json
{
  "app": {
    "name": "Claim Management Service",
    "version": "1.0.0",
    "description": "Insurance claim management microservice"
  }
}
```

## 📝 Claim Management Endpoints

### Create Claim

Creates a new insurance claim in the system.

```http
POST /claims
Content-Type: application/json
```

**Request Body:**
```json
{
  "policyNumber": "POL-2024-001234",
  "claimantName": "John Doe",
  "claimantEmail": "john.doe@email.com",
  "claimantPhone": "+1-555-123-4567",
  "description": "Vehicle collision at intersection of Main St and Oak Ave",
  "claimAmount": 5000.00,
  "incidentDate": "2024-01-15T14:30:00"
}
```

**Response (201 Created):**
```json
{
  "id": 1,
  "claimNumber": "CLM-2024-000001",
  "policyNumber": "POL-2024-001234",
  "claimantName": "John Doe",
  "claimantEmail": "john.doe@email.com",
  "claimantPhone": "+1-555-123-4567",
  "description": "Vehicle collision at intersection of Main St and Oak Ave",
  "claimAmount": 5000.00,
  "status": "SUBMITTED",
  "incidentDate": "2024-01-15T14:30:00",
  "createdAt": "2024-01-16T09:00:00",
  "updatedAt": "2024-01-16T09:00:00"
}
```

**Validation Rules:**
- `policyNumber`: Required, 5-50 characters
- `claimantName`: Required, 2-100 characters
- `claimantEmail`: Required, valid email format
- `claimantPhone`: Optional, valid phone format
- `description`: Required, 10-1000 characters
- `claimAmount`: Required, positive number, max $1,000,000
- `incidentDate`: Required, cannot be in future

### Get Claim by ID

Retrieves a specific claim by its unique identifier.

```http
GET /claims/{id}
```

**Path Parameters:**
- `id` (required): Unique claim identifier

**Response (200 OK):**
```json
{
  "id": 1,
  "claimNumber": "CLM-2024-000001",
  "policyNumber": "POL-2024-001234",
  "claimantName": "John Doe",
  "claimantEmail": "john.doe@email.com",
  "claimantPhone": "+1-555-123-4567",
  "description": "Vehicle collision at intersection",
  "claimAmount": 5000.00,
  "status": "SUBMITTED",
  "incidentDate": "2024-01-15T14:30:00",
  "createdAt": "2024-01-16T09:00:00",
  "updatedAt": "2024-01-16T09:00:00"
}
```

### Get Claim by Claim Number

Retrieves a claim by its business identifier.

```http
GET /claims/number/{claimNumber}
```

**Path Parameters:**
- `claimNumber` (required): Unique claim number (e.g., CLM-2024-000001)

**Response (200 OK):** Same as Get Claim by ID

### Get All Claims (Paginated)

Retrieves all claims with pagination and sorting support.

```http
GET /claims?page=0&size=10&sortBy=createdAt&sortDirection=desc
```

**Query Parameters:**
- `page` (optional): Page number (0-based), default: 0
- `size` (optional): Page size, default: 10
- `sortBy` (optional): Sort field, default: createdAt
- `sortDirection` (optional): Sort direction (asc/desc), default: desc

**Response (200 OK):**
```json
{
  "content": [
    {
      "id": 1,
      "claimNumber": "CLM-2024-000001",
      "policyNumber": "POL-2024-001234",
      "claimantName": "John Doe",
      "claimantEmail": "john.doe@email.com",
      "claimAmount": 5000.00,
      "status": "SUBMITTED",
      "createdAt": "2024-01-16T09:00:00"
    }
  ],
  "pageable": {
    "sort": {
      "sorted": true,
      "unsorted": false
    },
    "pageNumber": 0,
    "pageSize": 10
  },
  "totalElements": 1,
  "totalPages": 1,
  "last": true,
  "first": true,
  "numberOfElements": 1,
  "size": 10,
  "number": 0
}
```

### Update Claim

Updates an existing claim with new information.

```http
PUT /claims/{id}
Content-Type: application/json
```

**Path Parameters:**
- `id` (required): Unique claim identifier

**Request Body:** Same as Create Claim

**Response (200 OK):** Updated claim object

**Business Rules:**
- Cannot update claims in terminal states (PAID, REJECTED, CANCELLED)
- Status transitions must follow business rules

### Update Claim Status

Updates only the status of an existing claim.

```http
PATCH /claims/{id}/status?status=UNDER_REVIEW
```

**Path Parameters:**
- `id` (required): Unique claim identifier

**Query Parameters:**
- `status` (required): New status value

**Valid Status Values:**
- `SUBMITTED`
- `UNDER_REVIEW`
- `APPROVED`
- `REJECTED`
- `PAID`
- `CANCELLED`

**Status Transition Rules:**
| From | To | Valid |
|------|----|----|
| SUBMITTED | UNDER_REVIEW, CANCELLED | ✅ |
| UNDER_REVIEW | APPROVED, REJECTED, CANCELLED | ✅ |
| APPROVED | PAID, CANCELLED | ✅ |
| REJECTED | None | ❌ |
| PAID | None | ❌ |
| CANCELLED | None | ❌ |

**Response (200 OK):** Updated claim object

### Delete Claim

Deletes a claim from the system.

```http
DELETE /claims/{id}
```

**Path Parameters:**
- `id` (required): Unique claim identifier

**Response (204 No Content):** Empty response body

**Business Rules:**
- Only claims in SUBMITTED status can be deleted
- Deletion is permanent and cannot be undone

## 🔍 Search and Filter Endpoints

### Search Claims

Search claims using multiple optional criteria.

```http
GET /claims/search?policyNumber=POL-2024-001&status=SUBMITTED&claimantEmail=john@email.com&page=0&size=10
```

**Query Parameters:**
- `policyNumber` (optional): Filter by policy number
- `status` (optional): Filter by claim status
- `claimantEmail` (optional): Filter by claimant email
- `page` (optional): Page number, default: 0
- `size` (optional): Page size, default: 10
- `sortBy` (optional): Sort field, default: createdAt
- `sortDirection` (optional): Sort direction, default: desc

**Response (200 OK):** Paginated list of matching claims

### Get Claims by Policy Number

Retrieves all claims associated with a specific policy.

```http
GET /claims/policy/{policyNumber}
```

**Path Parameters:**
- `policyNumber` (required): Policy number to search for

**Response (200 OK):**
```json
[
  {
    "id": 1,
    "claimNumber": "CLM-2024-000001",
    "policyNumber": "POL-2024-001234",
    "claimantName": "John Doe",
    "claimAmount": 5000.00,
    "status": "SUBMITTED"
  }
]
```

### Get Claims by Claimant Email

Retrieves all claims for a specific claimant.

```http
GET /claims/claimant/{email}
```

**Path Parameters:**
- `email` (required): Claimant email address

**Response (200 OK):** Array of claims for the claimant

### Get High-Value Claims

Retrieves claims above a specified amount threshold.

```http
GET /claims/high-value?minimumAmount=10000
```

**Query Parameters:**
- `minimumAmount` (optional): Minimum claim amount, default: 10000

**Response (200 OK):** Array of high-value claims

### Get Claims by Date Range

Retrieves claims created within a specific date range.

```http
GET /claims/date-range?startDate=2024-01-01T00:00:00&endDate=2024-12-31T23:59:59
```

**Query Parameters:**
- `startDate` (required): Start date (ISO 8601 format)
- `endDate` (required): End date (ISO 8601 format)

**Response (200 OK):** Array of claims within date range

### Search Claims by Claimant Name

Search claims by partial claimant name match.

```http
GET /claims/search-by-name?name=John
```

**Query Parameters:**
- `name` (required): Partial or full claimant name

**Response (200 OK):** Array of matching claims

## 📊 Analytics Endpoints

### Get Claim Count by Status

Returns the number of claims with a specific status.

```http
GET /claims/count?status=SUBMITTED
```

**Query Parameters:**
- `status` (required): Status to count

**Response (200 OK):**
```json
5
```

## ❌ Error Responses

### Error Response Format

All error responses follow a consistent format:

```json
{
  "timestamp": "2024-01-16T10:30:00",
  "status": 400,
  "error": "Bad Request",
  "message": "Validation failed",
  "path": "/api/v1/claims",
  "details": {
    "additionalInfo": "value"
  }
}
```

### Common Error Codes

#### 400 Bad Request

**Validation Error:**
```json
{
  "timestamp": "2024-01-16T10:30:00",
  "status": 400,
  "error": "Validation Failed",
  "message": "Request validation failed",
  "path": "/api/v1/claims",
  "details": {
    "validationErrors": {
      "claimantEmail": "Please provide a valid email address",
      "claimAmount": "Claim amount must be greater than 0"
    }
  }
}
```

**Invalid State Transition:**
```json
{
  "timestamp": "2024-01-16T10:30:00",
  "status": 400,
  "error": "Invalid Claim State",
  "message": "Invalid status transition from SUBMITTED to PAID",
  "path": "/api/v1/claims/1/status",
  "details": {
    "currentStatus": "SUBMITTED",
    "attemptedStatus": "PAID",
    "claimIdentifier": "CLM-2024-000001"
  }
}
```

#### 404 Not Found

```json
{
  "timestamp": "2024-01-16T10:30:00",
  "status": 404,
  "error": "Claim Not Found",
  "message": "Claim not found with ID: 999",
  "path": "/api/v1/claims/999",
  "details": {
    "claimIdentifier": "999"
  }
}
```

#### 409 Conflict

```json
{
  "timestamp": "2024-01-16T10:30:00",
  "status": 409,
  "error": "Conflict",
  "message": "Cannot modify claim in terminal state: PAID",
  "path": "/api/v1/claims/1",
  "details": {
    "currentStatus": "PAID",
    "claimIdentifier": "CLM-2024-000001"
  }
}
```

#### 500 Internal Server Error

```json
{
  "timestamp": "2024-01-16T10:30:00",
  "status": 500,
  "error": "Internal Server Error",
  "message": "An unexpected error occurred. Please try again later.",
  "path": "/api/v1/claims"
}
```

## 📋 Data Models

### ClaimRequestDto

```json
{
  "policyNumber": "string (5-50 chars, required)",
  "claimantName": "string (2-100 chars, required)",
  "claimantEmail": "string (valid email, required)",
  "claimantPhone": "string (valid phone, optional)",
  "description": "string (10-1000 chars, required)",
  "claimAmount": "number (positive, max 1000000, required)",
  "status": "enum (optional, defaults to SUBMITTED)",
  "incidentDate": "datetime (ISO 8601, past/present, required)"
}
```

### ClaimResponseDto

```json
{
  "id": "number (auto-generated)",
  "claimNumber": "string (auto-generated, format: CLM-YYYY-NNNNNN)",
  "policyNumber": "string",
  "claimantName": "string",
  "claimantEmail": "string",
  "claimantPhone": "string",
  "description": "string",
  "claimAmount": "number",
  "status": "enum",
  "incidentDate": "datetime",
  "createdAt": "datetime (auto-generated)",
  "updatedAt": "datetime (auto-updated)"
}
```

### ClaimStatus Enum

```json
{
  "SUBMITTED": "Claim has been submitted and is awaiting initial review",
  "UNDER_REVIEW": "Claim is under review by claims department",
  "APPROVED": "Claim has been approved for payment",
  "REJECTED": "Claim has been rejected",
  "PAID": "Claim has been paid",
  "CANCELLED": "Claim has been cancelled"
}
```

## 🔧 Request/Response Examples

### Complete Claim Workflow

#### 1. Create Claim

```bash
curl -X POST http://localhost:8080/api/v1/claims \
  -H "Content-Type: application/json" \
  -d '{
    "policyNumber": "POL-2024-001234",
    "claimantName": "John Doe",
    "claimantEmail": "john.doe@email.com",
    "claimantPhone": "+1-555-123-4567",
    "description": "Vehicle collision at intersection",
    "claimAmount": 5000.00,
    "incidentDate": "2024-01-15T14:30:00"
  }'
```

#### 2. Update Status to Under Review

```bash
curl -X PATCH http://localhost:8080/api/v1/claims/1/status?status=UNDER_REVIEW
```

#### 3. Approve Claim

```bash
curl -X PATCH http://localhost:8080/api/v1/claims/1/status?status=APPROVED
```

#### 4. Pay Claim

```bash
curl -X PATCH http://localhost:8080/api/v1/claims/1/status?status=PAID
```

### Search Examples

#### Search by Multiple Criteria

```bash
curl "http://localhost:8080/api/v1/claims/search?status=SUBMITTED&policyNumber=POL-2024-001&page=0&size=10"
```

#### Get High-Value Claims

```bash
curl "http://localhost:8080/api/v1/claims/high-value?minimumAmount=10000"
```

#### Get Claims by Date Range

```bash
curl "http://localhost:8080/api/v1/claims/date-range?startDate=2024-01-01T00:00:00&endDate=2024-12-31T23:59:59"
```

## 🚀 Rate Limiting

Currently, no rate limiting is implemented. For production use, consider implementing:

- **Per-IP Rate Limiting**: 100 requests per minute
- **Per-User Rate Limiting**: 1000 requests per hour
- **Burst Handling**: Allow short bursts with token bucket algorithm

## 🔒 Security Considerations

### Current Implementation

- Input validation with Bean Validation
- SQL injection prevention through JPA
- XSS prevention through proper encoding

### Recommended Enhancements

- **Authentication**: JWT tokens or OAuth 2.0
- **Authorization**: Role-based access control
- **HTTPS**: Encrypt data in transit
- **API Keys**: For external integrations
- **Request Signing**: For sensitive operations

## 📈 Performance Considerations

### Response Times

- **Simple GET requests**: < 100ms
- **Complex searches**: < 500ms
- **Create/Update operations**: < 200ms

### Pagination

- Default page size: 10
- Maximum page size: 100
- Use cursor-based pagination for large datasets

### Caching

- Consider caching frequently accessed claims
- Implement cache invalidation on updates
- Use ETags for conditional requests

## 🧪 Testing the API

### Using Postman

1. Import the provided Postman collection
2. Set up the local environment
3. Run the collection to test all endpoints

### Using curl

See the examples throughout this documentation for curl commands.

### Using Swagger UI

1. Start the application
2. Navigate to `http://localhost:8080/swagger-ui.html`
3. Use the interactive interface to test endpoints

## 📚 Additional Resources

- **OpenAPI Specification**: Available at `/api-docs`
- **Health Checks**: Available at `/actuator/health`
- **Application Metrics**: Available at `/actuator/metrics`
- **Source Code**: Available in the project repository

## 🎯 Best Practices

### API Usage

1. **Use appropriate HTTP methods**: GET for retrieval, POST for creation, etc.
2. **Handle errors gracefully**: Check status codes and error messages
3. **Implement retry logic**: For transient failures
4. **Use pagination**: For large result sets
5. **Cache responses**: When appropriate

### Integration

1. **Validate responses**: Check response structure and data types
2. **Handle timeouts**: Set appropriate timeout values
3. **Log requests/responses**: For debugging and monitoring
4. **Use correlation IDs**: For request tracing
5. **Implement circuit breakers**: For resilience

---

**Happy API Integration! 🚀**