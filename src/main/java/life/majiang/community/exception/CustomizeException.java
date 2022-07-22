package life.majiang.community.exception;

/**
 * @author CZS
 * @create 2022-06-28 16:25
 *
 * 自定义异常
 *
 **/
public class CustomizeException extends RuntimeException{
    private Integer code;
    private String message;

    public CustomizeException(StatusCode statusCode) {
        this.code = statusCode.getCode();
        this.message = statusCode.getMessage();
    }

    public Integer getCode() {
        return code;
    }

    @Override
    public String getMessage() {
        return message;
    }
}
