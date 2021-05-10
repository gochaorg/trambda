package xyz.cofe.trambda.bc;

public interface ParameterFlags extends AccFlagsProperty {
    //region final : boolean
    default boolean isFinal(){
        return new AccFlags(getAccess()).isFinal();
    }
    default void setFinal(boolean v){
        setAccess( new AccFlags(getAccess()).withFinal(v).value() );
    }
    //endregion
    //region mandated : boolean
    default boolean isMandated(){
        return new AccFlags(getAccess()).isMandated();
    }
    default void setMandated(boolean v){
        setAccess( new AccFlags(getAccess()).withMandated(v).value() );
    }
    //endregion
    //region synthetic : boolean
    default boolean isSynthetic(){
        return new AccFlags(getAccess()).isSynthetic();
    }
    default void setSynthetic(boolean v){
        setAccess( new AccFlags(getAccess()).withSynthetic(v).value() );
    }
    //endregion
}
