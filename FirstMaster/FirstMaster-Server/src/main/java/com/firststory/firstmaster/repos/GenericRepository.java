package com.firststory.firstmaster.repos;

import com.firststory.firstmaster.IgnoreBean;
import org.springframework.data.neo4j.repository.Neo4jRepository;
import org.springframework.data.repository.query.Param;

import java.util.Collection;

@IgnoreBean
public interface GenericRepository<Type> extends Neo4jRepository< Type, Long > {
    
    
    Type findByName( @Param( "name" ) String name );
    
    Collection< Type > findByNameLike( @Param( "pattern" ) String pattern );
}
