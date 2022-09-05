package life.majiang.community.exception;

/**
 * @author CZS
 * @create 2022-07-19 16:49
 * <p>
 * 以后不同的业务service都有自己的异常，一般不能把所有业务异常定义在一起(否则这个类非常庞大)
 * 定义一个状态码的接口，只需实现这个接口，不同业务可以自定义不同的异常，方便管理
 **/
public interface StatusCode {
    Integer getCode();

    String getMessage();
}
