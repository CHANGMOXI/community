package life.majiang.community.controller;

import life.majiang.community.domain.User;
import life.majiang.community.dto.NotificationDTO;
import life.majiang.community.enums.NotificationTypeEnum;
import life.majiang.community.service.NotificationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.servlet.http.HttpServletRequest;

/**
 * @author CZS
 * @create 2022-07-26 17:02
 **/
@Controller
@RequestMapping("/notification")
public class NotificationController {
    @Autowired
    private NotificationService notificationService;

    @GetMapping("/{id}")
    public String read(@PathVariable(name = "id") Integer id, HttpServletRequest request){

        User user = (User) request.getSession().getAttribute("user");
        if (user == null){
            return "redirect:/";//如果没有用户信息，则跳转到首页进行登录
        }

        //获取id对应的通知，并设为已读
        NotificationDTO notificationDTO = notificationService.read(id,user);

        if (NotificationTypeEnum.REPLY_QUESTION.getType() == notificationDTO.getType()){
            //回复问题的通知，直接跳转到相应问题页面
            return "redirect:/question/" + notificationDTO.getParentId();
        }
        if (NotificationTypeEnum.REPLY_COMMENT.getType() == notificationDTO.getType()){
            //回复评论的通知，先根据parentId获取父类评论，根据父类评论再获取相应的问题id
            Integer questionId = notificationService.getQuestionId(notificationDTO.getParentId());
            //跳转到相应问题页面
            return "redirect:/question/" + questionId;
        }

        return "redirect:/";
    }
}
