package xyz.cofe.trambda.sec;

import java.util.List;

public interface SecurFilter<MESSAGE,SCOPE> {
    public List<SecurMessage<MESSAGE,SCOPE>> validate( List<SecurAccess<?,SCOPE>> secur );
}
