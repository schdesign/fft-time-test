# fft-time-test

Compare fourier transform algorithms: fft, rfft and ft.

Java 8 project.

Find error vector magnitude (EVM) between fft and rfft, and between fft and ft.
EVM for constant part of signal and
EVM for variable part of signal are calculated separately.
Repeat FFT algorithms to get min time, average time and max time.
Input data real part is random, image part is zero.
Fourier transform length start from 4 and increase to pow(2, 12).
Write result to file "FftTime.txt".
