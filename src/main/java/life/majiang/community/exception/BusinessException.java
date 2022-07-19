package life.majiang.community.exception;

/**
 * @author CZS
 * @create 2022-06-28 16:25
 *
 * 业务异常
 *
 **/
public class BusinessException extends RuntimeException{

    public BusinessException(ErrorCode errorCode) {
        super(errorCode.getMessage());
    }

}
