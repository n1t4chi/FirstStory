package com.firststory.firstmaster.repos;

import com.firststory.firstinscriptions.TextureTransferData;
import org.springframework.data.neo4j.repository.Neo4jRepository;
import org.springframework.data.repository.query.Param;

import java.util.Collection;

public interface TextureRepository extends Neo4jRepository< TextureTransferData, Long > {

    TextureTransferData findByName( @Param( "name" ) String name );

    Collection< TextureTransferData > findByNameLike( @Param( "name" ) String name );
}
