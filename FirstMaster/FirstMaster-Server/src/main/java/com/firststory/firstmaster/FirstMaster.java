package com.firststory.firstmaster;

import com.firststory.firstinscriptions.FirstInscriptionsUtils;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.data.neo4j.repository.config.EnableNeo4jRepositories;
import org.springframework.transaction.annotation.EnableTransactionManagement;

@EnableTransactionManagement
@EntityScan( basePackageClasses = { FirstMaster.class, FirstInscriptionsUtils.class } )
@EnableNeo4jRepositories
@SpringBootApplication
public class FirstMaster {

    public static void main( String[] args ) {
        SpringApplication.run( FirstMaster.class, args );
    }
}
