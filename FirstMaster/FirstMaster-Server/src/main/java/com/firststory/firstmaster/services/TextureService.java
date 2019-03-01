package com.firststory.firstmaster.services;

import com.firststory.firstinscriptions.transfer.objects.TextureNode;
import com.firststory.firstmaster.repos.TextureRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collection;

@Service
public class TextureService {

    private final TextureRepository TextureRepository;

    public TextureService( TextureRepository textureRepository ) {
        this.TextureRepository = textureRepository;
    }

    @Transactional( readOnly = true )
    public TextureNode findByName( String name ) {
        return TextureRepository.findByName( name );
    }

    @Transactional( readOnly = true )
    public Collection< TextureNode > findByNameLike( String name ) {
        return TextureRepository.findByNameLike( name );
    }
}
