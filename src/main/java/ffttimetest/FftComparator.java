// Copyright (C) 2019 Alexander Karpeko

package ffttimetest;

import java.util.Arrays;

public class FftComparator {
    /**
     * Compare fourier transform algorithms: fft, rfft and ft. 
     * Find error vector magnitude (EVM) between fft and rfft, and between fft and ft.
     * EVM for constant part of the signal and
     * EVM for variable part of the signal are calculated separately.
     * Repeat FFT algorithms to get min time, average time and max time.
     * Input data real part is random, image part is zero.
     * Fourier transform length start from 4 and increase to pow(2, maxPowerOf2).
     *
     * @param maxPowerOf2 Determine max fourier transform length.
     * @param fftCycles   Cycles number for fft and rfft.
     * @return text table with EVM and fourier transform time.
     *
     * evm[][2][2] Array[powerOf2][algorithmsPair][signalPart],
     * algorithmsPair: 0 - rfft and fft, 1 - ft and fft,
     * signalPart: 0 - constant, 1 - variable.
     * evm: dB
     * ftTime[][7] Array[powerOf2][param], param: 0 - minFftTime, 1 - avgFftTime,
     * 2 - maxFftTime, 3 - minRfftTime, 4 - avgRfftTime, 5 - maxRfftTime, 6 - FtTime.
     * ftTime: microseconds
     */
    public static String run(int maxPowerOf2, int fftCycles) {
        if (maxPowerOf2 < 1) {
            throw new IllegalArgumentException("maxPowerOf2 should be greater or equal 1");
        }

        if (fftCycles < 1) {
            throw new IllegalArgumentException("fftCycles should be greater or equal 1");
        }

        final int maxLength = 1 << maxPowerOf2;

        double[][][] evm = new double[maxPowerOf2][2][2];  // evm(rfft, fft), evm(ft, fft)
	    long[][] ftTime = new long[maxPowerOf2][7];        // fft[3], rfft[3], ft

        double[] re = new double[maxLength];
	    double[] im = new double[maxLength];
	    double[][] ftRe = new double[3][maxLength];  // fft, rfft, ft
	    double[][] ftIm = new double[3][maxLength];

        // Error vector magnitude
        for (int i = 1; i < maxPowerOf2; i++) {
            int length = 1 << (i + 1);

            Fft fft = new Fft(length);
            Rfft rfft = new Rfft(length);

            for (int j = 0; j < length; j++) {
                re[j] = Math.random() - 0.5;
                im[j] = 0;
            }

            fft.run(re, im, ftRe[0], ftIm[0]);
            rfft.run(re, ftRe[1], ftIm[1]);
            Ft.run(re, im, ftRe[2], ftIm[2], length, length);

            int n = length / 2;  // real input signal

            for (int j = 0; j < 2; j++) {
                evm[i][j][0] = errorVectorMagnitude(ftRe[0], ftIm[0], ftRe[j+1], ftIm[j+1], 0, 1);
                evm[i][j][1] = errorVectorMagnitude(ftRe[0], ftIm[0], ftRe[j+1], ftIm[j+1], 1, n);
            }
        }

        // Fourier transform time
        for (int i = 1; i < maxPowerOf2; i++) {
            int length = 1 << (i + 1);
            Fft fft = new Fft(length);
            Rfft rfft = new Rfft(length);
            long[] delta = new long[fftCycles];

            for (int ftType = 0; ftType < 3; ftType++) {
                for (int j = 0; j < fftCycles; j++) {
                    for (int k = 0; k < length; k++) {
                        re[k] = Math.random() - 0.5;
                        im[k] = 0;
                    }
                    long t1 = System.nanoTime();
                    switch (ftType) {
                        case 0:
                            fft.run(re, im, ftRe[0], ftIm[0]);
                            break;
                        case 1:
                            rfft.run(re, ftRe[1], ftIm[1]);
                            break;
                        case 2:
                            Ft.run(re, im, ftRe[2], ftIm[2], length, length);
                    }
                    long t2 = System.nanoTime();
                    delta[j] = Math.abs(t2 - t1) / 1000;  // microseconds
                    if (ftType < 2 && j == fftCycles - 1) {
                        ftTime[i][3*ftType] = Arrays.stream(delta).min().getAsLong();
                        ftTime[i][3*ftType+1] = (long) Arrays.stream(delta).average().getAsDouble();
                        ftTime[i][3*ftType+2] = Arrays.stream(delta).max().getAsLong();
                    }
                    if (ftType == 2) {
                        ftTime[i][6] = delta[0];
                        break;
                    }
                }
            }
        }

        return table(maxPowerOf2, fftCycles, evm, ftTime);
    }

    /**
     * Error vector magnitude (EVM) between parts of arrays,
     * determine by params: first, last.
     *
     * @return EVM in dB.
     * If error is zero, then return -900 dB.
     */
    private static double errorVectorMagnitude(double[] re, double[] im, double[] re2,
                                               double[] im2, int first, int last) {
        double errorEnergy = 0;
        double energy = 0;

        for (int i = first; i < last; i++) {
            errorEnergy += Math.pow((re[i] - re2[i]), 2) + Math.pow((im[i] - im2[i]), 2);
            energy += Math.pow(re[i], 2) + Math.pow(im[i], 2);
            energy += Math.pow(re2[i], 2) + Math.pow(im2[i], 2);  // 2 * energy of signal
        }

        double relativeError = 2 * errorEnergy / energy;

        double evm = 10 * Math.log10(relativeError);  // dB

        if (evm == Double.NEGATIVE_INFINITY) {
            evm = -900;
        }

        return evm;
    }

    /**
     * Convert evm and ftTime arrays to text table.
     */
    private static String table(int maxPowerOf2, int fftCycles, double[][][] evm, long[][] ftTime) {
        String text = "";

        text += String.format("Fourier transform algorithms: fft, rfft and ft.\n\n");
        text += String.format("FFT cycles: %d   EVM: dB   Time: microseconds\n\n", fftCycles);
        text += String.format("Length   EVM(rfft,fft)    EVM(ft,fft)        ");
        text += String.format("Time(fft)            Time(rfft)       Time(ft)\n");
        text += String.format("         Const    Var    Const    Var    ");
        text += String.format("min    avg    max    min    avg    max\n");

        for (int i = 1; i < maxPowerOf2; i++) {
            text += String.format("%5d %7.0f %7.0f %7.0f %7.0f ", 1 << (i + 1),
                                  evm[i][0][0], evm[i][0][1], evm[i][1][0], evm[i][1][1]);
            for (int j = 0; j < 6; j++) {
                text += String.format("%6d ", ftTime[i][j]);
            }
            text += String.format("%10d\n", ftTime[i][6]);
        }

        return text;
    }
}
