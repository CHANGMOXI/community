package life.majiang.community.service;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * @author CZS
 * @create 2022-07-13 22:56
 *
 * 业务层接口：登录功能
 *
 **/
public interface UserService {

    //GitHub登录功能：完成登录并显示登录状态
    String loginByGitHub(String code, HttpServletRequest request, HttpServletResponse response);

    //GitHub登录功能：持久化登录状态
    String persistLogin(HttpServletRequest request);
}
