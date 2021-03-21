package xyz.cofe.trambda.bc;

public class FieldInsn implements ByteCode {
    private static final long serialVersionUID = 1;

    public FieldInsn(){
    }
    public FieldInsn(int opcode,String owner,String name,String descriptor){
        this.opcode = opcode;
        this.owner = owner;
        this.name = name;
        this.descriptor = descriptor;
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
    //region owner : String
    private String owner;
    public String getOwner(){
        return owner;
    }

    public void setOwner(String owner){
        this.owner = owner;
    }
    //endregion
    //region name : String
    private String name;
    public String getName(){
        return name;
    }

    public void setName(String name){
        this.name = name;
    }
    //endregion
    //region descriptor : String
    private String descriptor;

    public String getDescriptor(){
        return descriptor;
    }

    public void setDescriptor(String descriptor){
        this.descriptor = descriptor;
    }
    //endregion

    @Override
    public String toString(){
        return "FieldInsn{" +
            "opcode=" + OpCode.code(opcode).map(OpCode::name).orElse("?") + " #" + opcode +
            ", owner='" + owner + '\'' +
            ", name='" + name + '\'' +
            ", descriptor='" + descriptor + '\'' +
            '}';
    }
}
