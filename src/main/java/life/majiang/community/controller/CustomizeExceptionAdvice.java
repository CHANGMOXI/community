package life.majiang.community.controller;

import com.alibaba.fastjson.JSON;
import life.majiang.community.dto.ResultDTO;
import life.majiang.community.exception.CustomizeException;
import life.majiang.community.enums.CustomizeStatusCode;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;

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
    public ModelAndView doException(Exception e, Model model, HttpServletRequest request, HttpServletResponse response) {
        String contentType = request.getContentType();
        if ("application/json".equals(contentType)){
            ResultDTO resultDTO;
            //返回JSON
            if (e instanceof CustomizeException){
                resultDTO = new ResultDTO((CustomizeException) e);
            }else {
                resultDTO = new ResultDTO(CustomizeStatusCode.SYSTEM_ERROR);
            }

            try {
                response.setContentType("application/json");
                response.setStatus(200);
                response.setCharacterEncoding("utf-8");

                PrintWriter writer = response.getWriter();
                writer.write(JSON.toJSONString(resultDTO));
                writer.close();
            } catch (IOException ioe) {
                ioe.printStackTrace();
            }
            return null;
        }else {
            //跳转错误页面
            if (e instanceof CustomizeException){
                //业务异常
                model.addAttribute("message",e.getMessage());
            }else {
                //其他异常
                model.addAttribute("message",CustomizeStatusCode.SYSTEM_ERROR.getMessage());
            }

            return new ModelAndView("error");//返回到error页面，相当于controller中的return "index"
        }
    }
}
