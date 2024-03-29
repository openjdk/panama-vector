/*
 * Copyright (c) 2000, 2024, Oracle and/or its affiliates. All rights reserved.
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

package nsk.jdi.Connector.defaultArguments;

import nsk.share.*;
import nsk.share.jpda.*;
import nsk.share.jdi.*;

import com.sun.jdi.*;
import com.sun.jdi.connect.Connector;
import java.io.PrintStream;
import java.util.*;

/**
 * Test for the control of
 *
 *      Interface:      com.sun.jdi.connect.Connector
 *      Method:         public java.util.Map defaultArguments()
 *      Assertion:      "The keys of the returned map are string
 *                       argument names."
 */

public class defaultArguments002 {

    private static Log log;

    public static void main( String argv[] ) {
        int result = run(argv,System.out);
        if (result != 0) {
            throw new RuntimeException("TEST FAILED with result " + result);
        }
    }

    public static int run(String argv[], PrintStream out) {
        ArgumentHandler argHandler = new ArgumentHandler(argv);
        log = new Log(out, argHandler);
        VirtualMachineManager vmm = Bootstrap.virtualMachineManager();

        List acl = vmm.allConnectors();
        if (acl.size() > 0) {
            log.display("Number of all known JDI connectors: " + acl.size());
        } else {
            log.complain("FAILURE: no JDI connectors found!");
            return 2;
        }

        Iterator aci = acl.iterator();
        for (int i = 1; aci.hasNext(); i++) {
            Connector c = (Connector) aci.next();
            Map cdfltArgmnts = c.defaultArguments();
            if (cdfltArgmnts.size() < 1) {
                log.complain("FAILURE: connector with empty list of "
                           + "default arguments is found!:");
                log.complain("         Name: " + c.name());
                return 2;
            }

            Set ks = cdfltArgmnts.keySet();
            if (ks.isEmpty()) {
                log.complain("FAILURE: empty argument name set is found "
                           + "for " + c.name() + " connector!");
                return 2;
            }

            log.display(c.name() + "connector arguments: ");

            Iterator argi = ks.iterator();
            for (int j = 1; argi.hasNext(); j++) {
                Object ob = argi.next();
                if (!(ob instanceof String)) {
                    log.complain("FAILURE: " + j + "-argument key must be "
                               + "of String type!");
                    return 2;
                }
                String argkey = (String) ob;

                if (argkey == null) {
                    log.complain("FAILURE: argument name is null "
                               + "for " + c.name() + " connector.");
                    return 2;
                }

                if (argkey.length() == 0) {
                    log.complain("FAILURE: empty argument name is found "
                               + "for " + c.name() + " connector!");
                    return 2;
                }

                log.display("Next (" + j + ") argument's name is: " + argkey);
            };
        };
        log.display("Test PASSED!");
        return 0;
    }
}
