package xyz.cofe.lang.basic.nodes;

import xyz.cofe.stsl.tast.TypeScope;
import xyz.cofe.stsl.types.Type;

import java.util.Arrays;
import java.util.EnumSet;
import java.util.LinkedHashMap;
import java.util.Map;

public class TContext {
    public TContext(){
    }

    public static enum CloneOption {
        All,
        TypeScope,
        Variables
    }

    public TContext(TContext sample, CloneOption ... options){
        if( sample==null )throw new IllegalArgumentException( "sample==null" );

        EnumSet<CloneOption> es = EnumSet.copyOf(Arrays.asList(options));

        if( sample.typeScope!=null && (es.contains(CloneOption.TypeScope) || es.contains(CloneOption.All)) ){
            typeScope = new TypeScope();
            typeScope.types_$eq(typeScope.types());
            typeScope.implicits_$eq(typeScope.implicits());
        }

        if( sample.variables!=null && (es.contains(CloneOption.Variables) || es.contains(CloneOption.All)) ){
            variables = new LinkedHashMap<>(sample.variables);
        }
    }

    @SuppressWarnings("MethodDoesntCallSuperMethod")
    public TContext clone(){
        return new TContext(this,CloneOption.All);
    }

    public TContext clone(CloneOption ... options){
        return new TContext(this,options);
    }

    protected TypeScope typeScope;
    public TypeScope getTypeScope(){
        if( typeScope!=null )return typeScope;
        typeScope = new TypeScope();
        typeScope.imports(Arrays.asList(BaseTypes.instance.types));
        return typeScope;
    }
    public void setTypeScope(TypeScope ts){
        this.typeScope = ts;
    }

    protected Map<String, Type> variables;
    public Map<String,Type> getVariables(){
        if( variables!=null )return variables;
        variables = new LinkedHashMap<>();
        return variables;
    }
    public void setVariables(Map<String,Type> vars){
        this.variables = vars;
    }
}
