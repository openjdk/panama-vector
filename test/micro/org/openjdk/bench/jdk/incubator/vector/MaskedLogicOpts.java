/*
 * Copyright (c) 2021, Oracle and/or its affiliates. All rights reserved.
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
package org.openjdk.bench.jdk.incubator.vector;

import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.infra.*;

import jdk.incubator.vector.*;
import java.util.concurrent.TimeUnit;
import java.util.Random;

@BenchmarkMode(Mode.Throughput)
@OutputTimeUnit(TimeUnit.MILLISECONDS)
@State(Scope.Thread)
public class MaskedLogicOpts {
    @Param({"256","512","1024","2048","4096"})
    private int VECLEN;

    @Param({"128", "256", "512"})
    private int SPECIES;

    boolean [] mask_arr = {
        false, false, false, true, false, false, false, false,
        false, false, false, true, false, false, false, false,
        false, false, false, true, false, false, false, false,
        true, true, true, true, true, true, true, true,
        true, true, true, true, true, true, true, true,
        false, false, false, true, false, false, false, false,
        false, false, false, true, false, false, false, false,
        false, false, false, true, false, false, false, false
    };

    int INVOC_COUNTER = 1024;

    int [] i1 = new int[VECLEN];
    int [] i2 = new int[VECLEN];
    int [] i3 = new int[VECLEN];
    int [] i4 = new int[VECLEN];
    int [] i5 = new int[VECLEN];

    long [] l1 = new long[VECLEN];
    long [] l2 = new long[VECLEN];
    long [] l3 = new long[VECLEN];
    long [] l4 = new long[VECLEN];
    long [] l5 = new long[VECLEN];

    Vector<Integer> iv1;
    Vector<Integer> iv2;
    Vector<Integer> iv3;
    Vector<Integer> iv4;
    Vector<Integer> iv5;

    Vector<Long> lv1;
    Vector<Long> lv2;
    Vector<Long> lv3;
    Vector<Long> lv4;
    Vector<Long> lv5;

    VectorMask<Integer> imask;
    VectorMask<Long> lmask;

    VectorSpecies<Integer> ispecies;
    VectorSpecies<Long> lspecies;

    int int_arr_idx;
    int long_arr_idx;

    private Random r = new Random();

    @Setup(Level.Trial)
    public void init() {
        int_arr_idx = 0;
        long_arr_idx = 0;
        i1 = new int[VECLEN];
        i2 = new int[VECLEN];
        i3 = new int[VECLEN];
        i4 = new int[VECLEN];
        i5 = new int[VECLEN];

        l1 = new long[VECLEN];
        l2 = new long[VECLEN];
        l3 = new long[VECLEN];
        l4 = new long[VECLEN];
        l5 = new long[VECLEN];

        for (int i=0; i<VECLEN; i++) {
            i1[i] = r.nextInt();
            i2[i] = r.nextInt();
            i3[i] = r.nextInt();
            i4[i] = r.nextInt();
            i5[i] = r.nextInt();

            l1[i] = r.nextLong();
            l2[i] = r.nextLong();
            l3[i] = r.nextLong();
            l4[i] = r.nextLong();
            l5[i] = r.nextLong();
        }

        ispecies = VectorSpecies.of(int.class, VectorShape.forBitSize(SPECIES));
        lspecies = VectorSpecies.of(long.class, VectorShape.forBitSize(SPECIES));

        imask = VectorMask.fromArray(ispecies, mask_arr, 0);
        lmask = VectorMask.fromArray(lspecies, mask_arr, 0);
    }

    @Setup(Level.Invocation)
    public void init_per_invoc() {
        iv1 = IntVector.fromArray(ispecies, i1, int_arr_idx);
        iv2 = IntVector.fromArray(ispecies, i2, int_arr_idx);
        iv3 = IntVector.fromArray(ispecies, i3, int_arr_idx);
        iv4 = IntVector.fromArray(ispecies, i4, int_arr_idx);
        iv5 = IntVector.fromArray(ispecies, i5, int_arr_idx);

        lv1 = LongVector.fromArray(lspecies, l1, long_arr_idx);
        lv2 = LongVector.fromArray(lspecies, l2, long_arr_idx);
        lv3 = LongVector.fromArray(lspecies, l3, long_arr_idx);
        lv4 = LongVector.fromArray(lspecies, l4, long_arr_idx);
        lv5 = LongVector.fromArray(lspecies, l5, long_arr_idx);

        int_arr_idx = (int_arr_idx + ispecies.length()) & (VECLEN -1);
        long_arr_idx = (long_arr_idx + lspecies.length()) & (VECLEN -1);
    }

    @Benchmark
    public int maskedLogicOperationsInt() {
       for(int i = 0; i < INVOC_COUNTER; i++) {
           IntVector.fromArray(ispecies, i1, int_arr_idx)
               .lanewise(VectorOperators.AND, iv2, imask)
               .lanewise(VectorOperators.OR,  iv3, imask)
               .lanewise(VectorOperators.AND, iv4, imask)
               .lanewise(VectorOperators.XOR, iv5, imask)
               .intoArray(i1, int_arr_idx);
       }
       return i1[int_arr_idx];
    }

    @Benchmark
    public int partiallyMaskedLogicOperationsInt() {
       for(int i = 0; i < INVOC_COUNTER; i++) {
           IntVector.fromArray(ispecies, i1, int_arr_idx)
               .lanewise(VectorOperators.AND, iv2, imask)
               .lanewise(VectorOperators.OR,  iv3)
               .lanewise(VectorOperators.AND, iv4, imask)
               .lanewise(VectorOperators.XOR, iv5)
               .intoArray(i1, int_arr_idx);
       }
       return i1[int_arr_idx];
    }

    @Benchmark
    public int bitwiseBlendOperationInt() {
       for(int i = 0; i < INVOC_COUNTER; i++) {
           IntVector.fromArray(ispecies, i1, int_arr_idx)
               .lanewise(VectorOperators.BITWISE_BLEND, iv2, iv3, imask)
               .lanewise(VectorOperators.BITWISE_BLEND, iv3, iv4, imask)
               .lanewise(VectorOperators.BITWISE_BLEND, iv4, iv5, imask)
               .intoArray(i1, int_arr_idx);
       }
       return i1[int_arr_idx];
    }

    @Benchmark
    public long maskedLogicOperationsLong() {
       for(int i = 0; i < INVOC_COUNTER; i++) {
           LongVector.fromArray(lspecies, l1, long_arr_idx)
               .lanewise(VectorOperators.AND, lv2, lmask)
               .lanewise(VectorOperators.OR,  lv3, lmask)
               .lanewise(VectorOperators.AND, lv4, lmask)
               .lanewise(VectorOperators.XOR, lv5, lmask)
               .intoArray(l1, long_arr_idx);
       }
       return l1[long_arr_idx];
    }

    @Benchmark
    public long partiallyMaskedLogicOperationsLong() {
       for(int i = 0; i < INVOC_COUNTER; i++) {
           LongVector.fromArray(lspecies, l1, long_arr_idx)
               .lanewise(VectorOperators.AND, lv2, lmask)
               .lanewise(VectorOperators.OR,  lv3)
               .lanewise(VectorOperators.AND, lv4, lmask)
               .lanewise(VectorOperators.XOR, lv5)
               .intoArray(l1, long_arr_idx);
       }
       return l1[long_arr_idx];
    }

    @Benchmark
    public long bitwiseBlendOperationLong() {
       for(int i = 0; i < INVOC_COUNTER; i++) {
           LongVector.fromArray(lspecies, l1, long_arr_idx)
               .lanewise(VectorOperators.BITWISE_BLEND, lv2, lv3, lmask)
               .lanewise(VectorOperators.BITWISE_BLEND, lv3, lv4, lmask)
               .lanewise(VectorOperators.BITWISE_BLEND, lv4, lv5, lmask)
               .intoArray(l1, long_arr_idx);
       }
       return l1[long_arr_idx];
    }
}
