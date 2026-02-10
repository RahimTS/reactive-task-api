#!/bin/bash

BASE_URL="http://localhost:8080/api/tasks"

echo "============================================"
echo "  Reactive Task API - Test Script"
echo "============================================"

# 1. Get all tasks
echo -e "\n--- 1. GET all tasks ---"
curl -s $BASE_URL
echo ""

# 2. Create a task
echo -e "\n--- 2. CREATE task ---"
CREATED=$(curl -s -X POST $BASE_URL \
  -H "Content-Type: application/json" \
  -d '{
    "title": "Learn WebFlux",
    "description": "Master reactive programming",
    "status": "IN_PROGRESS"
  }')
echo "$CREATED"
TASK_ID=$(echo "$CREATED" | sed -n 's/.*"id":"\([^"]*\)".*/\1/p')
echo -e "\nCreated task ID: $TASK_ID"

# 3. Get task by ID
echo -e "\n--- 3. GET task by ID ---"
curl -s $BASE_URL/$TASK_ID
echo ""

# 4. Filter by status
echo -e "\n--- 4. GET tasks by status (IN_PROGRESS) ---"
curl -s "$BASE_URL?status=IN_PROGRESS"
echo ""

# 5. Update task
echo -e "\n--- 5. UPDATE task ---"
curl -s -X PUT $BASE_URL/$TASK_ID \
  -H "Content-Type: application/json" \
  -d '{
    "title": "Learn WebFlux - Updated",
    "description": "Complete reactive course",
    "status": "DONE"
  }'
echo ""

# 6. Get stats
echo -e "\n--- 6. GET stats ---"
curl -s $BASE_URL/stats
echo ""

# 7. Delete task
echo -e "\n--- 7. DELETE task ---"
curl -s -o /dev/null -w "HTTP Status: %{http_code}" -X DELETE $BASE_URL/$TASK_ID
echo ""

# 8. Validation error (missing title)
echo -e "\n--- 8. VALIDATION error (missing title) ---"
curl -s -X POST $BASE_URL \
  -H "Content-Type: application/json" \
  -d '{
    "description": "No title",
    "status": "TODO"
  }'
echo ""

echo -e "\n============================================"
echo "  Tests complete!"
echo "============================================"
