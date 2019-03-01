package com.firststory.firstmaster;

import com.firststory.firstinscriptions.FirstInscriptionsUtils;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.FilterType;
import org.springframework.data.neo4j.repository.config.EnableNeo4jRepositories;
import org.springframework.transaction.annotation.EnableTransactionManagement;

@EnableTransactionManagement
@EntityScan( basePackageClasses = { FirstMaster.class, FirstInscriptionsUtils.class } )
@EnableNeo4jRepositories( excludeFilters = @ComponentScan.Filter(type= FilterType.ANNOTATION, value= IgnoreBean.class) )
@SpringBootApplication
public class FirstMaster {

    public static void main( String[] args ) {
        SpringApplication.run( FirstMaster.class, args );
    }
}
