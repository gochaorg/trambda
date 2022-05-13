package xyz.cofe.trambda.bc;

import org.junit.jupiter.api.Test;
import xyz.cofe.trambda.bc.cls.CBegin;

public class ModifyTest {
    // будем модифицировать ее
    public static int some(int a, int b) {
        if( a>b )throw new IllegalArgumentException("a > b");
        int cnt = 0;
        for( int i=a; i<=b; i++ ){
            cnt += i;
        }
        return cnt;
    }
    // этот код будеть внедряться
    public static void sampleEcho(){
        checkPoint("sampleEcho");
    }
    // отметка о прохождении контрольной точки
    public static void checkPoint( String pointName ){
        System.out.println("CP "+pointName);
    }

    @Test
    public void copyAsIs(){
        var cbegin = CBegin.parseByteCode(ModifyTest.class);
        var bc = cbegin.toByteCode();
    }
}
