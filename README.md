# YiBan_CheckIn
【Java】自动执行易班校本化每日健康打卡

文件下载：[下载地址](https://github.com/xieyingchao/YiBan_CheckIn/releases/tag/v1.0)

## 感谢：

1. 思路提供：[yiban_auto_submit](https://github.com/looyeagee/yiban_auto_submit)
2. 微信推送服务：server酱，官网地址：[http://sc.ftqq.com/](http://sc.ftqq.com/)

## 使用方法：

1. 将***PersonInfo.properties***和打包好的jar包放在用一个文件夹下

2. ~~cmd进入该文件夹~~

3. 执行命令 **java -jar jar包路径**

```shell
java -jar yiban-1.0-RELEASE-jar-with-dependencies.zip #jar包路径
```

4. 或者你也可以不用将***PersonInfo.properties***放在同个目录下，但是需要指出jar包和配置文件的路径

```shell
java -jar yiban-1.0-RELEASE-jar-with-dependencies D:\java\idea\yiban\PersonInfo.properties.zip
```



## Linux平台：

1. 将***PersonInfo.properties***和打包好的jar包放在用一个文件夹下

2. ~~cd进入目录~~

3. 创建log文件YiBan_CheckIn.log

4. 执行命令**java -jar jar包路径 >> 日志文件路径 &**

   ```shell
   java -jar yiban-1.0-RELEASE-jar-with-dependencies >> YiBan_CheckIn.log &
   ```

5. 设定定时任务

   1. 先执行编辑定时任务的命令

      ```shell
      crontab -e
      ```

   2. 添加任务

      ```shell
      01 00 * * * java -jar yiban-1.0-RELEASE-jar-with-dependencies.jar >> YiBan_CheckIn.log &
      ```

   3. 执行 **crontab -l** 查看定时任务
