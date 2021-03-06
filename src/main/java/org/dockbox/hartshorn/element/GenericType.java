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

import org.checkerframework.checker.nullness.qual.NonNull;
import org.dockbox.hartshorn.util.Exceptional;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;

/**
 * Generic type reference, allowing for generic type reading. This is derived
 * from Jackson's TypeReference.
 *
 * @param <T> The generic type
 */
public abstract class GenericType<T> implements Comparable<GenericType<T>> {

    protected final Type _type;

    protected GenericType() {
        final Type superClass = this.getClass().getGenericSuperclass();
        if (superClass instanceof Class<?>) {
            throw new IllegalArgumentException("Internal error: GenericType constructed without actual type information");
        }
        this._type = ((ParameterizedType) superClass).getActualTypeArguments()[0];
    }

    public Type type() {
        return this._type;
    }

    public Exceptional<Class<T>> asClass() {
        final Type type = this.type();
        if (type instanceof Class)
            return Exceptional.of((Class<T>) type);
        return Exceptional.empty();
    }

    @Override
    public int compareTo(@NonNull final GenericType<T> o) {
        return 0;
    }

}
