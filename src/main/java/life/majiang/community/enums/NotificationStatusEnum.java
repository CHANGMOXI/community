package life.majiang.community.enums;

/**
 * @author CZS
 * @create 2022-07-25 22:59
 **/
public enum NotificationStatusEnum {
    UNREAD(0),
    READ(1);

    private Integer status;

    NotificationStatusEnum(Integer status) {
        this.status = status;
    }

    public Integer getStatus() {
        return status;
    }
}
