package com.objectstoragesystem.exception;



public class InternalServerException extends RuntimeException {
    private static final long serialVersionUID = 1L;
	
    public InternalServerException() {
        super();
    }

    public InternalServerException(final String message, final Throwable cause) {
        super(message, cause);
    }

    public InternalServerException(final String message) {
        super(message);
    }

    public InternalServerException(final Throwable cause) {
        super(cause);
    }
}
