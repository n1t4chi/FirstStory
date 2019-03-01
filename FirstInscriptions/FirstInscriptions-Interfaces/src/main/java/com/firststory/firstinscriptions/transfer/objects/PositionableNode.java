package com.firststory.firstinscriptions.transfer.objects;

import lombok.Data;
import lombok.EqualsAndHashCode;
import org.neo4j.ogm.annotation.NodeEntity;

@NodeEntity( label = "PositionableObject" )
@EqualsAndHashCode( callSuper = true )
@Data
public class PositionableNode extends GraphicNode {
}
