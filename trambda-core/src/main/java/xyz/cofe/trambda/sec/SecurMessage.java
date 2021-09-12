package xyz.cofe.trambda.sec;

import java.io.Serializable;

/**
 * Сообщение о безопасности байт-кода
 * @param <MESSAGE> Сообщение, текст например
 * @param <SCOPE> К какому коду это сообщение относится
 */
public class SecurMessage<MESSAGE,SCOPE> implements Serializable {
    private static final long serialVersionUID = 1;

    /**
     * Конструктор по умолчаню
     */
    public SecurMessage(){}

    /**
     * Конструктор 
     * @param access тип доступа
     * @param allow разрешен (true) или запрещен (false) доступ
     * @param message связанное сообщение
     */
    public SecurMessage(SecurAccess<?,SCOPE> access, boolean allow, MESSAGE message){
        this.access = access;
        this.allow = allow;
        this.message = message;
    }

    /**
     * Конструктор копирования
     * @param sample образец для копирования
     */
    @SuppressWarnings("unchecked")
    public SecurMessage(SecurMessage<MESSAGE,SCOPE> sample ){
        if( sample==null )throw new IllegalArgumentException( "sample==null" );
        this.allow = sample.allow;
        this.access = sample.access!=null ? sample.access.clone() : sample.access;
        this.message = sample.message!=null ?
            (sample.message instanceof CloneSupport ? ((CloneSupport<MESSAGE>)sample.message).clone() : sample.message )
            : sample.message;
    }

    //region message : MESSAGE
    private MESSAGE message;
    
    /**
     * Возвращает сообщение
     * @return сообщение
     */
    public MESSAGE getMessage(){
        return message;
    }

    /**
     * Указывает сообщение 
     * @param message сообщение
     */
    public void setMessage(MESSAGE message){
        this.message = message;
    }
    //endregion
    //region access : SecurAccess<?,SCOPE>
    private SecurAccess<?, SCOPE> access;
    
    /**
     * Возвращает тип доступа / инструкция байт-кода
     * @return инструкция байт-кода
     */
    public SecurAccess<?, SCOPE> getAccess(){
        return access;
    }
    
    /**
     * Указывает инструкцию байт-кода
     * @param access инструкцию байт-кода
     */
    public void setAccess(SecurAccess<?, SCOPE> access){
        this.access = access;
    }
    //endregion
    //region allow : boolean
    private boolean allow;
    
    /**
     * Возвращает разрешен (true) или запрещен (false) доступ/инструкция
     * @return true - инструкция разрешена
     */
    public boolean isAllow(){
        return allow;
    }
    
    /**
     * Указывает  разрешен (true) или запрещен (false) доступ/инструкция
     * @param allow true - инструкция разрешена / false - запрещена
     */
    public void setAllow(boolean allow){
        this.allow = allow;
    }
    //endregion

    public String toString(){
        return SecurMessage.class.getSimpleName() +
            " " + (isAllow() ? "ALLOW" : "DENY") +
            " \"" + getMessage() + "\"" +
            " where: " + getAccess();
    }
}
