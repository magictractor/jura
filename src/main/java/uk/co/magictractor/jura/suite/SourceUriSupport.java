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

import java.lang.reflect.Method;
import java.net.URI;
import java.net.URISyntaxException;

import org.junit.platform.engine.TestSource;
import org.junit.platform.engine.support.descriptor.ClassSource;
import org.junit.platform.engine.support.descriptor.ClasspathResourceSource;
import org.junit.platform.engine.support.descriptor.MethodSource;
import org.junit.platform.launcher.TestIdentifier;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public final class SourceUriSupport {

    private static final Logger LOGGER = LoggerFactory.getLogger(SourceUriSupport.class);

    private SourceUriSupport() {
    }

    /**
     * May return null. null URIs are used in dynamic nodes to indicate that the
     * default should be used.
     */
    public static URI fromTestIdentifier(TestIdentifier testIdentifier) {
        if (!testIdentifier.getSource().isPresent()) {
            return null;
        }

        TestSource source = testIdentifier.getSource().get();
        if (source instanceof ClassSource) {
            String className = ((ClassSource) source).getClassName();
            return fromClassName(className);
        }
        else if (source instanceof ClasspathResourceSource) {
            String className = ((ClasspathResourceSource) source).getClasspathResourceName();
            return fromClassName(className);
        }
        else if (source instanceof MethodSource) {
            MethodSource methodSource = (MethodSource) source;
            return fromMethod(methodSource.getClassName(), methodSource.getMethodName(),
                methodSource.getMethodParameterTypes());
        }
        else {
            LOGGER.warn("Code needs modified to create a URI for source type {}", source.getClass().getSimpleName());
            return null;
        }
    }

    public static URI fromMethod(Method method) {

        String methodParameterTypes = null;
        if (method.getParameterCount() > 0) {
            StringBuilder paramBuilder = new StringBuilder();
            paramBuilder.append('(');
            boolean first = true;
            for (Class<?> paramType : method.getParameterTypes()) {
                if (first) {
                    first = false;
                }
                else {
                    paramBuilder.append(',');
                }
                paramBuilder.append(paramType.getName());
            }
            paramBuilder.append(')');
            methodParameterTypes = paramBuilder.toString();
        }

        return fromMethod(method.getDeclaringClass().getName(), method.getName(), methodParameterTypes);
    }

    private static URI fromMethod(String className, String methodName, String methodParameterTypes) {
        String path = pathFromClassName(className);
        String fragment = methodName;
        if (methodParameterTypes != null) {
            fragment += methodParameterTypes;
        }
        return createUri("method", path, null, fragment);
    }

    public static URI fromClassName(String className) {
        return createUri("classpath", pathFromClassName(className), null, null);
    }

    private static String pathFromClassName(String className) {
        return "/" + className.replace('.', '/');
    }

    private static URI createUri(String scheme, String path, String query, String fragment) {
        try {
            return new URI(scheme, null, path, query, fragment);
        }
        catch (URISyntaxException e) {
            throw new IllegalStateException(e);
        }
    }

}
