/*
 * Copyright (c) 2020, Oracle and/or its affiliates. All rights reserved.
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS FILE HEADER.
 *
 * This code is free software; you can redistribute it and/or modify it
 * under the terms of the GNU General Public License version 2 only, as
 * published by the Free Software Foundation.
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

import jdk.incubator.vector.ByteVector;
import jdk.incubator.vector.DoubleVector;
import jdk.incubator.vector.FloatVector;
import jdk.incubator.vector.IntVector;
import jdk.incubator.vector.LongVector;
import jdk.incubator.vector.ShortVector;
import jdk.incubator.vector.VectorOperators;
import org.testng.annotations.Test;

import java.util.function.IntFunction;

/**
 * @test
 * @modules jdk.incubator.vector
 * @modules java.base/jdk.internal.vm.annotation
 * @run testng/othervm  -XX:-TieredCompilation --add-opens jdk.incubator.vector/jdk.incubator.vector=ALL-UNNAMED
 * Vector128ConversionTests
 */

@Test
public class Vector128ConversionTests extends AbstractVectorConversionTest {

    @Test(dataProvider = "byteUnaryOpProvider")
    static void convertB2B_128_To_128(IntFunction<byte[]> fa) {
        byte[] a = fa.apply(1024);
        conversion_kernel(ByteVector.SPECIES_128, ByteVector.SPECIES_128,
                a, B2B,
                ConvAPI.CONVERT, a.length);
    }

    @Test(dataProvider = "byteUnaryOpProvider")
    static void convertB2S_128_To_128(IntFunction<byte[]> fa) {
        byte[] a = fa.apply(1024);
        conversion_kernel(ByteVector.SPECIES_128, ShortVector.SPECIES_128,
                a, VectorOperators.B2S,
                ConvAPI.CONVERT, a.length);
    }

    @Test(dataProvider = "byteUnaryOpProvider")
    static void convertB2I_128_To_128(IntFunction<byte[]> fa) {
        byte[] a = fa.apply(1024);
        conversion_kernel(ByteVector.SPECIES_128, IntVector.SPECIES_128,
                a, VectorOperators.B2I,
                ConvAPI.CONVERT, a.length);
    }

    @Test(dataProvider = "byteUnaryOpProvider")
    static void convertB2L_128_To_128(IntFunction<byte[]> fa) {
        byte[] a = fa.apply(1024);
        conversion_kernel(ByteVector.SPECIES_128, LongVector.SPECIES_128,
                a, VectorOperators.B2L,
                ConvAPI.CONVERT, a.length);
    }

    @Test(dataProvider = "byteUnaryOpProvider")
    static void convertB2F_128_To_128(IntFunction<byte[]> fa) {
        byte[] a = fa.apply(1024);
        conversion_kernel(ByteVector.SPECIES_128, FloatVector.SPECIES_128,
                a, VectorOperators.B2F,
                ConvAPI.CONVERT, a.length);
    }

    @Test(dataProvider = "byteUnaryOpProvider")
    static void convertB2D_128_To_128(IntFunction<byte[]> fa) {
        byte[] a = fa.apply(1024);
        conversion_kernel(ByteVector.SPECIES_128, DoubleVector.SPECIES_128,
                a, VectorOperators.B2D,
                ConvAPI.CONVERT, a.length);
    }

    @Test(dataProvider = "byteUnaryOpProvider")
    static void convertShapeB2B_128_To_64(IntFunction<byte[]> fa) {
        byte[] a = fa.apply(1024);
        conversion_kernel(ByteVector.SPECIES_128, ByteVector.SPECIES_64,
                a, B2B,
                ConvAPI.CONVERTSHAPE, a.length);
    }

    @Test(dataProvider = "byteUnaryOpProvider")
    static void convertShapeB2B_128_To_128(IntFunction<byte[]> fa) {
        byte[] a = fa.apply(1024);
        conversion_kernel(ByteVector.SPECIES_128, ByteVector.SPECIES_128,
                a, B2B,
                ConvAPI.CONVERTSHAPE, a.length);
    }

    @Test(dataProvider = "byteUnaryOpProvider")
    static void convertShapeB2B_128_To_256(IntFunction<byte[]> fa) {
        byte[] a = fa.apply(1024);
        conversion_kernel(ByteVector.SPECIES_128, ByteVector.SPECIES_256,
                a, B2B,
                ConvAPI.CONVERTSHAPE, a.length);
    }

    @Test(dataProvider = "byteUnaryOpProvider")
    static void convertShapeB2B_128_To_512(IntFunction<byte[]> fa) {
        byte[] a = fa.apply(1024);
        conversion_kernel(ByteVector.SPECIES_128, ByteVector.SPECIES_512,
                a, B2B,
                ConvAPI.CONVERTSHAPE, a.length);
    }

    @Test(dataProvider = "byteUnaryOpProvider")
    static void convertShapeB2B_128_To_MAX(IntFunction<byte[]> fa) {
        byte[] a = fa.apply(1024);
        conversion_kernel(ByteVector.SPECIES_128, ByteVector.SPECIES_MAX,
                a, B2B,
                ConvAPI.CONVERTSHAPE, a.length);
    }

    @Test(dataProvider = "byteUnaryOpProvider")
    static void convertShapeB2S_128_To_64(IntFunction<byte[]> fa) {
        byte[] a = fa.apply(1024);
        conversion_kernel(ByteVector.SPECIES_128, ShortVector.SPECIES_64,
                a, VectorOperators.B2S,
                ConvAPI.CONVERTSHAPE, a.length);
    }

    @Test(dataProvider = "byteUnaryOpProvider")
    static void convertShapeB2S_128_To_128(IntFunction<byte[]> fa) {
        byte[] a = fa.apply(1024);
        conversion_kernel(ByteVector.SPECIES_128, ShortVector.SPECIES_128,
                a, VectorOperators.B2S,
                ConvAPI.CONVERTSHAPE, a.length);
    }

    @Test(dataProvider = "byteUnaryOpProvider")
    static void convertShapeB2S_128_To_256(IntFunction<byte[]> fa) {
        byte[] a = fa.apply(1024);
        conversion_kernel(ByteVector.SPECIES_128, ShortVector.SPECIES_256,
                a, VectorOperators.B2S,
                ConvAPI.CONVERTSHAPE, a.length);
    }

    @Test(dataProvider = "byteUnaryOpProvider")
    static void convertShapeB2S_128_To_512(IntFunction<byte[]> fa) {
        byte[] a = fa.apply(1024);
        conversion_kernel(ByteVector.SPECIES_128, ShortVector.SPECIES_512,
                a, VectorOperators.B2S,
                ConvAPI.CONVERTSHAPE, a.length);
    }

    @Test(dataProvider = "byteUnaryOpProvider")
    static void convertShapeB2S_128_To_MAX(IntFunction<byte[]> fa) {
        byte[] a = fa.apply(1024);
        conversion_kernel(ByteVector.SPECIES_128, ShortVector.SPECIES_MAX,
                a, VectorOperators.B2S,
                ConvAPI.CONVERTSHAPE, a.length);
    }

    @Test(dataProvider = "byteUnaryOpProvider")
    static void convertShapeB2I_128_To_64(IntFunction<byte[]> fa) {
        byte[] a = fa.apply(1024);
        conversion_kernel(ByteVector.SPECIES_128, IntVector.SPECIES_64,
                a, VectorOperators.B2I,
                ConvAPI.CONVERTSHAPE, a.length);
    }

    @Test(dataProvider = "byteUnaryOpProvider")
    static void convertShapeB2I_128_To_128(IntFunction<byte[]> fa) {
        byte[] a = fa.apply(1024);
        conversion_kernel(ByteVector.SPECIES_128, IntVector.SPECIES_128,
                a, VectorOperators.B2I,
                ConvAPI.CONVERTSHAPE, a.length);
    }

    @Test(dataProvider = "byteUnaryOpProvider")
    static void convertShapeB2I_128_To_256(IntFunction<byte[]> fa) {
        byte[] a = fa.apply(1024);
        conversion_kernel(ByteVector.SPECIES_128, IntVector.SPECIES_256,
                a, VectorOperators.B2I,
                ConvAPI.CONVERTSHAPE, a.length);
    }

    @Test(dataProvider = "byteUnaryOpProvider")
    static void convertShapeB2I_128_To_512(IntFunction<byte[]> fa) {
        byte[] a = fa.apply(1024);
        conversion_kernel(ByteVector.SPECIES_128, IntVector.SPECIES_512,
                a, VectorOperators.B2I,
                ConvAPI.CONVERTSHAPE, a.length);
    }

    @Test(dataProvider = "byteUnaryOpProvider")
    static void convertShapeB2I_128_To_MAX(IntFunction<byte[]> fa) {
        byte[] a = fa.apply(1024);
        conversion_kernel(ByteVector.SPECIES_128, IntVector.SPECIES_MAX,
                a, VectorOperators.B2I,
                ConvAPI.CONVERTSHAPE, a.length);
    }

    @Test(dataProvider = "byteUnaryOpProvider")
    static void convertShapeB2L_128_To_64(IntFunction<byte[]> fa) {
        byte[] a = fa.apply(1024);
        conversion_kernel(ByteVector.SPECIES_128, LongVector.SPECIES_64,
                a, VectorOperators.B2L,
                ConvAPI.CONVERTSHAPE, a.length);
    }

    @Test(dataProvider = "byteUnaryOpProvider")
    static void convertShapeB2L_128_To_128(IntFunction<byte[]> fa) {
        byte[] a = fa.apply(1024);
        conversion_kernel(ByteVector.SPECIES_128, LongVector.SPECIES_128,
                a, VectorOperators.B2L,
                ConvAPI.CONVERTSHAPE, a.length);
    }

    @Test(dataProvider = "byteUnaryOpProvider")
    static void convertShapeB2L_128_To_256(IntFunction<byte[]> fa) {
        byte[] a = fa.apply(1024);
        conversion_kernel(ByteVector.SPECIES_128, LongVector.SPECIES_256,
                a, VectorOperators.B2L,
                ConvAPI.CONVERTSHAPE, a.length);
    }

    @Test(dataProvider = "byteUnaryOpProvider")
    static void convertShapeB2L_128_To_512(IntFunction<byte[]> fa) {
        byte[] a = fa.apply(1024);
        conversion_kernel(ByteVector.SPECIES_128, LongVector.SPECIES_512,
                a, VectorOperators.B2L,
                ConvAPI.CONVERTSHAPE, a.length);
    }

    @Test(dataProvider = "byteUnaryOpProvider")
    static void convertShapeB2L_128_To_MAX(IntFunction<byte[]> fa) {
        byte[] a = fa.apply(1024);
        conversion_kernel(ByteVector.SPECIES_128, LongVector.SPECIES_MAX,
                a, VectorOperators.B2L,
                ConvAPI.CONVERTSHAPE, a.length);
    }

    @Test(dataProvider = "byteUnaryOpProvider")
    static void convertShapeB2F_128_To_64(IntFunction<byte[]> fa) {
        byte[] a = fa.apply(1024);
        conversion_kernel(ByteVector.SPECIES_128, FloatVector.SPECIES_64,
                a, VectorOperators.B2F,
                ConvAPI.CONVERTSHAPE, a.length);
    }

    @Test(dataProvider = "byteUnaryOpProvider")
    static void convertShapeB2F_128_To_128(IntFunction<byte[]> fa) {
        byte[] a = fa.apply(1024);
        conversion_kernel(ByteVector.SPECIES_128, FloatVector.SPECIES_128,
                a, VectorOperators.B2F,
                ConvAPI.CONVERTSHAPE, a.length);
    }

    @Test(dataProvider = "byteUnaryOpProvider")
    static void convertShapeB2F_128_To_256(IntFunction<byte[]> fa) {
        byte[] a = fa.apply(1024);
        conversion_kernel(ByteVector.SPECIES_128, FloatVector.SPECIES_256,
                a, VectorOperators.B2F,
                ConvAPI.CONVERTSHAPE, a.length);
    }

    @Test(dataProvider = "byteUnaryOpProvider")
    static void convertShapeB2F_128_To_512(IntFunction<byte[]> fa) {
        byte[] a = fa.apply(1024);
        conversion_kernel(ByteVector.SPECIES_128, FloatVector.SPECIES_512,
                a, VectorOperators.B2F,
                ConvAPI.CONVERTSHAPE, a.length);
    }

    @Test(dataProvider = "byteUnaryOpProvider")
    static void convertShapeB2F_128_To_MAX(IntFunction<byte[]> fa) {
        byte[] a = fa.apply(1024);
        conversion_kernel(ByteVector.SPECIES_128, FloatVector.SPECIES_MAX,
                a, VectorOperators.B2F,
                ConvAPI.CONVERTSHAPE, a.length);
    }

    @Test(dataProvider = "byteUnaryOpProvider")
    static void convertShapeB2D_128_To_64(IntFunction<byte[]> fa) {
        byte[] a = fa.apply(1024);
        conversion_kernel(ByteVector.SPECIES_128, DoubleVector.SPECIES_64,
                a, VectorOperators.B2D,
                ConvAPI.CONVERTSHAPE, a.length);
    }

    @Test(dataProvider = "byteUnaryOpProvider")
    static void convertShapeB2D_128_To_128(IntFunction<byte[]> fa) {
        byte[] a = fa.apply(1024);
        conversion_kernel(ByteVector.SPECIES_128, DoubleVector.SPECIES_128,
                a, VectorOperators.B2D,
                ConvAPI.CONVERTSHAPE, a.length);
    }

    @Test(dataProvider = "byteUnaryOpProvider")
    static void convertShapeB2D_128_To_256(IntFunction<byte[]> fa) {
        byte[] a = fa.apply(1024);
        conversion_kernel(ByteVector.SPECIES_128, DoubleVector.SPECIES_256,
                a, VectorOperators.B2D,
                ConvAPI.CONVERTSHAPE, a.length);
    }

    @Test(dataProvider = "byteUnaryOpProvider")
    static void convertShapeB2D_128_To_512(IntFunction<byte[]> fa) {
        byte[] a = fa.apply(1024);
        conversion_kernel(ByteVector.SPECIES_128, DoubleVector.SPECIES_512,
                a, VectorOperators.B2D,
                ConvAPI.CONVERTSHAPE, a.length);
    }

    @Test(dataProvider = "byteUnaryOpProvider")
    static void convertShapeB2D_128_To_MAX(IntFunction<byte[]> fa) {
        byte[] a = fa.apply(1024);
        conversion_kernel(ByteVector.SPECIES_128, DoubleVector.SPECIES_MAX,
                a, VectorOperators.B2D,
                ConvAPI.CONVERTSHAPE, a.length);
    }

    @Test(dataProvider = "byteUnaryOpProvider")
    static void castShapeB2B_128_To_64(IntFunction<byte[]> fa) {
        byte[] a = fa.apply(1024);
        conversion_kernel(ByteVector.SPECIES_128, ByteVector.SPECIES_64,
                a, B2B,
                ConvAPI.CASTSHAPE, a.length);
    }

    @Test(dataProvider = "byteUnaryOpProvider")
    static void castShapeB2B_128_To_128(IntFunction<byte[]> fa) {
        byte[] a = fa.apply(1024);
        conversion_kernel(ByteVector.SPECIES_128, ByteVector.SPECIES_128,
                a, B2B,
                ConvAPI.CASTSHAPE, a.length);
    }

    @Test(dataProvider = "byteUnaryOpProvider")
    static void castShapeB2B_128_To_256(IntFunction<byte[]> fa) {
        byte[] a = fa.apply(1024);
        conversion_kernel(ByteVector.SPECIES_128, ByteVector.SPECIES_256,
                a, B2B,
                ConvAPI.CASTSHAPE, a.length);
    }

    @Test(dataProvider = "byteUnaryOpProvider")
    static void castShapeB2B_128_To_512(IntFunction<byte[]> fa) {
        byte[] a = fa.apply(1024);
        conversion_kernel(ByteVector.SPECIES_128, ByteVector.SPECIES_512,
                a, B2B,
                ConvAPI.CASTSHAPE, a.length);
    }

    @Test(dataProvider = "byteUnaryOpProvider")
    static void castShapeB2B_128_To_MAX(IntFunction<byte[]> fa) {
        byte[] a = fa.apply(1024);
        conversion_kernel(ByteVector.SPECIES_128, ByteVector.SPECIES_MAX,
                a, B2B,
                ConvAPI.CASTSHAPE, a.length);
    }

    @Test(dataProvider = "byteUnaryOpProvider")
    static void castShapeB2S_128_To_64(IntFunction<byte[]> fa) {
        byte[] a = fa.apply(1024);
        conversion_kernel(ByteVector.SPECIES_128, ShortVector.SPECIES_64,
                a, VectorOperators.B2S,
                ConvAPI.CASTSHAPE, a.length);
    }

    @Test(dataProvider = "byteUnaryOpProvider")
    static void castShapeB2S_128_To_128(IntFunction<byte[]> fa) {
        byte[] a = fa.apply(1024);
        conversion_kernel(ByteVector.SPECIES_128, ShortVector.SPECIES_128,
                a, VectorOperators.B2S,
                ConvAPI.CASTSHAPE, a.length);
    }

    @Test(dataProvider = "byteUnaryOpProvider")
    static void castShapeB2S_128_To_256(IntFunction<byte[]> fa) {
        byte[] a = fa.apply(1024);
        conversion_kernel(ByteVector.SPECIES_128, ShortVector.SPECIES_256,
                a, VectorOperators.B2S,
                ConvAPI.CASTSHAPE, a.length);
    }

    @Test(dataProvider = "byteUnaryOpProvider")
    static void castShapeB2S_128_To_512(IntFunction<byte[]> fa) {
        byte[] a = fa.apply(1024);
        conversion_kernel(ByteVector.SPECIES_128, ShortVector.SPECIES_512,
                a, VectorOperators.B2S,
                ConvAPI.CASTSHAPE, a.length);
    }

    @Test(dataProvider = "byteUnaryOpProvider")
    static void castShapeB2S_128_To_MAX(IntFunction<byte[]> fa) {
        byte[] a = fa.apply(1024);
        conversion_kernel(ByteVector.SPECIES_128, ShortVector.SPECIES_MAX,
                a, VectorOperators.B2S,
                ConvAPI.CASTSHAPE, a.length);
    }

    @Test(dataProvider = "byteUnaryOpProvider")
    static void castShapeB2I_128_To_64(IntFunction<byte[]> fa) {
        byte[] a = fa.apply(1024);
        conversion_kernel(ByteVector.SPECIES_128, IntVector.SPECIES_64,
                a, VectorOperators.B2I,
                ConvAPI.CASTSHAPE, a.length);
    }

    @Test(dataProvider = "byteUnaryOpProvider")
    static void castShapeB2I_128_To_128(IntFunction<byte[]> fa) {
        byte[] a = fa.apply(1024);
        conversion_kernel(ByteVector.SPECIES_128, IntVector.SPECIES_128,
                a, VectorOperators.B2I,
                ConvAPI.CASTSHAPE, a.length);
    }

    @Test(dataProvider = "byteUnaryOpProvider")
    static void castShapeB2I_128_To_256(IntFunction<byte[]> fa) {
        byte[] a = fa.apply(1024);
        conversion_kernel(ByteVector.SPECIES_128, IntVector.SPECIES_256,
                a, VectorOperators.B2I,
                ConvAPI.CASTSHAPE, a.length);
    }

    @Test(dataProvider = "byteUnaryOpProvider")
    static void castShapeB2I_128_To_512(IntFunction<byte[]> fa) {
        byte[] a = fa.apply(1024);
        conversion_kernel(ByteVector.SPECIES_128, IntVector.SPECIES_512,
                a, VectorOperators.B2I,
                ConvAPI.CASTSHAPE, a.length);
    }

    @Test(dataProvider = "byteUnaryOpProvider")
    static void castShapeB2I_128_To_MAX(IntFunction<byte[]> fa) {
        byte[] a = fa.apply(1024);
        conversion_kernel(ByteVector.SPECIES_128, IntVector.SPECIES_MAX,
                a, VectorOperators.B2I,
                ConvAPI.CASTSHAPE, a.length);
    }

    @Test(dataProvider = "byteUnaryOpProvider")
    static void castShapeB2L_128_To_64(IntFunction<byte[]> fa) {
        byte[] a = fa.apply(1024);
        conversion_kernel(ByteVector.SPECIES_128, LongVector.SPECIES_64,
                a, VectorOperators.B2L,
                ConvAPI.CASTSHAPE, a.length);
    }

    @Test(dataProvider = "byteUnaryOpProvider")
    static void castShapeB2L_128_To_128(IntFunction<byte[]> fa) {
        byte[] a = fa.apply(1024);
        conversion_kernel(ByteVector.SPECIES_128, LongVector.SPECIES_128,
                a, VectorOperators.B2L,
                ConvAPI.CASTSHAPE, a.length);
    }

    @Test(dataProvider = "byteUnaryOpProvider")
    static void castShapeB2L_128_To_256(IntFunction<byte[]> fa) {
        byte[] a = fa.apply(1024);
        conversion_kernel(ByteVector.SPECIES_128, LongVector.SPECIES_256,
                a, VectorOperators.B2L,
                ConvAPI.CASTSHAPE, a.length);
    }

    @Test(dataProvider = "byteUnaryOpProvider")
    static void castShapeB2L_128_To_512(IntFunction<byte[]> fa) {
        byte[] a = fa.apply(1024);
        conversion_kernel(ByteVector.SPECIES_128, LongVector.SPECIES_512,
                a, VectorOperators.B2L,
                ConvAPI.CASTSHAPE, a.length);
    }

    @Test(dataProvider = "byteUnaryOpProvider")
    static void castShapeB2L_128_To_MAX(IntFunction<byte[]> fa) {
        byte[] a = fa.apply(1024);
        conversion_kernel(ByteVector.SPECIES_128, LongVector.SPECIES_MAX,
                a, VectorOperators.B2L,
                ConvAPI.CASTSHAPE, a.length);
    }

    @Test(dataProvider = "byteUnaryOpProvider")
    static void castShapeB2F_128_To_64(IntFunction<byte[]> fa) {
        byte[] a = fa.apply(1024);
        conversion_kernel(ByteVector.SPECIES_128, FloatVector.SPECIES_64,
                a, VectorOperators.B2F,
                ConvAPI.CASTSHAPE, a.length);
    }

    @Test(dataProvider = "byteUnaryOpProvider")
    static void castShapeB2F_128_To_128(IntFunction<byte[]> fa) {
        byte[] a = fa.apply(1024);
        conversion_kernel(ByteVector.SPECIES_128, FloatVector.SPECIES_128,
                a, VectorOperators.B2F,
                ConvAPI.CASTSHAPE, a.length);
    }

    @Test(dataProvider = "byteUnaryOpProvider")
    static void castShapeB2F_128_To_256(IntFunction<byte[]> fa) {
        byte[] a = fa.apply(1024);
        conversion_kernel(ByteVector.SPECIES_128, FloatVector.SPECIES_256,
                a, VectorOperators.B2F,
                ConvAPI.CASTSHAPE, a.length);
    }

    @Test(dataProvider = "byteUnaryOpProvider")
    static void castShapeB2F_128_To_512(IntFunction<byte[]> fa) {
        byte[] a = fa.apply(1024);
        conversion_kernel(ByteVector.SPECIES_128, FloatVector.SPECIES_512,
                a, VectorOperators.B2F,
                ConvAPI.CASTSHAPE, a.length);
    }

    @Test(dataProvider = "byteUnaryOpProvider")
    static void castShapeB2F_128_To_MAX(IntFunction<byte[]> fa) {
        byte[] a = fa.apply(1024);
        conversion_kernel(ByteVector.SPECIES_128, FloatVector.SPECIES_MAX,
                a, VectorOperators.B2F,
                ConvAPI.CASTSHAPE, a.length);
    }

    @Test(dataProvider = "byteUnaryOpProvider")
    static void castShapeB2D_128_To_64(IntFunction<byte[]> fa) {
        byte[] a = fa.apply(1024);
        conversion_kernel(ByteVector.SPECIES_128, DoubleVector.SPECIES_64,
                a, VectorOperators.B2D,
                ConvAPI.CASTSHAPE, a.length);
    }

    @Test(dataProvider = "byteUnaryOpProvider")
    static void castShapeB2D_128_To_128(IntFunction<byte[]> fa) {
        byte[] a = fa.apply(1024);
        conversion_kernel(ByteVector.SPECIES_128, DoubleVector.SPECIES_128,
                a, VectorOperators.B2D,
                ConvAPI.CASTSHAPE, a.length);
    }

    @Test(dataProvider = "byteUnaryOpProvider")
    static void castShapeB2D_128_To_256(IntFunction<byte[]> fa) {
        byte[] a = fa.apply(1024);
        conversion_kernel(ByteVector.SPECIES_128, DoubleVector.SPECIES_256,
                a, VectorOperators.B2D,
                ConvAPI.CASTSHAPE, a.length);
    }

    @Test(dataProvider = "byteUnaryOpProvider")
    static void castShapeB2D_128_To_512(IntFunction<byte[]> fa) {
        byte[] a = fa.apply(1024);
        conversion_kernel(ByteVector.SPECIES_128, DoubleVector.SPECIES_512,
                a, VectorOperators.B2D,
                ConvAPI.CASTSHAPE, a.length);
    }

    @Test(dataProvider = "byteUnaryOpProvider")
    static void castShapeB2D_128_To_MAX(IntFunction<byte[]> fa) {
        byte[] a = fa.apply(1024);
        conversion_kernel(ByteVector.SPECIES_128, DoubleVector.SPECIES_MAX,
                a, VectorOperators.B2D,
                ConvAPI.CASTSHAPE, a.length);
    }

    @Test(dataProvider = "byteUnaryOpProvider")
    static void reinterpretShapeB2B_128_To_64(IntFunction<byte[]> fa) {
        byte[] a = fa.apply(1024);
        reinterpret_kernel(ByteVector.SPECIES_128, ByteVector.SPECIES_64,
                a, a.length);
    }

    @Test(dataProvider = "byteUnaryOpProvider")
    static void reinterpretShapeB2B_128_To_128(IntFunction<byte[]> fa) {
        byte[] a = fa.apply(1024);
        reinterpret_kernel(ByteVector.SPECIES_128, ByteVector.SPECIES_128,
                a, a.length);
    }

    @Test(dataProvider = "byteUnaryOpProvider")
    static void reinterpretShapeB2B_128_To_256(IntFunction<byte[]> fa) {
        byte[] a = fa.apply(1024);
        reinterpret_kernel(ByteVector.SPECIES_128, ByteVector.SPECIES_256,
                a, a.length);
    }

    @Test(dataProvider = "byteUnaryOpProvider")
    static void reinterpretShapeB2B_128_To_512(IntFunction<byte[]> fa) {
        byte[] a = fa.apply(1024);
        reinterpret_kernel(ByteVector.SPECIES_128, ByteVector.SPECIES_512,
                a, a.length);
    }

    @Test(dataProvider = "byteUnaryOpProvider")
    static void reinterpretShapeB2B_128_To_MAX(IntFunction<byte[]> fa) {
        byte[] a = fa.apply(1024);
        reinterpret_kernel(ByteVector.SPECIES_128, ByteVector.SPECIES_MAX,
                a, a.length);
    }

    @Test(dataProvider = "byteUnaryOpProvider")
    static void reinterpretShapeB2S_128_To_64(IntFunction<byte[]> fa) {
        byte[] a = fa.apply(1024);
        reinterpret_kernel(ByteVector.SPECIES_128, ShortVector.SPECIES_64,
                a, a.length);
    }

    @Test(dataProvider = "byteUnaryOpProvider")
    static void reinterpretShapeB2S_128_To_128(IntFunction<byte[]> fa) {
        byte[] a = fa.apply(1024);
        reinterpret_kernel(ByteVector.SPECIES_128, ShortVector.SPECIES_128,
                a, a.length);
    }

    @Test(dataProvider = "byteUnaryOpProvider")
    static void reinterpretShapeB2S_128_To_256(IntFunction<byte[]> fa) {
        byte[] a = fa.apply(1024);
        reinterpret_kernel(ByteVector.SPECIES_128, ShortVector.SPECIES_256,
                a, a.length);
    }

    @Test(dataProvider = "byteUnaryOpProvider")
    static void reinterpretShapeB2S_128_To_512(IntFunction<byte[]> fa) {
        byte[] a = fa.apply(1024);
        reinterpret_kernel(ByteVector.SPECIES_128, ShortVector.SPECIES_512,
                a, a.length);
    }

    @Test(dataProvider = "byteUnaryOpProvider")
    static void reinterpretShapeB2S_128_To_MAX(IntFunction<byte[]> fa) {
        byte[] a = fa.apply(1024);
        reinterpret_kernel(ByteVector.SPECIES_128, ShortVector.SPECIES_MAX,
                a, a.length);
    }

    @Test(dataProvider = "byteUnaryOpProvider")
    static void reinterpretShapeB2I_128_To_64(IntFunction<byte[]> fa) {
        byte[] a = fa.apply(1024);
        reinterpret_kernel(ByteVector.SPECIES_128, IntVector.SPECIES_64,
                a, a.length);
    }

    @Test(dataProvider = "byteUnaryOpProvider")
    static void reinterpretShapeB2I_128_To_128(IntFunction<byte[]> fa) {
        byte[] a = fa.apply(1024);
        reinterpret_kernel(ByteVector.SPECIES_128, IntVector.SPECIES_128,
                a, a.length);
    }

    @Test(dataProvider = "byteUnaryOpProvider")
    static void reinterpretShapeB2I_128_To_256(IntFunction<byte[]> fa) {
        byte[] a = fa.apply(1024);
        reinterpret_kernel(ByteVector.SPECIES_128, IntVector.SPECIES_256,
                a, a.length);
    }

    @Test(dataProvider = "byteUnaryOpProvider")
    static void reinterpretShapeB2I_128_To_512(IntFunction<byte[]> fa) {
        byte[] a = fa.apply(1024);
        reinterpret_kernel(ByteVector.SPECIES_128, IntVector.SPECIES_512,
                a, a.length);
    }

    @Test(dataProvider = "byteUnaryOpProvider")
    static void reinterpretShapeB2I_128_To_MAX(IntFunction<byte[]> fa) {
        byte[] a = fa.apply(1024);
        reinterpret_kernel(ByteVector.SPECIES_128, IntVector.SPECIES_MAX,
                a, a.length);
    }

    @Test(dataProvider = "byteUnaryOpProvider")
    static void reinterpretShapeB2L_128_To_64(IntFunction<byte[]> fa) {
        byte[] a = fa.apply(1024);
        reinterpret_kernel(ByteVector.SPECIES_128, LongVector.SPECIES_64,
                a, a.length);
    }

    @Test(dataProvider = "byteUnaryOpProvider")
    static void reinterpretShapeB2L_128_To_128(IntFunction<byte[]> fa) {
        byte[] a = fa.apply(1024);
        reinterpret_kernel(ByteVector.SPECIES_128, LongVector.SPECIES_128,
                a, a.length);
    }

    @Test(dataProvider = "byteUnaryOpProvider")
    static void reinterpretShapeB2L_128_To_256(IntFunction<byte[]> fa) {
        byte[] a = fa.apply(1024);
        reinterpret_kernel(ByteVector.SPECIES_128, LongVector.SPECIES_256,
                a, a.length);
    }

    @Test(dataProvider = "byteUnaryOpProvider")
    static void reinterpretShapeB2L_128_To_512(IntFunction<byte[]> fa) {
        byte[] a = fa.apply(1024);
        reinterpret_kernel(ByteVector.SPECIES_128, LongVector.SPECIES_512,
                a, a.length);
    }

    @Test(dataProvider = "byteUnaryOpProvider")
    static void reinterpretShapeB2L_128_To_MAX(IntFunction<byte[]> fa) {
        byte[] a = fa.apply(1024);
        reinterpret_kernel(ByteVector.SPECIES_128, LongVector.SPECIES_MAX,
                a, a.length);
    }

    @Test(dataProvider = "byteUnaryOpProvider")
    static void reinterpretShapeB2F_128_To_64(IntFunction<byte[]> fa) {
        byte[] a = fa.apply(1024);
        reinterpret_kernel(ByteVector.SPECIES_128, FloatVector.SPECIES_64,
                a, a.length);
    }

    @Test(dataProvider = "byteUnaryOpProvider")
    static void reinterpretShapeB2F_128_To_128(IntFunction<byte[]> fa) {
        byte[] a = fa.apply(1024);
        reinterpret_kernel(ByteVector.SPECIES_128, FloatVector.SPECIES_128,
                a, a.length);
    }

    @Test(dataProvider = "byteUnaryOpProvider")
    static void reinterpretShapeB2F_128_To_256(IntFunction<byte[]> fa) {
        byte[] a = fa.apply(1024);
        reinterpret_kernel(ByteVector.SPECIES_128, FloatVector.SPECIES_256,
                a, a.length);
    }

    @Test(dataProvider = "byteUnaryOpProvider")
    static void reinterpretShapeB2F_128_To_512(IntFunction<byte[]> fa) {
        byte[] a = fa.apply(1024);
        reinterpret_kernel(ByteVector.SPECIES_128, FloatVector.SPECIES_512,
                a, a.length);
    }

    @Test(dataProvider = "byteUnaryOpProvider")
    static void reinterpretShapeB2F_128_To_MAX(IntFunction<byte[]> fa) {
        byte[] a = fa.apply(1024);
        reinterpret_kernel(ByteVector.SPECIES_128, FloatVector.SPECIES_MAX,
                a, a.length);
    }

    @Test(dataProvider = "byteUnaryOpProvider")
    static void reinterpretShapeB2D_128_To_64(IntFunction<byte[]> fa) {
        byte[] a = fa.apply(1024);
        reinterpret_kernel(ByteVector.SPECIES_128, DoubleVector.SPECIES_64,
                a, a.length);
    }

    @Test(dataProvider = "byteUnaryOpProvider")
    static void reinterpretShapeB2D_128_To_128(IntFunction<byte[]> fa) {
        byte[] a = fa.apply(1024);
        reinterpret_kernel(ByteVector.SPECIES_128, DoubleVector.SPECIES_128,
                a, a.length);
    }

    @Test(dataProvider = "byteUnaryOpProvider")
    static void reinterpretShapeB2D_128_To_256(IntFunction<byte[]> fa) {
        byte[] a = fa.apply(1024);
        reinterpret_kernel(ByteVector.SPECIES_128, DoubleVector.SPECIES_256,
                a, a.length);
    }

    @Test(dataProvider = "byteUnaryOpProvider")
    static void reinterpretShapeB2D_128_To_512(IntFunction<byte[]> fa) {
        byte[] a = fa.apply(1024);
        reinterpret_kernel(ByteVector.SPECIES_128, DoubleVector.SPECIES_512,
                a, a.length);
    }

    @Test(dataProvider = "byteUnaryOpProvider")
    static void reinterpretShapeB2D_128_To_MAX(IntFunction<byte[]> fa) {
        byte[] a = fa.apply(1024);
        reinterpret_kernel(ByteVector.SPECIES_128, DoubleVector.SPECIES_MAX,
                a, a.length);
    }


    @Test(dataProvider = "doubleUnaryOpProvider")
    static void convertD2B_128_To_128(IntFunction<double[]> fa) {
        double[] a = fa.apply(1024);
        conversion_kernel(DoubleVector.SPECIES_128, ByteVector.SPECIES_128,
                a, VectorOperators.D2B,
                ConvAPI.CONVERT, a.length);
    }

    @Test(dataProvider = "doubleUnaryOpProvider")
    static void convertD2S_128_To_128(IntFunction<double[]> fa) {
        double[] a = fa.apply(1024);
        conversion_kernel(DoubleVector.SPECIES_128, ShortVector.SPECIES_128,
                a, VectorOperators.D2S,
                ConvAPI.CONVERT, a.length);
    }

    @Test(dataProvider = "doubleUnaryOpProvider")
    static void convertD2I_128_To_128(IntFunction<double[]> fa) {
        double[] a = fa.apply(1024);
        conversion_kernel(DoubleVector.SPECIES_128, IntVector.SPECIES_128,
                a, VectorOperators.D2I,
                ConvAPI.CONVERT, a.length);
    }

    @Test(dataProvider = "doubleUnaryOpProvider")
    static void convertD2L_128_To_128(IntFunction<double[]> fa) {
        double[] a = fa.apply(1024);
        conversion_kernel(DoubleVector.SPECIES_128, LongVector.SPECIES_128,
                a, VectorOperators.D2L,
                ConvAPI.CONVERT, a.length);
    }

    @Test(dataProvider = "doubleUnaryOpProvider")
    static void convertD2F_128_To_128(IntFunction<double[]> fa) {
        double[] a = fa.apply(1024);
        conversion_kernel(DoubleVector.SPECIES_128, FloatVector.SPECIES_128,
                a, VectorOperators.D2F,
                ConvAPI.CONVERT, a.length);
    }

    @Test(dataProvider = "doubleUnaryOpProvider")
    static void convertD2D_128_To_128(IntFunction<double[]> fa) {
        double[] a = fa.apply(1024);
        conversion_kernel(DoubleVector.SPECIES_128, DoubleVector.SPECIES_128,
                a, D2D,
                ConvAPI.CONVERT, a.length);
    }

    @Test(dataProvider = "doubleUnaryOpProvider")
    static void convertShapeD2B_128_To_64(IntFunction<double[]> fa) {
        double[] a = fa.apply(1024);
        conversion_kernel(DoubleVector.SPECIES_128, ByteVector.SPECIES_64,
                a, VectorOperators.D2B,
                ConvAPI.CONVERTSHAPE, a.length);
    }

    @Test(dataProvider = "doubleUnaryOpProvider")
    static void convertShapeD2B_128_To_128(IntFunction<double[]> fa) {
        double[] a = fa.apply(1024);
        conversion_kernel(DoubleVector.SPECIES_128, ByteVector.SPECIES_128,
                a, VectorOperators.D2B,
                ConvAPI.CONVERTSHAPE, a.length);
    }

    @Test(dataProvider = "doubleUnaryOpProvider")
    static void convertShapeD2B_128_To_256(IntFunction<double[]> fa) {
        double[] a = fa.apply(1024);
        conversion_kernel(DoubleVector.SPECIES_128, ByteVector.SPECIES_256,
                a, VectorOperators.D2B,
                ConvAPI.CONVERTSHAPE, a.length);
    }

    @Test(dataProvider = "doubleUnaryOpProvider")
    static void convertShapeD2B_128_To_512(IntFunction<double[]> fa) {
        double[] a = fa.apply(1024);
        conversion_kernel(DoubleVector.SPECIES_128, ByteVector.SPECIES_512,
                a, VectorOperators.D2B,
                ConvAPI.CONVERTSHAPE, a.length);
    }

    @Test(dataProvider = "doubleUnaryOpProvider")
    static void convertShapeD2B_128_To_MAX(IntFunction<double[]> fa) {
        double[] a = fa.apply(1024);
        conversion_kernel(DoubleVector.SPECIES_128, ByteVector.SPECIES_MAX,
                a, VectorOperators.D2B,
                ConvAPI.CONVERTSHAPE, a.length);
    }

    @Test(dataProvider = "doubleUnaryOpProvider")
    static void convertShapeD2S_128_To_64(IntFunction<double[]> fa) {
        double[] a = fa.apply(1024);
        conversion_kernel(DoubleVector.SPECIES_128, ShortVector.SPECIES_64,
                a, VectorOperators.D2S,
                ConvAPI.CONVERTSHAPE, a.length);
    }

    @Test(dataProvider = "doubleUnaryOpProvider")
    static void convertShapeD2S_128_To_128(IntFunction<double[]> fa) {
        double[] a = fa.apply(1024);
        conversion_kernel(DoubleVector.SPECIES_128, ShortVector.SPECIES_128,
                a, VectorOperators.D2S,
                ConvAPI.CONVERTSHAPE, a.length);
    }

    @Test(dataProvider = "doubleUnaryOpProvider")
    static void convertShapeD2S_128_To_256(IntFunction<double[]> fa) {
        double[] a = fa.apply(1024);
        conversion_kernel(DoubleVector.SPECIES_128, ShortVector.SPECIES_256,
                a, VectorOperators.D2S,
                ConvAPI.CONVERTSHAPE, a.length);
    }

    @Test(dataProvider = "doubleUnaryOpProvider")
    static void convertShapeD2S_128_To_512(IntFunction<double[]> fa) {
        double[] a = fa.apply(1024);
        conversion_kernel(DoubleVector.SPECIES_128, ShortVector.SPECIES_512,
                a, VectorOperators.D2S,
                ConvAPI.CONVERTSHAPE, a.length);
    }

    @Test(dataProvider = "doubleUnaryOpProvider")
    static void convertShapeD2S_128_To_MAX(IntFunction<double[]> fa) {
        double[] a = fa.apply(1024);
        conversion_kernel(DoubleVector.SPECIES_128, ShortVector.SPECIES_MAX,
                a, VectorOperators.D2S,
                ConvAPI.CONVERTSHAPE, a.length);
    }

    @Test(dataProvider = "doubleUnaryOpProvider")
    static void convertShapeD2I_128_To_64(IntFunction<double[]> fa) {
        double[] a = fa.apply(1024);
        conversion_kernel(DoubleVector.SPECIES_128, IntVector.SPECIES_64,
                a, VectorOperators.D2I,
                ConvAPI.CONVERTSHAPE, a.length);
    }

    @Test(dataProvider = "doubleUnaryOpProvider")
    static void convertShapeD2I_128_To_128(IntFunction<double[]> fa) {
        double[] a = fa.apply(1024);
        conversion_kernel(DoubleVector.SPECIES_128, IntVector.SPECIES_128,
                a, VectorOperators.D2I,
                ConvAPI.CONVERTSHAPE, a.length);
    }

    @Test(dataProvider = "doubleUnaryOpProvider")
    static void convertShapeD2I_128_To_256(IntFunction<double[]> fa) {
        double[] a = fa.apply(1024);
        conversion_kernel(DoubleVector.SPECIES_128, IntVector.SPECIES_256,
                a, VectorOperators.D2I,
                ConvAPI.CONVERTSHAPE, a.length);
    }

    @Test(dataProvider = "doubleUnaryOpProvider")
    static void convertShapeD2I_128_To_512(IntFunction<double[]> fa) {
        double[] a = fa.apply(1024);
        conversion_kernel(DoubleVector.SPECIES_128, IntVector.SPECIES_512,
                a, VectorOperators.D2I,
                ConvAPI.CONVERTSHAPE, a.length);
    }

    @Test(dataProvider = "doubleUnaryOpProvider")
    static void convertShapeD2I_128_To_MAX(IntFunction<double[]> fa) {
        double[] a = fa.apply(1024);
        conversion_kernel(DoubleVector.SPECIES_128, IntVector.SPECIES_MAX,
                a, VectorOperators.D2I,
                ConvAPI.CONVERTSHAPE, a.length);
    }

    @Test(dataProvider = "doubleUnaryOpProvider")
    static void convertShapeD2L_128_To_64(IntFunction<double[]> fa) {
        double[] a = fa.apply(1024);
        conversion_kernel(DoubleVector.SPECIES_128, LongVector.SPECIES_64,
                a, VectorOperators.D2L,
                ConvAPI.CONVERTSHAPE, a.length);
    }

    @Test(dataProvider = "doubleUnaryOpProvider")
    static void convertShapeD2L_128_To_128(IntFunction<double[]> fa) {
        double[] a = fa.apply(1024);
        conversion_kernel(DoubleVector.SPECIES_128, LongVector.SPECIES_128,
                a, VectorOperators.D2L,
                ConvAPI.CONVERTSHAPE, a.length);
    }

    @Test(dataProvider = "doubleUnaryOpProvider")
    static void convertShapeD2L_128_To_256(IntFunction<double[]> fa) {
        double[] a = fa.apply(1024);
        conversion_kernel(DoubleVector.SPECIES_128, LongVector.SPECIES_256,
                a, VectorOperators.D2L,
                ConvAPI.CONVERTSHAPE, a.length);
    }

    @Test(dataProvider = "doubleUnaryOpProvider")
    static void convertShapeD2L_128_To_512(IntFunction<double[]> fa) {
        double[] a = fa.apply(1024);
        conversion_kernel(DoubleVector.SPECIES_128, LongVector.SPECIES_512,
                a, VectorOperators.D2L,
                ConvAPI.CONVERTSHAPE, a.length);
    }

    @Test(dataProvider = "doubleUnaryOpProvider")
    static void convertShapeD2L_128_To_MAX(IntFunction<double[]> fa) {
        double[] a = fa.apply(1024);
        conversion_kernel(DoubleVector.SPECIES_128, LongVector.SPECIES_MAX,
                a, VectorOperators.D2L,
                ConvAPI.CONVERTSHAPE, a.length);
    }

    @Test(dataProvider = "doubleUnaryOpProvider")
    static void convertShapeD2F_128_To_64(IntFunction<double[]> fa) {
        double[] a = fa.apply(1024);
        conversion_kernel(DoubleVector.SPECIES_128, FloatVector.SPECIES_64,
                a, VectorOperators.D2F,
                ConvAPI.CONVERTSHAPE, a.length);
    }

    @Test(dataProvider = "doubleUnaryOpProvider")
    static void convertShapeD2F_128_To_128(IntFunction<double[]> fa) {
        double[] a = fa.apply(1024);
        conversion_kernel(DoubleVector.SPECIES_128, FloatVector.SPECIES_128,
                a, VectorOperators.D2F,
                ConvAPI.CONVERTSHAPE, a.length);
    }

    @Test(dataProvider = "doubleUnaryOpProvider")
    static void convertShapeD2F_128_To_256(IntFunction<double[]> fa) {
        double[] a = fa.apply(1024);
        conversion_kernel(DoubleVector.SPECIES_128, FloatVector.SPECIES_256,
                a, VectorOperators.D2F,
                ConvAPI.CONVERTSHAPE, a.length);
    }

    @Test(dataProvider = "doubleUnaryOpProvider")
    static void convertShapeD2F_128_To_512(IntFunction<double[]> fa) {
        double[] a = fa.apply(1024);
        conversion_kernel(DoubleVector.SPECIES_128, FloatVector.SPECIES_512,
                a, VectorOperators.D2F,
                ConvAPI.CONVERTSHAPE, a.length);
    }

    @Test(dataProvider = "doubleUnaryOpProvider")
    static void convertShapeD2F_128_To_MAX(IntFunction<double[]> fa) {
        double[] a = fa.apply(1024);
        conversion_kernel(DoubleVector.SPECIES_128, FloatVector.SPECIES_MAX,
                a, VectorOperators.D2F,
                ConvAPI.CONVERTSHAPE, a.length);
    }

    @Test(dataProvider = "doubleUnaryOpProvider")
    static void convertShapeD2D_128_To_64(IntFunction<double[]> fa) {
        double[] a = fa.apply(1024);
        conversion_kernel(DoubleVector.SPECIES_128, DoubleVector.SPECIES_64,
                a, D2D,
                ConvAPI.CONVERTSHAPE, a.length);
    }

    @Test(dataProvider = "doubleUnaryOpProvider")
    static void convertShapeD2D_128_To_128(IntFunction<double[]> fa) {
        double[] a = fa.apply(1024);
        conversion_kernel(DoubleVector.SPECIES_128, DoubleVector.SPECIES_128,
                a, D2D,
                ConvAPI.CONVERTSHAPE, a.length);
    }

    @Test(dataProvider = "doubleUnaryOpProvider")
    static void convertShapeD2D_128_To_256(IntFunction<double[]> fa) {
        double[] a = fa.apply(1024);
        conversion_kernel(DoubleVector.SPECIES_128, DoubleVector.SPECIES_256,
                a, D2D,
                ConvAPI.CONVERTSHAPE, a.length);
    }

    @Test(dataProvider = "doubleUnaryOpProvider")
    static void convertShapeD2D_128_To_512(IntFunction<double[]> fa) {
        double[] a = fa.apply(1024);
        conversion_kernel(DoubleVector.SPECIES_128, DoubleVector.SPECIES_512,
                a, D2D,
                ConvAPI.CONVERTSHAPE, a.length);
    }

    @Test(dataProvider = "doubleUnaryOpProvider")
    static void convertShapeD2D_128_To_MAX(IntFunction<double[]> fa) {
        double[] a = fa.apply(1024);
        conversion_kernel(DoubleVector.SPECIES_128, DoubleVector.SPECIES_MAX,
                a, D2D,
                ConvAPI.CONVERTSHAPE, a.length);
    }

    @Test(dataProvider = "doubleUnaryOpProvider")
    static void castShapeD2B_128_To_64(IntFunction<double[]> fa) {
        double[] a = fa.apply(1024);
        conversion_kernel(DoubleVector.SPECIES_128, ByteVector.SPECIES_64,
                a, VectorOperators.D2B,
                ConvAPI.CASTSHAPE, a.length);
    }

    @Test(dataProvider = "doubleUnaryOpProvider")
    static void castShapeD2B_128_To_128(IntFunction<double[]> fa) {
        double[] a = fa.apply(1024);
        conversion_kernel(DoubleVector.SPECIES_128, ByteVector.SPECIES_128,
                a, VectorOperators.D2B,
                ConvAPI.CASTSHAPE, a.length);
    }

    @Test(dataProvider = "doubleUnaryOpProvider")
    static void castShapeD2B_128_To_256(IntFunction<double[]> fa) {
        double[] a = fa.apply(1024);
        conversion_kernel(DoubleVector.SPECIES_128, ByteVector.SPECIES_256,
                a, VectorOperators.D2B,
                ConvAPI.CASTSHAPE, a.length);
    }

    @Test(dataProvider = "doubleUnaryOpProvider")
    static void castShapeD2B_128_To_512(IntFunction<double[]> fa) {
        double[] a = fa.apply(1024);
        conversion_kernel(DoubleVector.SPECIES_128, ByteVector.SPECIES_512,
                a, VectorOperators.D2B,
                ConvAPI.CASTSHAPE, a.length);
    }

    @Test(dataProvider = "doubleUnaryOpProvider")
    static void castShapeD2B_128_To_MAX(IntFunction<double[]> fa) {
        double[] a = fa.apply(1024);
        conversion_kernel(DoubleVector.SPECIES_128, ByteVector.SPECIES_MAX,
                a, VectorOperators.D2B,
                ConvAPI.CASTSHAPE, a.length);
    }

    @Test(dataProvider = "doubleUnaryOpProvider")
    static void castShapeD2S_128_To_64(IntFunction<double[]> fa) {
        double[] a = fa.apply(1024);
        conversion_kernel(DoubleVector.SPECIES_128, ShortVector.SPECIES_64,
                a, VectorOperators.D2S,
                ConvAPI.CASTSHAPE, a.length);
    }

    @Test(dataProvider = "doubleUnaryOpProvider")
    static void castShapeD2S_128_To_128(IntFunction<double[]> fa) {
        double[] a = fa.apply(1024);
        conversion_kernel(DoubleVector.SPECIES_128, ShortVector.SPECIES_128,
                a, VectorOperators.D2S,
                ConvAPI.CASTSHAPE, a.length);
    }

    @Test(dataProvider = "doubleUnaryOpProvider")
    static void castShapeD2S_128_To_256(IntFunction<double[]> fa) {
        double[] a = fa.apply(1024);
        conversion_kernel(DoubleVector.SPECIES_128, ShortVector.SPECIES_256,
                a, VectorOperators.D2S,
                ConvAPI.CASTSHAPE, a.length);
    }

    @Test(dataProvider = "doubleUnaryOpProvider")
    static void castShapeD2S_128_To_512(IntFunction<double[]> fa) {
        double[] a = fa.apply(1024);
        conversion_kernel(DoubleVector.SPECIES_128, ShortVector.SPECIES_512,
                a, VectorOperators.D2S,
                ConvAPI.CASTSHAPE, a.length);
    }

    @Test(dataProvider = "doubleUnaryOpProvider")
    static void castShapeD2S_128_To_MAX(IntFunction<double[]> fa) {
        double[] a = fa.apply(1024);
        conversion_kernel(DoubleVector.SPECIES_128, ShortVector.SPECIES_MAX,
                a, VectorOperators.D2S,
                ConvAPI.CASTSHAPE, a.length);
    }

    @Test(dataProvider = "doubleUnaryOpProvider")
    static void castShapeD2I_128_To_64(IntFunction<double[]> fa) {
        double[] a = fa.apply(1024);
        conversion_kernel(DoubleVector.SPECIES_128, IntVector.SPECIES_64,
                a, VectorOperators.D2I,
                ConvAPI.CASTSHAPE, a.length);
    }

    @Test(dataProvider = "doubleUnaryOpProvider")
    static void castShapeD2I_128_To_128(IntFunction<double[]> fa) {
        double[] a = fa.apply(1024);
        conversion_kernel(DoubleVector.SPECIES_128, IntVector.SPECIES_128,
                a, VectorOperators.D2I,
                ConvAPI.CASTSHAPE, a.length);
    }

    @Test(dataProvider = "doubleUnaryOpProvider")
    static void castShapeD2I_128_To_256(IntFunction<double[]> fa) {
        double[] a = fa.apply(1024);
        conversion_kernel(DoubleVector.SPECIES_128, IntVector.SPECIES_256,
                a, VectorOperators.D2I,
                ConvAPI.CASTSHAPE, a.length);
    }

    @Test(dataProvider = "doubleUnaryOpProvider")
    static void castShapeD2I_128_To_512(IntFunction<double[]> fa) {
        double[] a = fa.apply(1024);
        conversion_kernel(DoubleVector.SPECIES_128, IntVector.SPECIES_512,
                a, VectorOperators.D2I,
                ConvAPI.CASTSHAPE, a.length);
    }

    @Test(dataProvider = "doubleUnaryOpProvider")
    static void castShapeD2I_128_To_MAX(IntFunction<double[]> fa) {
        double[] a = fa.apply(1024);
        conversion_kernel(DoubleVector.SPECIES_128, IntVector.SPECIES_MAX,
                a, VectorOperators.D2I,
                ConvAPI.CASTSHAPE, a.length);
    }

    @Test(dataProvider = "doubleUnaryOpProvider")
    static void castShapeD2L_128_To_64(IntFunction<double[]> fa) {
        double[] a = fa.apply(1024);
        conversion_kernel(DoubleVector.SPECIES_128, LongVector.SPECIES_64,
                a, VectorOperators.D2L,
                ConvAPI.CASTSHAPE, a.length);
    }

    @Test(dataProvider = "doubleUnaryOpProvider")
    static void castShapeD2L_128_To_128(IntFunction<double[]> fa) {
        double[] a = fa.apply(1024);
        conversion_kernel(DoubleVector.SPECIES_128, LongVector.SPECIES_128,
                a, VectorOperators.D2L,
                ConvAPI.CASTSHAPE, a.length);
    }

    @Test(dataProvider = "doubleUnaryOpProvider")
    static void castShapeD2L_128_To_256(IntFunction<double[]> fa) {
        double[] a = fa.apply(1024);
        conversion_kernel(DoubleVector.SPECIES_128, LongVector.SPECIES_256,
                a, VectorOperators.D2L,
                ConvAPI.CASTSHAPE, a.length);
    }

    @Test(dataProvider = "doubleUnaryOpProvider")
    static void castShapeD2L_128_To_512(IntFunction<double[]> fa) {
        double[] a = fa.apply(1024);
        conversion_kernel(DoubleVector.SPECIES_128, LongVector.SPECIES_512,
                a, VectorOperators.D2L,
                ConvAPI.CASTSHAPE, a.length);
    }

    @Test(dataProvider = "doubleUnaryOpProvider")
    static void castShapeD2L_128_To_MAX(IntFunction<double[]> fa) {
        double[] a = fa.apply(1024);
        conversion_kernel(DoubleVector.SPECIES_128, LongVector.SPECIES_MAX,
                a, VectorOperators.D2L,
                ConvAPI.CASTSHAPE, a.length);
    }

    @Test(dataProvider = "doubleUnaryOpProvider")
    static void castShapeD2F_128_To_64(IntFunction<double[]> fa) {
        double[] a = fa.apply(1024);
        conversion_kernel(DoubleVector.SPECIES_128, FloatVector.SPECIES_64,
                a, VectorOperators.D2F,
                ConvAPI.CASTSHAPE, a.length);
    }

    @Test(dataProvider = "doubleUnaryOpProvider")
    static void castShapeD2F_128_To_128(IntFunction<double[]> fa) {
        double[] a = fa.apply(1024);
        conversion_kernel(DoubleVector.SPECIES_128, FloatVector.SPECIES_128,
                a, VectorOperators.D2F,
                ConvAPI.CASTSHAPE, a.length);
    }

    @Test(dataProvider = "doubleUnaryOpProvider")
    static void castShapeD2F_128_To_256(IntFunction<double[]> fa) {
        double[] a = fa.apply(1024);
        conversion_kernel(DoubleVector.SPECIES_128, FloatVector.SPECIES_256,
                a, VectorOperators.D2F,
                ConvAPI.CASTSHAPE, a.length);
    }

    @Test(dataProvider = "doubleUnaryOpProvider")
    static void castShapeD2F_128_To_512(IntFunction<double[]> fa) {
        double[] a = fa.apply(1024);
        conversion_kernel(DoubleVector.SPECIES_128, FloatVector.SPECIES_512,
                a, VectorOperators.D2F,
                ConvAPI.CASTSHAPE, a.length);
    }

    @Test(dataProvider = "doubleUnaryOpProvider")
    static void castShapeD2F_128_To_MAX(IntFunction<double[]> fa) {
        double[] a = fa.apply(1024);
        conversion_kernel(DoubleVector.SPECIES_128, FloatVector.SPECIES_MAX,
                a, VectorOperators.D2F,
                ConvAPI.CASTSHAPE, a.length);
    }

    @Test(dataProvider = "doubleUnaryOpProvider")
    static void castShapeD2D_128_To_64(IntFunction<double[]> fa) {
        double[] a = fa.apply(1024);
        conversion_kernel(DoubleVector.SPECIES_128, DoubleVector.SPECIES_64,
                a, D2D,
                ConvAPI.CASTSHAPE, a.length);
    }

    @Test(dataProvider = "doubleUnaryOpProvider")
    static void castShapeD2D_128_To_128(IntFunction<double[]> fa) {
        double[] a = fa.apply(1024);
        conversion_kernel(DoubleVector.SPECIES_128, DoubleVector.SPECIES_128,
                a, D2D,
                ConvAPI.CASTSHAPE, a.length);
    }

    @Test(dataProvider = "doubleUnaryOpProvider")
    static void castShapeD2D_128_To_256(IntFunction<double[]> fa) {
        double[] a = fa.apply(1024);
        conversion_kernel(DoubleVector.SPECIES_128, DoubleVector.SPECIES_256,
                a, D2D,
                ConvAPI.CASTSHAPE, a.length);
    }

    @Test(dataProvider = "doubleUnaryOpProvider")
    static void castShapeD2D_128_To_512(IntFunction<double[]> fa) {
        double[] a = fa.apply(1024);
        conversion_kernel(DoubleVector.SPECIES_128, DoubleVector.SPECIES_512,
                a, D2D,
                ConvAPI.CASTSHAPE, a.length);
    }

    @Test(dataProvider = "doubleUnaryOpProvider")
    static void castShapeD2D_128_To_MAX(IntFunction<double[]> fa) {
        double[] a = fa.apply(1024);
        conversion_kernel(DoubleVector.SPECIES_128, DoubleVector.SPECIES_MAX,
                a, D2D,
                ConvAPI.CASTSHAPE, a.length);
    }

    @Test(dataProvider = "doubleUnaryOpProvider")
    static void reinterpretShapeD2B_128_To_64(IntFunction<double[]> fa) {
        double[] a = fa.apply(1024);
        reinterpret_kernel(DoubleVector.SPECIES_128, ByteVector.SPECIES_64,
                a, a.length);
    }

    @Test(dataProvider = "doubleUnaryOpProvider")
    static void reinterpretShapeD2B_128_To_128(IntFunction<double[]> fa) {
        double[] a = fa.apply(1024);
        reinterpret_kernel(DoubleVector.SPECIES_128, ByteVector.SPECIES_128,
                a, a.length);
    }

    @Test(dataProvider = "doubleUnaryOpProvider")
    static void reinterpretShapeD2B_128_To_256(IntFunction<double[]> fa) {
        double[] a = fa.apply(1024);
        reinterpret_kernel(DoubleVector.SPECIES_128, ByteVector.SPECIES_256,
                a, a.length);
    }

    @Test(dataProvider = "doubleUnaryOpProvider")
    static void reinterpretShapeD2B_128_To_512(IntFunction<double[]> fa) {
        double[] a = fa.apply(1024);
        reinterpret_kernel(DoubleVector.SPECIES_128, ByteVector.SPECIES_512,
                a, a.length);
    }

    @Test(dataProvider = "doubleUnaryOpProvider")
    static void reinterpretShapeD2B_128_To_MAX(IntFunction<double[]> fa) {
        double[] a = fa.apply(1024);
        reinterpret_kernel(DoubleVector.SPECIES_128, ByteVector.SPECIES_MAX,
                a, a.length);
    }

    @Test(dataProvider = "doubleUnaryOpProvider")
    static void reinterpretShapeD2S_128_To_64(IntFunction<double[]> fa) {
        double[] a = fa.apply(1024);
        reinterpret_kernel(DoubleVector.SPECIES_128, ShortVector.SPECIES_64,
                a, a.length);
    }

    @Test(dataProvider = "doubleUnaryOpProvider")
    static void reinterpretShapeD2S_128_To_128(IntFunction<double[]> fa) {
        double[] a = fa.apply(1024);
        reinterpret_kernel(DoubleVector.SPECIES_128, ShortVector.SPECIES_128,
                a, a.length);
    }

    @Test(dataProvider = "doubleUnaryOpProvider")
    static void reinterpretShapeD2S_128_To_256(IntFunction<double[]> fa) {
        double[] a = fa.apply(1024);
        reinterpret_kernel(DoubleVector.SPECIES_128, ShortVector.SPECIES_256,
                a, a.length);
    }

    @Test(dataProvider = "doubleUnaryOpProvider")
    static void reinterpretShapeD2S_128_To_512(IntFunction<double[]> fa) {
        double[] a = fa.apply(1024);
        reinterpret_kernel(DoubleVector.SPECIES_128, ShortVector.SPECIES_512,
                a, a.length);
    }

    @Test(dataProvider = "doubleUnaryOpProvider")
    static void reinterpretShapeD2S_128_To_MAX(IntFunction<double[]> fa) {
        double[] a = fa.apply(1024);
        reinterpret_kernel(DoubleVector.SPECIES_128, ShortVector.SPECIES_MAX,
                a, a.length);
    }

    @Test(dataProvider = "doubleUnaryOpProvider")
    static void reinterpretShapeD2I_128_To_64(IntFunction<double[]> fa) {
        double[] a = fa.apply(1024);
        reinterpret_kernel(DoubleVector.SPECIES_128, IntVector.SPECIES_64,
                a, a.length);
    }

    @Test(dataProvider = "doubleUnaryOpProvider")
    static void reinterpretShapeD2I_128_To_128(IntFunction<double[]> fa) {
        double[] a = fa.apply(1024);
        reinterpret_kernel(DoubleVector.SPECIES_128, IntVector.SPECIES_128,
                a, a.length);
    }

    @Test(dataProvider = "doubleUnaryOpProvider")
    static void reinterpretShapeD2I_128_To_256(IntFunction<double[]> fa) {
        double[] a = fa.apply(1024);
        reinterpret_kernel(DoubleVector.SPECIES_128, IntVector.SPECIES_256,
                a, a.length);
    }

    @Test(dataProvider = "doubleUnaryOpProvider")
    static void reinterpretShapeD2I_128_To_512(IntFunction<double[]> fa) {
        double[] a = fa.apply(1024);
        reinterpret_kernel(DoubleVector.SPECIES_128, IntVector.SPECIES_512,
                a, a.length);
    }

    @Test(dataProvider = "doubleUnaryOpProvider")
    static void reinterpretShapeD2I_128_To_MAX(IntFunction<double[]> fa) {
        double[] a = fa.apply(1024);
        reinterpret_kernel(DoubleVector.SPECIES_128, IntVector.SPECIES_MAX,
                a, a.length);
    }

    @Test(dataProvider = "doubleUnaryOpProvider")
    static void reinterpretShapeD2L_128_To_64(IntFunction<double[]> fa) {
        double[] a = fa.apply(1024);
        reinterpret_kernel(DoubleVector.SPECIES_128, LongVector.SPECIES_64,
                a, a.length);
    }

    @Test(dataProvider = "doubleUnaryOpProvider")
    static void reinterpretShapeD2L_128_To_128(IntFunction<double[]> fa) {
        double[] a = fa.apply(1024);
        reinterpret_kernel(DoubleVector.SPECIES_128, LongVector.SPECIES_128,
                a, a.length);
    }

    @Test(dataProvider = "doubleUnaryOpProvider")
    static void reinterpretShapeD2L_128_To_256(IntFunction<double[]> fa) {
        double[] a = fa.apply(1024);
        reinterpret_kernel(DoubleVector.SPECIES_128, LongVector.SPECIES_256,
                a, a.length);
    }

    @Test(dataProvider = "doubleUnaryOpProvider")
    static void reinterpretShapeD2L_128_To_512(IntFunction<double[]> fa) {
        double[] a = fa.apply(1024);
        reinterpret_kernel(DoubleVector.SPECIES_128, LongVector.SPECIES_512,
                a, a.length);
    }

    @Test(dataProvider = "doubleUnaryOpProvider")
    static void reinterpretShapeD2L_128_To_MAX(IntFunction<double[]> fa) {
        double[] a = fa.apply(1024);
        reinterpret_kernel(DoubleVector.SPECIES_128, LongVector.SPECIES_MAX,
                a, a.length);
    }

    @Test(dataProvider = "doubleUnaryOpProvider")
    static void reinterpretShapeD2F_128_To_64(IntFunction<double[]> fa) {
        double[] a = fa.apply(1024);
        reinterpret_kernel(DoubleVector.SPECIES_128, FloatVector.SPECIES_64,
                a, a.length);
    }

    @Test(dataProvider = "doubleUnaryOpProvider")
    static void reinterpretShapeD2F_128_To_128(IntFunction<double[]> fa) {
        double[] a = fa.apply(1024);
        reinterpret_kernel(DoubleVector.SPECIES_128, FloatVector.SPECIES_128,
                a, a.length);
    }

    @Test(dataProvider = "doubleUnaryOpProvider")
    static void reinterpretShapeD2F_128_To_256(IntFunction<double[]> fa) {
        double[] a = fa.apply(1024);
        reinterpret_kernel(DoubleVector.SPECIES_128, FloatVector.SPECIES_256,
                a, a.length);
    }

    @Test(dataProvider = "doubleUnaryOpProvider")
    static void reinterpretShapeD2F_128_To_512(IntFunction<double[]> fa) {
        double[] a = fa.apply(1024);
        reinterpret_kernel(DoubleVector.SPECIES_128, FloatVector.SPECIES_512,
                a, a.length);
    }

    @Test(dataProvider = "doubleUnaryOpProvider")
    static void reinterpretShapeD2F_128_To_MAX(IntFunction<double[]> fa) {
        double[] a = fa.apply(1024);
        reinterpret_kernel(DoubleVector.SPECIES_128, FloatVector.SPECIES_MAX,
                a, a.length);
    }

    @Test(dataProvider = "doubleUnaryOpProvider")
    static void reinterpretShapeD2D_128_To_64(IntFunction<double[]> fa) {
        double[] a = fa.apply(1024);
        reinterpret_kernel(DoubleVector.SPECIES_128, DoubleVector.SPECIES_64,
                a, a.length);
    }

    @Test(dataProvider = "doubleUnaryOpProvider")
    static void reinterpretShapeD2D_128_To_128(IntFunction<double[]> fa) {
        double[] a = fa.apply(1024);
        reinterpret_kernel(DoubleVector.SPECIES_128, DoubleVector.SPECIES_128,
                a, a.length);
    }

    @Test(dataProvider = "doubleUnaryOpProvider")
    static void reinterpretShapeD2D_128_To_256(IntFunction<double[]> fa) {
        double[] a = fa.apply(1024);
        reinterpret_kernel(DoubleVector.SPECIES_128, DoubleVector.SPECIES_256,
                a, a.length);
    }

    @Test(dataProvider = "doubleUnaryOpProvider")
    static void reinterpretShapeD2D_128_To_512(IntFunction<double[]> fa) {
        double[] a = fa.apply(1024);
        reinterpret_kernel(DoubleVector.SPECIES_128, DoubleVector.SPECIES_512,
                a, a.length);
    }

    @Test(dataProvider = "doubleUnaryOpProvider")
    static void reinterpretShapeD2D_128_To_MAX(IntFunction<double[]> fa) {
        double[] a = fa.apply(1024);
        reinterpret_kernel(DoubleVector.SPECIES_128, DoubleVector.SPECIES_MAX,
                a, a.length);
    }


    @Test(dataProvider = "floatUnaryOpProvider")
    static void convertF2B_128_To_128(IntFunction<float[]> fa) {
        float[] a = fa.apply(1024);
        conversion_kernel(FloatVector.SPECIES_128, ByteVector.SPECIES_128,
                a, VectorOperators.F2B,
                ConvAPI.CONVERT, a.length);
    }

    @Test(dataProvider = "floatUnaryOpProvider")
    static void convertF2S_128_To_128(IntFunction<float[]> fa) {
        float[] a = fa.apply(1024);
        conversion_kernel(FloatVector.SPECIES_128, ShortVector.SPECIES_128,
                a, VectorOperators.F2S,
                ConvAPI.CONVERT, a.length);
    }

    @Test(dataProvider = "floatUnaryOpProvider")
    static void convertF2I_128_To_128(IntFunction<float[]> fa) {
        float[] a = fa.apply(1024);
        conversion_kernel(FloatVector.SPECIES_128, IntVector.SPECIES_128,
                a, VectorOperators.F2I,
                ConvAPI.CONVERT, a.length);
    }

    @Test(dataProvider = "floatUnaryOpProvider")
    static void convertF2L_128_To_128(IntFunction<float[]> fa) {
        float[] a = fa.apply(1024);
        conversion_kernel(FloatVector.SPECIES_128, LongVector.SPECIES_128,
                a, VectorOperators.F2L,
                ConvAPI.CONVERT, a.length);
    }

    @Test(dataProvider = "floatUnaryOpProvider")
    static void convertF2F_128_To_128(IntFunction<float[]> fa) {
        float[] a = fa.apply(1024);
        conversion_kernel(FloatVector.SPECIES_128, FloatVector.SPECIES_128,
                a, F2F,
                ConvAPI.CONVERT, a.length);
    }

    @Test(dataProvider = "floatUnaryOpProvider")
    static void convertF2D_128_To_128(IntFunction<float[]> fa) {
        float[] a = fa.apply(1024);
        conversion_kernel(FloatVector.SPECIES_128, DoubleVector.SPECIES_128,
                a, VectorOperators.F2D,
                ConvAPI.CONVERT, a.length);
    }

    @Test(dataProvider = "floatUnaryOpProvider")
    static void convertShapeF2B_128_To_64(IntFunction<float[]> fa) {
        float[] a = fa.apply(1024);
        conversion_kernel(FloatVector.SPECIES_128, ByteVector.SPECIES_64,
                a, VectorOperators.F2B,
                ConvAPI.CONVERTSHAPE, a.length);
    }

    @Test(dataProvider = "floatUnaryOpProvider")
    static void convertShapeF2B_128_To_128(IntFunction<float[]> fa) {
        float[] a = fa.apply(1024);
        conversion_kernel(FloatVector.SPECIES_128, ByteVector.SPECIES_128,
                a, VectorOperators.F2B,
                ConvAPI.CONVERTSHAPE, a.length);
    }

    @Test(dataProvider = "floatUnaryOpProvider")
    static void convertShapeF2B_128_To_256(IntFunction<float[]> fa) {
        float[] a = fa.apply(1024);
        conversion_kernel(FloatVector.SPECIES_128, ByteVector.SPECIES_256,
                a, VectorOperators.F2B,
                ConvAPI.CONVERTSHAPE, a.length);
    }

    @Test(dataProvider = "floatUnaryOpProvider")
    static void convertShapeF2B_128_To_512(IntFunction<float[]> fa) {
        float[] a = fa.apply(1024);
        conversion_kernel(FloatVector.SPECIES_128, ByteVector.SPECIES_512,
                a, VectorOperators.F2B,
                ConvAPI.CONVERTSHAPE, a.length);
    }

    @Test(dataProvider = "floatUnaryOpProvider")
    static void convertShapeF2B_128_To_MAX(IntFunction<float[]> fa) {
        float[] a = fa.apply(1024);
        conversion_kernel(FloatVector.SPECIES_128, ByteVector.SPECIES_MAX,
                a, VectorOperators.F2B,
                ConvAPI.CONVERTSHAPE, a.length);
    }

    @Test(dataProvider = "floatUnaryOpProvider")
    static void convertShapeF2S_128_To_64(IntFunction<float[]> fa) {
        float[] a = fa.apply(1024);
        conversion_kernel(FloatVector.SPECIES_128, ShortVector.SPECIES_64,
                a, VectorOperators.F2S,
                ConvAPI.CONVERTSHAPE, a.length);
    }

    @Test(dataProvider = "floatUnaryOpProvider")
    static void convertShapeF2S_128_To_128(IntFunction<float[]> fa) {
        float[] a = fa.apply(1024);
        conversion_kernel(FloatVector.SPECIES_128, ShortVector.SPECIES_128,
                a, VectorOperators.F2S,
                ConvAPI.CONVERTSHAPE, a.length);
    }

    @Test(dataProvider = "floatUnaryOpProvider")
    static void convertShapeF2S_128_To_256(IntFunction<float[]> fa) {
        float[] a = fa.apply(1024);
        conversion_kernel(FloatVector.SPECIES_128, ShortVector.SPECIES_256,
                a, VectorOperators.F2S,
                ConvAPI.CONVERTSHAPE, a.length);
    }

    @Test(dataProvider = "floatUnaryOpProvider")
    static void convertShapeF2S_128_To_512(IntFunction<float[]> fa) {
        float[] a = fa.apply(1024);
        conversion_kernel(FloatVector.SPECIES_128, ShortVector.SPECIES_512,
                a, VectorOperators.F2S,
                ConvAPI.CONVERTSHAPE, a.length);
    }

    @Test(dataProvider = "floatUnaryOpProvider")
    static void convertShapeF2S_128_To_MAX(IntFunction<float[]> fa) {
        float[] a = fa.apply(1024);
        conversion_kernel(FloatVector.SPECIES_128, ShortVector.SPECIES_MAX,
                a, VectorOperators.F2S,
                ConvAPI.CONVERTSHAPE, a.length);
    }

    @Test(dataProvider = "floatUnaryOpProvider")
    static void convertShapeF2I_128_To_64(IntFunction<float[]> fa) {
        float[] a = fa.apply(1024);
        conversion_kernel(FloatVector.SPECIES_128, IntVector.SPECIES_64,
                a, VectorOperators.F2I,
                ConvAPI.CONVERTSHAPE, a.length);
    }

    @Test(dataProvider = "floatUnaryOpProvider")
    static void convertShapeF2I_128_To_128(IntFunction<float[]> fa) {
        float[] a = fa.apply(1024);
        conversion_kernel(FloatVector.SPECIES_128, IntVector.SPECIES_128,
                a, VectorOperators.F2I,
                ConvAPI.CONVERTSHAPE, a.length);
    }

    @Test(dataProvider = "floatUnaryOpProvider")
    static void convertShapeF2I_128_To_256(IntFunction<float[]> fa) {
        float[] a = fa.apply(1024);
        conversion_kernel(FloatVector.SPECIES_128, IntVector.SPECIES_256,
                a, VectorOperators.F2I,
                ConvAPI.CONVERTSHAPE, a.length);
    }

    @Test(dataProvider = "floatUnaryOpProvider")
    static void convertShapeF2I_128_To_512(IntFunction<float[]> fa) {
        float[] a = fa.apply(1024);
        conversion_kernel(FloatVector.SPECIES_128, IntVector.SPECIES_512,
                a, VectorOperators.F2I,
                ConvAPI.CONVERTSHAPE, a.length);
    }

    @Test(dataProvider = "floatUnaryOpProvider")
    static void convertShapeF2I_128_To_MAX(IntFunction<float[]> fa) {
        float[] a = fa.apply(1024);
        conversion_kernel(FloatVector.SPECIES_128, IntVector.SPECIES_MAX,
                a, VectorOperators.F2I,
                ConvAPI.CONVERTSHAPE, a.length);
    }

    @Test(dataProvider = "floatUnaryOpProvider")
    static void convertShapeF2L_128_To_64(IntFunction<float[]> fa) {
        float[] a = fa.apply(1024);
        conversion_kernel(FloatVector.SPECIES_128, LongVector.SPECIES_64,
                a, VectorOperators.F2L,
                ConvAPI.CONVERTSHAPE, a.length);
    }

    @Test(dataProvider = "floatUnaryOpProvider")
    static void convertShapeF2L_128_To_128(IntFunction<float[]> fa) {
        float[] a = fa.apply(1024);
        conversion_kernel(FloatVector.SPECIES_128, LongVector.SPECIES_128,
                a, VectorOperators.F2L,
                ConvAPI.CONVERTSHAPE, a.length);
    }

    @Test(dataProvider = "floatUnaryOpProvider")
    static void convertShapeF2L_128_To_256(IntFunction<float[]> fa) {
        float[] a = fa.apply(1024);
        conversion_kernel(FloatVector.SPECIES_128, LongVector.SPECIES_256,
                a, VectorOperators.F2L,
                ConvAPI.CONVERTSHAPE, a.length);
    }

    @Test(dataProvider = "floatUnaryOpProvider")
    static void convertShapeF2L_128_To_512(IntFunction<float[]> fa) {
        float[] a = fa.apply(1024);
        conversion_kernel(FloatVector.SPECIES_128, LongVector.SPECIES_512,
                a, VectorOperators.F2L,
                ConvAPI.CONVERTSHAPE, a.length);
    }

    @Test(dataProvider = "floatUnaryOpProvider")
    static void convertShapeF2L_128_To_MAX(IntFunction<float[]> fa) {
        float[] a = fa.apply(1024);
        conversion_kernel(FloatVector.SPECIES_128, LongVector.SPECIES_MAX,
                a, VectorOperators.F2L,
                ConvAPI.CONVERTSHAPE, a.length);
    }

    @Test(dataProvider = "floatUnaryOpProvider")
    static void convertShapeF2F_128_To_64(IntFunction<float[]> fa) {
        float[] a = fa.apply(1024);
        conversion_kernel(FloatVector.SPECIES_128, FloatVector.SPECIES_64,
                a, F2F,
                ConvAPI.CONVERTSHAPE, a.length);
    }

    @Test(dataProvider = "floatUnaryOpProvider")
    static void convertShapeF2F_128_To_128(IntFunction<float[]> fa) {
        float[] a = fa.apply(1024);
        conversion_kernel(FloatVector.SPECIES_128, FloatVector.SPECIES_128,
                a, F2F,
                ConvAPI.CONVERTSHAPE, a.length);
    }

    @Test(dataProvider = "floatUnaryOpProvider")
    static void convertShapeF2F_128_To_256(IntFunction<float[]> fa) {
        float[] a = fa.apply(1024);
        conversion_kernel(FloatVector.SPECIES_128, FloatVector.SPECIES_256,
                a, F2F,
                ConvAPI.CONVERTSHAPE, a.length);
    }

    @Test(dataProvider = "floatUnaryOpProvider")
    static void convertShapeF2F_128_To_512(IntFunction<float[]> fa) {
        float[] a = fa.apply(1024);
        conversion_kernel(FloatVector.SPECIES_128, FloatVector.SPECIES_512,
                a, F2F,
                ConvAPI.CONVERTSHAPE, a.length);
    }

    @Test(dataProvider = "floatUnaryOpProvider")
    static void convertShapeF2F_128_To_MAX(IntFunction<float[]> fa) {
        float[] a = fa.apply(1024);
        conversion_kernel(FloatVector.SPECIES_128, FloatVector.SPECIES_MAX,
                a, F2F,
                ConvAPI.CONVERTSHAPE, a.length);
    }

    @Test(dataProvider = "floatUnaryOpProvider")
    static void convertShapeF2D_128_To_64(IntFunction<float[]> fa) {
        float[] a = fa.apply(1024);
        conversion_kernel(FloatVector.SPECIES_128, DoubleVector.SPECIES_64,
                a, VectorOperators.F2D,
                ConvAPI.CONVERTSHAPE, a.length);
    }

    @Test(dataProvider = "floatUnaryOpProvider")
    static void convertShapeF2D_128_To_128(IntFunction<float[]> fa) {
        float[] a = fa.apply(1024);
        conversion_kernel(FloatVector.SPECIES_128, DoubleVector.SPECIES_128,
                a, VectorOperators.F2D,
                ConvAPI.CONVERTSHAPE, a.length);
    }

    @Test(dataProvider = "floatUnaryOpProvider")
    static void convertShapeF2D_128_To_256(IntFunction<float[]> fa) {
        float[] a = fa.apply(1024);
        conversion_kernel(FloatVector.SPECIES_128, DoubleVector.SPECIES_256,
                a, VectorOperators.F2D,
                ConvAPI.CONVERTSHAPE, a.length);
    }

    @Test(dataProvider = "floatUnaryOpProvider")
    static void convertShapeF2D_128_To_512(IntFunction<float[]> fa) {
        float[] a = fa.apply(1024);
        conversion_kernel(FloatVector.SPECIES_128, DoubleVector.SPECIES_512,
                a, VectorOperators.F2D,
                ConvAPI.CONVERTSHAPE, a.length);
    }

    @Test(dataProvider = "floatUnaryOpProvider")
    static void convertShapeF2D_128_To_MAX(IntFunction<float[]> fa) {
        float[] a = fa.apply(1024);
        conversion_kernel(FloatVector.SPECIES_128, DoubleVector.SPECIES_MAX,
                a, VectorOperators.F2D,
                ConvAPI.CONVERTSHAPE, a.length);
    }

    @Test(dataProvider = "floatUnaryOpProvider")
    static void castShapeF2B_128_To_64(IntFunction<float[]> fa) {
        float[] a = fa.apply(1024);
        conversion_kernel(FloatVector.SPECIES_128, ByteVector.SPECIES_64,
                a, VectorOperators.F2B,
                ConvAPI.CASTSHAPE, a.length);
    }

    @Test(dataProvider = "floatUnaryOpProvider")
    static void castShapeF2B_128_To_128(IntFunction<float[]> fa) {
        float[] a = fa.apply(1024);
        conversion_kernel(FloatVector.SPECIES_128, ByteVector.SPECIES_128,
                a, VectorOperators.F2B,
                ConvAPI.CASTSHAPE, a.length);
    }

    @Test(dataProvider = "floatUnaryOpProvider")
    static void castShapeF2B_128_To_256(IntFunction<float[]> fa) {
        float[] a = fa.apply(1024);
        conversion_kernel(FloatVector.SPECIES_128, ByteVector.SPECIES_256,
                a, VectorOperators.F2B,
                ConvAPI.CASTSHAPE, a.length);
    }

    @Test(dataProvider = "floatUnaryOpProvider")
    static void castShapeF2B_128_To_512(IntFunction<float[]> fa) {
        float[] a = fa.apply(1024);
        conversion_kernel(FloatVector.SPECIES_128, ByteVector.SPECIES_512,
                a, VectorOperators.F2B,
                ConvAPI.CASTSHAPE, a.length);
    }

    @Test(dataProvider = "floatUnaryOpProvider")
    static void castShapeF2B_128_To_MAX(IntFunction<float[]> fa) {
        float[] a = fa.apply(1024);
        conversion_kernel(FloatVector.SPECIES_128, ByteVector.SPECIES_MAX,
                a, VectorOperators.F2B,
                ConvAPI.CASTSHAPE, a.length);
    }

    @Test(dataProvider = "floatUnaryOpProvider")
    static void castShapeF2S_128_To_64(IntFunction<float[]> fa) {
        float[] a = fa.apply(1024);
        conversion_kernel(FloatVector.SPECIES_128, ShortVector.SPECIES_64,
                a, VectorOperators.F2S,
                ConvAPI.CASTSHAPE, a.length);
    }

    @Test(dataProvider = "floatUnaryOpProvider")
    static void castShapeF2S_128_To_128(IntFunction<float[]> fa) {
        float[] a = fa.apply(1024);
        conversion_kernel(FloatVector.SPECIES_128, ShortVector.SPECIES_128,
                a, VectorOperators.F2S,
                ConvAPI.CASTSHAPE, a.length);
    }

    @Test(dataProvider = "floatUnaryOpProvider")
    static void castShapeF2S_128_To_256(IntFunction<float[]> fa) {
        float[] a = fa.apply(1024);
        conversion_kernel(FloatVector.SPECIES_128, ShortVector.SPECIES_256,
                a, VectorOperators.F2S,
                ConvAPI.CASTSHAPE, a.length);
    }

    @Test(dataProvider = "floatUnaryOpProvider")
    static void castShapeF2S_128_To_512(IntFunction<float[]> fa) {
        float[] a = fa.apply(1024);
        conversion_kernel(FloatVector.SPECIES_128, ShortVector.SPECIES_512,
                a, VectorOperators.F2S,
                ConvAPI.CASTSHAPE, a.length);
    }

    @Test(dataProvider = "floatUnaryOpProvider")
    static void castShapeF2S_128_To_MAX(IntFunction<float[]> fa) {
        float[] a = fa.apply(1024);
        conversion_kernel(FloatVector.SPECIES_128, ShortVector.SPECIES_MAX,
                a, VectorOperators.F2S,
                ConvAPI.CASTSHAPE, a.length);
    }

    @Test(dataProvider = "floatUnaryOpProvider")
    static void castShapeF2I_128_To_64(IntFunction<float[]> fa) {
        float[] a = fa.apply(1024);
        conversion_kernel(FloatVector.SPECIES_128, IntVector.SPECIES_64,
                a, VectorOperators.F2I,
                ConvAPI.CASTSHAPE, a.length);
    }

    @Test(dataProvider = "floatUnaryOpProvider")
    static void castShapeF2I_128_To_128(IntFunction<float[]> fa) {
        float[] a = fa.apply(1024);
        conversion_kernel(FloatVector.SPECIES_128, IntVector.SPECIES_128,
                a, VectorOperators.F2I,
                ConvAPI.CASTSHAPE, a.length);
    }

    @Test(dataProvider = "floatUnaryOpProvider")
    static void castShapeF2I_128_To_256(IntFunction<float[]> fa) {
        float[] a = fa.apply(1024);
        conversion_kernel(FloatVector.SPECIES_128, IntVector.SPECIES_256,
                a, VectorOperators.F2I,
                ConvAPI.CASTSHAPE, a.length);
    }

    @Test(dataProvider = "floatUnaryOpProvider")
    static void castShapeF2I_128_To_512(IntFunction<float[]> fa) {
        float[] a = fa.apply(1024);
        conversion_kernel(FloatVector.SPECIES_128, IntVector.SPECIES_512,
                a, VectorOperators.F2I,
                ConvAPI.CASTSHAPE, a.length);
    }

    @Test(dataProvider = "floatUnaryOpProvider")
    static void castShapeF2I_128_To_MAX(IntFunction<float[]> fa) {
        float[] a = fa.apply(1024);
        conversion_kernel(FloatVector.SPECIES_128, IntVector.SPECIES_MAX,
                a, VectorOperators.F2I,
                ConvAPI.CASTSHAPE, a.length);
    }

    @Test(dataProvider = "floatUnaryOpProvider")
    static void castShapeF2L_128_To_64(IntFunction<float[]> fa) {
        float[] a = fa.apply(1024);
        conversion_kernel(FloatVector.SPECIES_128, LongVector.SPECIES_64,
                a, VectorOperators.F2L,
                ConvAPI.CASTSHAPE, a.length);
    }

    @Test(dataProvider = "floatUnaryOpProvider")
    static void castShapeF2L_128_To_128(IntFunction<float[]> fa) {
        float[] a = fa.apply(1024);
        conversion_kernel(FloatVector.SPECIES_128, LongVector.SPECIES_128,
                a, VectorOperators.F2L,
                ConvAPI.CASTSHAPE, a.length);
    }

    @Test(dataProvider = "floatUnaryOpProvider")
    static void castShapeF2L_128_To_256(IntFunction<float[]> fa) {
        float[] a = fa.apply(1024);
        conversion_kernel(FloatVector.SPECIES_128, LongVector.SPECIES_256,
                a, VectorOperators.F2L,
                ConvAPI.CASTSHAPE, a.length);
    }

    @Test(dataProvider = "floatUnaryOpProvider")
    static void castShapeF2L_128_To_512(IntFunction<float[]> fa) {
        float[] a = fa.apply(1024);
        conversion_kernel(FloatVector.SPECIES_128, LongVector.SPECIES_512,
                a, VectorOperators.F2L,
                ConvAPI.CASTSHAPE, a.length);
    }

    @Test(dataProvider = "floatUnaryOpProvider")
    static void castShapeF2L_128_To_MAX(IntFunction<float[]> fa) {
        float[] a = fa.apply(1024);
        conversion_kernel(FloatVector.SPECIES_128, LongVector.SPECIES_MAX,
                a, VectorOperators.F2L,
                ConvAPI.CASTSHAPE, a.length);
    }

    @Test(dataProvider = "floatUnaryOpProvider")
    static void castShapeF2F_128_To_64(IntFunction<float[]> fa) {
        float[] a = fa.apply(1024);
        conversion_kernel(FloatVector.SPECIES_128, FloatVector.SPECIES_64,
                a, F2F,
                ConvAPI.CASTSHAPE, a.length);
    }

    @Test(dataProvider = "floatUnaryOpProvider")
    static void castShapeF2F_128_To_128(IntFunction<float[]> fa) {
        float[] a = fa.apply(1024);
        conversion_kernel(FloatVector.SPECIES_128, FloatVector.SPECIES_128,
                a, F2F,
                ConvAPI.CASTSHAPE, a.length);
    }

    @Test(dataProvider = "floatUnaryOpProvider")
    static void castShapeF2F_128_To_256(IntFunction<float[]> fa) {
        float[] a = fa.apply(1024);
        conversion_kernel(FloatVector.SPECIES_128, FloatVector.SPECIES_256,
                a, F2F,
                ConvAPI.CASTSHAPE, a.length);
    }

    @Test(dataProvider = "floatUnaryOpProvider")
    static void castShapeF2F_128_To_512(IntFunction<float[]> fa) {
        float[] a = fa.apply(1024);
        conversion_kernel(FloatVector.SPECIES_128, FloatVector.SPECIES_512,
                a, F2F,
                ConvAPI.CASTSHAPE, a.length);
    }

    @Test(dataProvider = "floatUnaryOpProvider")
    static void castShapeF2F_128_To_MAX(IntFunction<float[]> fa) {
        float[] a = fa.apply(1024);
        conversion_kernel(FloatVector.SPECIES_128, FloatVector.SPECIES_MAX,
                a, F2F,
                ConvAPI.CASTSHAPE, a.length);
    }

    @Test(dataProvider = "floatUnaryOpProvider")
    static void castShapeF2D_128_To_64(IntFunction<float[]> fa) {
        float[] a = fa.apply(1024);
        conversion_kernel(FloatVector.SPECIES_128, DoubleVector.SPECIES_64,
                a, VectorOperators.F2D,
                ConvAPI.CASTSHAPE, a.length);
    }

    @Test(dataProvider = "floatUnaryOpProvider")
    static void castShapeF2D_128_To_128(IntFunction<float[]> fa) {
        float[] a = fa.apply(1024);
        conversion_kernel(FloatVector.SPECIES_128, DoubleVector.SPECIES_128,
                a, VectorOperators.F2D,
                ConvAPI.CASTSHAPE, a.length);
    }

    @Test(dataProvider = "floatUnaryOpProvider")
    static void castShapeF2D_128_To_256(IntFunction<float[]> fa) {
        float[] a = fa.apply(1024);
        conversion_kernel(FloatVector.SPECIES_128, DoubleVector.SPECIES_256,
                a, VectorOperators.F2D,
                ConvAPI.CASTSHAPE, a.length);
    }

    @Test(dataProvider = "floatUnaryOpProvider")
    static void castShapeF2D_128_To_512(IntFunction<float[]> fa) {
        float[] a = fa.apply(1024);
        conversion_kernel(FloatVector.SPECIES_128, DoubleVector.SPECIES_512,
                a, VectorOperators.F2D,
                ConvAPI.CASTSHAPE, a.length);
    }

    @Test(dataProvider = "floatUnaryOpProvider")
    static void castShapeF2D_128_To_MAX(IntFunction<float[]> fa) {
        float[] a = fa.apply(1024);
        conversion_kernel(FloatVector.SPECIES_128, DoubleVector.SPECIES_MAX,
                a, VectorOperators.F2D,
                ConvAPI.CASTSHAPE, a.length);
    }

    @Test(dataProvider = "floatUnaryOpProvider")
    static void reinterpretShapeF2B_128_To_64(IntFunction<float[]> fa) {
        float[] a = fa.apply(1024);
        reinterpret_kernel(FloatVector.SPECIES_128, ByteVector.SPECIES_64,
                a, a.length);
    }

    @Test(dataProvider = "floatUnaryOpProvider")
    static void reinterpretShapeF2B_128_To_128(IntFunction<float[]> fa) {
        float[] a = fa.apply(1024);
        reinterpret_kernel(FloatVector.SPECIES_128, ByteVector.SPECIES_128,
                a, a.length);
    }

    @Test(dataProvider = "floatUnaryOpProvider")
    static void reinterpretShapeF2B_128_To_256(IntFunction<float[]> fa) {
        float[] a = fa.apply(1024);
        reinterpret_kernel(FloatVector.SPECIES_128, ByteVector.SPECIES_256,
                a, a.length);
    }

    @Test(dataProvider = "floatUnaryOpProvider")
    static void reinterpretShapeF2B_128_To_512(IntFunction<float[]> fa) {
        float[] a = fa.apply(1024);
        reinterpret_kernel(FloatVector.SPECIES_128, ByteVector.SPECIES_512,
                a, a.length);
    }

    @Test(dataProvider = "floatUnaryOpProvider")
    static void reinterpretShapeF2B_128_To_MAX(IntFunction<float[]> fa) {
        float[] a = fa.apply(1024);
        reinterpret_kernel(FloatVector.SPECIES_128, ByteVector.SPECIES_MAX,
                a, a.length);
    }

    @Test(dataProvider = "floatUnaryOpProvider")
    static void reinterpretShapeF2S_128_To_64(IntFunction<float[]> fa) {
        float[] a = fa.apply(1024);
        reinterpret_kernel(FloatVector.SPECIES_128, ShortVector.SPECIES_64,
                a, a.length);
    }

    @Test(dataProvider = "floatUnaryOpProvider")
    static void reinterpretShapeF2S_128_To_128(IntFunction<float[]> fa) {
        float[] a = fa.apply(1024);
        reinterpret_kernel(FloatVector.SPECIES_128, ShortVector.SPECIES_128,
                a, a.length);
    }

    @Test(dataProvider = "floatUnaryOpProvider")
    static void reinterpretShapeF2S_128_To_256(IntFunction<float[]> fa) {
        float[] a = fa.apply(1024);
        reinterpret_kernel(FloatVector.SPECIES_128, ShortVector.SPECIES_256,
                a, a.length);
    }

    @Test(dataProvider = "floatUnaryOpProvider")
    static void reinterpretShapeF2S_128_To_512(IntFunction<float[]> fa) {
        float[] a = fa.apply(1024);
        reinterpret_kernel(FloatVector.SPECIES_128, ShortVector.SPECIES_512,
                a, a.length);
    }

    @Test(dataProvider = "floatUnaryOpProvider")
    static void reinterpretShapeF2S_128_To_MAX(IntFunction<float[]> fa) {
        float[] a = fa.apply(1024);
        reinterpret_kernel(FloatVector.SPECIES_128, ShortVector.SPECIES_MAX,
                a, a.length);
    }

    @Test(dataProvider = "floatUnaryOpProvider")
    static void reinterpretShapeF2I_128_To_64(IntFunction<float[]> fa) {
        float[] a = fa.apply(1024);
        reinterpret_kernel(FloatVector.SPECIES_128, IntVector.SPECIES_64,
                a, a.length);
    }

    @Test(dataProvider = "floatUnaryOpProvider")
    static void reinterpretShapeF2I_128_To_128(IntFunction<float[]> fa) {
        float[] a = fa.apply(1024);
        reinterpret_kernel(FloatVector.SPECIES_128, IntVector.SPECIES_128,
                a, a.length);
    }

    @Test(dataProvider = "floatUnaryOpProvider")
    static void reinterpretShapeF2I_128_To_256(IntFunction<float[]> fa) {
        float[] a = fa.apply(1024);
        reinterpret_kernel(FloatVector.SPECIES_128, IntVector.SPECIES_256,
                a, a.length);
    }

    @Test(dataProvider = "floatUnaryOpProvider")
    static void reinterpretShapeF2I_128_To_512(IntFunction<float[]> fa) {
        float[] a = fa.apply(1024);
        reinterpret_kernel(FloatVector.SPECIES_128, IntVector.SPECIES_512,
                a, a.length);
    }

    @Test(dataProvider = "floatUnaryOpProvider")
    static void reinterpretShapeF2I_128_To_MAX(IntFunction<float[]> fa) {
        float[] a = fa.apply(1024);
        reinterpret_kernel(FloatVector.SPECIES_128, IntVector.SPECIES_MAX,
                a, a.length);
    }

    @Test(dataProvider = "floatUnaryOpProvider")
    static void reinterpretShapeF2L_128_To_64(IntFunction<float[]> fa) {
        float[] a = fa.apply(1024);
        reinterpret_kernel(FloatVector.SPECIES_128, LongVector.SPECIES_64,
                a, a.length);
    }

    @Test(dataProvider = "floatUnaryOpProvider")
    static void reinterpretShapeF2L_128_To_128(IntFunction<float[]> fa) {
        float[] a = fa.apply(1024);
        reinterpret_kernel(FloatVector.SPECIES_128, LongVector.SPECIES_128,
                a, a.length);
    }

    @Test(dataProvider = "floatUnaryOpProvider")
    static void reinterpretShapeF2L_128_To_256(IntFunction<float[]> fa) {
        float[] a = fa.apply(1024);
        reinterpret_kernel(FloatVector.SPECIES_128, LongVector.SPECIES_256,
                a, a.length);
    }

    @Test(dataProvider = "floatUnaryOpProvider")
    static void reinterpretShapeF2L_128_To_512(IntFunction<float[]> fa) {
        float[] a = fa.apply(1024);
        reinterpret_kernel(FloatVector.SPECIES_128, LongVector.SPECIES_512,
                a, a.length);
    }

    @Test(dataProvider = "floatUnaryOpProvider")
    static void reinterpretShapeF2L_128_To_MAX(IntFunction<float[]> fa) {
        float[] a = fa.apply(1024);
        reinterpret_kernel(FloatVector.SPECIES_128, LongVector.SPECIES_MAX,
                a, a.length);
    }

    @Test(dataProvider = "floatUnaryOpProvider")
    static void reinterpretShapeF2F_128_To_64(IntFunction<float[]> fa) {
        float[] a = fa.apply(1024);
        reinterpret_kernel(FloatVector.SPECIES_128, FloatVector.SPECIES_64,
                a, a.length);
    }

    @Test(dataProvider = "floatUnaryOpProvider")
    static void reinterpretShapeF2F_128_To_128(IntFunction<float[]> fa) {
        float[] a = fa.apply(1024);
        reinterpret_kernel(FloatVector.SPECIES_128, FloatVector.SPECIES_128,
                a, a.length);
    }

    @Test(dataProvider = "floatUnaryOpProvider")
    static void reinterpretShapeF2F_128_To_256(IntFunction<float[]> fa) {
        float[] a = fa.apply(1024);
        reinterpret_kernel(FloatVector.SPECIES_128, FloatVector.SPECIES_256,
                a, a.length);
    }

    @Test(dataProvider = "floatUnaryOpProvider")
    static void reinterpretShapeF2F_128_To_512(IntFunction<float[]> fa) {
        float[] a = fa.apply(1024);
        reinterpret_kernel(FloatVector.SPECIES_128, FloatVector.SPECIES_512,
                a, a.length);
    }

    @Test(dataProvider = "floatUnaryOpProvider")
    static void reinterpretShapeF2F_128_To_MAX(IntFunction<float[]> fa) {
        float[] a = fa.apply(1024);
        reinterpret_kernel(FloatVector.SPECIES_128, FloatVector.SPECIES_MAX,
                a, a.length);
    }

    @Test(dataProvider = "floatUnaryOpProvider")
    static void reinterpretShapeF2D_128_To_64(IntFunction<float[]> fa) {
        float[] a = fa.apply(1024);
        reinterpret_kernel(FloatVector.SPECIES_128, DoubleVector.SPECIES_64,
                a, a.length);
    }

    @Test(dataProvider = "floatUnaryOpProvider")
    static void reinterpretShapeF2D_128_To_128(IntFunction<float[]> fa) {
        float[] a = fa.apply(1024);
        reinterpret_kernel(FloatVector.SPECIES_128, DoubleVector.SPECIES_128,
                a, a.length);
    }

    @Test(dataProvider = "floatUnaryOpProvider")
    static void reinterpretShapeF2D_128_To_256(IntFunction<float[]> fa) {
        float[] a = fa.apply(1024);
        reinterpret_kernel(FloatVector.SPECIES_128, DoubleVector.SPECIES_256,
                a, a.length);
    }

    @Test(dataProvider = "floatUnaryOpProvider")
    static void reinterpretShapeF2D_128_To_512(IntFunction<float[]> fa) {
        float[] a = fa.apply(1024);
        reinterpret_kernel(FloatVector.SPECIES_128, DoubleVector.SPECIES_512,
                a, a.length);
    }

    @Test(dataProvider = "floatUnaryOpProvider")
    static void reinterpretShapeF2D_128_To_MAX(IntFunction<float[]> fa) {
        float[] a = fa.apply(1024);
        reinterpret_kernel(FloatVector.SPECIES_128, DoubleVector.SPECIES_MAX,
                a, a.length);
    }


    @Test(dataProvider = "intUnaryOpProvider")
    static void convertI2B_128_To_128(IntFunction<int[]> fa) {
        int[] a = fa.apply(1024);
        conversion_kernel(IntVector.SPECIES_128, ByteVector.SPECIES_128,
                a, VectorOperators.I2B,
                ConvAPI.CONVERT, a.length);
    }

    @Test(dataProvider = "intUnaryOpProvider")
    static void convertI2S_128_To_128(IntFunction<int[]> fa) {
        int[] a = fa.apply(1024);
        conversion_kernel(IntVector.SPECIES_128, ShortVector.SPECIES_128,
                a, VectorOperators.I2S,
                ConvAPI.CONVERT, a.length);
    }

    @Test(dataProvider = "intUnaryOpProvider")
    static void convertI2I_128_To_128(IntFunction<int[]> fa) {
        int[] a = fa.apply(1024);
        conversion_kernel(IntVector.SPECIES_128, IntVector.SPECIES_128,
                a, I2I,
                ConvAPI.CONVERT, a.length);
    }

    @Test(dataProvider = "intUnaryOpProvider")
    static void convertI2L_128_To_128(IntFunction<int[]> fa) {
        int[] a = fa.apply(1024);
        conversion_kernel(IntVector.SPECIES_128, LongVector.SPECIES_128,
                a, VectorOperators.I2L,
                ConvAPI.CONVERT, a.length);
    }

    @Test(dataProvider = "intUnaryOpProvider")
    static void convertI2F_128_To_128(IntFunction<int[]> fa) {
        int[] a = fa.apply(1024);
        conversion_kernel(IntVector.SPECIES_128, FloatVector.SPECIES_128,
                a, VectorOperators.I2F,
                ConvAPI.CONVERT, a.length);
    }

    @Test(dataProvider = "intUnaryOpProvider")
    static void convertI2D_128_To_128(IntFunction<int[]> fa) {
        int[] a = fa.apply(1024);
        conversion_kernel(IntVector.SPECIES_128, DoubleVector.SPECIES_128,
                a, VectorOperators.I2D,
                ConvAPI.CONVERT, a.length);
    }

    @Test(dataProvider = "intUnaryOpProvider")
    static void convertShapeI2B_128_To_64(IntFunction<int[]> fa) {
        int[] a = fa.apply(1024);
        conversion_kernel(IntVector.SPECIES_128, ByteVector.SPECIES_64,
                a, VectorOperators.I2B,
                ConvAPI.CONVERTSHAPE, a.length);
    }

    @Test(dataProvider = "intUnaryOpProvider")
    static void convertShapeI2B_128_To_128(IntFunction<int[]> fa) {
        int[] a = fa.apply(1024);
        conversion_kernel(IntVector.SPECIES_128, ByteVector.SPECIES_128,
                a, VectorOperators.I2B,
                ConvAPI.CONVERTSHAPE, a.length);
    }

    @Test(dataProvider = "intUnaryOpProvider")
    static void convertShapeI2B_128_To_256(IntFunction<int[]> fa) {
        int[] a = fa.apply(1024);
        conversion_kernel(IntVector.SPECIES_128, ByteVector.SPECIES_256,
                a, VectorOperators.I2B,
                ConvAPI.CONVERTSHAPE, a.length);
    }

    @Test(dataProvider = "intUnaryOpProvider")
    static void convertShapeI2B_128_To_512(IntFunction<int[]> fa) {
        int[] a = fa.apply(1024);
        conversion_kernel(IntVector.SPECIES_128, ByteVector.SPECIES_512,
                a, VectorOperators.I2B,
                ConvAPI.CONVERTSHAPE, a.length);
    }

    @Test(dataProvider = "intUnaryOpProvider")
    static void convertShapeI2B_128_To_MAX(IntFunction<int[]> fa) {
        int[] a = fa.apply(1024);
        conversion_kernel(IntVector.SPECIES_128, ByteVector.SPECIES_MAX,
                a, VectorOperators.I2B,
                ConvAPI.CONVERTSHAPE, a.length);
    }

    @Test(dataProvider = "intUnaryOpProvider")
    static void convertShapeI2S_128_To_64(IntFunction<int[]> fa) {
        int[] a = fa.apply(1024);
        conversion_kernel(IntVector.SPECIES_128, ShortVector.SPECIES_64,
                a, VectorOperators.I2S,
                ConvAPI.CONVERTSHAPE, a.length);
    }

    @Test(dataProvider = "intUnaryOpProvider")
    static void convertShapeI2S_128_To_128(IntFunction<int[]> fa) {
        int[] a = fa.apply(1024);
        conversion_kernel(IntVector.SPECIES_128, ShortVector.SPECIES_128,
                a, VectorOperators.I2S,
                ConvAPI.CONVERTSHAPE, a.length);
    }

    @Test(dataProvider = "intUnaryOpProvider")
    static void convertShapeI2S_128_To_256(IntFunction<int[]> fa) {
        int[] a = fa.apply(1024);
        conversion_kernel(IntVector.SPECIES_128, ShortVector.SPECIES_256,
                a, VectorOperators.I2S,
                ConvAPI.CONVERTSHAPE, a.length);
    }

    @Test(dataProvider = "intUnaryOpProvider")
    static void convertShapeI2S_128_To_512(IntFunction<int[]> fa) {
        int[] a = fa.apply(1024);
        conversion_kernel(IntVector.SPECIES_128, ShortVector.SPECIES_512,
                a, VectorOperators.I2S,
                ConvAPI.CONVERTSHAPE, a.length);
    }

    @Test(dataProvider = "intUnaryOpProvider")
    static void convertShapeI2S_128_To_MAX(IntFunction<int[]> fa) {
        int[] a = fa.apply(1024);
        conversion_kernel(IntVector.SPECIES_128, ShortVector.SPECIES_MAX,
                a, VectorOperators.I2S,
                ConvAPI.CONVERTSHAPE, a.length);
    }

    @Test(dataProvider = "intUnaryOpProvider")
    static void convertShapeI2I_128_To_64(IntFunction<int[]> fa) {
        int[] a = fa.apply(1024);
        conversion_kernel(IntVector.SPECIES_128, IntVector.SPECIES_64,
                a, I2I,
                ConvAPI.CONVERTSHAPE, a.length);
    }

    @Test(dataProvider = "intUnaryOpProvider")
    static void convertShapeI2I_128_To_128(IntFunction<int[]> fa) {
        int[] a = fa.apply(1024);
        conversion_kernel(IntVector.SPECIES_128, IntVector.SPECIES_128,
                a, I2I,
                ConvAPI.CONVERTSHAPE, a.length);
    }

    @Test(dataProvider = "intUnaryOpProvider")
    static void convertShapeI2I_128_To_256(IntFunction<int[]> fa) {
        int[] a = fa.apply(1024);
        conversion_kernel(IntVector.SPECIES_128, IntVector.SPECIES_256,
                a, I2I,
                ConvAPI.CONVERTSHAPE, a.length);
    }

    @Test(dataProvider = "intUnaryOpProvider")
    static void convertShapeI2I_128_To_512(IntFunction<int[]> fa) {
        int[] a = fa.apply(1024);
        conversion_kernel(IntVector.SPECIES_128, IntVector.SPECIES_512,
                a, I2I,
                ConvAPI.CONVERTSHAPE, a.length);
    }

    @Test(dataProvider = "intUnaryOpProvider")
    static void convertShapeI2I_128_To_MAX(IntFunction<int[]> fa) {
        int[] a = fa.apply(1024);
        conversion_kernel(IntVector.SPECIES_128, IntVector.SPECIES_MAX,
                a, I2I,
                ConvAPI.CONVERTSHAPE, a.length);
    }

    @Test(dataProvider = "intUnaryOpProvider")
    static void convertShapeI2L_128_To_64(IntFunction<int[]> fa) {
        int[] a = fa.apply(1024);
        conversion_kernel(IntVector.SPECIES_128, LongVector.SPECIES_64,
                a, VectorOperators.I2L,
                ConvAPI.CONVERTSHAPE, a.length);
    }

    @Test(dataProvider = "intUnaryOpProvider")
    static void convertShapeI2L_128_To_128(IntFunction<int[]> fa) {
        int[] a = fa.apply(1024);
        conversion_kernel(IntVector.SPECIES_128, LongVector.SPECIES_128,
                a, VectorOperators.I2L,
                ConvAPI.CONVERTSHAPE, a.length);
    }

    @Test(dataProvider = "intUnaryOpProvider")
    static void convertShapeI2L_128_To_256(IntFunction<int[]> fa) {
        int[] a = fa.apply(1024);
        conversion_kernel(IntVector.SPECIES_128, LongVector.SPECIES_256,
                a, VectorOperators.I2L,
                ConvAPI.CONVERTSHAPE, a.length);
    }

    @Test(dataProvider = "intUnaryOpProvider")
    static void convertShapeI2L_128_To_512(IntFunction<int[]> fa) {
        int[] a = fa.apply(1024);
        conversion_kernel(IntVector.SPECIES_128, LongVector.SPECIES_512,
                a, VectorOperators.I2L,
                ConvAPI.CONVERTSHAPE, a.length);
    }

    @Test(dataProvider = "intUnaryOpProvider")
    static void convertShapeI2L_128_To_MAX(IntFunction<int[]> fa) {
        int[] a = fa.apply(1024);
        conversion_kernel(IntVector.SPECIES_128, LongVector.SPECIES_MAX,
                a, VectorOperators.I2L,
                ConvAPI.CONVERTSHAPE, a.length);
    }

    @Test(dataProvider = "intUnaryOpProvider")
    static void convertShapeI2F_128_To_64(IntFunction<int[]> fa) {
        int[] a = fa.apply(1024);
        conversion_kernel(IntVector.SPECIES_128, FloatVector.SPECIES_64,
                a, VectorOperators.I2F,
                ConvAPI.CONVERTSHAPE, a.length);
    }

    @Test(dataProvider = "intUnaryOpProvider")
    static void convertShapeI2F_128_To_128(IntFunction<int[]> fa) {
        int[] a = fa.apply(1024);
        conversion_kernel(IntVector.SPECIES_128, FloatVector.SPECIES_128,
                a, VectorOperators.I2F,
                ConvAPI.CONVERTSHAPE, a.length);
    }

    @Test(dataProvider = "intUnaryOpProvider")
    static void convertShapeI2F_128_To_256(IntFunction<int[]> fa) {
        int[] a = fa.apply(1024);
        conversion_kernel(IntVector.SPECIES_128, FloatVector.SPECIES_256,
                a, VectorOperators.I2F,
                ConvAPI.CONVERTSHAPE, a.length);
    }

    @Test(dataProvider = "intUnaryOpProvider")
    static void convertShapeI2F_128_To_512(IntFunction<int[]> fa) {
        int[] a = fa.apply(1024);
        conversion_kernel(IntVector.SPECIES_128, FloatVector.SPECIES_512,
                a, VectorOperators.I2F,
                ConvAPI.CONVERTSHAPE, a.length);
    }

    @Test(dataProvider = "intUnaryOpProvider")
    static void convertShapeI2F_128_To_MAX(IntFunction<int[]> fa) {
        int[] a = fa.apply(1024);
        conversion_kernel(IntVector.SPECIES_128, FloatVector.SPECIES_MAX,
                a, VectorOperators.I2F,
                ConvAPI.CONVERTSHAPE, a.length);
    }

    @Test(dataProvider = "intUnaryOpProvider")
    static void convertShapeI2D_128_To_64(IntFunction<int[]> fa) {
        int[] a = fa.apply(1024);
        conversion_kernel(IntVector.SPECIES_128, DoubleVector.SPECIES_64,
                a, VectorOperators.I2D,
                ConvAPI.CONVERTSHAPE, a.length);
    }

    @Test(dataProvider = "intUnaryOpProvider")
    static void convertShapeI2D_128_To_128(IntFunction<int[]> fa) {
        int[] a = fa.apply(1024);
        conversion_kernel(IntVector.SPECIES_128, DoubleVector.SPECIES_128,
                a, VectorOperators.I2D,
                ConvAPI.CONVERTSHAPE, a.length);
    }

    @Test(dataProvider = "intUnaryOpProvider")
    static void convertShapeI2D_128_To_256(IntFunction<int[]> fa) {
        int[] a = fa.apply(1024);
        conversion_kernel(IntVector.SPECIES_128, DoubleVector.SPECIES_256,
                a, VectorOperators.I2D,
                ConvAPI.CONVERTSHAPE, a.length);
    }

    @Test(dataProvider = "intUnaryOpProvider")
    static void convertShapeI2D_128_To_512(IntFunction<int[]> fa) {
        int[] a = fa.apply(1024);
        conversion_kernel(IntVector.SPECIES_128, DoubleVector.SPECIES_512,
                a, VectorOperators.I2D,
                ConvAPI.CONVERTSHAPE, a.length);
    }

    @Test(dataProvider = "intUnaryOpProvider")
    static void convertShapeI2D_128_To_MAX(IntFunction<int[]> fa) {
        int[] a = fa.apply(1024);
        conversion_kernel(IntVector.SPECIES_128, DoubleVector.SPECIES_MAX,
                a, VectorOperators.I2D,
                ConvAPI.CONVERTSHAPE, a.length);
    }

    @Test(dataProvider = "intUnaryOpProvider")
    static void castShapeI2B_128_To_64(IntFunction<int[]> fa) {
        int[] a = fa.apply(1024);
        conversion_kernel(IntVector.SPECIES_128, ByteVector.SPECIES_64,
                a, VectorOperators.I2B,
                ConvAPI.CASTSHAPE, a.length);
    }

    @Test(dataProvider = "intUnaryOpProvider")
    static void castShapeI2B_128_To_128(IntFunction<int[]> fa) {
        int[] a = fa.apply(1024);
        conversion_kernel(IntVector.SPECIES_128, ByteVector.SPECIES_128,
                a, VectorOperators.I2B,
                ConvAPI.CASTSHAPE, a.length);
    }

    @Test(dataProvider = "intUnaryOpProvider")
    static void castShapeI2B_128_To_256(IntFunction<int[]> fa) {
        int[] a = fa.apply(1024);
        conversion_kernel(IntVector.SPECIES_128, ByteVector.SPECIES_256,
                a, VectorOperators.I2B,
                ConvAPI.CASTSHAPE, a.length);
    }

    @Test(dataProvider = "intUnaryOpProvider")
    static void castShapeI2B_128_To_512(IntFunction<int[]> fa) {
        int[] a = fa.apply(1024);
        conversion_kernel(IntVector.SPECIES_128, ByteVector.SPECIES_512,
                a, VectorOperators.I2B,
                ConvAPI.CASTSHAPE, a.length);
    }

    @Test(dataProvider = "intUnaryOpProvider")
    static void castShapeI2B_128_To_MAX(IntFunction<int[]> fa) {
        int[] a = fa.apply(1024);
        conversion_kernel(IntVector.SPECIES_128, ByteVector.SPECIES_MAX,
                a, VectorOperators.I2B,
                ConvAPI.CASTSHAPE, a.length);
    }

    @Test(dataProvider = "intUnaryOpProvider")
    static void castShapeI2S_128_To_64(IntFunction<int[]> fa) {
        int[] a = fa.apply(1024);
        conversion_kernel(IntVector.SPECIES_128, ShortVector.SPECIES_64,
                a, VectorOperators.I2S,
                ConvAPI.CASTSHAPE, a.length);
    }

    @Test(dataProvider = "intUnaryOpProvider")
    static void castShapeI2S_128_To_128(IntFunction<int[]> fa) {
        int[] a = fa.apply(1024);
        conversion_kernel(IntVector.SPECIES_128, ShortVector.SPECIES_128,
                a, VectorOperators.I2S,
                ConvAPI.CASTSHAPE, a.length);
    }

    @Test(dataProvider = "intUnaryOpProvider")
    static void castShapeI2S_128_To_256(IntFunction<int[]> fa) {
        int[] a = fa.apply(1024);
        conversion_kernel(IntVector.SPECIES_128, ShortVector.SPECIES_256,
                a, VectorOperators.I2S,
                ConvAPI.CASTSHAPE, a.length);
    }

    @Test(dataProvider = "intUnaryOpProvider")
    static void castShapeI2S_128_To_512(IntFunction<int[]> fa) {
        int[] a = fa.apply(1024);
        conversion_kernel(IntVector.SPECIES_128, ShortVector.SPECIES_512,
                a, VectorOperators.I2S,
                ConvAPI.CASTSHAPE, a.length);
    }

    @Test(dataProvider = "intUnaryOpProvider")
    static void castShapeI2S_128_To_MAX(IntFunction<int[]> fa) {
        int[] a = fa.apply(1024);
        conversion_kernel(IntVector.SPECIES_128, ShortVector.SPECIES_MAX,
                a, VectorOperators.I2S,
                ConvAPI.CASTSHAPE, a.length);
    }

    @Test(dataProvider = "intUnaryOpProvider")
    static void castShapeI2I_128_To_64(IntFunction<int[]> fa) {
        int[] a = fa.apply(1024);
        conversion_kernel(IntVector.SPECIES_128, IntVector.SPECIES_64,
                a, I2I,
                ConvAPI.CASTSHAPE, a.length);
    }

    @Test(dataProvider = "intUnaryOpProvider")
    static void castShapeI2I_128_To_128(IntFunction<int[]> fa) {
        int[] a = fa.apply(1024);
        conversion_kernel(IntVector.SPECIES_128, IntVector.SPECIES_128,
                a, I2I,
                ConvAPI.CASTSHAPE, a.length);
    }

    @Test(dataProvider = "intUnaryOpProvider")
    static void castShapeI2I_128_To_256(IntFunction<int[]> fa) {
        int[] a = fa.apply(1024);
        conversion_kernel(IntVector.SPECIES_128, IntVector.SPECIES_256,
                a, I2I,
                ConvAPI.CASTSHAPE, a.length);
    }

    @Test(dataProvider = "intUnaryOpProvider")
    static void castShapeI2I_128_To_512(IntFunction<int[]> fa) {
        int[] a = fa.apply(1024);
        conversion_kernel(IntVector.SPECIES_128, IntVector.SPECIES_512,
                a, I2I,
                ConvAPI.CASTSHAPE, a.length);
    }

    @Test(dataProvider = "intUnaryOpProvider")
    static void castShapeI2I_128_To_MAX(IntFunction<int[]> fa) {
        int[] a = fa.apply(1024);
        conversion_kernel(IntVector.SPECIES_128, IntVector.SPECIES_MAX,
                a, I2I,
                ConvAPI.CASTSHAPE, a.length);
    }

    @Test(dataProvider = "intUnaryOpProvider")
    static void castShapeI2L_128_To_64(IntFunction<int[]> fa) {
        int[] a = fa.apply(1024);
        conversion_kernel(IntVector.SPECIES_128, LongVector.SPECIES_64,
                a, VectorOperators.I2L,
                ConvAPI.CASTSHAPE, a.length);
    }

    @Test(dataProvider = "intUnaryOpProvider")
    static void castShapeI2L_128_To_128(IntFunction<int[]> fa) {
        int[] a = fa.apply(1024);
        conversion_kernel(IntVector.SPECIES_128, LongVector.SPECIES_128,
                a, VectorOperators.I2L,
                ConvAPI.CASTSHAPE, a.length);
    }

    @Test(dataProvider = "intUnaryOpProvider")
    static void castShapeI2L_128_To_256(IntFunction<int[]> fa) {
        int[] a = fa.apply(1024);
        conversion_kernel(IntVector.SPECIES_128, LongVector.SPECIES_256,
                a, VectorOperators.I2L,
                ConvAPI.CASTSHAPE, a.length);
    }

    @Test(dataProvider = "intUnaryOpProvider")
    static void castShapeI2L_128_To_512(IntFunction<int[]> fa) {
        int[] a = fa.apply(1024);
        conversion_kernel(IntVector.SPECIES_128, LongVector.SPECIES_512,
                a, VectorOperators.I2L,
                ConvAPI.CASTSHAPE, a.length);
    }

    @Test(dataProvider = "intUnaryOpProvider")
    static void castShapeI2L_128_To_MAX(IntFunction<int[]> fa) {
        int[] a = fa.apply(1024);
        conversion_kernel(IntVector.SPECIES_128, LongVector.SPECIES_MAX,
                a, VectorOperators.I2L,
                ConvAPI.CASTSHAPE, a.length);
    }

    @Test(dataProvider = "intUnaryOpProvider")
    static void castShapeI2F_128_To_64(IntFunction<int[]> fa) {
        int[] a = fa.apply(1024);
        conversion_kernel(IntVector.SPECIES_128, FloatVector.SPECIES_64,
                a, VectorOperators.I2F,
                ConvAPI.CASTSHAPE, a.length);
    }

    @Test(dataProvider = "intUnaryOpProvider")
    static void castShapeI2F_128_To_128(IntFunction<int[]> fa) {
        int[] a = fa.apply(1024);
        conversion_kernel(IntVector.SPECIES_128, FloatVector.SPECIES_128,
                a, VectorOperators.I2F,
                ConvAPI.CASTSHAPE, a.length);
    }

    @Test(dataProvider = "intUnaryOpProvider")
    static void castShapeI2F_128_To_256(IntFunction<int[]> fa) {
        int[] a = fa.apply(1024);
        conversion_kernel(IntVector.SPECIES_128, FloatVector.SPECIES_256,
                a, VectorOperators.I2F,
                ConvAPI.CASTSHAPE, a.length);
    }

    @Test(dataProvider = "intUnaryOpProvider")
    static void castShapeI2F_128_To_512(IntFunction<int[]> fa) {
        int[] a = fa.apply(1024);
        conversion_kernel(IntVector.SPECIES_128, FloatVector.SPECIES_512,
                a, VectorOperators.I2F,
                ConvAPI.CASTSHAPE, a.length);
    }

    @Test(dataProvider = "intUnaryOpProvider")
    static void castShapeI2F_128_To_MAX(IntFunction<int[]> fa) {
        int[] a = fa.apply(1024);
        conversion_kernel(IntVector.SPECIES_128, FloatVector.SPECIES_MAX,
                a, VectorOperators.I2F,
                ConvAPI.CASTSHAPE, a.length);
    }

    @Test(dataProvider = "intUnaryOpProvider")
    static void castShapeI2D_128_To_64(IntFunction<int[]> fa) {
        int[] a = fa.apply(1024);
        conversion_kernel(IntVector.SPECIES_128, DoubleVector.SPECIES_64,
                a, VectorOperators.I2D,
                ConvAPI.CASTSHAPE, a.length);
    }

    @Test(dataProvider = "intUnaryOpProvider")
    static void castShapeI2D_128_To_128(IntFunction<int[]> fa) {
        int[] a = fa.apply(1024);
        conversion_kernel(IntVector.SPECIES_128, DoubleVector.SPECIES_128,
                a, VectorOperators.I2D,
                ConvAPI.CASTSHAPE, a.length);
    }

    @Test(dataProvider = "intUnaryOpProvider")
    static void castShapeI2D_128_To_256(IntFunction<int[]> fa) {
        int[] a = fa.apply(1024);
        conversion_kernel(IntVector.SPECIES_128, DoubleVector.SPECIES_256,
                a, VectorOperators.I2D,
                ConvAPI.CASTSHAPE, a.length);
    }

    @Test(dataProvider = "intUnaryOpProvider")
    static void castShapeI2D_128_To_512(IntFunction<int[]> fa) {
        int[] a = fa.apply(1024);
        conversion_kernel(IntVector.SPECIES_128, DoubleVector.SPECIES_512,
                a, VectorOperators.I2D,
                ConvAPI.CASTSHAPE, a.length);
    }

    @Test(dataProvider = "intUnaryOpProvider")
    static void castShapeI2D_128_To_MAX(IntFunction<int[]> fa) {
        int[] a = fa.apply(1024);
        conversion_kernel(IntVector.SPECIES_128, DoubleVector.SPECIES_MAX,
                a, VectorOperators.I2D,
                ConvAPI.CASTSHAPE, a.length);
    }

    @Test(dataProvider = "intUnaryOpProvider")
    static void reinterpretShapeI2B_128_To_64(IntFunction<int[]> fa) {
        int[] a = fa.apply(1024);
        reinterpret_kernel(IntVector.SPECIES_128, ByteVector.SPECIES_64,
                a, a.length);
    }

    @Test(dataProvider = "intUnaryOpProvider")
    static void reinterpretShapeI2B_128_To_128(IntFunction<int[]> fa) {
        int[] a = fa.apply(1024);
        reinterpret_kernel(IntVector.SPECIES_128, ByteVector.SPECIES_128,
                a, a.length);
    }

    @Test(dataProvider = "intUnaryOpProvider")
    static void reinterpretShapeI2B_128_To_256(IntFunction<int[]> fa) {
        int[] a = fa.apply(1024);
        reinterpret_kernel(IntVector.SPECIES_128, ByteVector.SPECIES_256,
                a, a.length);
    }

    @Test(dataProvider = "intUnaryOpProvider")
    static void reinterpretShapeI2B_128_To_512(IntFunction<int[]> fa) {
        int[] a = fa.apply(1024);
        reinterpret_kernel(IntVector.SPECIES_128, ByteVector.SPECIES_512,
                a, a.length);
    }

    @Test(dataProvider = "intUnaryOpProvider")
    static void reinterpretShapeI2B_128_To_MAX(IntFunction<int[]> fa) {
        int[] a = fa.apply(1024);
        reinterpret_kernel(IntVector.SPECIES_128, ByteVector.SPECIES_MAX,
                a, a.length);
    }

    @Test(dataProvider = "intUnaryOpProvider")
    static void reinterpretShapeI2S_128_To_64(IntFunction<int[]> fa) {
        int[] a = fa.apply(1024);
        reinterpret_kernel(IntVector.SPECIES_128, ShortVector.SPECIES_64,
                a, a.length);
    }

    @Test(dataProvider = "intUnaryOpProvider")
    static void reinterpretShapeI2S_128_To_128(IntFunction<int[]> fa) {
        int[] a = fa.apply(1024);
        reinterpret_kernel(IntVector.SPECIES_128, ShortVector.SPECIES_128,
                a, a.length);
    }

    @Test(dataProvider = "intUnaryOpProvider")
    static void reinterpretShapeI2S_128_To_256(IntFunction<int[]> fa) {
        int[] a = fa.apply(1024);
        reinterpret_kernel(IntVector.SPECIES_128, ShortVector.SPECIES_256,
                a, a.length);
    }

    @Test(dataProvider = "intUnaryOpProvider")
    static void reinterpretShapeI2S_128_To_512(IntFunction<int[]> fa) {
        int[] a = fa.apply(1024);
        reinterpret_kernel(IntVector.SPECIES_128, ShortVector.SPECIES_512,
                a, a.length);
    }

    @Test(dataProvider = "intUnaryOpProvider")
    static void reinterpretShapeI2S_128_To_MAX(IntFunction<int[]> fa) {
        int[] a = fa.apply(1024);
        reinterpret_kernel(IntVector.SPECIES_128, ShortVector.SPECIES_MAX,
                a, a.length);
    }

    @Test(dataProvider = "intUnaryOpProvider")
    static void reinterpretShapeI2I_128_To_64(IntFunction<int[]> fa) {
        int[] a = fa.apply(1024);
        reinterpret_kernel(IntVector.SPECIES_128, IntVector.SPECIES_64,
                a, a.length);
    }

    @Test(dataProvider = "intUnaryOpProvider")
    static void reinterpretShapeI2I_128_To_128(IntFunction<int[]> fa) {
        int[] a = fa.apply(1024);
        reinterpret_kernel(IntVector.SPECIES_128, IntVector.SPECIES_128,
                a, a.length);
    }

    @Test(dataProvider = "intUnaryOpProvider")
    static void reinterpretShapeI2I_128_To_256(IntFunction<int[]> fa) {
        int[] a = fa.apply(1024);
        reinterpret_kernel(IntVector.SPECIES_128, IntVector.SPECIES_256,
                a, a.length);
    }

    @Test(dataProvider = "intUnaryOpProvider")
    static void reinterpretShapeI2I_128_To_512(IntFunction<int[]> fa) {
        int[] a = fa.apply(1024);
        reinterpret_kernel(IntVector.SPECIES_128, IntVector.SPECIES_512,
                a, a.length);
    }

    @Test(dataProvider = "intUnaryOpProvider")
    static void reinterpretShapeI2I_128_To_MAX(IntFunction<int[]> fa) {
        int[] a = fa.apply(1024);
        reinterpret_kernel(IntVector.SPECIES_128, IntVector.SPECIES_MAX,
                a, a.length);
    }

    @Test(dataProvider = "intUnaryOpProvider")
    static void reinterpretShapeI2L_128_To_64(IntFunction<int[]> fa) {
        int[] a = fa.apply(1024);
        reinterpret_kernel(IntVector.SPECIES_128, LongVector.SPECIES_64,
                a, a.length);
    }

    @Test(dataProvider = "intUnaryOpProvider")
    static void reinterpretShapeI2L_128_To_128(IntFunction<int[]> fa) {
        int[] a = fa.apply(1024);
        reinterpret_kernel(IntVector.SPECIES_128, LongVector.SPECIES_128,
                a, a.length);
    }

    @Test(dataProvider = "intUnaryOpProvider")
    static void reinterpretShapeI2L_128_To_256(IntFunction<int[]> fa) {
        int[] a = fa.apply(1024);
        reinterpret_kernel(IntVector.SPECIES_128, LongVector.SPECIES_256,
                a, a.length);
    }

    @Test(dataProvider = "intUnaryOpProvider")
    static void reinterpretShapeI2L_128_To_512(IntFunction<int[]> fa) {
        int[] a = fa.apply(1024);
        reinterpret_kernel(IntVector.SPECIES_128, LongVector.SPECIES_512,
                a, a.length);
    }

    @Test(dataProvider = "intUnaryOpProvider")
    static void reinterpretShapeI2L_128_To_MAX(IntFunction<int[]> fa) {
        int[] a = fa.apply(1024);
        reinterpret_kernel(IntVector.SPECIES_128, LongVector.SPECIES_MAX,
                a, a.length);
    }

    @Test(dataProvider = "intUnaryOpProvider")
    static void reinterpretShapeI2F_128_To_64(IntFunction<int[]> fa) {
        int[] a = fa.apply(1024);
        reinterpret_kernel(IntVector.SPECIES_128, FloatVector.SPECIES_64,
                a, a.length);
    }

    @Test(dataProvider = "intUnaryOpProvider")
    static void reinterpretShapeI2F_128_To_128(IntFunction<int[]> fa) {
        int[] a = fa.apply(1024);
        reinterpret_kernel(IntVector.SPECIES_128, FloatVector.SPECIES_128,
                a, a.length);
    }

    @Test(dataProvider = "intUnaryOpProvider")
    static void reinterpretShapeI2F_128_To_256(IntFunction<int[]> fa) {
        int[] a = fa.apply(1024);
        reinterpret_kernel(IntVector.SPECIES_128, FloatVector.SPECIES_256,
                a, a.length);
    }

    @Test(dataProvider = "intUnaryOpProvider")
    static void reinterpretShapeI2F_128_To_512(IntFunction<int[]> fa) {
        int[] a = fa.apply(1024);
        reinterpret_kernel(IntVector.SPECIES_128, FloatVector.SPECIES_512,
                a, a.length);
    }

    @Test(dataProvider = "intUnaryOpProvider")
    static void reinterpretShapeI2F_128_To_MAX(IntFunction<int[]> fa) {
        int[] a = fa.apply(1024);
        reinterpret_kernel(IntVector.SPECIES_128, FloatVector.SPECIES_MAX,
                a, a.length);
    }

    @Test(dataProvider = "intUnaryOpProvider")
    static void reinterpretShapeI2D_128_To_64(IntFunction<int[]> fa) {
        int[] a = fa.apply(1024);
        reinterpret_kernel(IntVector.SPECIES_128, DoubleVector.SPECIES_64,
                a, a.length);
    }

    @Test(dataProvider = "intUnaryOpProvider")
    static void reinterpretShapeI2D_128_To_128(IntFunction<int[]> fa) {
        int[] a = fa.apply(1024);
        reinterpret_kernel(IntVector.SPECIES_128, DoubleVector.SPECIES_128,
                a, a.length);
    }

    @Test(dataProvider = "intUnaryOpProvider")
    static void reinterpretShapeI2D_128_To_256(IntFunction<int[]> fa) {
        int[] a = fa.apply(1024);
        reinterpret_kernel(IntVector.SPECIES_128, DoubleVector.SPECIES_256,
                a, a.length);
    }

    @Test(dataProvider = "intUnaryOpProvider")
    static void reinterpretShapeI2D_128_To_512(IntFunction<int[]> fa) {
        int[] a = fa.apply(1024);
        reinterpret_kernel(IntVector.SPECIES_128, DoubleVector.SPECIES_512,
                a, a.length);
    }

    @Test(dataProvider = "intUnaryOpProvider")
    static void reinterpretShapeI2D_128_To_MAX(IntFunction<int[]> fa) {
        int[] a = fa.apply(1024);
        reinterpret_kernel(IntVector.SPECIES_128, DoubleVector.SPECIES_MAX,
                a, a.length);
    }


    @Test(dataProvider = "longUnaryOpProvider")
    static void convertL2B_128_To_128(IntFunction<long[]> fa) {
        long[] a = fa.apply(1024);
        conversion_kernel(LongVector.SPECIES_128, ByteVector.SPECIES_128,
                a, VectorOperators.L2B,
                ConvAPI.CONVERT, a.length);
    }

    @Test(dataProvider = "longUnaryOpProvider")
    static void convertL2S_128_To_128(IntFunction<long[]> fa) {
        long[] a = fa.apply(1024);
        conversion_kernel(LongVector.SPECIES_128, ShortVector.SPECIES_128,
                a, VectorOperators.L2S,
                ConvAPI.CONVERT, a.length);
    }

    @Test(dataProvider = "longUnaryOpProvider")
    static void convertL2I_128_To_128(IntFunction<long[]> fa) {
        long[] a = fa.apply(1024);
        conversion_kernel(LongVector.SPECIES_128, IntVector.SPECIES_128,
                a, VectorOperators.L2I,
                ConvAPI.CONVERT, a.length);
    }

    @Test(dataProvider = "longUnaryOpProvider")
    static void convertL2L_128_To_128(IntFunction<long[]> fa) {
        long[] a = fa.apply(1024);
        conversion_kernel(LongVector.SPECIES_128, LongVector.SPECIES_128,
                a, L2L,
                ConvAPI.CONVERT, a.length);
    }

    @Test(dataProvider = "longUnaryOpProvider")
    static void convertL2F_128_To_128(IntFunction<long[]> fa) {
        long[] a = fa.apply(1024);
        conversion_kernel(LongVector.SPECIES_128, FloatVector.SPECIES_128,
                a, VectorOperators.L2F,
                ConvAPI.CONVERT, a.length);
    }

    @Test(dataProvider = "longUnaryOpProvider")
    static void convertL2D_128_To_128(IntFunction<long[]> fa) {
        long[] a = fa.apply(1024);
        conversion_kernel(LongVector.SPECIES_128, DoubleVector.SPECIES_128,
                a, VectorOperators.L2D,
                ConvAPI.CONVERT, a.length);
    }

    @Test(dataProvider = "longUnaryOpProvider")
    static void convertShapeL2B_128_To_64(IntFunction<long[]> fa) {
        long[] a = fa.apply(1024);
        conversion_kernel(LongVector.SPECIES_128, ByteVector.SPECIES_64,
                a, VectorOperators.L2B,
                ConvAPI.CONVERTSHAPE, a.length);
    }

    @Test(dataProvider = "longUnaryOpProvider")
    static void convertShapeL2B_128_To_128(IntFunction<long[]> fa) {
        long[] a = fa.apply(1024);
        conversion_kernel(LongVector.SPECIES_128, ByteVector.SPECIES_128,
                a, VectorOperators.L2B,
                ConvAPI.CONVERTSHAPE, a.length);
    }

    @Test(dataProvider = "longUnaryOpProvider")
    static void convertShapeL2B_128_To_256(IntFunction<long[]> fa) {
        long[] a = fa.apply(1024);
        conversion_kernel(LongVector.SPECIES_128, ByteVector.SPECIES_256,
                a, VectorOperators.L2B,
                ConvAPI.CONVERTSHAPE, a.length);
    }

    @Test(dataProvider = "longUnaryOpProvider")
    static void convertShapeL2B_128_To_512(IntFunction<long[]> fa) {
        long[] a = fa.apply(1024);
        conversion_kernel(LongVector.SPECIES_128, ByteVector.SPECIES_512,
                a, VectorOperators.L2B,
                ConvAPI.CONVERTSHAPE, a.length);
    }

    @Test(dataProvider = "longUnaryOpProvider")
    static void convertShapeL2B_128_To_MAX(IntFunction<long[]> fa) {
        long[] a = fa.apply(1024);
        conversion_kernel(LongVector.SPECIES_128, ByteVector.SPECIES_MAX,
                a, VectorOperators.L2B,
                ConvAPI.CONVERTSHAPE, a.length);
    }

    @Test(dataProvider = "longUnaryOpProvider")
    static void convertShapeL2S_128_To_64(IntFunction<long[]> fa) {
        long[] a = fa.apply(1024);
        conversion_kernel(LongVector.SPECIES_128, ShortVector.SPECIES_64,
                a, VectorOperators.L2S,
                ConvAPI.CONVERTSHAPE, a.length);
    }

    @Test(dataProvider = "longUnaryOpProvider")
    static void convertShapeL2S_128_To_128(IntFunction<long[]> fa) {
        long[] a = fa.apply(1024);
        conversion_kernel(LongVector.SPECIES_128, ShortVector.SPECIES_128,
                a, VectorOperators.L2S,
                ConvAPI.CONVERTSHAPE, a.length);
    }

    @Test(dataProvider = "longUnaryOpProvider")
    static void convertShapeL2S_128_To_256(IntFunction<long[]> fa) {
        long[] a = fa.apply(1024);
        conversion_kernel(LongVector.SPECIES_128, ShortVector.SPECIES_256,
                a, VectorOperators.L2S,
                ConvAPI.CONVERTSHAPE, a.length);
    }

    @Test(dataProvider = "longUnaryOpProvider")
    static void convertShapeL2S_128_To_512(IntFunction<long[]> fa) {
        long[] a = fa.apply(1024);
        conversion_kernel(LongVector.SPECIES_128, ShortVector.SPECIES_512,
                a, VectorOperators.L2S,
                ConvAPI.CONVERTSHAPE, a.length);
    }

    @Test(dataProvider = "longUnaryOpProvider")
    static void convertShapeL2S_128_To_MAX(IntFunction<long[]> fa) {
        long[] a = fa.apply(1024);
        conversion_kernel(LongVector.SPECIES_128, ShortVector.SPECIES_MAX,
                a, VectorOperators.L2S,
                ConvAPI.CONVERTSHAPE, a.length);
    }

    @Test(dataProvider = "longUnaryOpProvider")
    static void convertShapeL2I_128_To_64(IntFunction<long[]> fa) {
        long[] a = fa.apply(1024);
        conversion_kernel(LongVector.SPECIES_128, IntVector.SPECIES_64,
                a, VectorOperators.L2I,
                ConvAPI.CONVERTSHAPE, a.length);
    }

    @Test(dataProvider = "longUnaryOpProvider")
    static void convertShapeL2I_128_To_128(IntFunction<long[]> fa) {
        long[] a = fa.apply(1024);
        conversion_kernel(LongVector.SPECIES_128, IntVector.SPECIES_128,
                a, VectorOperators.L2I,
                ConvAPI.CONVERTSHAPE, a.length);
    }

    @Test(dataProvider = "longUnaryOpProvider")
    static void convertShapeL2I_128_To_256(IntFunction<long[]> fa) {
        long[] a = fa.apply(1024);
        conversion_kernel(LongVector.SPECIES_128, IntVector.SPECIES_256,
                a, VectorOperators.L2I,
                ConvAPI.CONVERTSHAPE, a.length);
    }

    @Test(dataProvider = "longUnaryOpProvider")
    static void convertShapeL2I_128_To_512(IntFunction<long[]> fa) {
        long[] a = fa.apply(1024);
        conversion_kernel(LongVector.SPECIES_128, IntVector.SPECIES_512,
                a, VectorOperators.L2I,
                ConvAPI.CONVERTSHAPE, a.length);
    }

    @Test(dataProvider = "longUnaryOpProvider")
    static void convertShapeL2I_128_To_MAX(IntFunction<long[]> fa) {
        long[] a = fa.apply(1024);
        conversion_kernel(LongVector.SPECIES_128, IntVector.SPECIES_MAX,
                a, VectorOperators.L2I,
                ConvAPI.CONVERTSHAPE, a.length);
    }

    @Test(dataProvider = "longUnaryOpProvider")
    static void convertShapeL2L_128_To_64(IntFunction<long[]> fa) {
        long[] a = fa.apply(1024);
        conversion_kernel(LongVector.SPECIES_128, LongVector.SPECIES_64,
                a, L2L,
                ConvAPI.CONVERTSHAPE, a.length);
    }

    @Test(dataProvider = "longUnaryOpProvider")
    static void convertShapeL2L_128_To_128(IntFunction<long[]> fa) {
        long[] a = fa.apply(1024);
        conversion_kernel(LongVector.SPECIES_128, LongVector.SPECIES_128,
                a, L2L,
                ConvAPI.CONVERTSHAPE, a.length);
    }

    @Test(dataProvider = "longUnaryOpProvider")
    static void convertShapeL2L_128_To_256(IntFunction<long[]> fa) {
        long[] a = fa.apply(1024);
        conversion_kernel(LongVector.SPECIES_128, LongVector.SPECIES_256,
                a, L2L,
                ConvAPI.CONVERTSHAPE, a.length);
    }

    @Test(dataProvider = "longUnaryOpProvider")
    static void convertShapeL2L_128_To_512(IntFunction<long[]> fa) {
        long[] a = fa.apply(1024);
        conversion_kernel(LongVector.SPECIES_128, LongVector.SPECIES_512,
                a, L2L,
                ConvAPI.CONVERTSHAPE, a.length);
    }

    @Test(dataProvider = "longUnaryOpProvider")
    static void convertShapeL2L_128_To_MAX(IntFunction<long[]> fa) {
        long[] a = fa.apply(1024);
        conversion_kernel(LongVector.SPECIES_128, LongVector.SPECIES_MAX,
                a, L2L,
                ConvAPI.CONVERTSHAPE, a.length);
    }

    @Test(dataProvider = "longUnaryOpProvider")
    static void convertShapeL2F_128_To_64(IntFunction<long[]> fa) {
        long[] a = fa.apply(1024);
        conversion_kernel(LongVector.SPECIES_128, FloatVector.SPECIES_64,
                a, VectorOperators.L2F,
                ConvAPI.CONVERTSHAPE, a.length);
    }

    @Test(dataProvider = "longUnaryOpProvider")
    static void convertShapeL2F_128_To_128(IntFunction<long[]> fa) {
        long[] a = fa.apply(1024);
        conversion_kernel(LongVector.SPECIES_128, FloatVector.SPECIES_128,
                a, VectorOperators.L2F,
                ConvAPI.CONVERTSHAPE, a.length);
    }

    @Test(dataProvider = "longUnaryOpProvider")
    static void convertShapeL2F_128_To_256(IntFunction<long[]> fa) {
        long[] a = fa.apply(1024);
        conversion_kernel(LongVector.SPECIES_128, FloatVector.SPECIES_256,
                a, VectorOperators.L2F,
                ConvAPI.CONVERTSHAPE, a.length);
    }

    @Test(dataProvider = "longUnaryOpProvider")
    static void convertShapeL2F_128_To_512(IntFunction<long[]> fa) {
        long[] a = fa.apply(1024);
        conversion_kernel(LongVector.SPECIES_128, FloatVector.SPECIES_512,
                a, VectorOperators.L2F,
                ConvAPI.CONVERTSHAPE, a.length);
    }

    @Test(dataProvider = "longUnaryOpProvider")
    static void convertShapeL2F_128_To_MAX(IntFunction<long[]> fa) {
        long[] a = fa.apply(1024);
        conversion_kernel(LongVector.SPECIES_128, FloatVector.SPECIES_MAX,
                a, VectorOperators.L2F,
                ConvAPI.CONVERTSHAPE, a.length);
    }

    @Test(dataProvider = "longUnaryOpProvider")
    static void convertShapeL2D_128_To_64(IntFunction<long[]> fa) {
        long[] a = fa.apply(1024);
        conversion_kernel(LongVector.SPECIES_128, DoubleVector.SPECIES_64,
                a, VectorOperators.L2D,
                ConvAPI.CONVERTSHAPE, a.length);
    }

    @Test(dataProvider = "longUnaryOpProvider")
    static void convertShapeL2D_128_To_128(IntFunction<long[]> fa) {
        long[] a = fa.apply(1024);
        conversion_kernel(LongVector.SPECIES_128, DoubleVector.SPECIES_128,
                a, VectorOperators.L2D,
                ConvAPI.CONVERTSHAPE, a.length);
    }

    @Test(dataProvider = "longUnaryOpProvider")
    static void convertShapeL2D_128_To_256(IntFunction<long[]> fa) {
        long[] a = fa.apply(1024);
        conversion_kernel(LongVector.SPECIES_128, DoubleVector.SPECIES_256,
                a, VectorOperators.L2D,
                ConvAPI.CONVERTSHAPE, a.length);
    }

    @Test(dataProvider = "longUnaryOpProvider")
    static void convertShapeL2D_128_To_512(IntFunction<long[]> fa) {
        long[] a = fa.apply(1024);
        conversion_kernel(LongVector.SPECIES_128, DoubleVector.SPECIES_512,
                a, VectorOperators.L2D,
                ConvAPI.CONVERTSHAPE, a.length);
    }

    @Test(dataProvider = "longUnaryOpProvider")
    static void convertShapeL2D_128_To_MAX(IntFunction<long[]> fa) {
        long[] a = fa.apply(1024);
        conversion_kernel(LongVector.SPECIES_128, DoubleVector.SPECIES_MAX,
                a, VectorOperators.L2D,
                ConvAPI.CONVERTSHAPE, a.length);
    }

    @Test(dataProvider = "longUnaryOpProvider")
    static void castShapeL2B_128_To_64(IntFunction<long[]> fa) {
        long[] a = fa.apply(1024);
        conversion_kernel(LongVector.SPECIES_128, ByteVector.SPECIES_64,
                a, VectorOperators.L2B,
                ConvAPI.CASTSHAPE, a.length);
    }

    @Test(dataProvider = "longUnaryOpProvider")
    static void castShapeL2B_128_To_128(IntFunction<long[]> fa) {
        long[] a = fa.apply(1024);
        conversion_kernel(LongVector.SPECIES_128, ByteVector.SPECIES_128,
                a, VectorOperators.L2B,
                ConvAPI.CASTSHAPE, a.length);
    }

    @Test(dataProvider = "longUnaryOpProvider")
    static void castShapeL2B_128_To_256(IntFunction<long[]> fa) {
        long[] a = fa.apply(1024);
        conversion_kernel(LongVector.SPECIES_128, ByteVector.SPECIES_256,
                a, VectorOperators.L2B,
                ConvAPI.CASTSHAPE, a.length);
    }

    @Test(dataProvider = "longUnaryOpProvider")
    static void castShapeL2B_128_To_512(IntFunction<long[]> fa) {
        long[] a = fa.apply(1024);
        conversion_kernel(LongVector.SPECIES_128, ByteVector.SPECIES_512,
                a, VectorOperators.L2B,
                ConvAPI.CASTSHAPE, a.length);
    }

    @Test(dataProvider = "longUnaryOpProvider")
    static void castShapeL2B_128_To_MAX(IntFunction<long[]> fa) {
        long[] a = fa.apply(1024);
        conversion_kernel(LongVector.SPECIES_128, ByteVector.SPECIES_MAX,
                a, VectorOperators.L2B,
                ConvAPI.CASTSHAPE, a.length);
    }

    @Test(dataProvider = "longUnaryOpProvider")
    static void castShapeL2S_128_To_64(IntFunction<long[]> fa) {
        long[] a = fa.apply(1024);
        conversion_kernel(LongVector.SPECIES_128, ShortVector.SPECIES_64,
                a, VectorOperators.L2S,
                ConvAPI.CASTSHAPE, a.length);
    }

    @Test(dataProvider = "longUnaryOpProvider")
    static void castShapeL2S_128_To_128(IntFunction<long[]> fa) {
        long[] a = fa.apply(1024);
        conversion_kernel(LongVector.SPECIES_128, ShortVector.SPECIES_128,
                a, VectorOperators.L2S,
                ConvAPI.CASTSHAPE, a.length);
    }

    @Test(dataProvider = "longUnaryOpProvider")
    static void castShapeL2S_128_To_256(IntFunction<long[]> fa) {
        long[] a = fa.apply(1024);
        conversion_kernel(LongVector.SPECIES_128, ShortVector.SPECIES_256,
                a, VectorOperators.L2S,
                ConvAPI.CASTSHAPE, a.length);
    }

    @Test(dataProvider = "longUnaryOpProvider")
    static void castShapeL2S_128_To_512(IntFunction<long[]> fa) {
        long[] a = fa.apply(1024);
        conversion_kernel(LongVector.SPECIES_128, ShortVector.SPECIES_512,
                a, VectorOperators.L2S,
                ConvAPI.CASTSHAPE, a.length);
    }

    @Test(dataProvider = "longUnaryOpProvider")
    static void castShapeL2S_128_To_MAX(IntFunction<long[]> fa) {
        long[] a = fa.apply(1024);
        conversion_kernel(LongVector.SPECIES_128, ShortVector.SPECIES_MAX,
                a, VectorOperators.L2S,
                ConvAPI.CASTSHAPE, a.length);
    }

    @Test(dataProvider = "longUnaryOpProvider")
    static void castShapeL2I_128_To_64(IntFunction<long[]> fa) {
        long[] a = fa.apply(1024);
        conversion_kernel(LongVector.SPECIES_128, IntVector.SPECIES_64,
                a, VectorOperators.L2I,
                ConvAPI.CASTSHAPE, a.length);
    }

    @Test(dataProvider = "longUnaryOpProvider")
    static void castShapeL2I_128_To_128(IntFunction<long[]> fa) {
        long[] a = fa.apply(1024);
        conversion_kernel(LongVector.SPECIES_128, IntVector.SPECIES_128,
                a, VectorOperators.L2I,
                ConvAPI.CASTSHAPE, a.length);
    }

    @Test(dataProvider = "longUnaryOpProvider")
    static void castShapeL2I_128_To_256(IntFunction<long[]> fa) {
        long[] a = fa.apply(1024);
        conversion_kernel(LongVector.SPECIES_128, IntVector.SPECIES_256,
                a, VectorOperators.L2I,
                ConvAPI.CASTSHAPE, a.length);
    }

    @Test(dataProvider = "longUnaryOpProvider")
    static void castShapeL2I_128_To_512(IntFunction<long[]> fa) {
        long[] a = fa.apply(1024);
        conversion_kernel(LongVector.SPECIES_128, IntVector.SPECIES_512,
                a, VectorOperators.L2I,
                ConvAPI.CASTSHAPE, a.length);
    }

    @Test(dataProvider = "longUnaryOpProvider")
    static void castShapeL2I_128_To_MAX(IntFunction<long[]> fa) {
        long[] a = fa.apply(1024);
        conversion_kernel(LongVector.SPECIES_128, IntVector.SPECIES_MAX,
                a, VectorOperators.L2I,
                ConvAPI.CASTSHAPE, a.length);
    }

    @Test(dataProvider = "longUnaryOpProvider")
    static void castShapeL2L_128_To_64(IntFunction<long[]> fa) {
        long[] a = fa.apply(1024);
        conversion_kernel(LongVector.SPECIES_128, LongVector.SPECIES_64,
                a, L2L,
                ConvAPI.CASTSHAPE, a.length);
    }

    @Test(dataProvider = "longUnaryOpProvider")
    static void castShapeL2L_128_To_128(IntFunction<long[]> fa) {
        long[] a = fa.apply(1024);
        conversion_kernel(LongVector.SPECIES_128, LongVector.SPECIES_128,
                a, L2L,
                ConvAPI.CASTSHAPE, a.length);
    }

    @Test(dataProvider = "longUnaryOpProvider")
    static void castShapeL2L_128_To_256(IntFunction<long[]> fa) {
        long[] a = fa.apply(1024);
        conversion_kernel(LongVector.SPECIES_128, LongVector.SPECIES_256,
                a, L2L,
                ConvAPI.CASTSHAPE, a.length);
    }

    @Test(dataProvider = "longUnaryOpProvider")
    static void castShapeL2L_128_To_512(IntFunction<long[]> fa) {
        long[] a = fa.apply(1024);
        conversion_kernel(LongVector.SPECIES_128, LongVector.SPECIES_512,
                a, L2L,
                ConvAPI.CASTSHAPE, a.length);
    }

    @Test(dataProvider = "longUnaryOpProvider")
    static void castShapeL2L_128_To_MAX(IntFunction<long[]> fa) {
        long[] a = fa.apply(1024);
        conversion_kernel(LongVector.SPECIES_128, LongVector.SPECIES_MAX,
                a, L2L,
                ConvAPI.CASTSHAPE, a.length);
    }

    @Test(dataProvider = "longUnaryOpProvider")
    static void castShapeL2F_128_To_64(IntFunction<long[]> fa) {
        long[] a = fa.apply(1024);
        conversion_kernel(LongVector.SPECIES_128, FloatVector.SPECIES_64,
                a, VectorOperators.L2F,
                ConvAPI.CASTSHAPE, a.length);
    }

    @Test(dataProvider = "longUnaryOpProvider")
    static void castShapeL2F_128_To_128(IntFunction<long[]> fa) {
        long[] a = fa.apply(1024);
        conversion_kernel(LongVector.SPECIES_128, FloatVector.SPECIES_128,
                a, VectorOperators.L2F,
                ConvAPI.CASTSHAPE, a.length);
    }

    @Test(dataProvider = "longUnaryOpProvider")
    static void castShapeL2F_128_To_256(IntFunction<long[]> fa) {
        long[] a = fa.apply(1024);
        conversion_kernel(LongVector.SPECIES_128, FloatVector.SPECIES_256,
                a, VectorOperators.L2F,
                ConvAPI.CASTSHAPE, a.length);
    }

    @Test(dataProvider = "longUnaryOpProvider")
    static void castShapeL2F_128_To_512(IntFunction<long[]> fa) {
        long[] a = fa.apply(1024);
        conversion_kernel(LongVector.SPECIES_128, FloatVector.SPECIES_512,
                a, VectorOperators.L2F,
                ConvAPI.CASTSHAPE, a.length);
    }

    @Test(dataProvider = "longUnaryOpProvider")
    static void castShapeL2F_128_To_MAX(IntFunction<long[]> fa) {
        long[] a = fa.apply(1024);
        conversion_kernel(LongVector.SPECIES_128, FloatVector.SPECIES_MAX,
                a, VectorOperators.L2F,
                ConvAPI.CASTSHAPE, a.length);
    }

    @Test(dataProvider = "longUnaryOpProvider")
    static void castShapeL2D_128_To_64(IntFunction<long[]> fa) {
        long[] a = fa.apply(1024);
        conversion_kernel(LongVector.SPECIES_128, DoubleVector.SPECIES_64,
                a, VectorOperators.L2D,
                ConvAPI.CASTSHAPE, a.length);
    }

    @Test(dataProvider = "longUnaryOpProvider")
    static void castShapeL2D_128_To_128(IntFunction<long[]> fa) {
        long[] a = fa.apply(1024);
        conversion_kernel(LongVector.SPECIES_128, DoubleVector.SPECIES_128,
                a, VectorOperators.L2D,
                ConvAPI.CASTSHAPE, a.length);
    }

    @Test(dataProvider = "longUnaryOpProvider")
    static void castShapeL2D_128_To_256(IntFunction<long[]> fa) {
        long[] a = fa.apply(1024);
        conversion_kernel(LongVector.SPECIES_128, DoubleVector.SPECIES_256,
                a, VectorOperators.L2D,
                ConvAPI.CASTSHAPE, a.length);
    }

    @Test(dataProvider = "longUnaryOpProvider")
    static void castShapeL2D_128_To_512(IntFunction<long[]> fa) {
        long[] a = fa.apply(1024);
        conversion_kernel(LongVector.SPECIES_128, DoubleVector.SPECIES_512,
                a, VectorOperators.L2D,
                ConvAPI.CASTSHAPE, a.length);
    }

    @Test(dataProvider = "longUnaryOpProvider")
    static void castShapeL2D_128_To_MAX(IntFunction<long[]> fa) {
        long[] a = fa.apply(1024);
        conversion_kernel(LongVector.SPECIES_128, DoubleVector.SPECIES_MAX,
                a, VectorOperators.L2D,
                ConvAPI.CASTSHAPE, a.length);
    }

    @Test(dataProvider = "longUnaryOpProvider")
    static void reinterpretShapeL2B_128_To_64(IntFunction<long[]> fa) {
        long[] a = fa.apply(1024);
        reinterpret_kernel(LongVector.SPECIES_128, ByteVector.SPECIES_64,
                a, a.length);
    }

    @Test(dataProvider = "longUnaryOpProvider")
    static void reinterpretShapeL2B_128_To_128(IntFunction<long[]> fa) {
        long[] a = fa.apply(1024);
        reinterpret_kernel(LongVector.SPECIES_128, ByteVector.SPECIES_128,
                a, a.length);
    }

    @Test(dataProvider = "longUnaryOpProvider")
    static void reinterpretShapeL2B_128_To_256(IntFunction<long[]> fa) {
        long[] a = fa.apply(1024);
        reinterpret_kernel(LongVector.SPECIES_128, ByteVector.SPECIES_256,
                a, a.length);
    }

    @Test(dataProvider = "longUnaryOpProvider")
    static void reinterpretShapeL2B_128_To_512(IntFunction<long[]> fa) {
        long[] a = fa.apply(1024);
        reinterpret_kernel(LongVector.SPECIES_128, ByteVector.SPECIES_512,
                a, a.length);
    }

    @Test(dataProvider = "longUnaryOpProvider")
    static void reinterpretShapeL2B_128_To_MAX(IntFunction<long[]> fa) {
        long[] a = fa.apply(1024);
        reinterpret_kernel(LongVector.SPECIES_128, ByteVector.SPECIES_MAX,
                a, a.length);
    }

    @Test(dataProvider = "longUnaryOpProvider")
    static void reinterpretShapeL2S_128_To_64(IntFunction<long[]> fa) {
        long[] a = fa.apply(1024);
        reinterpret_kernel(LongVector.SPECIES_128, ShortVector.SPECIES_64,
                a, a.length);
    }

    @Test(dataProvider = "longUnaryOpProvider")
    static void reinterpretShapeL2S_128_To_128(IntFunction<long[]> fa) {
        long[] a = fa.apply(1024);
        reinterpret_kernel(LongVector.SPECIES_128, ShortVector.SPECIES_128,
                a, a.length);
    }

    @Test(dataProvider = "longUnaryOpProvider")
    static void reinterpretShapeL2S_128_To_256(IntFunction<long[]> fa) {
        long[] a = fa.apply(1024);
        reinterpret_kernel(LongVector.SPECIES_128, ShortVector.SPECIES_256,
                a, a.length);
    }

    @Test(dataProvider = "longUnaryOpProvider")
    static void reinterpretShapeL2S_128_To_512(IntFunction<long[]> fa) {
        long[] a = fa.apply(1024);
        reinterpret_kernel(LongVector.SPECIES_128, ShortVector.SPECIES_512,
                a, a.length);
    }

    @Test(dataProvider = "longUnaryOpProvider")
    static void reinterpretShapeL2S_128_To_MAX(IntFunction<long[]> fa) {
        long[] a = fa.apply(1024);
        reinterpret_kernel(LongVector.SPECIES_128, ShortVector.SPECIES_MAX,
                a, a.length);
    }

    @Test(dataProvider = "longUnaryOpProvider")
    static void reinterpretShapeL2I_128_To_64(IntFunction<long[]> fa) {
        long[] a = fa.apply(1024);
        reinterpret_kernel(LongVector.SPECIES_128, IntVector.SPECIES_64,
                a, a.length);
    }

    @Test(dataProvider = "longUnaryOpProvider")
    static void reinterpretShapeL2I_128_To_128(IntFunction<long[]> fa) {
        long[] a = fa.apply(1024);
        reinterpret_kernel(LongVector.SPECIES_128, IntVector.SPECIES_128,
                a, a.length);
    }

    @Test(dataProvider = "longUnaryOpProvider")
    static void reinterpretShapeL2I_128_To_256(IntFunction<long[]> fa) {
        long[] a = fa.apply(1024);
        reinterpret_kernel(LongVector.SPECIES_128, IntVector.SPECIES_256,
                a, a.length);
    }

    @Test(dataProvider = "longUnaryOpProvider")
    static void reinterpretShapeL2I_128_To_512(IntFunction<long[]> fa) {
        long[] a = fa.apply(1024);
        reinterpret_kernel(LongVector.SPECIES_128, IntVector.SPECIES_512,
                a, a.length);
    }

    @Test(dataProvider = "longUnaryOpProvider")
    static void reinterpretShapeL2I_128_To_MAX(IntFunction<long[]> fa) {
        long[] a = fa.apply(1024);
        reinterpret_kernel(LongVector.SPECIES_128, IntVector.SPECIES_MAX,
                a, a.length);
    }

    @Test(dataProvider = "longUnaryOpProvider")
    static void reinterpretShapeL2L_128_To_64(IntFunction<long[]> fa) {
        long[] a = fa.apply(1024);
        reinterpret_kernel(LongVector.SPECIES_128, LongVector.SPECIES_64,
                a, a.length);
    }

    @Test(dataProvider = "longUnaryOpProvider")
    static void reinterpretShapeL2L_128_To_128(IntFunction<long[]> fa) {
        long[] a = fa.apply(1024);
        reinterpret_kernel(LongVector.SPECIES_128, LongVector.SPECIES_128,
                a, a.length);
    }

    @Test(dataProvider = "longUnaryOpProvider")
    static void reinterpretShapeL2L_128_To_256(IntFunction<long[]> fa) {
        long[] a = fa.apply(1024);
        reinterpret_kernel(LongVector.SPECIES_128, LongVector.SPECIES_256,
                a, a.length);
    }

    @Test(dataProvider = "longUnaryOpProvider")
    static void reinterpretShapeL2L_128_To_512(IntFunction<long[]> fa) {
        long[] a = fa.apply(1024);
        reinterpret_kernel(LongVector.SPECIES_128, LongVector.SPECIES_512,
                a, a.length);
    }

    @Test(dataProvider = "longUnaryOpProvider")
    static void reinterpretShapeL2L_128_To_MAX(IntFunction<long[]> fa) {
        long[] a = fa.apply(1024);
        reinterpret_kernel(LongVector.SPECIES_128, LongVector.SPECIES_MAX,
                a, a.length);
    }

    @Test(dataProvider = "longUnaryOpProvider")
    static void reinterpretShapeL2F_128_To_64(IntFunction<long[]> fa) {
        long[] a = fa.apply(1024);
        reinterpret_kernel(LongVector.SPECIES_128, FloatVector.SPECIES_64,
                a, a.length);
    }

    @Test(dataProvider = "longUnaryOpProvider")
    static void reinterpretShapeL2F_128_To_128(IntFunction<long[]> fa) {
        long[] a = fa.apply(1024);
        reinterpret_kernel(LongVector.SPECIES_128, FloatVector.SPECIES_128,
                a, a.length);
    }

    @Test(dataProvider = "longUnaryOpProvider")
    static void reinterpretShapeL2F_128_To_256(IntFunction<long[]> fa) {
        long[] a = fa.apply(1024);
        reinterpret_kernel(LongVector.SPECIES_128, FloatVector.SPECIES_256,
                a, a.length);
    }

    @Test(dataProvider = "longUnaryOpProvider")
    static void reinterpretShapeL2F_128_To_512(IntFunction<long[]> fa) {
        long[] a = fa.apply(1024);
        reinterpret_kernel(LongVector.SPECIES_128, FloatVector.SPECIES_512,
                a, a.length);
    }

    @Test(dataProvider = "longUnaryOpProvider")
    static void reinterpretShapeL2F_128_To_MAX(IntFunction<long[]> fa) {
        long[] a = fa.apply(1024);
        reinterpret_kernel(LongVector.SPECIES_128, FloatVector.SPECIES_MAX,
                a, a.length);
    }

    @Test(dataProvider = "longUnaryOpProvider")
    static void reinterpretShapeL2D_128_To_64(IntFunction<long[]> fa) {
        long[] a = fa.apply(1024);
        reinterpret_kernel(LongVector.SPECIES_128, DoubleVector.SPECIES_64,
                a, a.length);
    }

    @Test(dataProvider = "longUnaryOpProvider")
    static void reinterpretShapeL2D_128_To_128(IntFunction<long[]> fa) {
        long[] a = fa.apply(1024);
        reinterpret_kernel(LongVector.SPECIES_128, DoubleVector.SPECIES_128,
                a, a.length);
    }

    @Test(dataProvider = "longUnaryOpProvider")
    static void reinterpretShapeL2D_128_To_256(IntFunction<long[]> fa) {
        long[] a = fa.apply(1024);
        reinterpret_kernel(LongVector.SPECIES_128, DoubleVector.SPECIES_256,
                a, a.length);
    }

    @Test(dataProvider = "longUnaryOpProvider")
    static void reinterpretShapeL2D_128_To_512(IntFunction<long[]> fa) {
        long[] a = fa.apply(1024);
        reinterpret_kernel(LongVector.SPECIES_128, DoubleVector.SPECIES_512,
                a, a.length);
    }

    @Test(dataProvider = "longUnaryOpProvider")
    static void reinterpretShapeL2D_128_To_MAX(IntFunction<long[]> fa) {
        long[] a = fa.apply(1024);
        reinterpret_kernel(LongVector.SPECIES_128, DoubleVector.SPECIES_MAX,
                a, a.length);
    }


    @Test(dataProvider = "shortUnaryOpProvider")
    static void convertS2B_128_To_128(IntFunction<short[]> fa) {
        short[] a = fa.apply(1024);
        conversion_kernel(ShortVector.SPECIES_128, ByteVector.SPECIES_128,
                a, VectorOperators.S2B,
                ConvAPI.CONVERT, a.length);
    }

    @Test(dataProvider = "shortUnaryOpProvider")
    static void convertS2S_128_To_128(IntFunction<short[]> fa) {
        short[] a = fa.apply(1024);
        conversion_kernel(ShortVector.SPECIES_128, ShortVector.SPECIES_128,
                a, S2S,
                ConvAPI.CONVERT, a.length);
    }

    @Test(dataProvider = "shortUnaryOpProvider")
    static void convertS2I_128_To_128(IntFunction<short[]> fa) {
        short[] a = fa.apply(1024);
        conversion_kernel(ShortVector.SPECIES_128, IntVector.SPECIES_128,
                a, VectorOperators.S2I,
                ConvAPI.CONVERT, a.length);
    }

    @Test(dataProvider = "shortUnaryOpProvider")
    static void convertS2L_128_To_128(IntFunction<short[]> fa) {
        short[] a = fa.apply(1024);
        conversion_kernel(ShortVector.SPECIES_128, LongVector.SPECIES_128,
                a, VectorOperators.S2L,
                ConvAPI.CONVERT, a.length);
    }

    @Test(dataProvider = "shortUnaryOpProvider")
    static void convertS2F_128_To_128(IntFunction<short[]> fa) {
        short[] a = fa.apply(1024);
        conversion_kernel(ShortVector.SPECIES_128, FloatVector.SPECIES_128,
                a, VectorOperators.S2F,
                ConvAPI.CONVERT, a.length);
    }

    @Test(dataProvider = "shortUnaryOpProvider")
    static void convertS2D_128_To_128(IntFunction<short[]> fa) {
        short[] a = fa.apply(1024);
        conversion_kernel(ShortVector.SPECIES_128, DoubleVector.SPECIES_128,
                a, VectorOperators.S2D,
                ConvAPI.CONVERT, a.length);
    }

    @Test(dataProvider = "shortUnaryOpProvider")
    static void convertShapeS2B_128_To_64(IntFunction<short[]> fa) {
        short[] a = fa.apply(1024);
        conversion_kernel(ShortVector.SPECIES_128, ByteVector.SPECIES_64,
                a, VectorOperators.S2B,
                ConvAPI.CONVERTSHAPE, a.length);
    }

    @Test(dataProvider = "shortUnaryOpProvider")
    static void convertShapeS2B_128_To_128(IntFunction<short[]> fa) {
        short[] a = fa.apply(1024);
        conversion_kernel(ShortVector.SPECIES_128, ByteVector.SPECIES_128,
                a, VectorOperators.S2B,
                ConvAPI.CONVERTSHAPE, a.length);
    }

    @Test(dataProvider = "shortUnaryOpProvider")
    static void convertShapeS2B_128_To_256(IntFunction<short[]> fa) {
        short[] a = fa.apply(1024);
        conversion_kernel(ShortVector.SPECIES_128, ByteVector.SPECIES_256,
                a, VectorOperators.S2B,
                ConvAPI.CONVERTSHAPE, a.length);
    }

    @Test(dataProvider = "shortUnaryOpProvider")
    static void convertShapeS2B_128_To_512(IntFunction<short[]> fa) {
        short[] a = fa.apply(1024);
        conversion_kernel(ShortVector.SPECIES_128, ByteVector.SPECIES_512,
                a, VectorOperators.S2B,
                ConvAPI.CONVERTSHAPE, a.length);
    }

    @Test(dataProvider = "shortUnaryOpProvider")
    static void convertShapeS2B_128_To_MAX(IntFunction<short[]> fa) {
        short[] a = fa.apply(1024);
        conversion_kernel(ShortVector.SPECIES_128, ByteVector.SPECIES_MAX,
                a, VectorOperators.S2B,
                ConvAPI.CONVERTSHAPE, a.length);
    }

    @Test(dataProvider = "shortUnaryOpProvider")
    static void convertShapeS2S_128_To_64(IntFunction<short[]> fa) {
        short[] a = fa.apply(1024);
        conversion_kernel(ShortVector.SPECIES_128, ShortVector.SPECIES_64,
                a, S2S,
                ConvAPI.CONVERTSHAPE, a.length);
    }

    @Test(dataProvider = "shortUnaryOpProvider")
    static void convertShapeS2S_128_To_128(IntFunction<short[]> fa) {
        short[] a = fa.apply(1024);
        conversion_kernel(ShortVector.SPECIES_128, ShortVector.SPECIES_128,
                a, S2S,
                ConvAPI.CONVERTSHAPE, a.length);
    }

    @Test(dataProvider = "shortUnaryOpProvider")
    static void convertShapeS2S_128_To_256(IntFunction<short[]> fa) {
        short[] a = fa.apply(1024);
        conversion_kernel(ShortVector.SPECIES_128, ShortVector.SPECIES_256,
                a, S2S,
                ConvAPI.CONVERTSHAPE, a.length);
    }

    @Test(dataProvider = "shortUnaryOpProvider")
    static void convertShapeS2S_128_To_512(IntFunction<short[]> fa) {
        short[] a = fa.apply(1024);
        conversion_kernel(ShortVector.SPECIES_128, ShortVector.SPECIES_512,
                a, S2S,
                ConvAPI.CONVERTSHAPE, a.length);
    }

    @Test(dataProvider = "shortUnaryOpProvider")
    static void convertShapeS2S_128_To_MAX(IntFunction<short[]> fa) {
        short[] a = fa.apply(1024);
        conversion_kernel(ShortVector.SPECIES_128, ShortVector.SPECIES_MAX,
                a, S2S,
                ConvAPI.CONVERTSHAPE, a.length);
    }

    @Test(dataProvider = "shortUnaryOpProvider")
    static void convertShapeS2I_128_To_64(IntFunction<short[]> fa) {
        short[] a = fa.apply(1024);
        conversion_kernel(ShortVector.SPECIES_128, IntVector.SPECIES_64,
                a, VectorOperators.S2I,
                ConvAPI.CONVERTSHAPE, a.length);
    }

    @Test(dataProvider = "shortUnaryOpProvider")
    static void convertShapeS2I_128_To_128(IntFunction<short[]> fa) {
        short[] a = fa.apply(1024);
        conversion_kernel(ShortVector.SPECIES_128, IntVector.SPECIES_128,
                a, VectorOperators.S2I,
                ConvAPI.CONVERTSHAPE, a.length);
    }

    @Test(dataProvider = "shortUnaryOpProvider")
    static void convertShapeS2I_128_To_256(IntFunction<short[]> fa) {
        short[] a = fa.apply(1024);
        conversion_kernel(ShortVector.SPECIES_128, IntVector.SPECIES_256,
                a, VectorOperators.S2I,
                ConvAPI.CONVERTSHAPE, a.length);
    }

    @Test(dataProvider = "shortUnaryOpProvider")
    static void convertShapeS2I_128_To_512(IntFunction<short[]> fa) {
        short[] a = fa.apply(1024);
        conversion_kernel(ShortVector.SPECIES_128, IntVector.SPECIES_512,
                a, VectorOperators.S2I,
                ConvAPI.CONVERTSHAPE, a.length);
    }

    @Test(dataProvider = "shortUnaryOpProvider")
    static void convertShapeS2I_128_To_MAX(IntFunction<short[]> fa) {
        short[] a = fa.apply(1024);
        conversion_kernel(ShortVector.SPECIES_128, IntVector.SPECIES_MAX,
                a, VectorOperators.S2I,
                ConvAPI.CONVERTSHAPE, a.length);
    }

    @Test(dataProvider = "shortUnaryOpProvider")
    static void convertShapeS2L_128_To_64(IntFunction<short[]> fa) {
        short[] a = fa.apply(1024);
        conversion_kernel(ShortVector.SPECIES_128, LongVector.SPECIES_64,
                a, VectorOperators.S2L,
                ConvAPI.CONVERTSHAPE, a.length);
    }

    @Test(dataProvider = "shortUnaryOpProvider")
    static void convertShapeS2L_128_To_128(IntFunction<short[]> fa) {
        short[] a = fa.apply(1024);
        conversion_kernel(ShortVector.SPECIES_128, LongVector.SPECIES_128,
                a, VectorOperators.S2L,
                ConvAPI.CONVERTSHAPE, a.length);
    }

    @Test(dataProvider = "shortUnaryOpProvider")
    static void convertShapeS2L_128_To_256(IntFunction<short[]> fa) {
        short[] a = fa.apply(1024);
        conversion_kernel(ShortVector.SPECIES_128, LongVector.SPECIES_256,
                a, VectorOperators.S2L,
                ConvAPI.CONVERTSHAPE, a.length);
    }

    @Test(dataProvider = "shortUnaryOpProvider")
    static void convertShapeS2L_128_To_512(IntFunction<short[]> fa) {
        short[] a = fa.apply(1024);
        conversion_kernel(ShortVector.SPECIES_128, LongVector.SPECIES_512,
                a, VectorOperators.S2L,
                ConvAPI.CONVERTSHAPE, a.length);
    }

    @Test(dataProvider = "shortUnaryOpProvider")
    static void convertShapeS2L_128_To_MAX(IntFunction<short[]> fa) {
        short[] a = fa.apply(1024);
        conversion_kernel(ShortVector.SPECIES_128, LongVector.SPECIES_MAX,
                a, VectorOperators.S2L,
                ConvAPI.CONVERTSHAPE, a.length);
    }

    @Test(dataProvider = "shortUnaryOpProvider")
    static void convertShapeS2F_128_To_64(IntFunction<short[]> fa) {
        short[] a = fa.apply(1024);
        conversion_kernel(ShortVector.SPECIES_128, FloatVector.SPECIES_64,
                a, VectorOperators.S2F,
                ConvAPI.CONVERTSHAPE, a.length);
    }

    @Test(dataProvider = "shortUnaryOpProvider")
    static void convertShapeS2F_128_To_128(IntFunction<short[]> fa) {
        short[] a = fa.apply(1024);
        conversion_kernel(ShortVector.SPECIES_128, FloatVector.SPECIES_128,
                a, VectorOperators.S2F,
                ConvAPI.CONVERTSHAPE, a.length);
    }

    @Test(dataProvider = "shortUnaryOpProvider")
    static void convertShapeS2F_128_To_256(IntFunction<short[]> fa) {
        short[] a = fa.apply(1024);
        conversion_kernel(ShortVector.SPECIES_128, FloatVector.SPECIES_256,
                a, VectorOperators.S2F,
                ConvAPI.CONVERTSHAPE, a.length);
    }

    @Test(dataProvider = "shortUnaryOpProvider")
    static void convertShapeS2F_128_To_512(IntFunction<short[]> fa) {
        short[] a = fa.apply(1024);
        conversion_kernel(ShortVector.SPECIES_128, FloatVector.SPECIES_512,
                a, VectorOperators.S2F,
                ConvAPI.CONVERTSHAPE, a.length);
    }

    @Test(dataProvider = "shortUnaryOpProvider")
    static void convertShapeS2F_128_To_MAX(IntFunction<short[]> fa) {
        short[] a = fa.apply(1024);
        conversion_kernel(ShortVector.SPECIES_128, FloatVector.SPECIES_MAX,
                a, VectorOperators.S2F,
                ConvAPI.CONVERTSHAPE, a.length);
    }

    @Test(dataProvider = "shortUnaryOpProvider")
    static void convertShapeS2D_128_To_64(IntFunction<short[]> fa) {
        short[] a = fa.apply(1024);
        conversion_kernel(ShortVector.SPECIES_128, DoubleVector.SPECIES_64,
                a, VectorOperators.S2D,
                ConvAPI.CONVERTSHAPE, a.length);
    }

    @Test(dataProvider = "shortUnaryOpProvider")
    static void convertShapeS2D_128_To_128(IntFunction<short[]> fa) {
        short[] a = fa.apply(1024);
        conversion_kernel(ShortVector.SPECIES_128, DoubleVector.SPECIES_128,
                a, VectorOperators.S2D,
                ConvAPI.CONVERTSHAPE, a.length);
    }

    @Test(dataProvider = "shortUnaryOpProvider")
    static void convertShapeS2D_128_To_256(IntFunction<short[]> fa) {
        short[] a = fa.apply(1024);
        conversion_kernel(ShortVector.SPECIES_128, DoubleVector.SPECIES_256,
                a, VectorOperators.S2D,
                ConvAPI.CONVERTSHAPE, a.length);
    }

    @Test(dataProvider = "shortUnaryOpProvider")
    static void convertShapeS2D_128_To_512(IntFunction<short[]> fa) {
        short[] a = fa.apply(1024);
        conversion_kernel(ShortVector.SPECIES_128, DoubleVector.SPECIES_512,
                a, VectorOperators.S2D,
                ConvAPI.CONVERTSHAPE, a.length);
    }

    @Test(dataProvider = "shortUnaryOpProvider")
    static void convertShapeS2D_128_To_MAX(IntFunction<short[]> fa) {
        short[] a = fa.apply(1024);
        conversion_kernel(ShortVector.SPECIES_128, DoubleVector.SPECIES_MAX,
                a, VectorOperators.S2D,
                ConvAPI.CONVERTSHAPE, a.length);
    }

    @Test(dataProvider = "shortUnaryOpProvider")
    static void castShapeS2B_128_To_64(IntFunction<short[]> fa) {
        short[] a = fa.apply(1024);
        conversion_kernel(ShortVector.SPECIES_128, ByteVector.SPECIES_64,
                a, VectorOperators.S2B,
                ConvAPI.CASTSHAPE, a.length);
    }

    @Test(dataProvider = "shortUnaryOpProvider")
    static void castShapeS2B_128_To_128(IntFunction<short[]> fa) {
        short[] a = fa.apply(1024);
        conversion_kernel(ShortVector.SPECIES_128, ByteVector.SPECIES_128,
                a, VectorOperators.S2B,
                ConvAPI.CASTSHAPE, a.length);
    }

    @Test(dataProvider = "shortUnaryOpProvider")
    static void castShapeS2B_128_To_256(IntFunction<short[]> fa) {
        short[] a = fa.apply(1024);
        conversion_kernel(ShortVector.SPECIES_128, ByteVector.SPECIES_256,
                a, VectorOperators.S2B,
                ConvAPI.CASTSHAPE, a.length);
    }

    @Test(dataProvider = "shortUnaryOpProvider")
    static void castShapeS2B_128_To_512(IntFunction<short[]> fa) {
        short[] a = fa.apply(1024);
        conversion_kernel(ShortVector.SPECIES_128, ByteVector.SPECIES_512,
                a, VectorOperators.S2B,
                ConvAPI.CASTSHAPE, a.length);
    }

    @Test(dataProvider = "shortUnaryOpProvider")
    static void castShapeS2B_128_To_MAX(IntFunction<short[]> fa) {
        short[] a = fa.apply(1024);
        conversion_kernel(ShortVector.SPECIES_128, ByteVector.SPECIES_MAX,
                a, VectorOperators.S2B,
                ConvAPI.CASTSHAPE, a.length);
    }

    @Test(dataProvider = "shortUnaryOpProvider")
    static void castShapeS2S_128_To_64(IntFunction<short[]> fa) {
        short[] a = fa.apply(1024);
        conversion_kernel(ShortVector.SPECIES_128, ShortVector.SPECIES_64,
                a, S2S,
                ConvAPI.CASTSHAPE, a.length);
    }

    @Test(dataProvider = "shortUnaryOpProvider")
    static void castShapeS2S_128_To_128(IntFunction<short[]> fa) {
        short[] a = fa.apply(1024);
        conversion_kernel(ShortVector.SPECIES_128, ShortVector.SPECIES_128,
                a, S2S,
                ConvAPI.CASTSHAPE, a.length);
    }

    @Test(dataProvider = "shortUnaryOpProvider")
    static void castShapeS2S_128_To_256(IntFunction<short[]> fa) {
        short[] a = fa.apply(1024);
        conversion_kernel(ShortVector.SPECIES_128, ShortVector.SPECIES_256,
                a, S2S,
                ConvAPI.CASTSHAPE, a.length);
    }

    @Test(dataProvider = "shortUnaryOpProvider")
    static void castShapeS2S_128_To_512(IntFunction<short[]> fa) {
        short[] a = fa.apply(1024);
        conversion_kernel(ShortVector.SPECIES_128, ShortVector.SPECIES_512,
                a, S2S,
                ConvAPI.CASTSHAPE, a.length);
    }

    @Test(dataProvider = "shortUnaryOpProvider")
    static void castShapeS2S_128_To_MAX(IntFunction<short[]> fa) {
        short[] a = fa.apply(1024);
        conversion_kernel(ShortVector.SPECIES_128, ShortVector.SPECIES_MAX,
                a, S2S,
                ConvAPI.CASTSHAPE, a.length);
    }

    @Test(dataProvider = "shortUnaryOpProvider")
    static void castShapeS2I_128_To_64(IntFunction<short[]> fa) {
        short[] a = fa.apply(1024);
        conversion_kernel(ShortVector.SPECIES_128, IntVector.SPECIES_64,
                a, VectorOperators.S2I,
                ConvAPI.CASTSHAPE, a.length);
    }

    @Test(dataProvider = "shortUnaryOpProvider")
    static void castShapeS2I_128_To_128(IntFunction<short[]> fa) {
        short[] a = fa.apply(1024);
        conversion_kernel(ShortVector.SPECIES_128, IntVector.SPECIES_128,
                a, VectorOperators.S2I,
                ConvAPI.CASTSHAPE, a.length);
    }

    @Test(dataProvider = "shortUnaryOpProvider")
    static void castShapeS2I_128_To_256(IntFunction<short[]> fa) {
        short[] a = fa.apply(1024);
        conversion_kernel(ShortVector.SPECIES_128, IntVector.SPECIES_256,
                a, VectorOperators.S2I,
                ConvAPI.CASTSHAPE, a.length);
    }

    @Test(dataProvider = "shortUnaryOpProvider")
    static void castShapeS2I_128_To_512(IntFunction<short[]> fa) {
        short[] a = fa.apply(1024);
        conversion_kernel(ShortVector.SPECIES_128, IntVector.SPECIES_512,
                a, VectorOperators.S2I,
                ConvAPI.CASTSHAPE, a.length);
    }

    @Test(dataProvider = "shortUnaryOpProvider")
    static void castShapeS2I_128_To_MAX(IntFunction<short[]> fa) {
        short[] a = fa.apply(1024);
        conversion_kernel(ShortVector.SPECIES_128, IntVector.SPECIES_MAX,
                a, VectorOperators.S2I,
                ConvAPI.CASTSHAPE, a.length);
    }

    @Test(dataProvider = "shortUnaryOpProvider")
    static void castShapeS2L_128_To_64(IntFunction<short[]> fa) {
        short[] a = fa.apply(1024);
        conversion_kernel(ShortVector.SPECIES_128, LongVector.SPECIES_64,
                a, VectorOperators.S2L,
                ConvAPI.CASTSHAPE, a.length);
    }

    @Test(dataProvider = "shortUnaryOpProvider")
    static void castShapeS2L_128_To_128(IntFunction<short[]> fa) {
        short[] a = fa.apply(1024);
        conversion_kernel(ShortVector.SPECIES_128, LongVector.SPECIES_128,
                a, VectorOperators.S2L,
                ConvAPI.CASTSHAPE, a.length);
    }

    @Test(dataProvider = "shortUnaryOpProvider")
    static void castShapeS2L_128_To_256(IntFunction<short[]> fa) {
        short[] a = fa.apply(1024);
        conversion_kernel(ShortVector.SPECIES_128, LongVector.SPECIES_256,
                a, VectorOperators.S2L,
                ConvAPI.CASTSHAPE, a.length);
    }

    @Test(dataProvider = "shortUnaryOpProvider")
    static void castShapeS2L_128_To_512(IntFunction<short[]> fa) {
        short[] a = fa.apply(1024);
        conversion_kernel(ShortVector.SPECIES_128, LongVector.SPECIES_512,
                a, VectorOperators.S2L,
                ConvAPI.CASTSHAPE, a.length);
    }

    @Test(dataProvider = "shortUnaryOpProvider")
    static void castShapeS2L_128_To_MAX(IntFunction<short[]> fa) {
        short[] a = fa.apply(1024);
        conversion_kernel(ShortVector.SPECIES_128, LongVector.SPECIES_MAX,
                a, VectorOperators.S2L,
                ConvAPI.CASTSHAPE, a.length);
    }

    @Test(dataProvider = "shortUnaryOpProvider")
    static void castShapeS2F_128_To_64(IntFunction<short[]> fa) {
        short[] a = fa.apply(1024);
        conversion_kernel(ShortVector.SPECIES_128, FloatVector.SPECIES_64,
                a, VectorOperators.S2F,
                ConvAPI.CASTSHAPE, a.length);
    }

    @Test(dataProvider = "shortUnaryOpProvider")
    static void castShapeS2F_128_To_128(IntFunction<short[]> fa) {
        short[] a = fa.apply(1024);
        conversion_kernel(ShortVector.SPECIES_128, FloatVector.SPECIES_128,
                a, VectorOperators.S2F,
                ConvAPI.CASTSHAPE, a.length);
    }

    @Test(dataProvider = "shortUnaryOpProvider")
    static void castShapeS2F_128_To_256(IntFunction<short[]> fa) {
        short[] a = fa.apply(1024);
        conversion_kernel(ShortVector.SPECIES_128, FloatVector.SPECIES_256,
                a, VectorOperators.S2F,
                ConvAPI.CASTSHAPE, a.length);
    }

    @Test(dataProvider = "shortUnaryOpProvider")
    static void castShapeS2F_128_To_512(IntFunction<short[]> fa) {
        short[] a = fa.apply(1024);
        conversion_kernel(ShortVector.SPECIES_128, FloatVector.SPECIES_512,
                a, VectorOperators.S2F,
                ConvAPI.CASTSHAPE, a.length);
    }

    @Test(dataProvider = "shortUnaryOpProvider")
    static void castShapeS2F_128_To_MAX(IntFunction<short[]> fa) {
        short[] a = fa.apply(1024);
        conversion_kernel(ShortVector.SPECIES_128, FloatVector.SPECIES_MAX,
                a, VectorOperators.S2F,
                ConvAPI.CASTSHAPE, a.length);
    }

    @Test(dataProvider = "shortUnaryOpProvider")
    static void castShapeS2D_128_To_64(IntFunction<short[]> fa) {
        short[] a = fa.apply(1024);
        conversion_kernel(ShortVector.SPECIES_128, DoubleVector.SPECIES_64,
                a, VectorOperators.S2D,
                ConvAPI.CASTSHAPE, a.length);
    }

    @Test(dataProvider = "shortUnaryOpProvider")
    static void castShapeS2D_128_To_128(IntFunction<short[]> fa) {
        short[] a = fa.apply(1024);
        conversion_kernel(ShortVector.SPECIES_128, DoubleVector.SPECIES_128,
                a, VectorOperators.S2D,
                ConvAPI.CASTSHAPE, a.length);
    }

    @Test(dataProvider = "shortUnaryOpProvider")
    static void castShapeS2D_128_To_256(IntFunction<short[]> fa) {
        short[] a = fa.apply(1024);
        conversion_kernel(ShortVector.SPECIES_128, DoubleVector.SPECIES_256,
                a, VectorOperators.S2D,
                ConvAPI.CASTSHAPE, a.length);
    }

    @Test(dataProvider = "shortUnaryOpProvider")
    static void castShapeS2D_128_To_512(IntFunction<short[]> fa) {
        short[] a = fa.apply(1024);
        conversion_kernel(ShortVector.SPECIES_128, DoubleVector.SPECIES_512,
                a, VectorOperators.S2D,
                ConvAPI.CASTSHAPE, a.length);
    }

    @Test(dataProvider = "shortUnaryOpProvider")
    static void castShapeS2D_128_To_MAX(IntFunction<short[]> fa) {
        short[] a = fa.apply(1024);
        conversion_kernel(ShortVector.SPECIES_128, DoubleVector.SPECIES_MAX,
                a, VectorOperators.S2D,
                ConvAPI.CASTSHAPE, a.length);
    }

    @Test(dataProvider = "shortUnaryOpProvider")
    static void reinterpretShapeS2B_128_To_64(IntFunction<short[]> fa) {
        short[] a = fa.apply(1024);
        reinterpret_kernel(ShortVector.SPECIES_128, ByteVector.SPECIES_64,
                a, a.length);
    }

    @Test(dataProvider = "shortUnaryOpProvider")
    static void reinterpretShapeS2B_128_To_128(IntFunction<short[]> fa) {
        short[] a = fa.apply(1024);
        reinterpret_kernel(ShortVector.SPECIES_128, ByteVector.SPECIES_128,
                a, a.length);
    }

    @Test(dataProvider = "shortUnaryOpProvider")
    static void reinterpretShapeS2B_128_To_256(IntFunction<short[]> fa) {
        short[] a = fa.apply(1024);
        reinterpret_kernel(ShortVector.SPECIES_128, ByteVector.SPECIES_256,
                a, a.length);
    }

    @Test(dataProvider = "shortUnaryOpProvider")
    static void reinterpretShapeS2B_128_To_512(IntFunction<short[]> fa) {
        short[] a = fa.apply(1024);
        reinterpret_kernel(ShortVector.SPECIES_128, ByteVector.SPECIES_512,
                a, a.length);
    }

    @Test(dataProvider = "shortUnaryOpProvider")
    static void reinterpretShapeS2B_128_To_MAX(IntFunction<short[]> fa) {
        short[] a = fa.apply(1024);
        reinterpret_kernel(ShortVector.SPECIES_128, ByteVector.SPECIES_MAX,
                a, a.length);
    }

    @Test(dataProvider = "shortUnaryOpProvider")
    static void reinterpretShapeS2S_128_To_64(IntFunction<short[]> fa) {
        short[] a = fa.apply(1024);
        reinterpret_kernel(ShortVector.SPECIES_128, ShortVector.SPECIES_64,
                a, a.length);
    }

    @Test(dataProvider = "shortUnaryOpProvider")
    static void reinterpretShapeS2S_128_To_128(IntFunction<short[]> fa) {
        short[] a = fa.apply(1024);
        reinterpret_kernel(ShortVector.SPECIES_128, ShortVector.SPECIES_128,
                a, a.length);
    }

    @Test(dataProvider = "shortUnaryOpProvider")
    static void reinterpretShapeS2S_128_To_256(IntFunction<short[]> fa) {
        short[] a = fa.apply(1024);
        reinterpret_kernel(ShortVector.SPECIES_128, ShortVector.SPECIES_256,
                a, a.length);
    }

    @Test(dataProvider = "shortUnaryOpProvider")
    static void reinterpretShapeS2S_128_To_512(IntFunction<short[]> fa) {
        short[] a = fa.apply(1024);
        reinterpret_kernel(ShortVector.SPECIES_128, ShortVector.SPECIES_512,
                a, a.length);
    }

    @Test(dataProvider = "shortUnaryOpProvider")
    static void reinterpretShapeS2S_128_To_MAX(IntFunction<short[]> fa) {
        short[] a = fa.apply(1024);
        reinterpret_kernel(ShortVector.SPECIES_128, ShortVector.SPECIES_MAX,
                a, a.length);
    }

    @Test(dataProvider = "shortUnaryOpProvider")
    static void reinterpretShapeS2I_128_To_64(IntFunction<short[]> fa) {
        short[] a = fa.apply(1024);
        reinterpret_kernel(ShortVector.SPECIES_128, IntVector.SPECIES_64,
                a, a.length);
    }

    @Test(dataProvider = "shortUnaryOpProvider")
    static void reinterpretShapeS2I_128_To_128(IntFunction<short[]> fa) {
        short[] a = fa.apply(1024);
        reinterpret_kernel(ShortVector.SPECIES_128, IntVector.SPECIES_128,
                a, a.length);
    }

    @Test(dataProvider = "shortUnaryOpProvider")
    static void reinterpretShapeS2I_128_To_256(IntFunction<short[]> fa) {
        short[] a = fa.apply(1024);
        reinterpret_kernel(ShortVector.SPECIES_128, IntVector.SPECIES_256,
                a, a.length);
    }

    @Test(dataProvider = "shortUnaryOpProvider")
    static void reinterpretShapeS2I_128_To_512(IntFunction<short[]> fa) {
        short[] a = fa.apply(1024);
        reinterpret_kernel(ShortVector.SPECIES_128, IntVector.SPECIES_512,
                a, a.length);
    }

    @Test(dataProvider = "shortUnaryOpProvider")
    static void reinterpretShapeS2I_128_To_MAX(IntFunction<short[]> fa) {
        short[] a = fa.apply(1024);
        reinterpret_kernel(ShortVector.SPECIES_128, IntVector.SPECIES_MAX,
                a, a.length);
    }

    @Test(dataProvider = "shortUnaryOpProvider")
    static void reinterpretShapeS2L_128_To_64(IntFunction<short[]> fa) {
        short[] a = fa.apply(1024);
        reinterpret_kernel(ShortVector.SPECIES_128, LongVector.SPECIES_64,
                a, a.length);
    }

    @Test(dataProvider = "shortUnaryOpProvider")
    static void reinterpretShapeS2L_128_To_128(IntFunction<short[]> fa) {
        short[] a = fa.apply(1024);
        reinterpret_kernel(ShortVector.SPECIES_128, LongVector.SPECIES_128,
                a, a.length);
    }

    @Test(dataProvider = "shortUnaryOpProvider")
    static void reinterpretShapeS2L_128_To_256(IntFunction<short[]> fa) {
        short[] a = fa.apply(1024);
        reinterpret_kernel(ShortVector.SPECIES_128, LongVector.SPECIES_256,
                a, a.length);
    }

    @Test(dataProvider = "shortUnaryOpProvider")
    static void reinterpretShapeS2L_128_To_512(IntFunction<short[]> fa) {
        short[] a = fa.apply(1024);
        reinterpret_kernel(ShortVector.SPECIES_128, LongVector.SPECIES_512,
                a, a.length);
    }

    @Test(dataProvider = "shortUnaryOpProvider")
    static void reinterpretShapeS2L_128_To_MAX(IntFunction<short[]> fa) {
        short[] a = fa.apply(1024);
        reinterpret_kernel(ShortVector.SPECIES_128, LongVector.SPECIES_MAX,
                a, a.length);
    }

    @Test(dataProvider = "shortUnaryOpProvider")
    static void reinterpretShapeS2F_128_To_64(IntFunction<short[]> fa) {
        short[] a = fa.apply(1024);
        reinterpret_kernel(ShortVector.SPECIES_128, FloatVector.SPECIES_64,
                a, a.length);
    }

    @Test(dataProvider = "shortUnaryOpProvider")
    static void reinterpretShapeS2F_128_To_128(IntFunction<short[]> fa) {
        short[] a = fa.apply(1024);
        reinterpret_kernel(ShortVector.SPECIES_128, FloatVector.SPECIES_128,
                a, a.length);
    }

    @Test(dataProvider = "shortUnaryOpProvider")
    static void reinterpretShapeS2F_128_To_256(IntFunction<short[]> fa) {
        short[] a = fa.apply(1024);
        reinterpret_kernel(ShortVector.SPECIES_128, FloatVector.SPECIES_256,
                a, a.length);
    }

    @Test(dataProvider = "shortUnaryOpProvider")
    static void reinterpretShapeS2F_128_To_512(IntFunction<short[]> fa) {
        short[] a = fa.apply(1024);
        reinterpret_kernel(ShortVector.SPECIES_128, FloatVector.SPECIES_512,
                a, a.length);
    }

    @Test(dataProvider = "shortUnaryOpProvider")
    static void reinterpretShapeS2F_128_To_MAX(IntFunction<short[]> fa) {
        short[] a = fa.apply(1024);
        reinterpret_kernel(ShortVector.SPECIES_128, FloatVector.SPECIES_MAX,
                a, a.length);
    }

    @Test(dataProvider = "shortUnaryOpProvider")
    static void reinterpretShapeS2D_128_To_64(IntFunction<short[]> fa) {
        short[] a = fa.apply(1024);
        reinterpret_kernel(ShortVector.SPECIES_128, DoubleVector.SPECIES_64,
                a, a.length);
    }

    @Test(dataProvider = "shortUnaryOpProvider")
    static void reinterpretShapeS2D_128_To_128(IntFunction<short[]> fa) {
        short[] a = fa.apply(1024);
        reinterpret_kernel(ShortVector.SPECIES_128, DoubleVector.SPECIES_128,
                a, a.length);
    }

    @Test(dataProvider = "shortUnaryOpProvider")
    static void reinterpretShapeS2D_128_To_256(IntFunction<short[]> fa) {
        short[] a = fa.apply(1024);
        reinterpret_kernel(ShortVector.SPECIES_128, DoubleVector.SPECIES_256,
                a, a.length);
    }

    @Test(dataProvider = "shortUnaryOpProvider")
    static void reinterpretShapeS2D_128_To_512(IntFunction<short[]> fa) {
        short[] a = fa.apply(1024);
        reinterpret_kernel(ShortVector.SPECIES_128, DoubleVector.SPECIES_512,
                a, a.length);
    }

    @Test(dataProvider = "shortUnaryOpProvider")
    static void reinterpretShapeS2D_128_To_MAX(IntFunction<short[]> fa) {
        short[] a = fa.apply(1024);
        reinterpret_kernel(ShortVector.SPECIES_128, DoubleVector.SPECIES_MAX,
                a, a.length);
    }
}
