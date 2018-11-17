/*
 * Copyright (c) 2018 Piotr "n1t4chi" Olejarz
 */

package com.firststory.firstoracle.input.parsers.classes;

import com.firststory.firstoracle.object.PositionableObject;

/**
 * @author n1t4chi
 */
public abstract class ObjectClassParser< Type extends PositionableObject< ?, ?, ?, ?, ?, ? > > extends ShareableClassParser< Type > {
}
