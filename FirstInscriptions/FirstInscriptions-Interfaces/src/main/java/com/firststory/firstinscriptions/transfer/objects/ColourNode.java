package com.firststory.firstinscriptions.transfer.objects;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import org.neo4j.ogm.annotation.NodeEntity;

@NodeEntity( label = "Colour" )
@EqualsAndHashCode( callSuper = true )
@ToString( callSuper = true )
@Data
public class ColourNode extends Node {
    private float r,g,b,a;
}
