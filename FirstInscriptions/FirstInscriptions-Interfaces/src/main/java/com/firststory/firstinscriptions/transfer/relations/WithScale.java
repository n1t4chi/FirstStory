package com.firststory.firstinscriptions.transfer.relations;

import com.firststory.firstinscriptions.transfer.objects.ScaleNode;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.neo4j.ogm.annotation.RelationshipEntity;

@EqualsAndHashCode( callSuper = true )
@RelationshipEntity( type = "withScale" )
@Data
public class WithScale extends WithGeneric< ScaleNode > {}
