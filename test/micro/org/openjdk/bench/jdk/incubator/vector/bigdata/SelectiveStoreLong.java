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

import jdk.incubator.vector.LongVector;
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
public class SelectiveStoreLong {

  private static int ARRAY_LENGTH = 512;

  private static final VectorSpecies<Long> LONG_64_SPECIES =
    LongVector.SPECIES_64;
  private static final VectorSpecies<Long> LONG_128_SPECIES =
    LongVector.SPECIES_128;
  private static final VectorSpecies<Long> LONG_256_SPECIES =
    LongVector.SPECIES_256;
  private static final VectorSpecies<Long> LONG_512_SPECIES =
    LongVector.SPECIES_512;
  private static final VectorSpecies<Long> LONG_PREFERRED_SPECIES =
    LongVector.SPECIES_PREFERRED;

  @State(Scope.Thread)
  public static class ThreadLocalData {

    private int conflict_scalar_cnt = 0;
    private int conflict_vector_cnt = 0;
    private long[] long_index = new long[ARRAY_LENGTH];
    private long[] long_conflict_scalar = new long[ARRAY_LENGTH];
    private long[] long_conflict_vector = new long[ARRAY_LENGTH];
    private long[] long_input1 = new long[ARRAY_LENGTH];
    private long[] long_input2 = new long[ARRAY_LENGTH];
    private Random rand = new Random();

    @Setup(Level.Trial)
    public void doSetup() {
      int conflict_id_each_5_elements = ThreadLocalRandom.current().nextInt(5);

      for (int i = 0; i < ARRAY_LENGTH; i++) {
        long_index[i] = ThreadLocalRandom.current().nextInt(ARRAY_LENGTH);
        long_input1[i] = ThreadLocalRandom.current().nextLong();
        // Generate 20% conflict data
        int remainder = i % 5;
        if (remainder == conflict_id_each_5_elements) {
          long_input2[i] = ThreadLocalRandom.current().nextLong();
        } else {
          long_input2[i] = long_input1[i];
        }

        long_conflict_scalar[i] = 0;
        long_conflict_vector[i] = 0;
      }
    }
  }

  @Benchmark
  @Threads(8)
  public void selectiveStore_long_scalar(ThreadLocalData tld) {
    tld.conflict_scalar_cnt = 0;
    for (int i = 0; i < ARRAY_LENGTH; i++) {
      if (tld.long_input1[i] != tld.long_input2[i]) {
        tld.long_conflict_scalar[tld.conflict_scalar_cnt++] = tld.long_index[i];
      }
    }
  }

  @Benchmark
  @Threads(8)
  public void selectiveStore_long_vector_64(ThreadLocalData tld) {
    tld.conflict_vector_cnt = 0;
    for (int i = 0; i < ARRAY_LENGTH; i += LONG_64_SPECIES.length()) {
      LongVector av = LongVector.fromArray(LONG_64_SPECIES, tld.long_input1, i);
      LongVector bv = LongVector.fromArray(LONG_64_SPECIES, tld.long_input2, i);
      LongVector cv = LongVector.fromArray(LONG_64_SPECIES, tld.long_index, i);
      VectorMask<Long> mask = av.compare(VectorOperators.NE, bv);
      tld.conflict_vector_cnt += cv.selectiveIntoArray(tld.long_conflict_vector, tld.conflict_vector_cnt, mask);
    }
  }

  @Benchmark
  @Threads(8)
  public void selectiveStore_long_vector_128(ThreadLocalData tld) {
    tld.conflict_vector_cnt = 0;
    for (int i = 0; i < ARRAY_LENGTH; i += LONG_128_SPECIES.length()) {
      LongVector av = LongVector.fromArray(LONG_128_SPECIES, tld.long_input1, i);
      LongVector bv = LongVector.fromArray(LONG_128_SPECIES, tld.long_input2, i);
      LongVector cv = LongVector.fromArray(LONG_128_SPECIES, tld.long_index, i);
      VectorMask<Long> mask = av.compare(VectorOperators.NE, bv);
      tld.conflict_vector_cnt += cv.selectiveIntoArray(tld.long_conflict_vector, tld.conflict_vector_cnt, mask);
    }
  }

  @Benchmark
  @Threads(8)
  public void selectiveStore_long_vector_256(ThreadLocalData tld) {
    tld.conflict_vector_cnt = 0;
    for (int i = 0; i < ARRAY_LENGTH; i += LONG_256_SPECIES.length()) {
      LongVector av = LongVector.fromArray(LONG_256_SPECIES, tld.long_input1, i);
      LongVector bv = LongVector.fromArray(LONG_256_SPECIES, tld.long_input2, i);
      LongVector cv = LongVector.fromArray(LONG_256_SPECIES, tld.long_index, i);
      VectorMask<Long> mask = av.compare(VectorOperators.NE, bv);
      tld.conflict_vector_cnt += cv.selectiveIntoArray(tld.long_conflict_vector, tld.conflict_vector_cnt, mask);
    }
  }

  @Benchmark
  @Threads(8)
  public void selectiveStore_long_vector_512(ThreadLocalData tld) {
    tld.conflict_vector_cnt = 0;
    for (int i = 0; i < ARRAY_LENGTH; i += LONG_512_SPECIES.length()) {
      LongVector av = LongVector.fromArray(LONG_512_SPECIES, tld.long_input1, i);
      LongVector bv = LongVector.fromArray(LONG_512_SPECIES, tld.long_input2, i);
      LongVector cv = LongVector.fromArray(LONG_512_SPECIES, tld.long_index, i);
      VectorMask<Long> mask = av.compare(VectorOperators.NE, bv);
      tld.conflict_vector_cnt += cv.selectiveIntoArray(tld.long_conflict_vector, tld.conflict_vector_cnt, mask);
    }
  }

  public void selectiveStore_long_vector_preferred(ThreadLocalData tld) {
    tld.conflict_vector_cnt = 0;
    for (int i = 0; i < ARRAY_LENGTH; i += LONG_PREFERRED_SPECIES.length()) {
      LongVector av = LongVector.fromArray(LONG_PREFERRED_SPECIES, tld.long_input1, i);
      LongVector bv = LongVector.fromArray(LONG_PREFERRED_SPECIES, tld.long_input2, i);
      LongVector cv = LongVector.fromArray(LONG_PREFERRED_SPECIES, tld.long_index, i);
      VectorMask<Long> mask = av.compare(VectorOperators.NE, bv);
      tld.conflict_vector_cnt += cv.selectiveIntoArray(tld.long_conflict_vector, tld.conflict_vector_cnt, mask);
    }
  }

  @Benchmark
  @Threads(8)
  public void selectiveStore_long_verify(ThreadLocalData tld) {
    // Initialization
    int conflict_id_each_5_elements = ThreadLocalRandom.current().nextInt(5);
    for (int i = 0; i < ARRAY_LENGTH; i++) {
      tld.long_index[i] = ThreadLocalRandom.current().nextInt(ARRAY_LENGTH);
      tld.long_input1[i] = ThreadLocalRandom.current().nextLong();
      // Generate 20% conflict data
      int remainder = i % 5;
      if (remainder == conflict_id_each_5_elements) {
        tld.long_input2[i] = ThreadLocalRandom.current().nextLong();
      } else {
        tld.long_input2[i] = tld.long_input1[i];
      }
      tld.long_conflict_scalar[i] = 0;
      tld.long_conflict_vector[i] = 0;
    }
    // Test
    selectiveStore_long_scalar(tld);
    selectiveStore_long_vector_preferred(tld);
    // Verify
    for (int i = 0; i < ARRAY_LENGTH; i++) {
      assert(tld.long_conflict_scalar[i] == tld.long_conflict_vector[i]);
    }
    assert(tld.conflict_scalar_cnt == tld.conflict_vector_cnt);
  }
}

