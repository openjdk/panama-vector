/*
 * Copyright (c) 1994, 2017, Oracle and/or its affiliates. All rights reserved.
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS FILE HEADER.
 *
 * This code is free software; you can redistribute it and/or modify it
 * under the terms of the GNU General Public License version 2 only, as
 * published by the Free Software Foundation.  Oracle designates this
 * particular file as subject to the "Classpath" exception as provided
 * by Oracle in the LICENSE file that accompanied this code.
 *
 * This code is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or
 * FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License
 * version 2 for more details (a copy is included in the LICENSE file that
 * accompanied this code).
 *
 * You should have received a copy of the GNU General Public License version
 * 2 along with this work; if not, write to the Free Software Foundation,
 * Inc., 51 Franklin St, Fifth Floor, Boston, MA 02110-1301 USA.
 *
 * Please contact Oracle, 500 Oracle Parkway, Redwood Shores, CA 94065 USA
 * or visit www.oracle.com if you need additional information or have any
 * questions.
 */
package jdk.incubator.vector;

import jdk.internal.vm.annotation.IntrinsicCandidate;

/**
 * A specialized {@link Vector} representing an ordered immutable sequence of
 * {@code short} values.
 * @author abc
 * @version 1.0
 * @since  10/01/2021
 */
@SuppressWarnings("cast")
public final class Halffloat {
   /**
    * Display here: {@value}
   */
    static final short MAX_VALUE = valueOf(0x1.fffffeP+127f);
    /* Definitions for FP16 */
    static final short MIN_VALUE = valueOf(0x0.000002P-126f);
    /* Definitions for FP16 */
    static final short POSITIVE_INFINITY = valueOf(1.0f/0.0f);
    /* Definitions for FP16 */
    static final short NEGATIVE_INFINITY = valueOf(-1.0f/0.0f);
    /* Definitions for FP16 */
    static final int SIZE = 16;
    /* Definitions for FP16 */
    static final int BYTES = SIZE / Byte.SIZE;
    /* Definitions for FP16 */
    private final short value;

     /**
     * Returns a new Halffloat.
     * @param f the species describing the element type
     * @return short value of float provided
    */    public static Halffloat valueOf(short f) {
        return new Halffloat(f);
    }
    /**
     * Halffloat constructor
     * @param value short value assigned to halffloat
     */
     public Halffloat(short value) {
        this.value = value;
    }
     /**
     * Returns floatvalue of a given short value.
     * @return a float value of short provided
     */
    public float floatValue() {
        int val = (int)value;
        float f = Float.intBitsToFloat(((val&0x8000)<<16) | (((val&0x7c00)+0x1C000)<<13) | ((val&0x03FF)<<13));
        return f;
    }
    /**
     * Returns halffloat value of a given float.
     * @param f float value to be converted into halffloat
     * @return short value of float provided
    */
    public static short valueOf(float f) {
        int val = Float.floatToIntBits(f);
        val = ((((val>>16)&0x8000)|((((val&0x7f800000)-0x38000000)>>13)&0x7c00)|((val>>13)&0x03ff)));
        return (short)val;
    }
    /**
     * Returns the size, in bits, of vectors of this shape.
     * @param bits the species describing the element type
     * @return short value of float provided
    */
    public static short shortBitsToHalffloat(short bits) {
        return bits;
    }
    /**
     * Returns the size, in bits, of vectors of this shape.
     * @param bits the species describing the element type
     * @return short value of float provided
    */
    public static short shortToRawShortBits(short bits) {
        return bits;
    }
    /**
     * Returns the size, in bits, of vectors of this shape.
     * @param bits the species describing the element type
     * @return short value of float provided
    */
    public static short shortToShortBits(short bits) {
        return bits;
    }
}
