package xyz.cofe.trambda.bc.mth;

import org.objectweb.asm.MethodVisitor;
import xyz.cofe.trambda.bc.ByteCode;

public class MLabel extends MAbstractBC implements MethodWriter {
    private static final long serialVersionUID = 1;

    public MLabel(){}
    public MLabel(String name){this.name = name;}

    /**
     * Конструктор копирования
     * @param sample образец
     */
    public MLabel(MLabel sample){
        if( sample==null )throw new IllegalArgumentException( "sample==null" );
        name = sample.getName();
    }

    @SuppressWarnings("MethodDoesntCallSuperMethod") public MLabel clone(){ return new MLabel(this); }

    private String name;
    public String getName(){ return name; }
    public void setName(String name){ this.name = name; }

    @Override
    public String toString(){
        return MLabel.class.getSimpleName()+" name="+name;
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
        if( obj.getClass()!= MLabel.class )return false;
        var lb = (MLabel)obj;
        var n0 = name;
        var n1 = lb.name;
        if( n0==null && n1==null )return true;
        if( n0!=null && n1==null )return false;
        //noinspection ConstantConditions
        if( n1!=null && n0==null )return false;
        return n0.equals(n1);
    }

    @Override
    public void write(MethodVisitor v, MethodWriterCtx ctx){
        if( v==null )throw new IllegalArgumentException( "v==null" );
        if( ctx==null )throw new IllegalArgumentException( "ctx==null" );

        var ln = getName();
        if( ln==null )throw new IllegalStateException("name not defined");

        v.visitLabel(ctx.labelCreateOrGet(ln));
    }
}
