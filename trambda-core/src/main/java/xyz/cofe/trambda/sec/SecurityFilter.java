package xyz.cofe.trambda.sec;

import java.util.List;

public interface SecurityFilter<MESSAGE,SCOPE> {
    public List<SecurMessage<MESSAGE,SCOPE>> validate( List<SecurAccess<?,SCOPE>> secur );
}
