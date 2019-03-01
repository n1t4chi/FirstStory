package com.firststory.firstmaster.controller;

import com.firststory.firstinscriptions.transfer.objects.TextureNode;
import com.firststory.firstmaster.services.TextureService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Collection;

@RestController
@RequestMapping( "/texture" )
public class TextureController {

    private final TextureService textureService;

    public TextureController( TextureService textureService ) {
        this.textureService = textureService;
    }

    @GetMapping( "" )
    public Collection< TextureNode > extractTerrain( @RequestParam( value = "name" ) String name ) {
        return textureService.findByNameLike( name );
    }
}
