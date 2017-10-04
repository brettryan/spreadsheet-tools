/*
 * FormulaValue.java    4 August 2006, 18:11
 *
 * Copyright 2006 Drunken Dev.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.drunkendev.spreadsheet.csv;

import java.util.Objects;


/**
 * Represents a formula string.
 *
 * @author Brett Ryan
 */
public class FormulaValue {

    private final String value;

    /**
     * Creates a new instance of {@code FormulaValue}
     *
     * @param   value
     *          Formula string this formula represents.
     */
    public FormulaValue(String value) {
        if (value == null) {
            throw new IllegalArgumentException("value may not be null");
        }

        if (value.length() > 0) {
            if (value.charAt(0) == '=') {
                this.value = value.substring(1).trim();
            } else if (value.charAt(0) == '{' && value.charAt(1) == '=') {
                this.value = "{" + value.substring(2).trim();
            } else {
                this.value = value.trim();
            }
        } else {
            this.value = value.trim();
        }
    }

    /**
     * Retrieves the formula value without the leading =.
     *
     * @return  Formula value omitting the = sign.
     */
    public String getValue() {
        return value;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String toString() {
        return "=" + value;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int hashCode() {
        return value.hashCode();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean equals(final Object obj) {
        if (obj == this) {
            return true;
        }
        if (obj != null && obj.getClass() == this.getClass()) {
            FormulaValue other = (FormulaValue) obj;
            return Objects.equals(other.value, this.value);
        }
        return false;
    }

}
