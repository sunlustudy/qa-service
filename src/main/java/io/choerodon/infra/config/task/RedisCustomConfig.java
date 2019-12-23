package io.choerodon.infra.config.task;

import com.alibaba.fastjson.support.spring.FastJsonRedisSerializer;
import io.choerodon.domain.BaseUser;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.CacheManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.cache.RedisCacheConfiguration;
import org.springframework.data.redis.cache.RedisCacheManager;
import org.springframework.data.redis.cache.RedisCacheWriter;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.listener.PatternTopic;
import org.springframework.data.redis.listener.RedisMessageListenerContainer;
import org.springframework.data.redis.listener.adapter.MessageListenerAdapter;
import org.springframework.data.redis.serializer.RedisSerializationContext;
import org.springframework.data.redis.serializer.RedisSerializer;

import java.time.Duration;
import java.util.HashMap;
import java.util.Map;

/*
 * @description:
 * @program: springboot
 * @author: syun
 * @create: 2019-09-29 17:05
 */
@Configuration
@Slf4j
public class RedisCustomConfig {


    @Value("${redis.topic.personal}")
    String personalTopic;

    @Bean
    RedisMessageListenerContainer container(RedisConnectionFactory connectionFactory,
                                            MessageListenerAdapter listenerAdapter) {
        RedisMessageListenerContainer container = new RedisMessageListenerContainer();
        container.setConnectionFactory(connectionFactory);
        container.addMessageListener(listenerAdapter, new PatternTopic(personalTopic));
        return container;
    }

    @Bean
    MessageListenerAdapter listenerAdapter(RedisReceiver receiver) {
        return new MessageListenerAdapter(receiver, "receiveMessage");
    }

    @Bean
    public CacheManager cacheManager(RedisConnectionFactory redisConnectionFactory) {

        FastJsonRedisSerializer<BaseUser> fastJsonRedisSerializer = new FastJsonRedisSerializer<>(BaseUser.class);

        RedisSerializationContext.SerializationPair baseUserSerializationPair = RedisSerializationContext.SerializationPair.fromSerializer(fastJsonRedisSerializer);
        RedisCacheConfiguration userDTOCacheConfiguration = RedisCacheConfiguration
                .defaultCacheConfig().entryTtl(Duration.ofMinutes(20))  // 缓存 20 分钟
                .disableCachingNullValues().prefixKeysWith("baseUser")
                .serializeValuesWith(baseUserSerializationPair);

        Map<String, RedisCacheConfiguration> redisCacheConfigurationMap = new HashMap<>();
        redisCacheConfigurationMap.put("baseUser", userDTOCacheConfiguration);
        RedisCacheWriter redisCacheWriter = RedisCacheWriter.nonLockingRedisCacheWriter(redisConnectionFactory);
        RedisCacheConfiguration defaultCacheConfig = RedisCacheConfiguration.defaultCacheConfig();

//        RedisCacheConfiguration defaultCacheConfig = RedisCacheConfiguration.defaultCacheConfig();
//        设置默认的过期时间为 30 秒
        defaultCacheConfig.entryTtl(Duration.ofSeconds(30));
//        初始化 RedisCacheManager
        return new RedisCacheManager(redisCacheWriter, defaultCacheConfig, redisCacheConfigurationMap);
    }


    @Bean("userRedisTemplate")
    @Autowired
    public RedisTemplate<String, BaseUser> userRedisTemplate(RedisConnectionFactory redisConnectionFactory) {
        RedisTemplate<String, BaseUser> template = new RedisTemplate<>();
        template.setConnectionFactory(redisConnectionFactory);
        FastJsonRedisSerializer<BaseUser> fastJsonRedisSerializer = new FastJsonRedisSerializer<>(BaseUser.class);
        template.setDefaultSerializer(fastJsonRedisSerializer);
        template.setKeySerializer(RedisSerializer.string());
        return template;
    }


}
