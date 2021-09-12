package xyz.cofe.trambda.sec;

import xyz.cofe.fn.Tuple2;
import xyz.cofe.trambda.LambdaDump;
import xyz.cofe.trambda.LambdaNode;
import xyz.cofe.trambda.bc.mth.MMethodInsn;

/**
 * Инструкция вызова метода
 */
public class InvokeMethod extends Invoke<MMethodInsn> {
    /**
     * Конструктор
     * @param methodInsn байт-код инструкция вызова метода
     * @param scope область определения инструкции
     */
    public InvokeMethod(MMethodInsn methodInsn, Tuple2<LambdaDump, LambdaNode> scope){
        super(methodInsn, scope);
    }

    /**
     * Конструктор копирования
     * @param sample образец
     */
    public InvokeMethod(InvokeMethod sample){
        super(sample.instruction, sample.scope);
    }

    /**
     * Клонирование
     * @return клон
     */
    @Override
    public InvokeMethod clone(){
        return new InvokeMethod(this);
    }

    public String toString(){
        StringBuilder sb = new StringBuilder();
        sb
            .append("method")
            .append(" iface=")
            .append(instruction.isIface())
            .append(" owner=").append(getOwner())
            .append(" method.name=").append(instruction.getName())
            .append(" method.type=").append(MethodDescTypes.parse(instruction.getDescriptor()))
            ;
        return sb.toString();
    }

    /**
     * Возвращает имя вызываемого класса / интерфейса
     * @return имя класса
     */
    public String getOwner(){
        if( instruction.getOwner()!=null ){
            return instruction.getOwner().replace("/",".");
        }
        return "?";
    }

    /**
     * Возвращает имя вызываемого метода
     * @return имя метода
     */
    public String getMethodName(){
        return instruction.getName();
    }

    /**
     * Возвращает сигнатуру вызываемого метода
     * @return описание сигнатуры
     */
    public MethodDescTypes getMethodTypes(){
        return MethodDescTypes.parse(instruction.getDescriptor());
    }
}
