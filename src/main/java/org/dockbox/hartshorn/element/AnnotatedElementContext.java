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

import org.dockbox.hartshorn.annotations.AnnotationHelper;
import org.dockbox.hartshorn.util.Exceptional;

import java.lang.annotation.Annotation;
import java.lang.reflect.AnnotatedElement;
import java.util.HashSet;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

/**
 * AnnotatedElementContext is a context that holds the annotations of an annotated element.
 *
 * @param <A> The type of the annotated element
 * @see AnnotatedElement
 * @author Guus Lieben
 * @since 21.5
 */
public abstract class AnnotatedElementContext<A extends AnnotatedElement> implements QualifiedElement {

    private Map<Class<?>, Annotation> annotationCache;

    /**
     * Returns the annotations of the annotated element.
     *
     * @return The annotations of the annotated element.
     */
    public Set<Annotation> annotations() {
        return new HashSet<>(this.validate().values());
    }

    /**
     * Returns the annotations of the annotated element, when the annotation is annotated with the given
     * annotation.
     * <pre>{@code
     * @ParentAnnotation
     * public @interface MyAnnotation { }
     * }</pre>
     * <pre>{@code
     * @MyAnnotation
     * public class MyClass { }
     * }</pre>
     *
     * <p>The following code will return the annotation {@code @MyAnnotation} of the class {@code MyClass}.
     * <pre>{@code
     * AnnotatedElementContext<MyClass> context = AnnotatedElementContext.of(MyClass.class);
     * Set<Annotation> annotations = context.annotations(MyAnnotation.class);
     * }</pre>
     *
     * @param annotation The annotation type
     * @param <T> The type of the annotation
     * @return The annotations of the annotated element, when the annotation is annotated with the given
     * @see #annotations(Class)
     */
    public <T extends Annotation> Set<Annotation> annotations(final TypeContext<T> annotation) {
        return this.annotations(annotation.type());
    }

    /**
     * Returns the annotations of the annotated element, when the annotation is annotated with the given
     * annotation.
     * <pre>{@code
     * @ParentAnnotation
     * public @interface MyAnnotation { }
     * }</pre>
     * <pre>{@code
     * @MyAnnotation
     * public class MyClass { }
     * }</pre>
     *
     * <p>The following code will return the annotation {@code @MyAnnotation} of the class {@code MyClass}.
     * <pre>{@code
     * AnnotatedElementContext<MyClass> context = AnnotatedElementContext.of(MyClass.class);
     * Set<Annotation> annotations = context.annotations(MyAnnotation.class);
     * }</pre>
     *
     * @param annotation The annotation type
     * @param <T> The type of the annotation
     * @return The annotations of the annotated element, when the annotation is annotated with the given
     * @see #annotations(TypeContext)
     */
    public <T extends Annotation> Set<Annotation> annotations(final Class<T> annotation) {
        return this.annotations().stream()
                .filter(a -> TypeContext.of(a.annotationType()).annotation(annotation).present())
                .collect(Collectors.toSet());
    }

    /**
     * Returns the annotation associated with the given annotation {@link TypeContext}. If the annotation is
     * not present, {@link Exceptional#empty()} is returned.
     *
     * @param annotation The annotation type
     * @param <T> The type of the annotation
     * @return The annotated element associated with the given {@link TypeContext}.
     * @see #annotation(Class)
     */
    public <T extends Annotation> Exceptional<T> annotation(final TypeContext<T> annotation) {
        return this.annotation(annotation.type());
    }

    /**
     * Returns the annotation associated with the given annotation type. If the annotation is not present,
     * {@link Exceptional#empty()} is returned.
     *
     * @param annotation The annotation type
     * @param <T> The type of the annotation
     * @return The annotation associated with the given annotation type.
     * @see #annotation(TypeContext)
     */
    public <T extends Annotation> Exceptional<T> annotation(final Class<T> annotation) {
        if (!annotation.isAnnotation()) return Exceptional.empty();

        final Map<Class<?>, Annotation> annotations = this.validate();
        if (annotations.containsKey(annotation))
            return Exceptional.of(() -> (T) annotations.get(annotation));

        final T oneOrNull = AnnotationHelper.oneOrNull(this.element(), annotation);
        if (oneOrNull != null) annotations.put(annotation, oneOrNull);
        return Exceptional.of(oneOrNull);
    }

    /**
     * Returns the annotation cache. If the cache is not present, a new cache is created. This method may be
     * overridden to provide a custom cache implementation.
     *
     * @return The annotation cache.
     */
    protected Map<Class<?>, Annotation> validate() {
        if (this.annotationCache == null) {
            this.annotationCache = new ConcurrentHashMap<>();
            for (final Annotation annotation : this.element().getAnnotations()) {
                this.annotationCache.put(annotation.annotationType(), annotation);
            }
        }
        return this.annotationCache;
    }

    /**
     * Returns the annotated element represented by this context.
     *
     * @return The annotated element represented by this context.
     */
    protected abstract A element();

    @Override
    public boolean equals(final Object o) {
        if (this == o) return true;
        if (o == null || this.getClass() != o.getClass()) return false;
        final AnnotatedElementContext<?> that = (AnnotatedElementContext<?>) o;
        return this.element().equals(that.element());
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.element());
    }
}
