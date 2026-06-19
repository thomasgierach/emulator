package com.zos.home.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.codec.protobuf.ProtobufDecoder;
import org.springframework.http.codec.protobuf.ProtobufEncoder;
import org.springframework.web.reactive.function.client.WebClient;

@Configuration
public class WebClientConfig {

    @Bean
    public WebClient authWebClient(WebClient.Builder builder) {
        return builder
                .baseUrl("http://auth:8080")
                .codecs(configurer -> {
                    configurer.defaultCodecs().protobufEncoder(new ProtobufEncoder());
                    configurer.defaultCodecs().protobufDecoder(new ProtobufDecoder());
                })
                .build();
    }
}