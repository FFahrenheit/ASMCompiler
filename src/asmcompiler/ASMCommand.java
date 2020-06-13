/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package asmcompiler;

public class ASMCommand 
{ 
    private String[] hexByte;
    private Integer byteCount;
    private Boolean isValid = false;
    private String error="";    
    private String line;
    private Boolean isJump = false;
    private Integer offset = 0;
    
    public Boolean isJump()
    {
        return this.isJump;
    }
    
    public Integer getSize()
    {
        return this.byteCount;
    }
    
    public String getCommand()
    {
        return this.line;
    }
    
    public String getLabel()
    {
        return hexByte[1];
    }
    
    public void setJump(Integer jump)
    {
        if(jump<0)
        {
            jump = 256 + (jump);
        }
        hexByte[1] = getAbsValue(Integer.toHexString(jump).toUpperCase());
    }
    
    public String getError()
    {
        return error;
    }
    
    public Boolean isValid()
    {
        return this.isValid;
    }
    
    public ASMCommand(String command)
    {
        command = command.toUpperCase();
        this.line = command;
        String mnemonic = command.substring(0,command.indexOf(" "));
        System.out.println(command);
        switch(mnemonic)
        {
            case "MOV":
                isValid = verifyMOV(command);
                break;
            case "ADD":
                isValid = verifyADD(command);
                break;
            case "SUB":
                isValid = verifySUB(command);
                break;
            case "MUL":
                isValid = verifyMUL(command);
                break;
            case "DIV":
                isValid = verifyDIV(command);
            //case "MOD":
            case "INC":
                isValid = verifyOneArgumentRegister(command, "A4");
                break;
            case "DEC":
                isValid = verifyOneArgumentRegister(command, "A5");
                break;
            case "NOT":
                isValid = verifyOneArgumentRegister(command, "AD");
                break;
            case "ROL":
                isValid = verifyOneArgumentRegister(command, "9A");
                break;
            case "ROR":
                isValid = verifyOneArgumentRegister(command, "9B");
                break;
            case "SHL":
                isValid = verifyOneArgumentRegister(command, "9C");
                break;
            case "SHR":
                isValid = verifyOneArgumentRegister(command, "9D");
                break;
            case "PUSH":
                isValid = verifyOneArgumentRegister(command, "E0");
                break;
            case "POP":
                isValid = verifyOneArgumentRegister(command, "E1");
                break;
            case "AND":
                isValid = verifyAND(command);
                break;
            case "OR":
                isValid = verifyOR(command);
                break;
            case "XOR":                
                isValid = verifyXOR(command);
                break;
            case "CMP":
                isValid = verifyCMP(command);
                break;
            case "RET":
                isValid = verifyNoArgument(command,mnemonic,"CB");
                break;
            case "IRET":
                isValid = verifyNoArgument(command,mnemonic,"CD");
                break;
            case "PUSHF":
                isValid = verifyNoArgument(command,mnemonic,"EA");
                break;
            case "POPF":
                isValid = verifyNoArgument(command,mnemonic,"EB");
                break;
            case "CLO":
                isValid = verifyNoArgument(command,mnemonic,"FE");
                break;
            case "HALT":
                isValid = verifyNoArgument(command,mnemonic,"00");
                break;
            case "NOP":
                isValid = verifyNoArgument(command,mnemonic,"FF");
                break;
            case "STI":
                isValid = verifyNoArgument(command,mnemonic,"FC");
                break;
            case "CLI":
                isValid = verifyNoArgument(command,mnemonic,"FD");
                break;
            case "CALL": //Funciona con una direccion de RAM, pero bastante complejo
                isValid = verifyOneHexArgumentRegister(command, "CA");
                break;
            case "INT": //Igual que CALL
                isValid = verifyOneHexArgumentRegister(command, "CC");
                break;
            case "IN":
                isValid = verify8Argument(command, "F0");
                break;
            case "OUT":
                isValid = verify8Argument(command,"F1");
                break;
            case "DB":
                isValid = verifyDB(command); 
                break;
            case "ORG":
                isValid = verifyORG(command);
                break;
            case "JMP":
                isValid = prepareJump(command,"C0");
                break;
            case "JZ":
                isValid = prepareJump(command,"C1");
                break;
            case "JNZ":
                isValid = prepareJump(command,"C2");
                break;
            case "JS":
                isValid = prepareJump(command,"C3");
                break;
            case "JNS":
                isValid = prepareJump(command,"C4");
                break;
            case "JO":
                isValid = prepareJump(command,"C5");
                break;
            case "JNO":
                isValid = prepareJump(command,"C6");
                break;
            default:
                isValid = false;
        }
    }
    /***
     * Retorna si es el string es un registro AL - DL
     * @param value
     * @return 
     */
    private Boolean isRegister(String value)
    {
        value = value.trim();
        return value.equals("AL") || value.equals("BL") || value.equals("CL") || value.equals("DL");
    }
    
    /***
     * Convierte el registro a su HEX: AL = 00 ... 
     * @param reg
     * @return 
     */
    private String getRegisterValue(String reg)
    {
        reg = reg.trim();
        Integer value = (int) reg.charAt(0) - 65;
        return "0" + value.toString();
    }
    
    /***
     * Retorna el valor hexadeciaml de un String
     * @param value
     * @return 
     */
    private Integer convertHex(String value)
    {
        Integer v;
        try 
        {
            v = Integer.parseInt(value,16);
        }
        catch(NumberFormatException ex)
        {
            error = "El valor no es hexadecimal";
            return -1;
        } 
        return v;
    }
    
    /**
     * Obtiene el valor HEX formateado con 0 a la izquierda
     * @param value
     * @return 
     */
    private String getValue(String value)
    {
        value = value.trim();
        return (value.length()==1) ? "0" + value : value;
    }
    
    /***
     * Verifica que el valor se encuentre entre 0 y 255
     * @param value
     * @return 
     */
    private Boolean isHexValid(String value)
    {
        return convertHex(value)>=0 && convertHex(value)<=255;
    }
    
    /***
     * Comprueba que el parametro se trate de un puntero []
     * @param reg
     * @return 
     */
    private Boolean isPointer(String reg)
    {
        return reg.startsWith("[") && reg.endsWith("]");
    }
    /***
     * Obtiene el valor dentro del puntero [VALOR]
     * @param reg
     * @return 
     */
    private String getPointer(String reg)
    {
        return reg.substring(1,reg.length()-1);
    }
    
    private String getAbsValue(String reg)
    {
        return reg.contains("L")? getRegisterValue(reg) : getValue(reg);
    }
    
    private Boolean verifyTwoArguments(String command)
    {
        byteCount = 3;
        String arg = command.substring(command.indexOf(" ")+1).trim();
        if(!arg.contains(","))
        {
            error = "No hay suficientes argumentos separados por coma";
            return false;
        }
        String[] args = arg.split(",");
        hexByte = new String[3];
        if(args.length!=2)
        {
            error = "Demasiados argumentos";
            return false;
        }
        return true;
    }
    
    private String[] getArguments(String command)
    {
        String[] args = command.substring(command.indexOf(" ")+1).trim().split(",");
        args[0] = args[0].trim();
        args[1] = args[1].trim();
        return args;
    }
    
    private Boolean verifyMOV(String command)
    {
        if(!verifyTwoArguments(command))
        {
            return false;
        }
        
        String[] args = getArguments(command);
        
        if(isRegister(args[0]))
        {
            if(isHexValid(args[1]))
            {
                hexByte[0] = "D0";
                hexByte[1] = getAbsValue(args[0]);
                hexByte[2] = getAbsValue(args[1]);
                return true;
            }
            else if(isPointer(args[1]));
            {
                String value = getPointer(args[1]);            
                if(isHexValid(value))
                {
                    hexByte[0] = "D1";
                    hexByte[1] = getAbsValue(args[0]);
                    hexByte[2] = getAbsValue(value);
                    return true;
                }
                else if(isRegister(value))
                {
                    hexByte[0] = "D3";
                    hexByte[1] = getAbsValue(args[0]);
                    hexByte[3] = getAbsValue(value);
                    return true;
                }

            }
        }
        else if(isPointer(args[0]))
        {
            String value = getPointer(args[0]);
            if(isHexValid(value) && isRegister(args[1]))
            {
                hexByte[0] = "D2";
                hexByte[1] = getAbsValue(value);
                hexByte[2] = getAbsValue(args[1]);
                return true;
            }
            else if(isRegister(value) && isRegister(args[1]))
            {
                hexByte[0] = "D4";
                hexByte[1] = getAbsValue(value);
                hexByte[2] = getAbsValue(args[1]);
                return true;
            }
        }
        if(!error.equals(""))
        {
            error = "Los argumentos no coinciden";
        }
        return false;
    }
    
    private Boolean verifyDIV(String command)
    {
                if(!verifyTwoArguments(command))
        {
            return false;
        }
        String[] args = getArguments(command);
        if(isRegister(args[0]) && isRegister(args[1]))
        {
            hexByte[0]= "A3";
            hexByte[1] = getAbsValue(args[0]);
            hexByte[2] = getAbsValue(args[1]);
            return true;
        }
        else if(isRegister(args[0]) && isHexValid(args[1]))
        {
            hexByte[0] = "B6";
            hexByte[1] = getAbsValue(args[0]);
            hexByte[2] = getAbsValue(args[1]);            
        }
        if(!error.equals(""))
        {
            error = "Los argumentos no coinciden";
        }
        return false;
    }
    
    private Boolean verifyMUL(String command)
    {
                if(!verifyTwoArguments(command))
        {
            return false;
        }
        String[] args = getArguments(command);
        if(isRegister(args[0]) && isRegister(args[1]))
        {
            hexByte[0]= "A2";
            hexByte[1] = getAbsValue(args[0]);
            hexByte[2] = getAbsValue(args[1]);
            return true;
        }
        else if(isRegister(args[0]) && isHexValid(args[1]))
        {
            hexByte[0] = "B2";
            hexByte[1] = getAbsValue(args[0]);
            hexByte[2] = getAbsValue(args[1]);            
        }
        if(!error.equals(""))
        {
            error = "Los argumentos no coinciden";
        }
        return false;
    }

    private Boolean verifySUB(String command)
    {
        if(!verifyTwoArguments(command))
        {
            return false;
        }
        String[] args = getArguments(command);
        if(isRegister(args[0]) && isRegister(args[1]))
        {
            hexByte[0]= "A1";
            hexByte[1] = getAbsValue(args[0]);
            hexByte[2] = getAbsValue(args[1]);
            return true;
        }
        else if(isRegister(args[0]) && isHexValid(args[1]))
        {
            hexByte[0] = "B1";
            hexByte[1] = getAbsValue(args[0]);
            hexByte[2] = getAbsValue(args[1]);            
        }
        if(!error.equals(""))
        {
            error = "Los argumentos no coinciden";
        }
        return false;    
    }
    
    
    private Boolean verifyADD(String command)
    {
        if(!verifyTwoArguments(command))
        {
            return false;
        }
        String[] args = getArguments(command);
        if(isRegister(args[0]) && isRegister(args[1]))
        {
            hexByte[0]= "A0";
            hexByte[1] = getAbsValue(args[0]);
            hexByte[2] = getAbsValue(args[1]);
            return true;
        }
        else if(isRegister(args[0]) && isHexValid(args[1]))
        {
            hexByte[0] = "B0";
            hexByte[1] = getAbsValue(args[0]);
            hexByte[2] = getAbsValue(args[1]);            
        }
        if(!error.equals(""))
        {
            error = "Los argumentos no coinciden";
        }
        return false;
    }
    
    public void showHex()
    {
        System.out.print(line+": ");
        for (int i = 0; i < hexByte.length; i++) {
            System.out.print(hexByte[i]+" ");
        }
        System.out.println("");
    }
    
    private Boolean verify8Argument(String command, String Hex)
    {
        byteCount = 2;
        hexByte = new String[2];
        String arg = command.substring(command.indexOf(" ")+1).trim();
        if(isHexValid(arg))
        {
            if(convertHex(arg)>=0 && convertHex(arg)<=8)
            {
                hexByte[0] = Hex;
                hexByte[1] = getAbsValue(arg);
                return true;   
            }
            else
            {
                error = "Los argumentos estan fuera de rango";
            }
        }
        if(error.equals(""))
        {
            error = "El argumento no es valido";
        }
        return false;    }
    
    private Boolean verifyOneHexArgumentRegister(String command, String hex)
    {
        byteCount = 2;
        hexByte = new String[2];
        String arg = command.substring(command.indexOf(" ")+1).trim();
        if(isHexValid(arg))
        {
            hexByte[0] = hex;
            hexByte[1] = getAbsValue(arg);
            return true;
        }
        if(error.equals(""))
        {
            error = "El argumento no es valido";
        }
        return false;
    }
    
    private Boolean verifyOneArgumentRegister(String command, String hex)
    {
        byteCount = 2;
        hexByte = new String[2];
        String arg = command.substring(command.indexOf(" ")+1).trim();
        if(isRegister(arg))
        {
            hexByte[0] = hex;
            hexByte[1] = getRegisterValue(arg);
            return true;
        }
        if(error.equals(""))
        {
            error = "El argumento no es valido";
        }
        return false;
    }
    
    private Boolean verifyOR(String command)
    {
        if(!verifyTwoArguments(command))
        {
            return false;
        }
        String[] args = getArguments(command);
        if(isRegister(args[0]) && isRegister(args[1]))
        {
            hexByte[0]= "AB";
            hexByte[1] = getAbsValue(args[0]);
            hexByte[2] = getAbsValue(args[1]);
            return true;
        }
        else if(isRegister(args[0]) && isHexValid(args[1]))
        {
            hexByte[0] = "BB";
            hexByte[1] = getAbsValue(args[0]);
            hexByte[2] = getAbsValue(args[1]);            
        }
        if(!error.equals(""))
        {
            error = "Los argumentos no coinciden";
        }
        return false;
    }
    
    private Boolean verifyAND(String command)
    {
        if(!verifyTwoArguments(command))
        {
            return false;
        }
        String[] args = getArguments(command);
        if(isRegister(args[0]) && isRegister(args[1]))
        {
            hexByte[0]= "AA";
            hexByte[1] = getAbsValue(args[0]);
            hexByte[2] = getAbsValue(args[1]);
            return true;
        }
        else if(isRegister(args[0]) && isHexValid(args[1]))
        {
            hexByte[0] = "BA";
            hexByte[1] = getAbsValue(args[0]);
            hexByte[2] = getAbsValue(args[1]);            
        }
        if(!error.equals(""))
        {
            error = "Los argumentos no coinciden";
        }
        return false;
    }
    
    private Boolean verifyXOR(String command)
    {
        if(!verifyTwoArguments(command))
        {
            return false;
        }
        String[] args = getArguments(command);
        if(isRegister(args[0]) && isRegister(args[1]))
        {
            hexByte[0]= "AC";
            hexByte[1] = getAbsValue(args[0]);
            hexByte[2] = getAbsValue(args[1]);
            return true;
        }
        else if(isRegister(args[0]) && isHexValid(args[1]))
        {
            hexByte[0] = "BC";
            hexByte[1] = getAbsValue(args[0]);
            hexByte[2] = getAbsValue(args[1]);            
        }
        if(!error.equals(""))
        {
            error = "Los argumentos no coinciden";
        }
        return false;
    }
    
    private Boolean verifyCMP(String command)
    {
        if(!verifyTwoArguments(command))
        {
            return false;
        }
        String[] args = getArguments(command);
        if(isRegister(args[0]) && isRegister(args[1]))
        {
            hexByte[0] = "DA";
            hexByte[1] = getAbsValue(args[0]);
            hexByte[2] = getAbsValue(args[1]);
            return true;
        }
        else if(isRegister(args[0]) && isHexValid(args[1]))
        {
            hexByte[0] = "DB";
            hexByte[1] = getAbsValue(args[0]);
            hexByte[2] = getAbsValue(args[1]);
            return true;
        }
        else if(isRegister(args[0]) && isPointer(args[1]) && isHexValid(getPointer(args[1])))
        {
            hexByte[0] = "DC";
            hexByte[1] = getAbsValue(args[0]);
            hexByte[2] = getAbsValue(getPointer(args[1]));
            return true;
        }
        if(!error.equals(""))
        {
            error = "Los argumentos no coinciden";
        }
        return false;
   }
    
    private Boolean verifyNoArgument(String command, String mnemonic, String hex)
    {
        if(command.trim().equals(mnemonic))
        {
            byteCount = 1;
            hexByte = new String[1];
            hexByte[0] = hex;
            return true;
        }
        return false;
    }
    
    private Boolean verifyDB(String command)
    {
        String arg = command.substring(command.indexOf(" ")+1).trim();
        if(isHexValid(arg))
        {
            hexByte = new String[1];
            byteCount = 1;
            hexByte[0] = getAbsValue(arg);
            return true;
        }
        if(arg.startsWith("\"") && arg.endsWith("\""))
        {
            arg = arg.substring(1, arg.length()-1);
            byteCount = arg.length();
            if(byteCount <= 0)
            {
                error = "Instruccion ignorada";
                return false;
            }
            hexByte = new String[byteCount];  //Iterar
            for(int i=0; i<arg.length();i++)
            {
                int value = (int) arg.charAt(i);
                hexByte[i] = getAbsValue(Integer.toHexString(value));
            }
        }
        return false;
    }
    
    private Boolean verifyORG(String command)
    {
        byteCount = 0;
        String arg = command.substring(command.indexOf(" ")+1).trim();
        if(isHexValid(arg))
        {
            offset = convertHex(arg);
            return true;
        }
        return false;
    }
    
    public Integer getOffset()
    {
        return this.offset;
    }

    private Boolean prepareJump(String command, String hex) 
    {
        byteCount = 2;
        hexByte = new String[2];
        isJump = true;
        String arg = command.substring(command.indexOf(" ")+1).trim();
        if(isValidLabel(arg))
        {
            hexByte[0] = hex;
            hexByte[1] = arg; //Temporal...
            return true;
        }
        return false;
    }
    
    private Boolean isValidLabel(String label)
    {
        if(label.isEmpty())
        {
            return false;
        }
        if(label.contains(":") || label.contains(" "))
        {
            return false;
        }
        if(!Character.isAlphabetic(label.charAt(0)))
        {
            return false;
        }
        return true;
    }    
}