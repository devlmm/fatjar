package com.workspace.fatjar.framework.mq.config;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.PropertyAccessor;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;

/**
 * RocketMQ 自动装配配置
 * <p>
 * 职责：仅在配置 rocketmq.name-server 时激活，注册通用 Jackson ObjectMapper（mqObjectMapper）
 *       用于消息体的 JSON 序列化（支持 LocalDateTime 等 Java8 时间类型）。
 * <p>
 * 说明：
 *   - RocketMQTemplate 由 rocketmq-spring-boot-starter 自动装配，此处不重复。
 *   - 业务方可通过 @Resource(name="mqObjectMapper") 注入使用，或自定义覆盖。
 *
 * @author fatjar
 * @since 1.0.0
 */
@AutoConfiguration
@ConditionalOnProperty(prefix = "rocketmq", name = "name-server")
public class MqConfig {

    /**
     * 注册 MQ 专用 ObjectMapper
     * <p>
     * 注册 JavaTimeModule 支持 LocalDateTime 序列化；关闭日期序列化为时间戳的默认行为，输出 ISO 字符串。
     *
     * @return ObjectMapper 实例
     */
    @Bean("mqObjectMapper")
    @ConditionalOnMissingBean(name = "mqObjectMapper")
    public ObjectMapper mqObjectMapper() {
        ObjectMapper mapper = new ObjectMapper();
        mapper.registerModule(new JavaTimeModule());
        mapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
        mapper.setVisibility(PropertyAccessor.ALL, JsonAutoDetect.Visibility.ANY);
        return mapper;
    }
}
