package xyz.cofe.lang.basic.byte_code_inspect;

import org.junit.jupiter.api.Test;
import xyz.cofe.lang.basic.nodes.ASTCompiler;
import xyz.cofe.lang.basic.nodes.BaseTypes;
import xyz.cofe.lang.basic.nodes.FieldImpl;

public class FieldTest {
    @Test
    public void test01(){
        var lst = BaseTypes.instance.STRING.fields().fields();
        for( int i=0; i<lst.size(); i++ ){
            var fld = lst.apply(i);
            System.out.println(""+fld+" "+(fld instanceof FieldImpl)+" "+(fld instanceof ASTCompiler));
        }
    }
}
