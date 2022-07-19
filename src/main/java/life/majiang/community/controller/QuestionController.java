package life.majiang.community.controller;

import jdk.nashorn.internal.ir.ReturnNode;
import life.majiang.community.dao.QuestionDao;
import life.majiang.community.service.QuestionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * @author CZS
 * @create 2022-07-18 20:23
 *
 * 问题详情页面controller
 *
 **/

@Controller
@RequestMapping("/question")
public class QuestionController {
    @Autowired
    private QuestionService questionService;

    @GetMapping("/{id}")
    public String question(@PathVariable(name = "id") Integer id, Model model){

        return questionService.question(id,model);
    }
}
