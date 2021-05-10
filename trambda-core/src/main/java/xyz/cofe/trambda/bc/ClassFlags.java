package xyz.cofe.trambda.bc;

public interface ClassFlags extends AccFlagsProperty {
    //region record : boolean
    default boolean isRecord(){
        return new AccFlags(getAccess()).isRecord();
    }

    default void setRecord(boolean v){
        setAccess( new AccFlags(getAccess()).withRecord(v).value() );
    }
    //endregion
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
    //region abstract : boolean
    default boolean isAbstract(){
        return new AccFlags(getAccess()).isAbstract();
    }
    default void setAbstract(boolean v){
        setAccess( new AccFlags(getAccess()).withAbstract(v).value() );
    }
    //endregion
    //region annotation : boolean
    default boolean isAnnotation(){
        return new AccFlags(getAccess()).isAnnotation();
    }
    default void setAnnotation(boolean v){
        setAccess( new AccFlags(getAccess()).withAnnotation(v).value() );
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
    //region interface : boolean
    default boolean isInterface(){
        return new AccFlags(getAccess()).isInterface();
    }
    default void setInterface(boolean v){
        setAccess( new AccFlags(getAccess()).withInterface(v).value() );
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
    //region module : boolean
    default boolean isModule(){
        return new AccFlags(getAccess()).isModule();
    }
    default void setModule(boolean v){
        setAccess( new AccFlags(getAccess()).withModule(v).value() );
    }
    //endregion
    //region open : boolean
    default boolean isOpen(){
        return new AccFlags(getAccess()).isOpen();
    }
    default void setOpen(boolean v){
        setAccess( new AccFlags(getAccess()).withOpen(v).value() );
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
    //region staticPhase : boolean
    default boolean isStaticPhase(){
        return new AccFlags(getAccess()).isStaticPhase();
    }
    default void setStaticPhase(boolean v){
        setAccess( new AccFlags(getAccess()).withStaticPhase(v).value() );
    }
    //endregion
    //region super : boolean
    default boolean isSuper(){
        return new AccFlags(getAccess()).isSuper();
    }
    default void setSuper(boolean v){
        setAccess( new AccFlags(getAccess()).withSuper(v).value() );
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
    //region transitive : boolean
    default boolean isTransitive(){
        return new AccFlags(getAccess()).isTransitive();
    }
    default void setTransitive(boolean v){
        setAccess( new AccFlags(getAccess()).withTransitive(v).value() );
    }
    //endregion
}
