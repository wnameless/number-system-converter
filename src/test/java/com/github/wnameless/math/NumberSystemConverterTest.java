/**
 *
 * @author Wei-Ming Wu
 *
 *
 * Copyright 2015 Wei-Ming Wu
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 *
 */
package com.github.wnameless.math;

import static com.google.common.collect.Lists.newArrayList;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.List;

import org.junit.Before;
import org.junit.Test;

import com.google.common.testing.EqualsTester;
import com.google.common.testing.NullPointerTester;

public class NumberSystemConverterTest {

  List<Character> base1 = NumberSystem.HEX;
  List<Character> base2 = NumberSystem.OCT;
  NumberSystemConverter converter;

  @Before
  public void setUp() throws Exception {
    converter = new NumberSystemConverter(base1, base2);
  }

  @Test
  public void testInstantiation() {
    assertTrue(converter instanceof NumberSystemConverter);
  }

  @Test(expected = IllegalArgumentException.class)
  public void testInstantiationWithShortBase1() {
    new NumberSystemConverter(newArrayList('1'), base2);
  }

  @Test(expected = IllegalArgumentException.class)
  public void testInstantiationWithShortBase2() {
    new NumberSystemConverter(base1, newArrayList('1'));
  }

  @Test(expected = IllegalArgumentException.class)
  public void testInstantiationWithBase1ContainsNull() {
    new NumberSystemConverter(newArrayList('1', null), base2);
  }

  @Test(expected = IllegalArgumentException.class)
  public void testInstantiationWithBase2ContainsNull() {
    new NumberSystemConverter(base1, newArrayList('1', null));
  }

  @Test(expected = IllegalArgumentException.class)
  public void testInstantiationWithInvalidBase1() {
    new NumberSystemConverter(newArrayList('1', '1', '1'), base2);
  }

  @Test(expected = IllegalArgumentException.class)
  public void testInstantiationWithInvalidBase2() {
    new NumberSystemConverter(base1, newArrayList('2', '2', '2'));
  }

  @Test(expected = IllegalArgumentException.class)
  public void testInstantiationWithInvalidBase3() {
    new NumberSystemConverter(base1, newArrayList('-', ' ', '2'));
  }

  @Test(expected = IllegalArgumentException.class)
  public void testInstantiationWithInvalidBase4() {
    new NumberSystemConverter(newArrayList('-', ' ', '2'), base2);
  }

  @Test
  public void testToDecimal() {
    assertEquals(100, converter.toDecimal("64").intValue());
    assertEquals(-100, converter.toDecimal("-64").intValue());
  }

  @Test(expected = IllegalArgumentException.class)
  public void testToDecimalWithInvalidDigit() {
    converter.toDecimal("64$");
  }

  @Test
  public void testToBase2() {
    assertEquals("144", converter.toBase2("64"));
    assertEquals("-144", converter.toBase2("-   64"));
  }

  @Test(expected = IllegalArgumentException.class)
  public void testToBase2WithInvalidDigit() {
    converter.toBase2("64$");
  }

  @Test
  public void testDefensiveCopy() {
    base1 = newArrayList(base1);
    base2 = newArrayList(base2);
    converter = new NumberSystemConverter(base1, base2);
    base1.remove(0);
    base2.remove(2);
    assertEquals("144", converter.toBase2("64"));
  }

  @Test
  public void testEquality() {
    new EqualsTester()
        .addEqualityGroup(converter, new NumberSystemConverter(base1, base2),
            new NumberSystemConverter(newArrayList(base1), newArrayList(base2)))
        .testEquals();
  }

  @Test
  public void testToString() {
    assertEquals("A number system converter from Base1" + base1 + " to "
        + "Base2" + base2, converter.toString());
  }

  @Test
  public void testNPEs() throws Exception {
    new NullPointerTester()
        .testAllPublicConstructors(NumberSystemConverter.class);
    new NullPointerTester()
        .ignore(NumberSystemConverter.class.getMethod("equals", Object.class))
        .testAllPublicInstanceMethods(converter);
  }

  @Test
  public void convert16To36AndViceVersa() {
    NumberSystemConverter c16To36 =
        new NumberSystemConverter(NumberSystem.HEX, NumberSystem.BASE_36);
    NumberSystemConverter c36To16 =
        new NumberSystemConverter(NumberSystem.BASE_36, NumberSystem.HEX);
    assertEquals("HEYIZX48OCXMNEVBYWHNGVOS",
        c16To36.toBase2("082bdebfb715b55ae18cee4558cbdf6c".toUpperCase()));
    assertEquals("82bdebfb715b55ae18cee4558cbdf6c".toUpperCase(),
        c36To16.toBase2("HEYIZX48OCXMNEVBYWHNGVOS"));
  }

  @Test
  public void testGetBase1() {
    assertEquals(NumberSystem.HEX, converter.getBase1());
  }

  @Test
  public void testGetBase2() {
    assertEquals(NumberSystem.OCT, converter.getBase2());
  }

  @Test
  public void testGetReversedConverter() {
    assertEquals(converter.getReversedConverter(),
        new NumberSystemConverter(NumberSystem.OCT, NumberSystem.HEX));
  }

}
