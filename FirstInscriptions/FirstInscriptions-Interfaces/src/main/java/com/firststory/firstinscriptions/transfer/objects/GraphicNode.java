package com.firststory.firstinscriptions.transfer.objects;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.firststory.firstinscriptions.transfer.relations.*;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import org.neo4j.ogm.annotation.NodeEntity;
import org.neo4j.ogm.annotation.Relationship;

import java.util.Collection;

@NodeEntity( label = "GraphicObject" )
@EqualsAndHashCode( callSuper = true )
@ToString( callSuper = true )
@Data
class GraphicNode extends Node {
    
    @JsonIgnoreProperties( "start" )
    @Relationship( type = "withTexture" )
    private Collection< WithTexture > texture;
    
    @JsonIgnoreProperties( "start" )
    @Relationship( type = "withTransformations" )
    private Collection< WithTransformations > transformations;
    
    @JsonIgnoreProperties( "start" )
    @Relationship( type = "withUvMap" )
    private Collection< WithUvMap > uvMap;
    
    @JsonIgnoreProperties( "start" )
    @Relationship( type = "withVertices" )
    private Collection< WithVertices > vertices;
    
    @JsonIgnoreProperties( "start" )
    @Relationship( type = "withColouring" )
    private Collection< WithColouring > colouring;
    
    @JsonIgnoreProperties( "start" )
    @Relationship( type = "withOverlayColour" )
    private Collection< WithOverlayColour > overlayColour;
    
    private float maxAlphaChannel;
}
