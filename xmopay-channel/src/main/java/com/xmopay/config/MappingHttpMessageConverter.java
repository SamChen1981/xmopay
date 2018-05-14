package com.xmopay.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;

/**
 * com.xmopay.config
 *
 * @author echo_coco.
 * @date 9:56 AM, 2018/5/5
 */
@Configuration
public class MappingHttpMessageConverter {

    @Bean
    @Primary
    public MappingJackson2HttpMessageConverter mappingJackson2HttpMessageConverter() {
        ObjectMapper objectMapper = new ObjectMapper();
//        objectMapper.setPropertyNamingStrategy(new PropertyNamingStrategy.SnakeCaseStrategy());
//        objectMapper.setPropertyNamingStrategy(new PropertyNamingStrategy.UpperCamelCaseStrategy()); // PartnerId
//        objectMapper.setPropertyNamingStrategy(new PropertyNamingStrategy.LowerCaseStrategy()); // partnerid
//        objectMapper.setPropertyNamingStrategy(new PropertyNamingStrategy.KebabCaseStrategy()); // partner-id
//        objectMapper.setPropertyNamingStrategy(new PropertyNamingStrategy.PascalCaseStrategy());
        MappingJackson2HttpMessageConverter converter = new MappingJackson2HttpMessageConverter();
        converter.setObjectMapper(objectMapper);
        return converter;
    }
}
