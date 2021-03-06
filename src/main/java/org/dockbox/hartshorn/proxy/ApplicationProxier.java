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

import org.dockbox.hartshorn.element.TypeContext;
import org.dockbox.hartshorn.util.Exceptional;

/**
 * The {@link ApplicationProxier} is responsible for creating proxies of components, as well as
 * providing accessors to the proxies.
 *
 * @author Guus Lieben
 * @since 21.9
 */
public interface ApplicationProxier extends ProxyLookup {

    /**
     * Gets the real type of the given proxy instance. If the given instance is not a proxy, the returned
     * type is the same as the given type. This method is used to determine the type of a proxy, without
     * having to unproxy it manually.
     *
     * @param instance The instance to get the type of.
     * @param <T> The type of the instance.
     * @return The type of the instance.
     * @see ProxyLookup#unproxy(Object)
     */
    <T> Exceptional<TypeContext<T>> real(T instance);

    <T> Exceptional<ProxyManager<T>> manager(T instance);

    <D, T extends D> Exceptional<D> delegate(TypeContext<D> type, T instance);

    <T> StateAwareProxyFactory<T, ?> factory(TypeContext<T> type);

    <T> StateAwareProxyFactory<T, ?> factory(Class<T> type);

    void registerProxyLookup(ProxyLookup proxyLookup);
}
