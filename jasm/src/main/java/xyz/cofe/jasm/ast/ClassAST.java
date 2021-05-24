package xyz.cofe.jasm.ast;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import xyz.cofe.jasm.lex.KeyWord;
import xyz.cofe.text.tparse.TPointer;
import xyz.cofe.trambda.bc.AccFlags;
import xyz.cofe.trambda.bc.cls.CBegin;

public class ClassAST extends ASTBase<ClassAST> {
    protected ClassAST(ClassAST sample){
        super(sample);
        if( sample!=null ){
            name = sample.name;
            access = sample.access;
            if( sample.fields!=null ){
                fields = sample.fields.stream().map(
                    f -> f != null ? f.clone() : null
                ).collect(Collectors.toList());
            }
        }
    }

    @Override
    public ClassAST clone(){
        return new ClassAST(this);
    }

    public ClassAST(TPointer begin, TPointer end){
        super(begin, end);
    }

    //region begin(), end()
    public ClassAST begin( TPointer p ){
        if( p==null )throw new IllegalArgumentException( "p==null" );
        begin = p;
        return this;
    }
    public ClassAST end( TPointer p ){
        if( p==null )throw new IllegalArgumentException( "p==null" );
        end = p;
        return this;
    }
    //endregion

    //region name : String
    protected String name;

    public String getName(){
        return name;
    }

    public void setName(String name){
        this.name = name;
    }
    //endregion

    //region extendz : TypeNameAST
    protected TypeNameAST extendz;
    public TypeNameAST getExtendz(){
        return extendz;
    }

    public void setExtendz(TypeNameAST extendz){
        this.extendz = extendz;
    }
    //endregion
    //region interfaces : List<TypeNameAST>
    protected List<TypeNameAST> interfaces;
    public List<TypeNameAST> getInterfaces(){
        if( interfaces==null )interfaces = new ArrayList<>();
        return interfaces;
    }
    public void setInterfaces(List<TypeNameAST> itfs){
        interfaces = itfs;
    }
    //endregion

    //region access : int
    protected int access;

    public int getAccess(){
        return access;
    }

    public void setAccess(int access){
        this.access = access;
    }
    //endregion
    //region fields : List<FieldAST>
    protected List<FieldAST> fields;
    public List<FieldAST> getFields(){
        if( fields==null )fields = new ArrayList<>();
        return fields;
    }
    public void setFields(List<FieldAST> flds){
        fields = flds;
    }
    //endregion
    //region methods : List<MethodAST>
    protected List<MethodAST> methods;
    public List<MethodAST> getMethods(){
        if( methods==null )methods = new ArrayList<>();
        return methods;
    }
    public void setMethods(List<MethodAST> ls){
        methods = ls;
    }
    //endregion

    //region toString()
    public String toString(){
        StringBuilder sb = new StringBuilder();
        if( access!=0 ){
            for( var kw : KeyWord.values() ){
                if( kw.accFlag.isPresent() ){
                    int bit = kw.accFlag.get();
                    var isSet = (access & bit) == bit;
                    if( isSet ){
                        sb.append(kw.text);
                        sb.append(" ");
                    }
                }
            }
        }

        sb.append(KeyWord.clazz.text);
        sb.append(" ").append(name);

        StringBuilder body = new StringBuilder();

        if( fields!=null ){
            for( var fld : fields ){
                if( fld!=null ){
                    body.append(fld.toString()).append(System.lineSeparator());
                }
            }
        }

        if( methods!=null ){
            for( var m : methods ){
                if( m!=null ){
                    body.append(m).append(System.lineSeparator());
                }
            }
        }

        if( body.length()<1 ){
            sb.append(KeyWord.semicolon.text);
        }else {
            sb.append(" {").append(System.lineSeparator());
            sb.append(body);
            sb.append("}").append(System.lineSeparator());
        }
        return sb.toString();
    }
    //endregion

    public CBegin compile( UnitCtx ctx ){
        if( ctx==null )throw new IllegalArgumentException( "ctx==null" );

        CBegin begin = new CBegin();
        begin.setName( ctx.rawClassName(this) );
        begin.setAccess( getAccess() );

        return begin;
    }
}
