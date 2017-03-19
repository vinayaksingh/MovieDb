package com.vin.moviedb;

import org.junit.Test;

import static org.junit.Assert.*;

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
public class MovieUnitTest {
    @Test
    public void addition_isCorrect() throws Exception {
        assertEquals(4, 2 + 2);
    }

    @Test
    public void testDateFormatting() throws Exception {
        String testDateStringAfterParse = "July 2014";
        String testDateStringFromApi = "2014-07-08";

        assertEquals("Error in parsing date string", testDateStringAfterParse,
                Utility.getUiDateString(testDateStringFromApi));
    }
}