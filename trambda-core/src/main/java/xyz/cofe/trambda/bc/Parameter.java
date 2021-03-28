package xyz.cofe.trambda.bc;

public class Parameter implements ByteCode {
    private static final long serialVersionUID = 1;

    public Parameter(){}
    public Parameter(String name, int access){
        this.access = access;
        this.name = name;
    }

    //region access : int
    private int access;

    public int getAccess(){
        return access;
    }

    public void setAccess(int acc){
        this.access = acc;
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

    public String toString(){
        return "Parameter "+name+" access:"+access+" #"+new AccFlags(access).flags()
            ;
    }
}
