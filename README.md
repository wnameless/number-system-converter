number-system-converter
=============
An Java utility library of number systems(math).

Example: Convert a decimal to quinary number
```java
NumberSystemConverter c10To5 =
        new NumberSystemConverter(NumberSystem.DEC, Arrays.asList('0', '1', '2', '3', '4'));
System.out.println(c10To5.toBase2("100"));
// Output: 400
```
