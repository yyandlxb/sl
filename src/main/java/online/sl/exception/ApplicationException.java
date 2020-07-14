package online.sl.exception;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ApplicationException extends RuntimeException {

    private String displayableMessage;

    public ApplicationException(String displayableMessage) {
        this.displayableMessage = displayableMessage;
    }

    public ApplicationException(String message, String displayableMessage) {
        super(displayableMessage);
        this.displayableMessage = displayableMessage;
    }

    public ApplicationException(String message, Throwable cause, String displayableMessage) {
        super(message, cause);
        this.displayableMessage = displayableMessage;
    }

}
