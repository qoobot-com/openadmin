package com.qoobot.openadmin.core.service.exceptions;

/**
 * Thrown when an entity is not found.
 */
public class NotFoundException extends ServiceException {
    public NotFoundException() { super(); }
    public NotFoundException(String message) { super(message); }
}

