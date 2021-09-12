package xyz.cofe.trambda.tcp;

import xyz.cofe.trambda.LambdaDump;

/**
 * Запрос компиляции лямбды
 */
public class Compile implements Message {
    private LambdaDump dump;

    /**
     * Возвращает дамп лямбды
     * @return лямбда
     */
    public LambdaDump getDump(){
        return dump;
    }

    /**
     * Указывает дамп лямбды
     * @param dump лямбда
     */
    public void setDump(LambdaDump dump){
        this.dump = dump;
    }
}
