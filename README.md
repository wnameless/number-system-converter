number-system-converter
=============
An utility library about number systems(math).

Example: Convert a decimal to quinary number
```java
SystemConvertor c10To5 =
        new SystemConvertor(DigitSystem.DEC, Arrays.asList('0', '1', '2', '3', '4'));
System.out.println(c10To5.toBase2("100"));
// Output: 400
```
