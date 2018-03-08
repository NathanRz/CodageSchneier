package application;

import java.util.ArrayList;

import javafx.beans.property.ReadOnlyDoubleProperty;
import javafx.beans.property.ReadOnlyDoubleWrapper;
import javafx.concurrent.Task;
import java.util.Collections;
import java.util.List;

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

        for(int i = 0; i < cartes.size(); i ++){
            cartesDec.add(0);
        }

        Collections.copy(cartesDec,cartes);

        //Génération de la clef de codage

        this.generateKey();

        //Génération de la liste des caractères correspondant au message au format ASCII

        this.stringToArray(this.contentASCII,this.content);
	}

    public void etape1(){
        int idx = this.cartes.indexOf(JN);

        this.cartes.remove(idx);
        if(idx == 53)
            this.cartes.add(1, JN);
        else
            this.cartes.add(idx+1,JN);
        //System.out.println(cartes.toString());
    }

    public void etape2(){
        int idx = this.cartes.indexOf(JR);

        this.cartes.remove(idx);
        if(idx == 53){
            this.cartes.add(2, JR);
        }else if(idx == 52)
            this.cartes.add(1, JR);
        else
            this.cartes.add(idx+2, JR);
        //System.out.println(cartes.toString());
    }

    public void etape3(){
        int idxJR = this.cartes.indexOf(JR);
        int idxJN = this.cartes.indexOf(JN);

        int idx1 = idxJR < idxJN ? idxJR : idxJN;
        int idx2 = idxJR > idxJN ? idxJR : idxJN;

        List<Integer> split2 = this.cartes.subList(idx2+1, this.cartes.size());
        ArrayList<Integer> split2Cp = new ArrayList(split2);
        split2.clear();
        List<Integer> split1 = this.cartes.subList(0, idx1);
        ArrayList<Integer> split1Cp = new ArrayList(split1);
        split1.clear();

        this.cartes.addAll(split1Cp);
        this.cartes.addAll(0, split2Cp);
        //System.out.println(cartes.toString());
    }

    public void etape4(){
        int val = this.cartes.get(this.cartes.size() - 1);

        List<Integer> split = this.cartes.subList(0, val-1);
        ArrayList<Integer> splitCp = new ArrayList(split);
        split.clear();
        this.cartes.addAll(this.cartes.size()-1,splitCp);
        //System.out.println(cartes.toString());
    }

    public int etape5(){
        int val = this.cartes.get(0);
        int valeur = this.cartes.get(val-1);
        int res = 0;
        if(valeur == JR || valeur == JN)
            valeur = operationsFluxClef();


        if(valeur > NBCHARS)
            res = valeur-NBCHARS;
        else
            res = valeur;
        //System.out.println(cartes.toString());
        return res;
    }

    public int operationsFluxClef(){
        etape1();
        etape2();
        etape3();
        etape4();
       return etape5();
    }

    public void generateKey(){
    	for(int i = 0; i < content.length(); i++)
    		this.clef.add(this.operationsFluxClef());
    }

    public void stringToArray(ArrayList<Integer> array,String message){
        int lg = message.length();

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
        
    }

    public String arrayToString(ArrayList<Integer> array){
        int lg = array.size();
        String message = "";

        for(int i = 0; i < lg; i++){
            if(array.get(i) == 27 )
                message += " ";
            else if(array.get(i) == 28)
                message += "'";
            else if(array.get(i) == 29)
                message += ".";
            else if(array.get(i) == 30)
                message += ",";
            else if(array.get(i) == 31)
                message += "!";
            else if(array.get(i) == 32)
                message += "?";
            else if(array.get(i) == 33)
                message += ":";
            else if(array.get(i) == 34)
                message += ";";
            else if(array.get(i) == 0)
                message += "\r\n";
            else
                message += (char) (array.get(i) + 64);
       }

        return message;
    }

    public void codage(){

        int lg = this.content.length();
        progress.set(0);
        int val;
        for(int i =0; i < lg; i++){

           /*if(code.get(i) == -32 || code.get(i) == -54)
               val = code.get(i);
           else*/
           val = ((this.contentASCII.get(i) + this.clef.get(i)) % NBCHARS) + 1;
           progress.set(1.0*i / 100);
           this.mCode.add(val);
        }

        this.mCodeStr = this.arrayToString(this.mCode);
    }

    public void decodage(){
        int lg = this.mCode.size();
        
        int val;
        for(int i =0; i < lg; i++){
            /*if((messageCode.get(i) == -32) || (messageCode.get(i) == -54))
                val = messageCode.get(i);
            else{*/
            val = (this.mCode.get(i) - this.clef.get(i)) % NBCHARS -1;
            if(val < 0){
                val += NBCHARS;
            }
            //}

            this.mDecode.add(val);
        }

        this.result = this.arrayToString(this.mDecode);
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
}
