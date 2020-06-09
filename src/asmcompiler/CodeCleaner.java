package asmcompiler;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import javax.swing.JOptionPane;

public class CodeCleaner 
{
    private String cleanCode;
    private String originalCode;
    private Boolean finished = false;
    
    public CodeCleaner(File source)
    {
        String line;
        originalCode = "";
        cleanCode = "";
        
        try 
        {
            BufferedReader reader = new BufferedReader(new FileReader(source));
            while ((line = reader.readLine()) != null) {
                originalCode += line+"\n";
                line = line.trim();
                if(line.toLowerCase().equals("end"))
                {
                    finished = true;
                    continue;
                }
                if(!finished)
                {
                    String[] codes = line.split(";");
                    line = codes[0]; //Se queda con todo lo que haya antes del comentario
                    
                    if(line.equals(""))   //Comentario completo
                    {
                        continue;
                    }
                    
                    if(line.contains(":"))
                    {
                        codes = line.split(":");
                        if(codes.length == 1)
                        {
                            line = codes[0]+":";
                        }
                        else
                        {
                            cleanCode += codes[0].trim() + ":" + "\n";
                            line = codes[1];
                        }                   
                    }                    
                    cleanCode += line.trim() + "\n";
                }
            }
        }
        catch (FileNotFoundException ex) 
        {
            System.out.println("No se pudo leer");
        }
        catch (IOException ex) 
        {
            System.out.println("Error al leer");
        }
        cleanCode = cleanCode.replace("\t", " ");
    }
    
    public String getCleanCode() 
    {
        return cleanCode;
    }
    
    public Boolean getFinished()
    {
        return finished;
    }

    public String getOriginalCode() 
    {
        return originalCode;
    }
}
