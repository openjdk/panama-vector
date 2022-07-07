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

import java.util.concurrent.TimeUnit;
import java.util.random.RandomGenerator;

import jdk.incubator.vector.IntVector;
import jdk.incubator.vector.VectorSpecies;
import jdk.incubator.vector.VectorMask;
import jdk.incubator.vector.VectorOperators;

import org.openjdk.jmh.annotations.*;

@BenchmarkMode(Mode.Throughput)
@OutputTimeUnit(TimeUnit.MILLISECONDS)
@State(Scope.Benchmark)
@Warmup(iterations = 10, time = 1)
@Measurement(iterations = 10, time = 1)
@Fork(value = 1, jvmArgsPrepend = {"--add-modules=jdk.incubator.vector"})
public class SelectiveStore {

  @Param("1024")
  int ARRAY_LENGTH;

  private static final VectorSpecies<Integer> INT_64_SPECIES =
    IntVector.SPECIES_64;
  private static final VectorSpecies<Integer> INT_128_SPECIES =
    IntVector.SPECIES_128;
  private static final VectorSpecies<Integer> INT_256_SPECIES =
    IntVector.SPECIES_256;
  private static final VectorSpecies<Integer> INT_512_SPECIES =
    IntVector.SPECIES_512;
  private static final VectorSpecies<Integer> INT_PREFERRED_SPECIES =
    IntVector.SPECIES_PREFERRED;

  private int conflict_cnt;
  private int[] index;
  private int[] input1;
  private int[] input2;
  private int[] conflict_array;

  @Setup
  public void init() {
    index = new int[ARRAY_LENGTH];
    input1 = new int[ARRAY_LENGTH];
    input2 = new int[ARRAY_LENGTH];
    conflict_array = new int[ARRAY_LENGTH];

    RandomGenerator rng = RandomGenerator.getDefault();
    int conflict_id_each_5_elements = rng.nextInt(5);

    for (int i = 0; i < ARRAY_LENGTH; i++) {
      index[i] = rng.nextInt(ARRAY_LENGTH);
      input1[i] = rng.nextInt();
      // Generate 20% conflict data
      int remainder = i % 5;
      if (remainder == conflict_id_each_5_elements) {
        input2[i] = rng.nextInt();
      } else {
        input2[i] = input1[i];
      }
      conflict_array[i] = 0;
    }
  }

  private void selectiveStore(VectorSpecies<Integer> species) {
    conflict_cnt = 0;
    for (int i = 0; i < ARRAY_LENGTH; i += species.length()) {
      IntVector av = IntVector.fromArray(species, input1, i);
      IntVector bv = IntVector.fromArray(species, input2, i);
      IntVector cv = IntVector.fromArray(species, index, i);
      VectorMask<Integer> mask = av.compare(VectorOperators.NE, bv);
      IntVector dv = cv.compress(mask);
      dv.intoArray(conflict_array, conflict_cnt, mask.compress());
      conflict_cnt += mask.trueCount();
    }
  }

  @Benchmark
  public void selectiveStore_scalar() {
    conflict_cnt = 0;
    for (int i = 0; i < ARRAY_LENGTH; i++) {
      if (input1[i] != input2[i]) {
        conflict_array[conflict_cnt] = index[i];
        conflict_cnt++;
      }
    }
  }

  @Benchmark
  public void selectiveStore_vector_64() {
    selectiveStore(INT_64_SPECIES);
  }

  @Benchmark
  public void selectiveStore_vector_128() {
    selectiveStore(INT_128_SPECIES);
  }

  @Benchmark
  public void selectiveStore_vector_256() {
    selectiveStore(INT_256_SPECIES);
  }

  @Benchmark
  public void selectiveStore_vector_512() {
    selectiveStore(INT_512_SPECIES);
  }

  @Benchmark
  public void selectiveStore_vector_preferred() {
    selectiveStore(INT_PREFERRED_SPECIES);
  }
}

