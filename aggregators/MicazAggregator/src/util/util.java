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
public class util {

    public static int sum(int[] array) {
        int summary=0;
        for(int x:array)
        {
                summary+=x;
        }
        return summary;
    }
    
    public static double median (int[] array)
    {
        return sum(array)/array.length;
    }
    
    public static long getTime(){
        return System.currentTimeMillis();
    }
}
