package life.majiang.community.provider;

import com.alibaba.fastjson.JSON;
import life.majiang.community.dto.AccessTokenDTO;
import life.majiang.community.dto.GitHubUser;
import okhttp3.*;
import org.springframework.stereotype.Component;


import java.io.IOException;

/**
 * @author CZS
 * @create 2022-07-11 22:15
 **/

@Component//作为 组件 放到spring的IoC容器中统一管理
public class GitHubProvider {

    //根据OkHttp的POST请求示例，改成自己的方法
    //模拟POST请求
    public String getAccessToken(AccessTokenDTO accessTokenDTO){

        //原本是public static final，由于是封装成方法，所以定义成局部变量即可
        MediaType mediaType = MediaType.get("application/json; charset=utf-8");

        OkHttpClient client = new OkHttpClient();

        //第一个参数json换成 JSON.toJSONString(accessTokenDTO)，也就是将accessTokenDTO转换成json对象放进去
        RequestBody body = RequestBody.create(JSON.toJSONString(accessTokenDTO), mediaType);
        Request request = new Request.Builder()
                .url("https://github.com/login/oauth/access_token")
                .post(body)
                .build();

        try (Response response = client.newCall(request).execute()) {
            //经测试，这里返回的string就是
            // access_token=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&scope=user&token_type=bearer
            //其中就有 真正的access_token
            String string = response.body().string();

            //获取真正的access_token: 截取出其中的access_token，▲▲▲▲▲▲截取方法待优化▲▲▲▲▲▲
            String token = string.split("&")[0].split("=")[1];

            return token;//没问题则返回真正的access_token
        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }

    ////根据OkHttp的GET请求示例，改成自己的方法
    //模拟GET请求
    public GitHubUser getUser(String accessToken){
        OkHttpClient client = new OkHttpClient();

        //现在GitHub推荐新的方式
        // 将access_token通过作为Authorization HTTP header中的参数传输，而不是作为url中的参数明文传输
        Request request = new Request.Builder()
                //▲▲▲▲▲▲新的方式▲▲▲▲▲▲
                .url("https://api.github.com/user")
                .header("Authorization","token " + accessToken)
                //旧的方式
//                .url("https://api.github.com/user?access_token=" + accessToken)
                .build();

        try {
            Response response = client.newCall(request).execute();
            String string = response.body().string();

            //用fastjson解析，将 string这个json对象 转换成 GitHubUser类对象
            GitHubUser gitHubUser = JSON.parseObject(string, GitHubUser.class);

            return gitHubUser;//没问题则返回真正的user信息
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;//有问题就返回null
    }
}
