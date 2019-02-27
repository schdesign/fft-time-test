// Copyright (C) 2019 Alexander Karpeko

// ft: complex input, complex output.
// Max input array length = 2^20

package ffttimetest;

public class Ft {
    public static void run(double[] re, double[] im, double[] ftRe,
                           double[] ftIm, int inLength, int outLength) {
        final int maxPowerOf2 = 20;
        final int maxLength = 1 << maxPowerOf2;

        if (inLength < 1 || outLength < 1) {
            throw new IllegalArgumentException("inLength and outLength should be more or equal 1");
        }

	    if (inLength > maxLength) {
            throw new IllegalArgumentException("inLength should be less or equal 2^20");
        }

    	if (outLength > inLength) {
            throw new IllegalArgumentException("outLength should be less or equal inLength");
        }

	    for (int i = 0; i < outLength; i++) {
		    ftRe[i] = 0; 
		    ftIm[i] = 0;
		    for (int j = 0; j < inLength; j++) {
                double f = 2 * Math.PI * i * j / inLength;
                double c = Math.cos(f);
                double s = Math.sin(f);
		    	ftRe[i] +=  re[j] * c + im[j] * s;
		    	ftIm[i] += -re[j] * s + im[j] * c;
		    }
	    }
    }
}
