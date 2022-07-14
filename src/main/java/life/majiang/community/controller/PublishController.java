package life.majiang.community.controller;

import life.majiang.community.dao.QuestionDao;
import life.majiang.community.dao.UserDao;
import life.majiang.community.domain.Question;
import life.majiang.community.domain.User;
import life.majiang.community.service.QuestionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.Cookie;
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
        return "publish";//跳转到publish页面
    }

    @PostMapping
    public String doPublish(@RequestParam("title") String title,
                            @RequestParam("description") String description,
                            @RequestParam("tag") String tag, HttpServletRequest request, Model model){

        return questionService.doPublish(title,description,tag,request,model);

    }
}
