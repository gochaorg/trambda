package xyz.cofe.trambda.sec;

import java.util.ArrayList;
import java.util.List;
import xyz.cofe.fn.Tuple2;
import xyz.cofe.trambda.LambdaDump;
import xyz.cofe.trambda.LambdaNode;

/**
 * Попытка доступа/инструкции которая проверяетмя
 * @param <INSTR> Инструкция (байт-код), например вызов метода
 * @param <SCOPE> Область определения этой инструкции, например лямбда или метод
 */
public abstract class SecurAccess<INSTR, SCOPE> {
    
    /**
     * Область определения этой инструкции, например лямбда или метод
     */
    protected final SCOPE scope;
    
    /**
     * Инструкция (байт-код), например вызов метода
     */
    protected final INSTR instruction;

    /**
     * Конструктор
     * @param instr Проверяеммая инструкция (байт-код), например вызов метода
     * @param scope Область определения этой инструкции, например лямбда или метод
     */
    public SecurAccess(INSTR instr, SCOPE scope){
        if( scope==null )throw new IllegalArgumentException( "scope==null" );
        if( instr==null )throw new IllegalArgumentException( "instr==null" );
        this.scope = scope;
        this.instruction = instr;
    }

    /**
     * Клонирование
     * @return клон
     */
    public abstract SecurAccess<INSTR, SCOPE> clone();

    /**
     * Область определения этой инструкции, например лямбда или метод
     * @return Область определения инструкции
     */
    public SCOPE getScope(){ return scope; }
    
    /**
     * Проверяеммая инструкция (байт-код), например вызов метода
     * @return Проверяеммая инструкция
     */
    public INSTR getInstruction(){ return instruction; }

    /**
     * Инспекция дампа лямбды на предмет опасный инструкций
     * @param dump дамп лямбды
     * @return список потенциально опасных инструкций
     */
    public static List<SecurAccess<?, Tuple2<LambdaDump, LambdaNode>>> inspect(LambdaDump dump){
        if( dump==null )throw new IllegalArgumentException( "dump==null" );
        ArrayList<SecurAccess<?, Tuple2<LambdaDump, LambdaNode>>> res = new ArrayList<>();
        res.addAll( FieldAccess.inspectField(dump) );
        res.addAll( Invoke.inspectCall(dump) );
        return res;
    }
}
