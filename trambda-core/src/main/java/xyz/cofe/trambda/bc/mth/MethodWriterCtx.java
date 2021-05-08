package xyz.cofe.trambda.bc.mth;

import java.util.LinkedHashMap;
import java.util.Map;
import org.objectweb.asm.Label;
import xyz.cofe.trambda.bc.bm.MHandle;

public class MethodWriterCtx {
    private Map<String, Label> labels = new LinkedHashMap<>();
    public Label labelCreate(String name){
        if( name==null )throw new IllegalArgumentException( "name==null" );
        if( labels.containsKey(name) ){
            throw new IllegalArgumentException("label "+name+" already defined");
        }
        var lbl = new Label();
        labels.put(name,lbl);
        return lbl;
    }
    public Label labelGet(String name){
        if( name==null )throw new IllegalArgumentException( "name==null" );
        if( !labels.containsKey(name) )throw new IllegalArgumentException("label "+name+" not found");
        return labels.get(name);
    }
    public Label labelCreateOrGet(String name){
        if( name==null )throw new IllegalArgumentException( "name==null" );
        if( labels.containsKey(name) ){
            return labels.get(name);
        }
        return labelCreate(name);
    }
    public Label[] labelsGet(String ... names){
        if( names==null )return null;
        Label[] res = new Label[names.length];
        for( int i=0; i<names.length; i++ ){
            res[i] = names[i]!=null ? labelGet(names[i]) : null;
        }
        return res;
    }

    public org.objectweb.asm.Handle bootstrapArgument(MHandle targetCallArg, org.objectweb.asm.Handle bootstrap){
        if( targetCallArg==null )throw new IllegalArgumentException( "targetCallArg==null" );

        String name  = targetCallArg.getName();
        String owner = targetCallArg.getOwner();

        var h = new org.objectweb.asm.Handle(
            targetCallArg.getTag(),
            owner,
            name,
            targetCallArg.getDesc(),
            targetCallArg.isIface()
        );

        return h;
    }
}
