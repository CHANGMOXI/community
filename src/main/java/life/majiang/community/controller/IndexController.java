package life.majiang.community.controller;

import life.majiang.community.dao.UserDao;
import life.majiang.community.domain.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;

/**
 * @author CZS
 * @create 2022-07-10 17:19
 *
 * 主页controller
 *
 **/

@Controller
//在这里不能使用@RestController，它相当于@Controller和@ResponseBody两个注解的结合
// 这样返回json数据不需要在方法前面加@ResponseBody注解了，但不能返回html和jsp页面
//  因为相当于这些方法用来传json数据而不是页面，并且这时候视图解析器无法解析jsp,html页面
public class IndexController {
    @Autowired
    private UserDao userDao;

    @GetMapping("/")//匹配根目录
    public String index(HttpServletRequest request){

        //*****************这种方法只适合小用户量，可以用Redis等方式优化*****************

        //首次登录时，客户端(用户浏览器) 会接收到 服务端(码匠社区)response返回过来的cookie，其中有服务端生成的 用户token
        //持久化登录状态
        // ---> 1.首次登录之后，客户端(用户浏览器)以不同窗口(标签页)再次访问首页时，会发送 首次登录 接收到的cookie(其中有 用户token)
        //  ---> 2.服务端(码匠社区) 接收到 客户端(用户浏览器) 再次发送的cookie，并获取cookie的所有信息
        //   ---> 3.如果找到token，就拿到token的值
        //    ---> 4.根据token的值查询数据库，获取对应的user
        //     ---> 5.如果user不为空，则写入session，让index.html根据session的内容显示登录状态，实现持久化登录状态
        Cookie[] cookies = request.getCookies();    //第2步
        for (Cookie cookie : cookies) {
            if (cookie.getName().equals("token")){
                String token = cookie.getValue();   //第3步
                User userByToken = userDao.findByToken(token);  //第4步

                if (userByToken != null){   //第5步
                    request.getSession().setAttribute("user",userByToken);
                }
                break;
            }
        }

        return "index";
    }
}
