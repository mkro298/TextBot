package org.cis1200;

import java.util.Iterator;
import java.io.BufferedReader;
import java.io.*;
import java.util.NoSuchElementException;


public class FileLineIterator implements Iterator<String> {

    // Add the fields needed to implement your FileLineIterator
    BufferedReader br;
    boolean next;
   
    public FileLineIterator(BufferedReader reader) {
        // Complete this constructor.
        if (reader == null) {
            throw new IllegalArgumentException();
        }
        br = reader;
        next = true;
    }

    
    public FileLineIterator(String filePath) {
        this(fileToReader(filePath));
    }

    
    public static BufferedReader fileToReader(String filePath) {
        if (filePath == null) {
            throw new IllegalArgumentException();
        }
        File file = new File(filePath);
        try {
            FileReader fr = new FileReader(file);
            BufferedReader br = new BufferedReader(fr);
            return br;
        } catch (FileNotFoundException e) {
            throw new IllegalArgumentException(e);
        }
    }

    
    @Override
    public boolean hasNext() {
        if (!next) {
            return false;
        }
        try {
            br.mark(1);
            if (br.read() == -1) {
                br.close();
                next = false;
            } else {
                br.reset();
                return true;
            }
        } catch (IOException e) {
            System.out.println("Error with closing");
        }
        return false;
    }

    
    @Override
    public String next() {
        if (!hasNext()) {
            throw new NoSuchElementException();
        }
        try {
            return br.readLine();
        } catch (IOException e) {
        }
        return null;
    }
}
