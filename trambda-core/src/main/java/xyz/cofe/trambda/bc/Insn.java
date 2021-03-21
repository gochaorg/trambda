package xyz.cofe.trambda.bc;

public class Insn implements ByteCode {
    private static final long serialVersionUID = 1;

    public Insn(){}
    public Insn(int op){this.opcode = op;}

    //region opcode
    private int opcode;

    public int getOpcode(){
        return opcode;
    }

    public void setOpcode(int opcode){
        this.opcode = opcode;
    }
    //endregion

    public String toString(){
        return "Insn "+OpCode.code(opcode).map(OpCode::name).orElse("?")+" #"+opcode;
    }
}
