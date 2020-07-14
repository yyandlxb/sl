package online.sl.exception;

import online.sl.util.Reply;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;

@ControllerAdvice
public class ApplicationExceptionHandler {

    private final Logger logger = LoggerFactory.getLogger(getClass());

    @ExceptionHandler(ApplicationException.class)
    @ResponseBody
    public Reply handleException(ApplicationException exception) {
        logger.error(exception.getMessage(), exception);
        return Reply.fail().message(exception.getDisplayableMessage());
    }

}
