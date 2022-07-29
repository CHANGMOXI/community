package life.majiang.community.controller;

import life.majiang.community.dto.FileDTO;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

/**
 * @author CZS
 * @create 2022-07-27 21:22
 *
 * 图片上传controller
 *
 **/
@Controller
public class FileController {

    @RequestMapping("/file/upload")
    @ResponseBody
    public FileDTO upload(){
        FileDTO fileDTO = new FileDTO();
        fileDTO.setSuccess(1);
        fileDTO.setUrl("/images/community项目二维码.jpg");
        return fileDTO;
    }
}
