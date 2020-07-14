package online.sl.util;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;

@Data
public class Reply {

    private State state;

    @JsonInclude(JsonInclude.Include.NON_NULL)
    private Object data;

    public Reply(int code, String message) {
        state = new State();
        state.stateCode = code;
        state.stateMessage = message;
    }

    @Data
    private static class State {

        private int stateCode;

        private String stateMessage;

    }

    public Reply message(String message) {
        state.stateMessage = message;
        return this;
    }

    public Reply data(Object data) {
        this.data = data;
        return this;
    }

    public static Reply success() {
        return new Reply(0,"成功");
    }

    public static Reply fail() {
        return new Reply(1,"失败");
    }

}
