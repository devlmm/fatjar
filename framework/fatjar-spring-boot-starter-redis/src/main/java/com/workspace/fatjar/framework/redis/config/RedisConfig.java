package com.workspace.fatjar.framework.redis.config;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.PropertyAccessor;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.jsontype.impl.LaissezFaireSubTypeValidator;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.AutoConfigureBefore;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.data.redis.RedisAutoConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.serializer.GenericJackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;

/**
 * Redis 配置
 * <p>
 * 职责：注册类型化 RedisTemplate&lt;String, Object&gt;（key 用 String 序列化，value 用 JSON 序列化）
 *       与 StringRedisTemplate，覆盖 Spring Boot 默认的 JDK 序列化方案，避免乱码与跨语言不可读。
 * <p>
 * 说明：
 *   - 通过 @AutoConfigureBefore(RedisAutoConfiguration.class) 在 Spring Boot 默认 Redis 自动配置之前装配，
 *     配合 @ConditionalOnMissingBean 让默认配置检测到已存在 Bean 后跳过，避免重复注册。
 *   - RedissonClient 由 redisson-spring-boot-starter 自动装配，此处不重复。
 *   - GenericJackson2JsonRedisSerializer 会将类型信息写入 JSON（@class 字段），
 *     便于反序列化为原对象，但要求对象可被 Jackson 序列化。
 *
 * @author fatjar
 * @since 1.0.0
 */
@AutoConfiguration
@AutoConfigureBefore(RedisAutoConfiguration.class)
public class RedisConfig {

    /**
     * 注册类型化 RedisTemplate
     * <p>
     * key 与 hashKey 使用 StringRedisSerializer（避免乱码）；
     * value 与 hashValue 使用 GenericJackson2JsonRedisSerializer（JSON 可读，含类型信息）。
     *
     * @param factory Redis 连接工厂（由 Spring Boot 根据 spring.data.redis 配置创建）
     * @return RedisTemplate 实例
     */
    @Bean
    @ConditionalOnMissingBean(name = "redisTemplate")
    public RedisTemplate<String, Object> redisTemplate(RedisConnectionFactory factory) {
        RedisTemplate<String, Object> template = new RedisTemplate<>();
        template.setConnectionFactory(factory);

        // 构造带类型信息的 ObjectMapper，使 JSON 反序列化为原对象
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.setVisibility(PropertyAccessor.ALL, JsonAutoDetect.Visibility.ANY);
        objectMapper.activateDefaultTyping(LaissezFaireSubTypeValidator.instance,
                ObjectMapper.DefaultTyping.NON_FINAL);

        StringRedisSerializer keySerializer = new StringRedisSerializer();
        GenericJackson2JsonRedisSerializer valueSerializer = new GenericJackson2JsonRedisSerializer(objectMapper);

        template.setKeySerializer(keySerializer);
        template.setHashKeySerializer(keySerializer);
        template.setValueSerializer(valueSerializer);
        template.setHashValueSerializer(valueSerializer);
        template.afterPropertiesSet();
        return template;
    }

    /**
     * 显式注册 StringRedisTemplate（String key + String value，常用于计数器、分布式锁值等）
     *
     * @param factory Redis 连接工厂
     * @return StringRedisTemplate 实例
     */
    @Bean
    @ConditionalOnMissingBean(StringRedisTemplate.class)
    public StringRedisTemplate stringRedisTemplate(RedisConnectionFactory factory) {
        return new StringRedisTemplate(factory);
    }
}
