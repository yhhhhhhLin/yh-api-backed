package xyz.linyh.model.utils;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import xyz.linyh.ducommon.utils.ConversionUtil;

import java.util.ArrayList;
import java.util.function.Function;

/**
 * 工具类，将page的实体类转换
 */
public class PageResultUtil {


    /**
     * @param iPage
     * @param mapper
     * @return
     * @param <T> 原先类型
     * @param <V> 转换后类型
     */
    public static <T, V> Page<V> transfer(IPage<T> iPage, Function<T, V> mapper) {
        int current = (int) iPage.getCurrent();
        int size = (int) iPage.getSize();
        int total = (int) iPage.getTotal();
        int pages = (int) iPage.getPages();
        Page<V> result = new Page<>(current, size, total);
        result.setPages(pages);
        if (iPage.getTotal() == 0) {
            result.setRecords(new ArrayList<>(0));
            return result;
        }

        ArrayList<V> newRecords = ConversionUtil.mapperCollection(iPage.getRecords(), mapper, ArrayList::new);
        result.setRecords(newRecords);
        return result;
    }
}
