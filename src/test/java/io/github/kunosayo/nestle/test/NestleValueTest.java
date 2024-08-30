package io.github.kunosayo.nestle.test;


import io.github.kunosayo.nestle.data.NestleValue;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;


public class NestleValueTest {

    @Test
    public void testGetIndex() {

        Assertions.assertEquals(0, NestleValue.getIndex(0.5));



        // *  *      *               *               Last Mark
        // 1  2  3   4   5   6   7   8   9  10       Distance
        // 1  4  9  16  25  36  49  64  61 100       Squared Distance
        // 0  1  2   2   3   3   3   3   4   4       Index

        Assertions.assertEquals(2, NestleValue.getIndex(9));
        Assertions.assertEquals(3, NestleValue.getIndex(25));
        Assertions.assertEquals(3, NestleValue.getIndex(36));
        Assertions.assertEquals(3, NestleValue.getIndex(49));
        Assertions.assertEquals(4, NestleValue.getIndex(81));
        Assertions.assertEquals(4, NestleValue.getIndex(100));

        for (int i = 0; i < 16; i++) {
            Assertions.assertEquals(i, NestleValue.getIndex((1 << (i << 1))));

            Assertions.assertEquals(i, NestleValue.getIndex((1 << (i << 1)) - 0.5));

            Assertions.assertEquals(i + 1, NestleValue.getIndex((1 << (i << 1)) + 0.5));

            Assertions.assertEquals(i, NestleValue.getIndex((1 << (i << 1)) - 0.1));
            Assertions.assertEquals(i + 1, NestleValue.getIndex((1 << (i << 1)) + 0.1));
        }

        Assertions.assertEquals(16, NestleValue.getIndex((1L << (16 << 1)) - 0.1));
        Assertions.assertEquals(16, NestleValue.getIndex((1L << (16 << 1)) + 0.1));

        Assertions.assertEquals(16, NestleValue.getIndex((1L << (17 << 1)) - 0.1));
        Assertions.assertEquals(16, NestleValue.getIndex((1L << (17 << 1)) + 0.1));
        Assertions.assertEquals(16, NestleValue.getIndex((1L << (18 << 1)) + 0.1));
        Assertions.assertEquals(16, NestleValue.getIndex((1L << (19 << 1)) + 0.1));

    }
}
