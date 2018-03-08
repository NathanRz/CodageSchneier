/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package application;

import java.awt.Image;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.imageio.ImageIO;

/**
 *
 * @author legol
 */
public class CodageFichierImg extends Codage{

    private final String filename;
    private final String extension;
    private StringBuilder test;
    
    public CodageFichierImg(File f) {
        super("");
        // TODO Auto-generated constructor stub
        this.filename = f.getName().lastIndexOf(".") > 0 ? f.getName().substring(0, f.getName().lastIndexOf(".")) : "";
        this.extension = f.getName().lastIndexOf(".") > 0? f.getName().substring(f.getName().lastIndexOf(".")+1).toLowerCase() : "";
        this.NBCHARS = 32;
        try {
            byte[] b = Files.readAllBytes(Paths.get(f.getPath()));
            test = new StringBuilder();
            for(byte bn : b){
                test.append(String.format("%8s", Integer.toBinaryString(bn & 0xFF)).replace(' ', '0'));
            }
            super.setContent(convertBinToLetters(test.toString()).toUpperCase());
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
    
    @Override
    public void stringToArray(ArrayList<Integer> array, String message){
        int lg = message.length();
        for(int i = 0; i < lg; i++){
            if(message.charAt(i) == 91 ) // [
                array.add(27);
            else if(message.charAt(i) == 92) // \
                array.add(28);
            else if(message.charAt(i) == 93) // ]
                array.add(29);
            else if(message.charAt(i) == 94) // ^
                array.add(30);
            else if(message.charAt(i) == 95) // _
                array.add(31);
            else if(message.charAt(i) == 96) // `
                array.add(32);
            else
                array.add(((int) message.charAt(i)) - 64);
        }
    }
    
    @Override
    public String arrayToString(ArrayList<Integer> array){
        int lg = array.size();
        String message = "";

        for(int i = 0; i < lg; i++){
            if(array.get(i) == 27)
                message += "[";
            else if(array.get(i) == 28)
                message += "\\";
            else if(array.get(i) == 29)
                message += "]";
            else if(array.get(i) == 30)
                message += "^";
            else if(array.get(i) == 31)
                message += "_";
            else if(array.get(i) == 0)
                message += "`";
            else
                message += (char) (array.get(i) + 64);
       }

        return message;
    }
        
    public void saveImgToFile(){
        String binFin = convertLetterToBin(this.result);
        
        byte[] data = new byte[binFin.length()/8 -1];
        for(int i = 0; i < data.length - 1; i++){
            int val = Integer.parseInt(binFin.substring(i*8,(i*8)+8),2);
            data[i] = (byte) val;
        }
        BufferedImage res = null;
            try{
                res = ImageIO.read(new ByteArrayInputStream(data));
                File outputfile = new File(this.filename + "_decode." + this.extension);
                ImageIO.write(res, this.extension, outputfile);


            }catch(IOException e){
                e.printStackTrace();
            }

    }
}
