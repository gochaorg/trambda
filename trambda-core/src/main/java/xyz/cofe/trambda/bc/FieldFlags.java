package xyz.cofe.trambda.bc;

public interface FieldFlags extends AccFlagsProperty {
    //region deprecated : boolean
    default boolean isDeprecated(){
        return new AccFlags(getAccess()).isDeprecated();
    }
    default void setDeprecated(boolean v){
        setAccess( new AccFlags(getAccess()).withDeprecated(v).value() );
    }
    //endregion
    //region enum : boolean
    default boolean isEnum(){
        return new AccFlags(getAccess()).isEnum();
    }
    default void setEnum(boolean v){
        setAccess( new AccFlags(getAccess()).withEnum(v).value() );
    }
    //endregion
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
    //region private : boolean
    default boolean isPrivate(){
        return new AccFlags(getAccess()).isPrivate();
    }
    default void setPrivate(boolean v){
        setAccess( new AccFlags(getAccess()).withPrivate(v).value() );
    }
    //endregion
    //region protected : boolean
    default boolean isProtected(){
        return new AccFlags(getAccess()).isProtected();
    }
    default void setProtected(boolean v){
        setAccess( new AccFlags(getAccess()).withProtected(v).value() );
    }
    //endregion
    //region public : boolean
    default boolean isPublic(){
        return new AccFlags(getAccess()).isPublic();
    }
    default void setPublic(boolean v){
        setAccess( new AccFlags(getAccess()).withPublic(v).value() );
    }
    //endregion
    //region static : boolean
    default boolean isStatic(){
        return new AccFlags(getAccess()).isStatic();
    }
    default void setStatic(boolean v){
        setAccess( new AccFlags(getAccess()).withStatic(v).value() );
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
    //region transient : boolean
    default boolean isTransient(){
        return new AccFlags(getAccess()).isTransient();
    }
    default void setTransient(boolean v){
        setAccess( new AccFlags(getAccess()).withTransient(v).value() );
    }
    //endregion
    //region volatile : boolean
    default boolean isVolatile(){
        return new AccFlags(getAccess()).isVolatile();
    }
    default void setVolatile(boolean v){
        setAccess( new AccFlags(getAccess()).withVolatile(v).value() );
    }
    //endregion
}
