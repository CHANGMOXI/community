package life.majiang.community.exception;

/**
 * @author CZS
 * @create 2022-07-19 17:07
 *
 * 实现ErrorCode接口，自定义业务异常
 *
 **/

public enum CustomizeErrorCode implements ErrorCode{
    //自定义业务异常，这样不用每次处理异常时都要写一遍异常信息
    QUESTION_NOT_FOUND("该问题不存在或已被删除");

    private String msg;

    CustomizeErrorCode(String msg) {
        this.msg = msg;
    }

    //实现ErrorCode接口的方法
    @Override
    public String getMessage() {
        return msg;
    }
}
