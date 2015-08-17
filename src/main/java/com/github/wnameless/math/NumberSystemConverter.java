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

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;
import static net.sf.rubycollect4j.RubyCollections.newRubyArray;
import static net.sf.rubycollect4j.RubyCollections.ra;

import java.math.BigDecimal;
import java.util.List;

import com.google.common.base.Objects;
import com.google.common.primitives.Chars;

import net.sf.rubycollect4j.RubyArray;

/**
 * 
 * {@link NumberSystemConverter} is a number system converter which converts any
 * number from arbitrary number system 1 to arbitrary number system 2. Ex: OCT
 * to HEX. <br>
 * <br>
 * Each digit of either base can be self-defined freely.
 *
 */
public final class NumberSystemConverter {

  private final RubyArray<Character> base1;
  private final RubyArray<Character> base2;

  /**
   * Creates a number system converter.
   * 
   * @param base1
   *          an List of each digit character in base 1, the 0-based order of
   *          each digit is corresponding to its decimal value
   * @param base2
   *          an List of each digit character in base 2, the 0-based order of
   *          each digit is corresponding to its decimal value
   */
  public NumberSystemConverter(List<Character> base1, List<Character> base2) {
    checkNotNull(base1);
    checkNotNull(base2);

    checkArgument(base1.size() >= 2 && base2.size() >= 2,
        "All bases must include more than 2 digits.");
    checkArgument(!base1.contains(null) && !base2.contains(null),
        "All bases must NOT include null.");

    this.base1 = RubyArray.copyOf(base1);
    this.base2 = RubyArray.copyOf(base2);

    checkArgument(//
        this.base1.uniqǃ() == null && this.base2.uniqǃ() == null,
        "All digits of a base must be unique.");
    checkArgument(//
        this.base1.intersection(ra(' ', '-')).isEmpty()
            && this.base2.intersection(ra(' ', '-')).isEmpty(),
        "All digits of a base must NOT include whitespace ' ' or minus sign '-'.");

    this.base1.freeze();
    this.base2.freeze();
  }

  private void checkNoInvalidDigit(String base1Digits) {
    List<Character> inputChars = Chars.asList(base1Digits.toCharArray());
    checkArgument(//
        newRubyArray(inputChars).minus(base1).size() == 0,
        "Input contains invalid digit.");
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

    return isNegtive ? decimal.multiply(new BigDecimal(-1)) : decimal;
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
