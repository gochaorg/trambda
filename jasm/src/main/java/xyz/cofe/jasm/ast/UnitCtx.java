package xyz.cofe.jasm.ast;

public class UnitCtx {
    public final UnitAST unit;
    public UnitCtx(UnitAST unit){
        if( unit==null )throw new IllegalArgumentException( "unit==null" );
        this.unit = unit;
    }

    public String rawClassName( ClassAST cls ){
        if( cls==null )throw new IllegalArgumentException( "cls==null" );

        String name = cls.getName();
        if( unit.getPkg()!=null && unit.getPkg().getName()!=null && unit.getPkg().getName().length()>0 ){
            name = name + "." + unit.getPkg().getName();
        }

        return name.replace(".","/");
    }

    //public TypeNameAST
}
