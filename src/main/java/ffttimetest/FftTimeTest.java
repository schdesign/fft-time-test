// Copyright (C) 2019 Alexander Karpeko

package ffttimetest;

import java.io.*;

public class FftTimeTest {
    /**
     * Compare fourier transform algorithms: fft, rfft and ft.
     * Fourier transform length start from 4 and increase to pow(2, 12).
     * Write result to file "FftTime.txt"
     */
    public static void main(String[] args) {
        final String filename = "FftTime.txt";
        final int maxPowerOf2 = 12;
        final int fftCycles = 10;
        String table = "";

        try {
            table = FftComparator.run(maxPowerOf2, fftCycles);
        }
        catch (IllegalArgumentException e) {
            System.out.println("Error: " + e);
            return;
        }

        try (FileWriter fw = new FileWriter(filename)) {
            fw.write(table);
        }
        catch (IOException e) {
            System.out.println("Output file error: " + e);
        }
    }
}
