package application;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;

import javafx.beans.property.ReadOnlyDoubleProperty;
import javafx.beans.property.ReadOnlyDoubleWrapper;
import javafx.concurrent.Task;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

public class Codage{

    protected String content;
    protected ArrayList<Integer> cartes;
    protected ArrayList<Integer> cartesDec;
    protected ArrayList<Integer> contentASCII;
    protected ArrayList<Integer> mCode;
    protected ArrayList<Integer> clef;
    protected ArrayList<Integer> mDecode;
    protected String mCodeStr;
    protected String result;
    protected String fname;
    protected final int JN = 53;
    protected final int JR = 54;
    protected int NBCHARS = 35;
    private final ReadOnlyDoubleWrapper progress = new ReadOnlyDoubleWrapper();

    public Codage(String content){
            this.content = content.toUpperCase();
            this.cartes = new ArrayList<Integer>();
            this.contentASCII = new ArrayList<Integer>();
            this.mCode = new ArrayList<Integer>();
            this.clef = new ArrayList<Integer>();
            this.mDecode = new ArrayList<Integer>();
            this.cartesDec = new ArrayList<Integer>();
    }

    public void init(){
        //Initialisation des cartes.
        for(int i = 1; i <= 54;i++){
            cartes.add(i);
        }

        Collections.shuffle(cartes);

        //Génération de la liste des caractères correspondant au message au format ASCII

        this.stringToArray(this.contentASCII,this.content);

        //Génération de la clef de codage
        
        if(this.fname != null){
            saveDeck(this.fname);
            this.generateKey(this.cartes);
        }else{
            ArrayList<Integer> temp = new ArrayList(this.cartes);           
            this.generateKey(this.cartes);
            this.cartes = new ArrayList<>(temp);
        }
    }

    public void initDecodageDistance(String filename){
        this.setDeckFromFile(filename, this.cartesDec);
    }

    public void etape1(ArrayList<Integer> deck){
        int idx = deck.indexOf(JN);

        deck.remove(idx);
        if(idx == 53)
            deck.add(1, JN);
        else
            deck.add(idx+1,JN);
        //System.out.println(cartes.toString());
    }

    public void etape2(ArrayList<Integer> deck){
        int idx = deck.indexOf(JR);

        deck.remove(idx);
        if(idx == 53){
            deck.add(2, JR);
        }else if(idx == 52)
            deck.add(1, JR);
        else
            deck.add(idx+2, JR);
        //System.out.println(cartes.toString());
    }

    public void etape3(ArrayList<Integer> deck){
        int idxJR = deck.indexOf(JR);
        int idxJN = deck.indexOf(JN);

        int idx1 = idxJR < idxJN ? idxJR : idxJN;
        int idx2 = idxJR > idxJN ? idxJR : idxJN;

        List<Integer> split2 = deck.subList(idx2+1, deck.size());
        ArrayList<Integer> split2Cp = new ArrayList(split2);
        split2.clear();
        List<Integer> split1 = deck.subList(0, idx1);
        ArrayList<Integer> split1Cp = new ArrayList(split1);
        split1.clear();

        deck.addAll(split1Cp);
        deck.addAll(0, split2Cp);
        //System.out.println(cartes.toString());
    }

    public void etape4(ArrayList<Integer> deck){
        int val = deck.get(deck.size() - 1);

        List<Integer> split = deck.subList(0, val-1);
        ArrayList<Integer> splitCp = new ArrayList(split);
        split.clear();
        deck.addAll(deck.size()-1,splitCp);
        //System.out.println(cartes.toString());
    }

    public int etape5(ArrayList<Integer> deck){
        int val = deck.get(0);
        int valeur = deck.get(val-1);
        int res = 0;
        if(valeur == JR || valeur == JN)
            valeur = operationsFluxClef(deck);


        if(valeur > NBCHARS)
            res = valeur-NBCHARS;
        else
            res = valeur;
        //System.out.println(cartes.toString());
        return res;
    }

    public int operationsFluxClef(ArrayList<Integer> deck){
        etape1(deck);
        etape2(deck);
        etape3(deck);
        etape4(deck);
       return etape5(deck);
    }
    
    public void generateKey(ArrayList<Integer> deck){
    	for(int i = 0; i < this.contentASCII.size(); i++)
    		this.clef.add(this.operationsFluxClef(deck));
    }

    public void stringToArray(ArrayList<Integer> array,String message){
        int lg = message.length();
        progress.set(0);
        for(int i = 0; i < lg; i++){
        	progress.set(1.0*i / lg);
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
            else if(message.charAt(i) == 13) {
            	if(i != lg-1 && message.charAt(i+1) == 10) {
            		array.add(35);
            		i++;
            	}
            }else
                array.add(((int) message.charAt(i)) - 64);
        }
        
    }

    public String arrayToString(ArrayList<Integer> array){
    	Map<Integer,String> m = new HashMap<>();
    	m.put(0, "\r\n");
    	for(int i = 1; i < 27; i++)
    		m.put(i, ""+(char)(i+64));
    	
    	m.put(27, " ");
    	m.put(28, "'");
    	m.put(29, ".");
    	m.put(30, ",");
    	m.put(31, "!");
    	m.put(32, "?");
    	m.put(33, ":");
    	m.put(34, ";");
    	
    	
    	progress.set(0);
        int lg = array.size();
        StringBuilder message = new StringBuilder(); 
        
        for(int i = 0; i < lg; i++){
        	progress.set(1.0*i / lg);
        	message.append(m.get(array.get(i)));
       }

        return message.toString();
    }

    public void codage(){

        int lg = this.contentASCII.size();
        int val;
        for(int i =0; i < lg; i++){
           val = ((this.contentASCII.get(i) + this.clef.get(i)) % NBCHARS) + 1;
           this.mCode.add(val);
        }

        this.mCodeStr = this.arrayToString(this.mCode);
        this.clef.clear();
    }

    public void decodage(){
        if(this.fname != null){
            
            setDeckFromFile(this.fname, this.cartesDec);
            
            generateKey(this.cartesDec);
            
        }
        else
            generateKey(this.cartes);
        
        int lg = this.mCode.size();
        
        
        int val;
        for(int i =0; i < lg; i++){
            val = (this.mCode.get(i) - this.clef.get(i)) % NBCHARS -1;
            if(val < 0){
                val += NBCHARS;
            }
      
            this.mDecode.add(val);
        }

        this.result = this.arrayToString(this.mDecode);
    }
    
    public void saveDeck(String filename){
        PrintWriter writer;
        try {
            String extension = filename.lastIndexOf(".") > 0? filename.substring(filename.lastIndexOf(".")).toLowerCase() : "";
            filename = filename.lastIndexOf(".") > 0 ? filename.substring(0,filename.lastIndexOf(".")).toLowerCase() + "_deck" + extension : "";
            writer = new PrintWriter(filename, "UTF-8");
            writer.print(this.cartes.toString());
            writer.close();
        } catch (FileNotFoundException | UnsupportedEncodingException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        // TODO Auto-generated catch block
        
    }
    
    public void setDeckFromFile(String filename, ArrayList<Integer> array){
        BufferedReader reader = null;
        try {
            reader = Files.newBufferedReader(Paths.get(filename), StandardCharsets.UTF_8);
            String line;
            String data = "";
            while ((line = reader.readLine()) != null) {
                data += line +"\n";
            }
            String[] numbers = data.split("\\[")[1].split(",");
            for(int i = 1; i < numbers.length; i++){
                StringBuilder temp = new StringBuilder(numbers[i]);
                temp.deleteCharAt(0);
                numbers[i] = temp.toString();
            }
            numbers[numbers.length-1] = numbers[numbers.length-1].substring(0,numbers[numbers.length-1].length()-2);

            for (String number : numbers) {
                array.add(Integer.parseUnsignedInt(number));
            }

        } catch (IOException ex) {
            Logger.getLogger(Codage.class.getName()).log(Level.SEVERE, null, ex);
        } finally {
            try {
                reader.close();
            } catch (IOException ex) {
                Logger.getLogger(Codage.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }

    public double getProgress() {
        return progressProperty().get();
    }

    public ReadOnlyDoubleProperty progressProperty() {
        return progress ;
    }

    public String getMessageCode(){
    	return this.mCodeStr;
    }

    public String getMessageDecode(){
    	return this.result;
    }

    public String getCleStr(){
    	return this.arrayToString(this.clef);
    }

    public void setContent(String content){
    	this.content = content;
    }
    
    public void setFname(String fname) {
    	this.fname = fname;
    }
}
