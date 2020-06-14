package asmcompiler;

public class Label {
    private String name;
    private Integer instruction;
    
    public Label(String n, Integer i)
    {
        name = n;
        instruction = i;
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
