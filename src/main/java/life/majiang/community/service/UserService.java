package life.majiang.community.service;

import life.majiang.community.domain.User;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

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

    //退出登录功能
    String logout(HttpServletRequest request, HttpServletResponse response);

    //个人中心功能
    String profile(HttpServletRequest request, Model model, String action, Integer page, Integer size);
}
