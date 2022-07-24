package micro_service.utils;


/**
 * @author sy
 * @date 2022/7/21 20:22
 */
public class ResponseError {
    private String message;
    public ResponseError(String message, Object... args) {
        this.message = String.format(message, args);
    }

    public ResponseError(Exception e) {
        this.message = e.getMessage();
    }

    public String getMessage() {
        return this.message;
    }
}
