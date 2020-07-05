package ua.com.periodicals.exception;

public class EntityEngagedException extends RuntimeException {
    public EntityEngagedException() {
        super();
    }

    public EntityEngagedException(String message) {
        super(message);
    }

    public EntityEngagedException(String message, Throwable cause) {
        super(message, cause);
    }

    public EntityEngagedException(Throwable cause) {
        super(cause);
    }
}
