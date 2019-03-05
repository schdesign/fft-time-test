// Copyright (C) 2019 Alexander Karpeko

// fft: complex input, complex output.
// Max input array length = 2^20

package ffttimetest;

public class Fft {
    public Fft(int fftLength) {
        final int maxPowerOf2 = 20;

        length = fftLength;
        powerOf2 = 0;

        for (int i = 1; i <= maxPowerOf2; i++) {
            if (length == 1 << i) {
                powerOf2 = i;
            }
        }

        if (powerOf2 == 0) {
            throw new IllegalArgumentException("fftLength should be power of 2");
        }

        fft2Re = new double[length];
        fft2Im = new double[length];
        c = new double[length];
        s = new double[length];

        double delta = 2 * Math.PI / length;

        for (int i = 0; i < length / 2; i++) {
            c[i] = Math.cos(i * delta);
            s[i] = Math.sin(i * delta);
        }
    }

    public void run(double[] re, double[] im, double[] fftRe, double[] fftIm) {
        int n1, n2, n3;
        int f;
        double[] re1 = fftRe;
        double[] im1 = fftIm;
        double[] re2 = fft2Re;
        double[] im2 = fft2Im;
        double[] tmpRef = fftRe;

        for (int i = 0; i < length; i++) {
            n1 = i;
            n2 = 0;
            for (int j = 0; j < powerOf2; j++) {
                n2 <<= 1;
                if (n1 % 2 == 1) {
                    n2++;
                }
                n1 >>= 1;
            }
            re1[i] = re[n2];
            im1[i] = im[n2];
        }

        for (int i = 0; i < powerOf2; i++) {
            n1 = 1 << i;
            n2 = 2 * n1;
            n3 = 1 << (powerOf2 - i - 1);
            for (int j = 0; j < length - n1; j += n2) {
                for (int k = 0; k < n1; k++) {
                    f = n3 * k;
                    int n4 = j + k;
                    int n5 = n1 + n4;
                    re2[n4] = re1[n4] + c[f] * re1[n5] + s[f] * im1[n5];
                    im2[n4] = im1[n4] - s[f] * re1[n5] + c[f] * im1[n5];
                    re2[n5] = re1[n4] - c[f] * re1[n5] - s[f] * im1[n5];
                    im2[n5] = im1[n4] + s[f] * re1[n5] - c[f] * im1[n5];
                }
            }
            if (i < powerOf2 - 1) {
                tmpRef = re1;
                re1 = re2;
                re2 = tmpRef;
                tmpRef = im1;
                im1 = im2;
                im2 = tmpRef;
            }
        }

        if (fftRe != re2) {
            for (int i = 0; i < length; i++) {
                fftRe[i] = re2[i];
                fftIm[i] = im2[i];
            }
        }
    }

    private int length;
    private int powerOf2;
    private double[] fft2Re;
    private double[] fft2Im;
    private double[] c;
    private double[] s;
}
