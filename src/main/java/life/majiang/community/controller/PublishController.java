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
 * 发起页面controller
 *
 **/

@Controller
@RequestMapping("/publish")
public class PublishController {
//    @Autowired
//    private QuestionDao questionDao;
//
//    @Autowired
//    private UserDao userDao;

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

//        //页面传递了title、description、tag，还需要获取creator
//
//        //即使有异常，也能拿到输入的信息，回显信息到publish页面
//        model.addAttribute("title",title);
//        model.addAttribute("description",description);
//        model.addAttribute("tag",tag);
//
//        //信息为空时，给出提示
//        if (title == null || title == ""){
//            model.addAttribute("error","标题不能为空");
//            return "publish";//有异常，则跳转回publish页面
//        }
//        if (description == null || description == ""){
//            model.addAttribute("error","问题补充不能为空");
//            return "publish";//有异常，则跳转回publish页面
//        }
//        if (tag == null || tag == ""){
//            model.addAttribute("error","标签不能为空");
//            return "publish";//有异常，则跳转回publish页面
//        }
//
//        //关于creator的获取
//        //1.原本思路：像持久化登录状态一样，获取cookie，用里面的token查询数据库获得user，user里的account_id也就是creator
//        //2.我的思路：直接从session里面获取gitHubUser的id，也就是creator
//        //      ---> 因为在AuthorizeController中，首次登录第一次写session时，就把giHubUser写入session，里面就有用户id
//        //       ---> 可能的不足：session默认无操作30分钟就会销毁，而cookie能保持更长时间
//
//        //获取creator原本思路
//        User userByToken = null;
//        Cookie[] cookies = request.getCookies();
//        for (Cookie cookie : cookies) {
//            if (cookie.getName().equals("token")){
//                String token = cookie.getValue();
//                userByToken = userDao.findByToken(token);//根据token去数据库查询对应用户
//
//                if (userByToken != null){
//                    //如果这个userByToken用户存在，则绑定到session
//                    request.getSession().setAttribute("user",userByToken);
//                }
//                break;
//            }
//        }
//        //如果这个userByToken用户不存在，给出提示信息
//        if (userByToken == null){
//            model.addAttribute("error","用户未登录");
//            return "publish";//有异常，则跳转回publish页面
//        }
//
//        //用户存在且绑定到session后，将文章信息存入数据库(表question)
//        Question question = new Question();
//        question.setTitle(title);
//        question.setDescription(description);
//        question.setTag(tag);
//        question.setCreator(Integer.valueOf(userByToken.getAccountId()));//设置creator
//        question.setGmtCreate(System.currentTimeMillis());
//        question.setGmtModified(question.getGmtCreate());
//
//        questionDao.save(question);//存入表question
//
//        return "redirect:/";//没有异常，则跳转回(重定向)首页
    }


}
