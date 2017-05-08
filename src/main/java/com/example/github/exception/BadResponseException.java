package com.example.github.exception;

/**
 * This exception represents an error in the request
 * 
 *
 */
public class BadResponseException extends Exception {

    /** The Constant serialVersionUID. */
    private static final long serialVersionUID = 202655880961347528L;

    /**
     * Instantiates a new failed response exception.
     * 
     * @param message the message
     * @param cause the cause
     */
    public BadResponseException(final String message, final Throwable cause) {
        super(message, cause);
    }

    /**
     * Instantiates a new failed respose exception.
     * 
     * @param message the message
     * @param cause the cause
     */
    public BadResponseException(final String message) {
        super(message);
    }

}
