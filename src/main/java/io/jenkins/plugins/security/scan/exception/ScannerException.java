package io.jenkins.plugins.security.scan.exception;

public class ScannerException extends Exception {
    private static final long serialVersionUID = 3172941819259598261L;

    public ScannerException() {
        super();
    }

    public ScannerException(String message) {
        super(message);
    }

    public ScannerException(String message, Throwable cause) {
        super(message, cause);
    }
}
