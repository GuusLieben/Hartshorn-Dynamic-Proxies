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

package org.dockbox.hartshorn.element;

import org.dockbox.hartshorn.util.Exceptional;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.function.Function;

/**
 * A context element that represents a constructor. This context element can be used to instantiate a component, as well as provide information
 * about the executable element properties as defined in {@link ExecutableElementContext}.
 *
 * @param <T> The type of the component that is instantiated by this constructor.
 * @see Constructor
 * @author Guus Lieben
 * @since 21.5
 */
public final class ConstructorContext<T> extends ExecutableElementContext<Constructor<T>, T> implements TypedElementContext<T> {

    private final Constructor<T> constructor;
    private Function<Object[], Exceptional<T>> invoker;

    private ConstructorContext(final Constructor<T> constructor) {
        this.constructor = constructor;
        this.constructor.setAccessible(true);
    }

    public Constructor<T> constructor() {
        return this.constructor;
    }

    /**
     * Creates a new {@link ConstructorContext} instance from the given {@link Constructor}.
     *
     * @param constructor The constructor to create the context from.
     * @param <T> The type of the component that is instantiated by this constructor.
     * @return A new {@link ConstructorContext} instance.
     */
    public static <T> ConstructorContext<T> of(final Constructor<T> constructor) {
        return new ConstructorContext<>(constructor);
    }

    /**
     * Invokes the constructor with the given arguments. This may be equal to calling {@link Constructor#newInstance(Object...)}, however it
     * may also be a more efficient way of invoking the constructor depending on the active invoker function.
     *
     * @param args The arguments to pass to the constructor.
     * @return The result of the invocation.
     */
    public Exceptional<T> createInstance(final Object... args) {
        this.prepareHandle();
        return this.invoker.apply(args);
    }

    @Override
    public TypeContext<T> type() {
        return this.parent();
    }

    @Override
    public TypeContext<T> genericType() {
        return this.type();
    }

    @Override
    public String name() {
        return this.qualifiedName();
    }

    private void prepareHandle() {
        if (this.invoker == null) {
            this.invoker = args -> Exceptional.of(() -> {
                try {
                    return this.constructor.newInstance(args);
                } catch (final InvocationTargetException e) {
                    if (e.getCause() instanceof Exception) throw (Exception) e.getCause();
                    throw e;
                }
            });
        }
    }

    @Override
    protected Constructor<T> element() {
        return this.constructor();
    }

    @Override
    public String qualifiedName() {
        return String.format("Constructor[%s]", this.type().qualifiedName());
    }
}
