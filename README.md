#1.项目说明 #
把jmeter测试过程中产生的sample save\persisting\保存到mongodb数据库中。
#2.使用说明 #
1. 将该项目打包
2. 把生成的包放到jmeter/lib/ext目录下
3. 启动jmeter
4. 在监听器中添加Persisting Sample to MongoDB 监听器
5. 在页面配置上mongodb等相关信息