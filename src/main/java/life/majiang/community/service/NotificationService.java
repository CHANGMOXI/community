package life.majiang.community.service;

import life.majiang.community.domain.User;
import life.majiang.community.dto.NotificationDTO;
import life.majiang.community.dto.PaginationDTO;
import org.springframework.ui.Model;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * @author CZS
 * @create 2022-07-13 22:56
 *
 * 业务层接口：通知功能
 *
 **/
public interface NotificationService {
    //最新通知列表
    public PaginationDTO<NotificationDTO> list(Integer accountId, Integer currentPage, Integer pageSize);

    //未读通知数
    Integer unreadCount(Integer accountId);

    //获取通知，设为已读
    NotificationDTO read(Integer id, User user);

    Integer getQuestionId(Integer commentId);
}
