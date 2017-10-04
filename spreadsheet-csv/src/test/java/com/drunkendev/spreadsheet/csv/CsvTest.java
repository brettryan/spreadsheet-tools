/*
 * CsvTest.java    25 May 2007, 16:27
 *
 * Copyright 2014 Drunken Dev.
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

import java.util.Arrays;
import java.util.Collection;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;

import static org.junit.Assert.assertEquals;


/**
 *
 * @author  Brett Ryan
 */
@RunWith(Parameterized.class)
public class CsvTest {

    @Parameters(name = "{index}: char={0}")
    public static Collection<Object[]> data() {
        return Arrays.asList(new Object[][]{
            {' '}, {','}, {'|'}, {'\t'}
        });
    }

    private final char delim;

    /**
     * Creates a new {@code CsvTestTemplate} instance.
     */
    public CsvTest(char delim) {
        this.delim = delim;
    }

    @Test
    public void testSplitSkip() throws Exception {
        System.out.format("split (%s) - skip\n", delim);

        String string = String.format("first%1$s%1$ssecond", delim);

        Object[] result = Csv.splitToArray(string, false, delim);
        assertEquals(3, result.length);
        assertEquals("first", result[0]);
        assertEquals(null, result[1]);
        assertEquals("second", result[2]);
    }

    @Test
    public void testSplitNull1() {
        System.out.format("split (%s) - null 1\n", delim);

        String string = String.format("%1$s%1$s%1$s", delim);

        Object[] result = Csv.splitToArray(string, false, delim);
        assertEquals(4, result.length);
        assertEquals(null, result[0]);
        assertEquals(null, result[1]);
        assertEquals(null, result[2]);
        assertEquals(null, result[3]);
    }

    @Test
    public void testSplitNull2() {
        System.out.format("split (%s) - null 2\n", delim);

        String string = String.format("%1$s\"\"%1$s\"\"%1$s", delim);

        Object[] result = Csv.splitToArray(string, false, delim);
        assertEquals(4, result.length);
        assertEquals(null, result[0]);
        assertEquals("", result[1]);
        assertEquals("", result[2]);
        assertEquals(null, result[3]);
    }

    @Test
    public void testSplitPlain() {
        System.out.format("split (%s) - plain\n", delim);

        String string = String.format("this%1$sis%1$sa%1$stest", delim);

        Object[] result = Csv.splitToArray(string, false, delim);
        assertEquals(4, result.length);
        assertEquals("this", result[0]);
        assertEquals("is", result[1]);
        assertEquals("a", result[2]);
        assertEquals("test", result[3]);
    }

    @Test
    public void testSplitChar() {
        System.out.format("split (%s) - char\n", delim);

        String string = String.format("'this'%1$s'is'%1$s'a'%1$s'test'", delim);

        Object[] result = Csv.splitToArray(string, false, delim);
        assertEquals(4, result.length);
        assertEquals("this", result[0]);
        assertEquals("is", result[1]);
        assertEquals("a", result[2]);
        assertEquals("test", result[3]);
    }

    @Test
    public void testSplitString() {
        System.out.format("split (%s) - string\n", delim);

        String string = String.format("\"this\"%1$s\"is\"%1$s\"a\"%1$s\"test\"", delim);

        Object[] result = Csv.splitToArray(string, false, delim);
        assertEquals(4, result.length);
        assertEquals("this", result[0]);
        assertEquals("is", result[1]);
        assertEquals("a", result[2]);
        assertEquals("test", result[3]);
    }

    @Test
    public void testSplitStringWithComma() {
        System.out.format("split (%s) - string with comma\n", delim);

        String string = String.format("\"one%1$stwo%1$sthree\"", delim);
        Object[] result = Csv.splitToArray(string, false, delim);
        assertEquals(1, result.length);
        assertEquals(String.format("one%1$stwo%1$sthree", delim), result[0]);
    }

    @Test
    public void testSplitStringWithCQuotes() {
        System.out.format("split (%s) - string with quotes\n", delim);

        String string = String.format("\"\"\"\"%1$s\"Test\"%1$s\"\"\"\"", delim);
        Object[] result = Csv.splitToArray(string, false, delim);
        assertEquals(3, result.length);
        assertEquals("\"", result[0]);
        assertEquals("Test", result[1]);
        assertEquals("\"", result[2]);
    }

    @Test
    public void testSplitCharAndString() {
        if (delim == ' ') {
            return;
        }
        System.out.format("split (%s) - char and string\n", delim);

        String string = String.format("'this'%1$s is%1$s a%1$s \"test\"", delim);

        Object[] result = Csv.splitToArray(string, false, delim);
        assertEquals(4, result.length);
        assertEquals("this", result[0]);
        assertEquals("is", result[1]);
        assertEquals("a", result[2]);
        assertEquals("test", result[3]);
    }

    @Test
    public void testSplitNumbers() {
        System.out.format("split (%s) - numbers\n", delim);

        String string = String.format("1%1$s2%1$s3%1$s4", delim);

        Object[] result = Csv.splitToArray(string, false, delim);
        assertEquals(4, result.length);
        assertEquals((double) 1, result[0]);
        assertEquals((double) 2, result[1]);
        assertEquals((double) 3, result[2]);
        assertEquals((double) 4, result[3]);
    }

    @Test
    public void testSplitNumbersNegative() {
        System.out.format("split (%s) - numbers - negative\n", delim);

        String string = String.format("-1%1$s-2%1$s-3%1$s-4", delim);

        Object[] result = Csv.splitToArray(string, false, delim);
        assertEquals(4, result.length);
        assertEquals((double) -1, result[0]);
        assertEquals((double) -2, result[1]);
        assertEquals((double) -3, result[2]);
        assertEquals((double) -4, result[3]);
    }

    @Test
    public void testSplitNumbersDecimal() {
        System.out.format("split (%s) - numbers - decimal\n", delim);

        String string = String.format("1.1%1$s2.2%1$s3.3%1$s4.4", delim);

        Object[] result = Csv.splitToArray(string, false, delim);
        assertEquals(4, result.length);
        assertEquals(1.1, result[0]);
        assertEquals(2.2, result[1]);
        assertEquals(3.3, result[2]);
        assertEquals(4.4, result[3]);
    }

    @Test
    public void testSplitNumbersDecimalUnderZero() {
        System.out.format("split (%s) - numbers - decimal under zero\n", delim);

        String string = String.format(".1%1$s.2%1$s.3%1$s.4", delim);

        Object[] result = Csv.splitToArray(string, false, delim);
        assertEquals(4, result.length);
        assertEquals(.1, result[0]);
        assertEquals(.2, result[1]);
        assertEquals(.3, result[2]);
        assertEquals(.4, result[3]);
    }

    @Test
    public void testSplitNumbersPadded() {
        if (delim == ' ') {
            return;
        }
        System.out.format("split (%s) - numbers padded\n", delim);

        String string = String.format(" 1 %1$s 2 %1$s 3 %1$s 4 ", delim);

        Object[] result = Csv.splitToArray(string, false, delim);
        assertEquals(4, result.length);
        assertEquals((double) 1, result[0]);
        assertEquals((double) 2, result[1]);
        assertEquals((double) 3, result[2]);
        assertEquals((double) 4, result[3]);
    }

    @Test
    public void testSplitNumbersQuoted() {
        System.out.format("split (%s) - numbers quoted\n", delim);

        String string = String.format("\"1\"%1$s\"2\"%1$s\"3\"%1$s\"4\"", delim);

        Object[] result = Csv.splitToArray(string, false, delim);
        assertEquals(4, result.length);
        assertEquals("1", result[0]);
        assertEquals("2", result[1]);
        assertEquals("3", result[2]);
        assertEquals("4", result[3]);
    }

    @Test
    public void testSplitNumbersQuotedAsNumbers() {
        System.out.format("split (%s) - numbers quoted\n", delim);

        String string = String.format("\"1\"%1$s\"2\"%1$s\"3\"%1$s\"4\"", delim);

        Object[] result = Csv.splitToArray(string, true, delim);
        assertEquals(4, result.length);
        assertEquals((double) 1, result[0]);
        assertEquals((double) 2, result[1]);
        assertEquals((double) 3, result[2]);
        assertEquals((double) 4, result[3]);
    }

    @Test
    public void testMultiLine1() {
        System.out.format("split (%s) - numbers quoted\n", delim);

        String string = String.format("\"Test\nWith\nNew\nLines\"", delim);

        Object[] result = Csv.splitToArray(string, true, delim);
        assertEquals(1, result.length);
        assertEquals("Test\nWith\nNew\nLines", result[0]);
    }

    @Test
    public void testMultiLine2() {
        System.out.format("split (%s) - numbers quoted\n", delim);

        String string = String.format("\"Test\n   With\n   New\n   Lines\"%1$s1", delim);

        Object[] result = Csv.splitToArray(string, true, delim);
        assertEquals(2, result.length);
        assertEquals("Test\n   With\n   New\n   Lines", result[0]);
        assertEquals((double) 1, result[1]);
    }

}
