/**
 * Copyright 2019 Ken Dobson
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package uk.co.magictractor.jura.suite;

import static org.assertj.core.api.Assertions.assertThat;

import java.lang.reflect.Method;
import java.net.URI;

import org.junit.jupiter.api.Test;
import org.junit.platform.commons.util.ReflectionUtils;

import uk.co.magictractor.jura.suite.SourceUriSupport;

public class SourceUriSupportTest {

    @Test
    public void testFromMethodWithNoParams() throws ReflectiveOperationException, SecurityException {
        Method method = getClass().getMethod("testFromMethodWithNoParams");
        URI uri = SourceUriSupport.fromMethod(method);
        checkMethodUri(uri, method, "");
    }

    @Test
    public void testFromMethodWithSimpleParams() throws ReflectiveOperationException, SecurityException {
        Method method = getClass().getDeclaredMethod("checkMethodUri", URI.class, Method.class, String.class);
        URI uri = SourceUriSupport.fromMethod(method);
        checkMethodUri(uri, method, "(java.net.URI,java.lang.reflect.Method,java.lang.String)");
    }

    @Test
    public void testFromMethodWithArrayParams() throws ReflectiveOperationException, SecurityException {
        Method method = getClass().getDeclaredMethod("dummy", String[].class, int[][].class);
        URI uri = SourceUriSupport.fromMethod(method);
        checkMethodUri(uri, method, "([Ljava.lang.String;,[[I)");
    }

    private void checkMethodUri(URI uri, Method expectedMethod, String expectedParamsString) {
        assertThat(uri.getScheme()).isEqualTo("method");
        assertThat(uri.getPath()).isEqualTo("/" + getClass().getName().replace('.', '/'));
        assertThat(uri.getFragment()).isEqualTo(expectedMethod.getName() + expectedParamsString);
        assertThat(uri.getQuery()).isNull();

        String[] methodParts = ReflectionUtils.parseFullyQualifiedMethodName(uri.toString().substring(7));
        Method methodFromSource = ReflectionUtils.findMethod(getClass(), methodParts[1], methodParts[2]).get();
        assertThat(methodFromSource).isEqualTo(expectedMethod);
    }

    public void dummy(String[] array1, int[][] array2) {
        // Do nothing. Just used for testing MethodSource URIs.
    }

}
