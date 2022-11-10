/*
 * Copyright (c) 1994, 2022, Oracle and/or its affiliates. All rights reserved.
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
@SuppressWarnings("serial")
public final class Halffloat extends Number implements Comparable<Halffloat>{
    /** Definitions for FP16*/
    public static final short MAX_VALUE = 0x7bff;
    /** Definitions for FP16 */
    public static final short MIN_VALUE = 0x400;
    /** Definitions for FP16 */
    public static final short POSITIVE_INFINITY = 0x7c00;
    /** Definitions for FP16 */
    public static final short NEGATIVE_INFINITY = (short)0xfc00;
    /** Definitions for FP16*/
    public static final short NaN = (short)0x7e00;
    /** Definitions for FP16*/
    private static final float MAX_FLOAT_VALUE = 0x1.ffep+15f;
    /** Definitions for FP16*/
    private static final float MIN_FLOAT_VALUE = 0x1.004p-14f;
    /** Definitions for FP16 */
    public static final int SIZE = 16;
    /** Definitions for FP16 */
    public static final int BYTES = SIZE / Byte.SIZE;
    /** Definitions for FP16 */
    private final short value;

    /**
    * Returns a new Halffloat.
    * @param f the species describing the element type
    * @return short value of float provided
    */
    public static Halffloat valueOf(short f) {
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
    * Halffloat constructor
    * @param f float value assigned to halffloat
    */
    public Halffloat(float f) {
        this.value = Float.floatToFloat16(f);
    }

    /**
    * Returns floatvalue of a given short value.
    * @return a float value of short provided
    */
    public float floatValue() {
       return Float.float16ToFloat(value);
    }

    /**
     * Returns halffloat value of a given float.
     * @param f float value to be converted into halffloat
     * @return short value of float provided
    */
    public static short valueOf(float f) {
        if (Float.isNaN(f)) {
            return Halffloat.NaN;
        }
        return Float.floatToFloat16(f);
    }

    /** doublevalue */
    public double doubleValue() {
       return (double) floatValue();
    }

    /** longValue */
    public long longValue() {
       return (long) value;
    }

    /** IntValue */
    public int intValue() {
       return (int) value;
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

    /**
       Compares two halffloats
     * @param hf value to be compared
     * @return 0, 1, -1
    */
    public int compareTo(Halffloat hf) {
        float f1 = floatValue();
        float f2 = hf.floatValue();
        return Float.compare(f1, f2);
    }
}
