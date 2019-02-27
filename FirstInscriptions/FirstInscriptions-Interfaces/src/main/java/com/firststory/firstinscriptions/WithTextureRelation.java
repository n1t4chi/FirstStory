package com.firststory.firstinscriptions;

import lombok.Data;
import org.neo4j.ogm.annotation.*;

@RelationshipEntity( type = "withTexture" )
@Data
public class WithTextureRelation {

    @Id
    @GeneratedValue
    private Long id;

    private String name;

    @StartNode
    private TerrainTransferData terrain;

    @EndNode
    private TextureTransferData texture;
}
