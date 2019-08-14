# 1.项目说明 #
    把jmeter测试过程中产生的sample save\persisting\保存到mongodb数据库中。
# 2.使用说明 #
1. 将该项目打包
2. 把生成的包放到jmeter/lib/ext目录下
3. 启动jmeter
4. 在监听器中添加Persisting Sample to MongoDB 监听器
5. 在页面配置上mongodb等相关信息
# 3.配置说明 #
1. MONGO_HOST: mongodb的ip地址
2. MONGO_PORT: mongod的端口地址，若不填写，则默认为27017
3. USERNAME: 用户名
4. PASSWORD: 密码
5. DATABASE_NAME: 数据库名，若不填写，则默认为JmeterSamplers。
6. TESTCASE_NAME: 测试用例名称，对应mongodb中collectionName，若不填写，则默认为:TestCaseName
# 4.数据格式说明 #
1. dt:
2. ats:
3. b:
4. ct:
5. ed:
6. ec:
7. gts:
8. it:
9. ly:
10. rc:
11. rm:
12. rf:
13. sc:
14. hn:
15. sl:
16. sf:
17. tn:
18. t:
19. ts:
20. ar:
21. vv: