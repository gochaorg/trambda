package xyz.cofe.trambda.bc;

public class JumpInsn implements ByteCode {
    private static final long serialVersionUID = 1;

    public JumpInsn(){}
    public JumpInsn(int op,String label){
        this.opcode = op;
        this.label = label;
    }

    //region opcode
    private int opcode;
    public int getOpcode(){
        return opcode;
    }
    public void setOpcode(int opcode){
        this.opcode = opcode;
    }
    //endregion
    //region label : String
    private String label;
    public String getLabel(){
        return label;
    }

    public void setLabel(String label){
        this.label = label;
    }
    //endregion

    public String toString(){
        return "JumpInsn "+
            OpCode.code(opcode).map(OpCode::name).orElse("?")+" #"+opcode+
            " "+label;
    }
}
