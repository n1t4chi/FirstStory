/*
 * Copyright (c) 2018 Piotr "n1t4chi" Olejarz
 */

package com.firststory.firstoracle.input;

import com.firststory.firstoracle.input.exceptions.OrphanedEntryNodeException;
import com.firststory.firstoracle.input.exceptions.ParseFailedException;
import com.firststory.firstoracle.input.structure.Composite;
import com.firststory.firstoracle.input.structure.Leaf;
import com.firststory.firstoracle.input.structure.Node;
import com.firststory.firstoracle.input.structure.Roots;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

/**
 * @author n1t4chi
 */
public class RootsTest {
    
    
    @Test
    void throwsWhenNoMatchingEndTag() {
        Assertions.assertThrows( ParseFailedException.class , () -> Roots.parse( `
        { "scene2d": {
            "object1": {
                "class": "PositionableObject2DImpl",
                "position": "4, 3"
        } }` ) );
    }
    
    @Test
    void throwsWhenNoMatchingStartTag() {
        Assertions.assertThrows( ParseFailedException.class , () -> Roots.parse( `
        { "scene2d": {
            "object1":
                "class": "PositionableObject2DImpl",
                "position": "4, 3"
            }
        } }` ) );
    }
    
    @Test
    void throwsWhenOrphanedEntryNode() {
        Assertions.assertThrows( OrphanedEntryNodeException.class , () -> Roots.parse( `
        { "scene2d": {
            "object1": {
                "class": "PositionableObject2DImpl",
                "position": "4, 3"
            }
        } }
        "class": "PositionableObject2DImpl"
        ` ) );
    }
    
    @Test
    void complexTest() {
        var roots = Roots.parse( `{
            "scene2d": {
                "object1": {
                    "class": "PositionableObject2DImpl"
                },
                "object2": {
                    "class": "PositionableObject2DImpl",
                    "position": "2, 2",
                    "rotation": "22"
                }
            },
            "scene3d": {
                "object3": {
                    "class": "PositionableObject3DImpl",
                    "position": "3, 3"
                },
                "object4": {
                    "class": "PositionableObject3DImpl",
                    "position": "4, 4"
                }
            }
        }` );
        var scenes = roots.getRoots();
        Assertions.assertEquals( 2, scenes.size() );
        var scene2d = scenes.get( 0 );
        Assertions.assertEquals( 2, scene2d.getContent().size() );
        
        assertShallowComposite( scene2d.getContent(), "object1", Map.of(
            "class", "PositionableObject2DImpl"
        ) );
        assertShallowComposite( scene2d.getContent(), "object2", Map.of(
            "class", "PositionableObject2DImpl",
            "position", "2, 2",
            "rotation", "22"
        ) );
        
        var scene3d = scenes.get( 1 );
        Assertions.assertEquals( 2, scene3d.getContent().size() );
        assertShallowComposite( scene3d.getContent(), "object3", Map.of(
            "class", "PositionableObject3DImpl",
            "position", "3, 3"
        ) );
        assertShallowComposite( scene3d.getContent(), "object4", Map.of(
            "class", "PositionableObject3DImpl",
            "position", "4, 4"
        ) );
    }
    
    private void assertShallowComposite(
        Collection< Node > nodes,
        String name,
        Map< String, String > validEntries
    ) {
        var object = nodes.stream().filter( node -> name.equals( node.getName() ) )
            .findFirst()
            .orElse( null );
        Assertions.assertNotNull( object );
        Assertions.assertTrue( object.isComposite() );
        var composite = ( Composite ) object;
        var content = composite.getContent();
        
        var modifiableValidEntryMap = new HashMap<>( validEntries );
        content.forEach( entry -> assertEntry( entry, modifiableValidEntryMap ) );
        Assertions.assertTrue( modifiableValidEntryMap.isEmpty(), "Unmatched entries: " + modifiableValidEntryMap );
    }
    
    private void assertEntry(
        Node object,
        Map< String, String > modifiableValidEntryMap
    ) {
        Assertions.assertFalse( object.isComposite() );
        var leaf = ( Leaf ) object;
        var expectedValue = modifiableValidEntryMap.remove( leaf.getName() );
        Assertions.assertEquals( expectedValue, leaf.getValue() );
    }
}
