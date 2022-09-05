package life.majiang.community.enums;

/**
 * @author CZS
 * @create 2022-07-25 22:59
 **/
public enum NotificationTypeEnum {
    REPLY_QUESTION(1, "回复了问题"),
    REPLY_COMMENT(2, "回复了评论");

    private Integer type;
    private String desc;//描述：回复了问题/评论

    NotificationTypeEnum(Integer type, String desc) {
        this.type = type;
        this.desc = desc;
    }

    public String getDesc() {
        return desc;
    }

    public Integer getType() {
        return type;
    }

    //获取传进来的type对应的desc
    public static String descOfType(Integer type) {
        for (NotificationTypeEnum notificationTypeEnum : NotificationTypeEnum.values()) {
            if (notificationTypeEnum.getType() == type) {
                return notificationTypeEnum.getDesc();
            }
        }
        return "";
    }
}
