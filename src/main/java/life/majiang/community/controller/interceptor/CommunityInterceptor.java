package life.majiang.community.controller.interceptor;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import life.majiang.community.dao.UserDao;
import life.majiang.community.domain.User;
import life.majiang.community.service.NotificationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * @author CZS
 * @create 2022-07-18 15:47
 *
 * 用于所有页面持久化登录状态的拦截器
 *
 **/

//拦截器步骤 1.定义拦截器类，声明为bean(用@Component)，实现HandlerInterceptor接口，同时配置类中要包扫描加载这个bean
@Component//注意当前类必须受Spring容器控制
public class CommunityInterceptor implements HandlerInterceptor {
    @Autowired
    private UserDao userDao;

    @Autowired
    private NotificationService notificationService;

    //在原始被拦截的操作 之前运行
    //持久化登录状态
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        //*****************这种方法只适合小用户量，可以用Redis等方式优化*****************

        //首次登录时，客户端(用户浏览器) 会接收到 服务端(码匠社区)response返回过来的cookie，其中有服务端生成的 用户token
        //持久化登录状态
        // ---> 1.首次登录之后，会发送 首次登录 接收到的cookie(其中有 用户token)
        //  ---> 2.服务端(码匠社区) 接收到 客户端(用户浏览器) 发送的cookie，并获取cookie的所有信息
        //   ---> 3.如果找到token，就拿到token的值
        //    ---> 4.根据token的值查询数据库，获取对应的user
        //     ---> 5.如果user不为空，则写入session，让index.html根据session的内容显示登录状态，实现持久化登录状态
        Cookie[] cookies = request.getCookies();    //第2步
        if (cookies != null && cookies.length > 0){ //判断cookie有没有内容
            for (Cookie cookie : cookies) {
                if (cookie.getName().equals("token")){
                    String token = cookie.getValue();   //第3步

                    LambdaQueryWrapper<User> lqw = new LambdaQueryWrapper<>();  //第4步
                    lqw.eq(User::getToken,token);//查询条件：where token = ?
                    User userByToken = userDao.selectOne(lqw);//用MyBatis-Plus自带的按条件查询

                    if (userByToken != null){
                        request.getSession().setAttribute("user",userByToken);  //第5步

                        //****************** 持久化登录之后，所有页面展示 通知数 ******************
                        request.getSession().setAttribute("unreadCount",notificationService.unreadCount(userByToken.getAccountId()));
                    }
                    break;
                }
            }
        }

        return true;
    }

    //在原始被拦截的操作 之后运行
    @Override
    public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler, ModelAndView modelAndView) throws Exception {

    }

    //在原始被拦截的操作 之后(在postHandle之后)运行
    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) throws Exception {

    }
}
