package xyz.cofe.trambda.clss;

import xyz.cofe.trambda.bc.cls.CBegin;
import xyz.cofe.trambda.bc.cls.CField;
import xyz.cofe.trambda.bc.cls.CMethod;

public class CDump {
    public static void dump(CBegin begin){
        if( begin==null )throw new IllegalArgumentException( "begin==null" );
        begin.walk().tree().forEach( ts -> {
            if( ts.getLevel()>0 ){
                var pref = ts.nodes().limit(ts.getLevel()).map( b -> {
                    if( b instanceof CMethod ){
                        return CMethod.class.getSimpleName()+"#"+((CMethod) b).getName()+"()";
                    }else if( b instanceof CField ){
                        return CField.class.getSimpleName()+"#"+((CField)b).getName();
                    }
                    return b.getClass().getSimpleName();
                }).reduce("", (a,b)->a+"/"+b);
                System.out.print(pref);
            }
            System.out.println("/"+ts.getNode());
        });
    }
}
