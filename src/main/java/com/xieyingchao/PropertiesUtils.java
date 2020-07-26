package com.xieyingchao;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.OutputStreamWriter;
import java.nio.charset.StandardCharsets;
import java.util.Properties;

/**
 * @author ：XieYingChao
 * @description：读取修改配置文件
 * @date ：2020/7/26 20:23
 */
public class PropertiesUtils {

    private String path;
    private Properties properties;

    PropertiesUtils(String path) throws Exception {
        this.path = path;
        properties = new Properties();
        FileInputStream file = new FileInputStream(path);
        properties.load(file);
    }

    public void updateValue(String key, String value) throws Exception {
        properties.setProperty(key, value);
        OutputStreamWriter outputStreamWriter;
        try {
            FileOutputStream file  = new FileOutputStream(path);
            outputStreamWriter = new OutputStreamWriter(file, StandardCharsets.UTF_8);
            properties.store(outputStreamWriter,"");
            file.close();
        }catch (Exception e){
            System.out.println("=================== 更新配置文件失败 ===================");
        }
    }

    public String getValue(String key) {
        return properties.getProperty(key);
    }
}
