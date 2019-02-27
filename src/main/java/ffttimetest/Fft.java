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
            fftRe[i] = re[n2];
            fftIm[i] = im[n2];
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
                    fft2Re[n4] = fftRe[n4] + c[f] * fftRe[n5] + s[f] * fftIm[n5];
                    fft2Im[n4] = fftIm[n4] - s[f] * fftRe[n5] + c[f] * fftIm[n5];
                    fft2Re[n5] = fftRe[n4] - c[f] * fftRe[n5] - s[f] * fftIm[n5];
                    fft2Im[n5] = fftIm[n4] + s[f] * fftRe[n5] - c[f] * fftIm[n5];
                }
            }
            for (int j = 0; j < length; j++) {
                fftRe[j] = fft2Re[j];
                fftIm[j] = fft2Im[j];
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
