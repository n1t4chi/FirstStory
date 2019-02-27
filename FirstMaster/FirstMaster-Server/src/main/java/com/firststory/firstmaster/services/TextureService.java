package com.firststory.firstmaster.services;

import com.firststory.firstinscriptions.TextureTransferData;
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
    public TextureTransferData findByName( String name ) {
        return TextureRepository.findByName( name );
    }

    @Transactional( readOnly = true )
    public Collection< TextureTransferData > findByNameLike( String name ) {
        return TextureRepository.findByNameLike( name );
    }
}
