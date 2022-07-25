package life.majiang.community.controller;

import life.majiang.community.dto.CommentCreateDTO;
import life.majiang.community.dto.CommentDTO;
import life.majiang.community.dto.ResultDTO;
import life.majiang.community.enums.CommentTypeEnum;
import life.majiang.community.service.CommentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

/**
 * @author CZS
 * @create 2022-07-20 12:11
 *
 * 回复页面controller
 *
 **/

@RestController//传输JSON数据，用@RestController
@RequestMapping("/comment")
public class CommentController {
    @Autowired
    private CommentService commentService;

    @PostMapping
    public Object postComment(@RequestBody CommentCreateDTO commentCreateDTO, HttpServletRequest request){

        return commentService.postComment(commentCreateDTO,request);
    }

    @GetMapping("/{id}")
    public ResultDTO<List<CommentDTO>> subComments(@PathVariable(name = "id") Integer id){
        //获取二级评论列表
        List<CommentDTO> subCommentDTOList = commentService.list(id,CommentTypeEnum.COMMENT);

        return new ResultDTO(subCommentDTOList);
    }
}
