/*
 * Copyright (c) 2017, 2021, Oracle and/or its affiliates. All rights reserved.
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

import java.nio.ByteBuffer;
import java.util.Arrays;
import java.util.Objects;
import java.util.function.IntUnaryOperator;

import jdk.internal.vm.annotation.ForceInline;
import jdk.internal.vm.vector.VectorSupport;

import static jdk.internal.vm.vector.VectorSupport.*;

import static jdk.incubator.vector.VectorOperators.*;

// -- This file was mechanically generated: Do not edit! -- //

@SuppressWarnings("cast")  // warning: redundant cast
final class Halffloat64Vector extends HalffloatVector {
    static final HalffloatSpecies VSPECIES =
        (HalffloatSpecies) HalffloatVector.SPECIES_64;

    static final VectorShape VSHAPE =
        VSPECIES.vectorShape();

    static final Class<Halffloat64Vector> VCLASS = Halffloat64Vector.class;

    static final int VSIZE = VSPECIES.vectorBitSize();

    static final int VLENGTH = VSPECIES.laneCount(); // used by the JVM

    static final Class<Halffloat> ETYPE = Halffloat.class; // used by the JVM

    Halffloat64Vector(short[] v) {
        super(v);
    }

    // For compatibility as Halffloat64Vector::new,
    // stored into species.vectorFactory.
    Halffloat64Vector(Object v) {
        this((short[]) v);
    }

    static final Halffloat64Vector ZERO = new Halffloat64Vector(new short[VLENGTH]);
    static final Halffloat64Vector IOTA = new Halffloat64Vector(VSPECIES.iotaArray());

    static {
        // Warm up a few species caches.
        // If we do this too much we will
        // get NPEs from bootstrap circularity.
        VSPECIES.dummyVector();
        VSPECIES.withLanes(LaneType.BYTE);
    }

    // Specialized extractors

    @ForceInline
    final @Override
    public HalffloatSpecies vspecies() {
        // ISSUE:  This should probably be a @Stable
        // field inside AbstractVector, rather than
        // a megamorphic method.
        return VSPECIES;
    }

    @ForceInline
    @Override
    public final Class<Halffloat> elementType() { return Halffloat.class; }

    @ForceInline
    @Override
    public final int elementSize() { return Halffloat.SIZE; }

    @ForceInline
    @Override
    public final VectorShape shape() { return VSHAPE; }

    @ForceInline
    @Override
    public final int length() { return VLENGTH; }

    @ForceInline
    @Override
    public final int bitSize() { return VSIZE; }

    @ForceInline
    @Override
    public final int byteSize() { return VSIZE / Byte.SIZE; }

    /*package-private*/
    @ForceInline
    final @Override
    short[] vec() {
        return (short[])getPayload();
    }

    // Virtualized constructors

    @Override
    @ForceInline
    public final Halffloat64Vector broadcast(short e) {
        return (Halffloat64Vector) super.broadcastTemplate(e);  // specialize
    }

    @Override
    @ForceInline
    public final Halffloat64Vector broadcast(long e) {
        return (Halffloat64Vector) super.broadcastTemplate(e);  // specialize
    }

    @Override
    @ForceInline
    Halffloat64Mask maskFromArray(boolean[] bits) {
        return new Halffloat64Mask(bits);
    }

    @Override
    @ForceInline
    Halffloat64Shuffle iotaShuffle() { return Halffloat64Shuffle.IOTA; }

    @ForceInline
    Halffloat64Shuffle iotaShuffle(int start, int step, boolean wrap) {
      if (wrap) {
        return (Halffloat64Shuffle)VectorSupport.shuffleIota(ETYPE, Halffloat64Shuffle.class, VSPECIES, VLENGTH, start, step, 1,
                (l, lstart, lstep, s) -> s.shuffleFromOp(i -> (VectorIntrinsics.wrapToRange(i*lstep + lstart, l))));
      } else {
        return (Halffloat64Shuffle)VectorSupport.shuffleIota(ETYPE, Halffloat64Shuffle.class, VSPECIES, VLENGTH, start, step, 0,
                (l, lstart, lstep, s) -> s.shuffleFromOp(i -> (i*lstep + lstart)));
      }
    }

    @Override
    @ForceInline
    Halffloat64Shuffle shuffleFromBytes(byte[] reorder) { return new Halffloat64Shuffle(reorder); }

    @Override
    @ForceInline
    Halffloat64Shuffle shuffleFromArray(int[] indexes, int i) { return new Halffloat64Shuffle(indexes, i); }

    @Override
    @ForceInline
    Halffloat64Shuffle shuffleFromOp(IntUnaryOperator fn) { return new Halffloat64Shuffle(fn); }

    // Make a vector of the same species but the given elements:
    @ForceInline
    final @Override
    Halffloat64Vector vectorFactory(short[] vec) {
        return new Halffloat64Vector(vec);
    }

    @ForceInline
    final @Override
    Byte64Vector asByteVectorRaw() {
        return (Byte64Vector) super.asByteVectorRawTemplate();  // specialize
    }

    @ForceInline
    final @Override
    AbstractVector<?> asVectorRaw(LaneType laneType) {
        return super.asVectorRawTemplate(laneType);  // specialize
    }

    // Unary operator

    @ForceInline
    final @Override
    Halffloat64Vector uOp(FUnOp f) {
        return (Halffloat64Vector) super.uOpTemplate(f);  // specialize
    }

    @ForceInline
    final @Override
    Halffloat64Vector uOp(VectorMask<Halffloat> m, FUnOp f) {
        return (Halffloat64Vector)
            super.uOpTemplate((Halffloat64Mask)m, f);  // specialize
    }

    // Binary operator

    @ForceInline
    final @Override
    Halffloat64Vector bOp(Vector<Halffloat> v, FBinOp f) {
        return (Halffloat64Vector) super.bOpTemplate((Halffloat64Vector)v, f);  // specialize
    }

    @ForceInline
    final @Override
    Halffloat64Vector bOp(Vector<Halffloat> v,
                     VectorMask<Halffloat> m, FBinOp f) {
        return (Halffloat64Vector)
            super.bOpTemplate((Halffloat64Vector)v, (Halffloat64Mask)m,
                              f);  // specialize
    }

    // Ternary operator

    @ForceInline
    final @Override
    Halffloat64Vector tOp(Vector<Halffloat> v1, Vector<Halffloat> v2, FTriOp f) {
        return (Halffloat64Vector)
            super.tOpTemplate((Halffloat64Vector)v1, (Halffloat64Vector)v2,
                              f);  // specialize
    }

    @ForceInline
    final @Override
    Halffloat64Vector tOp(Vector<Halffloat> v1, Vector<Halffloat> v2,
                     VectorMask<Halffloat> m, FTriOp f) {
        return (Halffloat64Vector)
            super.tOpTemplate((Halffloat64Vector)v1, (Halffloat64Vector)v2,
                              (Halffloat64Mask)m, f);  // specialize
    }

    @ForceInline
    final @Override
    short rOp(short v, VectorMask<Halffloat> m, FBinOp f) {
        return super.rOpTemplate(v, m, f);  // specialize
    }

    @Override
    @ForceInline
    public final <F>
    Vector<F> convertShape(VectorOperators.Conversion<Halffloat,F> conv,
                           VectorSpecies<F> rsp, int part) {
        return super.convertShapeTemplate(conv, rsp, part);  // specialize
    }

    @Override
    @ForceInline
    public final <F>
    Vector<F> reinterpretShape(VectorSpecies<F> toSpecies, int part) {
        return super.reinterpretShapeTemplate(toSpecies, part);  // specialize
    }

    // Specialized algebraic operations:

    // The following definition forces a specialized version of this
    // crucial method into the v-table of this class.  A call to add()
    // will inline to a call to lanewise(ADD,), at which point the JIT
    // intrinsic will have the opcode of ADD, plus all the metadata
    // for this particular class, enabling it to generate precise
    // code.
    //
    // There is probably no benefit to the JIT to specialize the
    // masked or broadcast versions of the lanewise method.

    @Override
    @ForceInline
    public Halffloat64Vector lanewise(Unary op) {
        return (Halffloat64Vector) super.lanewiseTemplate(op);  // specialize
    }

    @Override
    @ForceInline
    public Halffloat64Vector lanewise(Unary op, VectorMask<Halffloat> m) {
        return (Halffloat64Vector) super.lanewiseTemplate(op, Halffloat64Mask.class, (Halffloat64Mask) m);  // specialize
    }

    @Override
    @ForceInline
    public Halffloat64Vector lanewise(Binary op, Vector<Halffloat> v) {
        return (Halffloat64Vector) super.lanewiseTemplate(op, v);  // specialize
    }

    @Override
    @ForceInline
    public Halffloat64Vector lanewise(Binary op, Vector<Halffloat> v, VectorMask<Halffloat> m) {
        return (Halffloat64Vector) super.lanewiseTemplate(op, Halffloat64Mask.class, v, (Halffloat64Mask) m);  // specialize
    }


    /*package-private*/
    @Override
    @ForceInline
    public final
    Halffloat64Vector
    lanewise(Ternary op, Vector<Halffloat> v1, Vector<Halffloat> v2) {
        return (Halffloat64Vector) super.lanewiseTemplate(op, v1, v2);  // specialize
    }

    @Override
    @ForceInline
    public final
    Halffloat64Vector
    lanewise(Ternary op, Vector<Halffloat> v1, Vector<Halffloat> v2, VectorMask<Halffloat> m) {
        return (Halffloat64Vector) super.lanewiseTemplate(op, Halffloat64Mask.class, v1, v2, (Halffloat64Mask) m);  // specialize
    }

    @Override
    @ForceInline
    public final
    Halffloat64Vector addIndex(int scale) {
        return (Halffloat64Vector) super.addIndexTemplate(scale);  // specialize
    }

    // Type specific horizontal reductions

    @Override
    @ForceInline
    public final short reduceLanes(VectorOperators.Associative op) {
        return super.reduceLanesTemplate(op);  // specialized
    }

    @Override
    @ForceInline
    public final short reduceLanes(VectorOperators.Associative op,
                                    VectorMask<Halffloat> m) {
        return super.reduceLanesTemplate(op, Halffloat64Mask.class, (Halffloat64Mask) m);  // specialized
    }

    @Override
    @ForceInline
    public final long reduceLanesToLong(VectorOperators.Associative op) {
        return (long) super.reduceLanesTemplate(op);  // specialized
    }

    @Override
    @ForceInline
    public final long reduceLanesToLong(VectorOperators.Associative op,
                                        VectorMask<Halffloat> m) {
        return (long) super.reduceLanesTemplate(op, Halffloat64Mask.class, (Halffloat64Mask) m);  // specialized
    }

    @ForceInline
    public VectorShuffle<Halffloat> toShuffle() {
        return super.toShuffleTemplate(Halffloat64Shuffle.class); // specialize
    }

    // Specialized unary testing

    @Override
    @ForceInline
    public final Halffloat64Mask test(Test op) {
        return super.testTemplate(Halffloat64Mask.class, op);  // specialize
    }

    // Specialized comparisons

    @Override
    @ForceInline
    public final Halffloat64Mask compare(Comparison op, Vector<Halffloat> v) {
        return super.compareTemplate(Halffloat64Mask.class, op, v);  // specialize
    }

    @Override
    @ForceInline
    public final Halffloat64Mask compare(Comparison op, short s) {
        return super.compareTemplate(Halffloat64Mask.class, op, s);  // specialize
    }

    @Override
    @ForceInline
    public final Halffloat64Mask compare(Comparison op, long s) {
        return super.compareTemplate(Halffloat64Mask.class, op, s);  // specialize
    }

    @Override
    @ForceInline
    public final Halffloat64Mask compare(Comparison op, Vector<Halffloat> v, VectorMask<Halffloat> m) {
        return super.compareTemplate(Halffloat64Mask.class, op, v, (Halffloat64Mask) m);
    }


    @Override
    @ForceInline
    public Halffloat64Vector blend(Vector<Halffloat> v, VectorMask<Halffloat> m) {
        return (Halffloat64Vector)
            super.blendTemplate(Halffloat64Mask.class,
                                (Halffloat64Vector) v,
                                (Halffloat64Mask) m);  // specialize
    }

    @Override
    @ForceInline
    public Halffloat64Vector slice(int origin, Vector<Halffloat> v) {
        return (Halffloat64Vector) super.sliceTemplate(origin, v);  // specialize
    }

    @Override
    @ForceInline
    public Halffloat64Vector slice(int origin) {
        return (Halffloat64Vector) super.sliceTemplate(origin);  // specialize
    }

    @Override
    @ForceInline
    public Halffloat64Vector unslice(int origin, Vector<Halffloat> w, int part) {
        return (Halffloat64Vector) super.unsliceTemplate(origin, w, part);  // specialize
    }

    @Override
    @ForceInline
    public Halffloat64Vector unslice(int origin, Vector<Halffloat> w, int part, VectorMask<Halffloat> m) {
        return (Halffloat64Vector)
            super.unsliceTemplate(Halffloat64Mask.class,
                                  origin, w, part,
                                  (Halffloat64Mask) m);  // specialize
    }

    @Override
    @ForceInline
    public Halffloat64Vector unslice(int origin) {
        return (Halffloat64Vector) super.unsliceTemplate(origin);  // specialize
    }

    @Override
    @ForceInline
    public Halffloat64Vector rearrange(VectorShuffle<Halffloat> s) {
        return (Halffloat64Vector)
            super.rearrangeTemplate(Halffloat64Shuffle.class,
                                    (Halffloat64Shuffle) s);  // specialize
    }

    @Override
    @ForceInline
    public Halffloat64Vector rearrange(VectorShuffle<Halffloat> shuffle,
                                  VectorMask<Halffloat> m) {
        return (Halffloat64Vector)
            super.rearrangeTemplate(Halffloat64Shuffle.class,
                                    Halffloat64Mask.class,
                                    (Halffloat64Shuffle) shuffle,
                                    (Halffloat64Mask) m);  // specialize
    }

    @Override
    @ForceInline
    public Halffloat64Vector rearrange(VectorShuffle<Halffloat> s,
                                  Vector<Halffloat> v) {
        return (Halffloat64Vector)
            super.rearrangeTemplate(Halffloat64Shuffle.class,
                                    (Halffloat64Shuffle) s,
                                    (Halffloat64Vector) v);  // specialize
    }

    @Override
    @ForceInline
    public Halffloat64Vector selectFrom(Vector<Halffloat> v) {
        return (Halffloat64Vector)
            super.selectFromTemplate((Halffloat64Vector) v);  // specialize
    }

    @Override
    @ForceInline
    public Halffloat64Vector selectFrom(Vector<Halffloat> v,
                                   VectorMask<Halffloat> m) {
        return (Halffloat64Vector)
            super.selectFromTemplate((Halffloat64Vector) v,
                                     (Halffloat64Mask) m);  // specialize
    }


    @ForceInline
    @Override
    public short lane(int i) {
        short bits;
        switch(i) {
            case 0: bits = laneHelper(0); break;
            case 1: bits = laneHelper(1); break;
            case 2: bits = laneHelper(2); break;
            case 3: bits = laneHelper(3); break;
            default: throw new IllegalArgumentException("Index " + i + " must be zero or positive, and less than " + VLENGTH);
        }
        return Halffloat.shortBitsToHalffloat(bits);
    }

    public short laneHelper(int i) {
        return (short) VectorSupport.extract(
                     VCLASS, ETYPE, VLENGTH,
                     this, i,
                     (vec, ix) -> {
                     short[] vecarr = vec.vec();
                     return (long)Halffloat.shortToShortBits(vecarr[ix]);
                     });
    }

    @ForceInline
    @Override
    public Halffloat64Vector withLane(int i, short e) {
        switch(i) {
            case 0: return withLaneHelper(0, e);
            case 1: return withLaneHelper(1, e);
            case 2: return withLaneHelper(2, e);
            case 3: return withLaneHelper(3, e);
            default: throw new IllegalArgumentException("Index " + i + " must be zero or positive, and less than " + VLENGTH);
        }
    }

    public Halffloat64Vector withLaneHelper(int i, short e) {
        return VectorSupport.insert(
                                VCLASS, ETYPE, VLENGTH,
                                this, i, (long)Halffloat.shortToShortBits(e),
                                (v, ix, bits) -> {
                                    short[] res = v.vec().clone();
                                    res[ix] = Halffloat.shortBitsToHalffloat((short)bits);
                                    return v.vectorFactory(res);
                                });
    }

    // Mask

    static final class Halffloat64Mask extends AbstractMask<Halffloat> {
        static final int VLENGTH = VSPECIES.laneCount();    // used by the JVM
        static final Class<Halffloat> ETYPE = Halffloat.class; // used by the JVM

        Halffloat64Mask(boolean[] bits) {
            this(bits, 0);
        }

        Halffloat64Mask(boolean[] bits, int offset) {
            super(prepare(bits, offset));
        }

        Halffloat64Mask(boolean val) {
            super(prepare(val));
        }

        private static boolean[] prepare(boolean[] bits, int offset) {
            boolean[] newBits = new boolean[VSPECIES.laneCount()];
            for (int i = 0; i < newBits.length; i++) {
                newBits[i] = bits[offset + i];
            }
            return newBits;
        }

        private static boolean[] prepare(boolean val) {
            boolean[] bits = new boolean[VSPECIES.laneCount()];
            Arrays.fill(bits, val);
            return bits;
        }

        @ForceInline
        final @Override
        public HalffloatSpecies vspecies() {
            // ISSUE:  This should probably be a @Stable
            // field inside AbstractMask, rather than
            // a megamorphic method.
            return VSPECIES;
        }

        @ForceInline
        boolean[] getBits() {
            return (boolean[])getPayload();
        }

        @Override
        Halffloat64Mask uOp(MUnOp f) {
            boolean[] res = new boolean[vspecies().laneCount()];
            boolean[] bits = getBits();
            for (int i = 0; i < res.length; i++) {
                res[i] = f.apply(i, bits[i]);
            }
            return new Halffloat64Mask(res);
        }

        @Override
        Halffloat64Mask bOp(VectorMask<Halffloat> m, MBinOp f) {
            boolean[] res = new boolean[vspecies().laneCount()];
            boolean[] bits = getBits();
            boolean[] mbits = ((Halffloat64Mask)m).getBits();
            for (int i = 0; i < res.length; i++) {
                res[i] = f.apply(i, bits[i], mbits[i]);
            }
            return new Halffloat64Mask(res);
        }

        @ForceInline
        @Override
        public final
        Halffloat64Vector toVector() {
            return (Halffloat64Vector) super.toVectorTemplate();  // specialize
        }

        /**
         * Helper function for lane-wise mask conversions.
         * This function kicks in after intrinsic failure.
         */
        @ForceInline
        private final <E>
        VectorMask<E> defaultMaskCast(AbstractSpecies<E> dsp) {
            if (length() != dsp.laneCount())
                throw new IllegalArgumentException("VectorMask length and species length differ");
            boolean[] maskArray = toArray();
            return  dsp.maskFactory(maskArray).check(dsp);
        }

        @Override
        @ForceInline
        public <E> VectorMask<E> cast(VectorSpecies<E> dsp) {
            AbstractSpecies<E> species = (AbstractSpecies<E>) dsp;
            if (length() != species.laneCount())
                throw new IllegalArgumentException("VectorMask length and species length differ");

            return VectorSupport.convert(VectorSupport.VECTOR_OP_CAST,
                this.getClass(), ETYPE, VLENGTH,
                species.maskType(), species.elementType(), VLENGTH,
                this, species,
                (m, s) -> s.maskFactory(m.toArray()).check(s));
        }

        @Override
        @ForceInline
        public Halffloat64Mask eq(VectorMask<Halffloat> mask) {
            Objects.requireNonNull(mask);
            Halffloat64Mask m = (Halffloat64Mask)mask;
            return xor(m.not());
        }

        // Unary operations

        @Override
        @ForceInline
        public Halffloat64Mask not() {
            return xor(maskAll(true));
        }

        // Binary operations

        @Override
        @ForceInline
        public Halffloat64Mask and(VectorMask<Halffloat> mask) {
            Objects.requireNonNull(mask);
            Halffloat64Mask m = (Halffloat64Mask)mask;
            return VectorSupport.binaryOp(VECTOR_OP_AND, Halffloat64Mask.class, null, short.class, VLENGTH,
                                          this, m, null,
                                          (m1, m2, vm) -> m1.bOp(m2, (i, a, b) -> a & b));
        }

        @Override
        @ForceInline
        public Halffloat64Mask or(VectorMask<Halffloat> mask) {
            Objects.requireNonNull(mask);
            Halffloat64Mask m = (Halffloat64Mask)mask;
            return VectorSupport.binaryOp(VECTOR_OP_OR, Halffloat64Mask.class, null, short.class, VLENGTH,
                                          this, m, null,
                                          (m1, m2, vm) -> m1.bOp(m2, (i, a, b) -> a | b));
        }

        @ForceInline
        /* package-private */
        Halffloat64Mask xor(VectorMask<Halffloat> mask) {
            Objects.requireNonNull(mask);
            Halffloat64Mask m = (Halffloat64Mask)mask;
            return VectorSupport.binaryOp(VECTOR_OP_XOR, Halffloat64Mask.class, null, short.class, VLENGTH,
                                          this, m, null,
                                          (m1, m2, vm) -> m1.bOp(m2, (i, a, b) -> a ^ b));
        }

        // Mask Query operations

        @Override
        @ForceInline
        public int trueCount() {
            return (int) VectorSupport.maskReductionCoerced(VECTOR_OP_MASK_TRUECOUNT, Halffloat64Mask.class, short.class, VLENGTH, this,
                                                      (m) -> trueCountHelper(m.getBits()));
        }

        @Override
        @ForceInline
        public int firstTrue() {
            return (int) VectorSupport.maskReductionCoerced(VECTOR_OP_MASK_FIRSTTRUE, Halffloat64Mask.class, short.class, VLENGTH, this,
                                                      (m) -> firstTrueHelper(m.getBits()));
        }

        @Override
        @ForceInline
        public int lastTrue() {
            return (int) VectorSupport.maskReductionCoerced(VECTOR_OP_MASK_LASTTRUE, Halffloat64Mask.class, short.class, VLENGTH, this,
                                                      (m) -> lastTrueHelper(m.getBits()));
        }

        @Override
        @ForceInline
        public long toLong() {
            if (length() > Long.SIZE) {
                throw new UnsupportedOperationException("too many lanes for one long");
            }
            return VectorSupport.maskReductionCoerced(VECTOR_OP_MASK_TOLONG, Halffloat64Mask.class, short.class, VLENGTH, this,
                                                      (m) -> toLongHelper(m.getBits()));
        }

        // Reductions

        @Override
        @ForceInline
        public boolean anyTrue() {
            return VectorSupport.test(BT_ne, Halffloat64Mask.class, short.class, VLENGTH,
                                         this, vspecies().maskAll(true),
                                         (m, __) -> anyTrueHelper(((Halffloat64Mask)m).getBits()));
        }

        @Override
        @ForceInline
        public boolean allTrue() {
            return VectorSupport.test(BT_overflow, Halffloat64Mask.class, short.class, VLENGTH,
                                         this, vspecies().maskAll(true),
                                         (m, __) -> allTrueHelper(((Halffloat64Mask)m).getBits()));
        }

        @ForceInline
        /*package-private*/
        static Halffloat64Mask maskAll(boolean bit) {
            return VectorSupport.broadcastCoerced(Halffloat64Mask.class, short.class, VLENGTH,
                                                  (bit ? -1 : 0), null,
                                                  (v, __) -> (v != 0 ? TRUE_MASK : FALSE_MASK));
        }
        private static final Halffloat64Mask  TRUE_MASK = new Halffloat64Mask(true);
        private static final Halffloat64Mask FALSE_MASK = new Halffloat64Mask(false);

    }

    // Shuffle

    static final class Halffloat64Shuffle extends AbstractShuffle<Halffloat> {
        static final int VLENGTH = VSPECIES.laneCount();    // used by the JVM
        static final Class<Halffloat> ETYPE = Halffloat.class; // used by the JVM

        Halffloat64Shuffle(byte[] reorder) {
            super(VLENGTH, reorder);
        }

        public Halffloat64Shuffle(int[] reorder) {
            super(VLENGTH, reorder);
        }

        public Halffloat64Shuffle(int[] reorder, int i) {
            super(VLENGTH, reorder, i);
        }

        public Halffloat64Shuffle(IntUnaryOperator fn) {
            super(VLENGTH, fn);
        }

        @Override
        public HalffloatSpecies vspecies() {
            return VSPECIES;
        }

        static {
            // There must be enough bits in the shuffle lanes to encode
            // VLENGTH valid indexes and VLENGTH exceptional ones.
            assert(VLENGTH < Byte.MAX_VALUE);
            assert(Byte.MIN_VALUE <= -VLENGTH);
        }
        static final Halffloat64Shuffle IOTA = new Halffloat64Shuffle(IDENTITY);

        @Override
        @ForceInline
        public Halffloat64Vector toVector() {
            return VectorSupport.shuffleToVector(VCLASS, ETYPE, Halffloat64Shuffle.class, this, VLENGTH,
                                                    (s) -> ((Halffloat64Vector)(((AbstractShuffle<Halffloat>)(s)).toVectorTemplate())));
        }

        @Override
        @ForceInline
        public <F> VectorShuffle<F> cast(VectorSpecies<F> s) {
            AbstractSpecies<F> species = (AbstractSpecies<F>) s;
            if (length() != species.laneCount())
                throw new IllegalArgumentException("VectorShuffle length and species length differ");
            int[] shuffleArray = toArray();
            return s.shuffleFromArray(shuffleArray, 0).check(s);
        }

        @ForceInline
        @Override
        public Halffloat64Shuffle rearrange(VectorShuffle<Halffloat> shuffle) {
            Halffloat64Shuffle s = (Halffloat64Shuffle) shuffle;
            byte[] reorder1 = reorder();
            byte[] reorder2 = s.reorder();
            byte[] r = new byte[reorder1.length];
            for (int i = 0; i < reorder1.length; i++) {
                int ssi = reorder2[i];
                r[i] = reorder1[ssi];  // throws on exceptional index
            }
            return new Halffloat64Shuffle(r);
        }
    }

    // ================================================

    // Specialized low-level memory operations.

    @ForceInline
    @Override
    final
    HalffloatVector fromArray0(short[] a, int offset) {
        return super.fromArray0Template(a, offset);  // specialize
    }

    @ForceInline
    @Override
    final
    HalffloatVector fromArray0(short[] a, int offset, VectorMask<Halffloat> m) {
        return super.fromArray0Template(Halffloat64Mask.class, a, offset, (Halffloat64Mask) m);  // specialize
    }


    @ForceInline
    @Override
    final
    HalffloatVector fromCharArray0(char[] a, int offset) {
        return super.fromCharArray0Template(a, offset);  // specialize
    }

    @ForceInline
    @Override
    final
    HalffloatVector fromCharArray0(char[] a, int offset, VectorMask<Halffloat> m) {
        return super.fromCharArray0Template(Halffloat64Mask.class, a, offset, (Halffloat64Mask) m);  // specialize
    }


    @ForceInline
    @Override
    final
    HalffloatVector fromByteArray0(byte[] a, int offset) {
        return super.fromByteArray0Template(a, offset);  // specialize
    }

    @ForceInline
    @Override
    final
    HalffloatVector fromByteArray0(byte[] a, int offset, VectorMask<Halffloat> m) {
        return super.fromByteArray0Template(Halffloat64Mask.class, a, offset, (Halffloat64Mask) m);  // specialize
    }

    @ForceInline
    @Override
    final
    HalffloatVector fromByteBuffer0(ByteBuffer bb, int offset) {
        return super.fromByteBuffer0Template(bb, offset);  // specialize
    }

    @ForceInline
    @Override
    final
    HalffloatVector fromByteBuffer0(ByteBuffer bb, int offset, VectorMask<Halffloat> m) {
        return super.fromByteBuffer0Template(Halffloat64Mask.class, bb, offset, (Halffloat64Mask) m);  // specialize
    }

    @ForceInline
    @Override
    final
    void intoArray0(short[] a, int offset) {
        super.intoArray0Template(a, offset);  // specialize
    }

    @ForceInline
    @Override
    final
    void intoArray0(short[] a, int offset, VectorMask<Halffloat> m) {
        super.intoArray0Template(Halffloat64Mask.class, a, offset, (Halffloat64Mask) m);
    }



    @ForceInline
    @Override
    final
    void intoByteArray0(byte[] a, int offset) {
        super.intoByteArray0Template(a, offset);  // specialize
    }

    @ForceInline
    @Override
    final
    void intoByteArray0(byte[] a, int offset, VectorMask<Halffloat> m) {
        super.intoByteArray0Template(Halffloat64Mask.class, a, offset, (Halffloat64Mask) m);  // specialize
    }

    @ForceInline
    @Override
    final
    void intoByteBuffer0(ByteBuffer bb, int offset, VectorMask<Halffloat> m) {
        super.intoByteBuffer0Template(Halffloat64Mask.class, bb, offset, (Halffloat64Mask) m);
    }

    @ForceInline
    @Override
    final
    void intoCharArray0(char[] a, int offset, VectorMask<Halffloat> m) {
        super.intoCharArray0Template(Halffloat64Mask.class, a, offset, (Halffloat64Mask) m);
    }

    // End of specialized low-level memory operations.

    // ================================================

}
