/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package application;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;

/**
 *
 * @author legol
 */
public class CodageFichierImg extends Codage{

    private final String filename;
    private final String extension;
    
    public CodageFichierImg(File f) {
        super("");
        // TODO Auto-generated constructor stub
        this.filename = f.getName().lastIndexOf(".") > 0 ? f.getName().substring(0, f.getName().lastIndexOf(".")) : "";
        this.extension = f.getName().lastIndexOf(".") > 0? f.getName().substring(f.getName().lastIndexOf(".")+1) : "";
        System.out.print(this.extension);
        try {
            byte[] b = Files.readAllBytes(Paths.get(f.getPath()));
            StringBuilder imgBin = new StringBuilder();
            for(byte bn : b){
                imgBin.append(String.format("%8s", Integer.toBinaryString(bn & 0xFF)).replace(' ', '0'));
            }
            
            super.setContent(convertLetterToBin(result).toUpperCase());
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        
        
            
    }
    public static ArrayList<String> splitString(String str){
    ArrayList<String> bins = new ArrayList();
    int inc = 0;
    while(inc < str.length() - 5){
        bins.add(str.substring(inc, inc + 5));
        inc += 5;
    }
    bins.add(str.substring(inc));
    return bins;
    }
     public static String convertBinToLetters(String binStr){
        ArrayList<String> bin = splitString(binStr);
        StringBuilder str = new StringBuilder();
        for(int i = 0; i < bin.size(); i++){
            str.append((char) ('A' + ((char)  Integer.parseInt(bin.get(i), 2))));
        }         
        return str.toString();
    }
     
    public static String convertLetterToBin(String str){
        StringBuilder res = new StringBuilder();
        for(int i = 0; i < str.length(); i++){
            int current = (int)(str.charAt(i) - 'A');
            StringBuilder number = new StringBuilder(Integer.toBinaryString(current));
            while(number.length() != 5 && i != str.length() -1){
                number.insert(0, "0");
            }
            
            res.append(number);
        }
        
        
        return res.toString();
    }
    /* public static ArrayList<Integer> stringToArray(String message){
        int lg = message.length();
        ArrayList<Integer> array = new ArrayList(lg);

        for(int i = 0; i < lg; i++){
            if(message.charAt(i) == 32 ) //Ici on code l'espace
                array.add(27);
            else if(message.charAt(i) == 39) // On code l'apostrophe
                array.add(28);
            else if(message.charAt(i) == 46) // Point
                array.add(29);
            else if(message.charAt(i) == 44) // Virgule
                array.add(30);
            else if(message.charAt(i) == 33) // Point d'exclamation
                array.add(31);
            else if(message.charAt(i) == 63) // Point d'interrogation
                array.add(32);
            else if(message.charAt(i) == 58) // Deux points
                array.add(33);
            else if(message.charAt(i) == 59) // Point virgule
                array.add(34);
            else if(message.charAt(i) == 10) //On code le retour a la ligne
                array.add(35);
            else
                array.add(((int) message.charAt(i)) - 64);
        }

        return array;
    }*/
}
