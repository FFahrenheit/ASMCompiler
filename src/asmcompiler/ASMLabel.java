/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package asmcompiler;

/**
 *
 * @author ivan_
 */
public class ASMLabel {
    private String name;
    private Integer instruction;
    
    public ASMLabel(String name, Integer instruction) {
        this.name = name;
        this.instruction = instruction;
    }
        
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Integer getInstruction() {
        return instruction;
    }

    public void setInstruction(Integer instruction) {
        this.instruction = instruction;
    }
}
