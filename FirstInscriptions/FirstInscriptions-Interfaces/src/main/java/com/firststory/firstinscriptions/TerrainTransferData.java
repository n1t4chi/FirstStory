package com.firststory.firstinscriptions;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;
import org.neo4j.ogm.annotation.GeneratedValue;
import org.neo4j.ogm.annotation.Id;
import org.neo4j.ogm.annotation.NodeEntity;
import org.neo4j.ogm.annotation.Relationship;

import java.util.Collection;

@NodeEntity( label = "Terrain" )
@Data
public class TerrainTransferData {
    
    @Id
    @GeneratedValue
    private Long id;
    private String name;
    
    @JsonIgnoreProperties( "terrain" )
    @Relationship( type = "withTexture" )
    private Collection< WithTextureRelation > withTexture;
    
}
