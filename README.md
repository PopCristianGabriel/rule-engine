# 🧠 Rule Engine Example Dataset & Rules

This document contains sample **rules** and corresponding **event data** used in testing and showcasing the capabilities of the rule engine.

The examples demonstrate:

- Simple conditions (e.g., BET ≥ $5)
- Streak-based conditions (e.g., BET 3 consecutive days)
- Distinct intervals (e.g., BET on 3 different weeks)
- Complex conditions with logical operators (AND, OR, XOR)

---

## 📘 Rule 1 – "User has deposited at least $10"


🔸 Rule JSON

```json
{
  "eventType": "DEPOSIT",
  "operator": "GREATER_THAN_OR_EQUAL",
  "targetValue": 10.0
}
```

🟢 Passing Events – user 201

```json
[
  { "eventType": "DEPOSIT", "amount": 5.0, "userId": 201, "date": "2025-04-01T10:00:00Z" },
  { "eventType": "DEPOSIT", "amount": 6.0, "userId": 201, "date": "2025-04-02T12:00:00Z" }
]
```

🔴 Failing Events – user 202

```json
[
  { "eventType": "DEPOSIT", "amount": 4.0, "userId": 202, "date": "2025-04-01T10:00:00Z" },
  { "eventType": "DEPOSIT", "amount": 3.0, "userId": 202, "date": "2025-04-03T10:00:00Z" }
]
```

## 📘 Rule 2 – "User has bet every day for 3 consecutive days"

🔸 Rule JSON

```json
{
  "eventType": "BET",
  "operator": "EQUALS",
  "targetValue": 1.0,
  "streakInterval": "DAILY",
  "streakLength": 3
}
```

🟢 Passing Events – user 203

```json
[
  { "eventType": "BET", "amount": 1.0, "userId": 203, "date": "2025-04-01T10:00:00Z" },
  { "eventType": "BET", "amount": 1.0, "userId": 203, "date": "2025-04-02T10:00:00Z" },
  { "eventType": "BET", "amount": 1.0, "userId": 203, "date": "2025-04-03T10:00:00Z" }
]
```

🔴 Failing Events – user 204

```json
[
  { "eventType": "BET", "amount": 1.0, "userId": 204, "date": "2025-04-01T10:00:00Z" },
  { "eventType": "BET", "amount": 1.0, "userId": 204, "date": "2025-04-03T10:00:00Z" }
]
```

## 📘 Rule 3 – "User has bet in 3 different weeks this month"

🔸 Rule JSON

```json
{
  "eventType": "BET",
  "operator": "GREATER_THAN_OR_EQUAL",
  "targetValue": 1.0,
  "dateRange": "THIS_MONTH",
  "streakInterval": "WEEKLY",
  "requiredDistinctIntervals": 3
}
```

🟢 Passing Events – user 205

```json
[
  { "eventType": "BET", "amount": 1.0, "userId": 205, "date": "2025-04-01T10:00:00Z" },
  { "eventType": "BET", "amount": 1.0, "userId": 205, "date": "2025-04-08T10:00:00Z" },
  { "eventType": "BET", "amount": 1.0, "userId": 205, "date": "2025-04-15T10:00:00Z" }
]
```

🔴 Failing Events – user 206

```json
[
  { "eventType": "BET", "amount": 1.0, "userId": 206, "date": "2025-04-01T10:00:00Z" },
  { "eventType": "BET", "amount": 1.0, "userId": 206, "date": "2025-04-02T10:00:00Z" },
  { "eventType": "BET", "amount": 1.0, "userId": 206, "date": "2025-04-03T10:00:00Z" }
]
```

## 📘 Complex Rule 1 – "User has deposited at least $10 AND has bet at least $5"

🔸 Rule JSON

```json
{
  "operator": "AND",
  "left": {
    "eventType": "DEPOSIT",
    "operator": "GREATER_THAN_OR_EQUAL",
    "targetValue": 10.0
  },
  "right": {
    "eventType": "BET",
    "operator": "GREATER_THAN_OR_EQUAL",
    "targetValue": 5.0
  }
}
```

🟢 Passing Events – user 301

```json
[
  { "eventType": "DEPOSIT", "amount": 11.0, "userId": 301, "date": "2025-04-01T10:00:00Z" },
  { "eventType": "BET", "amount": 6.0, "userId": 301, "date": "2025-04-02T12:00:00Z" }
]
```

🔴 Failing Events – user 302

```json
[
  { "eventType": "DEPOSIT", "amount": 12.0, "userId": 302, "date": "2025-04-01T10:00:00Z" }
]
```

## 📘 Complex Rule 2 – "User has deposited ≥ $5 OR bet for 3 consecutive days"

🔸 Rule JSON

```json
{
  "operator": "OR",
  "left": {
    "eventType": "DEPOSIT",
    "operator": "GREATER_THAN_OR_EQUAL",
    "targetValue": 5.0
  },
  "right": {
    "eventType": "BET",
    "operator": "EQUALS",
    "targetValue": 1.0,
    "streakInterval": "DAILY",
    "streakLength": 3
  }
}
```

🟢 User 303 – Passes (consecutive bets)

```json
[
  { "eventType": "BET", "amount": 1.0, "userId": 303, "date": "2025-04-01T10:00:00Z" },
  { "eventType": "BET", "amount": 1.0, "userId": 303, "date": "2025-04-02T10:00:00Z" },
  { "eventType": "BET", "amount": 1.0, "userId": 303, "date": "2025-04-03T10:00:00Z" }
]
```

🔴 User 304 – Fails (no deposit, no streak)

```json
[
  { "eventType": "BET", "amount": 1.0, "userId": 304, "date": "2025-04-01T10:00:00Z" },
  { "eventType": "BET", "amount": 1.0, "userId": 304, "date": "2025-04-03T10:00:00Z" }
]
```