package com.aruba.columnchartview;

import org.junit.Test;

import static org.junit.Assert.*;

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
public class ExampleUnitTest {
    @Test
    public void addition_isCorrect() throws Exception {
        assertEquals(4, 2 + 2);

        int maxNumber = 100;
        int showYItemSize = 6;
        int isModZero = maxNumber % showYItemSize;
        if (isModZero != 0) {
            maxNumber = showYItemSize * (maxNumber / showYItemSize + 1);
        }

        System.out.print(maxNumber + "\n");
        System.out.print(maxNumber % 6.0);
    }
}