package life.majiang.community.dto;

import lombok.Data;

/**
 * @author CZS
 * @create 2022-07-11 22:19
 * <p>
 * 当参数超过2个时，最好不要直接放在方法形参上，而是把多个参数封装成 一个对象 再去操作
 **/

@Data
public class AccessTokenDTO {
    private String client_id;
    private String client_secret;
    private String code;
    private String redirect_uri;
//    private String state;//视频教程中GitHub登录功能官方文档 规定的参数中有state，但现在官方文档中没有了
}
