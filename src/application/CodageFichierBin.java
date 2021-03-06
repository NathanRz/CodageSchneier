/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package application;

import java.awt.Image;
import java.awt.image.BufferedImage;
import java.io.BufferedOutputStream;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.imageio.ImageIO;


public class CodageFichierBin extends Codage{

    private final String filename;
    private final String extension;
    private  String test;
    
    public CodageFichierBin(File f) {
        super("");
        // TODO Auto-generated constructor stub
        this.fname = f.getName();
        this.filename = f.getName().lastIndexOf(".") > 0 ? f.getName().substring(0, f.getName().lastIndexOf(".")) : "";
        this.extension = f.getName().lastIndexOf(".") > 0? f.getName().substring(f.getName().lastIndexOf(".")+1).toLowerCase() : "";
        this.NBCHARS = 32;
        this.test="";
        try {
            byte[] b = Files.readAllBytes(Paths.get(f.getPath()));
            StringBuilder imgBin = new StringBuilder();
            for(byte bn : b){
                imgBin.append(String.format("%8s", Integer.toBinaryString(bn & 0xFF)).replace(' ', '0'));
            }
            this.test = imgBin.toString();
            super.setContent(convertBinToLetters(imgBin.toString()).toUpperCase());
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
            while(number.length() < 5 && i != str.length() -1){
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
        Map<Integer,String> m = new HashMap<>();
        m.put(0, "`");
        for(int i = 1; i < 32; i++)
            m.put(i, ""+(char)(i+64));
        
        int lg = array.size();
        StringBuilder message = new StringBuilder();

        for(int i = 0; i < lg; i++){
            progress.set(1.0*i / 100);
            message.append(m.get(array.get(i)));
       }
        
        return message.toString();
    }
    
    public void saveCodeBinToFile(){
        String binFin = convertLetterToBin(this.mCodeStr);
        byte[] data = new byte[binFin.length()/8 -1];
        for(int i = 0; i < data.length - 1; i++){
            int val = Integer.parseInt(binFin.substring(i*8,(i*8)+8),2);
            data[i] = (byte) val;
        }
        OutputStream out = null;

        try {
            out = new BufferedOutputStream(new FileOutputStream(this.filename + "_code." + this.extension));
            out.write(data);
        } catch (FileNotFoundException ex) {
            Logger.getLogger(CodageFichierBin.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(CodageFichierBin.class.getName()).log(Level.SEVERE, null, ex);
        } finally {
            if (out != null) try {
                out.close();
            } catch (IOException ex) {
                Logger.getLogger(CodageFichierBin.class.getName()).log(Level.SEVERE, null, ex);
            }
        }

    }
    
    public void saveDecodeBinToFile(){
        String binFin = convertLetterToBin(this.result);        
        byte[] data = new byte[binFin.length()/8 -1];
        for(int i = 0; i < data.length - 1; i++){
            int val = Integer.parseInt(binFin.substring(i*8,(i*8)+8),2);
            data[i] = (byte) val;
        }
        OutputStream out = null;

        try {
            String filename = this.fname.lastIndexOf(".") > 0 ? this.fname.substring(0,this.fname.lastIndexOf(".")-5).toLowerCase() + "_decode" + this.extension : "";
            out = new BufferedOutputStream(new FileOutputStream(this.filename + "_decode." + this.extension));
            out.write(data);
            out.close();
        } catch (FileNotFoundException ex) {
            Logger.getLogger(CodageFichierBin.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(CodageFichierBin.class.getName()).log(Level.SEVERE, null, ex);
        }
 
    }
}
