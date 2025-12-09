package ro.sapientia.furniture.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * Custom logging wrapper class for consistent logging throughout the application.
 * Provides convenient methods for error, warning, info, and debug logging with context.
 */
public class AppLogger {

    private final Logger logger;
    private final String component;

    /**
     * Private constructor - use factory methods to create instances.
     */
    private AppLogger(Class<?> clazz) {
        this.logger = LoggerFactory.getLogger(clazz);
        this.component = clazz.getSimpleName();
    }

    /**
     * Create a logger instance for the given class.
     */
    public static AppLogger getLogger(Class<?> clazz) {
        return new AppLogger(clazz);
    }

    /**
     * Log an error message.
     */
    public void error(String message) {
        logger.error("[{}] {}", component, message);
    }

    /**
     * Log an error message with exception.
     */
    public void error(String message, Throwable throwable) {
        logger.error("[{}] {}", component, message, throwable);
    }

    /**
     * Log an error message with context.
     */
    public void error(String message, Map<String, String> context) {
        withContext(context, () -> logger.error("[{}] {}", component, message));
    }

    /**
     * Log an error message with exception and context.
     */
    public void error(String message, Throwable throwable, Map<String, String> context) {
        withContext(context, () -> logger.error("[{}] {}", component, message, throwable));
    }

    /**
     * Log a warning message.
     */
    public void warn(String message) {
        logger.warn("[{}] {}", component, message);
    }

    /**
     * Log a warning message with parameters (SLF4J-style placeholders).
     */
    public void warn(String message, Object... params) {
        logger.warn("[" + component + "] " + message, params);
    }

    /**
     * Log a warning message with exception.
     */
    public void warn(String message, Throwable throwable) {
        logger.warn("[{}] {}", component, message, throwable);
    }

    /**
     * Log a warning message with context.
     */
    public void warn(String message, Map<String, String> context) {
        withContext(context, () -> logger.warn("[{}] {}", component, message));
    }

    /**
     * Log an info message.
     */
    public void info(String message) {
        logger.info("[{}] {}", component, message);
    }

    /**
     * Log an info message with parameters (SLF4J-style placeholders).
     */
    public void info(String message, Object... params) {
        logger.info("[" + component + "] " + message, params);
    }

    /**
     * Log an info message with context.
     */
    public void info(String message, Map<String, String> context) {
        withContext(context, () -> logger.info("[{}] {}", component, message));
    }

    /**
     * Log a debug message.
     */
    public void debug(String message) {
        if (logger.isDebugEnabled()) {
            logger.debug("[{}] {}", component, message);
        }
    }

    /**
     * Log a debug message with parameters (SLF4J-style placeholders).
     */
    public void debug(String message, Object... params) {
        if (logger.isDebugEnabled()) {
            logger.debug("[" + component + "] " + message, params);
        }
    }

    /**
     * Log a debug message with context.
     */
    public void debug(String message, Map<String, String> context) {
        if (logger.isDebugEnabled()) {
            withContext(context, () -> logger.debug("[{}] {}", component, message));
        }
    }

    /**
     * Log a trace message.
     */
    public void trace(String message) {
        if (logger.isTraceEnabled()) {
            logger.trace("[{}] {}", component, message);
        }
    }

    /**
     * Log method entry for debugging.
     */
    public void entering(String methodName) {
        if (logger.isDebugEnabled()) {
            logger.debug("[{}] Entering method: {}", component, methodName);
        }
    }

    /**
     * Log method entry with parameters.
     */
    public void entering(String methodName, Object... params) {
        if (logger.isDebugEnabled()) {
            logger.debug("[{}] Entering method: {} with params: {}", component, methodName, params);
        }
    }

    /**
     * Log method exit for debugging.
     */
    public void exiting(String methodName) {
        if (logger.isDebugEnabled()) {
            logger.debug("[{}] Exiting method: {}", component, methodName);
        }
    }

    /**
     * Log method exit with result.
     */
    public void exiting(String methodName, Object result) {
        if (logger.isDebugEnabled()) {
            logger.debug("[{}] Exiting method: {} with result: {}", component, methodName, result);
        }
    }

    /**
     * Set a request ID in the logging context (MDC).
     */
    public static void setRequestId(String requestId) {
        MDC.put("requestId", requestId);
    }

    /**
     * Generate and set a new request ID.
     */
    public static String generateRequestId() {
        String requestId = UUID.randomUUID().toString();
        setRequestId(requestId);
        return requestId;
    }

    /**
     * Clear the request ID from the logging context.
     */
    public static void clearRequestId() {
        MDC.remove("requestId");
    }

    /**
     * Set a user ID in the logging context.
     */
    public static void setUserId(String userId) {
        MDC.put("userId", userId);
    }

    /**
     * Clear the user ID from the logging context.
     */
    public static void clearUserId() {
        MDC.remove("userId");
    }

    /**
     * Clear all MDC context.
     */
    public static void clearContext() {
        MDC.clear();
    }

    /**
     * Execute a logging action with temporary context.
     */
    private void withContext(Map<String, String> context, Runnable loggingAction) {
        if (context == null || context.isEmpty()) {
            loggingAction.run();
            return;
        }

        // Store existing MDC values
        Map<String, String> existing = new HashMap<>();
        context.keySet().forEach(key -> {
            String existingValue = MDC.get(key);
            if (existingValue != null) {
                existing.put(key, existingValue);
            }
        });

        try {
            // Set new context
            context.forEach(MDC::put);
            // Execute logging
            loggingAction.run();
        } finally {
            // Restore original context
            context.keySet().forEach(MDC::remove);
            existing.forEach(MDC::put);
        }
    }

    /**
     * Check if DEBUG level is enabled.
     */
    public boolean isDebugEnabled() {
        return logger.isDebugEnabled();
    }

    /**
     * Check if INFO level is enabled.
     */
    public boolean isInfoEnabled() {
        return logger.isInfoEnabled();
    }

    /**
     * Check if WARN level is enabled.
     */
    public boolean isWarnEnabled() {
        return logger.isWarnEnabled();
    }

    /**
     * Check if ERROR level is enabled.
     */
    public boolean isErrorEnabled() {
        return logger.isErrorEnabled();
    }

    /**
     * Builder class for creating context maps easily.
     */
    public static class ContextBuilder {
        private final Map<String, String> context = new HashMap<>();

        public ContextBuilder with(String key, String value) {
            context.put(key, value);
            return this;
        }

        public ContextBuilder with(String key, Object value) {
            context.put(key, value != null ? value.toString() : "null");
            return this;
        }

        public Map<String, String> build() {
            return context;
        }
    }

    /**
     * Create a new context builder.
     */
    public static ContextBuilder context() {
        return new ContextBuilder();
    }
}
