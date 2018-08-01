Spring Security与Shiro的学习Demo，没有使用默认的cookie-session，替换为jwt认证

1. 用于存储账号密码与权限的接口为SecurityUser
2. 用于查询验证账号密码和创建检验token的service接口为SecurityService
3. 虽然名字叫SecurityUser、SecurityService，但是并不是只用来对接Spring Security。这两个是与业务和Spring Securit与Shiro解耦的接口
4. 控制层的controller都在controller包里。ExceptionController用来捕获全局异常，学到的新用法。而剩余的两个controller分别为Security和Shiro的演示controller
5. Spring Security的代码security包里，Shiro在shiro包里
6. 如果同样只是简单使用jwt，除了Spring Security与Shiro各自的配置类可能需要自定义一下，其余的代码大体都不用改动了（作为demo，那些控制台打印可能需要删除一下）
7. 目前项目启动默认是在Spring Security保护状态下的。如果需要测试Shiro，需要注去除pom文件里的Spring Security依赖（没了依赖，Spring Security的代码也只能暂时注释掉了）
8. 要注去除pom文件里的Spring Security依赖是因为Spring Security太“智能”，只要引入依赖，什么都不做他都会保护你的项目，就没法调试Shiro了
9. 代码不多，但是里面的注释都是血与泪啊
10. 感觉Spring Security与Shiro都很难用，可能我比较垃圾。网上基本上都是说Shiro比Spring Security简单，但是这次学习整合jwt，感觉Spring Security的整合更加符合正常人的逻辑（当然我有可能不是正常人），所以我还是偏爱一些Spring Security