package asmcompiler;

import java.util.ArrayList;


public class Compiler 
{
    private ArrayList<ASMLabel> labels = new ArrayList<>();
    private ArrayList<ASMCommand> commands = new ArrayList<>();
    private String[] lines; 
    private Integer instructionCounter = 0;
    private String warnings = "";
    private Integer byteCounter;
    
    public Compiler(String code)
    {
        lines = code.split("\n");
        for (int i = 0; i < lines.length; i++) 
        {
            if(lines[i].contains(":"))  //Si es comentario...
            {
                String labelName = isValidLabel(lines[i]);
                if(labelName!=null)
                {
                    if(!isExistingLabel(labelName))
                    {
                        labels.add(new ASMLabel(labelName,instructionCounter));
                    }
                }
            }
            if(lines[i].contains(" "))
            {
                ASMCommand command = new ASMCommand(lines[i]);
                if(command.isValid())
                {
                    commands.add(command);
                    instructionCounter++;
                }
                else
                {
                    System.out.println(command.getError());
                }
            }
        }
        for (int i = 0; i < labels.size(); i++) {
            System.out.println(labels.get(i).getName());
        }
        System.out.println("HEX SO FAR -------");
        for (int i = 0; i < commands.size(); i++) {
            commands.get(i).showHex();
        }
    }
    
    private Boolean isExistingLabel(String label)
    {
        for (int i = 0; i < labels.size(); i++) 
        {
            if(labels.get(i).getName().equals(label))
            {
                return true;
            }
        }
        return false;
    }
    
    private String isValidLabel(String line)
    {
        if(!line.endsWith(":"))
        {
            return null;
        }
        String label = line.substring(0,line.length()-1);
        if(label.contains(":") || label.contains(" "))
        {
            return null;
        }
        if(!Character.isAlphabetic(label.charAt(0)))
        {
            return null;
        }
        if(label.charAt(0)=='_')
        {
            warnings += "La etiqueta _: no es amigable\n";
        }
        return label;
    }
}
