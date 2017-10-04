/*
 * CsvValueTest.java    Sep 29 2014, 00:08
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

import java.text.ParseException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import org.junit.Test;

import static org.junit.Assert.assertEquals;


/**
 *
 * @author  Brett Ryan
 */
public class CsvValueTest {

    /**
     * Test for a single value representing a single quote.
     */
    @Test
    public void testQuotes1() {
        System.out.println("quotes 1");

        String string = "\"\"\"\"";

        Object[] result = Csv.splitToArray(string, false);
        assertEquals(1, result.length);
        assertEquals("\"", result[0]);
    }

    /**
     * Test for a single value representing a string containing quotes.
     */
    @Test
    public void testQuotes2() {
        System.out.println("quotes 2");

        String string = "\"This is a \"\"TEST\"\" string\"";

        Object[] result = Csv.splitToArray(string, false);
        assertEquals(1, result.length);
        assertEquals("This is a \"TEST\" string", result[0]);
    }

    /**
     * Test for a simple formula unquoted.
     */
    @Test
    public void testFormula1() {
        System.out.println("basic formula");

        String string = "=1+1";

        Object[] result = Csv.splitToArray(string, false);
        assertEquals(1, result.length);
        assertEquals(new FormulaValue("1+1"), result[0]);
    }

    /**
     * Test for a simple formula quoted.
     */
    @Test
    public void testFormula2() {
        System.out.println("basic formula quoted");

        String string = "\"=1+1\"";

        Object[] result = Csv.splitToArray(string, false);
        assertEquals(1, result.length);
        assertEquals(new FormulaValue("1+1"), result[0]);
    }

    /**
     * Test for a simple formula unquoted with string.
     */
    @Test
    public void testFormula3() {
        System.out.println("basic formula with string");

        String string = "=1+1&\"TEST\"";

        Object[] result = Csv.splitToArray(string, false);
        assertEquals(1, result.length);
        assertEquals(new FormulaValue("1+1&\"TEST\""), result[0]);
    }

    /**
     * Test for a simple formula quoted with string.
     */
    @Test
    public void testFormula4() {
        System.out.println("basic formula quoted with string");

        String string = "\"=1+1&\"\"TEST\"\"\"";

        Object[] result = Csv.splitToArray(string, false);
        assertEquals(1, result.length);
        assertEquals(new FormulaValue("1+1&\"TEST\""), result[0]);
    }

    /**
     * Test for a simple formula quoted containing spaces.
     */
    @Test
    public void testFormula5() {
        System.out.println("basic formula padded string");

        String string = " \"=1+1\" ,  \"=2+3\" ";

        Object[] result = Csv.splitToArray(string, false);
        assertEquals(2, result.length);
        assertEquals(new FormulaValue("1+1"), result[0]);
        assertEquals(new FormulaValue("2+3"), result[1]);
    }

    /**
     * Test for formula that contains a comma.
     */
    @Test
    public void testFormula6() {
        System.out.println("basic formula quoted with comma");

        String string = "\"=if(A1=1,1,2)\"";

        Object[] result = Csv.splitToArray(string, false);
        assertEquals(1, result.length);
        assertEquals(new FormulaValue("if(A1=1,1,2)"), result[0]);
    }

    /**
     * Test for formula that contains a comma and strings.
     */
    @Test
    public void testFormula7() {
        System.out.println("complex formula");

        String string = "\"=if(A1=1,\"\"ONE\"\",\"\"OTHER\"\")\"";

        Object[] result = Csv.splitToArray(string, false);
        assertEquals(1, result.length);
        assertEquals(new FormulaValue("if(A1=1,\"ONE\",\"OTHER\")"), result[0]);
    }

    /**
     * Test for an iterative formula.
     */
    @Test
    public void testFormula8() {
        System.out.println("formula iterative");

        String string = "\"{=sum(if(A:A,B:B,C:C))}\"";

        Object[] result = Csv.splitToArray(string, false);
        assertEquals(1, result.length);
        assertEquals("{=sum(if(A:A,B:B,C:C))}", result[0]);
    }

    /**
     * Test for a basic date using dd/MM/yy format.
     * @throws ParseException
     */
    @Test
    public void testDate() throws ParseException {
        System.out.println("date");

        String pattern = "dd/MM/yy";
        DateTimeFormatter fmt = DateTimeFormatter.ofPattern(pattern);
        String string = "21/09/14";

        Object[] result = Csv.splitToArray(string, false, fmt);
        assertEquals(1, result.length);
        assertEquals(LocalDate.of(2014, 9, 21), result[0]);
    }

    /*
     System.out.println(parseDateTime());
     System.out.println(parseDateTime("2014-01-01 10:44:00"));
     System.out.println(parseDateTime("2014-01-01 10:44"));
     System.out.println(parseDateTime("2014-01-01T10:44"));
     System.out.println(parseDateTime("2014-01-01"));
     */
    @Test
    public void testJavaDate1() {
        System.out.println("local date");

        String string = "2014-01-01T10:44:00";

        Object[] result = Csv.splitToArray(string, false);
        assertEquals(1, result.length);
        assertEquals(LocalDateTime.of(2014, 1, 1, 10, 44), result[0]);
    }

    @Test
    public void testJavaDate2() {
        System.out.println("local date time");

        String string = "2014-01-01T10:44";

        Object[] result = Csv.splitToArray(string, false);
        assertEquals(1, result.length);
        assertEquals(LocalDateTime.of(2014, 1, 1, 10, 44), result[0]);
    }

    @Test
    public void testJavaDate3() {
        System.out.println("local date time");

        String string = "2014-01-01 10:44";

        Object[] result = Csv.splitToArray(string, false);
        assertEquals(1, result.length);
        assertEquals(LocalDateTime.of(2014, 1, 1, 10, 44), result[0]);
    }

    @Test
    public void testJavaDate4() {
        System.out.println("local date time");

        String string = "2014-01-01";

        Object[] result = Csv.splitToArray(string, false);
        assertEquals(1, result.length);
        assertEquals(LocalDate.of(2014, 1, 1), result[0]);
    }

    @Test
    public void testJavaDateInvalid() {
        System.out.println("local date time");

        String string = "2014-01-99";

        Object[] result = Csv.splitToArray(string, false);
        assertEquals(1, result.length);
        assertEquals(string, result[0]);
    }

}
