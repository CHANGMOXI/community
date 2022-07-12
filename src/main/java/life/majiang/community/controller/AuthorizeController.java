package life.majiang.community.controller;

import life.majiang.community.dto.AccessTokenDTO;
import life.majiang.community.dto.GitHubUser;
import life.majiang.community.provider.GitHubProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

/**
 * @author CZS
 * @create 2022-07-11 20:53
 *
 * GitHub登录功能中 用于认证的controller
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

    @GetMapping("/callback")
    public String callback(@RequestParam(name = "code") String code,
                           @RequestParam(name = "state") String state){

        AccessTokenDTO accessTokenDTO = new AccessTokenDTO();

        //设置access_token的具体信息
//        accessTokenDTO.setClient_id("9706d72cdbecf1898ce0");
        accessTokenDTO.setClient_id(clientId);//对应自己OAuth Apps中的Client ID
//        accessTokenDTO.setClient_secret("5cbabe5f152ee88ff9d7a99df2d19471bc5de400");
        accessTokenDTO.setClient_secret(clientSecret);//对应自己OAuth Apps中的Client secrets

        //发送给GitHub的模拟POST请求中的access_token携带了从请求参数中获取到的code，是为了从GitHub中获取真正的access_token
        accessTokenDTO.setCode(code);

//        accessTokenDTO.setRedirect_uri("http://localhost:8887/callback");
        accessTokenDTO.setRedirect_uri(redirectUri);//GitHub后续返回access_token的地址
//        accessTokenDTO.setState(state);//现在官方文档中没有这个参数了

        //模拟POST请求，如果携带了code的access_token正确，则GitHub返回 真正的access_token，有问题则返回null
        String accessToken = gitHubProvider.getAccessToken(accessTokenDTO);//这里的形参accessTokenDTO是携带了code的access_token

        //模拟GET请求，请求中携带了access_token，如果正确，则GitHub返回 真正的user信息，有问题则返回null
        GitHubUser user = gitHubProvider.getUser(accessToken);//这里的形参accessToken是获取到的 真正的access_token

        System.out.println(user.getName());//测试是否成功获取到真正的user信息

        return "index";
    }
}
