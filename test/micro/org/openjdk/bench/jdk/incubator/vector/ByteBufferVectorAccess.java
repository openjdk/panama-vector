/*
 *  Copyright (c) 2021, Oracle and/or its affiliates. All rights reserved.
 *  Copyright (c) 2021, Rado Smogura. All rights reserved.
 *
 *  DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS FILE HEADER.
 *
 *  This code is free software; you can redistribute it and/or modify it
 *  under the terms of the GNU General Public License version 2 only, as
 *  published by the Free Software Foundation.
 *
 *  This code is distributed in the hope that it will be useful, but WITHOUT
 *  ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or
 *  FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License
 *  version 2 for more details (a copy is included in the LICENSE file that
 *  accompanied this code).
 *
 *  You should have received a copy of the GNU General Public License version
 *  2 along with this work; if not, write to the Free Software Foundation,
 *  Inc., 51 Franklin St, Fifth Floor, Boston, MA 02110-1301 USA.
 *
 *  Please contact Oracle, 500 Oracle Parkway, Redwood Shores, CA 94065 USA
 *  or visit www.oracle.com if you need additional information or have any
 *  questions.
 *
 */
package org.openjdk.bench.jdk.incubator.vector;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.concurrent.TimeUnit;
import jdk.incubator.vector.ByteVector;
import jdk.incubator.vector.VectorSpecies;
import org.openjdk.jmh.annotations.Benchmark;
import org.openjdk.jmh.annotations.BenchmarkMode;
import org.openjdk.jmh.annotations.CompilerControl;
import org.openjdk.jmh.annotations.Fork;
import org.openjdk.jmh.annotations.Measurement;
import org.openjdk.jmh.annotations.Mode;
import org.openjdk.jmh.annotations.OutputTimeUnit;
import org.openjdk.jmh.annotations.Param;
import org.openjdk.jmh.annotations.Setup;
import org.openjdk.jmh.annotations.State;
import org.openjdk.jmh.annotations.Warmup;

@BenchmarkMode(Mode.AverageTime)
@Warmup(iterations = 5, time = 500, timeUnit = TimeUnit.MILLISECONDS)
@Measurement(iterations = 10, time = 500, timeUnit = TimeUnit.MILLISECONDS)
@State(org.openjdk.jmh.annotations.Scope.Benchmark)
@OutputTimeUnit(TimeUnit.NANOSECONDS)
@Fork(value = 1, jvmArgsAppend = {
    "--add-modules=jdk.incubator.foreign,jdk.incubator.vector",
    "-Dforeign.restricted=permit",
    "--enable-native-access", "ALL-UNNAMED"})
public class ByteBufferVectorAccess {
  private static final VectorSpecies<Byte> SPECIES = VectorSpecies.ofLargestShape(byte.class);

  @Param("1024")
  private int size;

  ByteBuffer directIn, directOut;
  ByteBuffer heapIn, heapOut;

  ByteBuffer directInRo, directOutRo;
  ByteBuffer heapInRo, heapOutRo;

  @Setup
  public void setup() {
    directIn = ByteBuffer.allocateDirect(size);
    directOut = ByteBuffer.allocateDirect(size);

    heapIn = ByteBuffer.wrap(new byte[size]);
    heapOut = ByteBuffer.wrap(new byte[size]);

    directInRo = directIn.asReadOnlyBuffer();
    directOutRo = directOut.asReadOnlyBuffer();

    heapInRo = heapIn.asReadOnlyBuffer();
    heapOutRo = heapOut.asReadOnlyBuffer();
  }

  @Benchmark
  public void directBuffers() {
    copyMemory(directIn, directOut);
  }

  @Benchmark
  public void heapBuffers() {
    copyMemory(heapIn, heapOut);
  }

  @Benchmark
  public void pollutedBuffers2() {
    copyIntoNotInlined(directIn, directOut);
    copyIntoNotInlined(heapIn, heapOut);
  }

  @Benchmark
  public void pollutedBuffers3() {
    copyIntoNotInlined(directIn, directOut);
    copyIntoNotInlined(heapIn, heapOut);

    copyIntoNotInlined(directInRo, directOut);
    copyIntoNotInlined(heapInRo, heapOut);
  }

  @Benchmark
  public void pollutedBuffers4() {
    copyIntoNotInlined(directIn, heapOut); // Pollute if unswitch on 2nd param
    copyIntoNotInlined(heapIn, heapOut);

    copyIntoNotInlined(heapIn, directIn); // Pollute if unswitch on 1st param
    copyIntoNotInlined(heapIn, directOut);
  }


  boolean readOnlyException;

  @Benchmark
  public void pollutedBuffers5() {
    copyIntoNotInlined(directIn, heapOut);
    copyIntoNotInlined(heapIn, heapOut);

    copyIntoNotInlined(heapIn, directIn);
    copyIntoNotInlined(heapIn, directOut);

    if (readOnlyException) {
      try {
        copyIntoNotInlined(heapIn, directOutRo);
      } catch (Exception ignored) {}
      readOnlyException = !readOnlyException;
    }
  }

  @Benchmark
  public void arrayCopy() {
    byte[] in = heapIn.array();
    byte[] out = heapOut.array();

    for (int i=0; i < SPECIES.loopBound(in.length); i += SPECIES.vectorByteSize()) {
      final var v = ByteVector.fromArray(SPECIES, in, i);
      v.intoArray(out, i);
    }
  }

  @CompilerControl(CompilerControl.Mode.DONT_INLINE)
  protected void copyIntoNotInlined(ByteBuffer in, ByteBuffer out) {
    copyMemory(in, out);
  }

  @CompilerControl(CompilerControl.Mode.INLINE)
  protected void copyMemory(ByteBuffer in, ByteBuffer out) {
    for (int i=0; i < SPECIES.loopBound(in.limit()); i += SPECIES.vectorByteSize()) {
      final var v = ByteVector.fromByteBuffer(SPECIES, in, i, ByteOrder.nativeOrder());
      v.intoByteBuffer(out, i, ByteOrder.nativeOrder());
    }
  }
}
