// Copyright (C) 2019 Alexander Karpeko

// rfft: real input, complex output.
// Max input array length = 2^20

package ffttimetest;

public class Rfft {
    public Rfft(int rfftLength) {
        final int maxPowerOf2 = 20;

        length = rfftLength;
        int power = 0;

        for (int i = 1; i <= maxPowerOf2; i++) {
            if (length == 1 << i) {
                power = i;
            }
        }

        if (power == 0) {
            throw new IllegalArgumentException("rfftLength should be power of 2");
        }

        length2 = length / 2;
        fft = new Fft(length2);

        rfft2Re = new double[length2];
        rfft2Im = new double[length2];
        c = new double[length2];
        s = new double[length2];

        double delta = Math.PI / length2;

        for (int i = 0; i < length2; i++) {
            c[i] = Math.cos(i * delta);
            s[i] = Math.sin(i * delta);
        }
    }

    public void run(double[] re, double[] rfftRe, double[] rfftIm) {
        double im1, im2;
        double re1, re2;

        for (int i = 0; i < length2; i++) {
            rfftRe[i] = re[2*i];
            rfftIm[i] = re[2*i+1];
        }

        fft.run(rfftRe, rfftIm, rfft2Re, rfft2Im);

        rfftRe[0] = rfft2Re[0] + c[0] * rfft2Im[0];
        rfftIm[0] = -s[0] * rfft2Im[0];

        for (int i = 1; i < length2; i++) {
            re1 = (rfft2Re[i] + rfft2Re[length2-i]) / 2;
            re2 = (rfft2Re[i] - rfft2Re[length2-i]) / 2;
            im1 = (rfft2Im[i] + rfft2Im[length2-i]) / 2;
            im2 = (rfft2Im[i] - rfft2Im[length2-i]) / 2;
            rfftRe[i] = re1 + c[i] * im1 - s[i] * re2;
            rfftIm[i] = im2 - s[i] * im1 - c[i] * re2;
        }
    }

    private int length;
    private int length2;
    private Fft fft;
    private double[] rfft2Re;
    private double[] rfft2Im;
    private double[] c;
    private double[] s;
}
