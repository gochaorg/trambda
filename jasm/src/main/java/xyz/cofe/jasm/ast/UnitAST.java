package xyz.cofe.jasm.ast;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;
import xyz.cofe.jasm.lex.KeyWord;
import xyz.cofe.text.tparse.TPointer;
import xyz.cofe.trambda.bc.cls.CBegin;

public class UnitAST extends ASTBase<UnitAST> {
    public UnitAST(TPointer begin, TPointer end){
        super(begin, end);
    }
    public UnitAST(UnitAST sample){
        super(sample);
        if( sample!=null ){
            if( sample.pkg!=null ){
                pkg = sample.pkg.clone();
            }
        }
    }

    public UnitAST configure(Consumer<UnitAST> conf){
        if( conf==null )throw new IllegalArgumentException( "conf==null" );
        conf.accept(this);
        return this;
    }

    //region pkg : PackageAST
    protected PackageAST pkg;
    public PackageAST getPkg(){ return pkg; }
    public void setPkg(PackageAST pkg){ this.pkg = pkg; }
    //endregion

    //region imports : List<ImportAST>
    protected List<ImportAST> imports;
    public List<ImportAST> getImports(){
        if( imports==null )imports = new ArrayList<>();
        return imports;
    }
    public void setImports(List<ImportAST> ls){
        imports = ls;
    }
    //endregion
    //region classes : List<ClassAST>
    protected List<ClassAST> classes;
    public List<ClassAST> getClasses(){
        if( classes==null )classes = new ArrayList<>();
        return classes;
    }
    public void setClasses(List<ClassAST> lst){
        classes = lst;
    }
    //endregion

    @Override
    public UnitAST clone(){
        return new UnitAST(this);
    }

    @Override
    public String toString(){
        StringBuilder sb = new StringBuilder();
        if( pkg!=null )sb.append(pkg).append(KeyWord.semicolon.text).append(System.lineSeparator());

        getImports().forEach( imp -> {
            sb.append(imp).append(System.lineSeparator());
        });

        getClasses().forEach( cls -> {
            sb.append(cls.toString());
        });
        return sb.toString();
    }

    public List<CBegin> compile(){
        List<CBegin> classes = new ArrayList<>();
        UnitCtx ctx = new UnitCtx(this);
        for( var cls : getClasses() ){
            if( cls!=null ){
                var c = cls.compile(ctx);
                if( c!=null ){
                    classes.add(c);
                }
            }
        }
        return classes;
    }
}
