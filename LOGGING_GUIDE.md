# AppLogger Usage Guide

## Overview

`AppLogger` is a custom logging wrapper class that provides a consistent and convenient way to log messages throughout the application. It wraps SLF4J logger with additional features like automatic component tagging and MDC (Mapped Diagnostic Context) support.

## Creating a Logger Instance

```java
import ro.sapientia.furniture.util.AppLogger;

public class MyService {
    private static final AppLogger logger = AppLogger.getLogger(MyService.class);
    
    // ... your code
}
```

## Basic Logging Methods

### Error Logging

```java
// Simple error message
logger.error("Failed to process request");

// Error with exception
try {
    // some code
} catch (Exception e) {
    logger.error("Failed to save data", e);
}

// Error with context
logger.error("Database connection failed", 
    AppLogger.context()
        .with("database", "furniture")
        .with("host", "localhost")
        .build()
);

// Error with exception and context
logger.error("Failed to process order", exception,
    AppLogger.context()
        .with("orderId", orderId)
        .with("userId", userId)
        .build()
);
```

### Warning Logging

```java
// Simple warning
logger.warn("Cache is getting full");

// Warning with exception
logger.warn("Failed to update cache", exception);

// Warning with context
logger.warn("Slow query detected",
    AppLogger.context()
        .with("queryTime", "2500ms")
        .with("threshold", "1000ms")
        .build()
);
```

### Info Logging

```java
// Simple info message
logger.info("Application started successfully");

// Info with context
logger.info("User logged in",
    AppLogger.context()
        .with("userId", user.getId())
        .with("username", user.getUsername())
        .build()
);
```

### Debug Logging

```java
// Simple debug message
logger.debug("Processing item");

// Debug with context
logger.debug("Cache hit",
    AppLogger.context()
        .with("key", cacheKey)
        .with("size", cacheSize)
        .build()
);
```

## Advanced Features

### Method Entry/Exit Logging

```java
public void processOrder(Long orderId, String userId) {
    logger.entering("processOrder", orderId, userId);
    
    try {
        // ... your business logic
        
        logger.exiting("processOrder", result);
        return result;
    } catch (Exception e) {
        logger.error("Error in processOrder", e);
        throw e;
    }
}
```

### Request ID Tracking

```java
// In a filter or interceptor
String requestId = AppLogger.generateRequestId();
try {
    // Process request
    logger.info("Processing request");
} finally {
    AppLogger.clearRequestId();
}

// Or manually set a request ID
AppLogger.setRequestId("custom-request-id-123");
```

### User Context

```java
// Set user context for all subsequent logs
AppLogger.setUserId("user123");

logger.info("User action performed");

// Clear when done
AppLogger.clearUserId();
```

### Clear All Context

```java
// Clear all MDC context
AppLogger.clearContext();
```

### Check Log Level

```java
if (logger.isDebugEnabled()) {
    String expensiveDebugInfo = computeExpensiveDebugInfo();
    logger.debug("Debug info: " + expensiveDebugInfo);
}
```

## Real-World Examples

### Controller Example

```java
@RestController
public class FurnitureController {
    private static final AppLogger logger = AppLogger.getLogger(FurnitureController.class);
    
    @PostMapping("/cut")
    public ResponseEntity<CutResponseDTO> optimizeCut(@Valid @RequestBody CutRequestDTO request) {
        String requestId = AppLogger.generateRequestId();
        
        try {
            logger.info("Received cut optimization request",
                AppLogger.context()
                    .with("sheetWidth", request.getSheetWidth())
                    .with("sheetHeight", request.getSheetHeight())
                    .with("elementCount", request.getElements().size())
                    .build()
            );
            
            CutResponseDTO response = cutOptimizationService.optimizeCutting(request);
            
            logger.info("Cut optimization completed successfully",
                AppLogger.context()
                    .with("placementCount", response.getPlacements().size())
                    .build()
            );
            
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            logger.error("Failed to optimize cutting", e,
                AppLogger.context()
                    .with("requestId", requestId)
                    .build()
            );
            throw e;
        } finally {
            AppLogger.clearContext();
        }
    }
}
```

### Service Example

```java
@Service
public class FurnitureBodyService {
    private static final AppLogger logger = AppLogger.getLogger(FurnitureBodyService.class);
    
    public FurnitureBodyDTO findFurnitureBodyById(Long id) {
        logger.debug("Finding furniture body", 
            AppLogger.context().with("id", id).build()
        );
        
        Optional<FurnitureBody> entity = repository.findById(id);
        
        if (entity.isEmpty()) {
            logger.warn("Furniture body not found",
                AppLogger.context().with("id", id).build()
            );
            throw new NotFoundException("Furniture body not found: " + id);
        }
        
        logger.debug("Furniture body found successfully");
        return mapper.toDTO(entity.get());
    }
    
    public FurnitureBodyDTO create(FurnitureBodyDTO dto) {
        logger.entering("create", dto);
        
        try {
            FurnitureBody entity = mapper.toEntity(dto);
            FurnitureBody saved = repository.save(entity);
            
            logger.info("Furniture body created",
                AppLogger.context()
                    .with("id", saved.getId())
                    .with("width", saved.getWidth())
                    .with("height", saved.getHeight())
                    .build()
            );
            
            FurnitureBodyDTO result = mapper.toDTO(saved);
            logger.exiting("create", result);
            return result;
            
        } catch (Exception e) {
            logger.error("Failed to create furniture body", e);
            throw e;
        }
    }
}
```

### Exception Handler Example

```java
@ControllerAdvice
public class GlobalExceptionHandler {
    private static final AppLogger logger = AppLogger.getLogger(GlobalExceptionHandler.class);
    
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> handleValidationException(MethodArgumentNotValidException ex) {
        logger.warn("Validation error occurred",
            AppLogger.context()
                .with("errorCount", ex.getBindingResult().getErrorCount())
                .with("requestId", MDC.get("requestId"))
                .build()
        );
        
        // ... handle exception
    }
    
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleGeneralException(Exception ex) {
        logger.error("Unexpected error occurred", ex,
            AppLogger.context()
                .with("exceptionType", ex.getClass().getSimpleName())
                .with("requestId", MDC.get("requestId"))
                .build()
        );
        
        // ... handle exception
    }
}
```

## Log Format

Logs will appear in the following format:

```
2025-12-09 15:30:45.123 [http-nio-8081-exec-1] [req-uuid-123] INFO  ro.sapientia.furniture.controller.FurnitureController - [FurnitureController] Received cut optimization request
```

Where:
- `2025-12-09 15:30:45.123` - Timestamp
- `[http-nio-8081-exec-1]` - Thread name
- `[req-uuid-123]` - Request ID (if set)
- `INFO` - Log level
- `ro.sapientia.furniture.controller.FurnitureController` - Logger name
- `[FurnitureController]` - Component name
- Message

## Best Practices

1. **Use appropriate log levels:**
   - `ERROR`: System errors, exceptions that require immediate attention
   - `WARN`: Unexpected situations that don't prevent operation
   - `INFO`: Important business events, state changes
   - `DEBUG`: Detailed diagnostic information
   - `TRACE`: Very detailed diagnostic information (rarely used)

2. **Add context to important logs:**
   ```java
   logger.error("Payment failed", exception,
       AppLogger.context()
           .with("amount", amount)
           .with("currency", currency)
           .with("userId", userId)
           .build()
   );
   ```

3. **Use request IDs for tracking:**
   ```java
   AppLogger.generateRequestId(); // At the start of request
   // ... process request
   AppLogger.clearContext(); // At the end
   ```

4. **Check log level for expensive operations:**
   ```java
   if (logger.isDebugEnabled()) {
       logger.debug("Complex object: " + object.toDetailedString());
   }
   ```

5. **Clean up context in finally blocks:**
   ```java
   try {
       AppLogger.setUserId(userId);
       // ... process
   } finally {
       AppLogger.clearContext();
   }
   ```

6. **Don't log sensitive information:**
   - Passwords
   - Credit card numbers
   - Personal identification numbers
   - API keys/tokens

## Configuration

Log levels can be configured in `application.properties`:

```properties
# Global log level
logging.level.root=INFO

# Package-specific log levels
logging.level.ro.sapientia.furniture=DEBUG
logging.level.ro.sapientia.furniture.controller=INFO
logging.level.ro.sapientia.furniture.service=DEBUG

# Framework log levels
logging.level.org.springframework.web=INFO
logging.level.org.hibernate.SQL=DEBUG
```

## Log Files

Logs are written to:
- **Console**: All log levels based on configuration
- **Application log**: `logs/furniture-api.log` (all logs)
- **Error log**: `logs/furniture-api-error.log` (ERROR level only)

Files are automatically rotated when they reach 50MB, with history kept for 30 days (application logs) or 90 days (error logs).

