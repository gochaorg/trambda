package xyz.cofe.trambda.bc;

import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

import static org.objectweb.asm.Opcodes.*;

/**
 * Флаги методов и инструкций
 */
public class AccFlags {
    private final int flags;
    public AccFlags(int flags){
        this.flags = flags;
    }
    public int value(){ return flags; }

    private static Map<String,Integer> flagName = new TreeMap<>();

    private boolean has(int flag){
        return (flags & flag) == flag;
    }
    private static int set(int flags, int flag){
        return flags | flag;
    }
    private static int set(int flags, int flag, boolean switchOn){
        return switchOn ? (flags | flag) : (flags & ~flag);
    }
    private static int unset(int flags,int flag){
        return flags & ~flag;
    }

    public boolean isAbstract(){ return has(ACC_ABSTRACT); }
    public AccFlags withAbstract(boolean v){ return new AccFlags(set(flags,ACC_ABSTRACT,v)); }
    static { flagName.put("Abstract", ACC_ABSTRACT); }

    public boolean isAnnotation(){ return has(ACC_ANNOTATION); }
    public AccFlags withAnnotation(boolean v){ return new AccFlags(set(flags,ACC_ANNOTATION,v)); }
    static { flagName.put("Annotation", ACC_ANNOTATION); }

    public boolean isBridge(){ return has(ACC_BRIDGE); }
    public AccFlags withBridge(boolean v){ return new AccFlags(set(flags,ACC_BRIDGE,v)); }
    static { flagName.put("Bridge", ACC_BRIDGE); }

    public boolean isDeprecated(){ return has(ACC_DEPRECATED); }
    public AccFlags withDeprecated(boolean v){ return new AccFlags(set(flags,ACC_DEPRECATED,v)); }
    static { flagName.put("Deprecated", ACC_DEPRECATED); }

    public boolean isEnum(){ return has(ACC_ENUM); }
    public AccFlags withEnum(boolean v){ return new AccFlags(set(flags,ACC_ENUM,v)); }
    static { flagName.put("Enum", ACC_ENUM); }

    public boolean isFinal(){ return has(ACC_FINAL); }
    public AccFlags withFinal(boolean v){ return new AccFlags(set(flags,ACC_FINAL,v)); }
    static { flagName.put("Final", ACC_FINAL); }

    public boolean isInterface(){ return has(ACC_INTERFACE); }
    public AccFlags withInterface(boolean v){ return new AccFlags(set(flags,ACC_INTERFACE,v)); }
    static { flagName.put("Interface", ACC_INTERFACE); }

    public boolean isMandated(){ return has(ACC_MANDATED); }
    public AccFlags withMandated(boolean v){ return new AccFlags(set(flags,ACC_MANDATED,v)); }
    static { flagName.put("Mandated", ACC_MANDATED); }

    public boolean isModule(){ return has(ACC_MODULE); }
    public AccFlags withModule(boolean v){ return new AccFlags(set(flags,ACC_MODULE,v)); }
    static { flagName.put("Module", ACC_MODULE); }

    public boolean isNative(){ return has(ACC_NATIVE); }
    public AccFlags withNative(boolean v){ return new AccFlags(set(flags,ACC_NATIVE,v)); }
    static { flagName.put("Native", ACC_NATIVE); }

    public boolean isOpen(){ return has(ACC_OPEN); }
    public AccFlags withOpen(boolean v){ return new AccFlags(set(flags,ACC_OPEN,v)); }
    static { flagName.put("Open", ACC_OPEN); }

    public boolean isPrivate(){ return has(ACC_PRIVATE); }
    public AccFlags withPrivate(boolean v){ return new AccFlags(set(flags,ACC_PRIVATE,v)); }
    static { flagName.put("Private", ACC_PRIVATE); }

    public boolean isProtected(){ return has(ACC_PROTECTED); }
    public AccFlags withProtected(boolean v){ return new AccFlags(set(flags,ACC_PROTECTED,v)); }
    static { flagName.put("Protected", ACC_PROTECTED); }

    public boolean isPublic(){ return has(ACC_PUBLIC); }
    public AccFlags withPublic(boolean v){ return new AccFlags(set(flags,ACC_PUBLIC,v)); }
    static { flagName.put("Public", ACC_PUBLIC); }

    public boolean isRecord(){ return has(ACC_RECORD); }
    public AccFlags withRecord(boolean v){ return new AccFlags(set(flags,ACC_RECORD,v)); }
    static { flagName.put("Record", ACC_RECORD); }

    public boolean isStatic(){ return has(ACC_STATIC); }
    public AccFlags withStatic(boolean v){ return new AccFlags(set(flags,ACC_STATIC,v)); }
    static { flagName.put("Static", ACC_STATIC); }

    public boolean isStaticPhase(){ return has(ACC_STATIC_PHASE); }
    public AccFlags withStaticPhase(boolean v){ return new AccFlags(set(flags,ACC_STATIC_PHASE,v)); }
    static { flagName.put("StaticPhase", ACC_STATIC_PHASE); }

    public boolean isStrict(){ return has(ACC_STRICT); }
    public AccFlags withStrict(boolean v){ return new AccFlags(set(flags,ACC_STRICT,v)); }
    static { flagName.put("Strict", ACC_STRICT); }

    public boolean isSuper(){ return has(ACC_SUPER); }
    public AccFlags withSuper(boolean v){ return new AccFlags(set(flags,ACC_SUPER,v)); }
    static { flagName.put("Super", ACC_SUPER); }

    public boolean isSyncronized(){ return has(ACC_SYNCHRONIZED); }
    public AccFlags withSyncronized(boolean v){ return new AccFlags(set(flags,ACC_SYNCHRONIZED,v)); }
    static { flagName.put("Syncronized", ACC_SYNCHRONIZED); }

    public boolean isSynthetic(){ return has(ACC_SYNTHETIC); }
    public AccFlags withSynthetic(boolean v){ return new AccFlags(set(flags,ACC_SYNTHETIC,v)); }
    static { flagName.put("Synthetic", ACC_SYNTHETIC); }

    public boolean isTransient(){ return has(ACC_TRANSIENT); }
    public AccFlags withTransient(boolean v){ return new AccFlags(set(flags,ACC_TRANSIENT,v)); }
    static { flagName.put("Transient", ACC_TRANSIENT); }

    public boolean isTransitive(){ return has(ACC_TRANSITIVE); }
    public AccFlags withTransitive(boolean v){ return new AccFlags(set(flags,ACC_TRANSITIVE,v)); }
    static { flagName.put("Transitive", ACC_TRANSITIVE); }

    public boolean isVarArgs(){ return has(ACC_VARARGS); }
    public AccFlags withVarArgs(boolean v){ return new AccFlags(set(flags,ACC_VARARGS,v)); }
    static { flagName.put("VarArgs", ACC_VARARGS); }

    public boolean isVolatile(){ return has(ACC_VOLATILE); }
    public AccFlags withVolatile(boolean v){ return new AccFlags(set(flags,ACC_VOLATILE,v)); }
    static { flagName.put("Volatile", ACC_VOLATILE); }

    static { flagName = Collections.unmodifiableMap(flagName); }

    public Set<String> flags(){
        LinkedHashSet<String> flags = new LinkedHashSet<>();
        for( var e : flagName.entrySet() ){
            if( has(e.getValue()) )flags.add(e.getKey());
        }
        return flags;
    }
    public AccFlags flags(Set<String> flags){
        if( flags==null )throw new IllegalArgumentException( "flags==null" );
        Map<String,Boolean> state = new HashMap<>();
        for( var f : flagName.keySet() ){
            state.put(f, flags.contains(f));
        }
        AccFlags res = this;
        for( var e : flagName.entrySet() ){
            var mask = e.getValue();
            var set = state.get(e.getKey());
            res = new AccFlags(set(res.value(),mask,set));
        }
        return res;
    }
}
