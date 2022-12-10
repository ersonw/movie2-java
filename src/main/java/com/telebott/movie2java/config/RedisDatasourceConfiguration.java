package com.telebott.movie2java.config;

import com.telebott.movie2java.util.ToolsUtil;
import org.apache.commons.pool2.impl.GenericObjectPoolConfig;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.connection.RedisPassword;
import org.springframework.data.redis.connection.RedisStandaloneConfiguration;
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory;
import org.springframework.data.redis.connection.lettuce.LettucePoolingClientConfiguration;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.RedisSerializer;
import org.springframework.data.redis.serializer.SerializationException;
import org.springframework.data.redis.serializer.StringRedisSerializer;
import org.springframework.util.ObjectUtils;

/**
 * reactive redis
 * @Author:wangqipeng
 * @Date:14:38 2019-07-03
 */

@Configuration
public class RedisDatasourceConfiguration {

    @Value("${spring.redis.isCleanRedisCache:false}")
    private String cleanRedisCache;
    @Value("${spring.redis.host:127.0.0.1}")
    public String host;
    @Value("${spring.redis.port:6379}")
    public Integer port;
    @Value("${spring.redis.password:null}")
    private String password;

    @Value("${spring.redis.timeout:2000}")
    public Integer timeout;
    public Integer maxIdle = 16;
    public Integer minIdle = 5;
    public Integer maxTotal = 30;


    @Bean
    public <T> RedisTemplate<String, T> userRedisTemplate() {
        RedisStandaloneConfiguration configuration = new RedisStandaloneConfiguration(host, port);
        configuration.setDatabase(1);
        if (!ObjectUtils.isEmpty(password)) {
            RedisPassword redisPassword = RedisPassword.of(password);
            configuration.setPassword(redisPassword);
        }
        return createRedisTemplate(creatFactory(configuration));
    }
    @Bean
    public <T> RedisTemplate<String, T> codeRedisTemplate() {
        RedisStandaloneConfiguration configuration = new RedisStandaloneConfiguration(host, port);
        configuration.setDatabase(2);
        if (!ObjectUtils.isEmpty(password)) {
            RedisPassword redisPassword = RedisPassword.of(password);
            configuration.setPassword(redisPassword);
        }
        return createRedisTemplate(creatFactory(configuration));
    }
    @Bean
    public <T> RedisTemplate<String, T> searchRedisTemplate() {
        RedisStandaloneConfiguration configuration = new RedisStandaloneConfiguration(host, port);
        configuration.setDatabase(3);
        if (!ObjectUtils.isEmpty(password)) {
            RedisPassword redisPassword = RedisPassword.of(password);
            configuration.setPassword(redisPassword);
        }
        return createRedisTemplate(creatFactory(configuration));
    }
    @Bean
    public <T> RedisTemplate<String, T> orderRedisTemplate() {
        RedisStandaloneConfiguration configuration = new RedisStandaloneConfiguration(host, port);
        configuration.setDatabase(4);
        if (!ObjectUtils.isEmpty(password)) {
            RedisPassword redisPassword = RedisPassword.of(password);
            configuration.setPassword(redisPassword);
        }
        return createRedisTemplate(creatFactory(configuration));
    }

    private <T> RedisTemplate<String, T> getSerializerRedisTemplate(){
        RedisTemplate<String, T> redisTemplate = new RedisTemplate<>();
        RedisSerializer<String> serializer = new StringRedisSerializer();
        RedisSerializer<T> serializerValue = new RedisSerializer<T>() {
            @Override
            public byte[] serialize(T t) throws SerializationException {
                return ToolsUtil.objectToByteArray(t);
            }

            @Override
            public T deserialize(byte[] bytes) throws SerializationException {
                return ToolsUtil.byteArrayToObject(bytes);
            }
        };
        redisTemplate.setValueSerializer(serializerValue);
        redisTemplate.setKeySerializer(serializer);
        redisTemplate.setHashKeySerializer(serializer);
        redisTemplate.setHashValueSerializer(serializer);
        return redisTemplate;
    }

    private <T> RedisTemplate<String, T> createRedisTemplate(RedisConnectionFactory redisConnectionFactory) {
        RedisTemplate<String, T> redisTemplate = getSerializerRedisTemplate();
        redisTemplate.setConnectionFactory(redisConnectionFactory);
        redisTemplate.afterPropertiesSet();
        return redisTemplate;
    }
    private <T> GenericObjectPoolConfig<T> getGenericObjectPoolConfig(){
        GenericObjectPoolConfig<T> genericObjectPoolConfig = new GenericObjectPoolConfig<>();
        genericObjectPoolConfig.setMaxTotal(maxTotal);
        genericObjectPoolConfig.setMinIdle(minIdle);
        genericObjectPoolConfig.setMaxIdle(maxIdle);
        genericObjectPoolConfig.setMaxWaitMillis(timeout);
        return genericObjectPoolConfig;
    }

    private LettuceConnectionFactory creatFactory(RedisStandaloneConfiguration configuration){
        LettucePoolingClientConfiguration.LettucePoolingClientConfigurationBuilder builder = LettucePoolingClientConfiguration.builder();
        builder.poolConfig(getGenericObjectPoolConfig());

//        LettuceClientConfiguration.LettuceClientConfigurationBuilder builder = LettuceClientConfiguration.builder();
//        builder.clientResources(clientResources());
//        builder.commandTimeout(Duration.ofSeconds(3000));
        LettuceConnectionFactory connectionFactory = new LettuceConnectionFactory(configuration, builder.build());
        connectionFactory.afterPropertiesSet();
        return connectionFactory;
    }
}
