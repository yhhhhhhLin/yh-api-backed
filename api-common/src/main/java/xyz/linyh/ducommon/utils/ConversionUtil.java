package xyz.linyh.ducommon.utils;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Optional;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Collectors;

/**
 *  集合类映射转换
 *  collection 集合
 *  function 映射函数
 *  collectionFactory 转换函数
 * @author linzz
 */
public class ConversionUtil {
    public static <T, R, CR extends Collection<R>> CR mapperCollection(Collection<T> collection,
                                                                       Function<T, R> function,
                                                                       Supplier<CR> collectionFactory) {
        return Optional.ofNullable(collection).orElse(new ArrayList<>(0))
                .stream().map(function).collect(Collectors.toCollection(collectionFactory));
    }
}
