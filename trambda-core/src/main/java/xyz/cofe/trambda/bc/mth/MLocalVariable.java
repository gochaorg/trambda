package xyz.cofe.trambda.bc.mth;

import org.objectweb.asm.Label;
import org.objectweb.asm.MethodVisitor;
import xyz.cofe.trambda.bc.ByteCode;

public class MLocalVariable extends MAbstractBC implements MethodWriter {
    private static final long serialVersionUID = 1;

    public MLocalVariable(){}

    public MLocalVariable(String name, String descriptor, String signature, String labelStart, String labelEnd, int index){
        this.name = name;
        this.descriptor = descriptor;
        this.signature = signature;
        this.labelStart = labelStart;
        this.labelEnd = labelEnd;
        this.index = index;
    }

    public MLocalVariable(MLocalVariable sample){
        if( sample==null )throw new IllegalArgumentException( "sample==null" );
        name = sample.name;
        descriptor = sample.descriptor;
        signature = sample.signature;
        labelStart = sample.labelStart;
        labelEnd = sample.labelEnd;
        index = sample.index;
    }

    @SuppressWarnings("MethodDoesntCallSuperMethod") public MLocalVariable clone(){ return new MLocalVariable(this); }

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
    //region signature : String
    private String signature;
    public String getSignature(){
        return signature;
    }
    public void setSignature(String signature){
        this.signature = signature;
    }
    //endregion
    //region labelStart : String
    private String labelStart;
    public String getLabelStart(){
        return labelStart;
    }
    public void setLabelStart(String labelStart){
        this.labelStart = labelStart;
    }
    //endregion
    //region labelEnd : String
    private String labelEnd;
    public String getLabelEnd(){
        return labelEnd;
    }
    public void setLabelEnd(String labelEnd){
        this.labelEnd = labelEnd;
    }
    //endregion
    //region index : int
    private int index;
    public int getIndex(){
        return index;
    }
    public void setIndex(int index){
        this.index = index;
    }
    //endregion

    public String toString(){
        return MLocalVariable.class.getSimpleName()+
            " name="+name+
            " descriptor="+descriptor+
            " signature="+signature+
            " start="+labelStart+
            " end="+labelEnd+
            " index="+index;
    }

    @Override
    public void write(MethodVisitor v, MethodWriterCtx ctx){
        if( v==null )throw new IllegalArgumentException( "v==null" );
        if( ctx==null )throw new IllegalArgumentException( "ctx==null" );

        var ls = getLabelStart();
        var le = getLabelEnd();

        v.visitLocalVariable(
            getName(),
            getDescriptor(),
            getSignature(),
            ls!=null ? ctx.labelGet(ls) : null,
            le!=null ? ctx.labelGet(le) : null,
            getIndex()
        );
    }
}
