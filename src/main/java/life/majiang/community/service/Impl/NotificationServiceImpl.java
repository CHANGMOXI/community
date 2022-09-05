package life.majiang.community.service.Impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import life.majiang.community.dao.CommentDao;
import life.majiang.community.dao.NotificationDao;
import life.majiang.community.domain.Comment;
import life.majiang.community.domain.Notification;
import life.majiang.community.domain.User;
import life.majiang.community.dto.NotificationDTO;
import life.majiang.community.dto.PaginationDTO;
import life.majiang.community.enums.CustomizeStatusCode;
import life.majiang.community.enums.NotificationStatusEnum;
import life.majiang.community.enums.NotificationTypeEnum;
import life.majiang.community.exception.CustomizeException;
import life.majiang.community.service.NotificationService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * @author CZS
 * @create 2022-07-13 22:59
 * <p>
 * 通知功能业务
 **/

@Service//业务层的组件，等价于@Component
public class NotificationServiceImpl implements NotificationService {
    @Autowired
    private NotificationDao notificationDao;

    @Autowired
    private CommentDao commentDao;

    //最新通知列表
    @Override
    public PaginationDTO<NotificationDTO> list(Integer accountId, Integer currentPage, Integer pageSize) {
        //分页查询之前，要防止页面url传递的currentPage超过总页数，导致分页查询结果为空
        LambdaQueryWrapper<Notification> lqw = new LambdaQueryWrapper<>();
        lqw.eq(Notification::getReceiver, accountId);//查询accountId对应的通知总数
        Integer totalCount = notificationDao.selectCount(lqw);//用MyBatis-Plus自带的查询总数
        int totalPage = 0;
        if (totalCount % pageSize == 0) {
            totalPage = totalCount / pageSize;
        } else {
            totalPage = (totalCount / pageSize) + 1;
        }
        if (currentPage < 1) {
            currentPage = 1;
        }
        if (currentPage > totalPage) {
            currentPage = totalPage;
        }
        //使用MyBatis-Plus自带的分页查询 获取 当前用户当前页 所有通知(按时间倒序)
        IPage<Notification> page = new Page<>(currentPage, pageSize);
        lqw.orderByDesc(Notification::getGmtCreate);//按时间倒序
        notificationDao.selectPage(page, lqw);
        List<Notification> currentPageNotifications = page.getRecords();

        List<NotificationDTO> NotificationDTOList = new ArrayList<>();//存放 当前用户当前页 所有通知记录(所有NotificationDTO)

        PaginationDTO<NotificationDTO> paginationDTO = new PaginationDTO<>();//代表当前用户当前页的PaginationDTO对象

        for (Notification notification : currentPageNotifications) {
            NotificationDTO notificationDTO = new NotificationDTO();
            BeanUtils.copyProperties(notification, notificationDTO);//利用工具类，快速复制
            notificationDTO.setDesc(NotificationTypeEnum.descOfType(notification.getType()));//根据type属性设置对应的描述

            //依次把 当前用户当前页的 所有通知记录(所有NotificationDTO) 放进 NotificationDTOList
            NotificationDTOList.add(notificationDTO);
        }

        //设置 代表当前用户当前页的PaginationDTO对象 的各个属性，最后一个形参为 当前页码前后最多可展示的页码
        paginationDTO.setPagination(NotificationDTOList, page.getPages(), currentPage, 3);

        return paginationDTO;
    }

    //未读通知数
    @Override
    public Integer unreadCount(Integer accountId) {
        LambdaQueryWrapper<Notification> lqw = new LambdaQueryWrapper<>();
        //where receiver = accountId and status = 0;
        lqw.eq(Notification::getReceiver, accountId).eq(Notification::getStatus, NotificationStatusEnum.UNREAD.getStatus());

        return notificationDao.selectCount(lqw);
    }

    //获取通知，设为已读
    @Override
    public NotificationDTO read(Integer id, User user) {
        Notification notification = notificationDao.selectById(id);
        //验证
        if (notification == null) {
            throw new CustomizeException(CustomizeStatusCode.NOTIFICATION_NOT_FOUND);
        }
        if (!Objects.equals(notification.getReceiver(), user.getAccountId())) {
            throw new CustomizeException(CustomizeStatusCode.READ_NOTIFICATION_FAIL);
        }

        //更新状态为 已读
        //已经是 已读 就不需要更新状态
        if (notification.getStatus() == NotificationStatusEnum.UNREAD.getStatus()) {
            notification.setStatus(NotificationStatusEnum.READ.getStatus());
            notificationDao.updateById(notification);
        }

        NotificationDTO notificationDTO = new NotificationDTO();
        BeanUtils.copyProperties(notification, notificationDTO);//利用工具类，快速复制
        notificationDTO.setDesc(NotificationTypeEnum.descOfType(notification.getType()));//根据type属性设置对应的描述

        return notificationDTO;
    }

    @Override
    public Integer getQuestionId(Integer commentId) {
        Comment comment = commentDao.selectById(commentId);

        if (comment == null) {
            throw new CustomizeException(CustomizeStatusCode.COMMENT_NOT_FOUND);
        }

        return comment.getParentId();
    }
}

