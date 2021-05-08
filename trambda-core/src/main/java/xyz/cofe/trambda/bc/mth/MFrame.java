package xyz.cofe.trambda.bc.mth;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.function.Function;
import org.objectweb.asm.Opcodes;
import xyz.cofe.trambda.bc.ByteCode;

public class MFrame extends MAbstractBC implements ByteCode {
    //region ElemType
    public static enum ElemType {
        Top(Opcodes.TOP),
        Integer(Opcodes.INTEGER),
        Float(Opcodes.FLOAT),
        Long(Opcodes.LONG),
        Double(Opcodes.DOUBLE),
        Null(Opcodes.NULL),
        UninitializedThis(Opcodes.UNINITIALIZED_THIS);
        public final int code;
        ElemType(int code){
            this.code = code;
        }
    }
    //endregion

    public MFrame(){}
    public MFrame(int type, int numLocal, List<ElemType> local, int numStack, List<ElemType> stack){
        this.type = type;
        this.numLocal = numLocal;
        this.local = local;
        this.numStack = numStack;
        this.stack = stack;
    }
    public MFrame(int type, int numLocal, Object[] local, int numStack, Object[] stack){
        this.type = type;
        this.numLocal = numLocal;
        Function<Object[],List<ElemType>> init = (arr) -> {
            if( arr==null )return null;
            var res = new ArrayList<ElemType>();
            for( var l : arr ){
                if( l==null ){
                    res.add(null);
                }else{
                    if( Objects.equals(l,Opcodes.TOP) ){
                        res.add(ElemType.Top);
                    }else if( Objects.equals(l,Opcodes.INTEGER) ){
                        res.add(ElemType.Integer);
                    }else if( Objects.equals(l,Opcodes.FLOAT) ){
                        res.add(ElemType.Float);
                    }else if( Objects.equals(l,Opcodes.LONG) ){
                        res.add(ElemType.Long);
                    }else if( Objects.equals(l,Opcodes.DOUBLE) ){
                        res.add(ElemType.Double);
                    }else if( Objects.equals(l,Opcodes.NULL) ){
                        res.add(ElemType.Null);
                    }else if( Objects.equals(l,Opcodes.UNINITIALIZED_THIS) ){
                        res.add(ElemType.UninitializedThis);
                    }else {
                        throw new IllegalArgumentException("undefined type in frame "+l);
                    }
                }
            }
            return res;
        };
        this.local = init.apply(local);
        this.numStack = numStack;
        this.stack = init.apply(stack);
    }

    //region type:int
    private int type;
    public int getType(){
        return type;
    }
    public void setType(int type){
        this.type = type;
    }
    //endregion
    //region numLocal:int
    private int numLocal;
    public int getNumLocal(){
        return numLocal;
    }
    public void setNumLocal(int numLocal){
        this.numLocal = numLocal;
    }
    //endregion
    //region local:List<ElemType>
    private List<ElemType> local;
    public List<ElemType> getLocal(){
        return local;
    }
    public void setLocal(List<ElemType> local){
        this.local = local;
    }
    //endregion
    //region numStack:int
    private int numStack;
    public int getNumStack(){
        return numStack;
    }
    public void setNumStack(int numStack){
        this.numStack = numStack;
    }
    //endregion
    //region stack:List<ElemType>
    private List<ElemType> stack;
    public List<ElemType> getStack(){
        return stack;
    }
    public void setStack(List<ElemType> stack){
        this.stack = stack;
    }
    //endregion

    public String toString(){
        return MFrame.class.getSimpleName()+
            " type="+type+
            " numLocal="+numLocal+
            " local="+(local==null ? "null" : local)+
            " numStack="+numStack+", stack="+(stack==null ? "null" : stack)+
            "";
    }
}
