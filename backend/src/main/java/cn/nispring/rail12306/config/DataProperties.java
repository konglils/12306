package cn.nispring.rail12306.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

import java.nio.file.Path;

@ConfigurationProperties(prefix = "data")
public class DataProperties {

    /// 数据文件夹
    private String dir;

    public Path getDir() {
        return Path.of(dir);
    }

    public void setDir(String dir) {
        this.dir = dir;
    }
}
