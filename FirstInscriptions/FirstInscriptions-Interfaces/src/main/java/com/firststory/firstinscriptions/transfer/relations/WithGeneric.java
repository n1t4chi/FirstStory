package com.firststory.firstinscriptions.transfer.relations;

import com.firststory.firstinscriptions.transfer.objects.Node;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

@EqualsAndHashCode( callSuper = true )
@ToString( callSuper = true )
@Data
public class WithGeneric< End extends Node > extends With< Node, End > {}
