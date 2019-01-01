package com.wisdom.separate.config;

import javax.sql.DataSource;
import org.apache.ibatis.session.SqlSessionFactory;
import org.mybatis.spring.SqlSessionFactoryBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.core.io.support.ResourcePatternResolver;
/**
 * mybatis相关配置(摒弃XML)
 * @author youben
 */
@Configuration
public class MyBatisConfiguration {
	
	 @Autowired
	 DataSource dynamicdataSource;
	 
	@Bean(name = "sqlSessionFactory")
    public SqlSessionFactory sqlSessionFactoryBean() throws Exception{
        SqlSessionFactoryBean bean = new SqlSessionFactoryBean();
        bean.setDataSource(dynamicdataSource);//注入数据源
        bean.setTypeAliasesPackage("com.wisdom.separate.model");//设置别名

        //显式指定Mapper文件位置,当Mapper文件跟对应的Mapper接口处于同一位置的时候可以不用指定该属性的值
        ResourcePatternResolver resolver = new PathMatchingResourcePatternResolver();
        bean.setMapperLocations(resolver.getResources("classpath:mapper/*.xml"));
        
        return bean.getObject();
    }

 }
