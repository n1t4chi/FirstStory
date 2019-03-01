package com.firststory.firstinscriptions.transfer.objects;

import lombok.Data;
import lombok.EqualsAndHashCode;
import org.neo4j.ogm.annotation.NodeEntity;

@NodeEntity( label = "Rotation" )
@EqualsAndHashCode( callSuper = true )
@Data
public class RotationNode extends Node {
    private float ox,oy,oz;
}
