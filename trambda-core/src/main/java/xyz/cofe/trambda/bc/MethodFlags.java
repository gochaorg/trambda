package xyz.cofe.trambda.bc;

@SuppressWarnings("unused")
public interface MethodFlags extends AccFlagsProperty {
    //region deprecated : boolean
    default boolean isDeprecated(){
        return new AccFlags(getAccess()).isDeprecated();
    }
    default void setDeprecated(boolean v){
        setAccess( new AccFlags(getAccess()).withDeprecated(v).value() );
    }
    //endregion
    //region abstract : boolean
    default boolean isAbstract(){
        return new AccFlags(getAccess()).isAbstract();
    }
    default void setAbstract(boolean v){
        setAccess( new AccFlags(getAccess()).withAbstract(v).value() );
    }
    //endregion
    //region bridge : boolean
    default boolean isBridge(){
        return new AccFlags(getAccess()).isBridge();
    }
    default void setBridge(boolean v){
        setAccess( new AccFlags(getAccess()).withBridge(v).value() );
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
    //region native : boolean
    default boolean isNative(){
        return new AccFlags(getAccess()).isNative();
    }
    default void setNative(boolean v){
        setAccess( new AccFlags(getAccess()).withNative(v).value() );
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
    //region strict : boolean
    default boolean isStrict(){
        return new AccFlags(getAccess()).isStrict();
    }
    default void setStrict(boolean v){
        setAccess( new AccFlags(getAccess()).withStrict(v).value() );
    }
    //endregion
    //region synchronized : boolean
    default boolean isSynchronized(){
        return new AccFlags(getAccess()).isSynchronized();
    }
    default void setSynchronized(boolean v){
        setAccess( new AccFlags(getAccess()).withSynchronized(v).value() );
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
    //region varArgs : boolean
    default boolean isVarArgs(){
        return new AccFlags(getAccess()).isVarArgs();
    }
    default void setVarArgs(boolean v){
        setAccess( new AccFlags(getAccess()).withVarArgs(v).value() );
    }
    //endregion
}
