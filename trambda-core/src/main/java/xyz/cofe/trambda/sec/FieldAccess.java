package xyz.cofe.trambda.sec;

import java.util.ArrayList;
import java.util.List;
import xyz.cofe.fn.Tuple2;
import xyz.cofe.trambda.LambdaDump;
import xyz.cofe.trambda.LambdaNode;
import xyz.cofe.trambda.bc.mth.MFieldInsn;
import xyz.cofe.trambda.bc.mth.OpCode;

/**
 * Инструкция доступа к полю JVM класса
 */
public class FieldAccess extends SecurAccess<MFieldInsn, Tuple2<LambdaDump, LambdaNode>> {
    /**
     * Конструктор
     * @param fieldInsn инструкция доступа к полю JVM класса
     * @param dump место определения инструкции
     */
    public FieldAccess(MFieldInsn fieldInsn, Tuple2<LambdaDump, LambdaNode> dump){
        super(fieldInsn, dump);
    }

    /**
     * Конструктор копирования
     * @param sample образец для копирования
     */
    public FieldAccess(FieldAccess sample){
        super(sample.instruction, sample.scope);
    }

    /**
     * Клонирование
     * @return клон
     */
    @Override
    public FieldAccess clone(){
        return new FieldAccess(this);
    }

    /**
     * Инспекция дампа лямбды
     * @param dump дамп лямбды
     * @return Список инструкций доступа к полям
     */
    public static List<FieldAccess> inspectField( LambdaDump dump ){
        if( dump==null )throw new IllegalArgumentException( "dump==null" );
        List<FieldAccess> result = new ArrayList<>();
        inspectField(result,dump);
        return result;
    }
    private static void inspectField(List<FieldAccess> result, LambdaDump dump){
        if( dump==null )throw new IllegalArgumentException( "dump==null" );
        if( result==null )throw new IllegalArgumentException( "result==null" );

        var lnode = dump.getLambdaNode();
        if( lnode==null )throw new IllegalArgumentException("dump.getLambdaNode()==null");

        lnode.walk().tree().forEach( t -> {
            var node = t.getNode();
            var meth = node.getMethod();
            var byteCodes = meth!=null ? meth.getMethodByteCodes() : null;
            if( byteCodes!=null ){
                for( var bc : byteCodes ){
                    if( bc==null )continue;

                    if( bc instanceof MFieldInsn ){
                        result.add(new FieldAccess((MFieldInsn)bc, Tuple2.of(dump,node)) );
                    }
                }
            }
        });
    }

    /**
     * Возвращает имя класса/интерфейса JVM который содержит поле
     * @return имя класса, например {@code java.lang.String}
     */
    public String getOwner(){
        if( instruction.getOwner()!=null ){
            return instruction.getOwner().replace("/",".");
        }
        return "?";
    }
    
    /**
     * Возвращает имя поля
     * @return имя поля
     */
    public String getFieldName(){
        if( instruction.getName()!=null ){
            return instruction.getName();
        }
        return "?";
    }
    
    /**
     * Возвращает описание типа данных
     * @return тип данных
     */
    public TypeDesc getFieldType(){
        if( instruction.getDescriptor()!=null ){
            return TypeDesc.parse(instruction.getDescriptor());
        }
        return TypeDesc.undefined;
    }

    /**
     * Тип операции над полем
     */
    public static enum Operation {
        Undefined(-1),
        
        /**
         * Чтение статического поля
         */
        GetStatic(OpCode.GETSTATIC.code),
        
        /**
         * Запись значения в статическое поле
         */
        PutStatic(OpCode.PUTSTATIC.code),
        
        /**
         * Чтение поля
         */
        GetField(OpCode.GETFIELD.code),
        
        /**
         * Запись значения в поле
         */
        PutField(OpCode.PUTFIELD.code);
        public final int opcode;
        Operation(int opcode){
            this.opcode = opcode;
        }
    }
    
    /**
     * Возвращает тип операции над полем
     * @return тип операции
     */
    public Operation getOperation(){
        for( var o : Operation.values() ){
            if( o.opcode==instruction.getOpcode() ){
                return o;
            }
        }
        return Operation.Undefined;
    }

    /**
     * Возвращает тип операции: над статическим полем класса или нет
     * @return true - операция над статическим полем класса
     */
    public boolean isStatic(){
        return instruction.getOpcode()==OpCode.GETSTATIC.code || instruction.getOpcode()==OpCode.PUTSTATIC.code;
    }
    
    /**
     * Возвращает операции, над обычным полем или над статическим
     * @return true - операция над обычным полем класса / false - над статическим
     */
    public boolean isField(){
        return instruction.getOpcode()==OpCode.GETFIELD.code || instruction.getOpcode()==OpCode.PUTFIELD.code;
    }
    
    /**
     * Возвращает тип операции: операция чтения
     * @return true - операция чтения
     */
    public boolean isReadAccess(){
        return instruction.getOpcode()==OpCode.GETSTATIC.code || instruction.getOpcode()==OpCode.GETFIELD.code;
    }
    
    /**
     * Возвращает тип операции: операция записи
     * @return true - операция записи
     */
    public boolean isWriteAccess(){
        return instruction.getOpcode()==OpCode.PUTSTATIC.code || instruction.getOpcode()==OpCode.PUTFIELD.code;
    }

    public String toString(){
        StringBuilder sb = new StringBuilder();
        sb.append("field");
        if( isStatic() ){
            sb.append(" static");
        }else {
        }
        if( isReadAccess() ){
            sb.append(" read");
        }else if( isWriteAccess() ){
            sb.append(" write");
        }
        sb.append(" ").append(getFieldName()).append(" : ").append(getFieldType());
        sb.append(" of ").append(getOwner());
        return sb.toString();
    }
}
