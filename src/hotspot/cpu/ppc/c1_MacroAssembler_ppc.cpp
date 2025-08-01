/*
 * Copyright (c) 1999, 2025, Oracle and/or its affiliates. All rights reserved.
 * Copyright (c) 2012, 2025 SAP SE. All rights reserved.
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
 *
 */

#include "asm/macroAssembler.inline.hpp"
#include "c1/c1_MacroAssembler.hpp"
#include "c1/c1_Runtime1.hpp"
#include "gc/shared/collectedHeap.hpp"
#include "gc/shared/tlab_globals.hpp"
#include "interpreter/interpreter.hpp"
#include "oops/arrayOop.hpp"
#include "oops/markWord.hpp"
#include "runtime/basicLock.hpp"
#include "runtime/os.hpp"
#include "runtime/sharedRuntime.hpp"
#include "runtime/stubRoutines.hpp"
#include "utilities/align.hpp"
#include "utilities/macros.hpp"
#include "utilities/powerOfTwo.hpp"


void C1_MacroAssembler::explicit_null_check(Register base) {
  Unimplemented();
}


void C1_MacroAssembler::build_frame(int frame_size_in_bytes, int bang_size_in_bytes) {
  const Register return_pc = R20;
  mflr(return_pc);

  // Make sure there is enough stack space for this method's activation.
  assert(bang_size_in_bytes >= frame_size_in_bytes, "stack bang size incorrect");
  generate_stack_overflow_check(bang_size_in_bytes);

  std(return_pc, _abi0(lr), R1_SP);     // SP->lr = return_pc
  push_frame(frame_size_in_bytes, R0); // SP -= frame_size_in_bytes

  BarrierSetAssembler* bs = BarrierSet::barrier_set()->barrier_set_assembler();
  bs->nmethod_entry_barrier(this, R20);
}


void C1_MacroAssembler::verified_entry(bool breakAtEntry) {
  if (breakAtEntry) illtrap();
  // build frame
}


void C1_MacroAssembler::lock_object(Register Rmark, Register Roop, Register Rbox, Register Rscratch, Label& slow_case) {
  assert_different_registers(Rmark, Roop, Rbox, Rscratch);

  Label done, cas_failed, slow_int;

  // The following move must be the first instruction of emitted since debug
  // information may be generated for it.
  // Load object header.
  ld(Rmark, oopDesc::mark_offset_in_bytes(), Roop);

  verify_oop(Roop, FILE_AND_LINE);

  // Save object being locked into the BasicObjectLock...
  std(Roop, in_bytes(BasicObjectLock::obj_offset()), Rbox);

  if (LockingMode == LM_LIGHTWEIGHT) {
    lightweight_lock(Rbox, Roop, Rmark, Rscratch, slow_int);
  } else if (LockingMode == LM_LEGACY) {

    if (DiagnoseSyncOnValueBasedClasses != 0) {
      load_klass(Rscratch, Roop);
      lbz(Rscratch, in_bytes(Klass::misc_flags_offset()), Rscratch);
      testbitdi(CR0, R0, Rscratch, exact_log2(KlassFlags::_misc_is_value_based_class));
      bne(CR0, slow_int);
    }

    // ... and mark it unlocked.
    ori(Rmark, Rmark, markWord::unlocked_value);

    // Save unlocked object header into the displaced header location on the stack.
    std(Rmark, BasicLock::displaced_header_offset_in_bytes(), Rbox);

    // Compare object markWord with Rmark and if equal exchange Rscratch with object markWord.
    assert(oopDesc::mark_offset_in_bytes() == 0, "cas must take a zero displacement");
    cmpxchgd(/*flag=*/CR0,
             /*current_value=*/Rscratch,
             /*compare_value=*/Rmark,
             /*exchange_value=*/Rbox,
             /*where=*/Roop/*+0==mark_offset_in_bytes*/,
             MacroAssembler::MemBarRel | MacroAssembler::MemBarAcq,
             MacroAssembler::cmpxchgx_hint_acquire_lock(),
             noreg,
             &cas_failed,
             /*check without membar and ldarx first*/true);
    // If compare/exchange succeeded we found an unlocked object and we now have locked it
    // hence we are done.
  } else {
    assert(false, "Unhandled LockingMode:%d", LockingMode);
  }
  b(done);

  bind(slow_int);
  b(slow_case); // far

  if (LockingMode == LM_LEGACY) {
    bind(cas_failed);
    // We did not find an unlocked object so see if this is a recursive case.
    sub(Rscratch, Rscratch, R1_SP);
    load_const_optimized(R0, (~(os::vm_page_size()-1) | markWord::lock_mask_in_place));
    and_(R0/*==0?*/, Rscratch, R0);
    std(R0/*==0, perhaps*/, BasicLock::displaced_header_offset_in_bytes(), Rbox);
    bne(CR0, slow_int);
  }

  bind(done);
  if (LockingMode == LM_LEGACY) {
    inc_held_monitor_count(Rmark /*tmp*/);
  }
}


void C1_MacroAssembler::unlock_object(Register Rmark, Register Roop, Register Rbox, Label& slow_case) {
  assert_different_registers(Rmark, Roop, Rbox);

  Label slow_int, done;

  Address mark_addr(Roop, oopDesc::mark_offset_in_bytes());
  assert(mark_addr.disp() == 0, "cas must take a zero displacement");

  if (LockingMode != LM_LIGHTWEIGHT) {
    // Test first if it is a fast recursive unlock.
    ld(Rmark, BasicLock::displaced_header_offset_in_bytes(), Rbox);
    cmpdi(CR0, Rmark, 0);
    beq(CR0, done);
  }

  // Load object.
  ld(Roop, in_bytes(BasicObjectLock::obj_offset()), Rbox);
  verify_oop(Roop, FILE_AND_LINE);

  if (LockingMode == LM_LIGHTWEIGHT) {
    lightweight_unlock(Roop, Rmark, slow_int);
  } else if (LockingMode == LM_LEGACY) {
    // Check if it is still a light weight lock, this is is true if we see
    // the stack address of the basicLock in the markWord of the object.
    cmpxchgd(/*flag=*/CR0,
             /*current_value=*/R0,
             /*compare_value=*/Rbox,
             /*exchange_value=*/Rmark,
             /*where=*/Roop,
             MacroAssembler::MemBarRel,
             MacroAssembler::cmpxchgx_hint_release_lock(),
             noreg,
             &slow_int);
  } else {
    assert(false, "Unhandled LockingMode:%d", LockingMode);
  }
  b(done);
  bind(slow_int);
  b(slow_case); // far

  // Done
  bind(done);
  if (LockingMode == LM_LEGACY) {
    dec_held_monitor_count(Rmark /*tmp*/);
  }
}


void C1_MacroAssembler::try_allocate(
  Register obj,                        // result: pointer to object after successful allocation
  Register var_size_in_bytes,          // object size in bytes if unknown at compile time; invalid otherwise
  int      con_size_in_bytes,          // object size in bytes if   known at compile time
  Register t1,                         // temp register
  Register t2,                         // temp register
  Label&   slow_case                   // continuation point if fast allocation fails
) {
  if (UseTLAB) {
    tlab_allocate(obj, var_size_in_bytes, con_size_in_bytes, t1, slow_case);
  } else {
    b(slow_case);
  }
}


void C1_MacroAssembler::initialize_header(Register obj, Register klass, Register len, Register t1, Register t2) {
  assert_different_registers(obj, klass, len, t1, t2);

  if (UseCompactObjectHeaders) {
    ld(t1, in_bytes(Klass::prototype_header_offset()), klass);
    std(t1, oopDesc::mark_offset_in_bytes(), obj);
  } else {
    load_const_optimized(t1, (intx)markWord::prototype().value());
    std(t1, oopDesc::mark_offset_in_bytes(), obj);
    store_klass(obj, klass);
  }

  if (len->is_valid()) {
    stw(len, arrayOopDesc::length_offset_in_bytes(), obj);
  } else if (UseCompressedClassPointers && !UseCompactObjectHeaders) {
    // Otherwise length is in the class gap.
    store_klass_gap(obj);
  }
}


void C1_MacroAssembler::initialize_body(Register base, Register index) {
  assert_different_registers(base, index);
  srdi(index, index, LogBytesPerWord);
  clear_memory_doubleword(base, index);
}

void C1_MacroAssembler::initialize_body(Register obj, Register tmp1, Register tmp2,
                                        int obj_size_in_bytes, int hdr_size_in_bytes) {
  const int index = (obj_size_in_bytes - hdr_size_in_bytes) / HeapWordSize;

  // 2x unrolled loop is shorter with more than 9 HeapWords.
  if (index <= 9) {
    clear_memory_unrolled(obj, index, R0, hdr_size_in_bytes);
  } else {
    const Register base_ptr = tmp1,
                   cnt_dwords = tmp2;

    addi(base_ptr, obj, hdr_size_in_bytes); // Compute address of first element.
    clear_memory_doubleword(base_ptr, cnt_dwords, R0, index);
  }
}

void C1_MacroAssembler::allocate_object(
  Register obj,                        // result: pointer to object after successful allocation
  Register t1,                         // temp register
  Register t2,                         // temp register
  Register t3,                         // temp register
  int      hdr_size,                   // object header size in words
  int      obj_size,                   // object size in words
  Register klass,                      // object klass
  Label&   slow_case                   // continuation point if fast allocation fails
) {
  assert_different_registers(obj, t1, t2, t3, klass);

  // allocate space & initialize header
  if (!is_simm16(obj_size * wordSize)) {
    // Would need to use extra register to load
    // object size => go the slow case for now.
    b(slow_case);
    return;
  }
  try_allocate(obj, noreg, obj_size * wordSize, t2, t3, slow_case);

  initialize_object(obj, klass, noreg, obj_size * HeapWordSize, t1, t2);
}

void C1_MacroAssembler::initialize_object(
  Register obj,                        // result: pointer to object after successful allocation
  Register klass,                      // object klass
  Register var_size_in_bytes,          // object size in bytes if unknown at compile time; invalid otherwise
  int      con_size_in_bytes,          // object size in bytes if   known at compile time
  Register t1,                         // temp register
  Register t2                          // temp register
  ) {
  const int hdr_size_in_bytes = instanceOopDesc::header_size() * HeapWordSize;

  initialize_header(obj, klass, noreg, t1, t2);

#ifdef ASSERT
  {
    lwz(t1, in_bytes(Klass::layout_helper_offset()), klass);
    if (var_size_in_bytes != noreg) {
      cmpw(CR0, t1, var_size_in_bytes);
    } else {
      cmpwi(CR0, t1, con_size_in_bytes);
    }
    asm_assert_eq("bad size in initialize_object");
  }
#endif

  // Initialize body.
  if (var_size_in_bytes != noreg) {
    // Use a loop.
    addi(t1, obj, hdr_size_in_bytes);                // Compute address of first element.
    addi(t2, var_size_in_bytes, -hdr_size_in_bytes); // Compute size of body.
    initialize_body(t1, t2);
  } else if (con_size_in_bytes > hdr_size_in_bytes) {
    // Use a loop.
    initialize_body(obj, t1, t2, con_size_in_bytes, hdr_size_in_bytes);
  }

  if (CURRENT_ENV->dtrace_alloc_probes()) {
    Unimplemented();
//    assert(obj == O0, "must be");
//    call(CAST_FROM_FN_PTR(address, Runtime1::entry_for(StubId::c1_dtrace_object_alloc_id)),
//         relocInfo::runtime_call_type);
  }

  verify_oop(obj, FILE_AND_LINE);
}


void C1_MacroAssembler::allocate_array(
  Register obj,                        // result: pointer to array after successful allocation
  Register len,                        // array length
  Register t1,                         // temp register
  Register t2,                         // temp register
  Register t3,                         // temp register
  int      base_offset_in_bytes,       // elements offset in bytes
  int      elt_size,                   // element size in bytes
  Register klass,                      // object klass
  Label&   slow_case,                  // continuation point if fast allocation fails
  bool     zero_array                  // zero the allocated array or not
) {
  assert_different_registers(obj, len, t1, t2, t3, klass);

  // Determine alignment mask.
  assert(!(BytesPerWord & 1), "must be a multiple of 2 for masking code to work");
  int log2_elt_size = exact_log2(elt_size);

  // Check for negative or excessive length.
  size_t max_length = max_array_allocation_length >> log2_elt_size;
  if (UseTLAB) {
    size_t max_tlab = align_up(ThreadLocalAllocBuffer::max_size() >> log2_elt_size, 64*K);
    if (max_tlab < max_length) { max_length = max_tlab; }
  }
  load_const_optimized(t1, max_length);
  cmpld(CR0, len, t1);
  bc_far_optimized(Assembler::bcondCRbiIs1, bi0(CR0, Assembler::greater), slow_case);

  // compute array size
  // note: If 0 <= len <= max_length, len*elt_size + header + alignment is
  //       smaller or equal to the largest integer; also, since top is always
  //       aligned, we can do the alignment here instead of at the end address
  //       computation.
  const Register arr_size = t1;
  Register arr_len_in_bytes = len;
  if (elt_size != 1) {
    sldi(t1, len, log2_elt_size);
    arr_len_in_bytes = t1;
  }
  addi(arr_size, arr_len_in_bytes, base_offset_in_bytes + MinObjAlignmentInBytesMask); // Add space for header & alignment.
  clrrdi(arr_size, arr_size, LogMinObjAlignmentInBytes);                              // Align array size.

  // Allocate space & initialize header.
  try_allocate(obj, arr_size, 0, t2, t3, slow_case);
  initialize_header(obj, klass, len, t2, t3);

  if (zero_array) {
    // Initialize body.
    const Register base  = t2;
    const Register index = t3;
    addi(base, obj, base_offset_in_bytes);               // compute address of first element
    addi(index, arr_size, -(base_offset_in_bytes));      // compute index = number of bytes to clear

    // Zero first 4 bytes, if start offset is not word aligned.
    if (!is_aligned(base_offset_in_bytes, BytesPerWord)) {
      assert(is_aligned(base_offset_in_bytes, BytesPerInt), "must be 4-byte aligned");
      li(t1, 0);
      stw(t1, 0, base);
      addi(base, base, BytesPerInt);
      // Note: initialize_body will align index down, no need to correct it here.
    }

    initialize_body(base, index);
  }

  if (CURRENT_ENV->dtrace_alloc_probes()) {
    Unimplemented();
    //assert(obj == O0, "must be");
    //call(CAST_FROM_FN_PTR(address, Runtime1::entry_for(StubId::c1_dtrace_object_alloc_id)),
    //     relocInfo::runtime_call_type);
  }

  verify_oop(obj, FILE_AND_LINE);
}


#ifndef PRODUCT

void C1_MacroAssembler::verify_stack_oop(int stack_offset) {
  verify_oop_addr((RegisterOrConstant)stack_offset, R1_SP, "broken oop in stack slot");
}

void C1_MacroAssembler::verify_not_null_oop(Register r) {
  Label not_null;
  cmpdi(CR0, r, 0);
  bne(CR0, not_null);
  stop("non-null oop required");
  bind(not_null);
  verify_oop(r, FILE_AND_LINE);
}

#endif // PRODUCT

void C1_MacroAssembler::null_check(Register r, Label* Lnull) {
  if (TrapBasedNullChecks) { // SIGTRAP based
    trap_null_check(r);
  } else { // explicit
    //const address exception_entry = Runtime1::entry_for(StubId::c1_throw_null_pointer_exception_id);
    assert(Lnull != nullptr, "must have Label for explicit check");
    cmpdi(CR0, r, 0);
    bc_far_optimized(Assembler::bcondCRbiIs1, bi0(CR0, Assembler::equal), *Lnull);
  }
}
