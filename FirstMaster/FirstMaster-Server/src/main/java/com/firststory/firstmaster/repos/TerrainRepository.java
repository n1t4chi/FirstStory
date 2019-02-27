package com.firststory.firstmaster.repos;

import com.firststory.firstinscriptions.TerrainTransferData;
import org.springframework.data.neo4j.annotation.Query;
import org.springframework.data.neo4j.repository.Neo4jRepository;
import org.springframework.data.repository.query.Param;

import java.util.Collection;

public interface TerrainRepository extends Neo4jRepository< TerrainTransferData, Long > {

    Collection< TerrainTransferData > findByNameLike( @Param( "pattern" ) String pattern );

    @Query( "MATCH (tr:Terrain)-[r:withTexture]->(tx:Texture) RETURN tr,r,tx LIMIT {limit}" )
    Collection< TerrainTransferData > graph( @Param( "limit" ) int limit );
}
