package xyz.cofe.lang.basic.nodes;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.Test;
import xyz.cofe.lang.basic.nodes.BaseTypes;
import xyz.cofe.stsl.types.CallableFn;

public class TContextTest {
    @Test
    public void test01(){
        var tc = new TContext();
        var ccs = tc.getTypeScope().callCases(BaseTypes.instance.INT,"+",
            List.of(BaseTypes.instance.INT,BaseTypes.instance.INT)
        );
        var pccs = ccs.preferred();
        for( int i=0; i<pccs.size(); i++ ){
            var cc = pccs.apply(i);
            var cf = cc.fun() instanceof CallableFn ? (CallableFn)(cc.fun()) : null;
            var opImpl = cf!=null && cf.call() instanceof OperatorImpl ?
                ((OperatorImpl)cf.call()) : null;

            if( opImpl==null )continue;

            //System.out.println( "["+i+"] "+opImpl.name );
        }
    }

    @Test
    public void test02(){
        var tc = new TContext();
        System.out.println(tc.getTypeScope().apply("int"));
    }
}
