/*
 * Copyright (c) 2021, Oracle and/or its affiliates. All rights reserved.
 * Copyright (c) 2021, Alibaba Group Holding Limited. All Rights Reserved.
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

package org.openjdk.bench.jdk.incubator.vector.bigdata;

import java.util.Random;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.ThreadLocalRandom;

import jdk.incubator.vector.IntVector;
import jdk.incubator.vector.VectorSpecies;
import jdk.incubator.vector.VectorMask;
import jdk.incubator.vector.VectorOperators;

import org.openjdk.jmh.annotations.*;

@BenchmarkMode(Mode.Throughput)
@OutputTimeUnit(TimeUnit.MILLISECONDS)
@State(Scope.Benchmark)
@Warmup(iterations = 5, time = 1)
@Measurement(iterations = 10, time = 1)
@Fork(value = 1, jvmArgsPrepend = {"--add-modules=jdk.incubator.vector", "--add-exports", "java.base/jdk.internal.vm.vector=ALL-UNNAMED", "-ea"})
public class SelectiveStore {

  private static int ARRAY_LENGTH = 1024;

  private static final VectorSpecies<Integer> INT_128_SPECIES =
    IntVector.SPECIES_128;
  private static final VectorSpecies<Integer> INT_256_SPECIES =
    IntVector.SPECIES_256;
  private static final VectorSpecies<Integer> INT_512_SPECIES =
    IntVector.SPECIES_512;
  private static final VectorSpecies<Integer> INT_PREFERRED_SPECIES =
    IntVector.SPECIES_PREFERRED;

  @State(Scope.Thread)
  public static class ThreadLocalData {

    private int conflict_scalar_cnt = 0;
    private int conflict_vector_cnt = 0;
    private int[] index = new int[ARRAY_LENGTH];
    private int[] conflict_scalar = new int[ARRAY_LENGTH];
    private int[] conflict_vector = new int[ARRAY_LENGTH];
    private int[] input1 = new int[ARRAY_LENGTH];
    private int[] input2 = new int[ARRAY_LENGTH];
    private Random rand = new Random();

    @Setup(Level.Trial)
    public void doSetup() {
      int conflict_id_each_5_elements = ThreadLocalRandom.current().nextInt(5);

      for (int i = 0; i < ARRAY_LENGTH; i++) {
        index[i] = ThreadLocalRandom.current().nextInt(ARRAY_LENGTH);
        input1[i] = ThreadLocalRandom.current().nextInt();
        // Generate 20% conflict data
        int remainder = i % 5;
        if (remainder == conflict_id_each_5_elements) {
          input2[i] = ThreadLocalRandom.current().nextInt();
        } else {
          input2[i] = input1[i];
        }

        conflict_scalar[i] = 0;
        conflict_vector[i] = 0;
      }
    }
  }

  @Benchmark
  @Threads(8)
  public void selectiveStore_scalar(ThreadLocalData tld) {
    tld.conflict_scalar_cnt = 0;
    for (int i = 0; i < ARRAY_LENGTH; i++) {
      if (tld.input1[i] != tld.input2[i]) {
        tld.conflict_scalar[tld.conflict_scalar_cnt++] = tld.index[i];
      }
    }
  }

  @Benchmark
  @Threads(8)
  public void selectiveStore_vector_128(ThreadLocalData tld) {
    tld.conflict_vector_cnt = 0;
    for (int i = 0; i < ARRAY_LENGTH; i += INT_128_SPECIES.length()) {
      IntVector av = IntVector.fromArray(INT_128_SPECIES, tld.input1, i);
      IntVector bv = IntVector.fromArray(INT_128_SPECIES, tld.input2, i);
      IntVector cv = IntVector.fromArray(INT_128_SPECIES, tld.index, i);
      VectorMask<Integer> mask = av.compare(VectorOperators.NE, bv);
      IntVector dv = cv.compress(mask);
      dv.intoArray(tld.conflict_vector, tld.conflict_vector_cnt, mask.prefixMask());
      tld.conflict_vector_cnt += mask.trueCount();
    }
  }

  @Benchmark
  @Threads(8)
  public void selectiveStore_vector_256(ThreadLocalData tld) {
    tld.conflict_vector_cnt = 0;
    for (int i = 0; i < ARRAY_LENGTH; i += INT_256_SPECIES.length()) {
      IntVector av = IntVector.fromArray(INT_256_SPECIES, tld.input1, i);
      IntVector bv = IntVector.fromArray(INT_256_SPECIES, tld.input2, i);
      IntVector cv = IntVector.fromArray(INT_256_SPECIES, tld.index, i);
      VectorMask<Integer> mask = av.compare(VectorOperators.NE, bv);
      IntVector dv = cv.compress(mask);
      dv.intoArray(tld.conflict_vector, tld.conflict_vector_cnt, mask.prefixMask());
      tld.conflict_vector_cnt += mask.trueCount();
    }
  }

  @Benchmark
  @Threads(8)
  public void selectiveStore_vector_512(ThreadLocalData tld) {
    tld.conflict_vector_cnt = 0;
    for (int i = 0; i < ARRAY_LENGTH; i += INT_512_SPECIES.length()) {
      IntVector av = IntVector.fromArray(INT_512_SPECIES, tld.input1, i);
      IntVector bv = IntVector.fromArray(INT_512_SPECIES, tld.input2, i);
      IntVector cv = IntVector.fromArray(INT_512_SPECIES, tld.index, i);
      VectorMask<Integer> mask = av.compare(VectorOperators.NE, bv);
      IntVector dv = cv.compress(mask);
      dv.intoArray(tld.conflict_vector, tld.conflict_vector_cnt, mask.prefixMask());
      tld.conflict_vector_cnt += mask.trueCount();
    }
  }

  @Benchmark
  @Threads(8)
  public void selectiveStore_vector_preferred(ThreadLocalData tld) {
    tld.conflict_vector_cnt = 0;
    for (int i = 0; i < ARRAY_LENGTH; i += INT_PREFERRED_SPECIES.length()) {
      IntVector av = IntVector.fromArray(INT_PREFERRED_SPECIES, tld.input1, i);
      IntVector bv = IntVector.fromArray(INT_PREFERRED_SPECIES, tld.input2, i);
      IntVector cv = IntVector.fromArray(INT_PREFERRED_SPECIES, tld.index, i);
      VectorMask<Integer> mask = av.compare(VectorOperators.NE, bv);
      IntVector dv = cv.compress(mask);
      dv.intoArray(tld.conflict_vector, tld.conflict_vector_cnt, mask.prefixMask());
      tld.conflict_vector_cnt += mask.trueCount();
    }
  }

  @Benchmark
  @Threads(8)
  public void selectiveStore_verify(ThreadLocalData tld) {
    // Initialization
    int conflict_id_each_5_elements = ThreadLocalRandom.current().nextInt(5);
    for (int i = 0; i < ARRAY_LENGTH; i++) {
      tld.index[i] = ThreadLocalRandom.current().nextInt(ARRAY_LENGTH);
      tld.input1[i] = ThreadLocalRandom.current().nextInt();
      // Generate 20% conflict data
      int remainder = i % 5;
      if (remainder == conflict_id_each_5_elements) {
        tld.input2[i] = ThreadLocalRandom.current().nextInt();
      } else {
        tld.input2[i] = tld.input1[i];
      }
      tld.conflict_scalar[i] = 0;
      tld.conflict_vector[i] = 0;
    }
    // Test
    selectiveStore_scalar(tld);
    selectiveStore_vector_preferred(tld);
    // Verify
    for (int i = 0; i < ARRAY_LENGTH; i++) {
      assert(tld.conflict_scalar[i] == tld.conflict_vector[i]);
    }
    assert(tld.conflict_scalar_cnt == tld.conflict_vector_cnt);
  }
}

