package life.majiang.community.controller;

import life.majiang.community.dto.PaginationDTO;
import life.majiang.community.service.QuestionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

/**
 * @author CZS
 * @create 2022-07-10 17:19
 * <p>
 * 主页controller
 **/

@Controller
//在这里不能使用@RestController，它相当于@Controller和@ResponseBody两个注解的结合
// 这样返回json数据不需要在方法前面加@ResponseBody注解了，但不能返回html和jsp页面
//  因为相当于这些方法用来传json数据而不是页面，并且这时候视图解析器无法解析jsp,html页面
public class IndexController {

    @Autowired
    private QuestionService questionService;

    @GetMapping("/")//匹配根目录
    public String index(Model model,
                        @RequestParam(name = "page", defaultValue = "1") Integer page,
                        @RequestParam(name = "size", defaultValue = "5") Integer size,
                        @RequestParam(name = "search", required = false) String search) {
        //所有页面持久化登录状态 交给 拦截器

        //展示首页问题列表并分页，以及搜索功能
        PaginationDTO paginationDTO = questionService.list(search, page, size);//获取当前页所有问题记录(包括头像url地址)
        model.addAttribute("pagination", paginationDTO);//把当前页所有问题记录发送给首页，首页进行显示
        model.addAttribute("search", search);//把search内容传递回前端，用于分页

        return "index";
    }
}
