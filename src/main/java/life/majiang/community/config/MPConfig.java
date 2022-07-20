package life.majiang.community.config;

import com.baomidou.mybatisplus.extension.plugins.MybatisPlusInterceptor;
import com.baomidou.mybatisplus.extension.plugins.inner.OptimisticLockerInnerInterceptor;
import com.baomidou.mybatisplus.extension.plugins.inner.PaginationInnerInterceptor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @author CZS
 * @create 2022-07-15 21:40
 **/

//定义 MyBatis-Plus分页查询 需要的 分页拦截器、乐观锁拦截器
@Configuration//需要MyBatis-Plus的配置类
public class MPConfig{
    @Bean//用spring管理第三方bean的方式
    public MybatisPlusInterceptor mybatisPlusInterceptor(){
        //定义MyBatis-Plus拦截器
        MybatisPlusInterceptor mybatisPlusInterceptor = new MybatisPlusInterceptor();//拦截器的壳
        //添加分页拦截器
        mybatisPlusInterceptor.addInnerInterceptor(new PaginationInnerInterceptor());
//        //添加乐观锁拦截器
//        mybatisPlusInterceptor.addInnerInterceptor(new OptimisticLockerInnerInterceptor());
        return mybatisPlusInterceptor;
    }
}
