package com.example.ium.chat.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.mongodb.MongoDatabaseFactory;
import org.springframework.data.mongodb.core.convert.MappingMongoConverter;
import org.springframework.data.mongodb.core.mapping.MongoMappingContext;
import org.springframework.data.mongodb.core.convert.DefaultDbRefResolver;
import org.springframework.data.mongodb.core.convert.DefaultMongoTypeMapper;

@Configuration
public class MongoConfig {
  /**
   * * _class 필드가 저장되지 않도록 설정
   * */
  @Bean
  public MappingMongoConverter mappingMongoConverter(
          MongoDatabaseFactory mongoDatabaseFactory,
          MongoMappingContext mongoMappingContext
  ) {
    DefaultDbRefResolver dbRefResolver = new DefaultDbRefResolver(mongoDatabaseFactory);
    MappingMongoConverter converter = new MappingMongoConverter(dbRefResolver, mongoMappingContext);
    converter.setTypeMapper(new DefaultMongoTypeMapper(null)); // _class 필드 제거
    return converter;
  }
}
