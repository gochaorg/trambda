package xyz.cofe.trambda.bc;

public class Label implements ByteCode {
    private static final long serialVersionUID = 1;

    public Label(){}
    public Label(String name){this.name = name;}

    private String name;
    public String getName(){ return name; }
    public void setName(String name){ this.name = name; }

    @Override
    public String toString(){
        return "Label "+name;
    }

    @Override
    public int hashCode(){
        var n = name;
        if( n!=null )return n.hashCode();
        return super.hashCode();
    }

    @Override
    public boolean equals(Object obj){
        if( obj==null )return false;
        if( obj.getClass()!=Label.class )return false;
        var lb = (Label)obj;
        var n0 = name;
        var n1 = lb.name;
        if( n0==null && n1==null )return true;
        if( n0!=null && n1==null )return false;
        //noinspection ConstantConditions
        if( n1!=null && n0==null )return false;
        return n0.equals(n1);
    }
}
