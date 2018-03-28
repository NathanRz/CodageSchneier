package application;

import java.io.BufferedReader;
import java.io.File;
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
    //Joker Noir
    protected final int JN = 53;
    //Joker Rouge
    protected final int JR = 54;
    protected int NBCHARS = 35;
    protected final ReadOnlyDoubleWrapper progress = new ReadOnlyDoubleWrapper();

    public Codage(String content){
            this.content = content.toUpperCase();
            this.cartes = new ArrayList<Integer>();
            this.contentASCII = new ArrayList<Integer>();
            this.mCode = new ArrayList<Integer>();
            this.clef = new ArrayList<Integer>();
            this.mDecode = new ArrayList<Integer>();
            this.cartesDec = new ArrayList<Integer>();
    }

    /**
     * M�thode permettant l'initialisation des diff�rents param�tres dont on a besoin pour le codage
     * C'est � dire le paquet de cartes m�lang�, la source � coder ainsi que la cl� via l'appel de la fonction generateKey()
     */
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
        	//On sauvegarde le contenu du paquet tel qu'il est dans un fichier
            saveDeck(this.fname);
            //On g�n�re la cl� de codage
            this.generateKey(this.cartes);
        }else{
        	//On sauvegarde la configuration du paquet de cartes
            ArrayList<Integer> temp = new ArrayList(this.cartes); 
            //On g�n�re la cl� de codage qui effectue des op�rations sur le paquet
            this.generateKey(this.cartes);
            //On revient � la configuration originelle
            this.cartes = new ArrayList<>(temp);
        }
    }

    /**
     * ETAPE 1
     * Recul du joker noir d�une position : Vous faites reculer le joker noir d�une place
     * (vous le permutez avec la carte qui est juste derri�re lui). Si le joker noir est en derni�re position
     * il passe derri�re la carte du dessus (donc, en deuxi�me position).
     * @param deck
     */
    public void etape1(ArrayList<Integer> deck){
        int idx = deck.indexOf(JN);

        deck.remove(idx);
        if(idx == 53)
            deck.add(1, JN);
        else
            deck.add(idx+1,JN);
    }
    /**
     * ETAPE 2
     * Recul du joker rouge de deux positions : Vous faites reculer le joker rouge de deux cartes. S�il �tait en
     * derni�re position, il passe en troisi�me position; s�il �tait en avant derni�re position il passe en deuxi�me.
     * @param deck
     */
    public void etape2(ArrayList<Integer> deck){
        int idx = deck.indexOf(JR);

        deck.remove(idx);
        if(idx == 53){
            deck.add(2, JR);
        }else if(idx == 52)
            deck.add(1, JR);
        else
            deck.add(idx+2, JR);
    }
    
    /**
     * ETAPE 3
     * Double coupe par rapport aux jokers. Vouz rep�rez les deux jokers et vous intervertissez le paquet des
     * cartes situ�es au-dessus du joker qui est en premier avec le paquet de cartes qui est au-dessous du joker
     * qui est en second. Dans cette op�ration la couleur des jokers est sans importance.
     * @param deck
     */
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

    /**
     * ETAPE 4
     * Coupe simple d�termin�e par la derni�re carte : vous regardez la derni�re carte et vous �valuez son
     * num�ro selon l�ordre du Bridge : tr�fle-carreau-c�ur-pique et dans chaque couleur as, 2, 3, 4, 5, 6, 7, 8, 9, 10,
     * valet, dame et roi (l�as de tr�fle a ainsi le num�ro 1, le roi de pique a le num�ro 52). Les jokers on
     * par convention le num�ro 53. Si le num�ero de la derni�re carte est n vous prenez les n premi�res cartes
     * du dessus du paquet et les placez derri�re les autres cartes � l�exception de la derni�re carte qui reste la derni�re.
     * @param deck
     */
    public void etape4(ArrayList<Integer> deck){
        int val = deck.get(deck.size() - 1);

        List<Integer> split = deck.subList(0, val-1);
        ArrayList<Integer> splitCp = new ArrayList(split);
        split.clear();
        deck.addAll(deck.size()-1,splitCp);
        //System.out.println(cartes.toString());
    }

    /**
     * ETAPE 5
     * Lecture d�une lettre pseudo-al�atoire : Vous regardez la num�ro de la premi�re carte, soit n ce num�ro. Vous comptez
     * n cartes � partir du d�but et vous regardez la carte � laquelle vous �tes arriv�e (la n + 1-i�me), soit m son numero.
     * Si c�est un jokers vous refaites une op�eration compl�te de m�lange et de lecture (les points 1-2-3-4-5). Si m d�passe
     * 26 vous soustrayez 26. Au nombre entre 1 et 26 ainsi obtenu est associ�e une lettre qui est la lettre suivante dans du flux de clefs.
     * L�op�ration de lecture ne modifie pas l�ordre du paquet de cartes. Vous proc�dez de la m�me fa�on pour avoir les autres lettres du flux de clefs. 
     * Lorsque vous en avez un nombre suffisant vous pouvez coder votre message.
     * @param deck
     * @return valeur : int
     */
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
    
    /**
     * M�thode permettant la r�cup�ration d'une valeur de cl�. On appel simplement les 5 �tapes � la suite.
     * @param deck
     * @return valeur :  int
     */
    public int operationsFluxClef(ArrayList<Integer> deck){
        etape1(deck);
        etape2(deck);
        etape3(deck);
        etape4(deck);
       return etape5(deck);
    }
    
    /**
     * M�thode permettant de g�n�rer la totalit� de la cl� de codage en fonction de la taille de la source � cod�e.
     * @param deck
     */
    public void generateKey(ArrayList<Integer> deck){
    	for(int i = 0; i < this.contentASCII.size(); i++)
    		this.clef.add(this.operationsFluxClef(deck));
    }
    
    /**
     * M�thode permettant de passer de notre source � coder qui est sous forme de String � un ArrayList d'entiers repr�sentant les codes de chaque caract�res.
     * Dans notre cas nous codons 35 caract�res diff�rents [A-Z ,;:!?']. En th�orie on peut coder jusqu'� 52 caract�res.
     * @param array : ArrayList<Integer>
     * @param message : String
     */
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
    
    /**
     * M�thode permettant de convertir un ArrayList d'entiers � un String. Ici on applique la m�thode inverse pour
     * retrouver les caract�res correspondant aux codes.
     * @param array : ArrayList<Integer>
     * @return message : String
     */
    public String arrayToString(ArrayList<Integer> array){
    	//On utilise une Map pour convertir plus rapidement les codes en caract�res
    	Map<Integer,String> m = new HashMap<>();
    	m.put(0, "\r\n");
    	//On ajoute les lettres A � Z dans la Map
    	for(int i = 1; i < 27; i++)
    		m.put(i, ""+(char)(i+64));
    	
    	//Puis on ajoute les diff�rents caract�res sp�ciaux que nous prenons en compte.
    	m.put(27, " ");
    	m.put(28, "'");
    	m.put(29, ".");
    	m.put(30, ",");
    	m.put(31, "!");
    	m.put(32, "?");
    	m.put(33, ":");
    	m.put(34, ";");
    	m.put(35, "\n");
    	
    	//Progression de la barre de progression dans l'interface mise � 0
    	progress.set(0);
        int lg = array.size();
        //On utilise encore un StringBuilder -> plus rapide qu'un String pour les chaines de caract�res tr�s longues.
        StringBuilder message = new StringBuilder(); 
        
        for(int i = 0; i < lg; i++){
        	//Permet de mettre � jour la barre de progression dans l'interface.
        	progress.set(1.0*i / lg);
        	
        	//D�bugage on v�rifie que tous les caract�res ont bien �t� cod�s.
        	if(m.get(array.get(i)) == null) {
        		System.out.println("NULL : " +i+ "CHAR : " + array.get(i));
        	}
        	message.append(m.get(array.get(i)));
       }

        return message.toString();
    }
    
    /**
     * M�thode permettant de coder la source par rapport � la cl� de codage
     */
    public void codage(){
    	//On r�cup�re la taille de la source.
        int lg = this.contentASCII.size();
        int val;
        
        //On parcourt la source
        for(int i =0; i < lg; i++){
        	//On code le caract�re
		    val = ((this.contentASCII.get(i) + this.clef.get(i)) % NBCHARS) + 1;
		    this.mCode.add(val);
        }
        //On transforme notre Array contenant le code en String
        this.mCodeStr = this.arrayToString(this.mCode);
        //On clear la cl�
        this.clef.clear();
    }

    /**
     * M�thode permettant de d�coder le message cod� en fonction de la cl�.
     * A noter que l'on recalcul la cl� lors du d�codage puisque pour d�coder le fichier nous r�cup�rons le fichier contenant l'�tat initial du paquet
     * qui a servit � produire la cl� de codage.
     */
    public void decodage(){
        if(this.fname != null){
            //On r�cup�re le paquet de cartes depuis le fichier
            setDeckFromFile(this.fname, this.cartesDec);
            //On g�n�re la cl�
            generateKey(this.cartesDec);   
        }
        else
            generateKey(this.cartes);
        
        
        //On r�cup�re la taille du message cod�
        int lg = this.mCode.size();
        
        int val;
        //On parcourt celui-ci
        for(int i =0; i < lg; i++){
        	//On d�code le caract�re en fonction de la cl�
            val = (this.mCode.get(i) - this.clef.get(i)) % NBCHARS -1;
            //Si la valeur r�sultant est inf�rieur � 0 on lui ajoute le nombre de carac�res pris en compte.
            if(val < 0){
                val += NBCHARS;
            }
      
            this.mDecode.add(val);
        }
        //Enfin on transforme l'ArrayList en un String
        this.result = this.arrayToString(this.mDecode);
        
    }
    
    /**
     * M�thode permettant de sauvegarder le paquet de cartes dans un fichier texte 
     * @param filename
     */
    public void saveDeck(String filename){
        PrintWriter writer;
        try {
        	//Nom du fichier : "XXX_deck.txt"
            filename = filename.lastIndexOf(".") > 0 ? filename.substring(0,filename.lastIndexOf(".")).toLowerCase() + "_deck.txt" : "";
            writer = new PrintWriter(filename, "UTF-8");
            //On print le contenu de l'ArrayList dans le fichier texte
            writer.print(this.cartes.toString());
            writer.close();
        } catch (FileNotFoundException | UnsupportedEncodingException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        
    }
    
    /**
     * M�thode permettant de r�cup�rer le paquet de carte depuis un fichier texte
     * @param filename : String , Nom du fichier
     * @param array : ArrayList<Integer>, ArrayList dans lequel stocker le paquet de cartes.
     */
    public void setDeckFromFile(String filename, ArrayList<Integer> array){
        BufferedReader reader = null;
        try {
        	//On utilise un buffer pour lire le fichier, on lit ligne par ligne
            reader = Files.newBufferedReader(Paths.get(filename), StandardCharsets.UTF_8);
            String line;
            String data = "";
            while ((line = reader.readLine()) != null) {
                data += line +"\n";
            }
            //On r�cup�re seulement les nombres dans le fichier text (de base au format "[0,1,2,...,N]")
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
    
    public void setMCode(String code) {
    	
    }
    
    /**
     * M�thode permettant de charger le contenu d'un fichier cod� au pr�alable
     * @param f
     */
    public void setMCodeFromFile(File f) {
		BufferedReader reader;
		try {
			reader = Files.newBufferedReader(Paths.get(f.getPath()), StandardCharsets.UTF_8);
			StringBuilder content = new StringBuilder(); 
			char[] buffer = new char[256];
			int readChars;
			while ((readChars = reader.read(buffer, 0, 256)) > 0)
			    content.append(buffer, 0, readChars);
			
			this.stringToArray(this.mCode, content.toString());
	    	//on copie dans contentASCII juste pour recup�rer la taille pour la m�thode generateKey
	    	for(int i = 0; i < this.mCode.size(); i++)
	    		this.contentASCII.add(0);
		}catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
















































