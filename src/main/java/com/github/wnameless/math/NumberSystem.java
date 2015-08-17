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

import java.util.List;

import com.google.common.collect.ImmutableList;

/**
 * 
 * {@link NumberSystem} provides all coding characters in common used number
 * systems.
 * 
 */
public final class NumberSystem {

  /**
   * Binary digit system.
   */
  public static final List<Character> BIN = ImmutableList.of('0', '1');

  /**
   * Octal digit system.
   */
  public static final List<Character> OCT =
      ImmutableList.of('0', '1', '2', '3', '4', '5', '6', '7');

  /**
   * Decimal digit system.
   */
  public static final List<Character> DEC =
      ImmutableList.of('0', '1', '2', '3', '4', '5', '6', '7', '8', '9');

  /**
   * Hexical digit system.
   */
  public static final List<Character> HEX = ImmutableList.of('0', '1', '2', '3',
      '4', '5', '6', '7', '8', '9', 'A', 'B', 'C', 'D', 'E', 'F');

  /**
   * 36-based digit system.
   */
  public static final List<Character> BASE_36 =
      ImmutableList.of('0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'A',
          'B', 'C', 'D', 'E', 'F', 'G', 'H', 'I', 'J', 'K', 'L', 'M', 'N', 'O',
          'P', 'Q', 'R', 'S', 'T', 'U', 'V', 'W', 'X', 'Y', 'Z');

  private NumberSystem() {}

}
