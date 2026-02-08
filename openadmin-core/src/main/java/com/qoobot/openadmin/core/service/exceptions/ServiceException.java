package com.qoobot.openadmin.core.service.exceptions;

/**
 * Generic checked exception for service layer errors.
 */
public class ServiceException extends Exception {
    public ServiceException() { super(); }
    public ServiceException(String message) { super(message); }
    public ServiceException(String message, Throwable cause) { super(message, cause); }
}

