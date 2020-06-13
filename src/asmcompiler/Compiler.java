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
        Boolean compilable = true;
        lines = code.split("\n");
        for (int i = 0; i < lines.length; i++)    //Lee las lineas, separa labels de instrucciones
        {
            if(lines[i].contains(":"))  //Si es label...
            {
                String labelName = isValidLabel(lines[i]);
                if(labelName!=null)
                {
                    if(!isExistingLabel(labelName))
                    {
                        labels.add(new ASMLabel(labelName,instructionCounter));
                    }
                    else
                    {
                        warnings += "La etiqueta "+labelName+" ya existe\n";
                        compilable = false;
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
                    compilable = false;
                    System.out.println("Error en la linea " + (i+1) +"("+command.getCommand()+"): "+command.getError());
                }
            }
        }
        System.out.println("---- LABELS EXISTENTES Y ANTES DE QUE INSTRUCCION ----");
        for (int i = 0; i < labels.size(); i++) {
            System.out.println(labels.get(i).getName()+ "Antes de la instruccion: "+labels.get(i).getInstruction());
        }
        System.out.println("------- HEX SO FAR -------");
        for (int i = 0; i < commands.size(); i++) {
            System.out.print("["+i+"]");
            commands.get(i).showHex();
        }
        for (int i = 0; i < commands.size(); i++) 
        {
            if(commands.get(i).isJump())
            {
                ASMCommand command = commands.get(i);
                String label = command.getLabel();
                Integer targetPosition = getLabelPosition(label);
                if(targetPosition >= 0)
                {
                    Integer originPosition = getPosition(i);
                    Integer jump = targetPosition - originPosition;
                    if(jump<=127 && jump>=-128)
                    {
                        command.setJump(jump);
                    }
                    else
                    {
                        compilable = false;
                        warnings += "El salto de la instruccion "+ (originPosition+1) + " a la "+(targetPosition+1)+" es muy grande";
                    }
                }
                else 
                {
                    compilable = false;
                    warnings += "No se encontro la etiqueta "+ label+"\n";
                }
            }
        }
        if(compilable)
        {
            for (int i = 0; i < commands.size(); i++) {
            System.out.print("["+i+"]");
            commands.get(i).showHex();
        }   
        }
        else
        {
            System.out.println(warnings);
        }
    }
    
    private Integer getLabelPosition(String label)
    {
        for (int i = 0; i < labels.size(); i++) 
        {
            if(labels.get(i).getName().equals(label))
            {
                return getPosition(labels.get(i).getInstruction());
            }
        }
        return -1;
    }
    
    private Integer getPosition(int position)
    {
        Integer count=0;
        for (int i = 0; i <position ; i++) 
        {
            count += commands.get(i).getSize() + commands.get(i).getOffset();
        }
        return count;
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
