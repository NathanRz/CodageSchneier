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
	private String filename;

	public CodageFichierTxt(File f) {

		super("");
		// TODO Auto-generated constructor stub
		this.filename = f.getName().lastIndexOf(".") > 0 ? f.getName().substring(0, f.getName().lastIndexOf(".")) : "";
		
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
		try {
			writer = new PrintWriter(filename+"_code.txt", "UTF-8");
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
		try {
			writer = new PrintWriter(filename+"_decode.txt", "UTF-8");
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
