package life.majiang.community.config;


import life.majiang.community.controller.interceptor.CommunityInterceptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
//@EnableWebMvc //@EnableWebMvc会导致Springboot自动配置的MVC被我们自定义的配置文件替代
//SpringBoot自动配置中将Static作为静态资源的访问根目录，而我们的MVC还没有配置静态资源的访问目录，所以导致静态资源失效

//实现WebMvcConfigurer接口可以简化开发，但具有一定的侵入性(该配置类就和Spring的API关联在一起了)
public class WebConfig implements WebMvcConfigurer {

    //在 WebConfig配置类 实现 WebMvcConfigurer接口 之后，拦截器步骤 3和4 就可以写在这里，无需 拦截器步骤2.创建SpringMvcSupport配置类
    //并且 包扫描中 不用加上"life.majiang.community.config"
    //拦截器步骤3.添加拦截器，用 自动装配
    @Autowired
    private CommunityInterceptor communityInterceptor;

    //拦截器步骤4.实现addInterceptors方法，添加拦截器 并 设定拦截的访问路径，路径可以通过 可变参数 设置多个
    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        //所有页面持久化登录状态
        registry.addInterceptor(communityInterceptor).addPathPatterns("/**");
        //如果有多个拦截器,也是同样的配置,拦截器执行顺序跟配置顺序一致,postHandle和afterCompletion方法的执行顺序相反
    }
}