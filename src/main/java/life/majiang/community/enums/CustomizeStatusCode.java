package life.majiang.community.enums;

import life.majiang.community.exception.StatusCode;

/**
 * @author CZS
 * @create 2022-07-19 17:07
 * <p>
 * 实现StatusCode接口，自定义业务状态码
 **/

public enum CustomizeStatusCode implements StatusCode {
    //根据需要自定义业务异常，这样不用每次处理异常时都要写一遍异常信息
    SYSTEM_ERROR(2001, "服务器太热啦，请稍后再试"),

    NO_LOGIN(2002, "请登录后再进行评论"),

    QUESTION_NOT_FOUND(2003, "该问题不存在或已被删除"),
    COMMENT_NOT_FOUND(2004, "该评论不存在或已被删除"),

    QUESTION_OR_COMMENT_NOT_FOUND(2005, "该问题/评论不存在或已被删除"),
    TYPE_PARAM_WRONG(2006, "评论类型错误或不存在"),

    PARENT_QUESTION_NOT_FOUND(2007, "回复的问题不存在或已被删除"),
    PARNET_COMMENT_NOT_FOUND(2008, "回复的评论不存在或已被删除"),

    CONTENT_IS_EMPTY(2009, "回复内容不能为空"),

    READ_NOTIFICATION_FAIL(2010, "不允许读取别人的信息"),
    NOTIFICATION_NOT_FOUND(2011, "找不到该通知了..."),

    SUCCESS(200, "请求成功");

    private Integer code;
    private String msg;

    CustomizeStatusCode(Integer code, String msg) {
        this.code = code;
        this.msg = msg;
    }

    //实现StatusCode接口的方法
    @Override
    public Integer getCode() {
        return code;
    }

    //实现StatusCode接口的方法
    @Override
    public String getMessage() {
        return msg;
    }
}
