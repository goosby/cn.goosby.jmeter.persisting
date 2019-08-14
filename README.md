# 1.项目说明 #
    把jmeter测试过程中产生的sample save\persisting\保存到mongodb数据库中。
# 2.使用说明 #
1. 将该项目打包
2. 把生成的包放到jmeter/lib/ext目录下
3. 启动jmeter
4. 在监听器中添加Persisting Sample to MongoDB 监听器
5. 在页面配置上mongodb等相关信息
# 3.配置说明 #
jmeter界面中个字段的说明。
```css
1. MONGO_HOST: mongodb的ip地址
2. MONGO_PORT: mongod的端口地址，若不填写，则默认为27017
3. USERNAME: 用户名
4. PASSWORD: 密码
5. DATABASE_NAME: 数据库名，若不填写，则默认为JmeterSamplers。
6. TESTCASE_NAME: 测试用例名称，对应mongodb中collectionName，若不填写，则默认为:TestCaseName
```
# 4.数据格式说明 #
   下面为保存到mongodb中各字段的意思。
```$xslt
1. dt:datatype - 数据类型
2. ats:AllThreads
3. b:Bytes - 字节
4. ct:ConnectTime
5. ed:DataEncodingNoDefault
6. ec:ErrorCount
7. gts:GroupThreads
8. it:IdleTime
9. ly:Latency - 接收到响应的第一个字节的时间点 - 请求开始发送的时间点
10. rc:ResponseCode - 返回码
11. rm:ResponseMessage
12. rf:ResultFileName
13. sc:SampleCount
14. hn:Hostname
15. sl:SampleLabel
16. sf:Successful
17. tn:ThreadName - 线程名字
18. t:Time
19. ts:TimeStamp - 请求发出的绝对时间
20. ar:AssertionResults
21. vv:VarValue
22. s:Successful - 是否成功
```