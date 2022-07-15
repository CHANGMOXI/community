package life.majiang.community.service.Impl;

import life.majiang.community.dao.QuestionDao;
import life.majiang.community.dao.UserDao;
import life.majiang.community.domain.Question;
import life.majiang.community.domain.User;
import life.majiang.community.dto.QuestionDTO;
import life.majiang.community.service.IndexService;
import life.majiang.community.service.QuestionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.ui.Model;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import java.util.List;

/**
 * @author CZS
 * @create 2022-07-14 12:30
 *
 * 首页功能业务
 *
 **/

@Service//业务层的组件，等价于@Component
public class IndexServiceImpl implements IndexService {
    @Autowired
    private UserDao userDao;

    @Autowired
    private QuestionService questionService;

    @Override
    public String index(HttpServletRequest request, Model model) {

        //********************************** 持久化登录状态 功能 **********************************

        //*****************这种方法只适合小用户量，可以用Redis等方式优化*****************

        //首次登录时，客户端(用户浏览器) 会接收到 服务端(码匠社区)response返回过来的cookie，其中有服务端生成的 用户token
        //持久化登录状态
        // ---> 1.首次登录之后，客户端(用户浏览器)以不同窗口(标签页)再次访问首页时，会发送 首次登录 接收到的cookie(其中有 用户token)
        //  ---> 2.服务端(码匠社区) 接收到 客户端(用户浏览器) 再次发送的cookie，并获取cookie的所有信息
        //   ---> 3.如果找到token，就拿到token的值
        //    ---> 4.根据token的值查询数据库，获取对应的user
        //     ---> 5.如果user不为空，则写入session，让index.html根据session的内容显示登录状态，实现持久化登录状态
        Cookie[] cookies = request.getCookies();    //第2步
        if (cookies != null && cookies.length > 0){ //判断cookie有没有内容
            for (Cookie cookie : cookies) {
                if (cookie.getName().equals("token")){
                    String token = cookie.getValue();   //第3步
                    User userByToken = userDao.selectByToken(token);  //第4步

                    if (userByToken != null){
                        request.getSession().setAttribute("user",userByToken);  //第5步
                    }
                    break;
                }
            }
        }
        //**************************************************************************************

        //*********************************** 首页问题列表 功能 ***********************************
        List<QuestionDTO> questionDTOList = questionService.list();//获取所有问题记录(包括头像url地址)
        model.addAttribute("questions",questionDTOList);//把所有问题记录发送给首页，首页进行显示

        //**************************************************************************************

        return "index";
    }
}
