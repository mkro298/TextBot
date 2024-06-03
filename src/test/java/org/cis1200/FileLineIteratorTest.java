package org.cis1200;

import org.junit.jupiter.api.Test;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.StringReader;
import java.io.BufferedReader;
import java.util.NoSuchElementException;

import static org.junit.jupiter.api.Assertions.*;

/** Tests for FileLineIterator */
public class FileLineIteratorTest {

  

    @Test
    public void testHasNextAndNext() {


        String words = "0, The end should come here.\n"
                + "1, This comes from data with no duplicate words!";
        StringReader sr = new StringReader(words);
        BufferedReader br = new BufferedReader(sr);
        FileLineIterator li = new FileLineIterator(br);
        assertTrue(li.hasNext());
        assertEquals("0, The end should come here.", li.next());
        assertTrue(li.hasNext());
        assertEquals("1, This comes from data with no duplicate words!", li.next());
        assertFalse(li.hasNext());
    }


    @Test
    public void testConstructorNullandInvalidFilePaths() {
        String words = "";
        StringReader sr = new StringReader(words);
        BufferedReader br = new BufferedReader(sr);
        FileLineIterator li = new FileLineIterator(br);
        try {
            li.fileToReader(null);
        } catch (IllegalArgumentException e) {
            assert (true);
        }
        try {
            li.fileToReader("RandomFile");
        } catch (IllegalArgumentException e) {
            assert (true);
        }
    }

    @Test
    public void testValidFile() throws FileNotFoundException {
        FileReader fr = new FileReader("files/noaa_tweets.csv");
        BufferedReader br = new BufferedReader(fr);
        FileLineIterator li = new FileLineIterator(br);
        li.fileToReader("files/noaa_tweets.csv");
        assert (true);
    }

    @Test
    public void testNoNextValueandNoHasNext() {
        String words = "";
        StringReader sr = new StringReader(words);
        BufferedReader br = new BufferedReader(sr);
        FileLineIterator li = new FileLineIterator(br);
        assertFalse(li.hasNext());
        try {
            li.next();
        } catch (NoSuchElementException e) {
            assert (true);
        }
    }

    @Test
    public void testAverageFile() {

        String words = "hi\ncats\nare\ncool";
        StringReader sr = new StringReader(words);
        BufferedReader br = new BufferedReader(sr);
        FileLineIterator li = new FileLineIterator(br);
        assertTrue(li.hasNext());
        assertEquals("hi", li.next());
        assertTrue(li.hasNext());
        assertEquals("cats", li.next());
        assertTrue(li.hasNext());
        assertEquals("are", li.next());
        assertTrue(li.hasNext());
        assertEquals("cool", li.next());
        assertFalse(li.hasNext());
    }
}
