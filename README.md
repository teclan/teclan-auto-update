# SpringBoot 使用记录

参考项目 [teclan-SpringBoot](https://github.com/teclan/teclan-SpringBoot)

## mybatis相关配置

- 1 ${classpath}/generatorConfig.xml 为自动生成sql的配置，注意配置一下几个地方

``` 

...
<jdbcConnection driverClass="com.mysql.cj.jdbc.Driver"
                        connectionURL="jdbc:mysql://localhost:3306/teclan?useUnicode=true&amp;characterEncoding=UTF-8&amp;useSSL=false&amp;serverTimezone=UTC"
                        userId="root"
                        password="root">
</jdbcConnection>
 ...
 
 <!-- 以下每个表都需要配置 -->
 <table tableName="users1" domainObjectName="Users1"
                enableCountByExample="false" enableUpdateByExample="false"
                enableDeleteByExample="false" enableSelectByExample="false"
                selectByExampleQueryId="false">
         </table>

...

```

- 2 配置之后，执行 `mvn mybatis-generator:generate` 即可生成实体类个接口


## 问题与解决方案

### 1.mysql无法连接导致的启动失败

详细的异常信息如下:
```
***************************
APPLICATION FAILED TO START
***************************

Description:

Failed to configure a DataSource: 'url' attribute is not specified and no embedded datasource could be configured.

Reason: Failed to determine a suitable driver class


Action:

Consider the following:
	If you want an embedded database (H2, HSQL or Derby), please put it on the classpath.
	If you have database settings to be loaded from a particular profile you may need to activate it (no profiles are currently active).


```

在多方查证后，需要在启动类的`@EnableAutoConfiguration`或`@SpringBootApplication中`添加
`exclude= {DataSourceAutoConfiguration.class}`，排除此类的autoconfig。启动以后就可以
正常运行。由于SpringBoot默认会自动扫描约定的数据库配置（约定的配置具体是什么，没有研究），但是项
目缺少默认的配置，导致无法读取相关配置，初始化连接失败。只要关闭自动的数据库配置扫描即可。

### 2.依赖版本号冲突

问题描述：

在 pom.xml 中引入 SpringBoot 的父pom,其中 pom.xml 的部分内容如下:

```
    ...
	<groupId>teclan.springboot</groupId>
	<artifactId>teclan-SpringBoot</artifactId>
	<version>0.0.1-SNAPSHOT</version>
	<packaging>jar</packaging>
	
	<parent>
		<groupId>org.springframework.boot</groupId>
		<artifactId>spring-boot-starter-parent</artifactId>
		<version>2.0.3.RELEASE</version>
	</parent>
	<dependencies>
	...
	<dependency>
    	<groupId>mysql</groupId>
    	<artifactId>mysql-connector-java</artifactId>
    	<version>8.0.11</version><!-- 此处会发出警告: Overriding managed version 5.1.46 for mysql-connector-java-->
    </dependency>
    ...
    </dependencies>	
	...
```
看到mysql的依赖有冲突，提示将会覆盖原来的 V5.1.46这个版本的依赖。其实这问题不大，只要覆盖就好，但是
我确实只有一个mysql的依赖项。根据经验，依赖的版本冲突会引发各种奇怪的事情，为弄清楚，开始寻找真相之路。

但是在pom.xml中确实找不到其他的mysql依赖，为了让自己更加确定，查看maven依赖树（执行 `mvn dependency:tree`）,
结果如下:
```
[INFO] teclan.springboot:teclan-SpringBoot:jar:0.0.1-SNAPSHOT
[INFO] +- org.springframework.boot:spring-boot-starter-web:jar:2.0.3.RELEASE:compile
[INFO] |  +- org.springframework.boot:spring-boot-starter:jar:2.0.3.RELEASE:compile
[INFO] |  |  +- org.springframework.boot:spring-boot:jar:2.0.3.RELEASE:compile
[INFO] |  |  +- org.springframework.boot:spring-boot-autoconfigure:jar:2.0.3.RELEASE:compile
[INFO] |  |  +- org.springframework.boot:spring-boot-starter-logging:jar:2.0.3.RELEASE:compile
[INFO] |  |  |  +- ch.qos.logback:logback-classic:jar:1.2.3:compile
[INFO] |  |  |  |  \- ch.qos.logback:logback-core:jar:1.2.3:compile
[INFO] |  |  |  +- org.apache.logging.log4j:log4j-to-slf4j:jar:2.10.0:compile
[INFO] |  |  |  |  \- org.apache.logging.log4j:log4j-api:jar:2.10.0:compile
[INFO] |  |  |  \- org.slf4j:jul-to-slf4j:jar:1.7.25:compile
[INFO] |  |  +- javax.annotation:javax.annotation-api:jar:1.3.2:compile
[INFO] |  |  \- org.yaml:snakeyaml:jar:1.19:runtime
[INFO] |  +- org.springframework.boot:spring-boot-starter-json:jar:2.0.3.RELEASE:compile
[INFO] |  |  +- com.fasterxml.jackson.core:jackson-databind:jar:2.9.6:compile
[INFO] |  |  |  +- com.fasterxml.jackson.core:jackson-annotations:jar:2.9.0:compile
[INFO] |  |  |  \- com.fasterxml.jackson.core:jackson-core:jar:2.9.6:compile
[INFO] |  |  +- com.fasterxml.jackson.datatype:jackson-datatype-jdk8:jar:2.9.6:compile
[INFO] |  |  +- com.fasterxml.jackson.datatype:jackson-datatype-jsr310:jar:2.9.6:compile
[INFO] |  |  \- com.fasterxml.jackson.module:jackson-module-parameter-names:jar:2.9.6:compile
[INFO] |  +- org.springframework.boot:spring-boot-starter-tomcat:jar:2.0.3.RELEASE:compile
[INFO] |  |  +- org.apache.tomcat.embed:tomcat-embed-core:jar:8.5.31:compile
[INFO] |  |  +- org.apache.tomcat.embed:tomcat-embed-el:jar:8.5.31:compile
[INFO] |  |  \- org.apache.tomcat.embed:tomcat-embed-websocket:jar:8.5.31:compile
[INFO] |  +- org.hibernate.validator:hibernate-validator:jar:6.0.10.Final:compile
[INFO] |  |  +- javax.validation:validation-api:jar:2.0.1.Final:compile
[INFO] |  |  +- org.jboss.logging:jboss-logging:jar:3.3.2.Final:compile
[INFO] |  |  \- com.fasterxml:classmate:jar:1.3.4:compile
[INFO] |  +- org.springframework:spring-web:jar:5.0.7.RELEASE:compile
[INFO] |  |  \- org.springframework:spring-beans:jar:5.0.7.RELEASE:compile
[INFO] |  \- org.springframework:spring-webmvc:jar:5.0.7.RELEASE:compile
[INFO] |     +- org.springframework:spring-aop:jar:5.0.7.RELEASE:compile
[INFO] |     +- org.springframework:spring-context:jar:5.0.7.RELEASE:compile
[INFO] |     \- org.springframework:spring-expression:jar:5.0.7.RELEASE:compile
[INFO] +- org.springframework.boot:spring-boot-starter-jdbc:jar:2.0.3.RELEASE:compile
[INFO] |  +- com.zaxxer:HikariCP:jar:2.7.9:compile
[INFO] |  \- org.springframework:spring-jdbc:jar:5.0.7.RELEASE:compile
[INFO] |     \- org.springframework:spring-tx:jar:5.0.7.RELEASE:compile
[INFO] +- org.springframework.boot:spring-boot-starter-test:jar:2.0.3.RELEASE:test
[INFO] |  +- org.springframework.boot:spring-boot-test:jar:2.0.3.RELEASE:test
[INFO] |  +- org.springframework.boot:spring-boot-test-autoconfigure:jar:2.0.3.RELEASE:test
[INFO] |  +- junit:junit:jar:4.12:test
[INFO] |  +- org.assertj:assertj-core:jar:3.9.1:test
[INFO] |  +- org.mockito:mockito-core:jar:2.15.0:test
[INFO] |  |  +- net.bytebuddy:byte-buddy:jar:1.7.11:test
[INFO] |  |  +- net.bytebuddy:byte-buddy-agent:jar:1.7.11:test
[INFO] |  |  \- org.objenesis:objenesis:jar:2.6:test
[INFO] |  +- org.hamcrest:hamcrest-core:jar:1.3:test
[INFO] |  +- org.hamcrest:hamcrest-library:jar:1.3:test
[INFO] |  +- org.skyscreamer:jsonassert:jar:1.5.0:test
[INFO] |  |  \- com.vaadin.external.google:android-json:jar:0.0.20131108.vaadin1:test
[INFO] |  +- org.springframework:spring-core:jar:5.0.7.RELEASE:compile
[INFO] |  |  \- org.springframework:spring-jcl:jar:5.0.7.RELEASE:compile
[INFO] |  +- org.springframework:spring-test:jar:5.0.7.RELEASE:test
[INFO] |  \- org.xmlunit:xmlunit-core:jar:2.5.1:test
[INFO] +- com.jayway.jsonpath:json-path:jar:2.4.0:test
[INFO] |  +- net.minidev:json-smart:jar:2.3:test
[INFO] |  |  \- net.minidev:accessors-smart:jar:1.2:test
[INFO] |  |     \- org.ow2.asm:asm:jar:5.0.4:test
[INFO] |  \- org.slf4j:slf4j-api:jar:1.7.25:compile
[INFO] +- com.alibaba:fastjson:jar:1.1.39:compile
[INFO] \- mysql:mysql-connector-java:jar:8.0.11:compile
[INFO]    \- com.google.protobuf:protobuf-java:jar:2.6.0:runtime
```
以上，仅在倒数第二行发现有mysql的驱动依赖，并没有找到版本为`5.1.46`的mysql依赖。
于是，锁定SpringBoot的父pom，于是，开始定位包，在Idea中右键pom.xml,选择 `Show Effective pom`,
结果如下：

```
...
<properties>
    <activemq.version>5.15.4</activemq.version>
    <antlr2.version>2.7.7</antlr2.version>
    <appengine-sdk.version>1.9.64</appengine-sdk.version>
    <artemis.version>2.4.0</artemis.version>
    <aspectj.version>1.8.13</aspectj.version>
    <assertj.version>3.9.1</assertj.version>
    <atomikos.version>4.0.6</atomikos.version>
    <bitronix.version>2.1.4</bitronix.version>
    <build-helper-maven-plugin.version>3.0.0</build-helper-maven-plugin.version>
    <byte-buddy.version>1.7.11</byte-buddy.version>
    <caffeine.version>2.6.2</caffeine.version>
    <cassandra-driver.version>3.4.0</cassandra-driver.version>
    <classmate.version>1.3.4</classmate.version>
    <commons-codec.version>1.11</commons-codec.version>
    <commons-dbcp2.version>2.2.0</commons-dbcp2.version>
    <commons-lang3.version>3.7</commons-lang3.version>
    <commons-pool.version>1.6</commons-pool.version>
    <commons-pool2.version>2.5.0</commons-pool2.version>
    <couchbase-cache-client.version>2.1.0</couchbase-cache-client.version>
    <couchbase-client.version>2.5.9</couchbase-client.version>
    <derby.version>10.14.1.0</derby.version>
    <dom4j.version>1.6.1</dom4j.version>
    <dropwizard-metrics.version>3.2.6</dropwizard-metrics.version>
    <ehcache.version>2.10.5</ehcache.version>
    <ehcache3.version>3.5.2</ehcache3.version>
    <elasticsearch.version>5.6.10</elasticsearch.version>
    <embedded-mongo.version>2.0.3</embedded-mongo.version>
    <exec-maven-plugin.version>1.5.0</exec-maven-plugin.version>
    <flatten-maven-plugin.version>1.0.0</flatten-maven-plugin.version>
    <flyway.version>5.0.7</flyway.version>
    <freemarker.version>2.3.28</freemarker.version>
    <git-commit-id-plugin.version>2.2.3</git-commit-id-plugin.version>
    <glassfish-el.version>3.0.0</glassfish-el.version>
    <groovy.version>2.4.15</groovy.version>
    <gson.version>2.8.5</gson.version>
    <h2.version>1.4.197</h2.version>
    <hamcrest.version>1.3</hamcrest.version>
    <hazelcast-hibernate5.version>1.2.3</hazelcast-hibernate5.version>
    <hazelcast.version>3.9.4</hazelcast.version>
    <hibernate-jpa-2.1-api.version>1.0.2.Final</hibernate-jpa-2.1-api.version>
    <hibernate-validator.version>6.0.10.Final</hibernate-validator.version>
    <hibernate.version>5.2.17.Final</hibernate.version>
    <hikaricp.version>2.7.9</hikaricp.version>
    <hsqldb.version>2.4.1</hsqldb.version>
    <htmlunit.version>2.29</htmlunit.version>
    <httpasyncclient.version>4.1.3</httpasyncclient.version>
    <httpclient.version>4.5.5</httpclient.version>
    <httpcore.version>4.4.9</httpcore.version>
    <infinispan.version>9.1.7.Final</infinispan.version>
    <influxdb-java.version>2.9</influxdb-java.version>
    <jackson.version>2.9.6</jackson.version>
    <janino.version>3.0.8</janino.version>
    <java.version>1.8</java.version>
    <javax-annotation.version>1.3.2</javax-annotation.version>
    <javax-cache.version>1.1.0</javax-cache.version>
    <javax-jaxb.version>2.3.0</javax-jaxb.version>
    <javax-jms.version>2.0.1</javax-jms.version>
    <javax-json.version>1.1.2</javax-json.version>
    <javax-jsonb.version>1.0</javax-jsonb.version>
    <javax-mail.version>1.6.1</javax-mail.version>
    <javax-money.version>1.0.3</javax-money.version>
    <javax-transaction.version>1.2</javax-transaction.version>
    <javax-validation.version>2.0.1.Final</javax-validation.version>
    <jaxen.version>1.1.6</jaxen.version>
    <jaybird.version>3.0.4</jaybird.version>
    <jboss-logging.version>3.3.2.Final</jboss-logging.version>
    <jboss-transaction-spi.version>7.6.0.Final</jboss-transaction-spi.version>
    <jdom2.version>2.0.6</jdom2.version>
    <jedis.version>2.9.0</jedis.version>
    <jersey.version>2.26</jersey.version>
    <jest.version>5.3.3</jest.version>
    <jetty-el.version>8.5.24.2</jetty-el.version>
    <jetty-jsp.version>2.2.0.v201112011158</jetty-jsp.version>
    <jetty.version>9.4.11.v20180605</jetty.version>
    <jmustache.version>1.14</jmustache.version>
    <jna.version>4.5.1</jna.version>
    <joda-time.version>2.9.9</joda-time.version>
    <johnzon-jsonb.version>1.1.7</johnzon-jsonb.version>
    <jolokia.version>1.5.0</jolokia.version>
    <jooq.version>3.10.7</jooq.version>
    <json-path.version>2.4.0</json-path.version>
    <jsonassert.version>1.5.0</jsonassert.version>
    <jstl.version>1.2</jstl.version>
    <jtds.version>1.3.1</jtds.version>
    <junit-jupiter.version>5.1.1</junit-jupiter.version>
    <junit-platform.version>1.1.0</junit-platform.version>
    <junit.version>4.12</junit.version>
    <kafka.version>1.0.1</kafka.version>
    <kotlin.version>1.2.41</kotlin.version>
    <lettuce.version>5.0.4.RELEASE</lettuce.version>
    <liquibase.version>3.5.5</liquibase.version>
    <log4j2.version>2.10.0</log4j2.version>
    <logback.version>1.2.3</logback.version>
    <lombok.version>1.16.22</lombok.version>
    <mariadb.version>2.2.5</mariadb.version>
    <maven-antrun-plugin.version>1.8</maven-antrun-plugin.version>
    <maven-assembly-plugin.version>3.1.0</maven-assembly-plugin.version>
    <maven-clean-plugin.version>3.0.0</maven-clean-plugin.version>
    <maven-compiler-plugin.version>3.7.0</maven-compiler-plugin.version>
    <maven-dependency-plugin.version>3.0.2</maven-dependency-plugin.version>
    <maven-deploy-plugin.version>2.8.2</maven-deploy-plugin.version>
    <maven-eclipse-plugin.version>2.10</maven-eclipse-plugin.version>
    <maven-enforcer-plugin.version>3.0.0-M1</maven-enforcer-plugin.version>
    <maven-failsafe-plugin.version>2.21.0</maven-failsafe-plugin.version>
    <maven-help-plugin.version>2.2</maven-help-plugin.version>
    <maven-install-plugin.version>2.5.2</maven-install-plugin.version>
    <maven-invoker-plugin.version>3.1.0</maven-invoker-plugin.version>
    <maven-jar-plugin.version>3.0.2</maven-jar-plugin.version>
    <maven-javadoc-plugin.version>3.0.0</maven-javadoc-plugin.version>
    <maven-resources-plugin.version>3.0.1</maven-resources-plugin.version>
    <maven-shade-plugin.version>2.4.3</maven-shade-plugin.version>
    <maven-site-plugin.version>3.6</maven-site-plugin.version>
    <maven-source-plugin.version>3.0.1</maven-source-plugin.version>
    <maven-surefire-plugin.version>2.21.0</maven-surefire-plugin.version>
    <maven-war-plugin.version>3.1.0</maven-war-plugin.version>
    <maven.compiler.source>1.8</maven.compiler.source>
    <maven.compiler.target>1.8</maven.compiler.target>
    <micrometer.version>1.0.5</micrometer.version>
    <mockito.version>2.15.0</mockito.version>
    <mongo-driver-reactivestreams.version>1.7.1</mongo-driver-reactivestreams.version>
    <mongodb.version>3.6.4</mongodb.version>
    <mssql-jdbc.version>6.2.2.jre8</mssql-jdbc.version>
    
    <!-- ######### 原来这个版本号在这里定义了 ############-->
    <mysql.version>5.1.46</mysql.version> 
    
    <narayana.version>5.8.2.Final</narayana.version>
    <nekohtml.version>1.9.22</nekohtml.version>
    <neo4j-ogm.version>3.1.0</neo4j-ogm.version>
    <netty.version>4.1.25.Final</netty.version>
    <nio-multipart-parser.version>1.1.0</nio-multipart-parser.version>
    <postgresql.version>42.2.2</postgresql.version>
    <project.build.sourceEncoding>UTF-8</project.build.sourceEncoding>
    <project.reporting.outputEncoding>UTF-8</project.reporting.outputEncoding>
    <quartz.version>2.3.0</quartz.version>
    <querydsl.version>4.1.4</querydsl.version>
    <rabbit-amqp-client.version>5.1.2</rabbit-amqp-client.version>
    <reactive-streams.version>1.0.2</reactive-streams.version>
    <reactor-bom.version>Bismuth-SR10</reactor-bom.version>
    <resource.delimiter>@</resource.delimiter>
    <rest-assured.version>3.0.7</rest-assured.version>
    <rxjava-adapter.version>1.2.1</rxjava-adapter.version>
    <rxjava.version>1.3.8</rxjava.version>
    <rxjava2.version>2.1.14</rxjava2.version>
    <selenium-htmlunit.version>2.29.3</selenium-htmlunit.version>
    <selenium.version>3.9.1</selenium.version>
    <sendgrid.version>4.1.2</sendgrid.version>
    <servlet-api.version>3.1.0</servlet-api.version>
    <simple-json.version>1.1.1</simple-json.version>
    <slf4j.version>1.7.25</slf4j.version>
    <snakeyaml.version>1.19</snakeyaml.version>
    <solr.version>6.6.4</solr.version>
    <spring-amqp.version>2.0.4.RELEASE</spring-amqp.version>
    <spring-batch.version>4.0.1.RELEASE</spring-batch.version>
    <spring-cloud-connectors.version>2.0.2.RELEASE</spring-cloud-connectors.version>
    <spring-data-releasetrain.version>Kay-SR8</spring-data-releasetrain.version>
    <spring-hateoas.version>0.24.0.RELEASE</spring-hateoas.version>
    <spring-integration.version>5.0.6.RELEASE</spring-integration.version>
    <spring-kafka.version>2.1.7.RELEASE</spring-kafka.version>
    <spring-ldap.version>2.3.2.RELEASE</spring-ldap.version>
    <spring-plugin.version>1.2.0.RELEASE</spring-plugin.version>
    <spring-restdocs.version>2.0.1.RELEASE</spring-restdocs.version>
    <spring-retry.version>1.2.2.RELEASE</spring-retry.version>
    <spring-security.version>5.0.6.RELEASE</spring-security.version>
    <spring-session-bom.version>Apple-SR3</spring-session-bom.version>
    <spring-ws.version>3.0.1.RELEASE</spring-ws.version>
    <spring.version>5.0.7.RELEASE</spring.version>
    <sqlite-jdbc.version>3.21.0.1</sqlite-jdbc.version>
    <statsd-client.version>3.1.0</statsd-client.version>
    <sun-mail.version>1.6.1</sun-mail.version>
    <thymeleaf-extras-data-attribute.version>2.0.1</thymeleaf-extras-data-attribute.version>
    <thymeleaf-extras-java8time.version>3.0.1.RELEASE</thymeleaf-extras-java8time.version>
    <thymeleaf-extras-springsecurity4.version>3.0.2.RELEASE</thymeleaf-extras-springsecurity4.version>
    <thymeleaf-layout-dialect.version>2.3.0</thymeleaf-layout-dialect.version>
    <thymeleaf.version>3.0.9.RELEASE</thymeleaf.version>
    <tomcat.version>8.5.31</tomcat.version>
    <unboundid-ldapsdk.version>4.0.6</unboundid-ldapsdk.version>
    <undertow.version>1.4.25.Final</undertow.version>
    <versions-maven-plugin.version>2.3</versions-maven-plugin.version>
    <webjars-hal-browser.version>3325375</webjars-hal-browser.version>
    <webjars-locator-core.version>0.35</webjars-locator-core.version>
    <wsdl4j.version>1.6.3</wsdl4j.version>
    <xml-apis.version>1.4.01</xml-apis.version>
    <xml-maven-plugin.version>1.0.1</xml-maven-plugin.version>
    <xmlunit2.version>2.5.1</xmlunit2.version>
  </properties>
...

```
至此，确定了这个版本覆盖来源是StringBoot的父pom集成了很多默认的配置。集合我们自己的
项目需要，可覆盖他原来的依赖版本，在版本后面加上注解即可，例如:

```
    <dependency>
        <groupId>mysql</groupId>
        <artifactId>mysql-connector-java</artifactId>
        <version>8.0.11</version><!--$NO-MVN-MAN-VER$-->
    </dependency>
```

## Mybatis生成的Mapper文件无法注入
 
 提示如下：
 
 ``` 
org.springframework.beans.factory.UnsatisfiedDependencyException: 
Error creating bean with name 'userController': Unsatisfied dependency
 expressed through field 'users1Mapper'; nested exception is 
 org.springframework.beans.factory.UnsatisfiedDependencyException:
  Error creating bean with name 'users1Mapper' defined in file 
  [E:\tanbingjian\Depository\Codes\opensources\teclan-SpringBoot\target
  \classes\teclan\springboot\dao\Users1Mapper.class]: 
  Unsatisfied dependency expressed through bean property 
  'sqlSessionFactory'; nested exception is org.springframework.beans.
  factory.BeanCreationException: Error creating bean with name 
  'sqlSessionFactory' defined in class path resource 
  [org/mybatis/spring/boot/autoconfigure/MybatisAutoConfiguration.class]: 
  Bean instantiation via factory method failed; nested exception is 
  org.springframework.beans.BeanInstantiationException: Failed to 
  instantiate [org.apache.ibatis.session.SqlSessionFactory]: Factory method 
  'sqlSessionFactory' threw exception; nested exception 
  is org.springframework.core.NestedIOException: Failed to parse mapping resource: 
  'file [E:\tanbingjian\Depository\Codes\opensources\teclan-SpringBoot\target\classes\generatorConfig.xml]'; 
  nested exception is org.apache.ibatis.builder.BuilderException: Error parsing 
  Mapper XML. The XML location is 'file [E:\tanbingjian\Depository\Codes\opensources\teclan-SpringBoot\target\
  classes\generatorConfig.xml]'. Cause: java.lang.NullPointerException
``` 
 
 解决办法：
 
 添加一下配置:
 
 ```
 
	@Bean(name="dataSource")
	public DataSource getDataSource() {
		HikariConfig config = new HikariConfig();
		config.setDriverClassName(driver);
		config.setJdbcUrl(url);
		config.setUsername(username);
		config.setPassword(password);
		return new HikariDataSource(config);
	}

	@Bean(name="sqlSessionFactory")
	public SqlSessionFactory getSqlSessionFactory() throws Exception {
		SqlSessionFactoryBean sqlSessionFactoryBean = new SqlSessionFactoryBean();
		sqlSessionFactoryBean.setDataSource(getDataSource());
		return sqlSessionFactoryBean.getObject();
	}
```