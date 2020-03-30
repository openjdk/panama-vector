package jdk.incubator.vector;

import jdk.internal.misc.Unsafe;
import jdk.internal.vm.annotation.ForceInline;

import java.nio.Buffer;
import java.nio.ByteBuffer;
import java.util.Objects;

/*non-public*/ class VectorIntrinsics {
    static final Unsafe U = Unsafe.getUnsafe();

    static final long BUFFER_ADDRESS
            = U.objectFieldOffset(Buffer.class, "address");

    // Buffer.limit
    static final long BUFFER_LIMIT
            = U.objectFieldOffset(Buffer.class, "limit");

    // ByteBuffer.hb
    static final long BYTE_BUFFER_HB
            = U.objectFieldOffset(ByteBuffer.class, "hb");

    // ByteBuffer.isReadOnly
    static final long BYTE_BUFFER_IS_READ_ONLY
            = U.objectFieldOffset(ByteBuffer.class, "isReadOnly");

    /* ============================================================================ */

    static final int VECTOR_ACCESS_OOB_CHECK = Integer.getInteger("jdk.incubator.vector.VECTOR_ACCESS_OOB_CHECK", 2);

    @ForceInline
    static void requireLength(int haveLength, int length) {
        if (haveLength != length) {
            throw requireLengthFailed(haveLength, length);
        }
    }
    static IllegalArgumentException requireLengthFailed(int haveLength, int length) {
        String msg = String.format("Length check failed: "+
                                   "length %d should have been %s",
                                   haveLength, length);
        return new IllegalArgumentException(msg);
    }

    @ForceInline
    static int checkFromIndexSize(int ix, int vlen, int length) {
        switch (VectorIntrinsics.VECTOR_ACCESS_OOB_CHECK) {
            case 0: return ix; // no range check
            case 1: return Objects.checkFromIndexSize(ix, vlen, length);
            case 2: return Objects.checkIndex(ix, length - (vlen - 1));
            default: throw new InternalError();
        }
    }

    @ForceInline
    static IntVector checkIndex(IntVector vix, int length) {
        switch (VectorIntrinsics.VECTOR_ACCESS_OOB_CHECK) {
            case 0: return vix; // no range check
            case 1: // fall-through
            case 2:
                if (vix.compare(VectorOperators.LT, 0)
                    .or(vix.compare(VectorOperators.GE, length))
                    .anyTrue()) {
                    throw checkIndexFailed(vix, length);
                }
                return vix;
            default: throw new InternalError();
        }
    }

    private static
    IndexOutOfBoundsException checkIndexFailed(IntVector vix, int length) {
        String msg = String.format("Range check failed: vector %s out of bounds for length %d", vix, length);
        return new IndexOutOfBoundsException(msg);
    }

    // If the index is not already a multiple of size,
    // round it down to the next smaller multiple of size.
    // It is an error if size is less than zero.
    @ForceInline
    static int roundDown(int index, int size) {
        if ((size & (size - 1)) == 0) {
            // Size is zero or a power of two, so we got this.
            return index & ~(size - 1);
        } else {
            return roundDownNPOT(index, size);
        }
    }
    private static int roundDownNPOT(int index, int size) {
        if (index >= 0) {
            return index - (index % size);
        } else {
            return index - Math.floorMod(index, Math.abs(size));
        }
    }
    @ForceInline
    static int wrapToRange(int index, int size) {
        if ((size & (size - 1)) == 0) {
            // Size is zero or a power of two, so we got this.
            return index & (size - 1);
        } else {
            return wrapToRangeNPOT(index, size);
        }
    }
    private static int wrapToRangeNPOT(int index, int size) {
        if (index >= 0) {
            return (index % size);
        } else {
            return Math.floorMod(index, Math.abs(size));
        }
    }

    /* ============================================================================ */

    /*package-private*/
    @ForceInline
    static Object bufferBase(ByteBuffer bb) {
        return U.getReference(bb, BYTE_BUFFER_HB);
    }

    /*package-private*/
    @ForceInline
    static long bufferAddress(ByteBuffer bb, long offset) {
        return U.getLong(bb, BUFFER_ADDRESS) + offset;
    }
}
