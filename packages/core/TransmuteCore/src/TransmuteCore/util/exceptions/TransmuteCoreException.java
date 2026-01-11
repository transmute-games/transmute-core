package TransmuteCore.System.Exceptions;

/**
 * Base exception class for all TransmuteCore-related exceptions.
 * This provides a common exception hierarchy for the engine.
 */
public class TransmuteCoreException extends RuntimeException {
    
    private final String errorCode;
    
    /**
     * Creates a new TransmuteCoreException with a message.
     *
     * @param message The error message describing what went wrong.
     */
    public TransmuteCoreException(String message) {
        super(message);
        this.errorCode = generateErrorCode();
    }
    
    /**
     * Creates a new TransmuteCoreException with a message and cause.
     *
     * @param message The error message describing what went wrong.
     * @param cause   The underlying cause of this exception.
     */
    public TransmuteCoreException(String message, Throwable cause) {
        super(message, cause);
        this.errorCode = generateErrorCode();
    }
    
    /**
     * Creates a new TransmuteCoreException with a message and custom error code.
     *
     * @param message   The error message describing what went wrong.
     * @param errorCode A custom error code for tracking this error.
     */
    public TransmuteCoreException(String message, String errorCode) {
        super(message);
        this.errorCode = errorCode;
    }
    
    /**
     * Creates a new TransmuteCoreException with a message, cause, and custom error code.
     *
     * @param message   The error message describing what went wrong.
     * @param cause     The underlying cause of this exception.
     * @param errorCode A custom error code for tracking this error.
     */
    public TransmuteCoreException(String message, Throwable cause, String errorCode) {
        super(message, cause);
        this.errorCode = errorCode;
    }
    
    /**
     * @return The error code associated with this exception.
     */
    public String getErrorCode() {
        return errorCode;
    }
    
    /**
     * Generates a random error code for tracking purposes.
     *
     * @return A 16-character error code.
     */
    private static String generateErrorCode() {
        String chars = "ABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";
        StringBuilder code = new StringBuilder();
        for (int i = 0; i < 16; i++) {
            code.append(chars.charAt((int) (Math.random() * chars.length())));
        }
        return code.toString();
    }
    
    @Override
    public String getMessage() {
        return "[TransmuteCore Error " + errorCode + "] " + super.getMessage();
    }
}
