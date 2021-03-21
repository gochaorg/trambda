package xyz.cofe.trambda.bc;

import static org.objectweb.asm.Opcodes.*;

public class AccFlags {
    private final int flags;
    public AccFlags(int flags){
        this.flags = flags;
    }
    public int value(){ return flags; }
    private boolean has(int flag){
        return (flags & flag) == flag;
    }
    private static int set(int flags,int flag){
        return flags | flag;
    }
    private static int set(int flags,int flag, boolean v){
        return v ? (flags | flag) : (flags & ~flag);
    }
    private static int unset(int flags,int flag){
        return flags & ~flag;
    }
    public boolean isAbstract(){ return has(ACC_ABSTRACT); }
    public AccFlags withAbstract(boolean v){ return new AccFlags(set(flags,ACC_ABSTRACT,v)); }

    public boolean isAnnotation(){ return has(ACC_ANNOTATION); }
    public AccFlags withAnnotation(boolean v){ return new AccFlags(set(flags,ACC_ANNOTATION,v)); }

    public boolean isBridge(){ return has(ACC_BRIDGE); }
    public AccFlags withBridge(boolean v){ return new AccFlags(set(flags,ACC_BRIDGE,v)); }

    public boolean isDeprecated(){ return has(ACC_DEPRECATED); }
    public AccFlags withDeprecated(boolean v){ return new AccFlags(set(flags,ACC_DEPRECATED,v)); }

    public boolean isEnum(){ return has(ACC_ENUM); }
    public AccFlags withEnum(boolean v){ return new AccFlags(set(flags,ACC_ENUM,v)); }

    public boolean isFinal(){ return has(ACC_FINAL); }
    public AccFlags withFinal(boolean v){ return new AccFlags(set(flags,ACC_FINAL,v)); }

    public boolean isInterface(){ return has(ACC_INTERFACE); }
    public AccFlags withInterface(boolean v){ return new AccFlags(set(flags,ACC_INTERFACE,v)); }

    public boolean isMandated(){ return has(ACC_MANDATED); }
    public AccFlags withMandated(boolean v){ return new AccFlags(set(flags,ACC_MANDATED,v)); }

    public boolean isModule(){ return has(ACC_MODULE); }
    public AccFlags withModule(boolean v){ return new AccFlags(set(flags,ACC_MODULE,v)); }

    public boolean isNative(){ return has(ACC_NATIVE); }
    public AccFlags withNative(boolean v){ return new AccFlags(set(flags,ACC_NATIVE,v)); }

    public boolean isOpen(){ return has(ACC_OPEN); }
    public AccFlags withOpen(boolean v){ return new AccFlags(set(flags,ACC_OPEN,v)); }

    public boolean isPrivate(){ return has(ACC_PRIVATE); }
    public AccFlags withPrivate(boolean v){ return new AccFlags(set(flags,ACC_PRIVATE,v)); }

    public boolean isProtected(){ return has(ACC_PROTECTED); }
    public AccFlags withProtected(boolean v){ return new AccFlags(set(flags,ACC_PROTECTED,v)); }

    public boolean isPublic(){ return has(ACC_PUBLIC); }
    public AccFlags withPublic(boolean v){ return new AccFlags(set(flags,ACC_PUBLIC,v)); }

    public boolean isRecord(){ return has(ACC_RECORD); }
    public AccFlags withRecord(boolean v){ return new AccFlags(set(flags,ACC_RECORD,v)); }

    public boolean isStatic(){ return has(ACC_STATIC); }
    public AccFlags withStatic(boolean v){ return new AccFlags(set(flags,ACC_STATIC,v)); }

    public boolean isStaticPhase(){ return has(ACC_STATIC_PHASE); }
    public AccFlags withStaticPhase(boolean v){ return new AccFlags(set(flags,ACC_STATIC_PHASE,v)); }

    public boolean isStrict(){ return has(ACC_STRICT); }
    public AccFlags withStrict(boolean v){ return new AccFlags(set(flags,ACC_STRICT,v)); }

    public boolean isSuper(){ return has(ACC_SUPER); }
    public AccFlags withSuper(boolean v){ return new AccFlags(set(flags,ACC_SUPER,v)); }

    public boolean isSyncronized(){ return has(ACC_SYNCHRONIZED); }
    public AccFlags withSyncronized(boolean v){ return new AccFlags(set(flags,ACC_SYNCHRONIZED,v)); }

    public boolean isSynthetic(){ return has(ACC_SYNTHETIC); }
    public AccFlags withSynthetic(boolean v){ return new AccFlags(set(flags,ACC_SYNTHETIC,v)); }

    public boolean isTransient(){ return has(ACC_TRANSIENT); }
    public AccFlags withTransient(boolean v){ return new AccFlags(set(flags,ACC_TRANSIENT,v)); }

    public boolean isTransitive(){ return has(ACC_TRANSITIVE); }
    public AccFlags withTransitive(boolean v){ return new AccFlags(set(flags,ACC_TRANSITIVE,v)); }

    public boolean isVarArgs(){ return has(ACC_VARARGS); }
    public AccFlags withVarArgs(boolean v){ return new AccFlags(set(flags,ACC_VARARGS,v)); }

    public boolean isVolatile(){ return has(ACC_VOLATILE); }
    public AccFlags withVolatile(boolean v){ return new AccFlags(set(flags,ACC_VOLATILE,v)); }
}
