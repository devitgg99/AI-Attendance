# Attendance Management API

This document demonstrates how to use the attendance management system.

## API Endpoints

### 1. Check-in

```http
POST /api/attendance/checkin
Authorization: Bearer <your-jwt-token>
```

### 2. Check-out

```http
POST /api/attendance/checkout
Authorization: Bearer <your-jwt-token>
```

### 3. Get Attendance History (GET)

```http
GET /api/attendance/history?status=CHECKIN_LATE&startDate=2024-01-01&endDate=2024-01-31
Authorization: Bearer <your-jwt-token>
```

**Parameters:**

- `status` (optional): Filter by attendance status (MISSED_CHECKIN, CHECKIN_LATE, ABSENT, MISSED_CHECKOUT, PRESENT)
- `startDate` (optional): Start date in yyyy-MM-dd format
- `endDate` (optional): End date in yyyy-MM-dd format

**Note:** If no dates are provided, it defaults to the current month (first day to last day). If no status is provided, returns all attendance records.

### 4. Get Attendance by Date (GET)

```http
GET /api/attendance/date?date=2024-01-15
Authorization: Bearer <your-jwt-token>
```

**Note:** If no date is provided, it defaults to the current date.

## Usage Examples

### Example 1: Check-in

```bash
curl -X POST "http://localhost:8080/api/attendance/checkin" \
  -H "Authorization: Bearer <your-jwt-token>"
```

### Example 2: Check-out

```bash
curl -X POST "http://localhost:8080/api/attendance/checkout" \
  -H "Authorization: Bearer <your-jwt-token>"
```

### Example 3: Get attendance history for current month (default)

```bash
curl -X GET "http://localhost:8080/api/attendance/history" \
  -H "Authorization: Bearer <your-jwt-token>"
```

### Example 4: Get attendance history for specific date range

```bash
curl -X GET "http://localhost:8080/api/attendance/history?startDate=2024-01-01&endDate=2024-01-31" \
  -H "Authorization: Bearer <your-jwt-token>"
```

### Example 5: Get late check-ins for current month

```bash
curl -X GET "http://localhost:8080/api/attendance/history?status=CHECKIN_LATE" \
  -H "Authorization: Bearer <your-jwt-token>"
```

### Example 6: Get present employees for specific date range

```bash
curl -X GET "http://localhost:8080/api/attendance/history?status=PRESENT&startDate=2024-01-01&endDate=2024-01-31" \
  -H "Authorization: Bearer <your-jwt-token>"
```

### Example 7: Get all attendance records for a specific date

```bash
curl -X GET "http://localhost:8080/api/attendance/date?date=2024-01-15" \
  -H "Authorization: Bearer <your-jwt-token>"
```

### Example 8: Get all attendance records for current date (default)

```bash
curl -X GET "http://localhost:8080/api/attendance/date" \
  -H "Authorization: Bearer <your-jwt-token>"
```

## Response Format

All attendance endpoints return the same response format:

```json
{
  "success": true,
  "message": "Attendance records retrieved successfully for date: 2024-01-15",
  "payload": {
    "attendance_records": [
      {
        "attendance_id": "uuid-here",
        "attendance_date": "2024-01-15",
        "check_in_time": "08:30:00",
        "check_out_time": "17:30:00",
        "checkin_status": "CHECKIN_LATE",
        "checkout_status": "CHECKOUT",
        "date_status": "WEEKDAY",
        "checkin_datetime": "2024-01-15T08:30:00",
        "checkout_datetime": "2024-01-15T17:30:00"
      }
    ]
  },
  "status": "OK"
}
```

## Response Fields

The attendance response includes the following fields:

### Individual Attendance Record Fields

| Field               | Type   | Description                                              |
| ------------------- | ------ | -------------------------------------------------------- |
| `attendance_id`     | String | Unique identifier for the attendance record              |
| `attendance_date`   | String | Date of attendance (yyyy-MM-dd)                          |
| `check_in_time`     | String | Check-in time (HH:mm:ss)                                 |
| `check_out_time`    | String | Check-out time (HH:mm:ss)                                |
| `checkin_status`    | String | Check-in status (CHECKIN, CHECKIN_LATE)                  |
| `checkout_status`   | String | Check-out status (CHECKOUT, PERMISSION, MISSED_CHECKOUT) |
| `date_status`       | String | Date type (WEEKDAY, WEEKEND, OVERTIME)                   |
| `checkin_datetime`  | String | Full check-in datetime (yyyy-MM-dd'T'HH:mm:ss)           |
| `checkout_datetime` | String | Full check-out datetime (yyyy-MM-dd'T'HH:mm:ss)          |

**Note:** `checkin_datetime` and `checkout_datetime` provide full datetime information combining the attendance date with the respective times.

## Default Date Behavior

### GET `/api/attendance/history`

- **Default Date Range**: First day of current month to last day of current month
- **Custom Date Range**: Provide both `startDate` and `endDate` parameters
- **Status Filter**: Optional `status` parameter (MISSED_CHECKIN, CHECKIN_LATE, ABSENT, MISSED_CHECKOUT, PRESENT)
- **Returns**: Attendance records filtered by status and date range

### GET `/api/attendance/date`

- **Default Date**: Current date
- **Custom Date**: Provide `date` parameter in yyyy-MM-dd format
- **Returns**: All attendance records for the specified date

## Date Format

All dates should be provided in `yyyy-MM-dd` format:

- `2024-01-01` (January 1, 2024)
- `2024-12-31` (December 31, 2024)

## Implementation Details

- Repository methods are optimized for date-based queries
- All queries support date filtering
- Check-in deadline: 8:01 AM (after this time is considered late)
- Check-out deadline: 5:00 PM (must check out after this time)
- Timezone: Asia/Jakarta
