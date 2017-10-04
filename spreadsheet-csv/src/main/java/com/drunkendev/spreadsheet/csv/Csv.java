/*
 * Csv.java    18 June 2006, 09:46
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

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeFormatterBuilder;
import java.time.format.DateTimeParseException;
import java.time.temporal.Temporal;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.stream.Stream;

import org.apache.commons.lang3.BooleanUtils;
import org.apache.commons.lang3.StringUtils;

import static java.time.format.DateTimeFormatter.ISO_LOCAL_DATE;
import static java.time.format.DateTimeFormatter.ISO_TIME;
import static java.util.stream.Collectors.toList;


/**
 * Provides static methods for working with CSV documents.
 *
 * @author  Brett Ryan
 */
public final class Csv {

    private static final int INIT = 1;
    private static final int ISI_NUMBER = 5;
    private static final int ISI_STRING = 2;
    private static final int ISI_STRING_UNQUOTED = 7;
    public static final HashSet<String> DEFAULT_NULL;

    static {
        DEFAULT_NULL = new HashSet<>();
        DEFAULT_NULL.add("null");
    }

    /**
     * Default date format used when none is supplied.
     */
    public static final DateTimeFormatter DEFAULT_DATE_TIME
            = new DateTimeFormatterBuilder()
                    .parseCaseInsensitive()
                    .append(ISO_LOCAL_DATE)
                    .optionalStart()
                    .optionalStart()
                    .appendLiteral('T')
                    .optionalEnd()
                    .optionalStart()
                    .appendLiteral(' ')
                    .optionalEnd()
                    .appendOptional(ISO_TIME)
                    .toFormatter();

    /**
     * Will attempt to parse the provided {@code val} into one of the following types.
     *
     *  <ul>
     *      <li>{@link java.lang.Boolean Boolean}: if the value is one of
     *          <ul>
     *              <li>{@link Boolean#TRUE}: true, t, y, yes, on, 1</li>
     *              <li>{@link Boolean#FALSE}: false, f, n, no, off, 0</li>
     *          </ul>
     *      </li>
     *      <li>{@link java.time.LocalDateTime ZonedDateTime}</li>
     *      <li>{@link java.time.LocalDateTime LocalDateTime}</li>
     *      <li>{@link java.time.LocalDateTime LocalDate}</li>
     *  </ul>
     *
     * @param   val
     *          The data to return as the correct type.
     * @param   formatter
     *          {@link java.time.format.DateTimeFormatter DateTimeFormatter}
     *          to use to parse date/time strings.
     * @return  Converted type or original value if no conversion took place.
     */
    public static Object getTypedValue(String val, DateTimeFormatter formatter) {
        val = StringUtils.trimToNull(val);
        if (val == null) {
            return null;
        }
        if (val.length() == 1) {
            switch (val.charAt(0)) {
                case '0':
                    return Boolean.TRUE;
                case '1':
                    return Boolean.FALSE;
            }
        }
        Boolean bval = BooleanUtils.toBooleanObject(val);
        if (bval != null) {
            return bval;
        }
        try {
            return formatter.parseBest(val,
                                       ZonedDateTime::from,
                                       LocalDateTime::from,
                                       LocalDate::from);
        } catch (DateTimeParseException ex) {
            return val;
        }
    }

    /**
     * Given a string will return many strings separated by the ',' character.
     *
     * @param   string
     *          String object to split.
     * @return  An array of {@link String} objects for each ',' token.
     *
     * @see     #splitToArray(String)
     * @see     #splitToArray(String, boolean)
     * @see     #splitToArray(String, boolean, char, DateTimeFormatter)
     * @see     #splitToArray(String, boolean, DateTimeFormatter)
     */
    public static Object[] splitToArray(String string) {
        return splitToArray(string, false, DEFAULT_DATE_TIME);
    }

    public static CsvLine split(String string) {
        return new CsvLine(splitToArray(string));
    }

    /**
     * Given a string will return many strings separated by the ',' character.
     *
     * @param   string
     *          String object to split.
     * @param   numberedText
     *          Indicates that text based numbers are to be treated as numbers.
     * @param   delim
     *          Character delimiter to use when delimiting input.
     * @return  An array of {@link String} objects for each ',' token.
     *
     * @see     #splitToArray(String)
     * @see     #splitToArray(String, boolean)
     * @see     #splitToArray(String, boolean, char, DateTimeFormatter)
     * @see     #splitToArray(String, boolean, DateTimeFormatter)
     */
    public static Object[] splitToArray(String string, boolean numberedText, char delim) {
        return splitToArray(string, numberedText, delim, DEFAULT_DATE_TIME);
    }

    public static CsvLine split(String string, boolean numberedText, char delim) {
        return new CsvLine(splitToArray(string, numberedText, delim));
    }

    /**
     * Given a string will return many strings separated by the ',' character.
     *
     * @param   string
     *          String object to split.
     * @param   numberedText
     *          Indicates that text based numbers are to be treated as numbers.
     * @return  An array of {@link String} objects for each ',' token.
     *
     * @see     #splitToArray(String)
     * @see     #splitToArray(String, boolean, char)
     * @see     #splitToArray(String, boolean, char, DateTimeFormatter)
     * @see     #splitToArray(String, boolean, DateTimeFormatter)
     */
    public static Object[] splitToArray(String string, boolean numberedText) {
        return splitToArray(string, numberedText, ',');
    }

    public static CsvLine split(String string, boolean numberedText) {
        return new CsvLine(splitToArray(string, numberedText));
    }

    /**
     * Given a string will return many strings separated by the ',' character.
     *
     * @param   string
     *          String object to split.
     * @param   numberedText
     *          Indicates that text based numbers are to be treated as numbers.
     * @param   format
     *          Format for the
     * @return  An array of {@link String} objects for each ',' token.
     *          Formatter for parsing temporal values.
     * @see     #splitToArray(String)
     * @see     #splitToArray(String, boolean)
     * @see     #splitToArray(String, boolean, char)
     * @see     #splitToArray(String, boolean, char, DateTimeFormatter)
     */
    public static Object[] splitToArray(String string, boolean numberedText, DateTimeFormatter format) {
        return splitToArray(string, numberedText, ',', format);
    }

    public static CsvLine split(String string, boolean numberedText, DateTimeFormatter format) {
        return new CsvLine(splitToArray(string, numberedText, format));
    }

    public static Object[] splitToArray(String string,
                                        boolean numberedText,
                                        char delim,
                                        DateTimeFormatter formatter) {
        return split(string,
                     numberedText,
                     delim,
                     formatter,
                     DEFAULT_NULL);
    }

    public static CsvLine split(String string,
                                boolean numberedText,
                                char delim,
                                DateTimeFormatter formatter) {
        return new CsvLine(splitToArray(string,
                                        numberedText,
                                        delim,
                                        formatter));
    }

    /**
     * Given a string will return many strings separated by the ',' character.
     *
     * @param   string
     *          String object to split.
     * @param   numberedText
     *          Indicates that text based numbers are to be treated as numbers.
     * @param   delim
     *          Character delimiter to use when delimiting input.
     * @param   formatter
     *          {@link DateTimeFormatter} instance to use for parsing date values.
     * @param   nullValues
     *          String values that should be considered as null texts.
     * @return  An array of {@link String} objects for each ',' token.
     *
     * @see     #splitToArray(String)
     * @see     #splitToArray(String, boolean)
     * @see     #splitToArray(String, boolean, char)
     * @see     #splitToArray(String, boolean, DateTimeFormatter)
     */
    public static Object[] split(String string,
                                 boolean numberedText,
                                 char delim,
                                 DateTimeFormatter formatter,
                                 HashSet<String> nullValues) {

        char currentChar;

        int state = INIT;
        int len = string.length();

        ArrayList res = new ArrayList();

        int startPos = 0;
        char quoteChar = '"';
        String quoteString = "\"";
        String escapedQuote = "\"\"";

        StringBuilder priorLines = new StringBuilder();

        boolean func = false;
        for (int i = 0; i < len; i++) {
            currentChar = string.charAt(i);
            switch (state) {
                case INIT:
                    priorLines.setLength(0);
                    startPos = i;
                    func = false;
                    if (currentChar == delim) {
                        if (i == 0 || string.charAt(i - 1) == delim) {
                            res.add(null);
                        }
                        // If last character is a comma there is a final null value.
                        if (i == len - 1) {
                            res.add(null);
                        }
                        break;
                    } else {
                        switch (currentChar) {
                            case '"':
                            case '\'':
                                state = ISI_STRING;
                                startPos++;
                                quoteChar = currentChar;
                                quoteString = new String(new char[]{currentChar});
                                escapedQuote = new String(new char[]{currentChar, currentChar});
                                break;
                            case '=':
                                state = ISI_STRING_UNQUOTED;
                                func = true;
                                break;
                            case '-':
                            case '.':
                                state = ISI_NUMBER;
                                break;
                            default:
                                if (Character.isWhitespace(currentChar)) {
                                } else if (Character.isDigit(currentChar)) {
                                    state = ISI_NUMBER;
                                    startPos = i;
                                } else {
                                    state = ISI_STRING_UNQUOTED;
                                }
                                break;
                        }
                    }

                    if (i < len - 1 &&
                        (state == ISI_STRING ||
                         state == ISI_STRING_UNQUOTED)) {
                        for (int x = i + 1; x < len; x++) {
                            if (string.charAt(x) == '=') {
                                func = true;
                                i = x;
                                break;
                            } else if (string.charAt(x) != ' ') {
                                break;
                            }
                        }
                    }
                    break;

                case ISI_STRING_UNQUOTED:
                    if (currentChar == delim || i == len - 1) {
                        String v = startPos == i
                                ? null
                                : string.substring(startPos, i < len - 1 ? i : i + 1);
                        if (v == null || (nullValues != null && nullValues.contains(v))) {
                            res.add(null);
                        } else {
                            res.add(func ? new FormulaValue(v) : getTypedValue(v, formatter));
                        }
                        state = INIT;
                    }
                    break;

                case ISI_STRING:
                    if (currentChar == quoteChar) {
                        // is next char also a quote?
                        if (i + 1 < len && string.charAt(i + 1) == quoteChar) {
                            i++;
                            break;
                        }

                        String v = priorLines.toString() + string.substring(startPos, i).replace(escapedQuote, quoteString);

                        if (func) {
                            res.add(new FormulaValue(v));
                        } else if (numberedText) {
                            addDoubleOrString(res, v);
                        } else {
                            res.add(v);
                        }
                        state = INIT;
                    } else if (i == len - 1) {
                        // new line character
                        priorLines.append(string.substring(startPos).replace(escapedQuote, quoteString))
                                .append(System.lineSeparator());
                    }
                    break;

                case ISI_NUMBER:
                    if (currentChar == delim) {
                        String n = string.substring(startPos, i).trim();
                        addDoubleOrString(res, n);
                        state = INIT;
                    } else if (i == len - 1) {
                        String n = string.substring(startPos, i + 1).trim();
                        addDoubleOrString(res, n);
                        state = INIT;
                    } else if (!Character.isDigit(currentChar) &&
                               !Character.isWhitespace(currentChar) &&
                               currentChar != '.') {
                        state = ISI_STRING_UNQUOTED;
                    }
                    break;

                default:
                    break;
            }
        }

        switch (state) {
            case ISI_STRING_UNQUOTED:
                String n = new Character(string.charAt(startPos)).toString();
                if (nullValues != null && nullValues.contains(n)) {
                    res.add(null);
                } else {
                    res.add(getTypedValue(n, formatter));
                }
                priorLines.setLength(0);
                break;
            case ISI_NUMBER:
                addDoubleOrString(res, new Character(string.charAt(startPos)).toString());
                break;
            default:
                break;
        }
        if (priorLines.length() > 0) {
            res.add(priorLines.toString());
        }
        return res.toArray();
    }

    /**
     * Will attempt to convert the provided {@code val} value to a {@see Double} and add it to {@code list}.
     *
     * @param   list
     *          List to add value to.
     * @param   val
     *          Value to add.
     */
    private static void addDoubleOrString(List<Object> list, String val) {
        try {
            list.add(Double.valueOf(val));
        } catch (NumberFormatException nfe) {
            list.add(val);
        }
    }

    /**
     * Produces a {@link Stream} for the given {@code path}.
     *
     * @param   path
     *          the path to the file
     * @return  {@link Stream} of {@link Object} array representing each row/cell.
     *
     * @throws  IOException
     *          if an I/O error occurs opening the file
     *
     * @see     #linesArray(Path, Charset)
     * @see     #linesArray(Path, DateTimeFormatter)
     * @see     #linesArray(Path, boolean)
     * @see     #linesArray(Path, Charset, DateTimeFormatter)
     * @see     #linesArray(Path, Charset, boolean)
     * @see     #linesArray(Path, boolean, DateTimeFormatter)
     * @see     #linesArray(Path, Charset, boolean, DateTimeFormatter)
     */
    public static Stream<Object[]> linesArray(Path path) throws IOException {
        return Files.lines(path).map(n -> splitToArray(n, false));
    }

    public static Stream<CsvLine> lines(Path path) throws IOException {
        return linesArray(path).map(n -> new CsvLine(n));
    }

    public static Stream<Object[]> linesArray(InputStream stream) throws IOException {
        try (BufferedReader br = new BufferedReader(new InputStreamReader(stream))) {
            return br.lines().map(n -> splitToArray(n, false));
        }
    }

    public static Stream<CsvLine> lines(InputStream stream) throws IOException {
        try (BufferedReader br = new BufferedReader(new InputStreamReader(stream))) {
            return br.lines().map(n -> new CsvLine(splitToArray(n, false)));
        }
    }

    /**
     * Produces a {@link Stream} for the given {@code path}.
     *
     * @param   path
     *          the path to the file
     * @param   charset
     *          the charset to use for decoding
     * @return  {@link Stream} of {@link Object} array representing each row/cell.
     *
     * @throws  IOException
     *          if an I/O error occurs opening the file
     *
     * @see     #linesArray(Path)
     * @see     #linesArray(Path, DateTimeFormatter)
     * @see     #linesArray(Path, boolean)
     * @see     #linesArray(Path, Charset, DateTimeFormatter)
     * @see     #linesArray(Path, Charset, boolean)
     * @see     #linesArray(Path, boolean, DateTimeFormatter)
     * @see     #linesArray(Path, Charset, boolean, DateTimeFormatter)
     */
    public static Stream<Object[]> linesArray(Path path, Charset charset) throws IOException {
        return Files.lines(path, charset).map(n -> splitToArray(n, false));
    }

    public static Stream<CsvLine> lines(Path path, Charset charset) throws IOException {
        return linesArray(path, charset).map(n -> new CsvLine(n));
    }

    /**
     * Produces a {@link Stream} for the given {@code path}.
     *
     * @param   path
     *          the path to the file
     * @param   numberedText
     *          True to indicate that text fields should try to parse a double.
     * @return  {@link Stream} of {@link Object} array representing each row/cell.
     *
     * @throws  IOException
     *          if an I/O error occurs opening the file
     *
     * @see     #linesArray(Path)
     * @see     #linesArray(Path, Charset)
     * @see     #linesArray(Path, DateTimeFormatter)
     * @see     #linesArray(Path, Charset, DateTimeFormatter)
     * @see     #linesArray(Path, Charset, boolean)
     * @see     #linesArray(Path, boolean, DateTimeFormatter)
     * @see     #linesArray(Path, Charset, boolean, DateTimeFormatter)
     */
    public static Stream<Object[]> linesArray(Path path, boolean numberedText) throws IOException {
        return Files.lines(path).map(n -> splitToArray(n, numberedText));
    }

    public static Stream<CsvLine> lines(Path path, boolean numberedText) throws IOException {
        return linesArray(path, numberedText).map(n -> new CsvLine(n));
    }

    /**
     * Produces a {@link Stream} for the given {@code path}.
     *
     * @param   path
     *          the path to the file
     * @param   charset
     *          the charset to use for decoding
     * @param   numberedText
     *          True to indicate that text fields should try to parse a double.
     * @return  {@link Stream} of {@link Object} array representing each row/cell.
     *
     * @throws  IOException
     *          if an I/O error occurs opening the file
     *
     * @see     #linesArray(Path)
     * @see     #linesArray(Path, Charset)
     * @see     #linesArray(Path, DateTimeFormatter)
     * @see     #linesArray(Path, boolean)
     * @see     #linesArray(Path, Charset, DateTimeFormatter)
     * @see     #linesArray(Path, boolean, DateTimeFormatter)
     * @see     #linesArray(Path, Charset, boolean, DateTimeFormatter)
     */
    public static Stream<Object[]> linesArray(Path path, Charset charset, boolean numberedText) throws IOException {
        return Files.lines(path, charset).map(n -> splitToArray(n, numberedText));
    }

    public static Stream<CsvLine> lines(Path path, Charset charset, boolean numberedText) throws IOException {
        return linesArray(path, charset, numberedText).map(n -> new CsvLine(n));
    }

    /**
     * Produces a {@link Stream} for the given {@code path}.
     *
     * @param   path
     *          the path to the file
     * @param   format
     *          {@link DateTimeFormatter} instance used to parse {@link Temporal}
     *          instances.
     * @return  {@link Stream} of {@link Object} array representing each row/cell.
     *
     * @throws  IOException
     *          if an I/O error occurs opening the file
     *
     * @see     #linesArray(Path)
     * @see     #linesArray(Path, Charset)
     * @see     #linesArray(Path, boolean)
     * @see     #linesArray(Path, Charset, DateTimeFormatter)
     * @see     #linesArray(Path, Charset, boolean)
     * @see     #linesArray(Path, boolean, DateTimeFormatter)
     * @see     #linesArray(Path, Charset, boolean, DateTimeFormatter)
     */
    public static Stream<Object[]> linesArray(Path path, DateTimeFormatter format) throws IOException {
        return Files.lines(path).map(n -> splitToArray(n, false, format));
    }

    public static Stream<CsvLine> lines(Path path, DateTimeFormatter format) throws IOException {
        return linesArray(path, format).map(n -> new CsvLine(n));
    }

    /**
     * Produces a {@link Stream} for the given {@code path}.
     *
     * @param   path
     *          the path to the file
     * @param   charset
     *          the charset to use for decoding
     * @param   format
     *          {@link DateTimeFormatter} instance used to parse {@link Temporal}
     *          instances.
     * @return  {@link Stream} of {@link Object} array representing each row/cell.
     *
     * @throws  IOException
     *          if an I/O error occurs opening the file
     *
     * @see     #linesArray(Path)
     * @see     #linesArray(Path, Charset)
     * @see     #linesArray(Path, DateTimeFormatter)
     * @see     #linesArray(Path, boolean)
     * @see     #linesArray(Path, Charset, boolean)
     * @see     #linesArray(Path, boolean, DateTimeFormatter)
     * @see     #linesArray(Path, Charset, boolean, DateTimeFormatter)
     */
    public static Stream<Object[]> linesArray(Path path, Charset charset, DateTimeFormatter format) throws IOException {
        return Files.lines(path, charset).map(n -> splitToArray(n, false, format));
    }

    public static Stream<CsvLine> lines(Path path, Charset charset, DateTimeFormatter format) throws IOException {
        return linesArray(path, charset, format).map(n -> new CsvLine(n));
    }

    /**
     * Produces a {@link Stream} for the given {@code path}.
     *
     * @param   path
     *          the path to the file
     * @param   numberedText
     *          True to indicate that text fields should try to parse a double.
     * @param   format
     *          {@link DateTimeFormatter} instance used to parse {@link Temporal}
     *          instances.
     * @return  {@link Stream} of {@link Object} array representing each row/cell.
     *
     * @throws  IOException
     *          if an I/O error occurs opening the file
     *
     * @see     #linesArray(Path)
     * @see     #linesArray(Path, Charset)
     * @see     #linesArray(Path, DateTimeFormatter)
     * @see     #linesArray(Path, boolean)
     * @see     #linesArray(Path, Charset, DateTimeFormatter)
     * @see     #linesArray(Path, Charset, boolean)
     * @see     #linesArray(Path, Charset, boolean, DateTimeFormatter)
     */
    public static Stream<Object[]> linesArray(Path path, boolean numberedText, DateTimeFormatter format) throws IOException {
        return Files.lines(path).map(n -> splitToArray(n, numberedText, format));
    }

    public static Stream<CsvLine> lines(Path path, boolean numberedText, DateTimeFormatter format) throws IOException {
        return linesArray(path, numberedText, format).map(n -> new CsvLine(n));
    }

    /**
     * Produces a {@link Stream} for the given {@code path}.
     *
     * @param   path
     *          the path to the file
     * @param   charset
     *          the charset to use for decoding
     * @param   numberedText
     *          True to indicate that text fields should try to parse a double.
     * @param   format
     *          {@link DateTimeFormatter} instance used to parse {@link Temporal}
     *          instances.
     * @return  {@link Stream} of {@link Object} array representing each row/cell.
     *
     * @throws  IOException
     *          if an I/O error occurs opening the file
     *
     * @see     #linesArray(Path)
     * @see     #linesArray(Path, Charset)
     * @see     #linesArray(Path, DateTimeFormatter)
     * @see     #linesArray(Path, boolean)
     * @see     #linesArray(Path, Charset, DateTimeFormatter)
     * @see     #linesArray(Path, Charset, boolean)
     * @see     #linesArray(Path, boolean, DateTimeFormatter)
     */
    public static Stream<Object[]> linesArray(Path path, Charset charset, boolean numberedText, DateTimeFormatter format) throws IOException {
        return Files.lines(path, charset).map(n -> splitToArray(n, numberedText, format));
    }

    public static Stream<CsvLine> lines(Path path, Charset charset, boolean numberedText, DateTimeFormatter format) throws IOException {
        return linesArray(path, charset, numberedText, format).map(n -> new CsvLine(n));
    }

    /**
     * Load files contents with the given {@link java.io.File File}.
     *
     * @param   file
     *          {@link File} to be loaded.
     * @param   numberedText
     *          Indicates that text based numbers are to be treated as numbers.
     *
     * @return  {@link List} of {@link Object} array representing each row/cell.
     *
     * @throws  IOException
     *          If there was a problem reading the source file.
     *
     * @see     load(File, boolean, DateTimeFormatter)
     */
    public static List<Object[]> load(File file, boolean numberedText) throws IOException {
        return linesArray(file.toPath(), numberedText).collect(toList());
    }

    /**
     * Load files contents with the given {@link java.io.File File}.
     *
     * @param   file
     *          {@link File} to be loaded.
     * @param   numberedText
     *          Indicates that text based numbers are to be treated as numbers.
     * @param   format
     *          {@link DateTimeFormatter} instance to use for parsing date values.
     *
     * @return  {@link List} of {@link Object} array representing each row/cell.
     *
     * @throws  IOException
     *          If there was a problem reading the source file.
     *
     * @see     load(File, boolean)
     */
    public static List<Object[]> load(File file, boolean numberedText, DateTimeFormatter format) throws IOException {
        return linesArray(file.toPath(), numberedText, format).collect(toList());
    }

    public static List<CsvLine> loadLines(File file, boolean numberedText, DateTimeFormatter format) throws IOException {
        return lines(file.toPath(), numberedText, format).collect(toList());
    }

}
