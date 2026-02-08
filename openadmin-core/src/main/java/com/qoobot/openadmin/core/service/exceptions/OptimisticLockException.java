package com.qoobot.openadmin.core.service.exceptions;

/**
 * Thrown when optimistic lock conflict detected.
 */
public class OptimisticLockException extends ServiceException {
    public OptimisticLockException() { super(); }
    public OptimisticLockException(String message) { super(message); }
}

