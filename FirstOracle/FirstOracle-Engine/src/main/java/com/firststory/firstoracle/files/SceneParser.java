/*
 * Copyright (c) 2018 Piotr "n1t4chi" Olejarz
 */

package com.firststory.firstoracle.files;

import com.firststory.firstoracle.FirstOracleConstants;
import com.firststory.firstoracle.data.*;
import com.firststory.firstoracle.files.Exceptions.GraphicObjectClassNotFoundException;
import com.firststory.firstoracle.files.Exceptions.ParseFailedException;
import com.firststory.firstoracle.files.structure.Composite;
import com.firststory.firstoracle.files.structure.Node;
import com.firststory.firstoracle.files.structure.Roots;
import com.firststory.firstoracle.object.*;
import com.firststory.firstoracle.object2D.*;
import com.firststory.firstoracle.object3D.*;
import com.firststory.firstoracle.scene.*;

import java.util.Collection;
import java.util.List;
import java.util.function.BiConsumer;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;

import static com.firststory.firstoracle.data.Position2D.pos2;
import static com.firststory.firstoracle.data.Position3D.pos3;
import static com.firststory.firstoracle.data.Rotation3D.rot3;
import static com.firststory.firstoracle.data.Scale2D.scale2;
import static com.firststory.firstoracle.data.Scale3D.scale3;
import static com.firststory.firstoracle.files.ParseUtils.*;

/**
 * @author n1t4chi
 */
public class SceneParser {
    private static final Logger logger = FirstOracleConstants.getLogger( SceneParser.class );
    
    public static ScenePair<
        OptimisedRegistrableScene2DImpl,
        OptimisedRegistrableScene3DImpl
    > parseToOptimised( String text ) {
        return new SceneParser().parse(
            text,
            OptimisedRegistrableScene2DImpl::new,
            OptimisedRegistrableScene3DImpl::new
        );
    }
    
    public static ScenePair<
        RegistrableScene2DImpl,
        RegistrableScene3DImpl
    > parseToNonOptimised( String text ) {
        return new SceneParser().parse(
            text,
            RegistrableScene2DImpl::new,
            RegistrableScene3DImpl::new
        );
    }
    
    public <
        S2D extends RegistrableScene2D,
        S3D extends RegistrableScene3D
    > ScenePair< S2D, S3D > parse(
        String text,
        SceneSupplier< Index2D, S2D > scene2dSupplier,
        SceneSupplier< Index3D, S3D > scene3dSupplier
    ) {
        var roots = Roots.parse( text );
        System.err.println( roots );
    
        return new ScenePair<>(
            parseScene2D( scene2dSupplier, roots ),
            parseScene3D( scene3dSupplier, roots )
        );
    }
    
    public < S2D extends RegistrableScene2D > S2D parseScene2D(
        SceneSupplier< Index2D, S2D > scene2dSupplier,
        Roots roots
    ) {
        return parseScene(
            roots.find( NAME_OBJECTS_2D ),
            roots.find( NAME_TERRAIN_2D ),
            scene2dSupplier,
            this::toObject2D,
            this::toTerrain2D,
            RegistrableScene2D::registerMultipleObjects2D,
            RegistrableScene2D::registerMultipleTerrains2D,
            FirstOracleConstants.INDEX_ZERO_2I,
            FirstOracleConstants.INDEX_ZERO_2I
    
        );
    }
    
    public < S3D extends RegistrableScene3D > S3D parseScene3D(
        SceneSupplier< Index3D, S3D > scene3dSupplier,
        Roots roots
    ) {
        return parseScene(
            roots.find( NAME_OBJECTS_3D ),
            roots.find( NAME_TERRAIN_3D ),
            scene3dSupplier,
            this::toObject3D,
            this::toTerrain3D,
            RegistrableScene3D::registerMultipleObjects3D,
            RegistrableScene3D::registerMultipleTerrains3D,
            FirstOracleConstants.INDEX_ZERO_3I,
            FirstOracleConstants.INDEX_ZERO_3I
            
        );
    }
    
    private <
        Scene,
        IndexT extends Index,
        PositionableObjectT extends PositionableObject< ? , ?, ? >,
        TerrainT extends Terrain< ? , ?, ? >
    > Scene parseScene(
        Composite objectsNode,
        Composite terrainsNode,
        SceneSupplier< IndexT, Scene > sceneSupplier,
        Function< Composite, PositionableObjectT > toObjectT,
        Function< Composite, TerrainT > toTerrainT,
        BiConsumer< Scene, Collection< PositionableObjectT > > registerObject,
        TriConsumer< Scene, TerrainT, Collection< IndexT >  > registerTerrain,
        IndexT size,
        IndexT shift

    ) {
        Collection< PositionableObjectT > objects = objectsNode.getContent().stream()
            .filter( Node::isComposite )
            .map( Composite.class::cast )
            .map( toObjectT )
            .collect( Collectors.toList() )
        ;
    
        var s3D = sceneSupplier.create( size, shift );
        registerObject.accept( s3D, objects );
        return s3D;
    }
    
    private Terrain2D< ? > toTerrain2D( Composite node ) {
        return null;
    }
    
    private void setVertices2D(
        PositionableObject2D< ?, ? > object,
        String verticesText
    ) {
        setVertices(
            object,
            Vertices2D.class,
            ParseUtils::toVec2,
            Position2D::pos2,
            Vertices2D::new,
            verticesText
        );
    }
    
    private void setVertices3D(
        PositionableObject3D< ?, ? > object,
        String verticesText
    ) {
        setVertices(
            object,
            Vertices3D.class,
            ParseUtils::toVec3,
            Position3D::pos3,
            Vertices3D::new,
            verticesText
        );
    }
    
    @SuppressWarnings( { "unchecked", "rawtypes" } )
    private < Vector, Posistion extends Position, VerticesT extends Vertices<?,?> > void setVertices(
        PositionableObject< ?, ?, ? extends VerticesT > object,
        Class< VerticesT > verticesClass,
        Function< String, Vector > toVector,
        Function< Vector, Posistion > toPosition,
        Function< List< Posistion >[] , VerticesT > verticesProvider,
        String verticesText
    ) {
        if( verticesText == null ) {
            return;
        }
        try {
            object.getClass()
                .getMethod( METHOD_SET_VERTICES, verticesClass)
                .invoke( object,
                    verticesProvider.apply( toList( verticesText ).stream()
                        .map( ParseUtils::toList )
                        .map( verticesText1 -> verticesText1.stream()
                            .map( toVector )
                            .map( toPosition )
                            .collect( Collectors.toList() )
                        )
                        .toArray( List[]::new )
                    )
                )
            ;
        } catch ( Exception ex ) {
            throw new ParseFailedException( "Exception while setting up object texture", ex );
        }
    }
    
    @SuppressWarnings( { "unchecked", "rawtypes" } )
    private void setUvMap(
        PositionableObject< ?, ?, ? > object,
        String uvText
    ) {
        if( uvText == null ) {
            return;
        }
        try {
            object.getClass()
                .getMethod( METHOD_SET_UV_MAP, UvMap.class )
                .invoke( object, new UvMap( toList( uvText ).stream()
                        .map( ParseUtils::toList )
                        .map( uvText1 -> (List< UV >[]) uvText1.stream()
                            .map( ParseUtils::toList )
                            .map( uvTetxt2 -> uvTetxt2.stream()
                                .map( ParseUtils::toVec2 )
                                .map( UV::uv )
                                .collect( Collectors.toList() )
                            )
                            .toArray( List[]::new )
                        )
                        .toArray( List[][]::new )
                    )
                )
            ;
        } catch ( Exception ex ) {
            throw new ParseFailedException( "Exception while setting up object texture", ex );
        }
    }
    
    private void setColouring(
        PositionableObject< ?, ?, ? > object,
        String colouringText
    ) {
        if( colouringText == null ) {
            return;
        }
        try {
            object.getClass()
                .getMethod( METHOD_SET_COLOURING, Colouring.class )
                .invoke( object,
                    new Colouring( toList( colouringText ).stream()
                        .map( ParseUtils::toVec4 )
                        .map( Colour::col )
                        .collect( Collectors.toList() )
                    )
                )
            ;
        } catch ( Exception ex ) {
            throw new ParseFailedException( "Exception while setting up object texture", ex );
        }
    }
    
    private void setTexture(
        PositionableObject< ?, ?, ? > object,
        String textureName
    ) {
        if( textureName == null ) {
            return;
        }
        try {
            object.getClass()
                .getMethod( METHOD_SET_TEXTURE, Texture.class )
                .invoke( object, Texture.create( textureName ) );
        } catch ( Exception ex ) {
            throw new ParseFailedException( "Exception while setting up object texture", ex );
        }
    }
    
    @SuppressWarnings( "unchecked" )
    private void setTransformations2D(
        PositionableObject2D< ?, ? > object,
        String position,
        String rotation,
        String scale
    ) {
        setTransformations(
            object,
            Mutable2DTransformations::new,
            position,
            this::setPosition2D,
            rotation,
            this::setRotation2D,
            scale,
            this::setScale2D
        );
    }
    
    @SuppressWarnings( "unchecked" )
    private void setTransformations3D(
        PositionableObject3D< ?, ? > object,
        String position,
        String rotation,
        String scale
    ) {
        setTransformations(
            object,
            Mutable3DTransformations::new,
            position,
            this::setPosition3D,
            rotation,
            this::setRotation3D,
            scale,
            this::setScale3D
        );
    }
    
    @SuppressWarnings( "unchecked" )
    private < O extends PositionableObject<?,?,?>, T extends PositionableObjectTransformations<?,?,?> > void setTransformations(
        O object,
        Supplier< T > transformationsSupplier,
        String position,
        BiConsumer< String, T > positionApplier,
        String rotation,
        BiConsumer< String, T > rotationApplier,
        String scale,
        BiConsumer< String, T > scaleApplier
    ) {
        if ( position == null && rotation == null && scale == null ) {
            return;
        }
        try {
            var mutableObject = ( PositionableObject<T,?,?> ) object;
            var transformations = mutableObject.getTransformations();
            if ( transformations == null ) {
                mutableObject.setTransformations( transformations = transformationsSupplier.get() );
            }
            if ( position != null ) {
                positionApplier.accept( position, transformations );
            }
            if ( rotation != null ) {
                rotationApplier.accept( rotation, transformations );
            }
            if ( scale != null ) {
                scaleApplier.accept( scale, transformations );
            }
        } catch ( Exception ex ) {
            throw new ParseFailedException( "Exception while setting up object transformations", ex );
        }
    }
    
    private void setScale2D(
        String scale,
        Mutable2DTransformations transformations
    ) {
        var vec2 = scale2( ParseUtils.toVec2( scale ) );
        transformations.setScale( vec2 );
    }
    
    private void setRotation2D(
        String rotation,
        Mutable2DTransformations transformations
    ) {
        transformations.setRotation( ParseUtils.toVec1( rotation ) );
    }
    
    private void setPosition2D(
        String position,
        Mutable2DTransformations transformations
    ) {
        var vec2 = pos2( ParseUtils.toVec2( position ) );
        transformations.setPosition( vec2 );
    }
    
    private void setScale3D(
        String scale,
        Mutable3DTransformations transformations
    ) {
        var vec3 = scale3( ParseUtils.toVec3( scale ) );
        transformations.setScale( vec3 );
    }
    
    private void setRotation3D(
        String rotation,
        Mutable3DTransformations transformations
    ) {
        var vec3 = rot3( ParseUtils.toVec3( rotation ) );
        transformations.setRotation( vec3 );
    }
    
    private void setPosition3D(
        String position,
        Mutable3DTransformations transformations
    ) {
        var vec3 = pos3( ParseUtils.toVec3( position ) );
        transformations.setPosition( vec3 );
    }
    
    private PositionableObject2D< ?, ? > createObject2dInstance( String className ) {
        return createObjectInstance(
            className,
            PositionableObject2D.class,
            FirstOracleConstants.OBJECT_2D_PACKAGE_NAME
        );
    }
    
    private PositionableObject3D< ?, ? > createObject3dInstance( String className ) {
        return createObjectInstance(
            className,
            PositionableObject3D.class,
            FirstOracleConstants.OBJECT_3D_PACKAGE_NAME
        );
    }
    
    private < ObjectT extends PositionableObject< ?, ?, ? > > ObjectT createObjectInstance(
        String className,
        Class< ObjectT > typeClass,
        String packageName
    ) {
        try {
            var aClass = getObjectClass(
                className,
                typeClass,
                packageName
            );
            return aClass.getDeclaredConstructor().newInstance();
        } catch ( Exception e ) {
            throw new RuntimeException( e );
        }
    }
    
    private < T extends PositionableObject<?, ?, ?> > Class< ? extends T > getObjectClass(
        String className,
        Class< T > aClass,
        String defaultPackage
    ) {
        try {
            return getClass().getClassLoader().loadClass( className ).asSubclass( aClass );
        } catch ( Exception e1 ) {
            logger.log( Level.WARNING, "Exception while extracting object class " + className ,e1 );
            try {
                return getClass().getClassLoader()
                    .loadClass( defaultPackage + "." + className )
                    .asSubclass( aClass )
                ;
            } catch ( Exception e2 ) {
                e2.addSuppressed( e1 );
                throw new GraphicObjectClassNotFoundException( className, aClass, e2 );
            }
        }
    }
    
    private PositionableObject2D<?, ?> toObject2D( Composite node ) {
        return toObject(
            node,
            this::createObject2dInstance,
            PositionableObject2DImpl.class,
            this::setTransformations2D,
            this::setVertices2D
        );
    }
    
    private PositionableObject3D<?, ?> toObject3D( Composite node ) {
        return toObject(
            node,
            this::createObject3dInstance,
            PositionableObject3DImpl.class,
            this::setTransformations3D,
            this::setVertices3D
        );
    }
    
    private < ObjectT extends PositionableObject< ?, ?, ? > > ObjectT toObject(
        Composite node,
        Function< String, ObjectT > objectCreator,
        Class< ? extends ObjectT > defaultClass,
        QuadConsumer< ObjectT, String, String, String > transformationApplier,
        BiConsumer< ObjectT, String > verticesApplier
    ) {
        var object = objectCreator.apply( node.findValue( PARAM_CLASS, defaultClass.getName() ) );
        transformationApplier.accept(
            object,
            node.findValue( PARAM_POSITION, null ),
            node.findValue( PARAM_ROTATION, null ),
            node.findValue( PARAM_SCALE, null )
        );
        setTexture( object, node.findValue( PARAM_TEXTURE, null ) );
        setColouring( object, node.findValue( PARAM_COLOURING, null ) );
        setUvMap( object, node.findValue( PARAM_UV, null ) );
        verticesApplier.accept( object, node.findValue( PARAM_VERTICES, null ) );
        return object;
    }
    
    private Terrain3D< ? > toTerrain3D( Composite node ) {
        return null;
    }
    
    private interface TriConsumer< A, B, C > {
        void accept( A a, B b, C c);
    }
    
    private interface QuadConsumer< A, B, C, D > {
        void accept( A a, B b, C c, D d);
    }
}