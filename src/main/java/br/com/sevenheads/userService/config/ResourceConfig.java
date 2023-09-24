package br.com.sevenheads.userService.config;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;

@Configuration
@RequiredArgsConstructor
@PropertySource("classpath:configuration.properties")
public class ResourceConfig {

}
