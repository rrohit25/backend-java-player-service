# 🎯 **Unified Players Endpoint Guide**

## 📋 **Overview**

The `/v1/players/paginated` endpoint now serves as a **single, unified endpoint** for retrieving players. It intelligently handles both scenarios:

1. **No parameters**: Returns all players (like the old `/v1/players` endpoint)
2. **With pagination parameters**: Returns paginated results (like the old `/v1/players/paginated` endpoint)

## 🔗 **Endpoint Details**

```
GET /v1/players/paginated
```

**Authentication Required**: `Authorization: Basic YWRtaW46YWRtaW4xMjM=`

## 📊 **Usage Scenarios**

### **Scenario 1: Get All Players**
**URL**: `GET /v1/players/paginated`
**Parameters**: None

**Response Format**:
```json
{
  "players": [
    {
      "playerId": "abadfe01",
      "firstName": "Fernando",
      "lastName": "Abad",
      // ... all player fields
    },
    // ... all 19,373 players
  ]
}
```

**Example**:
```bash
curl -X GET "http://localhost:8080/v1/players/paginated" \
  -H "Authorization: Basic YWRtaW46YWRtaW4xMjM="
```

### **Scenario 2: Get Paginated Players**
**URL**: `GET /v1/players/paginated?page=0&size=5`
**Parameters**: 
- `page` (optional): Page number (0-based, default: 0)
- `size` (optional): Page size (default: 10)
- `sortBy` (optional): Sort field (default: "playerId")
- `sortDirection` (optional): "asc" or "desc" (default: "asc")

**Response Format**:
```json
{
  "players": [
    {
      "playerId": "abadfe01",
      "firstName": "Fernando",
      "lastName": "Abad",
      // ... all player fields
    },
    // ... 5 players total
  ],
  "page": 0,
  "size": 5,
  "totalElements": 19373,
  "totalPages": 3875,
  "hasNext": true,
  "hasPrevious": false,
  "isFirst": true,
  "isLast": false
}
```

## 🧪 **Test Cases**

### **Test 1: All Players (No Parameters)**
```bash
curl -X GET "http://localhost:8080/v1/players/paginated" \
  -H "Authorization: Basic YWRtaW46YWRtaW4xMjM="
```
**Expected**: Returns all 19,373 players in simple format

### **Test 2: Paginated Players (Both Parameters)**
```bash
curl -X GET "http://localhost:8080/v1/players/paginated?page=0&size=5" \
  -H "Authorization: Basic YWRtaW46YWRtaW4xMjM="
```
**Expected**: Returns 5 players with pagination metadata

### **Test 3: Only Page Parameter**
```bash
curl -X GET "http://localhost:8080/v1/players/paginated?page=1" \
  -H "Authorization: Basic YWRtaW46YWRtaW4xMjM="
```
**Expected**: Returns 10 players (default size) from page 1

### **Test 4: Only Size Parameter**
```bash
curl -X GET "http://localhost:8080/v1/players/paginated?size=3" \
  -H "Authorization: Basic YWRtaW46YWRtaW4xMjM="
```
**Expected**: Returns 3 players from page 0 (default page)

### **Test 5: With Sorting**
```bash
curl -X GET "http://localhost:8080/v1/players/paginated?page=0&size=5&sortBy=firstName&sortDirection=desc" \
  -H "Authorization: Basic YWRtaW46YWRtaW4xMjM="
```
**Expected**: Returns 5 players sorted by firstName in descending order

## 🔧 **Parameter Details**

| Parameter | Type | Required | Default | Description |
|-----------|------|----------|---------|-------------|
| `page` | Integer | No | 0 | Page number (0-based indexing) |
| `size` | Integer | No | 10 | Number of players per page (1-100) |
| `sortBy` | String | No | "playerId" | Field to sort by |
| `sortDirection` | String | No | "asc" | Sort direction ("asc" or "desc") |

## 📝 **Available Sort Fields**

- `playerId` (default)
- `firstName`
- `lastName`
- `birthYear`
- `birthMonth`
- `birthDay`
- `birthCountry`
- `birthState`
- `birthCity`
- `deathYear`
- `deathMonth`
- `deathDay`
- `deathCountry`
- `deathState`
- `deathCity`
- `nameFirst`
- `nameLast`
- `nameGiven`
- `weight`
- `height`
- `bats`
- `throws`
- `debut`
- `finalGame`
- `retroID`
- `bbrefID`

## 🚨 **Error Handling**

### **Authentication Required**
```bash
curl -X GET "http://localhost:8080/v1/players/paginated"
```
**Response**: `401 Unauthorized`

### **Invalid Parameters**
```bash
curl -X GET "http://localhost:8080/v1/players/paginated?page=-1&size=1000" \
  -H "Authorization: Basic YWRtaW46YWRtaW4xMjM="
```
**Response**: `500 Internal Server Error` (parameters are validated and corrected)

## 📊 **Response Format Comparison**

### **All Players Response**
```json
{
  "players": [
    // Array of all players
  ]
}
```

### **Paginated Response**
```json
{
  "players": [
    // Array of players for current page
  ],
  "page": 0,
  "size": 5,
  "totalElements": 19373,
  "totalPages": 3875,
  "hasNext": true,
  "hasPrevious": false,
  "isFirst": true,
  "isLast": false
}
```

## 🎯 **Migration from Old Endpoints**

| Old Endpoint | New Endpoint | Notes |
|--------------|--------------|-------|
| `GET /v1/players` | `GET /v1/players/paginated` | Returns all players |
| `GET /v1/players/paginated?page=0&size=5` | `GET /v1/players/paginated?page=0&size=5` | Same functionality |

## ✅ **Benefits**

1. **Single Endpoint**: One URL for all player retrieval needs
2. **Backward Compatible**: Existing pagination requests work unchanged
3. **Flexible**: Supports both all players and paginated requests
4. **Intelligent**: Automatically detects intent based on parameters
5. **Consistent**: Same authentication and error handling

## 🔍 **Implementation Details**

The endpoint uses Spring's `@RequestParam(required = false)` to make pagination parameters optional. The logic checks:

1. If both `page` and `size` are `null` → Return all players
2. If either `page` or `size` is provided → Return paginated results
3. Default values are applied for missing pagination parameters

This provides a clean, intuitive API that handles both use cases seamlessly. 