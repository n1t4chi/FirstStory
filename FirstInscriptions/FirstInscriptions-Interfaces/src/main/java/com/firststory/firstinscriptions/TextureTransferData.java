package com.firststory.firstinscriptions;

import lombok.Data;
import org.neo4j.ogm.annotation.GeneratedValue;
import org.neo4j.ogm.annotation.Id;
import org.neo4j.ogm.annotation.NodeEntity;

@NodeEntity( label = "Texture" )
@Data
public class TextureTransferData {

    @Id
    @GeneratedValue
    private Long id;
    private String name;
    private String fileName;
    private Integer directions;
    private Integer frames;
    private Integer rows;
    private Integer columns;
}
