package com.firststory.firstmaster.controller;

import com.firststory.firstinscriptions.transfer.objects.TerrainNode;
import com.firststory.firstmaster.services.TerrainService;
import org.springframework.web.bind.annotation.*;

import java.util.Collection;
import java.util.Map;

@RestController
@RequestMapping( "/terrain" )
public class TerrainController {

    private final TerrainService terrainService;

    public TerrainController( TerrainService terrainService ) {
        this.terrainService = terrainService;
    }

    @GetMapping( "/graph" )
    public Map< String, Object > graph( @RequestParam( value = "limit", required = false ) Integer limit ) {
        return terrainService.graph( limit == null ? 100 : limit );
    }

    @GetMapping( "" )
    public Collection< TerrainNode > findTerrains( @RequestParam( value = "pattern" ) String pattern ) {
        return terrainService.findByNameLike( pattern );
    }

    @PutMapping( "" )
    public Collection< TerrainNode > saveTerrains( @RequestParam( value = "terrains" ) Collection< TerrainNode > terrains ) {
        return terrainService.save( terrains );
    }

    @DeleteMapping( "" )
    public void deleteTerrains( @RequestParam( value = "terrains" ) Collection< TerrainNode > terrains ) {
        terrainService.delete( terrains );
    }
}
