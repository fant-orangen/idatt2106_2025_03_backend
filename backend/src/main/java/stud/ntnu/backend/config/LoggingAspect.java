package stud.ntnu.backend.config;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.AfterThrowing;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.RequestBody;

import java.lang.reflect.Parameter;
import java.util.Arrays;
import java.util.stream.Collectors;

/**
 * Aspect for logging execution of all Spring MVC controller methods. This aspect logs: 1. All API
 * requests received from the frontend 2. All responses sent back to the frontend, including error
 * information 3. All errors that occur during request processing
 */
@Aspect
@Component
public class LoggingAspect {

  private final Logger log = LoggerFactory.getLogger(this.getClass());

  /**
   * Pointcut that matches all REST controllers in the application.
   */
  @Pointcut("within(@org.springframework.web.bind.annotation.RestController *)")
  public void controllerPointcut() {
    // Method is empty as this is just a Pointcut, the implementations are in the advices.
  }

  /**
   * Advice that logs when a controller method is entered and exited. This logs both the request and
   * the response.
   *
   * @param joinPoint join point for advice
   * @return result
   * @throws Throwable throws IllegalArgumentException
   */
  @Around("controllerPointcut()")
  public Object logAround(ProceedingJoinPoint joinPoint) throws Throwable {
    // Log the request
    logRequest(joinPoint);

    try {
      // Execute the method and get the result
      Object result = joinPoint.proceed();

      // Log the response
      logResponse(joinPoint, result);

      return result;
    } catch (Exception e) {
      // Log the error and rethrow
      String className = joinPoint.getSignature().getDeclaringTypeName();
      String methodName = joinPoint.getSignature().getName();

      log.error(
          "\n┌─────────────────────────────────────────────────────────────────────────────┐" +
              "\n│ API PROCESSING ERROR                                                         │" +
              "\n├─────────────────────────────────────────────────────────────────────────────┤" +
              "\n│ Endpoint: {}.{}()                                                           " +
              "\n│ Error: {}                                                                   " +
              "\n└─────────────────────────────────────────────────────────────────────────────┘",
          className, methodName, e.getMessage());
      throw e;
    }
  }

  /**
   * Advice that logs when a controller method throws an exception.
   *
   * @param joinPoint join point for advice
   * @param e         exception
   */
  @AfterThrowing(pointcut = "controllerPointcut()", throwing = "e")
  public void logAfterThrowing(JoinPoint joinPoint, Throwable e) {
    String className = joinPoint.getSignature().getDeclaringTypeName();
    String methodName = joinPoint.getSignature().getName();
    String cause = e.getCause() != null ? e.getCause().toString() : "NULL";

    log.error("\n┌─────────────────────────────────────────────────────────────────────────────┐" +
            "\n│ API EXCEPTION THROWN                                                         │" +
            "\n├─────────────────────────────────────────────────────────────────────────────┤" +
            "\n│ Endpoint: {}.{}()                                                           " +
            "\n│ Exception: {}                                                               " +
            "\n│ Cause: {}                                                                   " +
            "\n└─────────────────────────────────────────────────────────────────────────────┘",
        className, methodName, e.getClass().getName(), cause);
  }

  /**
   * Log the request details.
   *
   * @param joinPoint the join point
   */
  private void logRequest(JoinPoint joinPoint) {
    String className = joinPoint.getSignature().getDeclaringTypeName();
    String methodName = joinPoint.getSignature().getName();

    // Extract request body parameters if available
    String requestParams = extractRequestParams(joinPoint);

    log.info("\n┌─────────────────────────────────────────────────────────────────────────────┐" +
            "\n│ API REQUEST RECEIVED                                                         │" +
            "\n├─────────────────────────────────────────────────────────────────────────────┤" +
            "\n│ Endpoint: {}.{}()                                                           " +
            "\n│ Parameters: [{}]                                                            " +
            "\n└─────────────────────────────────────────────────────────────────────────────┘",
        className, methodName, requestParams);
  }

  /**
   * Log the response details.
   *
   * @param joinPoint the join point
   * @param result    the result of the method execution
   */
  private void logResponse(JoinPoint joinPoint, Object result) {
    String className = joinPoint.getSignature().getDeclaringTypeName();
    String methodName = joinPoint.getSignature().getName();

    boolean isError = false;
    String status = "SUCCESS";
    String statusCode = "";

    if (result instanceof ResponseEntity) {
      ResponseEntity<?> responseEntity = (ResponseEntity<?>) result;
      isError = responseEntity.getStatusCode().isError();
      statusCode = responseEntity.getStatusCode().toString();
      status = isError ? "ERROR" : "SUCCESS";
    }

    String headerTitle = isError ? "API RESPONSE ERROR" : "API RESPONSE SUCCESS";

    log.info("\n┌─────────────────────────────────────────────────────────────────────────────┐" +
            "\n│ {}{}│" +
            "\n├─────────────────────────────────────────────────────────────────────────────┤" +
            "\n│ Endpoint: {}.{}()                                                           " +
            "\n│ Status: {}                                                                  " +
            (statusCode.isEmpty() ? ""
                : "\n│ Status Code: {}                                                             ") +
            "\n└─────────────────────────────────────────────────────────────────────────────┘",
        headerTitle, " ".repeat(Math.max(0, 75 - headerTitle.length())),
        className, methodName, status, statusCode);
  }

  /**
   * Extract request parameters from the join point.
   *
   * @param joinPoint the join point
   * @return a string representation of the request parameters
   */
  private String extractRequestParams(JoinPoint joinPoint) {
    Object[] args = joinPoint.getArgs();
    if (args == null || args.length == 0) {
      return "no parameters";
    }

    return Arrays.stream(args)
        .map(arg -> arg == null ? "null" : arg.toString())
        .collect(Collectors.joining(", "));
  }
}
