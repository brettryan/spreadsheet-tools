/*
 * CsvLine.java    23 September 2015, 11:23
 *
 * Copyright 2015 Drunken Dev.
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

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import org.apache.commons.lang3.BooleanUtils;
import org.apache.commons.lang3.math.NumberUtils;

import static org.apache.commons.lang3.StringUtils.isNotBlank;


/**
 *
 * @author  Brett Ryan
 */
public class CsvLine {

    private final Object[] cells;

    public CsvLine(Object[] cells) {
        this.cells = cells;
    }

    public String getString(int idx) {
        if (cells.length < idx) {
            return null;
        }
        Object v = cells[idx];
        if (v == null) {
            return null;
        }
        if (v instanceof String) {
            return (String) v;
        }
        return v.toString();
    }

    public int getInt(int idx, int def) {
        if (cells.length < idx) {
            return def;
        }
        Object v = cells[idx];
        if (v == null) {
            return def;
        }
        if (v instanceof Number) {
            return ((Number) v).intValue();
        }
        if (v instanceof String) {
            return (int)NumberUtils.toDouble((String) v, def);
        }
        return (int)NumberUtils.toDouble(v.toString(), def);
    }

    public int getInt(int idx) {
        return getInt(idx, 0);
    }

    public double getDouble(int idx, double def) {
        if (cells.length < idx) {
            return def;
        }
        Object v = cells[idx];
        if (v == null) {
            return def;
        }
        if (v instanceof Number) {
            return ((Number) v).doubleValue();
        }
        if (v instanceof String) {
            return NumberUtils.toDouble((String) v, def);
        }
        return NumberUtils.toDouble(v.toString(), def);
    }

    public double getDouble(int idx) {
        return getDouble(idx, 0.0);
    }

    public boolean getBoolean(int idx) {
        if (cells.length < idx) {
            return false;
        }
        Object v = cells[idx];
        if (v == null) {
            return false;
        }
        if (v instanceof Boolean) {
            return (boolean) v;
        }
        if (v instanceof String) {
            if (v == "1") {
                return true;
            }
            return BooleanUtils.toBoolean((String) v);
        }
        return BooleanUtils.toBoolean(v.toString());
    }

    public LocalDate getLocalDate(int idx) {
        if (cells.length < idx) {
            return null;
        }
        Object v = cells[idx];
        if (v == null) {
            return null;
        }
        if (v instanceof LocalDate) {
            return (LocalDate) v;
        }
        if (v instanceof LocalDateTime) {
            return ((LocalDateTime) v).toLocalDate();
        }
        if (v instanceof String) {
            if (isNotBlank((String) v)) {
                try {
                    return DateTimeFormatter.ISO_LOCAL_DATE.parse(v.toString(), LocalDate::from);
                } catch (DateTimeParseException ex) {
                    return null;
                }
            }
        }
        return null;
    }

    public LocalDate getLocalDate(int idx, String format) {
        if (cells.length < idx) {
            return null;
        }
        Object v = cells[idx];
        if (v == null) {
            return null;
        }
        if (v instanceof LocalDate) {
            return (LocalDate) v;
        }
        if (v instanceof LocalDateTime) {
            return ((LocalDateTime) v).toLocalDate();
        }
        if (v instanceof String) {
            if (isNotBlank((String) v)) {
                try {
                    return DateTimeFormatter.ofPattern(format).parse(v.toString(), LocalDate::from);
                } catch (DateTimeParseException ex) {
                    return null;
                }
            }
        }
        return null;
    }

    public LocalDateTime getLocalDateTime(int idx) {
        if (cells.length < idx) {
            return null;
        }
        Object v = cells[idx];
        if (v == null) {
            return null;
        }
        if (v instanceof LocalDateTime) {
            return (LocalDateTime) v;
        }
        if (v instanceof LocalDate) {
            return ((LocalDate) v).atStartOfDay();
        }
        if (v instanceof String) {
            if (isNotBlank((String) v)) {
                try {
                    return DateTimeFormatter.ISO_LOCAL_DATE_TIME.parse(v.toString(), LocalDateTime::from);
                } catch (DateTimeParseException ex) {
                    return null;
                }
            }
        }
        return null;
    }

    public ZonedDateTime getZonedDateTime(int idx) {
        if (cells.length < idx) {
            return null;
        }
        Object v = cells[idx];
        if (v == null) {
            return null;
        }
        if (v instanceof ZonedDateTime) {
            return (ZonedDateTime) v;
        }
        if (v instanceof LocalDateTime) {
            return ((LocalDateTime) v).atZone(ZoneId.systemDefault());
        }
        if (v instanceof String) {
            if (isNotBlank((String) v)) {
                try {
                    return DateTimeFormatter.ISO_LOCAL_DATE_TIME.parse(v.toString(), ZonedDateTime::from);
                } catch (DateTimeParseException ex) {
                    return null;
                }
            }
        }
        return null;
    }

}
