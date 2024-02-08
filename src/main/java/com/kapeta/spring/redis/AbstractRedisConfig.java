/*
 * Copyright 2023 Kapeta Inc.
 * SPDX-License-Identifier: MIT
 */

package com.kapeta.spring.redis;


import com.kapeta.spring.config.providers.KapetaConfigurationProvider;
import com.kapeta.spring.config.providers.types.ResourceInfo;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.data.redis.connection.RedisStandaloneConfiguration;
import org.springframework.data.redis.connection.jedis.JedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;

/**
 * Redis configuration class.
 */
@Slf4j
abstract public class AbstractRedisConfig {

    private static final String RESOURCE_TYPE = "kapeta/resource-type-redis";

    private static final String PORT_TYPE = "redis";

    @Autowired
    private KapetaConfigurationProvider configurationProvider;

    private final String resourceName;

    protected AbstractRedisConfig(String resourceName) {
        this.resourceName = resourceName;
    }

    @Bean("redisInfo")
    public ResourceInfo redisInfo() {
        return configurationProvider.getResourceInfo(RESOURCE_TYPE, PORT_TYPE, resourceName);
    }

    @Bean
    public JedisConnectionFactory jedisConnectionFactory(ResourceInfo redisInfo) {
        RedisStandaloneConfiguration config = new RedisStandaloneConfiguration(redisInfo.getHost(), Integer.parseInt(redisInfo.getPort()));
        config.setDatabase((Integer) redisInfo.getOptions().getOrDefault("database", 1));

        if (StringUtils.isNotBlank(redisInfo.getCredentials().get("username"))) {
            config.setUsername(redisInfo.getCredentials().get("username"));

            if (StringUtils.isNotBlank(redisInfo.getCredentials().get("password"))) {
                config.setPassword(redisInfo.getCredentials().get("password"));
            }
        }

        return new JedisConnectionFactory(config);
    }

    @Bean
    public RedisTemplate<String, Object> redisTemplate(JedisConnectionFactory jedisConnectionFactory) {
        RedisTemplate<String, Object> template = new RedisTemplate<>();
        template.setConnectionFactory(jedisConnectionFactory);
        return template;
    }


}
