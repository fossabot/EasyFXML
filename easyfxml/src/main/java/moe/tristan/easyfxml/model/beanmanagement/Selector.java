/*
 * Copyright 2017 - 2019 EasyFXML project and contributors
 *
 *    Licensed under the Apache License, Version 2.0 (the "License");
 *    you may not use this file except in compliance with the License.
 *    You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 *    Unless required by applicable law or agreed to in writing, software
 *    distributed under the License is distributed on an "AS IS" BASIS,
 *    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *    See the License for the specific language governing permissions and
 *    limitations under the License.
 */

package moe.tristan.easyfxml.model.beanmanagement;

import java.lang.ref.WeakReference;
import java.util.Objects;

import moe.tristan.easyfxml.EasyFxml;

/**
 * Selectors are special wrappers around a value used in an {@link AbstractInstanceManager} to distinguish multiple
 * children of the mapped class amongst themselves. It uses a weak reference as a way to store the actual selector
 * value. The overriding of equals and hashCode makes it transparent to use.
 * <p>
 * Its main use is to avoid confusion in the multiple signatures of {@link EasyFxml}.
 *
 * @see AbstractInstanceManager
 * @see EasyFxml
 */
public final class Selector {

    private final WeakReference<?> reference;

    /**
     * Creates a Selector from a given object.
     *
     * @param reference The actual value of the selector to use in map-based storage implementations.
     */
    public Selector(Object reference) {
        this.reference = new WeakReference<>(reference);
    }

    /**
     * @return the backing reference.
     */
    @SuppressWarnings("WeakerAccess")
    public WeakReference getReference() {
        return reference;
    }

    /**
     * Two Selectors are equals iff the values backing their weak references are the same. Null is supported.
     *
     * @param o The other selector to check for equality with.
     *
     * @return true if this selector and the one passed as parameter have the same backing value.
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Selector)) {
            return false;
        }

        final Object thisRef = reference.get();
        final Object oRef = ((Selector) o).getReference().get();

        return Objects.equals(thisRef, oRef);
    }

    /**
     * @return The hashcode of the backing value. 0 if it is null. See {@link Objects#hashCode(Object)}.
     */
    @Override
    public int hashCode() {
        final Object thisRef = reference.get();
        return Objects.hashCode(thisRef);
    }

}
