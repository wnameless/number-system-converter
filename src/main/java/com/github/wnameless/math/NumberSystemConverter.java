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

import static com.google.common.base.MoreObjects.firstNonNull;
import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.collect.Lists.newArrayList;
import static com.google.common.collect.Sets.newLinkedHashSet;

import java.math.BigDecimal;
import java.util.List;
import java.util.Set;

import com.google.common.base.Objects;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Iterables;
import com.google.common.primitives.Chars;

/**
 * 
 * {@link NumberSystemConverter} converts any number from arbitrary number
 * system 1 to arbitrary number system 2.<br>
 * Ex: OCT to HEX.<br>
 * <br>
 * Each digit of either radix can be self-defined freely.
 *
 */
public final class NumberSystemConverter {

  private final List<Character> radix1;
  private final List<Character> radix2;
  private NumberSystemConverter reversedConverter;

  /**
   * Creates a number system converter.
   * 
   * @param radix1
   *          a List of each digit character in radix 1, the 0-based order of
   *          each digit is corresponding to its decimal value
   * @param radix2
   *          a List of each digit character in radix 2, the 0-based order of
   *          each digit is corresponding to its decimal value
   */
  public NumberSystemConverter(List<Character> radix1, List<Character> radix2) {
    checkNotNull(radix1);
    checkNotNull(radix2);

    checkArgument(radix1.size() >= 2 && radix2.size() >= 2,
        "All radice must include more than 2 digits.");
    checkArgument(!radix1.contains(null) && !radix2.contains(null),
        "All radice must NOT include null.");

    Set<Character> set1 = newLinkedHashSet(radix1);
    Set<Character> set2 = newLinkedHashSet(radix2);

    checkArgument(radix1.size() == set1.size() && radix2.size() == set2.size(),
        "All digits of a radix must be unique.");

    List<Character> invalidChars = ImmutableList.of(' ', '-');
    checkArgument(
        !Iterables.removeAll(set1, invalidChars)
            && !Iterables.removeAll(set2, invalidChars),
        "All digits of a radix must NOT include whitespace ' ' or minus sign '-'.");

    this.radix1 = ImmutableList.copyOf(radix1);
    this.radix2 = ImmutableList.copyOf(radix2);
  }

  private void checkNoInvalidDigit(String radix1Digits) {
    List<Character> inputChars =
        newArrayList(Chars.asList(radix1Digits.toCharArray()));
    Iterables.removeAll(inputChars, radix1);
    checkArgument(inputChars.size() == 0, "Input contains invalid digit.");
  }

  /**
   * Returns a decimal value of input radix 1 number.
   * 
   * @param radix1Digits
   *          any radix 1 number
   * @return a decimal
   */
  public BigDecimal toDecimal(String radix1Digits) {
    checkNotNull(radix1Digits);

    String input = radix1Digits.replaceFirst("^- *", "");
    boolean isNegtive = input.length() < radix1Digits.length();

    checkNoInvalidDigit(input);

    BigDecimal decimal = new BigDecimal(0);
    BigDecimal base = new BigDecimal(radix1.size());
    int exponent = 0;
    for (int i = input.length() - 1; i >= 0; i--) {
      BigDecimal pos = new BigDecimal(radix1.indexOf(input.charAt(i)));
      decimal = decimal.add(pos.multiply(base.pow(exponent)));
      exponent++;
    }

    return isNegtive ? decimal.negate() : decimal;
  }

  /**
   * Returns a radix 2 number of input radix 1 number.
   * 
   * @param radix1Digits
   *          any radix 1 number
   * @return a radix 2 number
   * @deprecated please use {@link #toRadix2(String)} instead
   */
  @Deprecated
  public String toBase2(String radix1Digits) {
    return toRadix2(radix1Digits);
  }

  /**
   * Returns a radix 2 number of input radix 1 number.
   * 
   * @param radix1Digits
   *          any radix 1 number
   * @return a radix 2 number
   */
  public String toRadix2(String radix1Digits) {
    checkNotNull(radix1Digits);

    String input = radix1Digits.replaceFirst("^- *", "");
    boolean isNegtive = input.length() < radix1Digits.length();

    checkNoInvalidDigit(input);

    StringBuilder outputNumberStr = new StringBuilder();
    BigDecimal number = toDecimal(input);

    BigDecimal base = new BigDecimal(radix2.size());
    while (number.compareTo(base) >= 0) {
      BigDecimal[] qAndR = number.divideAndRemainder(base);
      BigDecimal quotient = qAndR[0];
      BigDecimal remainder = qAndR[1];

      outputNumberStr.insert(0, radix2.get(remainder.intValue()));
      number = quotient;
    }
    outputNumberStr.insert(0, radix2.get(number.intValue()));

    if (isNegtive) outputNumberStr.insert(0, '-');

    return outputNumberStr.toString();
  }

  /**
   * Returns the input radix.
   * 
   * @return radix1
   */
  public List<Character> getRadix1() {
    return radix1;
  }

  /**
   * Returns the output radix.
   * 
   * @return radix2
   */
  public List<Character> getRadix2() {
    return radix2;
  }

  /**
   * Returns a {@link NumberSystemConverter} which converts numbers from radix2
   * to radix1.
   * 
   * @return a reversed {@link NumberSystemConverter}
   */
  public NumberSystemConverter getReversedConverter() {
    return firstNonNull(reversedConverter,
        reversedConverter = new NumberSystemConverter(radix2, radix1));
  }

  @Override
  public int hashCode() {
    return Objects.hashCode(radix1, radix2);
  }

  @Override
  public boolean equals(Object o) {
    if (o == this) return true;
    if (o == null) return false;
    if (!(o instanceof NumberSystemConverter)) return false;

    NumberSystemConverter other = (NumberSystemConverter) o;
    return Objects.equal(radix1, other.radix1)
        && Objects.equal(radix2, other.radix2);
  }

  @Override
  public String toString() {
    return "A number system converter from Base1" + radix1 + " to " + "Base2"
        + radix2;
  }

}
