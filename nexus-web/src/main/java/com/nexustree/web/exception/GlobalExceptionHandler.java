package com.nexustree.web.exception;

import com.nexustree.core.exception.CommitNotFoundException;
import com.nexustree.core.exception.NoChangesDetectedException;
import com.nexustree.core.exception.UnsupportedPatchOperationException;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.net.URI;
import java.time.Instant;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(NoChangesDetectedException.class)
    public ProblemDetail handleNoChangesDetected(NoChangesDetectedException ex) {
        ProblemDetail problemDetail = ProblemDetail.forStatusAndDetail(HttpStatus.UNPROCESSABLE_ENTITY, ex.getMessage());
        problemDetail.setTitle("Unprocessable Entity");
        problemDetail.setType(URI.create("https://nexustree.api/errors/no-changes"));
        problemDetail.setProperty("timestamp", Instant.now());
        return problemDetail;
    }

    @ExceptionHandler(CommitNotFoundException.class)
    public ProblemDetail handleCommitNotFound(CommitNotFoundException ex) {
        ProblemDetail problemDetail = ProblemDetail.forStatusAndDetail(HttpStatus.NOT_FOUND, ex.getMessage());
        problemDetail.setTitle("Commit Not Found");
        problemDetail.setType(URI.create("https://nexustree.api/errors/commit-not-found"));
        problemDetail.setProperty("timestamp", Instant.now());
        return problemDetail;
    }

    @ExceptionHandler(UnsupportedPatchOperationException.class)
    public ProblemDetail handleUnsupportedPatchOperation(UnsupportedPatchOperationException ex) {
        ProblemDetail problemDetail = ProblemDetail.forStatusAndDetail(HttpStatus.BAD_REQUEST, ex.getMessage());
        problemDetail.setTitle("Invalid Patch Operation");
        problemDetail.setType(URI.create("https://nexustree.api/errors/invalid-patch-operation"));
        problemDetail.setProperty("timestamp", Instant.now());
        return problemDetail;
    }

    @ExceptionHandler(DataIntegrityViolationException.class)
    public ProblemDetail handleDataIntegrityViolation(DataIntegrityViolationException ex) {
        ProblemDetail problemDetail = ProblemDetail.forStatusAndDetail(HttpStatus.CONFLICT, "Database constraint violation. The resource might already exist or contains invalid relations.");
        problemDetail.setTitle("Data Integrity Violation");
        problemDetail.setType(URI.create("https://nexustree.api/errors/data-integrity-violation"));
        problemDetail.setProperty("timestamp", Instant.now());
        return problemDetail;
    }

    @ExceptionHandler(Exception.class)
    public ProblemDetail handleGenericException(Exception ex) {
        ProblemDetail problemDetail = ProblemDetail.forStatusAndDetail(HttpStatus.INTERNAL_SERVER_ERROR, "An unexpected error occurred.");
        problemDetail.setTitle("Internal Server Error");
        problemDetail.setProperty("timestamp", Instant.now());
        problemDetail.setProperty("debug_message", ex.getMessage());
        return problemDetail;
    }
}
