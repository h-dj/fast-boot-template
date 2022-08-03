## Spring Boot 单体脚手架


### 项目集成依赖
- Druid
- Mybatis-Plus
- Logback 日志配置
- 集成 Swagger API 文档
- 整合 Redis & Redisson 



### Maven Archetype项目

#### 安装
- 构建
```text
mvn archetype:create-from-project -Darchetype.properties=archetype.properties
```
- 安装到本地
```text
# ./target/generated-sources/archetype 目录下执行
mvn install
```

#### 生成项目
```text
mvn archetype:generate \
    -DarchetypeGroupId=cn.hdj \
    -DarchetypeArtifactId=x-boot-template-archetype \
    -DarchetypeVersion=0.0.1-SNAPSHOT
```