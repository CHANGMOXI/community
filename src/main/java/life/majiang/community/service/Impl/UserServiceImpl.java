package life.majiang.community.service.Impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import life.majiang.community.dao.UserDao;
import life.majiang.community.domain.User;
import life.majiang.community.dto.AccessTokenDTO;
import life.majiang.community.dto.GitHubUser;
import life.majiang.community.provider.GitHubProvider;
import life.majiang.community.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
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
            //GitHub返回的用户信息存在，登录成功

            //根据 gitHubUser中的用户id 查询用户信息 是否已经存在于 数据库的表user
            LambdaQueryWrapper<User> lqw = new LambdaQueryWrapper<>();
            lqw.eq(User::getAccountId,gitHubUser.getId());//查询条件：where account_id = ?
            User userByAccountId = userDao.selectOne(lqw);//用MyBatis-Plus自带的按条件查询

            //新的token，用来 更新已存在的用户token 或 作为新用户的token
            String token = UUID.randomUUID().toString();//生成一个token，写cookie时要用到token

            if (userByAccountId != null){
                //用户信息已存在，只更新字段name、token、gmt_modified、bio、avatar_url
                userByAccountId.setName(gitHubUser.getName());
                userByAccountId.setToken(token);//把上面生成的新token也放进去
                userByAccountId.setGmtModified(System.currentTimeMillis());
                userByAccountId.setBio(gitHubUser.getBio());
                userByAccountId.setAvatarUrl(gitHubUser.getAvatarUrl());//更新用户头像url地址

                userDao.updateById(userByAccountId);//更新对应的用户信息(用MyBatis-Plus自带的)
            }else {
                //用户信息不存在，将新的用户信息写入数据库
                userByAccountId = new User();
                userByAccountId.setName(gitHubUser.getName());
                userByAccountId.setAccountId(gitHubUser.getId());
                userByAccountId.setToken(token);//把上面生成的新token也放进去
                userByAccountId.setGmtCreate(System.currentTimeMillis());
                userByAccountId.setGmtModified(userByAccountId.getGmtCreate());//新用户gmtModified与gmtCreate一致
                userByAccountId.setBio(gitHubUser.getBio());
                userByAccountId.setAvatarUrl(gitHubUser.getAvatarUrl());//设置用户头像url地址

                userDao.insert(userByAccountId);//新用户信息存入数据库(用MyBatis-Plus自带的)
            }


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
        //浏览器第一次访问首页时，需要重新登录，这时候登录后通过这种重定向方式会让页面url多了一串jsessionid
        //比如：http://localhost:8887/;jsessionid=6E789A291143576A39A230E1C462F512
        //导致 无法显示问题列表，因为这个地址并不是 根目录http://localhost:8887/
        //▲▲▲目前解决方案▲▲▲ ---> application.yml配置server.servlet.session.tracking-modes: cookie
        //                                         server.servlet.session.cookie.http-only: true
    }

}

