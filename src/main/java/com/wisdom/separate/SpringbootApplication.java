package com.wisdom.separate;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.boot.autoconfigure.jms.JmsAutoConfiguration;
import org.springframework.context.annotation.ImportResource;


/**
 * 知识点:@SpringBootApplication = @Configuration + @EnableAutoConfiguration + @ComponentScan
 * @author youben
 *
 */
@SpringBootApplication(scanBasePackages = "com.wisdom")		//核心注解
@EnableAutoConfiguration(exclude = {DataSourceAutoConfiguration.class,JmsAutoConfiguration.class})
@MapperScan("com.wisdom.separate.dao")				//扫描 mybatis mapper 包路径
@ImportResource(value= {"applicationContext-datasource.xml"})//载入外部XML文件
public class SpringbootApplication {

    public static void main(String[] args) {
        
    		SpringApplication.run(SpringbootApplication.class, args);
    }
}
