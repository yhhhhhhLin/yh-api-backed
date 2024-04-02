package xyz.linyh.ducommon.constant;


/**
 * @author lin
 */
public interface RedisConstant {

    String USER_ID_PREFIX = "user_id_";

    /**
     * 分页的interface缓存names
     */
    String INTERFACE_PAGE_CACHE_NAMES = "interface_page";

    /**
     * 分页的interface缓存names
     */
    String AUDIT_PAGE_CACHE_NAMES = "audit_page";

    /**
     * 所有积分产品缓存names
     */
    String CREDIT_PRODUCT_CACHE_NAMES = "credit_product_list";;

    /**
     * 保存到redis中的每日访问量key前缀
     */
    String DAILY_VISIT_KEY_PRE = "daily_visit_";

    /**
     * 保存到redis中的每日访问量key前缀
     */
    String INTERFACE_ID_AND_CALL_COUNT_KEY = "interface_id_and_count:";
}
