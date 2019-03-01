package com.firststory.firstinscriptions.transfer.objects;

import lombok.Data;
import lombok.EqualsAndHashCode;
import org.neo4j.ogm.annotation.NodeEntity;

@NodeEntity( label = "UvMap" )
@EqualsAndHashCode( callSuper = true )
@Data
public class UvMappingNode extends TextNode {
}
