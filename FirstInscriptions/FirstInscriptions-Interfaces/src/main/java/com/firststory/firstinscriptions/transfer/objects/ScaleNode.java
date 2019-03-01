package com.firststory.firstinscriptions.transfer.objects;

import lombok.Data;
import lombok.EqualsAndHashCode;
import org.neo4j.ogm.annotation.NodeEntity;

@NodeEntity( label = "Scale" )
@EqualsAndHashCode( callSuper = true )
@Data
public class ScaleNode extends Node {
    private float x,y,z;
}
