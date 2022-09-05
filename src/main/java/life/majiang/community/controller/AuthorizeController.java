package life.majiang.community.controller;

import life.majiang.community.dto.AccessTokenDTO;
import life.majiang.community.dto.GitHubUser;
import life.majiang.community.provider.GitHubProvider;
import life.majiang.community.service.UserService;
import lombok.extern.slf4j.Slf4j;
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
 * <p>
 * GitHub登录功能的controller
 **/

@Controller
@Slf4j//追加日志
public class AuthorizeController {
    //将 注解里的配置文件属性值 赋值 给这些成员变量
    @Value("${github.client.id}")
    private String clientId;
    @Value("${github.client.secret}")
    private String clientSecret;
    @Value("${github.redirect.uri}")
    private String redirectUri;

    @Autowired
    private GitHubProvider gitHubProvider;

    @Autowired
    private UserService userService;

    @GetMapping("/callback")
    public String callback(@RequestParam(name = "code") String code,
                           HttpServletRequest request, HttpServletResponse response) {
        AccessTokenDTO accessTokenDTO = new AccessTokenDTO();

        //1.设置access_token的具体信息
        accessTokenDTO.setClient_id(clientId);//对应自己OAuth Apps中的Client ID
        accessTokenDTO.setClient_secret(clientSecret);//对应自己OAuth Apps中的Client secrets

        //发送给GitHub的模拟POST请求中的access_token携带了从请求参数中获取到的code，是为了从GitHub中获取真正的access_token
        accessTokenDTO.setCode(code);

        accessTokenDTO.setRedirect_uri(redirectUri);//GitHub后续返回access_token的地址
//        accessTokenDTO.setState(state);//现在官方文档中没有这个参数了

        //2.模拟POST请求，如果携带了code的access_token正确，则GitHub返回 真正的access_token，有问题则返回null
        String accessToken = gitHubProvider.getAccessToken(accessTokenDTO);//这里的形参accessTokenDTO是携带了code的access_token

        //3.模拟GET请求，请求中携带了access_token，如果正确，则GitHub返回 真正的user信息，有问题则返回null
        GitHubUser gitHubUser = gitHubProvider.getUser(accessToken);//这里的形参accessToken是获取到的 真正的access_token

        //4.验证用户信息
        if (gitHubUser != null && gitHubUser.getId() != null) {
            //GitHub返回的用户信息存在，登录成功

            //新的token，用来 更新已存在的用户token 或 作为新用户的token
            String token = UUID.randomUUID().toString();//生成一个token，写cookie时要用到token

            userService.createOrUpdate(gitHubUser, token);//新增或更新用户信息

            //登录成功，写 session 和 cookie
            //写cookie ---> 把生成的token放到cookie中，response返回给客户端(用户浏览器)
            //          ---> 客户端(用户浏览器)后续继续访问首页时会发送相同的cookie回来
            //           ---> 服务端(码匠社区)获取cookie并根据里面的token来验证，实现持久化登录状态
            //            ---> 在IndexController.javawa中完成
            response.addCookie(new Cookie("token", token));
        } else {
            //登录失败，追加日志
            log.error("callback userService.loginByGitHub get github error,{}", gitHubUser);

        }

        //登录成功或失败，都跳转回(重定向)到首页index页面
        return "redirect:/";
        //浏览器第一次访问首页时，需要重新登录，这时候登录后通过这种重定向方式会让页面url多了一串jsessionid
        //比如：http://localhost:8887/;jsessionid=6E789A291143576A39A230E1C462F512
        //导致 无法显示问题列表，因为这个地址并不是 根目录http://localhost:8887/
        //▲▲▲目前解决方案▲▲▲ ---> application.yml配置server.servlet.session.tracking-modes: cookie
        //                                         server.servlet.session.cookie.http-only: true
    }

    @GetMapping("/logout")
    public String logout(HttpServletRequest request, HttpServletResponse response) {
        //移除session中的user信息
        request.getSession().removeAttribute("user");
        //移除cookie中的token
        Cookie cookie = new Cookie("token", null);
        cookie.setMaxAge(0);
        response.addCookie(cookie);

        return "redirect:/";//退出后跳转回(重定向)首页
    }
}
