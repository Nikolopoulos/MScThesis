/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package util;

/**
 *
 * @author billaros
 */
public class Util {

    public static int sum(int[] array) {
        int summary = 0;
        for (int x : array) {
            summary += x;
        }
        return summary;
    }

    public static double median(int[] array) {
        return sum(array) / array.length;
    }

    public static long getTime() {
        return System.currentTimeMillis();
    }

    public static double a2d2engi(int thermistor) {
        double adc = thermistor;
        double Rthr = 10000 * (1023 - adc) / adc;
        return Rthr;
    }

    public static double a2d2celsius(int reading) {

        double temperature, a, b, c, Rthr;
        a = 0.001307050;
        b = 0.000214381;
        c = 0.000000093;
        Rthr = a2d2engi(reading);
        temperature = 1 / (a + b * Math.log(Rthr) + c * Math.pow(Math.log(Rthr), 3));
        temperature -= 273.15; //Convert from Kelvin to Celcius
       /* printf(
         “debug:
         a =  % f b =  % f Rt =  % f temp =  % f\n”, a, b, c,Rt, temperature
         );*/
        return temperature;

    }
}
