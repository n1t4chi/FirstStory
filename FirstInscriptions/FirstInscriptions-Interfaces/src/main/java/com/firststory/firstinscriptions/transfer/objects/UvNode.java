package com.firststory.firstinscriptions.transfer.objects;

import lombok.Data;
import lombok.EqualsAndHashCode;
import org.neo4j.ogm.annotation.NodeEntity;

@NodeEntity( label = "Uv" )
@EqualsAndHashCode( callSuper = true )
@Data
public class UvNode extends Node {
    private float u,v;
}
