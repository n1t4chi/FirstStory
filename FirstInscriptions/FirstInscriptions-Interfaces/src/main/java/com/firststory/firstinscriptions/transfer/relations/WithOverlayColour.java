package com.firststory.firstinscriptions.transfer.relations;

import com.firststory.firstinscriptions.transfer.objects.ColourNode;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.neo4j.ogm.annotation.RelationshipEntity;

@EqualsAndHashCode( callSuper = true )
@RelationshipEntity( type = "WithColour" )
@Data
public class WithOverlayColour extends WithGeneric< ColourNode > {}
