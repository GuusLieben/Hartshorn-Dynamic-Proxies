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

/**
 * Acts as an extension of a given {@link QualifiedElement}, allowing you to modify protected values. Modifiers can not
 * modify private or final values. Any given modifier will only add or remove values like
 * {@link java.lang.annotation.Annotation annotations}, or change single non-final values.
 *
 * @param <E> The type of the element to modify.
 * @author Guus Lieben
 * @since 22.2
 */
@FunctionalInterface
public interface ElementModifier<E extends QualifiedElement> {
    /**
     * The element wrapped by this modifier. This is the element that will be modified.
     */
    E element();
}
