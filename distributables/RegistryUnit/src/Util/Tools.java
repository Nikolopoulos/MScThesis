/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Util;

/**
 *
 * @author billaros
 */
public class Tools {
    public static String byteArrayToString(byte[] convert){
        String result="";
        
        for(byte val: convert){
            result+= (char) val;
        }
        
        return result;
    }
}
