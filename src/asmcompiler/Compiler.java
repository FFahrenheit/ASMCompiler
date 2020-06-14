package asmcompiler;

import java.awt.TextArea;
import java.util.ArrayList;
import javax.swing.JOptionPane;


public class Compiler 
{
    private ArrayList<ASMLabel> labels = new ArrayList<>();
    private ArrayList<ASMCommand> commands = new ArrayList<>();
    private String[] lines; 
    private Integer instructionCounter = 0;
    private TextArea logger;
    
    public Compiler(String code, TextArea logger)
    {
        this.logger = logger;
        log("Compilando ...");
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
                        log("La etiqueta "+labelName+" ya existe, se repite en la linea "+(i+1));
                        compilable = false;
                    }
                }
                else 
                {
                    log("No se encontro nombre valido para la etiqueta en la linea "+(i+1));
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
                    log("Error en la linea " + (i+1) +" ("+command.getCommand()+"): "+command.getError());
                }
            }
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
                        log("El salto de la instruccion en la linea"+ (originPosition+1) + " a la "+(targetPosition+1)+" es muy grande");
                    }
                }
                else 
                {
                    compilable = false;
                    log("No se encontro la etiqueta "+ label+" para la instruccion en la linea "+(i+1));
                }
            }
        }
        if(compilable)
        {
            log("Compilacion completada... Abriendo hexadecimal");
            for (int i = 0; i < commands.size(); i++) 
            {
                System.out.print("["+i+"]");
                commands.get(i).showHex();
            }   
        }
        else
        {
            log("Compilacion fallida");
            JOptionPane.showMessageDialog(null,
            "El codigo no es compilable, observe el logger",
            "Error",
            JOptionPane.ERROR_MESSAGE); 
        }
    }
    
    private Integer getLabelPosition(String label)
    {
        for (int i = 0; i < labels.size(); i++) 
        {
            if(labels.get(i).getName().toUpperCase().equals(label.toUpperCase()))
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
            if(labels.get(i).getName().toUpperCase().equals(label.toUpperCase()))
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
            log("ADVERTENCIA: La etiqueta _: no es amigable");
        }
        return label;
    }
    
    private void log(String log)
    {
        this.logger.setText(this.logger.getText()+log+"\n");
    }
}
