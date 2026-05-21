package com.gearx7.app.web.rest.errors;

import static org.springframework.core.annotation.AnnotatedElementUtils.findMergedAnnotation;

import jakarta.servlet.http.HttpServletRequest;
import java.net.URI;
import java.util.*;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.env.Environment;
import org.springframework.dao.ConcurrencyFailureException;
import org.springframework.dao.DataAccessException;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageConversionException;
import org.springframework.lang.Nullable;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.web.ErrorResponse;
import org.springframework.web.ErrorResponseException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.context.request.ServletWebRequest;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;
import tech.jhipster.config.JHipsterConstants;
import tech.jhipster.web.rest.errors.ProblemDetailWithCause;
import tech.jhipster.web.rest.errors.ProblemDetailWithCause.ProblemDetailWithCauseBuilder;
import tech.jhipster.web.util.HeaderUtil;

/**
 * Controller advice to translate the server side exceptions to client-friendly json structures.
 * The error response follows RFC7807 - Problem Details for HTTP APIs (https://tools.ietf.org/html/rfc7807).
 */
@ControllerAdvice
public class ExceptionTranslator extends ResponseEntityExceptionHandler {

    private static final String FIELD_ERRORS_KEY = "fieldErrors";
    private static final String MESSAGE_KEY = "message";
    private static final String PATH_KEY = "path";
    private static final boolean CASUAL_CHAIN_ENABLED = false;

    @Value("${jhipster.clientApp.name}")
    private String applicationName;

    private final Environment env;

    public ExceptionTranslator(Environment env) {
        this.env = env;
    }

    @ExceptionHandler(Throwable.class)
    public ResponseEntity<Object> handleAnyException(Throwable ex, NativeWebRequest request) {
        ProblemDetailWithCause pdCause = wrapAndCustomizeProblem(ex, request);
        return handleExceptionInternal((Exception) ex, pdCause, buildHeaders(ex), HttpStatusCode.valueOf(pdCause.getStatus()), request);
    }

    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<Object> handleAccessDeniedException(AccessDeniedException ex, NativeWebRequest request) {
        Map<String, Object> body = new LinkedHashMap<>();

        body.put("status", false);

        body.put("statusCode", HttpStatus.FORBIDDEN.value());

        HttpServletRequest httpRequest = request.getNativeRequest(HttpServletRequest.class);

        String path = httpRequest.getRequestURI();

        String method = httpRequest.getMethod();

        String message = "You are not authorized to perform this action.";

        // ================= MACHINES =================

        if (path.contains("/api/machines")) {
            if (path.contains("/by-owner")) {
                message = "You are not allowed to access partner machines.";
            } else if ("POST".equals(method)) {
                message = "You are not allowed to create machines.";
            } else if ("PUT".equals(method) || "PATCH".equals(method)) {
                message = "You are not allowed to update machines.";
            } else if ("DELETE".equals(method)) {
                message = "You are not allowed to delete machines.";
            } else {
                message = "You are not allowed to manage machines.";
            }
        }
        // ================= MACHINE OPERATORS =================

        else if (path.contains("/api/machine-operators")) {
            if ("POST".equals(method)) {
                message = "You are not allowed to create machine operators.";
            } else if ("PUT".equals(method) || "PATCH".equals(method)) {
                message = "You are not allowed to update machine operators.";
            } else if ("DELETE".equals(method)) {
                message = "You are not allowed to delete machine operators.";
            } else if ("GET".equals(method) && path.contains(("/partner"))) {
                message = "You are not allowed to access partner machine operators.";
            } else {
                message = "You are not allowed to manage machine operators.";
            }
        }
        // ================= VEHICLE DOCUMENTS =================

        else if (path.contains("/api/vehicle-documents")) {
            if ("POST".equals(method)) {
                message = "You are not allowed to upload vehicle documents.";
            } else if ("DELETE".equals(method)) {
                message = "You are not allowed to delete vehicle documents.";
            } else {
                message = "You are not allowed to manage vehicle documents.";
            }
        }
        // ================= BOOKINGS =================

        else if (path.contains("/api/bookings")) {
            if (path.contains("/by-owner")) {
                message = "You are not allowed to access partner bookings.";
            } else {
                message = "You are not authorized to manage bookings.";
            }
        }
        body.put("message", message);
        body.put("data", null);
        body.put("path", path);
        return ResponseEntity.status(HttpStatus.FORBIDDEN).body(body);
    }

    @ExceptionHandler(BadRequestAlertException.class)
    public ResponseEntity<Object> handleBadRequestException(BadRequestAlertException ex, NativeWebRequest request) {
        Map<String, Object> body = new LinkedHashMap<>();

        body.put("status", false);

        body.put("statusCode", 400);

        body.put("message", ex.getProblemDetailWithCause().getProperties().get("message"));

        body.put("data", null);

        body.put("path", request.getNativeRequest(HttpServletRequest.class).getRequestURI());

        return ResponseEntity.badRequest().body(body);
    }

    @ExceptionHandler(NotFoundAlertException.class)
    public ResponseEntity<Object> handleNotFoundException(NotFoundAlertException ex, NativeWebRequest request) {
        Map<String, Object> body = new LinkedHashMap<>();

        body.put("status", false);

        body.put("statusCode", 404);

        body.put("message", ex.getProblemDetailWithCause().getProperties().get("message"));

        body.put("data", null);

        body.put("path", request.getNativeRequest(HttpServletRequest.class).getRequestURI());

        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(body);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<Object> handleGlobalException(Exception ex, NativeWebRequest request) {
        Map<String, Object> body = new LinkedHashMap<>();

        body.put("status", false);

        body.put("statusCode", 500);

        body.put("message", "Internal server error");

        body.put("data", null);

        body.put("path", request.getNativeRequest(HttpServletRequest.class).getRequestURI());

        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(body);
    }

    @ExceptionHandler(HttpMessageConversionException.class)
    public ResponseEntity<Object> handleHttpMessageConversionException(HttpMessageConversionException ex, NativeWebRequest request) {
        Map<String, Object> body = new LinkedHashMap<>();

        body.put("status", false);

        body.put("statusCode", 400);

        body.put("message", "Invalid request payload");

        List<Map<String, String>> errors = new ArrayList<>();

        Map<String, String> error = new HashMap<>();

        error.put("field", "requestBody");

        error.put("message", "Malformed or invalid JSON request");

        errors.add(error);

        body.put("errors", errors);

        body.put("data", null);

        body.put("path", request.getNativeRequest(HttpServletRequest.class).getRequestURI());

        return ResponseEntity.badRequest().body(body);
    }

    @Override
    protected ResponseEntity<Object> handleMethodArgumentNotValid(
        MethodArgumentNotValidException ex,
        HttpHeaders headers,
        HttpStatusCode status,
        WebRequest request
    ) {
        Map<String, Object> body = new LinkedHashMap<>();

        body.put("status", false);

        body.put("statusCode", 400);

        body.put("message", "Invalid request parameters");

        body.put("errors", getFieldErrors(ex));

        body.put("data", null);

        body.put("path", ((ServletWebRequest) request).getRequest().getRequestURI());

        return ResponseEntity.badRequest().body(body);
    }

    @Nullable
    @Override
    protected ResponseEntity<Object> handleExceptionInternal(
        Exception ex,
        @Nullable Object body,
        HttpHeaders headers,
        HttpStatusCode statusCode,
        WebRequest request
    ) {
        body = body == null ? wrapAndCustomizeProblem((Throwable) ex, (NativeWebRequest) request) : body;
        return super.handleExceptionInternal(ex, body, headers, statusCode, request);
    }

    protected ProblemDetailWithCause wrapAndCustomizeProblem(Throwable ex, NativeWebRequest request) {
        return customizeProblem(getProblemDetailWithCause(ex), ex, request);
    }

    private ProblemDetailWithCause getProblemDetailWithCause(Throwable ex) {
        if (
            ex instanceof com.gearx7.app.service.UsernameAlreadyUsedException
        ) return (ProblemDetailWithCause) new LoginAlreadyUsedException().getBody();
        if (ex instanceof com.gearx7.app.service.EmailAlreadyUsedException) return (ProblemDetailWithCause) new EmailAlreadyUsedException()
            .getBody();
        if (ex instanceof com.gearx7.app.service.InvalidPasswordException) return (ProblemDetailWithCause) new InvalidPasswordException()
            .getBody();

        if (
            ex instanceof ErrorResponseException exp && exp.getBody() instanceof ProblemDetailWithCause problemDetailWithCause
        ) return problemDetailWithCause;
        return ProblemDetailWithCauseBuilder.instance().withStatus(toStatus(ex).value()).build();
    }

    protected ProblemDetailWithCause customizeProblem(ProblemDetailWithCause problem, Throwable err, NativeWebRequest request) {
        if (problem.getStatus() <= 0) problem.setStatus(toStatus(err));

        if (problem.getType() == null || problem.getType().equals(URI.create("about:blank"))) problem.setType(getMappedType(err));

        // higher precedence to Custom/ResponseStatus types
        String title = extractTitle(err, problem.getStatus());
        String problemTitle = problem.getTitle();
        if (problemTitle == null || !problemTitle.equals(title)) {
            problem.setTitle(title);
        }

        if (problem.getDetail() == null) {
            // higher precedence to cause
            problem.setDetail(getCustomizedErrorDetails(err));
        }

        Map<String, Object> problemProperties = problem.getProperties();
        if (problemProperties == null || !problemProperties.containsKey(MESSAGE_KEY)) problem.setProperty(
            MESSAGE_KEY,
            getMappedMessageKey(err) != null ? getMappedMessageKey(err) : "error.http." + problem.getStatus()
        );

        if (problemProperties == null || !problemProperties.containsKey(PATH_KEY)) problem.setProperty(PATH_KEY, getPathValue(request));

        if (
            (err instanceof MethodArgumentNotValidException fieldException) &&
            (problemProperties == null || !problemProperties.containsKey(FIELD_ERRORS_KEY))
        ) problem.setProperty(FIELD_ERRORS_KEY, getFieldErrors(fieldException));

        problem.setCause(buildCause(err.getCause(), request).orElse(null));

        return problem;
    }

    private String extractTitle(Throwable err, int statusCode) {
        return getCustomizedTitle(err) != null ? getCustomizedTitle(err) : extractTitleForResponseStatus(err, statusCode);
    }

    private List<FieldErrorVM> getFieldErrors(MethodArgumentNotValidException ex) {
        return ex
            .getBindingResult()
            .getFieldErrors()
            .stream()
            .map(f ->
                new FieldErrorVM(
                    f.getObjectName().replaceFirst("DTO$", ""),
                    f.getField(),
                    StringUtils.isNotBlank(f.getDefaultMessage()) ? f.getDefaultMessage() : f.getCode()
                )
            )
            .toList();
    }

    private String extractTitleForResponseStatus(Throwable err, int statusCode) {
        ResponseStatus specialStatus = extractResponseStatus(err);
        return specialStatus == null ? HttpStatus.valueOf(statusCode).getReasonPhrase() : specialStatus.reason();
    }

    private String extractURI(NativeWebRequest request) {
        HttpServletRequest nativeRequest = request.getNativeRequest(HttpServletRequest.class);
        return nativeRequest != null ? nativeRequest.getRequestURI() : StringUtils.EMPTY;
    }

    private HttpStatus toStatus(final Throwable throwable) {
        // Let the ErrorResponse take this responsibility
        if (throwable instanceof ErrorResponse err) return HttpStatus.valueOf(err.getBody().getStatus());

        return Optional
            .ofNullable(getMappedStatus(throwable))
            .orElse(
                Optional.ofNullable(resolveResponseStatus(throwable)).map(ResponseStatus::value).orElse(HttpStatus.INTERNAL_SERVER_ERROR)
            );
    }

    private ResponseStatus extractResponseStatus(final Throwable throwable) {
        return Optional.ofNullable(resolveResponseStatus(throwable)).orElse(null);
    }

    private ResponseStatus resolveResponseStatus(final Throwable type) {
        final ResponseStatus candidate = findMergedAnnotation(type.getClass(), ResponseStatus.class);
        return candidate == null && type.getCause() != null ? resolveResponseStatus(type.getCause()) : candidate;
    }

    private URI getMappedType(Throwable err) {
        if (err instanceof MethodArgumentNotValidException) return ErrorConstants.CONSTRAINT_VIOLATION_TYPE;
        return ErrorConstants.DEFAULT_TYPE;
    }

    private String getMappedMessageKey(Throwable err) {
        if (err instanceof MethodArgumentNotValidException) {
            return ErrorConstants.ERR_VALIDATION;
        } else if (err instanceof ConcurrencyFailureException || err.getCause() instanceof ConcurrencyFailureException) {
            return ErrorConstants.ERR_CONCURRENCY_FAILURE;
        }
        return null;
    }

    private String getCustomizedTitle(Throwable err) {
        if (err instanceof MethodArgumentNotValidException) return "Method argument not valid";
        return null;
    }

    private String getCustomizedErrorDetails(Throwable err) {
        Collection<String> activeProfiles = Arrays.asList(env.getActiveProfiles());
        if (activeProfiles.contains(JHipsterConstants.SPRING_PROFILE_PRODUCTION)) {
            if (err instanceof HttpMessageConversionException) return "Unable to convert http message";
            if (err instanceof DataAccessException) return "Failure during data access";
            if (containsPackageName(err.getMessage())) return "Unexpected runtime exception";
        }
        return err.getCause() != null ? err.getCause().getMessage() : err.getMessage();
    }

    private HttpStatus getMappedStatus(Throwable err) {
        // Where we disagree with Spring defaults
        if (err instanceof AccessDeniedException) return HttpStatus.FORBIDDEN;
        if (err instanceof ConcurrencyFailureException) return HttpStatus.CONFLICT;
        if (err instanceof BadCredentialsException) return HttpStatus.UNAUTHORIZED;
        return null;
    }

    private URI getPathValue(NativeWebRequest request) {
        if (request == null) return URI.create("about:blank");
        return URI.create(extractURI(request));
    }

    private HttpHeaders buildHeaders(Throwable err) {
        return err instanceof BadRequestAlertException badRequestAlertException
            ? HeaderUtil.createFailureAlert(
                applicationName,
                true,
                badRequestAlertException.getEntityName(),
                badRequestAlertException.getErrorKey(),
                badRequestAlertException.getMessage()
            )
            : null;
    }

    public Optional<ProblemDetailWithCause> buildCause(final Throwable throwable, NativeWebRequest request) {
        if (throwable != null && isCasualChainEnabled()) {
            return Optional.of(customizeProblem(getProblemDetailWithCause(throwable), throwable, request));
        }
        return Optional.ofNullable(null);
    }

    private boolean isCasualChainEnabled() {
        // Customize as per the needs
        return CASUAL_CHAIN_ENABLED;
    }

    private boolean containsPackageName(String message) {
        // This list is for sure not complete
        return StringUtils.containsAny(message, "org.", "java.", "net.", "jakarta.", "javax.", "com.", "io.", "de.", "com.gearx7.app");
    }
}
