package life.majiang.community.controller;

import life.majiang.community.exception.BusinessException;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.servlet.ModelAndView;

/**
 * @author CZS
 * @create 2022-07-19 16:12
 *
 * 异常处理器 ---> 放在 表现层controller，其他层的异常 都抛到 表现层 再统一处理
 *
 **/

@ControllerAdvice//异常处理器
public class CustomizeExceptionAdvice {

    @ExceptionHandler(Exception.class)
    public ModelAndView doException(Exception e, Model model){

        if (e instanceof BusinessException){
            //业务异常
            model.addAttribute("message",e.getMessage());
        }else {
            //其他异常
            model.addAttribute("message","服务太热啦，请稍后再试");
        }

        return new ModelAndView("error");//返回到error页面，相当于controller中的return "index"
    }


}
