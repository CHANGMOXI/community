package life.majiang.community.controller;

import life.majiang.community.service.IndexService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

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
    private IndexService indexService;

    @GetMapping("/")//匹配根目录
    public String index(HttpServletRequest request, Model model,
                        @RequestParam(name = "page",defaultValue = "1") Integer page,
                        @RequestParam(name = "size",defaultValue = "5") Integer size){

        return indexService.index(request,model,page,size);
    }
}
