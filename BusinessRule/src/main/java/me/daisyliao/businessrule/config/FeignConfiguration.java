package me.daisyliao.businessrule.config;

import org.springframework.cloud.netflix.feign.EnableFeignClients;
import org.springframework.context.annotation.Configuration;

@Configuration
@EnableFeignClients(basePackages = "me.daisyliao.businessrule")
public class FeignConfiguration {

}
