package me.daisyliao.service.config;

import org.springframework.cloud.netflix.feign.EnableFeignClients;
import org.springframework.context.annotation.Configuration;

@Configuration
@EnableFeignClients(basePackages = "me.daisyliao.service")
public class FeignConfiguration {

}
