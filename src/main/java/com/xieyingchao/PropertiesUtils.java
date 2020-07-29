package com.xieyingchao;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.Objects;
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
        String prePath = this.getClass().getProtectionDomain().getCodeSource().getLocation().getPath();
        int last = prePath.lastIndexOf("/") + 1;
        int first = 0;
        if(System.getProperty("os.name").equals("Windows"))
            first = 1;
        prePath = prePath.substring(first, last);
        System.out.println("==================== jar包所在路径 ==================");
        System.out.println("   " + prePath);

        //这是jar包文件路径,idea运行需要改成this.path =path;
        this.path = prePath + path;
        properties = new Properties();
        InputStream file = new FileInputStream(this.path);
        properties.load(new InputStreamReader(file, StandardCharsets.UTF_8));
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
