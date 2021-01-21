# 一文道尽SpringMVC的那些爱恨情仇
`reference:`[爱上狂神说](https://space.bilibili.com/95256449)
## 1.中心控制器
&ensp;&ensp;Spring的web框架围绕**DispatcherServlet**设计。DispatcherServlet的作用是将请求分发到不同的处理器。DispatcherServlet是一个实际的Servlet (它继承自HttpServlet 基类)。~~这里我借用狂神两张图🐶~~

- 1.DispatcherServlet表示前置控制器，是整个SpringMVC的控制中心。用户发出请求，DispatcherServlet接收请求并拦截请求。

&ensp;&ensp;我们假设请求的url为 : http://localhost:8080/SpringMVC/hello


&ensp;&ensp;&ensp;&ensp;**如上url拆分成三部分：**

&ensp;&ensp;&ensp;&ensp;http://localhost:8080服务器域名

&ensp;&ensp;&ensp;&ensp;SpringMVC部署在服务器上的web站点

&ensp;&ensp;&ensp;&ensp;hello表示控制器

&ensp;&ensp;&ensp;&ensp;通过分析，如上url表示为：请求位于服务器localhost:8080上的SpringMVC站点的hello控制器。

- 2.HandlerMapping为处理器映射。DispatcherServlet调用HandlerMapping,HandlerMapping根据请求url查找Handler。
- 3.HandlerExecution表示具体的Handler,其主要作用是根据url查找控制器，如上url被查找控制器为：hello。
- 4.HandlerExecution将解析后的信息传递给DispatcherServlet,如解析控制器映射等。
- 5.HandlerAdapter表示处理器适配器，其按照特定的规则去执行Handler。
- 6.Handler让具体的Controller执行。
- 7.Controller将具体的执行信息返回给HandlerAdapter,如ModelAndView。
- 8.HandlerAdapter将视图逻辑名或模型传递给DispatcherServlet。
- 9.DispatcherServlet调用视图解析器(ViewResolver)来解析HandlerAdapter传递的逻辑视图名。
- 10.视图解析器将解析的逻辑视图名传给DispatcherServlet。
- 11.DispatcherServlet根据视图解析器解析的视图结果，调用具体的视图。
- 12.最终视图呈现给用户。

## 2.SpringMVC注解版项目创建
- 0.必要依赖要导入
  ```xml
    <dependencies>
        <dependency>
            <groupId>junit</groupId>
            <artifactId>junit</artifactId>
            <version>4.12</version>
        </dependency>
        <dependency>
            <groupId>org.springframework</groupId>
            <artifactId>spring-webmvc</artifactId>
            <version>5.1.9.RELEASE</version>
        </dependency>
        <dependency>
            <groupId>javax.servlet</groupId>
            <artifactId>servlet-api</artifactId>
            <version>2.5</version>
        </dependency>
        <dependency>
            <groupId>javax.servlet.jsp</groupId>
            <artifactId>jsp-api</artifactId>
            <version>2.2</version>
        </dependency>
        <dependency>
            <groupId>javax.servlet</groupId>
            <artifactId>jstl</artifactId>
            <version>1.2</version>
        </dependency>
    </dependencies>
  ```
- 1.在`pom.xml`文件中更换镜像源
  ```xml
    <!-- 配置阿里云仓库 -->
    <repositories>
        <repository>
            <id>aliyun-repos</id>
            <url>https://maven.aliyun.com/repository/public</url>
            <releases>
                <enabled>true</enabled>
            </releases>
            <snapshots>
                <enabled>false</enabled>
            </snapshots>
        </repository>
    </repositories>
    <pluginRepositories>
        <pluginRepository>
            <id>aliyun-repos</id>
            <url>https://maven.aliyun.com/repository/public</url>
            <releases>
                <enabled>true</enabled>
            </releases>
            <snapshots>
                <enabled>false</enabled>
            </snapshots>
        </pluginRepository>
    </pluginRepositories>
  ```
- 2.在pom.xml中配置资源过滤
  ```xml
    <!-- 导入资源过滤 -->
    <build>
        <resources>
            <resource>
                <directory>src/main/java</directory>
                <includes>
                    <include>**/*.properties</include>
                    <include>**/*.xml</include>
                </includes>
                <filtering>false</filtering>
            </resource>
            <resource>
                <directory>src/main/resources</directory>
                <includes>
                    <include>**/*.properties</include>
                    <include>**/*.xml</include>
                </includes>
                <filtering>false</filtering>
            </resource>
        </resources>
    </build>
  ```
- 3.配置`web.xml`
  ```xml
    <?xml version="1.0" encoding="UTF-8"?>
    <web-app xmlns="https://jakarta.ee/xml/ns/jakartaee"
            xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
            xsi:schemaLocation="https://jakarta.ee/xml/ns/jakartaee https://jakarta.ee/xml/ns/jakartaee/web-app_5_0.xsd"
            version="5.0">
        
        <!--1.配置DispatcherServlet是springmvc的核心
            请求分发器/前端控制器-->
        <servlet>
            <servlet-name>springmvc</servlet-name>
            <servlet-class>org.springframework.web.servlet.DispatcherServlet</servlet-class>
            <!--3.DispatcherServlet要绑定springmvc的配置文件-->
            <init-param>
                <!-- 参数说明
                    contextConfigLocation: 根据路径来匹配 -->
                <param-name>contextConfigLocation</param-name>
                <!-- 这个配置文件放在resources下
                    classpath: 当前类的路径 -->
                <param-value>classpath:springmvc-servlet.xml</param-value>
            </init-param>
            <!--启动级别设定为1：服务器一起动就跟着启动
                启动顺序，数字越小，启动越早 -->
            <load-on-startup>1</load-on-startup>
        </servlet>
        
        <!--2.注册完DispatcherServlet类之后去注册其请求
            所有请求都会被springmvc拦截 -->
        <servlet-mapping>
            <servlet-name>springmvc</servlet-name>
            <!-- 这里写/就完事了
                "/": 只会匹配请求，不会匹配jsp页面
                "/*": 匹配所有请求，包括jsp页面 -->
            <url-pattern>/</url-pattern>
        </servlet-mapping>
    </web-app>
  ```
- 4.添加SpringMVC配置文件：在recourse目录下创建`springmvc-servlet.xml`
  ```xml
    <?xml version="1.0" encoding="UTF-8"?>
    <beans xmlns="http://www.springframework.org/schema/beans"
        xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
        xmlns:context="http://www.springframework.org/schema/context"
        xmlns:mvc="http://www.springframework.org/schema/mvc"
        xsi:schemaLocation="http://www.springframework.org/schema/beans
        http://www.springframework.org/schema/beans/spring-beans.xsd
        http://www.springframework.org/schema/context
        https://www.springframework.org/schema/context/spring-context.xsd
        http://www.springframework.org/schema/mvc
        https://www.springframework.org/schema/mvc/spring-mvc.xsd">

        <!-- 自动扫描包，让指定包下的注解生效,由IOC容器统一管理 -->
        <context:component-scan base-package="com._.controller 你的controller package路径"/>
        <!-- 让Spring MVC不处理静态资源 -->
        <mvc:default-servlet-handler />
        <!--
        支持mvc注解驱动
            在spring中一般采用@RequestMapping注解来完成映射关系
            要想使@RequestMapping注解生效
            必须向上下文中注册DefaultAnnotationHandlerMapping
            和一个AnnotationMethodHandlerAdapter实例
            这两个实例分别在类级别和方法级别处理。
            而annotation-driven配置帮助我们自动完成上述两个实例的注入。
        -->
        <mvc:annotation-driven />

        <!-- 视图解析器 -->
        <bean class="org.springframework.web.servlet.view.InternalResourceViewResolver"
            id="internalResourceViewResolver">
            <!-- 前缀 -->
            <property name="prefix" value="/WEB-INF/jsp/" />
            <!-- 后缀 -->
            <property name="suffix" value=".jsp" />
        </bean>

    </beans>
  ```
- 5.创建Controller
  ```java
    import org.springframework.stereotype.Controller;
    import org.springframework.ui.Model;
    import org.springframework.web.bind.annotation.RequestMapping;

    @Controller
    @RequestMapping("/HelloController")
    public class HelloController {

        // http://localhost8080/HelloController/hello
        @RequestMapping("/hello")
        public String hello(Model model) {
            // 封装数据
            // 在WEB-INF路径下创建jsp文件夹，其中存放前端视图文件-jsp文件
            // 前端视图中存在msg，则会被解析成你下面映射的内容
            model.addAttribute("msg", "Hello, SpringMVC");
            // 会被视图解析器处理，这里返回的是前端视图名（xxx.jsp则返回xxx）
            return "hello"; 
        }
    }
  ```
  - `@Controller`
    - 该注解类型用于声明Spring类的实例是一个控制器
    - 为了保证Spring能找到你的控制器，需要在配置文件中声明组件扫描（见上面`springmvc-servlet.xml`中配置的`context:component-scan`）
  - `@RequestMapping`
    - 该注解用于映射url到控制器类或一个特定的处理程序方法。可用于类或方法上。用于类上，表示类中的所有响应请求的方法都是以该地址作为父路径。



- 6.编写前端视图层hello.jsp
  
  msg要写成`${msg}`
  ```jsp
    <%@ page contentType="text/html;charset=UTF-8" language="java" %>
    <html>
    <head>
        <title>Title</title>
    </head>
    <body>
    ${msg}
    </body>
    </html>
  ```
- 7.HelloWorld写好了，你会配置Tomcat让他跑起来吗？
  
  **除了将该项目Tomcat以外，点击项目结构，为该项目创建一个lib目录，将所有依赖加入进去**

## 3.RestFul 风格
- Restful就是一个资源定位及资源操作的风格。
- 传统方式操作资源：通过不同的参数来实现不同的效果！方法单一，post 和 get
  - http://127.0.0.1/item/queryItem.action?id=1 查询,GET
  - http://127.0.0.1/item/saveItem.action 新增,POST
  - http://127.0.0.1/item/updateItem.action 更新,POST
  - http://127.0.0.1/item/deleteItem.action?id=1 删除,GET或POST
- 使用RESTful操作资源：可以通过不同的请求方式来实现不同的效果！如下：请求地址一样，但是功能可以不同！
  - http://127.0.0.1/item/1 查询,GET
  - http://127.0.0.1/item 新增,POST
  - http://127.0.0.1/item 更新,PUT
  - http://127.0.0.1/item/1 删除,DELETE
- 通过`method`属性还可以指定请求类型
- 若不使用`method`属性指定还可以通过以下注解声明请求类型
  - `@GetMapping`
  - `@PostMapping`
  - `@PutMapping`
  - `@DeleteMapping`
  - `@PatchMapping`
```java
/** 普通风格
 *  请求方式：http://localhost:8080/SpringMVC/hello?name=yize&age=23
 */
@Controller
@RequestMapping("/SpringMVC")
public class HelloController {

    @RequestMapping("/hello")
    public String hello(String name, String age, Model model) {
        model.addAttribute("name", name);
        model.addAttribute("age", age);
        return "hello";
    }
}
```
```java
/** RestFul风格
 * 请求方式：http://localhost:8080/SpringMVC/hello/yize/23
 * @method POST
 */
@Controller
@RequestMapping("/RestFul")
public class RestFulController {

    @RequestMapping(value = "/hello/{name}/{age}", method = {RequestMethod.POST})
    public String hello(@PathVariable String name, @PathVariable String age, Model model) {
        model.addAttribute("name", name);
        model.addAttribute("age", age);
        return "hello";

    }
}
```

## 4.后端数据向前端传递
&ensp;&ensp;这里我喜欢用Model方式，其他方式可以自己搜索了解
- **通过Model**
- 通过ModelAndView
- 通过ModelMap

## 5.中文乱码问题怎么解决
&ensp;&ensp;在`web.xml`中添加一段配置信息（用于过滤）**每次修改配置文件记得重启Tomcat**
```xml
    <filter>
        <filter-name>encoding</filter-name>
        <filter-class>org.springframework.web.filter.CharacterEncodingFilter</filter-class>
        <init-param>
            <param-name>encoding</param-name>
            <param-value>utf-8</param-value>
        </init-param>
    </filter>
    <filter-mapping>
        <filter-name>encoding</filter-name>
        <url-pattern>/*</url-pattern>
    </filter-mapping>
```

## 6.实战：整合SSM框架（~~来了老弟🤩~~）
- 需要的依赖先安排一下
  - 前提：更换了阿里云镜像和配置好了Maven资源过滤
    ```xml
        <!-- Maven资源过滤 -->
        <build>
            <resources>
                <resource>
                    <directory>src/main/java</directory>
                    <includes>
                        <include>**/*.properties</include>
                        <include>**/*.xml</include>
                    </includes>
                    <filtering>false</filtering>
                </resource>
                <resource>
                    <directory>src/main/resources</directory>
                    <includes>
                        <include>**/*.properties</include>
                        <include>**/*.xml</include>
                    </includes>
                    <filtering>false</filtering>
                </resource>
            </resources>
        </build>

        <!-- 配置阿里云仓库 -->
        <repositories>
            <repository>
                <id>aliyun-repos</id>
                <url>https://maven.aliyun.com/repository/public</url>
                <releases>
                    <enabled>true</enabled>
                </releases>
                <snapshots>
                    <enabled>false</enabled>
                </snapshots>
            </repository>
        </repositories>
        <pluginRepositories>
            <pluginRepository>
                <id>aliyun-repos</id>
                <url>https://maven.aliyun.com/repository/public</url>
                <releases>
                    <enabled>true</enabled>
                </releases>
                <snapshots>
                    <enabled>false</enabled>
                </snapshots>
            </pluginRepository>
        </pluginRepositories>
    ```
  - 添加必要的依赖
    ```xml
        <dependencies>

            <!--Junit-->
            <dependency>
                <groupId>junit</groupId>
                <artifactId>junit</artifactId>
                <version>4.12</version>
            </dependency>

            <!--数据库驱动-->
            <dependency>
                <groupId>mysql</groupId>
                <artifactId>mysql-connector-java</artifactId>
                <version>5.1.47</version>
            </dependency>

            <!-- 数据库连接池c3p0 -->
            <dependency>
                <groupId>com.mchange</groupId>
                <artifactId>c3p0</artifactId>
                <version>0.9.5.2</version>
            </dependency>

            <!--Servlet - JSP -->
            <dependency>
                <groupId>javax.servlet</groupId>
                <artifactId>servlet-api</artifactId>
                <version>2.5</version>
            </dependency>
            <dependency>
                <groupId>javax.servlet.jsp</groupId>
                <artifactId>jsp-api</artifactId>
                <version>2.2</version>
            </dependency>
            <dependency>
                <groupId>javax.servlet</groupId>
                <artifactId>jstl</artifactId>
                <version>1.2</version>
            </dependency>

            <!--Mybatis-->
            <dependency>
                <groupId>org.mybatis</groupId>
                <artifactId>mybatis</artifactId>
                <version>3.5.2</version>
            </dependency>
            <dependency>
                <groupId>org.mybatis</groupId>
                <artifactId>mybatis-spring</artifactId>
                <version>2.0.2</version>
            </dependency>

            <!--Spring-->
            <dependency>
                <groupId>org.springframework</groupId>
                <artifactId>spring-webmvc</artifactId>
                <version>5.1.9.RELEASE</version>
            </dependency>
            <dependency>
                <groupId>org.springframework</groupId>
                <artifactId>spring-jdbc</artifactId>
                <version>5.1.9.RELEASE</version>
            </dependency>

            <!--lombok-->
            <dependency>
                <groupId>org.projectlombok</groupId>
                <artifactId>lombok</artifactId>
                <version>1.18.16</version>
            </dependency>

        </dependencies>
    ```
- 准备我们必要的配置文件（文件、目录结构）
  ```
  |- com
      |- springmvc
             |- controller
             |- dao
             |- pojo
             |- service


  |- resources
        |- spring
        |     |- spring-dao.xml
        |     |- spring-mvc.xml
        |     |- spring-service.xml
        |
        |- applicationContext.xml
        |- mybatis-config.xml
        |- database.properties


  |- web
      |- WEB-INF
        |   |- jsp
        |   |- web.xml
        |
        |- index.jsp
   ```
- `database.properties`配置（关联数据库）
  
  这个文件主要是配置和你数据库的连接信息，**这里useSSL参数要根据你mysql版本而定**，关于版本不同可能还有别的问题（听说mysql8.0以上还要增添时区配置）
  ```properties
    jdbc.driver=com.mysql.jdbc.Driver
    jdbc.url=jdbc:mysql://localhost:3306/你数据库的名字?useSSL=false&useUnicode=true&characterEncoding=utf8
    jdbc.username=数据库用户名
    jdbc.password=数据库密码
  ```
- 编列MyBatis核心配置文件`mybatis-config.xml`
  ```xml
    <?xml version="1.0" encoding="UTF-8" ?>
    <!DOCTYPE configuration
            PUBLIC "-//mybatis.org//DTD Config 3.0//EN"
            "http://mybatis.org/dtd/mybatis-3-config.dtd">
    <configuration>
        <!--数据源配置，交给Spring去做-->
        <typeAliases>
            <package name="com.springmvc.pojo"/>
        </typeAliases>
        <mappers>
            <mapper resource="com/springmvc/dao/Mapper.xml"/>
        </mappers>
    </configuration>
  ```
- 编写实体类Books
  ```java
    package com.springmvc.pojo;

    import lombok.AllArgsConstructor;
    import lombok.Data;
    import lombok.NoArgsConstructor;

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public class Books {

        private int bookID;
        private String bookName;
        private int bookCounts;
        private String detail;

    }
  ```
- 编写Dao层Mapper接口（定义可以执行的操作）
  
  这里我的接口名叫`BookMapper`或可以起名`BookDao`
  ```java
    package com.springmvc.dao;

    import com.springmvc.pojo.Books;
    import java.util.List;

    public interface BookMapper {

        //查询全部Book,返回list集合
        List<Books> queryAllBook();

    }
  ```
- 编写BookMapper所对应的Mapper.xml
  ```xml
    <?xml version="1.0" encoding="UTF-8" ?>
    <!DOCTYPE mapper
            PUBLIC "-//mybatis.org//DTD Mapper 3.0//EN"
            "http://mybatis.org/dtd/mybatis-3-mapper.dtd">

    <mapper namespace="com.springmvc.dao.BookMapper">

        <!--查询全部Book-->
        <select id="queryAllBook" resultType="Books">
            SELECT * from ssmbuild.books
        </select>

    </mapper>
  ```

--> *接口层编写完，进入业务层编写*
- 编写service接口和实现类
  `BookService`接口：该接口参照Dao编写即可
  ```java
    package com.springmvc.service;

    import com.springmvc.pojo.Books;
    import java.util.List;

    //BookService:底下需要去实现,调用dao层
    public interface BookService {
        //查询全部Book,返回list集合
        List<Books> queryAllBook();
    }
  ```
  `BookServiceImpl`实现
  ```java
    package com.springmvc.service;

    import com.springmvc.dao.BookMapper;
    import com.springmvc.pojo.Books;
    import java.util.List;

    public class BookServiceImpl implements BookService {

        //调用dao层的操作，设置一个set接口，方便Spring管理
        private BookMapper bookMapper;

        public void setBookMapper(BookMapper bookMapper) {
            this.bookMapper = bookMapper;
        }

        public List<Books> queryAllBook() {
            return bookMapper.queryAllBook();
        }
    }
  ```
----
- 配置Spring整合MyBatis - `spring-dao.xml`
  ```xml
    <?xml version="1.0" encoding="UTF-8"?>
    <beans xmlns="http://www.springframework.org/schema/beans"
        xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
        xmlns:context="http://www.springframework.org/schema/context"
        xsi:schemaLocation="http://www.springframework.org/schema/beans
        http://www.springframework.org/schema/beans/spring-beans.xsd
        http://www.springframework.org/schema/context
        https://www.springframework.org/schema/context/spring-context.xsd">

        <!-- 配置整合mybatis -->
        <!-- 1.关联数据库文件 -->
        <context:property-placeholder location="classpath:database.properties"/>

        <!-- 2.数据库连接池 -->
        <!--数据库连接池
            dbcp 半自动化操作 不能自动连接
            c3p0 自动化操作（自动的加载配置文件 并且设置到对象里面）
        -->
        <bean id="dataSource" class="com.mchange.v2.c3p0.ComboPooledDataSource">
            <!-- 配置连接池属性 -->
            <property name="driverClass" value="${jdbc.driver}"/>
            <property name="jdbcUrl" value="${jdbc.url}"/>
            <property name="user" value="${jdbc.username}"/>
            <property name="password" value="${jdbc.password}"/>

            <!-- c3p0连接池的私有属性 -->
            <property name="maxPoolSize" value="30"/>
            <property name="minPoolSize" value="10"/>
            <!-- 关闭连接后不自动commit -->
            <property name="autoCommitOnClose" value="false"/>
            <!-- 获取连接超时时间 -->
            <property name="checkoutTimeout" value="10000"/>
            <!-- 当获取连接失败重试次数 -->
            <property name="acquireRetryAttempts" value="2"/>
        </bean>

        <!-- 3.配置SqlSessionFactory对象 -->
        <bean id="sqlSessionFactory" class="org.mybatis.spring.SqlSessionFactoryBean">
            <!-- 注入数据库连接池 -->
            <property name="dataSource" ref="dataSource"/>
            <!-- 配置MyBaties全局配置文件:mybatis-config.xml -->
            <property name="configLocation" value="classpath:mybatis-config.xml"/>
        </bean>

        <!-- 4.配置扫描Dao接口包，动态实现Dao接口注入到spring容器中 -->
        <!--解释 ：https://www.cnblogs.com/jpfss/p/7799806.html-->
        <bean class="org.mybatis.spring.mapper.MapperScannerConfigurer">
            <!-- 注入sqlSessionFactory -->
            <property name="sqlSessionFactoryBeanName" value="sqlSessionFactory"/>
            <!-- 给出需要扫描Dao接口包 -->
            <property name="basePackage" value="com.springmvc.dao"/>
        </bean>

    </beans>
  ```
- 配置Spring整合Service - `spring-service.xml`
  ```xml
    <?xml version="1.0" encoding="UTF-8"?>
    <beans xmlns="http://www.springframework.org/schema/beans"
        xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
        xmlns:context="http://www.springframework.org/schema/context"
        xsi:schemaLocation="http://www.springframework.org/schema/beans
    http://www.springframework.org/schema/beans/spring-beans.xsd
    http://www.springframework.org/schema/context
    http://www.springframework.org/schema/context/spring-context.xsd">

        <!-- 扫描service相关的bean -->
        <context:component-scan base-package="com.springmvc.service" />

        <!--BookServiceImpl注入到IOC容器中-->
        <bean id="BookServiceImpl" class="com.springmvc.service.BookServiceImpl">
            <property name="bookMapper" ref="bookMapper"/>
        </bean>

        <!-- 配置事务管理器 -->
        <bean id="transactionManager" class="org.springframework.jdbc.datasource.DataSourceTransactionManager">
            <!-- 注入数据库连接池 -->
            <property name="dataSource" ref="dataSource" />
        </bean>

    </beans>
  ```
- SpringMVC层配置
  - `web.xml`配置
    ```xml
        <?xml version="1.0" encoding="UTF-8"?>
        <web-app xmlns="http://xmlns.jcp.org/xml/ns/javaee"
                xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
                xsi:schemaLocation="http://xmlns.jcp.org/xml/ns/javaee http://xmlns.jcp.org/xml/ns/javaee/web-app_4_0.xsd"
                version="4.0">

            <!--DispatcherServlet-->
            <servlet>
                <servlet-name>DispatcherServlet</servlet-name>
                <servlet-class>org.springframework.web.servlet.DispatcherServlet</servlet-class>
                <init-param>
                    <param-name>contextConfigLocation</param-name>
                    <!--一定要注意:我们这里加载的是总的配置文件，之前被这里坑了！-->
                    <param-value>classpath:applicationContext.xml</param-value>
                </init-param>
                <load-on-startup>1</load-on-startup>
            </servlet>
            <servlet-mapping>
                <servlet-name>DispatcherServlet</servlet-name>
                <url-pattern>/</url-pattern>
            </servlet-mapping>

            <!--encodingFilter-->
            <filter>
                <filter-name>encodingFilter</filter-name>
                <filter-class>
                    org.springframework.web.filter.CharacterEncodingFilter
                </filter-class>
                <init-param>
                    <param-name>encoding</param-name>
                    <param-value>utf-8</param-value>
                </init-param>
            </filter>
            <filter-mapping>
                <filter-name>encodingFilter</filter-name>
                <url-pattern>/*</url-pattern>
            </filter-mapping>

            <!--Session过期时间-->
            <session-config>
                <session-timeout>15</session-timeout>
            </session-config>

        </web-app>
    ```
  - `spring-mvc.xml`配置
    ```xml
        <?xml version="1.0" encoding="UTF-8"?>
        <beans xmlns="http://www.springframework.org/schema/beans"
            xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
            xmlns:context="http://www.springframework.org/schema/context"
            xmlns:mvc="http://www.springframework.org/schema/mvc"
            xsi:schemaLocation="http://www.springframework.org/schema/beans
        http://www.springframework.org/schema/beans/spring-beans.xsd
        http://www.springframework.org/schema/context
        http://www.springframework.org/schema/context/spring-context.xsd
        http://www.springframework.org/schema/mvc
        https://www.springframework.org/schema/mvc/spring-mvc.xsd">

            <!-- 配置SpringMVC -->
            <!-- 1.开启SpringMVC注解驱动 -->
            <mvc:annotation-driven />
            <!-- 2.静态资源默认servlet配置-->
            <mvc:default-servlet-handler/>

            <!-- 3.配置jsp 显示ViewResolver视图解析器 -->
            <bean class="org.springframework.web.servlet.view.InternalResourceViewResolver">
                <property name="viewClass" value="org.springframework.web.servlet.view.JstlView" />
                <property name="prefix" value="/WEB-INF/jsp/" />
                <property name="suffix" value=".jsp" />
            </bean>

            <!-- 4.扫描web相关的bean -->
            <context:component-scan base-package="com.springmvc.controller" />

        </beans>
    ```
- `applicationContext.xml`整合配置资源
  ```xml
    <?xml version="1.0" encoding="UTF-8"?>
    <beans xmlns="http://www.springframework.org/schema/beans"
        xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
        xsi:schemaLocation="http://www.springframework.org/schema/beans
        http://www.springframework.org/schema/beans/spring-beans.xsd">

        <import resource="spring/spring-dao.xml"/>
        <import resource="spring/spring-service.xml"/>
        <import resource="spring/spring-mvc.xml"/>

    </beans>
  ```
----
- 编写控制器
  ```java
    package com.springmvc.controller;

    import com.springmvc.pojo.Books;
    import com.springmvc.service.BookService;
    import org.springframework.beans.factory.annotation.Autowired;
    import org.springframework.beans.factory.annotation.Qualifier;
    import org.springframework.stereotype.Controller;
    import org.springframework.ui.Model;
    import org.springframework.web.bind.annotation.RequestMapping;

    import java.util.List;

    @Controller
    @RequestMapping("/book")
    public class BookController {

        @Autowired
        @Qualifier("BookServiceImpl")
        private BookService bookService;

        @RequestMapping("/allBook")
        public String list(Model model) {
            List<Books> list = bookService.queryAllBook();
            model.addAttribute("list", list);
            return "allBook";
        }
    }
  ```
- 编写简易的前端界面观察我们查询到的数据
  - `allBook.jsp`
    ```jsp
        <%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
        <%@ page contentType="text/html;charset=UTF-8" language="java" %>
        <html>
        <head>
            <title>书籍列表</title>
            <meta name="viewport" content="width=device-width, initial-scale=1.0">
            <!-- 引入 Bootstrap -->
            <link href="https://cdn.bootcss.com/bootstrap/3.3.7/css/bootstrap.min.css" rel="stylesheet">
        </head>
        <body>

        <div class="container">

            <div class="row clearfix">
                <div class="col-md-12 column">
                    <div class="page-header">
                        <h1>
                            <small>书籍列表 —— 显示所有书籍</small>
                        </h1>
                    </div>
                </div>
            </div>

            <div class="row">
                <div class="col-md-4 column">
                    <a class="btn btn-primary" href="${pageContext.request.contextPath}/book/toAddBook">新增</a>
                </div>
            </div>

            <div class="row clearfix">
                <div class="col-md-12 column">
                    <table class="table table-hover table-striped">
                        <thead>
                        <tr>
                            <th>书籍编号</th>
                            <th>书籍名字</th>
                            <th>书籍数量</th>
                            <th>书籍详情</th>
                            <th>操作</th>
                        </tr>
                        </thead>

                        <tbody>
                        <c:forEach var="book" items="${requestScope.get('list')}">
                            <tr>
                                <td>${book.getBookID()}</td>
                                <td>${book.getBookName()}</td>
                                <td>${book.getBookCounts()}</td>
                                <td>${book.getDetail()}</td>
                                <td>
                                    <a href="${pageContext.request.contextPath}/book/toUpdateBook?id=${book.getBookID()}">更改</a> |
                                    <a href="${pageContext.request.contextPath}/book/del/${book.getBookID()}">删除</a>
                                </td>
                            </tr>
                        </c:forEach>
                        </tbody>
                    </table>
                </div>
            </div>
        </div>

    ```
  - `index.jsp`
    ```jsp
        <%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
        <!DOCTYPE HTML>
        <html>
        <head>
        <title>首页</title>
        <style type="text/css">
            a {
            text-decoration: none;
            color: black;
            font-size: 18px;
            }
            h3 {
            width: 180px;
            height: 38px;
            margin: 100px auto;
            text-align: center;
            line-height: 38px;
            background: deepskyblue;
            border-radius: 4px;
            }
        </style>
        </head>
        <body>

        <h3>
        <a href="${pageContext.request.contextPath}/book/allBook">点击进入列表页</a>
        </h3>
        </body>
        </html>
    ```
- 额外注意
  配置好Tomcat，**并且进入Project Structure选项中的Artifacts选项卡中在WEB-INF中创建lib目录，将所有依赖添加进去**（针对IDEA用户），启动，Jumping~~😝


