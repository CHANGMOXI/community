package life.majiang.community.controller;

import life.majiang.community.dto.AccessTokenDTO;
import life.majiang.community.dto.GitHubUser;
import life.majiang.community.dao.UserDao;
import life.majiang.community.domain.User;
import life.majiang.community.provider.GitHubProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.UUID;

/**
 * @author CZS
 * @create 2022-07-11 20:53
 *
 * GitHub登录功能的controller
 *
 **/

@Controller
public class AuthorizeController {
    @Autowired
    private GitHubProvider gitHubProvider;

    //将 注解里的配置文件属性值 赋值 给这些成员变量
    @Value("${github.client.id}")
    private String clientId;
    @Value("${github.client.secret}")
    private String clientSecret;
    @Value("${github.redirect.uri}")
    private String redirectUri;

    @Autowired
    private UserDao userDao;

    @GetMapping("/callback")
    public String callback(@RequestParam(name = "code") String code,
                           @RequestParam(name = "state") String state,
                           HttpServletRequest request, HttpServletResponse response){

        AccessTokenDTO accessTokenDTO = new AccessTokenDTO();

        //设置access_token的具体信息
        accessTokenDTO.setClient_id(clientId);//对应自己OAuth Apps中的Client ID
        accessTokenDTO.setClient_secret(clientSecret);//对应自己OAuth Apps中的Client secrets

        //发送给GitHub的模拟POST请求中的access_token携带了从请求参数中获取到的code，是为了从GitHub中获取真正的access_token
        accessTokenDTO.setCode(code);

        accessTokenDTO.setRedirect_uri(redirectUri);//GitHub后续返回access_token的地址
//        accessTokenDTO.setState(state);//现在官方文档中没有这个参数了

        //模拟POST请求，如果携带了code的access_token正确，则GitHub返回 真正的access_token，有问题则返回null
        String accessToken = gitHubProvider.getAccessToken(accessTokenDTO);//这里的形参accessTokenDTO是携带了code的access_token

        //模拟GET请求，请求中携带了access_token，如果正确，则GitHub返回 真正的user信息，有问题则返回null
        GitHubUser gitHubUser = gitHubProvider.getUser(accessToken);//这里的形参accessToken是获取到的 真正的access_token

        if (gitHubUser != null){
            //登录成功

            //用户信息写入数据库
            User user = new User();
            user.setName(gitHubUser.getName());

            String token = UUID.randomUUID().toString();//生成一个token，写cookie时要用到token
            user.setToken(token);//把生成的token也放到user对象，存入数据库

            user.setAccountId(String.valueOf(gitHubUser.getId()));//getId()返回Long类型，转成String类型放进去
            user.setGmtCreate(System.currentTimeMillis());
            user.setGmtModified(user.getGmtCreate());//初始化时，gmtModified与gmtCreate一致
            userDao.insert(user);//user信息写入数据库

            //登录成功，写 session 和 cookie
            //1.这里的 写session ---> 为了在首次登录时，把user信息写入session
            //                 ---> 这样首页index.html能根据user信息是否为空，在首次登录后显示 用户名 或 登录按钮
            //如果这里不把user写入session中，即使首次登录成功，也没有user的用户名信息，无法显示用户名，后续持久化更新状态也无法显示用户名
            request.getSession().setAttribute("user",gitHubUser);

            //2.写cookie ---> 把生成的token放到cookie中，response返回给客户端(用户浏览器)
            //            ---> 客户端(用户浏览器)后续以不同窗口(标签页)访问首页时会发送相同的cookie回来
            //             ---> 服务端(码匠社区)获取cookie并根据里面的token来验证，实现持久化登录状态
            //              ---> 在IndexController.javawa中完成
            response.addCookie(new Cookie("token",token));

            return "redirect:/";//跳转回(重定向)到首页index页面
        }else {
            //登录失败，重新登录
            return "redirect:/";//跳转回(重定向)到首页index页面
        }
    }
}
