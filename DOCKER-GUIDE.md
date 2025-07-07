# Docker构建与运行指南

## 代码质量与可维护性建议

为了提高代码质量和可维护性，我们建议以下最佳实践：

1. **Docker相关**：
   - 使用多阶段构建减小镜像大小
   - 为环境变量设置默认值，增强容器配置的灵活性
   - 考虑使用Docker健康检查确保服务正常运行
   - 实现优雅关闭以避免数据丢失

2. **应用架构**：
   - 将配置外部化，避免硬编码
   - 实现适当的日志记录，便于问题排查
   - 考虑使用Spring Profiles管理不同环境的配置

3. **安全性**：
   - 避免在代码或配置文件中硬编码敏感信息
   - 考虑使用Docker Secrets或环境变量管理密码
   - 定期更新依赖以修复安全漏洞

4. **性能优化**：
   - 配置适当的JVM参数优化性能
   - 考虑使用连接池管理数据库连接
   - 实现缓存机制减少数据库负载

### 改进示例：Docker Compose配置与Dockerfile优化

以下是一个改进的docker-compose.yml示例，实现了健康检查和优雅关闭：

```yaml
version: '3.8'

services:
  mysql:
    image: mysql:8.0
    container_name: ocg-mysql
    restart: always
    environment:
      MYSQL_ROOT_PASSWORD: ${MYSQL_ROOT_PASSWORD:-Ee123456}
      MYSQL_DATABASE: ${MYSQL_DATABASE:-OCGtradingSystem}
    ports:
      - "${MYSQL_PORT:-3307}:3306"
    volumes:
      - mysql-data:/var/lib/mysql
      - ./database:/docker-entrypoint-initdb.d
    command: --character-set-server=utf8mb4 --collation-server=utf8mb4_unicode_ci
    networks:
      - ocg-network
    healthcheck:
      test: ["CMD", "mysqladmin", "ping", "-h", "localhost", "-u", "root", "-p${MYSQL_ROOT_PASSWORD:-Ee123456}"]
      interval: 10s
      timeout: 5s
      retries: 5
      start_period: 30s

  app:
    build:
      context: .
      dockerfile: Dockerfile
    container_name: ocg-app
    restart: always
    depends_on:
      mysql:
        condition: service_healthy
    ports:
      - "${APP_PORT:-8080}:8080"
    environment:
      SPRING_DATASOURCE_URL: jdbc:mysql://mysql:3306/${MYSQL_DATABASE:-OCGtradingSystem}?useSSL=false&allowPublicKeyRetrieval=true
      SPRING_DATASOURCE_USERNAME: root
      SPRING_DATASOURCE_PASSWORD: ${MYSQL_ROOT_PASSWORD:-Ee123456}
      JAVA_OPTS: "-Xms256m -Xmx512m -XX:+UseG1GC"
    networks:
      - ocg-network
    healthcheck:
      test: ["CMD", "curl", "-f", "http://localhost:8080/actuator/health"]
      interval: 30s
      timeout: 10s
      retries: 3
      start_period: 40s
    stop_grace_period: 30s

networks:
  ocg-network:
    driver: bridge

volumes:
  mysql-data:
```

这个改进版本包含以下增强：

1. 使用环境变量替换硬编码值，并提供默认值
2. 为MySQL和应用添加健康检查
3. 配置应用依赖于MySQL的健康状态
4. 添加JVM优化参数
5. 设置优雅关闭的等待时间

注意：要使用应用健康检查，需要在项目中添加Spring Boot Actuator依赖：

```xml
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-actuator</artifactId>
</dependency>
```

#### Dockerfile优化

以下是一个优化的Dockerfile示例，提高了构建效率和安全性：

```dockerfile
# 构建阶段
FROM maven:3.8.4-openjdk-8 AS build

WORKDIR /app

# 首先只复制pom.xml以利用Docker缓存机制
COPY pom.xml .
# 预先下载依赖
RUN mvn dependency:go-offline

# 然后复制源代码
COPY src ./src

# 构建应用
RUN mvn clean package -DskipTests

# 运行阶段使用较小的基础镜像
FROM openjdk:8-jre-slim

# 创建非root用户
RUN groupadd -r appuser && useradd -r -g appuser appuser

WORKDIR /app

# 从构建阶段复制构建好的jar文件
COPY --from=build /app/target/ocgtradingsystem-0.0.1-SNAPSHOT.jar app.jar

# 创建存放图片的目录并设置权限
RUN mkdir -p /app/photos && chown -R appuser:appuser /app
COPY --chown=appuser:appuser photos /app/photos

# 切换到非root用户
USER appuser

# 暴露应用端口
EXPOSE 8080

# 设置健康检查
HEALTHCHECK --interval=30s --timeout=10s --start-period=40s --retries=3 \
  CMD curl -f http://localhost:8080/actuator/health || exit 1

# 启动应用，添加JVM参数
ENTRYPOINT ["java", "-XX:+UseG1GC", "-Xms256m", "-Xmx512m", "-jar", "app.jar"]
```

这个优化版本包含以下改进：

1. **利用Docker缓存机制**：先复制pom.xml并下载依赖，再复制源代码，这样在源代码变更但依赖不变时可以重用缓存
2. **安全性增强**：使用非root用户运行应用
3. **添加健康检查**：在容器级别添加健康检查
4. **JVM优化**：添加内存和垃圾回收器配置
5. **权限管理**：正确设置文件和目录的所有权

#### 应用架构优化建议

除了Docker配置优化外，以下是一些Spring Boot应用架构优化建议：

1. **分层架构**：
   - 遵循清晰的分层架构（控制器、服务、数据访问层）
   - 使用接口定义服务契约，提高可测试性
   - 实现依赖注入，降低组件间耦合

2. **配置管理**：
   - 使用`@ConfigurationProperties`绑定配置属性
   - 为不同环境创建配置文件（dev、test、prod）
   - 示例：
     ```java
     @Configuration
     @ConfigurationProperties(prefix = "app")
     public class AppProperties {
         private String uploadDir;
         private int maxFileSize;
         // getters and setters
     }
     ```

3. **异常处理**：
   - 实现全局异常处理器
   - 返回统一的错误响应格式
   - 示例：
     ```java
     @RestControllerAdvice
     public class GlobalExceptionHandler {
         @ExceptionHandler(Exception.class)
         public ResponseEntity<ApiError> handleException(Exception ex) {
             ApiError error = new ApiError(HttpStatus.INTERNAL_SERVER_ERROR, ex.getMessage());
             return new ResponseEntity<>(error, HttpStatus.INTERNAL_SERVER_ERROR);
         }
     }
     ```

4. **API文档**：
   - 使用Knife4j/Swagger注解详细描述API
   - 为所有API端点添加适当的文档
   - 示例：
     ```java
     @Api(tags = "卡牌管理")
     @RestController
     @RequestMapping("/api/cards")
     public class CardController {
         @ApiOperation(value = "获取卡牌列表", notes = "分页获取所有卡牌信息")
         @GetMapping
         public Result<IPage<Card>> list(CardQuery query) {
             // 实现代码
         }
     }
     ```

5. **日志记录**：
   - 使用SLF4J进行日志记录
   - 配置适当的日志级别
   - 实现MDC（Mapped Diagnostic Context）跟踪请求
   - 示例：
     ```java
     @Component
     public class RequestLoggingFilter extends OncePerRequestFilter {
         @Override
         protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) {
             MDC.put("requestId", UUID.randomUUID().toString());
             try {
                 filterChain.doFilter(request, response);
             } finally {
                 MDC.clear();
             }
         }
     }
     ```

#### 安全性增强建议

为了提高应用的安全性，建议实施以下措施：

1. **HTTPS配置**：
   - 配置SSL/TLS以启用HTTPS
   - 在application.yml中添加以下配置：
     ```yaml
     server:
       port: 8080
       ssl:
         key-store: classpath:keystore.p12
         key-store-password: ${SSL_KEY_STORE_PASSWORD}
         key-store-type: PKCS12
         key-alias: tomcat
     ```

2. **安全头部**：
   - 添加安全相关的HTTP头部
   - 示例配置：
     ```java
     @Configuration
     public class WebSecurityConfig {
         @Bean
         public FilterRegistrationBean<Filter> securityHeadersFilter() {
             FilterRegistrationBean<Filter> registrationBean = new FilterRegistrationBean<>();
             registrationBean.setFilter((request, response, chain) -> {
                 HttpServletResponse httpResponse = (HttpServletResponse) response;
                 httpResponse.setHeader("X-Content-Type-Options", "nosniff");
                 httpResponse.setHeader("X-Frame-Options", "DENY");
                 httpResponse.setHeader("X-XSS-Protection", "1; mode=block");
                 httpResponse.setHeader("Strict-Transport-Security", "max-age=31536000; includeSubDomains");
                 chain.doFilter(request, response);
             });
             registrationBean.addUrlPatterns("/*");
             return registrationBean;
         }
     }
     ```

3. **输入验证**：
   - 对所有用户输入进行验证
   - 使用Bean Validation（JSR-380）
   - 示例：
     ```java
     public class CardDTO {
         @NotBlank(message = "卡牌名称不能为空")
         private String name;
         
         @Min(value = 0, message = "价格不能为负数")
         private BigDecimal price;
         
         // getters and setters
     }
     ```

4. **敏感数据保护**：
   - 使用加密存储敏感信息
   - 示例服务：
     ```java
     @Service
     public class EncryptionService {
         private final String secretKey;
         
         public EncryptionService(@Value("${app.encryption.secret-key}") String secretKey) {
             this.secretKey = secretKey;
         }
         
         public String encrypt(String data) {
             // 实现加密逻辑
         }
         
         public String decrypt(String encryptedData) {
             // 实现解密逻辑
         }
     }
     ```

5. **安全审计**：
   - 记录关键操作的审计日志
   - 示例：
     ```java
     @Aspect
     @Component
     public class AuditLogAspect {
         private final Logger logger = LoggerFactory.getLogger(AuditLogAspect.class);
         
         @Around("@annotation(auditLog)")
         public Object logAuditEvent(ProceedingJoinPoint joinPoint, AuditLog auditLog) throws Throwable {
             String username = SecurityContextHolder.getContext().getAuthentication().getName();
             String action = auditLog.action();
             logger.info("User {} performing action: {}", username, action);
             return joinPoint.proceed();
         }
     }
     ```

#### 性能优化建议

为了提高应用的性能，建议实施以下优化措施：

1. **数据库优化**：
   - 使用适当的索引
   - 优化SQL查询
   - 示例MyBatis-Plus配置：
     ```java
     @Configuration
     public class MybatisPlusConfig {
         @Bean
         public MybatisPlusInterceptor mybatisPlusInterceptor() {
             MybatisPlusInterceptor interceptor = new MybatisPlusInterceptor();
             // 添加分页插件
             interceptor.addInnerInterceptor(new PaginationInnerInterceptor(DbType.MYSQL));
             // 添加防止全表更新与删除插件
             interceptor.addInnerInterceptor(new BlockAttackInnerInterceptor());
             return interceptor;
         }
     }
     ```

2. **缓存实现**：
   - 使用Spring Cache或Redis缓存频繁访问的数据
   - 示例配置：
     ```java
     @Configuration
     @EnableCaching
     public class CacheConfig {
         @Bean
         public CacheManager cacheManager(RedisConnectionFactory redisConnectionFactory) {
             RedisCacheConfiguration config = RedisCacheConfiguration.defaultCacheConfig()
                 .entryTtl(Duration.ofMinutes(10))
                 .serializeKeysWith(RedisSerializationContext.SerializationPair.fromSerializer(new StringRedisSerializer()))
                 .serializeValuesWith(RedisSerializationContext.SerializationPair.fromSerializer(new GenericJackson2JsonRedisSerializer()));
             
             return RedisCacheManager.builder(redisConnectionFactory)
                 .cacheDefaults(config)
                 .build();
         }
     }
     ```

3. **连接池配置**：
   - 优化数据库连接池
   - 在application.yml中添加：
     ```yaml
     spring:
       datasource:
         hikari:
           maximum-pool-size: 10
           minimum-idle: 5
           idle-timeout: 30000
           connection-timeout: 30000
           max-lifetime: 1800000
     ```

4. **异步处理**：
   - 使用Spring的@Async注解处理耗时操作
   - 示例配置：
     ```java
     @Configuration
     @EnableAsync
     public class AsyncConfig {
         @Bean
         public Executor taskExecutor() {
             ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
             executor.setCorePoolSize(5);
             executor.setMaxPoolSize(10);
             executor.setQueueCapacity(25);
             executor.setThreadNamePrefix("AppAsync-");
             executor.initialize();
             return executor;
         }
     }
     ```

5. **资源压缩**：
   - 启用HTTP响应压缩
   - 在application.yml中添加：
     ```yaml
     server:
       compression:
         enabled: true
         mime-types: application/json,application/xml,text/html,text/plain
         min-response-size: 2048
     ```

## 问题修复

我们修复了Docker镜像构建过程中的一个问题，该问题导致了"no main manifest attribute, in app.jar"错误。修复包括：

1. 从pom.xml中移除了`<skip>true</skip>`配置，这允许Maven正确创建可执行的JAR文件
2. 在Dockerfile中指定了确切的JAR文件名称，而不是使用通配符
3. 添加了调试步骤，以便在构建过程中查看生成的文件

## 使用Docker Compose构建和运行

推荐使用Docker Compose来构建和运行整个应用，这将同时启动MySQL数据库和应用服务：

```bash
# 在项目根目录下执行
docker-compose up --build
```

这个命令会：
1. 构建应用镜像
2. 启动MySQL容器
3. 启动应用容器
4. 将应用映射到本地的8080端口

## 单独构建和运行Docker镜像

如果您想单独构建和运行应用镜像（不包括MySQL），可以使用以下命令：

```bash
# 构建镜像
docker build -t ocgtradingsystem .

# 运行镜像
docker run -p 8080:8080 ocgtradingsystem
```

注意：单独运行应用容器时，您需要确保MySQL数据库可用，并且应用能够连接到它。

## 访问应用

应用成功启动后，可以通过以下地址访问：

- 应用访问地址：http://localhost:8080
- API文档地址：http://localhost:8080/doc.html

## 故障排除

如果您在构建或运行过程中遇到问题，可以尝试以下步骤：

1. 检查Docker日志：
   ```bash
   docker logs ocg-app
   ```

2. 确认JAR文件是否正确构建：
   ```bash
   # 进入构建容器
   docker run --rm -it --entrypoint sh ocgtradingsystem
   # 查看JAR文件
   ls -la /app
   ```

3. 确认数据库连接：
   ```bash
   # 检查MySQL容器是否运行
   docker ps | grep ocg-mysql
   ```

4. 解决端口冲突问题：
   如果遇到以下错误：
   ```
   Error response from daemon: ports are not available: exposing port TCP 0.0.0.0:3306 -> 127.0.0.1:0: listen tcp 0.0.0.0:3306: bind: Only one usage of each socket address (protocol/network address/port) is normally permitted.
   ```
   
   这表明MySQL默认的3306端口已被占用。我们已经将docker-compose.yml中的端口映射修改为3307:3306，这样MySQL容器内部仍使用3306端口，但映射到主机的3307端口。
   
   如果您仍然遇到端口冲突，可以：
   - 检查本地是否有MySQL服务正在运行，可以临时停止它
   - 修改docker-compose.yml中的端口映射，使用其他未被占用的端口
   - 使用以下命令查看当前占用的端口：
     ```bash
     # Windows
     netstat -ano | findstr 3306
     
     # Linux/Mac
     netstat -tulpn | grep 3306
     ```