package life.majiang.community.service;

import org.springframework.ui.Model;

import javax.servlet.http.HttpServletRequest;

/**
 * @author CZS
 * @create 2022-07-14 12:30
 *
 * 业务层接口：首页功能
 *
 **/
public interface IndexService {

    //首页功能：持久化登录状态、首页问题列表、分页功能
    String index(HttpServletRequest request, Model model, Integer page, Integer size);
}
