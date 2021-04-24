package xyz.cofe.trambda.bc.fld;

/**
 * end of the method
 */
public class FieldEnd implements FieldByteCode {
    private static final long serialVersionUID = 1;

    protected int fieldVisitorId = 0;

    @Override
    public int getFieldVisitorId(){
        return fieldVisitorId;
    }

    @Override
    public void setFieldVisitorId(int id){
        fieldVisitorId = id;
    }

    public String toString(){
        return FieldEnd.class.getSimpleName();
    }
}
