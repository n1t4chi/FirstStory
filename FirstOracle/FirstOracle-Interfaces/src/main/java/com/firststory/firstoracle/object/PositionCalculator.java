/*
 * Copyright (c) 2018 Piotr "n1t4chi" Olejarz
 */

package com.firststory.firstoracle.object;

import com.firststory.firstoracle.data.*;

/**
 * @author n1t4chi
 */
public interface PositionCalculator< IndexType extends Index, PositionType extends Position > {
    
    PositionType indexToPosition( IndexType index, IndexType shift );
    
    IndexType positionToIndex( PositionType position, IndexType shift );
}
