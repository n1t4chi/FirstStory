/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.firststory.objects3D;

/**
 *
 * @author n1t4chi
 */
public enum Object3DType {
    /**
     * Cube object.
     */
    CUBE{
        @Override
        public String toString() {
            return "Cube";
        }
    },
    /**
     * Plane object.
     */
    PLANE{
        @Override
        public String toString() {
            return "Plane";
        }
    },
    /**
     * Hexagonal Prism object.
     */
    HEXPRISM{
        @Override
        public String toString() {
            return "Hexagonal Prism";
        }
    },
    /**
     * Polyhedron object.
     */
    POLYHEDRON{
        @Override
        public String toString() {
            return "Polyhedron";
        }
    },
}
