/*
 * Copyright (c) 2019, 2024, Oracle and/or its affiliates. All rights reserved.
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

package jdk.jpackage.internal;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.LinkedHashMap;
import java.util.stream.Stream;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrowsExactly;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;


public class AppImageFileTest {

    @Test
    public void testIdentity() throws IOException {
        Map<String, Object> params = new LinkedHashMap<>();
        params.put(Arguments.CLIOptions.NAME.getId(), "Foo");
        params.put(Arguments.CLIOptions.APPCLASS.getId(), "TestClass");
        params.put(Arguments.CLIOptions.VERSION.getId(), "2.3");
        params.put(Arguments.CLIOptions.DESCRIPTION.getId(), "Duck is the King");
        AppImageFile aif = create(params);

        assertEquals("Foo", aif.getLauncherName());
    }

    @Test
    public void testInvalidCommandLine() throws IOException {
        // Just make sure AppImageFile will tolerate jpackage params that would
        // never create app image at both load/save phases.
        // People would edit this file just because they can.
        // We should be ready to handle curious minds.
        Map<String, Object> params = new LinkedHashMap<>();
        params.put("invalidParamName", "randomStringValue");
        params.put(Arguments.CLIOptions.APPCLASS.getId(), "TestClass");
        params.put(Arguments.CLIOptions.MAIN_JAR.getId(), "test.jar");
        create(params);

        params = new LinkedHashMap<>();
        params.put(Arguments.CLIOptions.NAME.getId(), "foo");
        params.put(Arguments.CLIOptions.APPCLASS.getId(), "TestClass");
        params.put(Arguments.CLIOptions.VERSION.getId(), "1.0");
        create(params);
    }

    @ParameterizedTest
    @MethodSource
    public void testInavlidXml(String[] xmlData) throws IOException {
        Exception ex = assertThrowsExactly(RuntimeException.class, () -> createFromXml(xmlData));
        assertTrue(ex.getMessage().contains("generated by another jpackage version or malformed"));
        assertTrue(ex.getMessage().endsWith(".jpackage.xml\""));
    }

    private static Stream<org.junit.jupiter.params.provider.Arguments> testInavlidXml() {
        return Stream.of(
                makeArguments((Object)new String[] {"<foo/>"}),
                makeArguments((Object)new String[] {"<jpackage-state/>"}),
                makeArguments((Object)new String[] {JPACKAGE_STATE_OPEN, "</jpackage-state>"}),
                makeArguments((Object)new String[] {
                        JPACKAGE_STATE_OPEN,
                        "<main-launcher></main-launcher>",
                        "</jpackage-state>"
                }),
                makeArguments((Object)new String[] {
                        JPACKAGE_STATE_OPEN,
                        "<main-launcher>Foo</main-launcher>",
                        "<main-class></main-class>",
                        "</jpackage-state>"
                }),
                makeArguments((Object)new String[] {
                        JPACKAGE_STATE_OPEN,
                        "<launcher>A</launcher>",
                        "<launcher>B</launcher>",
                        "</jpackage-state>"
                })
        );
    }

    @ParameterizedTest
    @MethodSource
    public void testValidXml(String expectedLauncherName, String xmlData[]) throws IOException {
        var file = createFromXml(xmlData);
        assertEquals(expectedLauncherName, file.getLauncherName());
        assertTrue(file.getAddLaunchers().isEmpty());
    }

    private static Stream<org.junit.jupiter.params.provider.Arguments> testValidXml() {
        return Stream.of(
                makeArguments("Foo", List.of(
                        JPACKAGE_STATE_OPEN,
                                "<app-version>1.0</app-version>",
                                "<main-launcher>Foo</main-launcher>",
                                "<main-class>main.Class</main-class>",
                                "<signed>false</signed>",
                                "<app-store>false</app-store>",
                        "</jpackage-state>").toArray(String[]::new)
                ),
                makeArguments("Boo", List.of(
                        JPACKAGE_STATE_OPEN,
                                "<app-version>1.0</app-version>",
                                "<main-launcher>Boo</main-launcher>",
                                "<main-launcher>Bar</main-launcher>",
                                "<main-class>main.Class</main-class>",
                                "<signed>false</signed>",
                                "<app-store>false</app-store>",
                            "</jpackage-state>").toArray(String[]::new)
                ),
                makeArguments("duke", List.of(
                        JPACKAGE_STATE_OPEN,
                                "<app-version>1.0</app-version>",
                                "<main-launcher>duke</main-launcher>",
                                "<main-class>main.Class</main-class>",
                                "<signed>false</signed>",
                                "<app-store>false</app-store>",
                                "<launcher></launcher>",
                        "</jpackage-state>").toArray(String[]::new)
                )
        );
    }

    @Test
    public void testMainLauncherName() throws IOException {
        Map<String, Object> params = new LinkedHashMap<>();
        params.put("name", "Foo");
        params.put("main-class", "main.Class");
        params.put("description", "Duck App Description");
        AppImageFile aif = create(params);

        assertEquals("Foo", aif.getLauncherName());
    }

    @Test
    public void testMainClass() throws IOException {
        Map<String, Object> params = new LinkedHashMap<>();
        params.put("name", "Foo");
        params.put("main-class", "main.Class");
        params.put("description", "Duck App Description");
        AppImageFile aif = create(params);

        assertEquals("main.Class", aif.getMainClass());
    }

    @Test
    public void testMacSign() throws IOException {
        Map<String, Object> params = new LinkedHashMap<>();
        params.put("name", "Foo");
        params.put("main-class", "main.Class");
        params.put("description", "Duck App Description");
        params.put("mac-sign", Boolean.TRUE);
        AppImageFile aif = create(params);

        assertTrue(aif.isSigned());
    }

    @Test
    public void testCopyAsSigned() throws IOException {
        Map<String, Object> params = new LinkedHashMap<>();
        params.put("name", "Foo");
        params.put("main-class", "main.Class");
        params.put("description", "Duck App Description");
        params.put("mac-sign", Boolean.FALSE);

        AppImageFile aif = create(params);
        assertFalse(aif.isSigned());

        aif = aif.copyAsSigned();
        assertTrue(aif.isSigned());
    }

    @Test
    public void testMacAppStore() throws IOException {
        Map<String, Object> params = new LinkedHashMap<>();
        params.put("name", "Foo");
        params.put("main-class", "main.Class");
        params.put("description", "Duck App Description");
        params.put("mac-app-store", Boolean.TRUE);
        AppImageFile aif = create(params);

        assertTrue(aif.isAppStore());
    }

    @Test
    public void testAddLaunchers() throws IOException {
        Map<String, Object> params = new LinkedHashMap<>();
        List<Map<String, Object>> launchersAsMap = new ArrayList<>();

        Map<String, Object> addLauncher2Params = new LinkedHashMap<>();
        addLauncher2Params.put("name", "Launcher2Name");
        launchersAsMap.add(addLauncher2Params);

        Map<String, Object> addLauncher3Params = new LinkedHashMap<>();
        addLauncher3Params.put("name", "Launcher3Name");
        launchersAsMap.add(addLauncher3Params);

        params.put("name", "Duke App");
        params.put("main-class", "main.Class");
        params.put("description", "Duke App Description");
        params.put("add-launcher", launchersAsMap);
        AppImageFile aif = create(params);

        List<AppImageFile.LauncherInfo> addLaunchers = aif.getAddLaunchers();
        assertEquals(2, addLaunchers.size());
        List<String> names = new ArrayList<>();
        names.add(addLaunchers.get(0).getName());
        names.add(addLaunchers.get(1).getName());

        assertTrue(names.contains("Launcher2Name"));
        assertTrue(names.contains("Launcher3Name"));
    }

    private AppImageFile create(Map<String, Object> params) throws IOException {
        AppImageFile.save(tempFolder, params);
        return AppImageFile.load(tempFolder);
    }

    private AppImageFile createFromXml(String... xmlData) throws IOException {
        Path path = AppImageFile.getPathInAppImage(tempFolder);
        path.toFile().mkdirs();
        Files.delete(path);

        List<String> data = new ArrayList<>();
        data.add("<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"no\" ?>");
        data.addAll(List.of(xmlData));

        Files.write(path, data, StandardOpenOption.CREATE,
                    StandardOpenOption.TRUNCATE_EXISTING);

        AppImageFile image = AppImageFile.load(tempFolder);
        return image;
    }

    static org.junit.jupiter.params.provider.Arguments makeArguments(Object ... args) {
        return org.junit.jupiter.params.provider.Arguments.of(args);
    }

    @TempDir
    private Path tempFolder;

    private static final String JPACKAGE_STATE_OPEN = String.format(
            "<jpackage-state platform=\"%s\" version=\"%s\">",
            AppImageFile.getPlatform(), AppImageFile.getVersion());

}
