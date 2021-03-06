/*
 * Copyright 2019-2022 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.dockbox.hartshorn.proxy;

import org.dockbox.hartshorn.annotations.AnnotationHelper.AnnotationInvocationHandler;

import java.lang.annotation.Annotation;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Proxy;

/**
 * A proxy lookup implementation that is capable of looking up native proxies. Native proxies are
 * proxies that are created through the standard Java API, or through a {@link ProxyFactory}.
 *
 * @author Guus Lieben
 * @since 22.2
 */
public class NativeProxyLookup implements ProxyLookup {

    @Override
    public <T> Class<T> unproxy(final T instance) {
        final InvocationHandler invocationHandler = Proxy.getInvocationHandler(instance);
        if (invocationHandler instanceof AnnotationInvocationHandler) {
            return (Class<T>) ((AnnotationInvocationHandler) invocationHandler).annotation().annotationType();
        }
        else if (instance instanceof Annotation) {
            return (Class<T>) ((Annotation) instance).annotationType();
        }
        else if (instance instanceof org.dockbox.hartshorn.proxy.Proxy) {
            return (Class<T>) ((org.dockbox.hartshorn.proxy.Proxy) instance).manager().targetClass();
        }
        return instance != null ? (Class<T>) instance.getClass() : null;
    }

    @Override
    public boolean isProxy(final Object instance) {
        return instance != null && (instance instanceof org.dockbox.hartshorn.proxy.Proxy || this.isProxy(instance.getClass()));
    }

    @Override
    public boolean isProxy(final Class<?> candidate) {
        return Proxy.isProxyClass(candidate) || org.dockbox.hartshorn.proxy.Proxy.class.isAssignableFrom(candidate);
    }
}
