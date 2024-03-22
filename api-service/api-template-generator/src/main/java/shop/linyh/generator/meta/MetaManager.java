package shop.linyh.generator.meta;

import cn.hutool.core.io.resource.ResourceUtil;
import cn.hutool.json.JSONUtil;
import shop.linyh.generator.entity.Meta;

import java.io.File;

public class MetaManager {

    /**
     * volatile 保证所有线程都是可以使用这个变量 有原子性
     */
    private static volatile Meta meta;

    private MetaManager() {

    }

    /**
     * 双重检查锁创建单例模式
     *
     * @return
     */
    public static Meta getMeta(String metaPath) {
        if (meta == null) {
            synchronized (MetaManager.class) {
                if (meta == null) {
                    meta = initMeta(metaPath);
                }
            }
        }
        return meta;
    }

    /**
     * 如果是饿汉式创建单例模式，可以用这种方式
     * private static Meta meta = initMeta();
     *
     * @return
     */
    public static Meta initMeta(String metaPath) {
//        获取模板文件里面的json配置数据
        String metaJson = ResourceUtil.readUtf8Str(metaPath);
        Meta meta = JSONUtil.toBean(metaJson, Meta.class);
//        TODO 可能还有一些其他处理（默认值）
        return meta;
    }
}
