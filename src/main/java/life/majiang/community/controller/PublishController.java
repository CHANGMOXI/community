package life.majiang.community.controller;

import life.majiang.community.service.QuestionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;

/**
 * @author CZS
 * @create 2022-07-13 11:34
 *
 * 发布问题页面controller
 *
 **/

@Controller
@RequestMapping("/publish")
public class PublishController {

    @Autowired
    private QuestionService questionService;

    @GetMapping
    public String publish(){
        //所有页面持久化登录状态 交给 拦截器

        return "publish";//跳转到publish页面
    }

    @PostMapping
    public String doPublish(@RequestParam(value = "title",required = false) String title,
                            @RequestParam(value = "description",required = false) String description,
                            @RequestParam(value = "tag",required = false) String tag,
                            @RequestParam(value = "id",required = false) Integer id,
                            HttpServletRequest request, Model model){

        return questionService.doPublish(title,description,tag,id,request,model);

    }

    @GetMapping("/{id}")
    public String edit(@PathVariable(name = "id") Integer id, Model model){

        return questionService.edit(id,model);
    }

}
