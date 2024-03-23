package shop.linyh.generator.entity;

import cn.hutool.core.io.FileUtil;
import cn.hutool.json.JSONUtil;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@NoArgsConstructor
@Data
public class Meta {

    private String name;
    private String description;
    private String basePackage;
    private String baseArtifact;
    private String version;
    private String author;
    private String createTime;
    private FileConfig fileConfig;
    private ModelsConfig modelConfig;

    /**
     * 更新meta文件
     * @param metaPath
     */
    public void updateMeta(String metaPath) {
        FileUtil.writeUtf8String(JSONUtil.toJsonPrettyStr(this),metaPath);

    }

    @NoArgsConstructor
    @Data
    public static class FileConfig {
        private String inputRootPath;
        private String outputRootPath;
        private String type;
        private List<Files> files;

        @NoArgsConstructor
        @AllArgsConstructor
        @Data
        public static class Files {
            private String inputPath;
            private String outputPath;
            private String type;
            private String generateType;
//        可以判断condition，然后在model里面找到对应的model，然后判断是否生成
            private String[] condition;
            private List<Files> files;
        }
    }

    @NoArgsConstructor
    @Data
    public static class ModelsConfig {
        private List<Models> models = new ArrayList<>();

        @NoArgsConstructor
        @Data
        @AllArgsConstructor
        public static class Models {
            private String fieldName;
            private String type;
            private String description;
            private Object defaultValue;
            private String abbr;
            private String condition;
            private List<Models> models;

            public Models(String fieldName, String type) {
                this.fieldName = fieldName;
                this.type = type;
            }
        }
    }


}
