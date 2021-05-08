package xyz.cofe.trambda.bc.fld;

import org.objectweb.asm.FieldVisitor;

/**
 * end of the method
 */
public class FieldEnd implements FieldByteCode {
    private static final long serialVersionUID = 1;

    //region fieldVisitorId : int
    protected int fieldVisitorId = 0;
    @Override public int getFieldVisitorId(){
        return fieldVisitorId;
    }
    @Override public void setFieldVisitorId(int id){
        fieldVisitorId = id;
    }
    //endregion

    public String toString(){
        return FieldEnd.class.getSimpleName();
    }

    @Override
    public void write(FieldVisitor v){
        if( v==null )throw new IllegalArgumentException( "v==null" );
        v.visitEnd();
    }
}
