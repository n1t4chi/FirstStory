package com.firststory.firstinscriptions.transfer.objects;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.firststory.firstinscriptions.transfer.relations.WithPosition;
import com.firststory.firstinscriptions.transfer.relations.WithRotation;
import com.firststory.firstinscriptions.transfer.relations.WithScale;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.neo4j.ogm.annotation.NodeEntity;
import org.neo4j.ogm.annotation.Relationship;

@NodeEntity( label = "Transformations" )
@EqualsAndHashCode( callSuper = true )
@Data
public class TransformationsNode extends Node {
    
    @JsonIgnoreProperties( "start" )
    @Relationship( type = "withPosition" )
    private WithPosition position;
    
    @JsonIgnoreProperties( "start" )
    @Relationship( type = "withRotation" )
    private WithRotation withRotation;
    
    @JsonIgnoreProperties( "start" )
    @Relationship( type = "withScale" )
    private WithScale scale;
}
