/*
 * Copyright (c) 1999, 2024, Oracle and/or its affiliates. All rights reserved.
 * Copyright (c) 2012, 2024 SAP SE. All rights reserved.
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

#ifndef OS_AIX_OSTHREAD_AIX_HPP
#define OS_AIX_OSTHREAD_AIX_HPP

#include "runtime/osThreadBase.hpp"
#include "suspendResume_posix.hpp"
#include "utilities/globalDefinitions.hpp"

class OSThread : public OSThreadBase {
  friend class VMStructs;

  typedef pthread_t thread_id_t;

  thread_id_t _thread_id;

  // On AIX, we use the pthread id as OSThread::thread_id and keep the kernel thread id
  // separately for diagnostic purposes.
  //
  // Note: this kernel thread id is saved at thread start. Depending on the
  // AIX scheduling mode, this may not be the current thread id (usually not
  // a problem though as we run with AIXTHREAD_SCOPE=S).
  tid_t _kernel_thread_id;

  sigset_t _caller_sigmask; // Caller's signal mask

 public:
  OSThread();
  ~OSThread();

  // Methods to save/restore caller's signal mask
  sigset_t  caller_sigmask() const       { return _caller_sigmask; }
  void    set_caller_sigmask(sigset_t sigmask)  { _caller_sigmask = sigmask; }

  thread_id_t thread_id() const {
    return _thread_id;
  }
  void set_thread_id(thread_id_t id) {
    _thread_id = id;
  }

  tid_t kernel_thread_id() const {
    return _kernel_thread_id;
  }
  void set_kernel_thread_id(tid_t tid) {
    _kernel_thread_id = tid;
  }

  pthread_t pthread_id() const {
    // Here: same as thread_id()
    return _thread_id;
  }

  // ***************************************************************
  // suspension support.
  // ***************************************************************

  // flags that support signal based suspend/resume on Aix are in a
  // separate class to avoid confusion with many flags in OSThread that
  // are used by VM level suspend/resume.
  SuspendResume sr;

  // _ucontext and _siginfo are used by SR_handler() to save thread context,
  // and they will later be used to walk the stack or reposition thread PC.
  // If the thread is not suspended in SR_handler() (e.g. self suspend),
  // the value in _ucontext is meaningless, so we must use the last Java
  // frame information as the frame. This will mean that for threads
  // that are parked on a mutex the profiler (and safepoint mechanism)
  // will see the thread as if it were still in the Java frame. This
  // not a problem for the profiler since the Java frame is a close
  // enough result. For the safepoint mechanism when the give it the
  // Java frame we are not at a point where the safepoint needs the
  // frame to that accurate (like for a compiled safepoint) since we
  // should be in a place where we are native and will block ourselves
  // if we transition.
 private:
  void* _siginfo;
  ucontext_t* _ucontext;
  int _expanding_stack;                 // non zero if manually expanding stack
  address _alt_sig_stack;               // address of base of alternate signal stack

 public:
  void* siginfo() const                   { return _siginfo;  }
  void set_siginfo(void* ptr)             { _siginfo = ptr;   }
  ucontext_t* ucontext() const            { return _ucontext; }
  void set_ucontext(ucontext_t* ptr)      { _ucontext = ptr;  }
  void set_expanding_stack(void)          { _expanding_stack = 1;  }
  void clear_expanding_stack(void)        { _expanding_stack = 0;  }
  int  expanding_stack(void)              { return _expanding_stack;  }

  void set_alt_sig_stack(address val)     { _alt_sig_stack = val; }
  address alt_sig_stack(void)             { return _alt_sig_stack; }

  // Printing
  uintx thread_id_for_printing() const override {
    return (uintx)_thread_id;
  }
};

#endif // OS_AIX_OSTHREAD_AIX_HPP
