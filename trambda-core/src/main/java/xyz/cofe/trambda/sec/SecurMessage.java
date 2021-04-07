package xyz.cofe.trambda.sec;

import java.io.Serializable;
import xyz.cofe.trambda.CloneSupport;

public class SecurMessage<MESSAGE,SCOPE> implements Serializable {
    private static final long serialVersionUID = 1;

    public SecurMessage(){}

    public SecurMessage(SecurAccess<?,SCOPE> access, boolean allow, MESSAGE message){
        this.access = access;
        this.allow = allow;
        this.message = message;
    }

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

    public MESSAGE getMessage(){
        return message;
    }

    public void setMessage(MESSAGE message){
        this.message = message;
    }
    //endregion
    //region access : SecurAccess<?,SCOPE>
    private SecurAccess<?, SCOPE> access;
    public SecurAccess<?, SCOPE> getAccess(){
        return access;
    }
    public void setAccess(SecurAccess<?, SCOPE> access){
        this.access = access;
    }
    //endregion
    //region allow : boolean
    private boolean allow;
    public boolean isAllow(){
        return allow;
    }
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
