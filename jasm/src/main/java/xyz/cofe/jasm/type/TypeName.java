package xyz.cofe.jasm.type;

import java.util.List;

public class TypeName implements ITypeName {
    public TypeName(String name){
        if( name==null )throw new IllegalArgumentException( "name==null" );
        this.name = name;
        this.primitive = true;
        this.dimension = 0;
        this.packaje = "";
        this.simpleName = "";
        this.params = List.of();
        this.description = "";
        this.signature = null;
    }

    public final int dimension;

    @Override
    public int getDimension(){
        return dimension;
    }

    public final boolean primitive;

    @Override
    public boolean isPrimitive(){
        return primitive;
    }

    public final String name;

    @Override
    public String getName(){
        return name;
    }

    public final String packaje;

    @Override
    public String getPackage(){
        return packaje;
    }

    public final String simpleName;

    @Override
    public String getSimpleName(){
        return simpleName;
    }

    public final List<TypeName> params;

    @Override
    public List<? extends ITypeName> getParams(){
        return params;
    }

    public final String description;

    @Override
    public String toDescription(){
        return description;
    }

    public final String signature;

    @Override
    public String toSignature(){
        return signature;
    }
}
