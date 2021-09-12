package xyz.cofe.trambda.tcp;

import xyz.cofe.trambda.tcp.Message;

/**
 * Результат компиляции {@link Compile}
 */
public class CompileResult implements Message {
    //region key : Integer - идентификатор единицы компиляции
    private Integer key;
    /**
     * Возвращает идентификатор единицы компиляции
     * @return идентификатор единицы компиляции
     */
    public Integer getKey(){ return key; }
    /**
     * Указывает идентификатор единицы компиляции
     * @param key идентификатор единицы компиляции
     */
    public void setKey(Integer key){ this.key = key; }
    //endregion
    //region hash : String - хеш код
    private String hash;
    
    /**
     * Возвращает хеш код
     * @return хеш код
     */
    public String getHash(){
        return hash;
    }

    /**
     * Указывает хеш код
     * @param hash хеш код
     */
    public void setHash(String hash){
        this.hash = hash;
    }
    //endregion
}
