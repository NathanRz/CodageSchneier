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

public class CodageFichierTxt extends Codage{

	public CodageFichierTxt(File f) {

		super("");
		// TODO Auto-generated constructor stub
		this.fname = f.getName();
		
		BufferedReader reader;
		try {
			reader = Files.newBufferedReader(Paths.get(f.getPath()), StandardCharsets.UTF_8);
			StringBuilder content = new StringBuilder(); 
			char[] buffer = new char[256];
			int readChars;
			while ((readChars = reader.read(buffer, 0, 256)) > 0)
			    content.append(buffer, 0, readChars);
			super.setContent(content.toString().toUpperCase());
		}catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public void saveCodeToFile(){
		PrintWriter writer;
		String filename = this.fname.lastIndexOf(".") > 0 ? this.fname.substring(0,this.fname.lastIndexOf(".")).toLowerCase() + "_code.txt" : "";
		try {
			writer = new PrintWriter(filename, "UTF-8");
			writer.print(this.mCodeStr);
	        writer.close();
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	public void saveDecodeToFile(){
		PrintWriter writer;
		String filename = this.fname.lastIndexOf(".") > 0 ? this.fname.substring(0,this.fname.lastIndexOf(".")-5).toLowerCase() + "_decode.txt" : "";
		try {
			writer = new PrintWriter(filename, "UTF-8");
			writer.print(this.result);
	        writer.close();
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

}
