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
 * Each digit of either base can be self-defined freely.
 *
 */
public final class NumberSystemConverter {

  private final List<Character> base1;
  private final List<Character> base2;
  private NumberSystemConverter reversedConverter;

  /**
   * Creates a number system converter.
   * 
   * @param base1
   *          a List of each digit character in base 1, the 0-based order of
   *          each digit is corresponding to its decimal value
   * @param base2
   *          a List of each digit character in base 2, the 0-based order of
   *          each digit is corresponding to its decimal value
   */
  public NumberSystemConverter(List<Character> base1, List<Character> base2) {
    checkNotNull(base1);
    checkNotNull(base2);

    checkArgument(base1.size() >= 2 && base2.size() >= 2,
        "All bases must include more than 2 digits.");
    checkArgument(!base1.contains(null) && !base2.contains(null),
        "All bases must NOT include null.");

    Set<Character> set1 = newLinkedHashSet(base1);
    Set<Character> set2 = newLinkedHashSet(base2);

    checkArgument(base1.size() == set1.size() && base2.size() == set2.size(),
        "All digits of a base must be unique.");

    List<Character> invalidChars = ImmutableList.of(' ', '-');
    checkArgument(
        !Iterables.removeAll(set1, invalidChars)
            && !Iterables.removeAll(set2, invalidChars),
        "All digits of a base must NOT include whitespace ' ' or minus sign '-'.");

    this.base1 = ImmutableList.copyOf(base1);
    this.base2 = ImmutableList.copyOf(base2);
  }

  private void checkNoInvalidDigit(String base1Digits) {
    List<Character> inputChars =
        newArrayList(Chars.asList(base1Digits.toCharArray()));
    Iterables.removeAll(inputChars, base1);
    checkArgument(inputChars.size() == 0, "Input contains invalid digit.");
  }

  /**
   * Returns a decimal value of input base 1 number.
   * 
   * @param base1Digits
   *          any base 1 number
   * @return a decimal
   */
  public BigDecimal toDecimal(String base1Digits) {
    checkNotNull(base1Digits);

    String input = base1Digits.replaceFirst("^- *", "");
    boolean isNegtive = input.length() < base1Digits.length();

    checkNoInvalidDigit(input);

    BigDecimal decimal = new BigDecimal(0);
    BigDecimal base = new BigDecimal(base1.size());
    int exponent = 0;
    for (int i = input.length() - 1; i >= 0; i--) {
      BigDecimal pos = new BigDecimal(base1.indexOf(input.charAt(i)));
      decimal = decimal.add(pos.multiply(base.pow(exponent)));
      exponent++;
    }

    return isNegtive ? decimal.negate() : decimal;
  }

  /**
   * Returns a base 2 number of input base 1 number.
   * 
   * @param base1Digits
   *          any base 1 number
   * @return a base 2 number
   */
  public String toBase2(String base1Digits) {
    checkNotNull(base1Digits);

    String input = base1Digits.replaceFirst("^- *", "");
    boolean isNegtive = input.length() < base1Digits.length();

    checkNoInvalidDigit(input);

    StringBuilder outputNumberStr = new StringBuilder();
    BigDecimal number = toDecimal(input);

    BigDecimal base = new BigDecimal(base2.size());
    while (number.compareTo(base) >= 0) {
      BigDecimal[] qAndR = number.divideAndRemainder(base);
      BigDecimal quotient = qAndR[0];
      BigDecimal remainder = qAndR[1];

      outputNumberStr.insert(0, base2.get(remainder.intValue()));
      number = quotient;
    }
    outputNumberStr.insert(0, base2.get(number.intValue()));

    if (isNegtive) outputNumberStr.insert(0, '-');

    return outputNumberStr.toString();
  }

  /**
   * Returns the input base.
   * 
   * @return base1
   */
  public List<Character> getBase1() {
    return base1;
  }

  /**
   * Returns the output base.
   * 
   * @return base2
   */
  public List<Character> getBase2() {
    return base2;
  }

  /**
   * Returns a {@link NumberSystemConverter} which converts numbers from base2
   * to base1.
   * 
   * @return a reversed {@link NumberSystemConverter}
   */
  public NumberSystemConverter getReversedConverter() {
    return firstNonNull(reversedConverter,
        reversedConverter = new NumberSystemConverter(base2, base1));
  }

  @Override
  public int hashCode() {
    return Objects.hashCode(base1, base2);
  }

  @Override
  public boolean equals(Object o) {
    if (o == this) return true;
    if (o == null) return false;
    if (!(o instanceof NumberSystemConverter)) return false;

    NumberSystemConverter other = (NumberSystemConverter) o;
    return Objects.equal(base1, other.base1)
        && Objects.equal(base2, other.base2);
  }

  @Override
  public String toString() {
    return "A number system converter from Base1" + base1 + " to " + "Base2"
        + base2;
  }

}
