package life.majiang.community.controller;

import life.majiang.community.dto.CommentDTO;
import life.majiang.community.dto.QuestionDTO;
import life.majiang.community.enums.CommentTypeEnum;
import life.majiang.community.service.CommentService;
import life.majiang.community.service.QuestionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;

/**
 * @author CZS
 * @create 2022-07-18 20:23
 * <p>
 * 问题详情页面controller
 **/

@Controller
@RequestMapping("/question")
public class QuestionController {
    @Autowired
    private QuestionService questionService;

    @Autowired
    private CommentService commentService;

    @GetMapping("/{id}")
    public String question(@PathVariable(name = "id") Integer id, Model model) {
        //获取id对应的问题信息
        QuestionDTO questionDTO = questionService.question(id);
        //获取问题的回复列表(按时间倒序)
        List<CommentDTO> commentDTOList = commentService.list(id, CommentTypeEnum.QUESTION);
        //获取相关问题列表
        List<QuestionDTO> relatedQuestionDTOList = questionService.relatedQuestion(questionDTO);

        //发送给前端
        model.addAttribute("question", questionDTO);
        model.addAttribute("comments", commentDTOList);
        model.addAttribute("relatedQuestions", relatedQuestionDTOList);

        return "question";
    }
}
