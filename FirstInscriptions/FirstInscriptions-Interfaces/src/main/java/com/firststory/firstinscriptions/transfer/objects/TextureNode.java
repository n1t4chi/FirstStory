package com.firststory.firstinscriptions.transfer.objects;

import lombok.Data;
import lombok.EqualsAndHashCode;
import org.neo4j.ogm.annotation.NodeEntity;

@NodeEntity( label = "Texture" )
@EqualsAndHashCode( callSuper = true )
@Data
public class TextureNode extends Node {
    
    private String fileName;
    private Integer directions;
    private Integer frames;
    private Integer rows;
    private Integer columns;
}
