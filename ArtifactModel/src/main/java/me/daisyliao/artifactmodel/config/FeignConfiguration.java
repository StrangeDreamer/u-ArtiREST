package me.daisyliao.artifactmodel.config;

import org.springframework.cloud.netflix.feign.EnableFeignClients;
import org.springframework.context.annotation.Configuration;

@Configuration
@EnableFeignClients(basePackages = "me.daisyliao.artifactmodel")
public class FeignConfiguration {

}
