package me.daisyliao.processes.config;

import org.springframework.cloud.netflix.feign.EnableFeignClients;
import org.springframework.context.annotation.Configuration;

@Configuration
@EnableFeignClients(basePackages = "me.daisyliao.processes")
public class FeignConfiguration {

}
