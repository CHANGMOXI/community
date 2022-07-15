package life.majiang.community.service.Impl;

import life.majiang.community.dao.UserDao;
import life.majiang.community.domain.User;
import life.majiang.community.dto.AccessTokenDTO;
import life.majiang.community.dto.GitHubUser;
import life.majiang.community.provider.GitHubProvider;
import life.majiang.community.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.UUID;

/**
 * @author CZS
 * @create 2022-07-13 22:59
 *
 * 登录功能业务
 *
 **/

@Service//业务层的组件，等价于@Component
public class UserServiceImpl implements UserService {

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
    private UserDao userDao;

    //GitHub登录功能：完成登录并显示登录状态
    @Override
    public String loginByGitHub(String code, HttpServletRequest request, HttpServletResponse response) {
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
        if (gitHubUser != null && gitHubUser.getId() != null){
            //用户信息存在，登录成功

            //用户信息写入数据库
            User user = new User();
            user.setName(gitHubUser.getName());

            String token = UUID.randomUUID().toString();//生成一个token，写cookie时要用到token
            user.setToken(token);//把生成的token也放到user对象，存入数据库

            user.setAccountId(String.valueOf(gitHubUser.getId()));//getId()返回Long类型，转成String类型放进去
            user.setGmtCreate(System.currentTimeMillis());
            user.setGmtModified(user.getGmtCreate());//初始化时，gmtModified与gmtCreate一致
            user.setAvatarUrl(gitHubUser.getAvatarUrl());//设置用户头像url地址

            userDao.insert(user);//用户信息存入数据库

            //登录成功，写 session 和 cookie
            //1.这里 写session ---> 为了在首次登录时，把user信息写入session
            //                   ---> 这样首页index.html能根据user信息是否为空，在首次登录成功后显示 用户名
            //如果这里不把user写入session中，即使首次登录成功，也没有user的用户名信息，无法显示用户名，后续持久化更新状态也无法显示用户名
            request.getSession().setAttribute("user",gitHubUser);

            //2.写cookie ---> 把生成的token放到cookie中，response返回给客户端(用户浏览器)
            //            ---> 客户端(用户浏览器)后续以不同窗口(标签页)访问首页时会发送相同的cookie回来
            //             ---> 服务端(码匠社区)获取cookie并根据里面的token来验证，实现持久化登录状态
            //              ---> 在IndexController.javawa中完成
            response.addCookie(new Cookie("token",token));
        }

        //登录成功或失败，都跳转回(重定向)到首页index页面
        return "redirect:/";
    }

//    //GitHub登录功能：持久化登录状态
//    @Override
//    public String persistLogin(HttpServletRequest request) {
//
//        //*****************这种方法只适合小用户量，可以用Redis等方式优化*****************
//
//        //首次登录时，客户端(用户浏览器) 会接收到 服务端(码匠社区)response返回过来的cookie，其中有服务端生成的 用户token
//        //持久化登录状态
//        // ---> 1.首次登录之后，客户端(用户浏览器)以不同窗口(标签页)再次访问首页时，会发送 首次登录 接收到的cookie(其中有 用户token)
//        //  ---> 2.服务端(码匠社区) 接收到 客户端(用户浏览器) 再次发送的cookie，并获取cookie的所有信息
//        //   ---> 3.如果找到token，就拿到token的值
//        //    ---> 4.根据token的值查询数据库，获取对应的user
//        //     ---> 5.如果user不为空，则写入session，让index.html根据session的内容显示登录状态，实现持久化登录状态
//        Cookie[] cookies = request.getCookies();    //第2步
//        if (cookies != null && cookies.length > 0){ //判断cookie有没有内容
//            for (Cookie cookie : cookies) {
//                if (cookie.getName().equals("token")){
//                    String token = cookie.getValue();   //第3步
//                    User userByToken = userDao.selectByToken(token);  //第4步
//
//                    if (userByToken != null){
//                        request.getSession().setAttribute("user",userByToken);  //第5步
//                    }
//                    break;
//                }
//            }
//        }
//
//        return "index";
//    }

}

