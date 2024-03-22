package shop.linyh.generator.service;

import shop.linyh.generator.entity.ftl.DataModel;

public interface GeneratorService {

    /**
     * 根据dataModel里面的参数，生成项目文件
     * @param dataModel
     */
    public void generate(DataModel dataModel);
}
