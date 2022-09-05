package life.majiang.community.controller;

import life.majiang.community.domain.Comment;
import life.majiang.community.domain.User;
import life.majiang.community.dto.CommentCreateDTO;
import life.majiang.community.dto.CommentDTO;
import life.majiang.community.dto.ResultDTO;
import life.majiang.community.enums.CommentTypeEnum;
import life.majiang.community.enums.CustomizeStatusCode;
import life.majiang.community.service.CommentService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

/**
 * @author CZS
 * @create 2022-07-20 12:11
 * <p>
 * 回复页面controller
 **/

@RestController//传输JSON数据，用@RestController
@RequestMapping("/comment")
public class CommentController {
    @Autowired
    private CommentService commentService;

    @PostMapping
    public Object postComment(@RequestBody CommentCreateDTO commentCreateDTO, HttpServletRequest request) {
        //需要登录才能评论
        User commentAuthor = (User) request.getSession().getAttribute("user");
        if (commentAuthor == null) {
            return new ResultDTO(CustomizeStatusCode.NO_LOGIN);
        }
        //验证回复内容是否为空
        //这里判断是否为空(包括空白符)，使用commons-lang3的工具类StringUtils的isBlank()方法
        if (commentCreateDTO == null || StringUtils.isBlank(commentCreateDTO.getContent())) {
            return new ResultDTO(CustomizeStatusCode.CONTENT_IS_EMPTY);
        }

        Comment comment = new Comment();
        comment.setParentId(commentCreateDTO.getParentId());
        comment.setContent(commentCreateDTO.getContent());
        comment.setType(commentCreateDTO.getType());
        comment.setCommentAuthor(commentAuthor.getAccountId());
        comment.setGmtCreate(System.currentTimeMillis());
        comment.setGmtModified(comment.getGmtCreate());

        commentService.postComment(comment, commentAuthor);

        return new ResultDTO(CustomizeStatusCode.SUCCESS);
    }

    @GetMapping("/{id}")
    public ResultDTO<List<CommentDTO>> subComments(@PathVariable(name = "id") Integer id) {
        //获取二级评论列表
        List<CommentDTO> subCommentDTOList = commentService.list(id, CommentTypeEnum.COMMENT);

        return new ResultDTO(subCommentDTOList);
    }
}
