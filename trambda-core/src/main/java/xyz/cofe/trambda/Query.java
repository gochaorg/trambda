package xyz.cofe.trambda;

public interface Query<ENV> {
    public <RES> RES apply( Fn<ENV,RES> fn );
}
