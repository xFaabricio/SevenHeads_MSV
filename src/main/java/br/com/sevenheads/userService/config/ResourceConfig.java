package br.com.sevenheads.userService.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.context.annotation.PropertySources;

import lombok.RequiredArgsConstructor;

@Configuration
@RequiredArgsConstructor
@PropertySources({
    @PropertySource("classpath:html.properties"),
    @PropertySource("classpath:configuration.properties")
})
public class ResourceConfig {

}
