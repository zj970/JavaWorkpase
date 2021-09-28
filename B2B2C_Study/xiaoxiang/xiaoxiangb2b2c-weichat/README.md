#  小象电商 微信小程序商城

#### 演示环境
1. PC和H5商城：https://shop.xiaoxiangai.com/ 测试账户：jack/123456
2. 商家连接：https://shop.xiaoxiangai.com/business  测试账户：business/123456
3. 总后台：https://shop.xiaoxiangai.com/admin 测试账户：admin/123456
4. 商城后台代码Git地址：https://gitee.com/xiaoxiangopen/xiaoxiangb2b2c
5. 小程序商城：
- ![输入图片说明](https://images.gitee.com/uploads/images/2021/0225/175527_375a7b51_5325125.jpeg "xiaoxiangweicha.png")

#### 小象电商微信： mzv988    请注明：小象电商 码云
- ![输入图片说明](https://images.gitee.com/uploads/images/2021/0201/105231_685d973a_5325125.png "xiaoxiangopen.png")

#### 介绍
小象电商是采用JAVA开发的B2B2C多用户商城系统。以“平台自营+多商户入驻”为主要经营模式，可快速帮客户打造类似“京东”一样的自营+招商入驻的经营模式电商平台。覆盖微信小程序、PC、H5、APP，涵盖直播、积分商城、多级分销等社交电商能力。
产品核心代码应用在多个大型B2C、B2B和B2B2C项目，经过千万级电商项目考验，安全、稳定、功能扩展性强，符合安全等保3级要求，满足超大型连锁百货零售电商业务需求。

 **如果对您有帮助，您可以点右上角 “Star” 收藏一下 ，获取第一时间更新，谢谢！** 

#### 国家高新技术企业
![输入图片说明](https://images.gitee.com/uploads/images/2021/0426/112651_dff37ec6_5325125.png "高新证书 - 600.png")

#### 官网

1. 官网地址：https://www.xiaoxiangai.com
2. 技术交流QQ群：958957921 请说明入群原因：码云
3. 微信：mzv988
- ![输入图片说明](https://images.gitee.com/uploads/images/2021/0527/153027_783da19c_5325125.png "屏幕截图.png")

#### 软件架构
1. 核心框架：Spring Boot 2.0.3.RELEASE
2. 安全框架：Apache Shiro 1.4.0
3. 视图框架：Spring MVC 5.0.6
4. 搜索框架：Elasticsearch 6.5.0
5. 任务调度：Spring + Quartz 2.2.3
6. 持久层框架：MyBatis 3.4.6 + Mybatis-plus 2.3
7. 数据库连接池：Alibaba Druid 1.1.10
8. 缓存框架：Ehcache 2.6 + Redis 6.0  
9. 日志管理：SLF4J 1.7 + Log4j2 2.7
10. 工具类：Apache Commons、Jackson 2.9.6、fastjson 1.2.6


#### 推荐运行环境
1. 操作系统：Linux、Unix、Windows
2. JDK：JDK 1.8
3. 应用服务器：Tomcat 8.5
4. 数据库：MySQL 5.7 +
5. Redis：6.0
6. Elasticsearch：6.5


#### 安装教程

1. 环境准备


- 确保您的电脑已安装JDK 1.8,Eclipse或别的开发工具；
- 确保你的电脑上已经下载安装MySQL 5.7+
- 安装elasticsearch-6.5.0。常见错误帮助（https://www.cnblogs.com/yijialong/p/9707238.html）：

启动elasticsearch 报如下错误：
[2017-05-10T10:04:50,648][WARN ][o.e.b.JNANatives         ] unable to install syscall filter: 
java.lang.UnsupportedOperationException: seccomp unavailable: CONFIG_SECCOMP not compiled into kernel, CONFIG_SECCOMP and CONFIG_SECCOMP_FILTER are needed
解决方法：
修改elasticsearch.yml 添加一下内容
bootstrap.memory_lock: false
bootstrap.system_call_filter: false

2. 运行mysql文件

- 开源版本运行free_db.sql； 
- 旗舰版本运行flagship_db.sql；

3. 下载商城商户端和平台运营端代码：https://gitee.com/xiaoxiangopen/xiaoxiangb2b2c

4. 使用IED打开java项目，Maven拉去需要的jar包；
  - 百度网盘
  - 链接：https://pan.baidu.com/s/17LE-q0YnqIxYGae2eR1bFg 
  - 提取码：4321 

5. 修改application.yml

- 文件地址：/src/main/resources/application.yml;
- 配置当前使用的运行环境：profiles；
- 在对应的运行环境文件中（application-test、application-dev、application-pro），配置数据库、redis、elasticsearch地址。


6. 启动服务

- 文件地址：/src/main/java/net/xiaoxiangshop/ApplicationB2B2C.java
- 右击 -> Run As -> Java Application

7.项目运行
- 下载微信小程序开发者工具：https://developers.weixin.qq.com/miniprogram/dev/devtools/download.html
- 下载并导入本商城小程序代码
- 小程序编译并运行
- 由于PC端没有开源，访问http://localhost:8080会提示找不到页面

#### 使用说明
[小象电商产品手册](https://www.yuque.com/xiaoxiangai/b2b2c/)


#### 项目地址

1. 商城PC及服务端地址：https://gitee.com/xiaoxiangopen/xiaoxiangb2b2c/
2. 前端小程序地址：https://gitee.com/xiaoxiangopen/xiaoxiangb2b2c-weichat

#### 软著
![输入图片说明](https://images.gitee.com/uploads/images/2021/0201/103942_26048a33_5325125.png "b2b2c软著.png")

#### 用户权益

1. 允许免费用于学习、毕设等。
2. 代码文件需保留相关license信息。
3. 禁止直接将本项目挂淘宝等商业平台出售。
4. 禁止基于本项目直接进行商业项目和获利的相关行为。
5. 非界面代码50%以上相似度的二次开源，二次开源需先联系作者。