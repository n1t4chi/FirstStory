package com.firststory.firstmaster.repos;

import com.firststory.firstinscriptions.transfer.objects.TerrainNode;
import org.springframework.data.neo4j.annotation.Query;
import org.springframework.data.repository.query.Param;

import java.util.Collection;

public interface TerrainRepository extends GenericRepository< TerrainNode > {
    
    @Query( "MATCH (tr:Terrain)-[r:withTexture]->(tx:Texture) RETURN tr,r,tx LIMIT {limit}" )
    Collection< TerrainNode > graph( @Param( "limit" ) int limit );
}
