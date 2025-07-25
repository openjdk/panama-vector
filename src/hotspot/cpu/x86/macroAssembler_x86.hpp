/*
 * Copyright (c) 1997, 2025, Oracle and/or its affiliates. All rights reserved.
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

#ifndef CPU_X86_MACROASSEMBLER_X86_HPP
#define CPU_X86_MACROASSEMBLER_X86_HPP

#include "asm/assembler.hpp"
#include "asm/register.hpp"
#include "code/vmreg.inline.hpp"
#include "compiler/oopMap.hpp"
#include "utilities/macros.hpp"
#include "runtime/vm_version.hpp"
#include "utilities/checkedCast.hpp"

// MacroAssembler extends Assembler by frequently used macros.
//
// Instructions for which a 'better' code sequence exists depending
// on arguments should also go in here.

class MacroAssembler: public Assembler {
  friend class LIR_Assembler;
  friend class Runtime1;      // as_Address()

 public:
  // Support for VM calls
  //
  // This is the base routine called by the different versions of call_VM_leaf. The interpreter
  // may customize this version by overriding it for its purposes (e.g., to save/restore
  // additional registers when doing a VM call).

  virtual void call_VM_leaf_base(
    address entry_point,               // the entry point
    int     number_of_arguments        // the number of arguments to pop after the call
  );

 protected:
  // This is the base routine called by the different versions of call_VM. The interpreter
  // may customize this version by overriding it for its purposes (e.g., to save/restore
  // additional registers when doing a VM call).
  //
  // call_VM_base returns the register which contains the thread upon return.
  // If no last_java_sp is specified (noreg) than rsp will be used instead.
  virtual void call_VM_base(           // returns the register containing the thread upon return
    Register oop_result,               // where an oop-result ends up if any; use noreg otherwise
    Register last_java_sp,             // to set up last_Java_frame in stubs; use noreg otherwise
    address  entry_point,              // the entry point
    int      number_of_arguments,      // the number of arguments (w/o thread) to pop after the call
    bool     check_exceptions          // whether to check for pending exceptions after return
  );

  void call_VM_helper(Register oop_result, address entry_point, int number_of_arguments, bool check_exceptions = true);

 public:
  MacroAssembler(CodeBuffer* code) : Assembler(code) {}

 // These routines should emit JVMTI PopFrame and ForceEarlyReturn handling code.
 // The implementation is only non-empty for the InterpreterMacroAssembler,
 // as only the interpreter handles PopFrame and ForceEarlyReturn requests.
 virtual void check_and_handle_popframe();
 virtual void check_and_handle_earlyret();

  Address as_Address(AddressLiteral adr);
  Address as_Address(ArrayAddress adr, Register rscratch);

  // Support for null-checks
  //
  // Generates code that causes a null OS exception if the content of reg is null.
  // If the accessed location is M[reg + offset] and the offset is known, provide the
  // offset. No explicit code generation is needed if the offset is within a certain
  // range (0 <= offset <= page_size).

  void null_check(Register reg, int offset = -1);
  static bool needs_explicit_null_check(intptr_t offset);
  static bool uses_implicit_null_check(void* address);

  // Required platform-specific helpers for Label::patch_instructions.
  // They _shadow_ the declarations in AbstractAssembler, which are undefined.
  void pd_patch_instruction(address branch, address target, const char* file, int line) {
    unsigned char op = branch[0];
    assert(op == 0xE8 /* call */ ||
        op == 0xE9 /* jmp */ ||
        op == 0xEB /* short jmp */ ||
        (op & 0xF0) == 0x70 /* short jcc */ ||
        (op == 0x0F && (branch[1] & 0xF0) == 0x80) /* jcc */ ||
        (op == 0xC7 && branch[1] == 0xF8) /* xbegin */ ||
        (op == 0x8D) /* lea */,
        "Invalid opcode at patch point");

    if (op == 0xEB || (op & 0xF0) == 0x70) {
      // short offset operators (jmp and jcc)
      char* disp = (char*) &branch[1];
      int imm8 = checked_cast<int>(target - (address) &disp[1]);
      guarantee(this->is8bit(imm8), "Short forward jump exceeds 8-bit offset at %s:%d",
                file == nullptr ? "<null>" : file, line);
      *disp = (char)imm8;
    } else {
      int* disp = (int*) &branch[(op == 0x0F || op == 0xC7 || op == 0x8D) ? 2 : 1];
      int imm32 = checked_cast<int>(target - (address) &disp[1]);
      *disp = imm32;
    }
  }

  // The following 4 methods return the offset of the appropriate move instruction

  // Support for fast byte/short loading with zero extension (depending on particular CPU)
  int load_unsigned_byte(Register dst, Address src);
  int load_unsigned_short(Register dst, Address src);

  // Support for fast byte/short loading with sign extension (depending on particular CPU)
  int load_signed_byte(Register dst, Address src);
  int load_signed_short(Register dst, Address src);

  // Support for sign-extension (hi:lo = extend_sign(lo))
  void extend_sign(Register hi, Register lo);

  // Load and store values by size and signed-ness
  void load_sized_value(Register dst, Address src, size_t size_in_bytes, bool is_signed, Register dst2 = noreg);
  void store_sized_value(Address dst, Register src, size_t size_in_bytes, Register src2 = noreg);

  // Support for inc/dec with optimal instruction selection depending on value

  void increment(Register reg, int value = 1) { incrementq(reg, value); }
  void decrement(Register reg, int value = 1) { decrementq(reg, value); }
  void increment(Address dst, int value = 1)  { incrementq(dst, value); }
  void decrement(Address dst, int value = 1)  { decrementq(dst, value); }

  void decrementl(Address dst, int value = 1);
  void decrementl(Register reg, int value = 1);

  void decrementq(Register reg, int value = 1);
  void decrementq(Address dst, int value = 1);

  void incrementl(Address dst, int value = 1);
  void incrementl(Register reg, int value = 1);

  void incrementq(Register reg, int value = 1);
  void incrementq(Address dst, int value = 1);

  void incrementl(AddressLiteral dst, Register rscratch = noreg);
  void incrementl(ArrayAddress   dst, Register rscratch);

  void incrementq(AddressLiteral dst, Register rscratch = noreg);

  // Support optimal SSE move instructions.
  void movflt(XMMRegister dst, XMMRegister src) {
    if (dst-> encoding() == src->encoding()) return;
    if (UseXmmRegToRegMoveAll) { movaps(dst, src); return; }
    else                       { movss (dst, src); return; }
  }
  void movflt(XMMRegister dst, Address src) { movss(dst, src); }
  void movflt(XMMRegister dst, AddressLiteral src, Register rscratch = noreg);
  void movflt(Address dst, XMMRegister src) { movss(dst, src); }

  // Move with zero extension
  void movfltz(XMMRegister dst, XMMRegister src) { movss(dst, src); }

  void movdbl(XMMRegister dst, XMMRegister src) {
    if (dst-> encoding() == src->encoding()) return;
    if (UseXmmRegToRegMoveAll) { movapd(dst, src); return; }
    else                       { movsd (dst, src); return; }
  }

  void movdbl(XMMRegister dst, AddressLiteral src, Register rscratch = noreg);

  void movdbl(XMMRegister dst, Address src) {
    if (UseXmmLoadAndClearUpper) { movsd (dst, src); return; }
    else                         { movlpd(dst, src); return; }
  }
  void movdbl(Address dst, XMMRegister src) { movsd(dst, src); }

  void flt_to_flt16(Register dst, XMMRegister src, XMMRegister tmp) {
    // Use separate tmp XMM register because caller may
    // requires src XMM register to be unchanged (as in x86.ad).
    vcvtps2ph(tmp, src, 0x04, Assembler::AVX_128bit);
    movdl(dst, tmp);
    movswl(dst, dst);
  }

  void flt16_to_flt(XMMRegister dst, Register src) {
    movdl(dst, src);
    vcvtph2ps(dst, dst, Assembler::AVX_128bit);
  }

  // Alignment
  void align32();
  void align64();
  void align(uint modulus);
  void align(uint modulus, uint target);

  void post_call_nop();

  // Stack frame creation/removal
  void enter();
  void leave();

  // Support for getting the JavaThread pointer (i.e.; a reference to thread-local information).
  // The pointer will be loaded into the thread register. This is a slow version that does native call.
  // Normally, JavaThread pointer is available in r15_thread, use that where possible.
  void get_thread_slow(Register thread);

  // Support for argument shuffling

  // bias in bytes
  void move32_64(VMRegPair src, VMRegPair dst, Register tmp = rax, int in_stk_bias = 0, int out_stk_bias = 0);
  void long_move(VMRegPair src, VMRegPair dst, Register tmp = rax, int in_stk_bias = 0, int out_stk_bias = 0);
  void float_move(VMRegPair src, VMRegPair dst, Register tmp = rax, int in_stk_bias = 0, int out_stk_bias = 0);
  void double_move(VMRegPair src, VMRegPair dst, Register tmp = rax, int in_stk_bias = 0, int out_stk_bias = 0);
  void move_ptr(VMRegPair src, VMRegPair dst);
  void object_move(OopMap* map,
                   int oop_handle_offset,
                   int framesize_in_slots,
                   VMRegPair src,
                   VMRegPair dst,
                   bool is_receiver,
                   int* receiver_offset);

  // Support for VM calls
  //
  // It is imperative that all calls into the VM are handled via the call_VM macros.
  // They make sure that the stack linkage is setup correctly. call_VM's correspond
  // to ENTRY/ENTRY_X entry points while call_VM_leaf's correspond to LEAF entry points.


  void call_VM(Register oop_result,
               address entry_point,
               bool check_exceptions = true);
  void call_VM(Register oop_result,
               address entry_point,
               Register arg_1,
               bool check_exceptions = true);
  void call_VM(Register oop_result,
               address entry_point,
               Register arg_1, Register arg_2,
               bool check_exceptions = true);
  void call_VM(Register oop_result,
               address entry_point,
               Register arg_1, Register arg_2, Register arg_3,
               bool check_exceptions = true);

  // Overloadings with last_Java_sp
  void call_VM(Register oop_result,
               Register last_java_sp,
               address entry_point,
               int number_of_arguments = 0,
               bool check_exceptions = true);
  void call_VM(Register oop_result,
               Register last_java_sp,
               address entry_point,
               Register arg_1, bool
               check_exceptions = true);
  void call_VM(Register oop_result,
               Register last_java_sp,
               address entry_point,
               Register arg_1, Register arg_2,
               bool check_exceptions = true);
  void call_VM(Register oop_result,
               Register last_java_sp,
               address entry_point,
               Register arg_1, Register arg_2, Register arg_3,
               bool check_exceptions = true);

  void get_vm_result_oop(Register oop_result);
  void get_vm_result_metadata(Register metadata_result);

  // These always tightly bind to MacroAssembler::call_VM_base
  // bypassing the virtual implementation
  void super_call_VM(Register oop_result, Register last_java_sp, address entry_point, int number_of_arguments = 0, bool check_exceptions = true);
  void super_call_VM(Register oop_result, Register last_java_sp, address entry_point, Register arg_1, bool check_exceptions = true);
  void super_call_VM(Register oop_result, Register last_java_sp, address entry_point, Register arg_1, Register arg_2, bool check_exceptions = true);
  void super_call_VM(Register oop_result, Register last_java_sp, address entry_point, Register arg_1, Register arg_2, Register arg_3, bool check_exceptions = true);
  void super_call_VM(Register oop_result, Register last_java_sp, address entry_point, Register arg_1, Register arg_2, Register arg_3, Register arg_4, bool check_exceptions = true);

  void call_VM_leaf0(address entry_point);
  void call_VM_leaf(address entry_point,
                    int number_of_arguments = 0);
  void call_VM_leaf(address entry_point,
                    Register arg_1);
  void call_VM_leaf(address entry_point,
                    Register arg_1, Register arg_2);
  void call_VM_leaf(address entry_point,
                    Register arg_1, Register arg_2, Register arg_3);

  void call_VM_leaf(address entry_point,
                    Register arg_1, Register arg_2, Register arg_3, Register arg_4);

  // These always tightly bind to MacroAssembler::call_VM_leaf_base
  // bypassing the virtual implementation
  void super_call_VM_leaf(address entry_point);
  void super_call_VM_leaf(address entry_point, Register arg_1);
  void super_call_VM_leaf(address entry_point, Register arg_1, Register arg_2);
  void super_call_VM_leaf(address entry_point, Register arg_1, Register arg_2, Register arg_3);
  void super_call_VM_leaf(address entry_point, Register arg_1, Register arg_2, Register arg_3, Register arg_4);

  void set_last_Java_frame(Register last_java_sp,
                           Register last_java_fp,
                           address  last_java_pc,
                           Register rscratch);

  void set_last_Java_frame(Register last_java_sp,
                           Register last_java_fp,
                           Label &last_java_pc,
                           Register scratch);

  void reset_last_Java_frame(bool clear_fp);

  // jobjects
  void clear_jobject_tag(Register possibly_non_local);
  void resolve_jobject(Register value, Register tmp);
  void resolve_global_jobject(Register value, Register tmp);

  // C 'boolean' to Java boolean: x == 0 ? 0 : 1
  void c2bool(Register x);

  // C++ bool manipulation

  void movbool(Register dst, Address src);
  void movbool(Address dst, bool boolconst);
  void movbool(Address dst, Register src);
  void testbool(Register dst);

  void resolve_oop_handle(Register result, Register tmp);
  void resolve_weak_handle(Register result, Register tmp);
  void load_mirror(Register mirror, Register method, Register tmp);
  void load_method_holder_cld(Register rresult, Register rmethod);

  void load_method_holder(Register holder, Register method);

  // oop manipulations
  void load_narrow_klass_compact(Register dst, Register src);
  void load_klass(Register dst, Register src, Register tmp);
  void store_klass(Register dst, Register src, Register tmp);

  // Compares the Klass pointer of an object to a given Klass (which might be narrow,
  // depending on UseCompressedClassPointers).
  void cmp_klass(Register klass, Register obj, Register tmp);

  // Compares the Klass pointer of two objects obj1 and obj2. Result is in the condition flags.
  // Uses tmp1 and tmp2 as temporary registers.
  void cmp_klasses_from_objects(Register obj1, Register obj2, Register tmp1, Register tmp2);

  void access_load_at(BasicType type, DecoratorSet decorators, Register dst, Address src,
                      Register tmp1);
  void access_store_at(BasicType type, DecoratorSet decorators, Address dst, Register val,
                       Register tmp1, Register tmp2, Register tmp3);

  void load_heap_oop(Register dst, Address src, Register tmp1 = noreg, DecoratorSet decorators = 0);
  void load_heap_oop_not_null(Register dst, Address src, Register tmp1 = noreg, DecoratorSet decorators = 0);
  void store_heap_oop(Address dst, Register val, Register tmp1 = noreg,
                      Register tmp2 = noreg, Register tmp3 = noreg, DecoratorSet decorators = 0);

  // Used for storing null. All other oop constants should be
  // stored using routines that take a jobject.
  void store_heap_oop_null(Address dst);

  void store_klass_gap(Register dst, Register src);

  // This dummy is to prevent a call to store_heap_oop from
  // converting a zero (like null) into a Register by giving
  // the compiler two choices it can't resolve

  void store_heap_oop(Address dst, void* dummy);

  void encode_heap_oop(Register r);
  void decode_heap_oop(Register r);
  void encode_heap_oop_not_null(Register r);
  void decode_heap_oop_not_null(Register r);
  void encode_heap_oop_not_null(Register dst, Register src);
  void decode_heap_oop_not_null(Register dst, Register src);

  void set_narrow_oop(Register dst, jobject obj);
  void set_narrow_oop(Address dst, jobject obj);
  void cmp_narrow_oop(Register dst, jobject obj);
  void cmp_narrow_oop(Address dst, jobject obj);

  void encode_klass_not_null(Register r, Register tmp);
  void decode_klass_not_null(Register r, Register tmp);
  void encode_and_move_klass_not_null(Register dst, Register src);
  void decode_and_move_klass_not_null(Register dst, Register src);
  void set_narrow_klass(Register dst, Klass* k);
  void set_narrow_klass(Address dst, Klass* k);
  void cmp_narrow_klass(Register dst, Klass* k);
  void cmp_narrow_klass(Address dst, Klass* k);

  // if heap base register is used - reinit it with the correct value
  void reinit_heapbase();

  DEBUG_ONLY(void verify_heapbase(const char* msg);)

  // Int division/remainder for Java
  // (as idivl, but checks for special case as described in JVM spec.)
  // returns idivl instruction offset for implicit exception handling
  int corrected_idivl(Register reg);

  // Long division/remainder for Java
  // (as idivq, but checks for special case as described in JVM spec.)
  // returns idivq instruction offset for implicit exception handling
  int corrected_idivq(Register reg);

  void int3();

  // Long operation macros for a 32bit cpu
  // Long negation for Java
  void lneg(Register hi, Register lo);

  // Long multiplication for Java
  // (destroys contents of eax, ebx, ecx and edx)
  void lmul(int x_rsp_offset, int y_rsp_offset); // rdx:rax = x * y

  // Long shifts for Java
  // (semantics as described in JVM spec.)
  void lshl(Register hi, Register lo);                               // hi:lo << (rcx & 0x3f)
  void lshr(Register hi, Register lo, bool sign_extension = false);  // hi:lo >> (rcx & 0x3f)

  // Long compare for Java
  // (semantics as described in JVM spec.)
  void lcmp2int(Register x_hi, Register x_lo, Register y_hi, Register y_lo); // x_hi = lcmp(x, y)


  // misc

  // Sign extension
  void sign_extend_short(Register reg);
  void sign_extend_byte(Register reg);

  // Division by power of 2, rounding towards 0
  void division_with_shift(Register reg, int shift_value);

  // dst = c = a * b + c
  void fmad(XMMRegister dst, XMMRegister a, XMMRegister b, XMMRegister c);
  void fmaf(XMMRegister dst, XMMRegister a, XMMRegister b, XMMRegister c);

  void vfmad(XMMRegister dst, XMMRegister a, XMMRegister b, XMMRegister c, int vector_len);
  void vfmaf(XMMRegister dst, XMMRegister a, XMMRegister b, XMMRegister c, int vector_len);
  void vfmad(XMMRegister dst, XMMRegister a, Address b, XMMRegister c, int vector_len);
  void vfmaf(XMMRegister dst, XMMRegister a, Address b, XMMRegister c, int vector_len);


  // same as fcmp2int, but using SSE2
  void cmpss2int(XMMRegister opr1, XMMRegister opr2, Register dst, bool unordered_is_less);
  void cmpsd2int(XMMRegister opr1, XMMRegister opr2, Register dst, bool unordered_is_less);

  void push_IU_state();
  void pop_IU_state();

  void push_FPU_state();
  void pop_FPU_state();

  void push_CPU_state();
  void pop_CPU_state();

  void push_cont_fastpath();
  void pop_cont_fastpath();

  void inc_held_monitor_count();
  void dec_held_monitor_count();

  DEBUG_ONLY(void stop_if_in_cont(Register cont_reg, const char* name);)

  // Round up to a power of two
  void round_to(Register reg, int modulus);

private:
  // General purpose and XMM registers potentially clobbered by native code; there
  // is no need for FPU or AVX opmask related methods because C1/interpreter
  // - we save/restore FPU state as a whole always
  // - do not care about AVX-512 opmask
  static RegSet call_clobbered_gp_registers();
  static XMMRegSet call_clobbered_xmm_registers();

  void push_set(XMMRegSet set, int offset);
  void pop_set(XMMRegSet set, int offset);

public:
  void push_set(RegSet set, int offset = -1);
  void pop_set(RegSet set, int offset = -1);

  // Push and pop everything that might be clobbered by a native
  // runtime call.
  // Only save the lower 64 bits of each vector register.
  // Additional registers can be excluded in a passed RegSet.
  void push_call_clobbered_registers_except(RegSet exclude, bool save_fpu = true);
  void pop_call_clobbered_registers_except(RegSet exclude, bool restore_fpu = true);

  void push_call_clobbered_registers(bool save_fpu = true) {
    push_call_clobbered_registers_except(RegSet(), save_fpu);
  }
  void pop_call_clobbered_registers(bool restore_fpu = true) {
    pop_call_clobbered_registers_except(RegSet(), restore_fpu);
  }

  // allocation
  void tlab_allocate(
    Register obj,                      // result: pointer to object after successful allocation
    Register var_size_in_bytes,        // object size in bytes if unknown at compile time; invalid otherwise
    int      con_size_in_bytes,        // object size in bytes if   known at compile time
    Register t1,                       // temp register
    Register t2,                       // temp register
    Label&   slow_case                 // continuation point if fast allocation fails
  );
  void zero_memory(Register address, Register length_in_bytes, int offset_in_bytes, Register temp);

  void population_count(Register dst, Register src, Register scratch1, Register scratch2);

  // interface method calling
  void lookup_interface_method(Register recv_klass,
                               Register intf_klass,
                               RegisterOrConstant itable_index,
                               Register method_result,
                               Register scan_temp,
                               Label& no_such_interface,
                               bool return_method = true);

  void lookup_interface_method_stub(Register recv_klass,
                                    Register holder_klass,
                                    Register resolved_klass,
                                    Register method_result,
                                    Register scan_temp,
                                    Register temp_reg2,
                                    Register receiver,
                                    int itable_index,
                                    Label& L_no_such_interface);

  // virtual method calling
  void lookup_virtual_method(Register recv_klass,
                             RegisterOrConstant vtable_index,
                             Register method_result);

  // Test sub_klass against super_klass, with fast and slow paths.

  // The fast path produces a tri-state answer: yes / no / maybe-slow.
  // One of the three labels can be null, meaning take the fall-through.
  // If super_check_offset is -1, the value is loaded up from super_klass.
  // No registers are killed, except temp_reg.
  void check_klass_subtype_fast_path(Register sub_klass,
                                     Register super_klass,
                                     Register temp_reg,
                                     Label* L_success,
                                     Label* L_failure,
                                     Label* L_slow_path,
                RegisterOrConstant super_check_offset = RegisterOrConstant(-1));

  // The rest of the type check; must be wired to a corresponding fast path.
  // It does not repeat the fast path logic, so don't use it standalone.
  // The temp_reg and temp2_reg can be noreg, if no temps are available.
  // Updates the sub's secondary super cache as necessary.
  // If set_cond_codes, condition codes will be Z on success, NZ on failure.
  void check_klass_subtype_slow_path(Register sub_klass,
                                     Register super_klass,
                                     Register temp_reg,
                                     Register temp2_reg,
                                     Label* L_success,
                                     Label* L_failure,
                                     bool set_cond_codes = false);

  // The 64-bit version, which may do a hashed subclass lookup.
  void check_klass_subtype_slow_path(Register sub_klass,
                                     Register super_klass,
                                     Register temp_reg,
                                     Register temp2_reg,
                                     Register temp3_reg,
                                     Register temp4_reg,
                                     Label* L_success,
                                     Label* L_failure);

  // Three parts of a hashed subclass lookup: a simple linear search,
  // a table lookup, and a fallback that does linear probing in the
  // event of a hash collision.
  void check_klass_subtype_slow_path_linear(Register sub_klass,
                                            Register super_klass,
                                            Register temp_reg,
                                            Register temp2_reg,
                                            Label* L_success,
                                            Label* L_failure,
                                            bool set_cond_codes = false);
  void check_klass_subtype_slow_path_table(Register sub_klass,
                                           Register super_klass,
                                           Register temp_reg,
                                           Register temp2_reg,
                                           Register temp3_reg,
                                           Register result_reg,
                                           Label* L_success,
                                           Label* L_failure);
  void hashed_check_klass_subtype_slow_path(Register sub_klass,
                                            Register super_klass,
                                            Register temp_reg,
                                            Label* L_success,
                                            Label* L_failure);

  // As above, but with a constant super_klass.
  // The result is in Register result, not the condition codes.
  void lookup_secondary_supers_table_const(Register sub_klass,
                                           Register super_klass,
                                           Register temp1,
                                           Register temp2,
                                           Register temp3,
                                           Register temp4,
                                           Register result,
                                           u1 super_klass_slot);

  using Assembler::salq;
  void salq(Register dest, Register count);
  using Assembler::rorq;
  void rorq(Register dest, Register count);
  void lookup_secondary_supers_table_var(Register sub_klass,
                                         Register super_klass,
                                         Register temp1,
                                         Register temp2,
                                         Register temp3,
                                         Register temp4,
                                         Register result);

  void lookup_secondary_supers_table_slow_path(Register r_super_klass,
                                               Register r_array_base,
                                               Register r_array_index,
                                               Register r_bitmap,
                                               Register temp1,
                                               Register temp2,
                                               Label* L_success,
                                               Label* L_failure = nullptr);

  void verify_secondary_supers_table(Register r_sub_klass,
                                     Register r_super_klass,
                                     Register expected,
                                     Register temp1,
                                     Register temp2,
                                     Register temp3);

  void repne_scanq(Register addr, Register value, Register count, Register limit,
                   Label* L_success,
                   Label* L_failure = nullptr);

  // If r is valid, return r.
  // If r is invalid, remove a register r2 from available_regs, add r2
  // to regs_to_push, then return r2.
  Register allocate_if_noreg(const Register r,
                             RegSetIterator<Register> &available_regs,
                             RegSet &regs_to_push);

  // Simplified, combined version, good for typical uses.
  // Falls through on failure.
  void check_klass_subtype(Register sub_klass,
                           Register super_klass,
                           Register temp_reg,
                           Label& L_success);

  void clinit_barrier(Register klass,
                      Label* L_fast_path = nullptr,
                      Label* L_slow_path = nullptr);

  // method handles (JSR 292)
  Address argument_address(RegisterOrConstant arg_slot, int extra_slot_offset = 0);

  // Debugging

  // only if +VerifyOops
  void _verify_oop(Register reg, const char* s, const char* file, int line);
  void _verify_oop_addr(Address addr, const char* s, const char* file, int line);

  void _verify_oop_checked(Register reg, const char* s, const char* file, int line) {
    if (VerifyOops) {
      _verify_oop(reg, s, file, line);
    }
  }
  void _verify_oop_addr_checked(Address reg, const char* s, const char* file, int line) {
    if (VerifyOops) {
      _verify_oop_addr(reg, s, file, line);
    }
  }

  // TODO: verify method and klass metadata (compare against vptr?)
  void _verify_method_ptr(Register reg, const char * msg, const char * file, int line) {}
  void _verify_klass_ptr(Register reg, const char * msg, const char * file, int line){}

#define verify_oop(reg) _verify_oop_checked(reg, "broken oop " #reg, __FILE__, __LINE__)
#define verify_oop_msg(reg, msg) _verify_oop_checked(reg, "broken oop " #reg ", " #msg, __FILE__, __LINE__)
#define verify_oop_addr(addr) _verify_oop_addr_checked(addr, "broken oop addr " #addr, __FILE__, __LINE__)
#define verify_method_ptr(reg) _verify_method_ptr(reg, "broken method " #reg, __FILE__, __LINE__)
#define verify_klass_ptr(reg) _verify_klass_ptr(reg, "broken klass " #reg, __FILE__, __LINE__)

  // Verify or restore cpu control state after JNI call
  void restore_cpu_control_state_after_jni(Register rscratch);

  // prints msg, dumps registers and stops execution
  void stop(const char* msg);

  // prints msg and continues
  void warn(const char* msg);

  // dumps registers and other state
  void print_state();

  static void debug32(int rdi, int rsi, int rbp, int rsp, int rbx, int rdx, int rcx, int rax, int eip, char* msg);
  static void debug64(char* msg, int64_t pc, int64_t regs[]);
  static void print_state32(int rdi, int rsi, int rbp, int rsp, int rbx, int rdx, int rcx, int rax, int eip);
  static void print_state64(int64_t pc, int64_t regs[]);

  void os_breakpoint();

  void untested()                                { stop("untested"); }

  void unimplemented(const char* what = "");

  void should_not_reach_here()                   { stop("should not reach here"); }

  void print_CPU_state();

  // Stack overflow checking
  void bang_stack_with_offset(int offset) {
    // stack grows down, caller passes positive offset
    assert(offset > 0, "must bang with negative offset");
    movl(Address(rsp, (-offset)), rax);
  }

  // Writes to stack successive pages until offset reached to check for
  // stack overflow + shadow pages.  Also, clobbers tmp
  void bang_stack_size(Register size, Register tmp);

  // Check for reserved stack access in method being exited (for JIT)
  void reserved_stack_check();

  void safepoint_poll(Label& slow_path, bool at_return, bool in_nmethod);

  void verify_tlab();

  static Condition negate_condition(Condition cond);

  // Instructions that use AddressLiteral operands. These instruction can handle 32bit/64bit
  // operands. In general the names are modified to avoid hiding the instruction in Assembler
  // so that we don't need to implement all the varieties in the Assembler with trivial wrappers
  // here in MacroAssembler. The major exception to this rule is call

  // Arithmetics


  void addptr(Address dst, int32_t src) { addq(dst, src); }
  void addptr(Address dst, Register src);

  void addptr(Register dst, Address src) { addq(dst, src); }
  void addptr(Register dst, int32_t src);
  void addptr(Register dst, Register src);
  void addptr(Register dst, RegisterOrConstant src) {
    if (src.is_constant()) addptr(dst, checked_cast<int>(src.as_constant()));
    else                   addptr(dst, src.as_register());
  }

  void andptr(Register dst, int32_t src);
  void andptr(Register src1, Register src2) { andq(src1, src2); }

  using Assembler::andq;
  void andq(Register dst, AddressLiteral src, Register rscratch = noreg);

  void cmp8(AddressLiteral src1, int imm, Register rscratch = noreg);

  // renamed to drag out the casting of address to int32_t/intptr_t
  void cmp32(Register src1, int32_t imm);

  void cmp32(AddressLiteral src1, int32_t imm, Register rscratch = noreg);
  // compare reg - mem, or reg - &mem
  void cmp32(Register src1, AddressLiteral src2, Register rscratch = noreg);

  void cmp32(Register src1, Address src2);

  void cmpoop(Register src1, Register src2);
  void cmpoop(Register src1, Address src2);
  void cmpoop(Register dst, jobject obj, Register rscratch);

  // NOTE src2 must be the lval. This is NOT an mem-mem compare
  void cmpptr(Address src1, AddressLiteral src2, Register rscratch);

  void cmpptr(Register src1, AddressLiteral src2, Register rscratch = noreg);

  void cmpptr(Register src1, Register src2) { cmpq(src1, src2); }
  void cmpptr(Register src1, Address src2) { cmpq(src1, src2); }

  void cmpptr(Register src1, int32_t src2) { cmpq(src1, src2); }
  void cmpptr(Address src1, int32_t src2) { cmpq(src1, src2); }

  // cmp64 to avoild hiding cmpq
  void cmp64(Register src1, AddressLiteral src, Register rscratch = noreg);

  void cmpxchgptr(Register reg, Address adr);

  void locked_cmpxchgptr(Register reg, AddressLiteral adr, Register rscratch = noreg);

  void imulptr(Register dst, Register src) { imulq(dst, src); }
  void imulptr(Register dst, Register src, int imm32) { imulq(dst, src, imm32); }


  void negptr(Register dst) { negq(dst); }

  void notptr(Register dst) { notq(dst); }

  void shlptr(Register dst, int32_t shift);
  void shlptr(Register dst) { shlq(dst); }

  void shrptr(Register dst, int32_t shift);
  void shrptr(Register dst) { shrq(dst); }

  void sarptr(Register dst) { sarq(dst); }
  void sarptr(Register dst, int32_t src) { sarq(dst, src); }

  void subptr(Address dst, int32_t src) { subq(dst, src); }

  void subptr(Register dst, Address src) { subq(dst, src); }
  void subptr(Register dst, int32_t src);
  // Force generation of a 4 byte immediate value even if it fits into 8bit
  void subptr_imm32(Register dst, int32_t src);
  void subptr(Register dst, Register src);
  void subptr(Register dst, RegisterOrConstant src) {
    if (src.is_constant()) subptr(dst, (int) src.as_constant());
    else                   subptr(dst,       src.as_register());
  }

  void sbbptr(Address dst, int32_t src) { sbbq(dst, src); }
  void sbbptr(Register dst, int32_t src) { sbbq(dst, src); }

  void xchgptr(Register src1, Register src2) { xchgq(src1, src2); }
  void xchgptr(Register src1, Address src2) { xchgq(src1, src2); }

  void xaddptr(Address src1, Register src2) { xaddq(src1, src2); }



  // Helper functions for statistics gathering.
  // Conditionally (atomically, on MPs) increments passed counter address, preserving condition codes.
  void cond_inc32(Condition cond, AddressLiteral counter_addr, Register rscratch = noreg);
  // Unconditional atomic increment.
  void atomic_incl(Address counter_addr);
  void atomic_incl(AddressLiteral counter_addr, Register rscratch = noreg);
  void atomic_incq(Address counter_addr);
  void atomic_incq(AddressLiteral counter_addr, Register rscratch = noreg);
  void atomic_incptr(AddressLiteral counter_addr, Register rscratch = noreg) { atomic_incq(counter_addr, rscratch); }
  void atomic_incptr(Address counter_addr) { atomic_incq(counter_addr); }

  using Assembler::lea;
  void lea(Register dst, AddressLiteral adr);
  void lea(Address  dst, AddressLiteral adr, Register rscratch);

  void leal32(Register dst, Address src) { leal(dst, src); }

  // Import other testl() methods from the parent class or else
  // they will be hidden by the following overriding declaration.
  using Assembler::testl;
  void testl(Address dst, int32_t imm32);
  void testl(Register dst, int32_t imm32);
  void testl(Register dst, AddressLiteral src); // requires reachable address
  using Assembler::testq;
  void testq(Address dst, int32_t imm32);
  void testq(Register dst, int32_t imm32);

  void orptr(Register dst, Address src) { orq(dst, src); }
  void orptr(Register dst, Register src) { orq(dst, src); }
  void orptr(Register dst, int32_t src) { orq(dst, src); }
  void orptr(Address dst, int32_t imm32) { orq(dst, imm32); }

  void testptr(Register src, int32_t imm32) { testq(src, imm32); }
  void testptr(Register src1, Address src2) { testq(src1, src2); }
  void testptr(Address src, int32_t imm32) { testq(src, imm32); }
  void testptr(Register src1, Register src2);

  void xorptr(Register dst, Register src) { xorq(dst, src); }
  void xorptr(Register dst, Address src) { xorq(dst, src); }

  // Calls

  void call(Label& L, relocInfo::relocType rtype);
  void call(Register entry);
  void call(Address addr) { Assembler::call(addr); }

  // NOTE: this call transfers to the effective address of entry NOT
  // the address contained by entry. This is because this is more natural
  // for jumps/calls.
  void call(AddressLiteral entry, Register rscratch = rax);

  // Emit the CompiledIC call idiom
  void ic_call(address entry, jint method_index = 0);
  static int ic_check_size();
  int ic_check(int end_alignment);

  void emit_static_call_stub();

  // Jumps

  // NOTE: these jumps transfer to the effective address of dst NOT
  // the address contained by dst. This is because this is more natural
  // for jumps/calls.
  void jump(AddressLiteral dst, Register rscratch = noreg);

  void jump_cc(Condition cc, AddressLiteral dst, Register rscratch = noreg);

  // 32bit can do a case table jump in one instruction but we no longer allow the base
  // to be installed in the Address class. This jump will transfer to the address
  // contained in the location described by entry (not the address of entry)
  void jump(ArrayAddress entry, Register rscratch);

  // Adding more natural conditional jump instructions
  void ALWAYSINLINE jo(Label& L, bool maybe_short = true) { jcc(Assembler::overflow, L, maybe_short); }
  void ALWAYSINLINE jno(Label& L, bool maybe_short = true) { jcc(Assembler::noOverflow, L, maybe_short); }
  void ALWAYSINLINE js(Label& L, bool maybe_short = true) { jcc(Assembler::negative, L, maybe_short); }
  void ALWAYSINLINE jns(Label& L, bool maybe_short = true) { jcc(Assembler::positive, L, maybe_short); }
  void ALWAYSINLINE je(Label& L, bool maybe_short = true) { jcc(Assembler::equal, L, maybe_short); }
  void ALWAYSINLINE jz(Label& L, bool maybe_short = true) { jcc(Assembler::zero, L, maybe_short); }
  void ALWAYSINLINE jne(Label& L, bool maybe_short = true) { jcc(Assembler::notEqual, L, maybe_short); }
  void ALWAYSINLINE jnz(Label& L, bool maybe_short = true) { jcc(Assembler::notZero, L, maybe_short); }
  void ALWAYSINLINE jb(Label& L, bool maybe_short = true) { jcc(Assembler::below, L, maybe_short); }
  void ALWAYSINLINE jnae(Label& L, bool maybe_short = true) { jcc(Assembler::below, L, maybe_short); }
  void ALWAYSINLINE jc(Label& L, bool maybe_short = true) { jcc(Assembler::carrySet, L, maybe_short); }
  void ALWAYSINLINE jnb(Label& L, bool maybe_short = true) { jcc(Assembler::aboveEqual, L, maybe_short); }
  void ALWAYSINLINE jae(Label& L, bool maybe_short = true) { jcc(Assembler::aboveEqual, L, maybe_short); }
  void ALWAYSINLINE jnc(Label& L, bool maybe_short = true) { jcc(Assembler::carryClear, L, maybe_short); }
  void ALWAYSINLINE jbe(Label& L, bool maybe_short = true) { jcc(Assembler::belowEqual, L, maybe_short); }
  void ALWAYSINLINE jna(Label& L, bool maybe_short = true) { jcc(Assembler::belowEqual, L, maybe_short); }
  void ALWAYSINLINE ja(Label& L, bool maybe_short = true) { jcc(Assembler::above, L, maybe_short); }
  void ALWAYSINLINE jnbe(Label& L, bool maybe_short = true) { jcc(Assembler::above, L, maybe_short); }
  void ALWAYSINLINE jl(Label& L, bool maybe_short = true) { jcc(Assembler::less, L, maybe_short); }
  void ALWAYSINLINE jnge(Label& L, bool maybe_short = true) { jcc(Assembler::less, L, maybe_short); }
  void ALWAYSINLINE jge(Label& L, bool maybe_short = true) { jcc(Assembler::greaterEqual, L, maybe_short); }
  void ALWAYSINLINE jnl(Label& L, bool maybe_short = true) { jcc(Assembler::greaterEqual, L, maybe_short); }
  void ALWAYSINLINE jle(Label& L, bool maybe_short = true) { jcc(Assembler::lessEqual, L, maybe_short); }
  void ALWAYSINLINE jng(Label& L, bool maybe_short = true) { jcc(Assembler::lessEqual, L, maybe_short); }
  void ALWAYSINLINE jg(Label& L, bool maybe_short = true) { jcc(Assembler::greater, L, maybe_short); }
  void ALWAYSINLINE jnle(Label& L, bool maybe_short = true) { jcc(Assembler::greater, L, maybe_short); }
  void ALWAYSINLINE jp(Label& L, bool maybe_short = true) { jcc(Assembler::parity, L, maybe_short); }
  void ALWAYSINLINE jpe(Label& L, bool maybe_short = true) { jcc(Assembler::parity, L, maybe_short); }
  void ALWAYSINLINE jnp(Label& L, bool maybe_short = true) { jcc(Assembler::noParity, L, maybe_short); }
  void ALWAYSINLINE jpo(Label& L, bool maybe_short = true) { jcc(Assembler::noParity, L, maybe_short); }
  // * No condition for this *  void ALWAYSINLINE jcxz(Label& L, bool maybe_short = true) { jcc(Assembler::cxz, L, maybe_short); }
  // * No condition for this *  void ALWAYSINLINE jecxz(Label& L, bool maybe_short = true) { jcc(Assembler::cxz, L, maybe_short); }

  // Short versions of the above
  void ALWAYSINLINE jo_b(Label& L) { jccb(Assembler::overflow, L); }
  void ALWAYSINLINE jno_b(Label& L) { jccb(Assembler::noOverflow, L); }
  void ALWAYSINLINE js_b(Label& L) { jccb(Assembler::negative, L); }
  void ALWAYSINLINE jns_b(Label& L) { jccb(Assembler::positive, L); }
  void ALWAYSINLINE je_b(Label& L) { jccb(Assembler::equal, L); }
  void ALWAYSINLINE jz_b(Label& L) { jccb(Assembler::zero, L); }
  void ALWAYSINLINE jne_b(Label& L) { jccb(Assembler::notEqual, L); }
  void ALWAYSINLINE jnz_b(Label& L) { jccb(Assembler::notZero, L); }
  void ALWAYSINLINE jb_b(Label& L) { jccb(Assembler::below, L); }
  void ALWAYSINLINE jnae_b(Label& L) { jccb(Assembler::below, L); }
  void ALWAYSINLINE jc_b(Label& L) { jccb(Assembler::carrySet, L); }
  void ALWAYSINLINE jnb_b(Label& L) { jccb(Assembler::aboveEqual, L); }
  void ALWAYSINLINE jae_b(Label& L) { jccb(Assembler::aboveEqual, L); }
  void ALWAYSINLINE jnc_b(Label& L) { jccb(Assembler::carryClear, L); }
  void ALWAYSINLINE jbe_b(Label& L) { jccb(Assembler::belowEqual, L); }
  void ALWAYSINLINE jna_b(Label& L) { jccb(Assembler::belowEqual, L); }
  void ALWAYSINLINE ja_b(Label& L) { jccb(Assembler::above, L); }
  void ALWAYSINLINE jnbe_b(Label& L) { jccb(Assembler::above, L); }
  void ALWAYSINLINE jl_b(Label& L) { jccb(Assembler::less, L); }
  void ALWAYSINLINE jnge_b(Label& L) { jccb(Assembler::less, L); }
  void ALWAYSINLINE jge_b(Label& L) { jccb(Assembler::greaterEqual, L); }
  void ALWAYSINLINE jnl_b(Label& L) { jccb(Assembler::greaterEqual, L); }
  void ALWAYSINLINE jle_b(Label& L) { jccb(Assembler::lessEqual, L); }
  void ALWAYSINLINE jng_b(Label& L) { jccb(Assembler::lessEqual, L); }
  void ALWAYSINLINE jg_b(Label& L) { jccb(Assembler::greater, L); }
  void ALWAYSINLINE jnle_b(Label& L) { jccb(Assembler::greater, L); }
  void ALWAYSINLINE jp_b(Label& L) { jccb(Assembler::parity, L); }
  void ALWAYSINLINE jpe_b(Label& L) { jccb(Assembler::parity, L); }
  void ALWAYSINLINE jnp_b(Label& L) { jccb(Assembler::noParity, L); }
  void ALWAYSINLINE jpo_b(Label& L) { jccb(Assembler::noParity, L); }
  // * No condition for this *  void ALWAYSINLINE jcxz_b(Label& L) { jccb(Assembler::cxz, L); }
  // * No condition for this *  void ALWAYSINLINE jecxz_b(Label& L) { jccb(Assembler::cxz, L); }

  // Floating

  void push_f(XMMRegister r);
  void pop_f(XMMRegister r);
  void push_d(XMMRegister r);
  void pop_d(XMMRegister r);

  void push_ppx(Register src);
  void pop_ppx(Register dst);

  void andpd(XMMRegister dst, XMMRegister    src) { Assembler::andpd(dst, src); }
  void andpd(XMMRegister dst, Address        src) { Assembler::andpd(dst, src); }
  void andpd(XMMRegister dst, AddressLiteral src, Register rscratch = noreg);

  void andnpd(XMMRegister dst, XMMRegister src) { Assembler::andnpd(dst, src); }

  void andps(XMMRegister dst, XMMRegister    src) { Assembler::andps(dst, src); }
  void andps(XMMRegister dst, Address        src) { Assembler::andps(dst, src); }
  void andps(XMMRegister dst, AddressLiteral src, Register rscratch = noreg);

  void comiss(XMMRegister dst, XMMRegister    src) { Assembler::comiss(dst, src); }
  void comiss(XMMRegister dst, Address        src) { Assembler::comiss(dst, src); }
  void comiss(XMMRegister dst, AddressLiteral src, Register rscratch = noreg);

  void comisd(XMMRegister dst, XMMRegister    src) { Assembler::comisd(dst, src); }
  void comisd(XMMRegister dst, Address        src) { Assembler::comisd(dst, src); }
  void comisd(XMMRegister dst, AddressLiteral src, Register rscratch = noreg);

  void orpd(XMMRegister dst, XMMRegister src) { Assembler::orpd(dst, src); }

  void cmp32_mxcsr_std(Address mxcsr_save, Register tmp, Register rscratch = noreg);
  void ldmxcsr(Address src) { Assembler::ldmxcsr(src); }
  void ldmxcsr(AddressLiteral src, Register rscratch = noreg);

 private:
  void sha256_AVX2_one_round_compute(
    Register  reg_old_h,
    Register  reg_a,
    Register  reg_b,
    Register  reg_c,
    Register  reg_d,
    Register  reg_e,
    Register  reg_f,
    Register  reg_g,
    Register  reg_h,
    int iter);
  void sha256_AVX2_four_rounds_compute_first(int start);
  void sha256_AVX2_four_rounds_compute_last(int start);
  void sha256_AVX2_one_round_and_sched(
        XMMRegister xmm_0,     /* == ymm4 on 0, 1, 2, 3 iterations, then rotate 4 registers left on 4, 8, 12 iterations */
        XMMRegister xmm_1,     /* ymm5 */  /* full cycle is 16 iterations */
        XMMRegister xmm_2,     /* ymm6 */
        XMMRegister xmm_3,     /* ymm7 */
        Register    reg_a,      /* == eax on 0 iteration, then rotate 8 register right on each next iteration */
        Register    reg_b,      /* ebx */    /* full cycle is 8 iterations */
        Register    reg_c,      /* edi */
        Register    reg_d,      /* esi */
        Register    reg_e,      /* r8d */
        Register    reg_f,      /* r9d */
        Register    reg_g,      /* r10d */
        Register    reg_h,      /* r11d */
        int iter);

  void addm(int disp, Register r1, Register r2);

  void sha512_AVX2_one_round_compute(Register old_h, Register a, Register b, Register c, Register d,
                                     Register e, Register f, Register g, Register h, int iteration);

  void sha512_AVX2_one_round_and_schedule(XMMRegister xmm4, XMMRegister xmm5, XMMRegister xmm6, XMMRegister xmm7,
                                          Register a, Register b, Register c, Register d, Register e, Register f,
                                          Register g, Register h, int iteration);

  void addmq(int disp, Register r1, Register r2);
 public:
  void sha256_AVX2(XMMRegister msg, XMMRegister state0, XMMRegister state1, XMMRegister msgtmp0,
                   XMMRegister msgtmp1, XMMRegister msgtmp2, XMMRegister msgtmp3, XMMRegister msgtmp4,
                   Register buf, Register state, Register ofs, Register limit, Register rsp,
                   bool multi_block, XMMRegister shuf_mask);
  void sha512_AVX2(XMMRegister msg, XMMRegister state0, XMMRegister state1, XMMRegister msgtmp0,
                   XMMRegister msgtmp1, XMMRegister msgtmp2, XMMRegister msgtmp3, XMMRegister msgtmp4,
                   Register buf, Register state, Register ofs, Register limit, Register rsp, bool multi_block,
                   XMMRegister shuf_mask);
  void sha512_update_ni_x1(Register arg_hash, Register arg_msg, Register ofs, Register limit, bool multi_block);

  void fast_md5(Register buf, Address state, Address ofs, Address limit,
                bool multi_block);

  void fast_sha1(XMMRegister abcd, XMMRegister e0, XMMRegister e1, XMMRegister msg0,
                 XMMRegister msg1, XMMRegister msg2, XMMRegister msg3, XMMRegister shuf_mask,
                 Register buf, Register state, Register ofs, Register limit, Register rsp,
                 bool multi_block);

  void fast_sha256(XMMRegister msg, XMMRegister state0, XMMRegister state1, XMMRegister msgtmp0,
                   XMMRegister msgtmp1, XMMRegister msgtmp2, XMMRegister msgtmp3, XMMRegister msgtmp4,
                   Register buf, Register state, Register ofs, Register limit, Register rsp,
                   bool multi_block, XMMRegister shuf_mask);

  void fast_exp(XMMRegister xmm0, XMMRegister xmm1, XMMRegister xmm2, XMMRegister xmm3,
                XMMRegister xmm4, XMMRegister xmm5, XMMRegister xmm6, XMMRegister xmm7,
                Register rax, Register rcx, Register rdx, Register tmp);

private:

  // these are private because users should be doing movflt/movdbl

  void movss(Address     dst, XMMRegister    src) { Assembler::movss(dst, src); }
  void movss(XMMRegister dst, XMMRegister    src) { Assembler::movss(dst, src); }
  void movss(XMMRegister dst, Address        src) { Assembler::movss(dst, src); }
  void movss(XMMRegister dst, AddressLiteral src, Register rscratch = noreg);

  void movlpd(XMMRegister dst, Address        src) {Assembler::movlpd(dst, src); }
  void movlpd(XMMRegister dst, AddressLiteral src, Register rscratch = noreg);

public:

  void addsd(XMMRegister dst, XMMRegister    src) { Assembler::addsd(dst, src); }
  void addsd(XMMRegister dst, Address        src) { Assembler::addsd(dst, src); }
  void addsd(XMMRegister dst, AddressLiteral src, Register rscratch = noreg);

  void addss(XMMRegister dst, XMMRegister    src) { Assembler::addss(dst, src); }
  void addss(XMMRegister dst, Address        src) { Assembler::addss(dst, src); }
  void addss(XMMRegister dst, AddressLiteral src, Register rscratch = noreg);

  void addpd(XMMRegister dst, XMMRegister    src) { Assembler::addpd(dst, src); }
  void addpd(XMMRegister dst, Address        src) { Assembler::addpd(dst, src); }
  void addpd(XMMRegister dst, AddressLiteral src, Register rscratch = noreg);

  using Assembler::vbroadcasti128;
  void vbroadcasti128(XMMRegister dst, AddressLiteral src, int vector_len, Register rscratch = noreg);

  using Assembler::vbroadcastsd;
  void vbroadcastsd(XMMRegister dst, AddressLiteral src, int vector_len, Register rscratch = noreg);

  using Assembler::vbroadcastss;
  void vbroadcastss(XMMRegister dst, AddressLiteral src, int vector_len, Register rscratch = noreg);

  // Vector float blend
  void vblendvps(XMMRegister dst, XMMRegister nds, XMMRegister src, XMMRegister mask, int vector_len, bool compute_mask = true, XMMRegister scratch = xnoreg);
  void vblendvpd(XMMRegister dst, XMMRegister nds, XMMRegister src, XMMRegister mask, int vector_len, bool compute_mask = true, XMMRegister scratch = xnoreg);

  void divsd(XMMRegister dst, XMMRegister    src) { Assembler::divsd(dst, src); }
  void divsd(XMMRegister dst, Address        src) { Assembler::divsd(dst, src); }
  void divsd(XMMRegister dst, AddressLiteral src, Register rscratch = noreg);

  void divss(XMMRegister dst, XMMRegister    src) { Assembler::divss(dst, src); }
  void divss(XMMRegister dst, Address        src) { Assembler::divss(dst, src); }
  void divss(XMMRegister dst, AddressLiteral src, Register rscratch = noreg);

  // Move Unaligned Double Quadword
  void movdqu(Address     dst, XMMRegister    src);
  void movdqu(XMMRegister dst, XMMRegister    src);
  void movdqu(XMMRegister dst, Address        src);
  void movdqu(XMMRegister dst, AddressLiteral src, Register rscratch = noreg);

  void kmovwl(Register  dst, KRegister      src) { Assembler::kmovwl(dst, src); }
  void kmovwl(Address   dst, KRegister      src) { Assembler::kmovwl(dst, src); }
  void kmovwl(KRegister dst, KRegister      src) { Assembler::kmovwl(dst, src); }
  void kmovwl(KRegister dst, Register       src) { Assembler::kmovwl(dst, src); }
  void kmovwl(KRegister dst, Address        src) { Assembler::kmovwl(dst, src); }
  void kmovwl(KRegister dst, AddressLiteral src, Register rscratch = noreg);

  void kmovql(KRegister dst, KRegister      src) { Assembler::kmovql(dst, src); }
  void kmovql(KRegister dst, Register       src) { Assembler::kmovql(dst, src); }
  void kmovql(Register  dst, KRegister      src) { Assembler::kmovql(dst, src); }
  void kmovql(KRegister dst, Address        src) { Assembler::kmovql(dst, src); }
  void kmovql(Address   dst, KRegister      src) { Assembler::kmovql(dst, src); }
  void kmovql(KRegister dst, AddressLiteral src, Register rscratch = noreg);

  // Safe move operation, lowers down to 16bit moves for targets supporting
  // AVX512F feature and 64bit moves for targets supporting AVX512BW feature.
  void kmov(Address  dst, KRegister src);
  void kmov(KRegister dst, Address src);
  void kmov(KRegister dst, KRegister src);
  void kmov(Register dst, KRegister src);
  void kmov(KRegister dst, Register src);

  using Assembler::movddup;
  void movddup(XMMRegister dst, AddressLiteral src, Register rscratch = noreg);

  using Assembler::vmovddup;
  void vmovddup(XMMRegister dst, AddressLiteral src, int vector_len, Register rscratch = noreg);

  // AVX Unaligned forms
  void vmovdqu(Address     dst, XMMRegister    src);
  void vmovdqu(XMMRegister dst, Address        src);
  void vmovdqu(XMMRegister dst, XMMRegister    src);
  void vmovdqu(XMMRegister dst, AddressLiteral src,                 Register rscratch = noreg);
  void vmovdqu(XMMRegister dst, AddressLiteral src, int vector_len, Register rscratch = noreg);
  void vmovdqu(XMMRegister dst, XMMRegister    src, int vector_len);
  void vmovdqu(XMMRegister dst, Address        src, int vector_len);
  void vmovdqu(Address     dst, XMMRegister    src, int vector_len);

  // AVX Aligned forms
  using Assembler::vmovdqa;
  void vmovdqa(XMMRegister dst, AddressLiteral src,                 Register rscratch = noreg);
  void vmovdqa(XMMRegister dst, AddressLiteral src, int vector_len, Register rscratch = noreg);

  // AVX512 Unaligned
  void evmovdqu(BasicType type, KRegister kmask, Address     dst, XMMRegister src, bool merge, int vector_len);
  void evmovdqu(BasicType type, KRegister kmask, XMMRegister dst, Address     src, bool merge, int vector_len);
  void evmovdqu(BasicType type, KRegister kmask, XMMRegister dst, XMMRegister src, bool merge, int vector_len);

  void evmovdqub(XMMRegister dst, XMMRegister src, int vector_len) { Assembler::evmovdqub(dst, src, vector_len); }
  void evmovdqub(XMMRegister dst, Address     src, int vector_len) { Assembler::evmovdqub(dst, src, vector_len); }

  void evmovdqub(XMMRegister dst, KRegister mask, XMMRegister src, bool merge, int vector_len) {
    if (dst->encoding() != src->encoding() || mask != k0)  {
      Assembler::evmovdqub(dst, mask, src, merge, vector_len);
    }
  }
  void evmovdqub(Address     dst, KRegister mask, XMMRegister    src, bool merge, int vector_len) { Assembler::evmovdqub(dst, mask, src, merge, vector_len); }
  void evmovdqub(XMMRegister dst, KRegister mask, Address        src, bool merge, int vector_len) { Assembler::evmovdqub(dst, mask, src, merge, vector_len); }
  void evmovdqub(XMMRegister dst, KRegister mask, AddressLiteral src, bool merge, int vector_len, Register rscratch = noreg);

  void evmovdquw(XMMRegister dst, XMMRegister src, int vector_len) { Assembler::evmovdquw(dst, src, vector_len); }
  void evmovdquw(Address     dst, XMMRegister src, int vector_len) { Assembler::evmovdquw(dst, src, vector_len); }
  void evmovdquw(XMMRegister dst, Address     src, int vector_len) { Assembler::evmovdquw(dst, src, vector_len); }

  void evmovdquw(XMMRegister dst, KRegister mask, XMMRegister src, bool merge, int vector_len) {
    if (dst->encoding() != src->encoding() || mask != k0) {
      Assembler::evmovdquw(dst, mask, src, merge, vector_len);
    }
  }
  void evmovdquw(XMMRegister dst, KRegister mask, Address        src, bool merge, int vector_len) { Assembler::evmovdquw(dst, mask, src, merge, vector_len); }
  void evmovdquw(Address     dst, KRegister mask, XMMRegister    src, bool merge, int vector_len) { Assembler::evmovdquw(dst, mask, src, merge, vector_len); }
  void evmovdquw(XMMRegister dst, KRegister mask, AddressLiteral src, bool merge, int vector_len, Register rscratch = noreg);

  void evmovdqul(XMMRegister dst, XMMRegister src, int vector_len) {
     if (dst->encoding() != src->encoding()) {
       Assembler::evmovdqul(dst, src, vector_len);
     }
  }
  void evmovdqul(Address     dst, XMMRegister src, int vector_len) { Assembler::evmovdqul(dst, src, vector_len); }
  void evmovdqul(XMMRegister dst, Address     src, int vector_len) { Assembler::evmovdqul(dst, src, vector_len); }

  void evmovdqul(XMMRegister dst, KRegister mask, XMMRegister src, bool merge, int vector_len) {
    if (dst->encoding() != src->encoding() || mask != k0)  {
      Assembler::evmovdqul(dst, mask, src, merge, vector_len);
    }
  }
  void evmovdqul(Address     dst, KRegister mask, XMMRegister    src, bool merge, int vector_len) { Assembler::evmovdqul(dst, mask, src, merge, vector_len); }
  void evmovdqul(XMMRegister dst, KRegister mask, Address        src, bool merge, int vector_len) { Assembler::evmovdqul(dst, mask, src, merge, vector_len); }
  void evmovdqul(XMMRegister dst, KRegister mask, AddressLiteral src, bool merge, int vector_len, Register rscratch = noreg);

  void evmovdquq(XMMRegister dst, XMMRegister src, int vector_len) {
    if (dst->encoding() != src->encoding()) {
      Assembler::evmovdquq(dst, src, vector_len);
    }
  }
  void evmovdquq(XMMRegister dst, Address        src, int vector_len) { Assembler::evmovdquq(dst, src, vector_len); }
  void evmovdquq(Address     dst, XMMRegister    src, int vector_len) { Assembler::evmovdquq(dst, src, vector_len); }
  void evmovdquq(XMMRegister dst, AddressLiteral src, int vector_len, Register rscratch = noreg);
  void evmovdqaq(XMMRegister dst, AddressLiteral src, int vector_len, Register rscratch = noreg);

  void evmovdquq(XMMRegister dst, KRegister mask, XMMRegister src, bool merge, int vector_len) {
    if (dst->encoding() != src->encoding() || mask != k0) {
      Assembler::evmovdquq(dst, mask, src, merge, vector_len);
    }
  }
  void evmovdquq(Address     dst, KRegister mask, XMMRegister    src, bool merge, int vector_len) { Assembler::evmovdquq(dst, mask, src, merge, vector_len); }
  void evmovdquq(XMMRegister dst, KRegister mask, Address        src, bool merge, int vector_len) { Assembler::evmovdquq(dst, mask, src, merge, vector_len); }
  void evmovdquq(XMMRegister dst, KRegister mask, AddressLiteral src, bool merge, int vector_len, Register rscratch = noreg);
  void evmovdqaq(XMMRegister dst, KRegister mask, AddressLiteral src, bool merge, int vector_len, Register rscratch = noreg);

  using Assembler::movapd;
  void movapd(XMMRegister dst, AddressLiteral src, Register rscratch = noreg);

  // Move Aligned Double Quadword
  void movdqa(XMMRegister dst, XMMRegister    src) { Assembler::movdqa(dst, src); }
  void movdqa(XMMRegister dst, Address        src) { Assembler::movdqa(dst, src); }
  void movdqa(XMMRegister dst, AddressLiteral src, Register rscratch = noreg);

  void movsd(Address     dst, XMMRegister    src) { Assembler::movsd(dst, src); }
  void movsd(XMMRegister dst, XMMRegister    src) { Assembler::movsd(dst, src); }
  void movsd(XMMRegister dst, Address        src) { Assembler::movsd(dst, src); }
  void movsd(XMMRegister dst, AddressLiteral src, Register rscratch = noreg);

  void mulpd(XMMRegister dst, XMMRegister    src) { Assembler::mulpd(dst, src); }
  void mulpd(XMMRegister dst, Address        src) { Assembler::mulpd(dst, src); }
  void mulpd(XMMRegister dst, AddressLiteral src, Register rscratch = noreg);

  void mulsd(XMMRegister dst, XMMRegister    src) { Assembler::mulsd(dst, src); }
  void mulsd(XMMRegister dst, Address        src) { Assembler::mulsd(dst, src); }
  void mulsd(XMMRegister dst, AddressLiteral src, Register rscratch = noreg);

  void mulss(XMMRegister dst, XMMRegister    src) { Assembler::mulss(dst, src); }
  void mulss(XMMRegister dst, Address        src) { Assembler::mulss(dst, src); }
  void mulss(XMMRegister dst, AddressLiteral src, Register rscratch = noreg);

  // Carry-Less Multiplication Quadword
  void pclmulldq(XMMRegister dst, XMMRegister src) {
    // 0x00 - multiply lower 64 bits [0:63]
    Assembler::pclmulqdq(dst, src, 0x00);
  }
  void pclmulhdq(XMMRegister dst, XMMRegister src) {
    // 0x11 - multiply upper 64 bits [64:127]
    Assembler::pclmulqdq(dst, src, 0x11);
  }

  void pcmpeqb(XMMRegister dst, XMMRegister src);
  void pcmpeqw(XMMRegister dst, XMMRegister src);

  void pcmpestri(XMMRegister dst, Address src, int imm8);
  void pcmpestri(XMMRegister dst, XMMRegister src, int imm8);

  void pmovzxbw(XMMRegister dst, XMMRegister src);
  void pmovzxbw(XMMRegister dst, Address src);

  void pmovmskb(Register dst, XMMRegister src);

  void ptest(XMMRegister dst, XMMRegister src);

  void roundsd(XMMRegister dst, XMMRegister    src, int32_t rmode) { Assembler::roundsd(dst, src, rmode); }
  void roundsd(XMMRegister dst, Address        src, int32_t rmode) { Assembler::roundsd(dst, src, rmode); }
  void roundsd(XMMRegister dst, AddressLiteral src, int32_t rmode, Register rscratch = noreg);

  void sqrtss(XMMRegister dst, XMMRegister     src) { Assembler::sqrtss(dst, src); }
  void sqrtss(XMMRegister dst, Address         src) { Assembler::sqrtss(dst, src); }
  void sqrtss(XMMRegister dst, AddressLiteral  src, Register rscratch = noreg);

  void subsd(XMMRegister dst, XMMRegister    src) { Assembler::subsd(dst, src); }
  void subsd(XMMRegister dst, Address        src) { Assembler::subsd(dst, src); }
  void subsd(XMMRegister dst, AddressLiteral src, Register rscratch = noreg);

  void subss(XMMRegister dst, XMMRegister    src) { Assembler::subss(dst, src); }
  void subss(XMMRegister dst, Address        src) { Assembler::subss(dst, src); }
  void subss(XMMRegister dst, AddressLiteral src, Register rscratch = noreg);

  void ucomiss(XMMRegister dst, XMMRegister    src) { Assembler::ucomiss(dst, src); }
  void ucomiss(XMMRegister dst, Address        src) { Assembler::ucomiss(dst, src); }
  void ucomiss(XMMRegister dst, AddressLiteral src, Register rscratch = noreg);

  void ucomisd(XMMRegister dst, XMMRegister    src) { Assembler::ucomisd(dst, src); }
  void ucomisd(XMMRegister dst, Address        src) { Assembler::ucomisd(dst, src); }
  void ucomisd(XMMRegister dst, AddressLiteral src, Register rscratch = noreg);

  // Bitwise Logical XOR of Packed Double-Precision Floating-Point Values
  void xorpd(XMMRegister dst, XMMRegister    src);
  void xorpd(XMMRegister dst, Address        src) { Assembler::xorpd(dst, src); }
  void xorpd(XMMRegister dst, AddressLiteral src, Register rscratch = noreg);

  // Bitwise Logical XOR of Packed Single-Precision Floating-Point Values
  void xorps(XMMRegister dst, XMMRegister    src);
  void xorps(XMMRegister dst, Address        src) { Assembler::xorps(dst, src); }
  void xorps(XMMRegister dst, AddressLiteral src, Register rscratch = noreg);

  // Shuffle Bytes
  void pshufb(XMMRegister dst, XMMRegister    src) { Assembler::pshufb(dst, src); }
  void pshufb(XMMRegister dst, Address        src) { Assembler::pshufb(dst, src); }
  void pshufb(XMMRegister dst, AddressLiteral src, Register rscratch = noreg);
  // AVX 3-operands instructions

  void vaddsd(XMMRegister dst, XMMRegister nds, XMMRegister    src) { Assembler::vaddsd(dst, nds, src); }
  void vaddsd(XMMRegister dst, XMMRegister nds, Address        src) { Assembler::vaddsd(dst, nds, src); }
  void vaddsd(XMMRegister dst, XMMRegister nds, AddressLiteral src, Register rscratch = noreg);

  void vaddss(XMMRegister dst, XMMRegister nds, XMMRegister    src) { Assembler::vaddss(dst, nds, src); }
  void vaddss(XMMRegister dst, XMMRegister nds, Address        src) { Assembler::vaddss(dst, nds, src); }
  void vaddss(XMMRegister dst, XMMRegister nds, AddressLiteral src, Register rscratch = noreg);

  void vabsss(XMMRegister dst, XMMRegister nds, XMMRegister src, AddressLiteral negate_field, int vector_len, Register rscratch = noreg);
  void vabssd(XMMRegister dst, XMMRegister nds, XMMRegister src, AddressLiteral negate_field, int vector_len, Register rscratch = noreg);

  void vpaddb(XMMRegister dst, XMMRegister nds, XMMRegister    src, int vector_len);
  void vpaddb(XMMRegister dst, XMMRegister nds, Address        src, int vector_len);
  void vpaddb(XMMRegister dst, XMMRegister nds, AddressLiteral src, int vector_len, Register rscratch = noreg);

  void vpaddw(XMMRegister dst, XMMRegister nds, XMMRegister src, int vector_len);
  void vpaddw(XMMRegister dst, XMMRegister nds, Address     src, int vector_len);

  void vpaddd(XMMRegister dst, XMMRegister nds, XMMRegister    src, int vector_len) { Assembler::vpaddd(dst, nds, src, vector_len); }
  void vpaddd(XMMRegister dst, XMMRegister nds, Address        src, int vector_len) { Assembler::vpaddd(dst, nds, src, vector_len); }
  void vpaddd(XMMRegister dst, XMMRegister nds, AddressLiteral src, int vector_len, Register rscratch = noreg);

  void vpand(XMMRegister dst, XMMRegister nds, XMMRegister    src, int vector_len) { Assembler::vpand(dst, nds, src, vector_len); }
  void vpand(XMMRegister dst, XMMRegister nds, Address        src, int vector_len) { Assembler::vpand(dst, nds, src, vector_len); }
  void vpand(XMMRegister dst, XMMRegister nds, AddressLiteral src, int vector_len, Register rscratch = noreg);

  using Assembler::vpbroadcastd;
  void vpbroadcastd(XMMRegister dst, AddressLiteral src, int vector_len, Register rscratch = noreg);

  using Assembler::vpbroadcastq;
  void vpbroadcastq(XMMRegister dst, AddressLiteral src, int vector_len, Register rscratch = noreg);

  void vpcmpeqb(XMMRegister dst, XMMRegister nds, XMMRegister src, int vector_len);
  void vpcmpeqb(XMMRegister dst, XMMRegister src1, Address src2, int vector_len);

  void vpcmpeqw(XMMRegister dst, XMMRegister nds, Address src, int vector_len);
  void vpcmpeqw(XMMRegister dst, XMMRegister nds, XMMRegister src, int vector_len);
  void evpcmpeqd(KRegister kdst, KRegister mask, XMMRegister nds, AddressLiteral src, int vector_len, Register rscratch = noreg);

  // Vector compares
  void evpcmpd(KRegister kdst, KRegister mask, XMMRegister nds, XMMRegister    src, int comparison, bool is_signed, int vector_len) {
    Assembler::evpcmpd(kdst, mask, nds, src, comparison, is_signed, vector_len);
  }
  void evpcmpd(KRegister kdst, KRegister mask, XMMRegister nds, AddressLiteral src, int comparison, bool is_signed, int vector_len, Register rscratch = noreg);

  void evpcmpq(KRegister kdst, KRegister mask, XMMRegister nds, XMMRegister    src, int comparison, bool is_signed, int vector_len) {
    Assembler::evpcmpq(kdst, mask, nds, src, comparison, is_signed, vector_len);
  }
  void evpcmpq(KRegister kdst, KRegister mask, XMMRegister nds, AddressLiteral src, int comparison, bool is_signed, int vector_len, Register rscratch = noreg);

  void evpcmpb(KRegister kdst, KRegister mask, XMMRegister nds, XMMRegister    src, int comparison, bool is_signed, int vector_len) {
    Assembler::evpcmpb(kdst, mask, nds, src, comparison, is_signed, vector_len);
  }
  void evpcmpb(KRegister kdst, KRegister mask, XMMRegister nds, AddressLiteral src, int comparison, bool is_signed, int vector_len, Register rscratch = noreg);

  void evpcmpw(KRegister kdst, KRegister mask, XMMRegister nds, XMMRegister    src, int comparison, bool is_signed, int vector_len) {
    Assembler::evpcmpw(kdst, mask, nds, src, comparison, is_signed, vector_len);
  }
  void evpcmpw(KRegister kdst, KRegister mask, XMMRegister nds, AddressLiteral src, int comparison, bool is_signed, int vector_len, Register rscratch = noreg);

  void evpbroadcast(BasicType type, XMMRegister dst, Register src, int vector_len);

  // Emit comparison instruction for the specified comparison predicate.
  void vpcmpCCW(XMMRegister dst, XMMRegister nds, XMMRegister src, XMMRegister xtmp, ComparisonPredicate cond, Width width, int vector_len);
  void vpcmpCC(XMMRegister dst, XMMRegister nds, XMMRegister src, int cond_encoding, Width width, int vector_len);

  void vpmovzxbw(XMMRegister dst, Address     src, int vector_len);
  void vpmovzxbw(XMMRegister dst, XMMRegister src, int vector_len) { Assembler::vpmovzxbw(dst, src, vector_len); }

  void vpmovmskb(Register dst, XMMRegister src, int vector_len = Assembler::AVX_256bit);

  void vpmullw(XMMRegister dst, XMMRegister nds, XMMRegister src, int vector_len);
  void vpmullw(XMMRegister dst, XMMRegister nds, Address     src, int vector_len);

  void vpmulld(XMMRegister dst, XMMRegister nds, XMMRegister    src, int vector_len) { Assembler::vpmulld(dst, nds, src, vector_len); }
  void vpmulld(XMMRegister dst, XMMRegister nds, Address        src, int vector_len) { Assembler::vpmulld(dst, nds, src, vector_len); }
  void vpmulld(XMMRegister dst, XMMRegister nds, AddressLiteral src, int vector_len, Register rscratch = noreg);

  void vpmuldq(XMMRegister dst, XMMRegister nds, XMMRegister    src, int vector_len) { Assembler::vpmuldq(dst, nds, src, vector_len); }

  void vpsubb(XMMRegister dst, XMMRegister nds, XMMRegister src, int vector_len);
  void vpsubb(XMMRegister dst, XMMRegister nds, Address     src, int vector_len);

  void vpsubw(XMMRegister dst, XMMRegister nds, XMMRegister src, int vector_len);
  void vpsubw(XMMRegister dst, XMMRegister nds, Address     src, int vector_len);

  void vpsraw(XMMRegister dst, XMMRegister nds, XMMRegister shift, int vector_len);
  void vpsraw(XMMRegister dst, XMMRegister nds, int         shift, int vector_len);

  void evpsrad(XMMRegister dst, XMMRegister nds, XMMRegister shift, int vector_len);
  void evpsrad(XMMRegister dst, XMMRegister nds, int         shift, int vector_len);

  void evpsraq(XMMRegister dst, XMMRegister nds, XMMRegister shift, int vector_len);
  void evpsraq(XMMRegister dst, XMMRegister nds, int         shift, int vector_len);

  using Assembler::evpsllw;
  void evpsllw(XMMRegister dst, KRegister mask, XMMRegister nds, XMMRegister src, bool merge, int vector_len, bool is_varshift) {
    if (!is_varshift) {
      Assembler::evpsllw(dst, mask, nds, src, merge, vector_len);
    } else {
      Assembler::evpsllvw(dst, mask, nds, src, merge, vector_len);
    }
  }
  void evpslld(XMMRegister dst, KRegister mask, XMMRegister nds, XMMRegister src, bool merge, int vector_len, bool is_varshift) {
    if (!is_varshift) {
      Assembler::evpslld(dst, mask, nds, src, merge, vector_len);
    } else {
      Assembler::evpsllvd(dst, mask, nds, src, merge, vector_len);
    }
  }
  void evpsllq(XMMRegister dst, KRegister mask, XMMRegister nds, XMMRegister src, bool merge, int vector_len, bool is_varshift) {
    if (!is_varshift) {
      Assembler::evpsllq(dst, mask, nds, src, merge, vector_len);
    } else {
      Assembler::evpsllvq(dst, mask, nds, src, merge, vector_len);
    }
  }
  void evpsrlw(XMMRegister dst, KRegister mask, XMMRegister nds, XMMRegister src, bool merge, int vector_len, bool is_varshift) {
    if (!is_varshift) {
      Assembler::evpsrlw(dst, mask, nds, src, merge, vector_len);
    } else {
      Assembler::evpsrlvw(dst, mask, nds, src, merge, vector_len);
    }
  }
  void evpsrld(XMMRegister dst, KRegister mask, XMMRegister nds, XMMRegister src, bool merge, int vector_len, bool is_varshift) {
    if (!is_varshift) {
      Assembler::evpsrld(dst, mask, nds, src, merge, vector_len);
    } else {
      Assembler::evpsrlvd(dst, mask, nds, src, merge, vector_len);
    }
  }

  using Assembler::evpsrlq;
  void evpsrlq(XMMRegister dst, KRegister mask, XMMRegister nds, XMMRegister src, bool merge, int vector_len, bool is_varshift) {
    if (!is_varshift) {
      Assembler::evpsrlq(dst, mask, nds, src, merge, vector_len);
    } else {
      Assembler::evpsrlvq(dst, mask, nds, src, merge, vector_len);
    }
  }
  using Assembler::evpsraw;
  void evpsraw(XMMRegister dst, KRegister mask, XMMRegister nds, XMMRegister src, bool merge, int vector_len, bool is_varshift) {
    if (!is_varshift) {
      Assembler::evpsraw(dst, mask, nds, src, merge, vector_len);
    } else {
      Assembler::evpsravw(dst, mask, nds, src, merge, vector_len);
    }
  }
  using Assembler::evpsrad;
  void evpsrad(XMMRegister dst, KRegister mask, XMMRegister nds, XMMRegister src, bool merge, int vector_len, bool is_varshift) {
    if (!is_varshift) {
      Assembler::evpsrad(dst, mask, nds, src, merge, vector_len);
    } else {
      Assembler::evpsravd(dst, mask, nds, src, merge, vector_len);
    }
  }
  using Assembler::evpsraq;
  void evpsraq(XMMRegister dst, KRegister mask, XMMRegister nds, XMMRegister src, bool merge, int vector_len, bool is_varshift) {
    if (!is_varshift) {
      Assembler::evpsraq(dst, mask, nds, src, merge, vector_len);
    } else {
      Assembler::evpsravq(dst, mask, nds, src, merge, vector_len);
    }
  }

  void evpmins(BasicType type, XMMRegister dst, KRegister mask, XMMRegister nds, XMMRegister src, bool merge, int vector_len);
  void evpmaxs(BasicType type, XMMRegister dst, KRegister mask, XMMRegister nds, XMMRegister src, bool merge, int vector_len);
  void evpmins(BasicType type, XMMRegister dst, KRegister mask, XMMRegister nds, Address src, bool merge, int vector_len);
  void evpmaxs(BasicType type, XMMRegister dst, KRegister mask, XMMRegister nds, Address src, bool merge, int vector_len);

  void evpminu(BasicType type, XMMRegister dst, KRegister mask, XMMRegister nds, XMMRegister src, bool merge, int vector_len);
  void evpmaxu(BasicType type, XMMRegister dst, KRegister mask, XMMRegister nds, XMMRegister src, bool merge, int vector_len);
  void evpminu(BasicType type, XMMRegister dst, KRegister mask, XMMRegister nds, Address src, bool merge, int vector_len);
  void evpmaxu(BasicType type, XMMRegister dst, KRegister mask, XMMRegister nds, Address src, bool merge, int vector_len);

  void vpsrlw(XMMRegister dst, XMMRegister nds, XMMRegister shift, int vector_len);
  void vpsrlw(XMMRegister dst, XMMRegister nds, int shift, int vector_len);

  void vpsllw(XMMRegister dst, XMMRegister nds, XMMRegister shift, int vector_len);
  void vpsllw(XMMRegister dst, XMMRegister nds, int shift, int vector_len);

  void vptest(XMMRegister dst, XMMRegister src);
  void vptest(XMMRegister dst, XMMRegister src, int vector_len) { Assembler::vptest(dst, src, vector_len); }

  void punpcklbw(XMMRegister dst, XMMRegister src);
  void punpcklbw(XMMRegister dst, Address src) { Assembler::punpcklbw(dst, src); }

  void pshufd(XMMRegister dst, Address src, int mode);
  void pshufd(XMMRegister dst, XMMRegister src, int mode) { Assembler::pshufd(dst, src, mode); }

  void pshuflw(XMMRegister dst, XMMRegister src, int mode);
  void pshuflw(XMMRegister dst, Address src, int mode) { Assembler::pshuflw(dst, src, mode); }

  void vandpd(XMMRegister dst, XMMRegister nds, XMMRegister    src, int vector_len) { Assembler::vandpd(dst, nds, src, vector_len); }
  void vandpd(XMMRegister dst, XMMRegister nds, Address        src, int vector_len) { Assembler::vandpd(dst, nds, src, vector_len); }
  void vandpd(XMMRegister dst, XMMRegister nds, AddressLiteral src, int vector_len, Register rscratch = noreg);

  void vandps(XMMRegister dst, XMMRegister nds, XMMRegister    src, int vector_len) { Assembler::vandps(dst, nds, src, vector_len); }
  void vandps(XMMRegister dst, XMMRegister nds, Address        src, int vector_len) { Assembler::vandps(dst, nds, src, vector_len); }
  void vandps(XMMRegister dst, XMMRegister nds, AddressLiteral src, int vector_len, Register rscratch = noreg);

  void evpord(XMMRegister dst, KRegister mask, XMMRegister nds, AddressLiteral src, bool merge, int vector_len, Register rscratch = noreg);

  void vdivsd(XMMRegister dst, XMMRegister nds, XMMRegister    src) { Assembler::vdivsd(dst, nds, src); }
  void vdivsd(XMMRegister dst, XMMRegister nds, Address        src) { Assembler::vdivsd(dst, nds, src); }
  void vdivsd(XMMRegister dst, XMMRegister nds, AddressLiteral src, Register rscratch = noreg);

  void vdivss(XMMRegister dst, XMMRegister nds, XMMRegister    src) { Assembler::vdivss(dst, nds, src); }
  void vdivss(XMMRegister dst, XMMRegister nds, Address        src) { Assembler::vdivss(dst, nds, src); }
  void vdivss(XMMRegister dst, XMMRegister nds, AddressLiteral src, Register rscratch = noreg);

  void vmulsd(XMMRegister dst, XMMRegister nds, XMMRegister    src) { Assembler::vmulsd(dst, nds, src); }
  void vmulsd(XMMRegister dst, XMMRegister nds, Address        src) { Assembler::vmulsd(dst, nds, src); }
  void vmulsd(XMMRegister dst, XMMRegister nds, AddressLiteral src, Register rscratch = noreg);

  void vmulss(XMMRegister dst, XMMRegister nds, XMMRegister    src) { Assembler::vmulss(dst, nds, src); }
  void vmulss(XMMRegister dst, XMMRegister nds, Address        src) { Assembler::vmulss(dst, nds, src); }
  void vmulss(XMMRegister dst, XMMRegister nds, AddressLiteral src, Register rscratch = noreg);

  void vsubsd(XMMRegister dst, XMMRegister nds, XMMRegister    src) { Assembler::vsubsd(dst, nds, src); }
  void vsubsd(XMMRegister dst, XMMRegister nds, Address        src) { Assembler::vsubsd(dst, nds, src); }
  void vsubsd(XMMRegister dst, XMMRegister nds, AddressLiteral src, Register rscratch = noreg);

  void vsubss(XMMRegister dst, XMMRegister nds, XMMRegister    src) { Assembler::vsubss(dst, nds, src); }
  void vsubss(XMMRegister dst, XMMRegister nds, Address        src) { Assembler::vsubss(dst, nds, src); }
  void vsubss(XMMRegister dst, XMMRegister nds, AddressLiteral src, Register rscratch = noreg);

  void vnegatess(XMMRegister dst, XMMRegister nds, AddressLiteral src, Register rscratch = noreg);
  void vnegatesd(XMMRegister dst, XMMRegister nds, AddressLiteral src, Register rscratch = noreg);

  // AVX Vector instructions

  void vxorpd(XMMRegister dst, XMMRegister nds, XMMRegister    src, int vector_len) { Assembler::vxorpd(dst, nds, src, vector_len); }
  void vxorpd(XMMRegister dst, XMMRegister nds, Address        src, int vector_len) { Assembler::vxorpd(dst, nds, src, vector_len); }
  void vxorpd(XMMRegister dst, XMMRegister nds, AddressLiteral src, int vector_len, Register rscratch = noreg);

  void vxorps(XMMRegister dst, XMMRegister nds, XMMRegister    src, int vector_len) { Assembler::vxorps(dst, nds, src, vector_len); }
  void vxorps(XMMRegister dst, XMMRegister nds, Address        src, int vector_len) { Assembler::vxorps(dst, nds, src, vector_len); }
  void vxorps(XMMRegister dst, XMMRegister nds, AddressLiteral src, int vector_len, Register rscratch = noreg);

  void vpxor(XMMRegister dst, XMMRegister nds, XMMRegister src, int vector_len) {
    if (UseAVX > 1 || (vector_len < 1)) // vpxor 256 bit is available only in AVX2
      Assembler::vpxor(dst, nds, src, vector_len);
    else
      Assembler::vxorpd(dst, nds, src, vector_len);
  }
  void vpxor(XMMRegister dst, XMMRegister nds, Address src, int vector_len) {
    if (UseAVX > 1 || (vector_len < 1)) // vpxor 256 bit is available only in AVX2
      Assembler::vpxor(dst, nds, src, vector_len);
    else
      Assembler::vxorpd(dst, nds, src, vector_len);
  }
  void vpxor(XMMRegister dst, XMMRegister nds, AddressLiteral src, int vector_len, Register rscratch = noreg);

  // Simple version for AVX2 256bit vectors
  void vpxor(XMMRegister dst, XMMRegister src) {
    assert(UseAVX >= 2, "Should be at least AVX2");
    Assembler::vpxor(dst, dst, src, AVX_256bit);
  }
  void vpxor(XMMRegister dst, Address src) {
    assert(UseAVX >= 2, "Should be at least AVX2");
    Assembler::vpxor(dst, dst, src, AVX_256bit);
  }

  void vpermd(XMMRegister dst, XMMRegister nds, XMMRegister    src, int vector_len) { Assembler::vpermd(dst, nds, src, vector_len); }
  void vpermd(XMMRegister dst, XMMRegister nds, AddressLiteral src, int vector_len, Register rscratch = noreg);

  void vinserti128(XMMRegister dst, XMMRegister nds, XMMRegister src, uint8_t imm8) {
    if (UseAVX > 2 && VM_Version::supports_avx512novl()) {
      Assembler::vinserti32x4(dst, nds, src, imm8);
    } else if (UseAVX > 1) {
      // vinserti128 is available only in AVX2
      Assembler::vinserti128(dst, nds, src, imm8);
    } else {
      Assembler::vinsertf128(dst, nds, src, imm8);
    }
  }

  void vinserti128(XMMRegister dst, XMMRegister nds, Address src, uint8_t imm8) {
    if (UseAVX > 2 && VM_Version::supports_avx512novl()) {
      Assembler::vinserti32x4(dst, nds, src, imm8);
    } else if (UseAVX > 1) {
      // vinserti128 is available only in AVX2
      Assembler::vinserti128(dst, nds, src, imm8);
    } else {
      Assembler::vinsertf128(dst, nds, src, imm8);
    }
  }

  void vextracti128(XMMRegister dst, XMMRegister src, uint8_t imm8) {
    if (UseAVX > 2 && VM_Version::supports_avx512novl()) {
      Assembler::vextracti32x4(dst, src, imm8);
    } else if (UseAVX > 1) {
      // vextracti128 is available only in AVX2
      Assembler::vextracti128(dst, src, imm8);
    } else {
      Assembler::vextractf128(dst, src, imm8);
    }
  }

  void vextracti128(Address dst, XMMRegister src, uint8_t imm8) {
    if (UseAVX > 2 && VM_Version::supports_avx512novl()) {
      Assembler::vextracti32x4(dst, src, imm8);
    } else if (UseAVX > 1) {
      // vextracti128 is available only in AVX2
      Assembler::vextracti128(dst, src, imm8);
    } else {
      Assembler::vextractf128(dst, src, imm8);
    }
  }

  // 128bit copy to/from high 128 bits of 256bit (YMM) vector registers
  void vinserti128_high(XMMRegister dst, XMMRegister src) {
    vinserti128(dst, dst, src, 1);
  }
  void vinserti128_high(XMMRegister dst, Address src) {
    vinserti128(dst, dst, src, 1);
  }
  void vextracti128_high(XMMRegister dst, XMMRegister src) {
    vextracti128(dst, src, 1);
  }
  void vextracti128_high(Address dst, XMMRegister src) {
    vextracti128(dst, src, 1);
  }

  void vinsertf128_high(XMMRegister dst, XMMRegister src) {
    if (UseAVX > 2 && VM_Version::supports_avx512novl()) {
      Assembler::vinsertf32x4(dst, dst, src, 1);
    } else {
      Assembler::vinsertf128(dst, dst, src, 1);
    }
  }

  void vinsertf128_high(XMMRegister dst, Address src) {
    if (UseAVX > 2 && VM_Version::supports_avx512novl()) {
      Assembler::vinsertf32x4(dst, dst, src, 1);
    } else {
      Assembler::vinsertf128(dst, dst, src, 1);
    }
  }

  void vextractf128_high(XMMRegister dst, XMMRegister src) {
    if (UseAVX > 2 && VM_Version::supports_avx512novl()) {
      Assembler::vextractf32x4(dst, src, 1);
    } else {
      Assembler::vextractf128(dst, src, 1);
    }
  }

  void vextractf128_high(Address dst, XMMRegister src) {
    if (UseAVX > 2 && VM_Version::supports_avx512novl()) {
      Assembler::vextractf32x4(dst, src, 1);
    } else {
      Assembler::vextractf128(dst, src, 1);
    }
  }

  // 256bit copy to/from high 256 bits of 512bit (ZMM) vector registers
  void vinserti64x4_high(XMMRegister dst, XMMRegister src) {
    Assembler::vinserti64x4(dst, dst, src, 1);
  }
  void vinsertf64x4_high(XMMRegister dst, XMMRegister src) {
    Assembler::vinsertf64x4(dst, dst, src, 1);
  }
  void vextracti64x4_high(XMMRegister dst, XMMRegister src) {
    Assembler::vextracti64x4(dst, src, 1);
  }
  void vextractf64x4_high(XMMRegister dst, XMMRegister src) {
    Assembler::vextractf64x4(dst, src, 1);
  }
  void vextractf64x4_high(Address dst, XMMRegister src) {
    Assembler::vextractf64x4(dst, src, 1);
  }
  void vinsertf64x4_high(XMMRegister dst, Address src) {
    Assembler::vinsertf64x4(dst, dst, src, 1);
  }

  // 128bit copy to/from low 128 bits of 256bit (YMM) vector registers
  void vinserti128_low(XMMRegister dst, XMMRegister src) {
    vinserti128(dst, dst, src, 0);
  }
  void vinserti128_low(XMMRegister dst, Address src) {
    vinserti128(dst, dst, src, 0);
  }
  void vextracti128_low(XMMRegister dst, XMMRegister src) {
    vextracti128(dst, src, 0);
  }
  void vextracti128_low(Address dst, XMMRegister src) {
    vextracti128(dst, src, 0);
  }

  void vinsertf128_low(XMMRegister dst, XMMRegister src) {
    if (UseAVX > 2 && VM_Version::supports_avx512novl()) {
      Assembler::vinsertf32x4(dst, dst, src, 0);
    } else {
      Assembler::vinsertf128(dst, dst, src, 0);
    }
  }

  void vinsertf128_low(XMMRegister dst, Address src) {
    if (UseAVX > 2 && VM_Version::supports_avx512novl()) {
      Assembler::vinsertf32x4(dst, dst, src, 0);
    } else {
      Assembler::vinsertf128(dst, dst, src, 0);
    }
  }

  void vextractf128_low(XMMRegister dst, XMMRegister src) {
    if (UseAVX > 2 && VM_Version::supports_avx512novl()) {
      Assembler::vextractf32x4(dst, src, 0);
    } else {
      Assembler::vextractf128(dst, src, 0);
    }
  }

  void vextractf128_low(Address dst, XMMRegister src) {
    if (UseAVX > 2 && VM_Version::supports_avx512novl()) {
      Assembler::vextractf32x4(dst, src, 0);
    } else {
      Assembler::vextractf128(dst, src, 0);
    }
  }

  // 256bit copy to/from low 256 bits of 512bit (ZMM) vector registers
  void vinserti64x4_low(XMMRegister dst, XMMRegister src) {
    Assembler::vinserti64x4(dst, dst, src, 0);
  }
  void vinsertf64x4_low(XMMRegister dst, XMMRegister src) {
    Assembler::vinsertf64x4(dst, dst, src, 0);
  }
  void vextracti64x4_low(XMMRegister dst, XMMRegister src) {
    Assembler::vextracti64x4(dst, src, 0);
  }
  void vextractf64x4_low(XMMRegister dst, XMMRegister src) {
    Assembler::vextractf64x4(dst, src, 0);
  }
  void vextractf64x4_low(Address dst, XMMRegister src) {
    Assembler::vextractf64x4(dst, src, 0);
  }
  void vinsertf64x4_low(XMMRegister dst, Address src) {
    Assembler::vinsertf64x4(dst, dst, src, 0);
  }

  // Carry-Less Multiplication Quadword
  void vpclmulldq(XMMRegister dst, XMMRegister nds, XMMRegister src) {
    // 0x00 - multiply lower 64 bits [0:63]
    Assembler::vpclmulqdq(dst, nds, src, 0x00);
  }
  void vpclmulhdq(XMMRegister dst, XMMRegister nds, XMMRegister src) {
    // 0x11 - multiply upper 64 bits [64:127]
    Assembler::vpclmulqdq(dst, nds, src, 0x11);
  }
  void vpclmullqhqdq(XMMRegister dst, XMMRegister nds, XMMRegister src) {
    // 0x10 - multiply nds[0:63] and src[64:127]
    Assembler::vpclmulqdq(dst, nds, src, 0x10);
  }
  void vpclmulhqlqdq(XMMRegister dst, XMMRegister nds, XMMRegister src) {
    //0x01 - multiply nds[64:127] and src[0:63]
    Assembler::vpclmulqdq(dst, nds, src, 0x01);
  }

  void evpclmulldq(XMMRegister dst, XMMRegister nds, XMMRegister src, int vector_len) {
    // 0x00 - multiply lower 64 bits [0:63]
    Assembler::evpclmulqdq(dst, nds, src, 0x00, vector_len);
  }
  void evpclmulhdq(XMMRegister dst, XMMRegister nds, XMMRegister src, int vector_len) {
    // 0x11 - multiply upper 64 bits [64:127]
    Assembler::evpclmulqdq(dst, nds, src, 0x11, vector_len);
  }

  // AVX-512 mask operations.
  void kand(BasicType etype, KRegister dst, KRegister src1, KRegister src2);
  void kor(BasicType type, KRegister dst, KRegister src1, KRegister src2);
  void knot(uint masklen, KRegister dst, KRegister src, KRegister ktmp = knoreg, Register rtmp = noreg);
  void kxor(BasicType type, KRegister dst, KRegister src1, KRegister src2);
  void kortest(uint masklen, KRegister src1, KRegister src2);
  void ktest(uint masklen, KRegister src1, KRegister src2);

  void evperm(BasicType type, XMMRegister dst, KRegister mask, XMMRegister nds, XMMRegister src, bool merge, int vector_len);
  void evperm(BasicType type, XMMRegister dst, KRegister mask, XMMRegister nds, Address src, bool merge, int vector_len);

  void evor(BasicType type, XMMRegister dst, KRegister mask, XMMRegister nds, XMMRegister src, bool merge, int vector_len);
  void evor(BasicType type, XMMRegister dst, KRegister mask, XMMRegister nds, Address src, bool merge, int vector_len);

  void evand(BasicType type, XMMRegister dst, KRegister mask, XMMRegister nds, XMMRegister src, bool merge, int vector_len);
  void evand(BasicType type, XMMRegister dst, KRegister mask, XMMRegister nds, Address src, bool merge, int vector_len);

  void evxor(BasicType type, XMMRegister dst, KRegister mask, XMMRegister nds, XMMRegister src, bool merge, int vector_len);
  void evxor(BasicType type, XMMRegister dst, KRegister mask, XMMRegister nds, Address src, bool merge, int vector_len);

  void evrold(BasicType type, XMMRegister dst, KRegister mask, XMMRegister src, int shift, bool merge, int vlen_enc);
  void evrold(BasicType type, XMMRegister dst, KRegister mask, XMMRegister src1, XMMRegister src2, bool merge, int vlen_enc);
  void evrord(BasicType type, XMMRegister dst, KRegister mask, XMMRegister src, int shift, bool merge, int vlen_enc);
  void evrord(BasicType type, XMMRegister dst, KRegister mask, XMMRegister src1, XMMRegister src2, bool merge, int vlen_enc);

  using Assembler::evpandq;
  void evpandq(XMMRegister dst, XMMRegister nds, AddressLiteral src, int vector_len, Register rscratch = noreg);

  using Assembler::evpaddq;
  void evpaddq(XMMRegister dst, KRegister mask, XMMRegister nds, AddressLiteral src, bool merge, int vector_len, Register rscratch = noreg);

  using Assembler::evporq;
  void evporq(XMMRegister dst, XMMRegister nds, AddressLiteral src, int vector_len, Register rscratch = noreg);

  using Assembler::vpshufb;
  void vpshufb(XMMRegister dst, XMMRegister nds, AddressLiteral src, int vector_len, Register rscratch = noreg);

  using Assembler::vpor;
  void vpor(XMMRegister dst, XMMRegister nds, AddressLiteral src, int vector_len, Register rscratch = noreg);

  using Assembler::vpternlogq;
  void vpternlogq(XMMRegister dst, int imm8, XMMRegister src2, AddressLiteral src3, int vector_len, Register rscratch = noreg);

  void cmov32( Condition cc, Register dst, Address  src);
  void cmov32( Condition cc, Register dst, Register src);

  void cmov(   Condition cc, Register dst, Register src) { cmovptr(cc, dst, src); }

  void cmovptr(Condition cc, Register dst, Address  src) { cmovq(cc, dst, src); }
  void cmovptr(Condition cc, Register dst, Register src) { cmovq(cc, dst, src); }

  void movoop(Register dst, jobject obj);
  void movoop(Address  dst, jobject obj, Register rscratch);

  void mov_metadata(Register dst, Metadata* obj);
  void mov_metadata(Address  dst, Metadata* obj, Register rscratch);

  void movptr(Register     dst, Register       src);
  void movptr(Register     dst, Address        src);
  void movptr(Register     dst, AddressLiteral src);
  void movptr(Register     dst, ArrayAddress   src);
  void movptr(Register     dst, intptr_t       src);
  void movptr(Address      dst, Register       src);
  void movptr(Address      dst, int32_t        imm);
  void movptr(Address      dst, intptr_t       src, Register rscratch);
  void movptr(ArrayAddress dst, Register       src, Register rscratch);

  void movptr(Register dst, RegisterOrConstant src) {
    if (src.is_constant()) movptr(dst, src.as_constant());
    else                   movptr(dst, src.as_register());
  }


  // to avoid hiding movl
  void mov32(Register       dst, AddressLiteral src);
  void mov32(AddressLiteral dst, Register        src, Register rscratch = noreg);

  // Import other mov() methods from the parent class or else
  // they will be hidden by the following overriding declaration.
  using Assembler::movdl;
  void movdl(XMMRegister dst, AddressLiteral src, Register rscratch = noreg);

  using Assembler::movq;
  void movq(XMMRegister dst, AddressLiteral src, Register rscratch = noreg);

  // Can push value or effective address
  void pushptr(AddressLiteral src, Register rscratch);

  void pushptr(Address src) { pushq(src); }
  void popptr(Address src) { popq(src); }

  void pushoop(jobject obj, Register rscratch);
  void pushklass(Metadata* obj, Register rscratch);

  // sign extend as need a l to ptr sized element
  void movl2ptr(Register dst, Address src) { movslq(dst, src); }
  void movl2ptr(Register dst, Register src) { movslq(dst, src); }


 public:
  // clear memory of size 'cnt' qwords, starting at 'base';
  // if 'is_large' is set, do not try to produce short loop
  void clear_mem(Register base, Register cnt, Register rtmp, XMMRegister xtmp, bool is_large, KRegister mask=knoreg);

  // clear memory initialization sequence for constant size;
  void clear_mem(Register base, int cnt, Register rtmp, XMMRegister xtmp, KRegister mask=knoreg);

  // clear memory of size 'cnt' qwords, starting at 'base' using XMM/YMM registers
  void xmm_clear_mem(Register base, Register cnt, Register rtmp, XMMRegister xtmp, KRegister mask=knoreg);

  // Fill primitive arrays
  void generate_fill(BasicType t, bool aligned,
                     Register to, Register value, Register count,
                     Register rtmp, XMMRegister xtmp);

  void encode_iso_array(Register src, Register dst, Register len,
                        XMMRegister tmp1, XMMRegister tmp2, XMMRegister tmp3,
                        XMMRegister tmp4, Register tmp5, Register result, bool ascii);

  void add2_with_carry(Register dest_hi, Register dest_lo, Register src1, Register src2);
  void multiply_64_x_64_loop(Register x, Register xstart, Register x_xstart,
                             Register y, Register y_idx, Register z,
                             Register carry, Register product,
                             Register idx, Register kdx);
  void multiply_add_128_x_128(Register x_xstart, Register y, Register z,
                              Register yz_idx, Register idx,
                              Register carry, Register product, int offset);
  void multiply_128_x_128_bmi2_loop(Register y, Register z,
                                    Register carry, Register carry2,
                                    Register idx, Register jdx,
                                    Register yz_idx1, Register yz_idx2,
                                    Register tmp, Register tmp3, Register tmp4);
  void multiply_128_x_128_loop(Register x_xstart, Register y, Register z,
                               Register yz_idx, Register idx, Register jdx,
                               Register carry, Register product,
                               Register carry2);
  void multiply_to_len(Register x, Register xlen, Register y, Register ylen, Register z, Register tmp0,
                       Register tmp1, Register tmp2, Register tmp3, Register tmp4, Register tmp5);
  void square_rshift(Register x, Register len, Register z, Register tmp1, Register tmp3,
                     Register tmp4, Register tmp5, Register rdxReg, Register raxReg);
  void multiply_add_64_bmi2(Register sum, Register op1, Register op2, Register carry,
                            Register tmp2);
  void multiply_add_64(Register sum, Register op1, Register op2, Register carry,
                       Register rdxReg, Register raxReg);
  void add_one_64(Register z, Register zlen, Register carry, Register tmp1);
  void lshift_by_1(Register x, Register len, Register z, Register zlen, Register tmp1, Register tmp2,
                       Register tmp3, Register tmp4);
  void square_to_len(Register x, Register len, Register z, Register zlen, Register tmp1, Register tmp2,
                     Register tmp3, Register tmp4, Register tmp5, Register rdxReg, Register raxReg);

  void mul_add_128_x_32_loop(Register out, Register in, Register offset, Register len, Register tmp1,
               Register tmp2, Register tmp3, Register tmp4, Register tmp5, Register rdxReg,
               Register raxReg);
  void mul_add(Register out, Register in, Register offset, Register len, Register k, Register tmp1,
               Register tmp2, Register tmp3, Register tmp4, Register tmp5, Register rdxReg,
               Register raxReg);
  void vectorized_mismatch(Register obja, Register objb, Register length, Register log2_array_indxscale,
                           Register result, Register tmp1, Register tmp2,
                           XMMRegister vec1, XMMRegister vec2, XMMRegister vec3);

  // CRC32 code for java.util.zip.CRC32::updateBytes() intrinsic.
  void update_byte_crc32(Register crc, Register val, Register table);
  void kernel_crc32(Register crc, Register buf, Register len, Register table, Register tmp);

  void kernel_crc32_avx512(Register crc, Register buf, Register len, Register table, Register tmp1, Register tmp2);
  void kernel_crc32_avx512_256B(Register crc, Register buf, Register len, Register key, Register pos,
                                Register tmp1, Register tmp2, Label& L_barrett, Label& L_16B_reduction_loop,
                                Label& L_get_last_two_xmms, Label& L_128_done, Label& L_cleanup);

  // CRC32C code for java.util.zip.CRC32C::updateBytes() intrinsic
  // Note on a naming convention:
  // Prefix w = register only used on a Westmere+ architecture
  // Prefix n = register only used on a Nehalem architecture
  void crc32c_ipl_alg4(Register in_out, uint32_t n,
                       Register tmp1, Register tmp2, Register tmp3);
  void crc32c_pclmulqdq(XMMRegister w_xtmp1,
                        Register in_out,
                        uint32_t const_or_pre_comp_const_index, bool is_pclmulqdq_supported,
                        XMMRegister w_xtmp2,
                        Register tmp1,
                        Register n_tmp2, Register n_tmp3);
  void crc32c_rec_alt2(uint32_t const_or_pre_comp_const_index_u1, uint32_t const_or_pre_comp_const_index_u2, bool is_pclmulqdq_supported, Register in_out, Register in1, Register in2,
                       XMMRegister w_xtmp1, XMMRegister w_xtmp2, XMMRegister w_xtmp3,
                       Register tmp1, Register tmp2,
                       Register n_tmp3);
  void crc32c_proc_chunk(uint32_t size, uint32_t const_or_pre_comp_const_index_u1, uint32_t const_or_pre_comp_const_index_u2, bool is_pclmulqdq_supported,
                         Register in_out1, Register in_out2, Register in_out3,
                         Register tmp1, Register tmp2, Register tmp3,
                         XMMRegister w_xtmp1, XMMRegister w_xtmp2, XMMRegister w_xtmp3,
                         Register tmp4, Register tmp5,
                         Register n_tmp6);
  void crc32c_ipl_alg2_alt2(Register in_out, Register in1, Register in2,
                            Register tmp1, Register tmp2, Register tmp3,
                            Register tmp4, Register tmp5, Register tmp6,
                            XMMRegister w_xtmp1, XMMRegister w_xtmp2, XMMRegister w_xtmp3,
                            bool is_pclmulqdq_supported);
  // Fold 128-bit data chunk
  void fold_128bit_crc32(XMMRegister xcrc, XMMRegister xK, XMMRegister xtmp, Register buf, int offset);
  void fold_128bit_crc32(XMMRegister xcrc, XMMRegister xK, XMMRegister xtmp, XMMRegister xbuf);
  // Fold 512-bit data chunk
  void fold512bit_crc32_avx512(XMMRegister xcrc, XMMRegister xK, XMMRegister xtmp, Register buf, Register pos, int offset);
  // Fold 8-bit data
  void fold_8bit_crc32(Register crc, Register table, Register tmp);
  void fold_8bit_crc32(XMMRegister crc, Register table, XMMRegister xtmp, Register tmp);

  // Compress char[] array to byte[].
  void char_array_compress(Register src, Register dst, Register len,
                           XMMRegister tmp1, XMMRegister tmp2, XMMRegister tmp3,
                           XMMRegister tmp4, Register tmp5, Register result,
                           KRegister mask1 = knoreg, KRegister mask2 = knoreg);

  // Inflate byte[] array to char[].
  void byte_array_inflate(Register src, Register dst, Register len,
                          XMMRegister tmp1, Register tmp2, KRegister mask = knoreg);

  void fill_masked(BasicType bt, Address dst, XMMRegister xmm, KRegister mask,
                   Register length, Register temp, int vec_enc);

  void fill64_masked(uint shift, Register dst, int disp,
                         XMMRegister xmm, KRegister mask, Register length,
                         Register temp, bool use64byteVector = false);

  void fill32_masked(uint shift, Register dst, int disp,
                         XMMRegister xmm, KRegister mask, Register length,
                         Register temp);

  void fill32(Address dst, XMMRegister xmm);

  void fill32(Register dst, int disp, XMMRegister xmm);

  void fill64(Address dst, XMMRegister xmm, bool use64byteVector = false);

  void fill64(Register dst, int dis, XMMRegister xmm, bool use64byteVector = false);

  void convert_f2i(Register dst, XMMRegister src);
  void convert_d2i(Register dst, XMMRegister src);
  void convert_f2l(Register dst, XMMRegister src);
  void convert_d2l(Register dst, XMMRegister src);
  void round_double(Register dst, XMMRegister src, Register rtmp, Register rcx);
  void round_float(Register dst, XMMRegister src, Register rtmp, Register rcx);

  void cache_wb(Address line);
  void cache_wbsync(bool is_pre);

#ifdef COMPILER2_OR_JVMCI
  void generate_fill_avx3(BasicType type, Register to, Register value,
                          Register count, Register rtmp, XMMRegister xtmp);
#endif // COMPILER2_OR_JVMCI

  void vallones(XMMRegister dst, int vector_len);

  void check_stack_alignment(Register sp, const char* msg, unsigned bias = 0, Register tmp = noreg);

  void lightweight_lock(Register basic_lock, Register obj, Register reg_rax, Register tmp, Label& slow);
  void lightweight_unlock(Register obj, Register reg_rax, Register tmp, Label& slow);

  void save_legacy_gprs();
  void restore_legacy_gprs();
  void setcc(Assembler::Condition comparison, Register dst);
};

#endif // CPU_X86_MACROASSEMBLER_X86_HPP
