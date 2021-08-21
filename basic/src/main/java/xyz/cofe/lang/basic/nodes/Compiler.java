package xyz.cofe.lang.basic.nodes;

import xyz.cofe.stsl.types.Type;
import xyz.cofe.trambda.bc.cls.CMethod;
import xyz.cofe.trambda.bc.mth.*;

import java.util.LinkedHashMap;
import java.util.Map;

public class Compiler {
    /**
     * Имя типа используемый в байт-коде (description)
     * @param type тип
     * @return имя (description)
     */
    protected String description( Type type ){
        if( type==null )throw new IllegalArgumentException( "type==null" );
        return null;
    }

    public String descriptor( FunAST fun ){
        if( fun==null )throw new IllegalArgumentException( "fun==null" );
        StringBuilder sb = new StringBuilder();
        sb.append("(");
        for( var arg : fun.getArgs().getChildren() ){
            var t = arg.getType();
            if( t==null )throw new Error("undefined type in "+fun);

            var tn = BaseTypes.instance.primitiveName.get(t);
            if( tn==null )throw new Error("primitive type undefined for "+t);

            sb.append(tn);
        }
        sb.append(")");

        var t = fun.getReturns().getType();
        if( t==null )throw new Error("undefined return type in "+fun);

        var tn = BaseTypes.instance.primitiveName.get(t);
        if( tn==null )throw new Error("primitive type undefined for "+t);

        sb.append(tn);
        return sb.toString();
    }

    protected long labelIdSeq = 0;
    public String nextLabel(String suff){
        labelIdSeq++;
        long id = labelIdSeq;
        return "L"+id+(suff!=null ? "_"+suff : "");
    }
    public String nextLabel(){ return nextLabel(null); }

    protected CMethod method;
    public CMethod method(){ return method; }

    protected String methodBeginLabel;
    protected String methodEndLabel;

    protected Map<String,Integer> varIndex;
    public Integer varIndex( String name ){
        if( name==null )return null;
        if( varIndex!=null )return varIndex.get(name);
        return null;
    }

    public CMethod compile( FunAST funAst ){
        if( funAst==null )throw new IllegalArgumentException( "funAst==null" );

        varIndex = new LinkedHashMap<>();
        for( var idx=0; idx<funAst.getArgs().getChildren().size(); idx++ ){
            var a = funAst.getArgs().getChildren().get(idx);
            varIndex.put(a.getName(),idx);
        }

        method = new CMethod();
        method.setName(funAst.getName());
        method.setStatic(true);
        method.setPublic(true);
        method.setDescriptor(descriptor(funAst));

        method.getMethodByteCodes().add(new MCode());
        methodBeginLabel = nextLabel("begin");
        method.getMethodByteCodes().add(new MLabel(methodBeginLabel));

        method.getMethodByteCodes().add(new MLineNumber(1,methodBeginLabel));

        for( var st : funAst.getStatements() ){
            compile(st);
        }

        // конец метода
        methodEndLabel = nextLabel("end");
        method.getMethodByteCodes().add(new MLabel(methodEndLabel));

        //region информация о переменных
        int idx = -1;
        for( var arg : funAst.getArgs().getChildren() ){
            idx++;
            String tn = BaseTypes.instance.primitiveName.get(arg.getType());
            var lvar = new MLocalVariable();
            lvar.setName(arg.getName());
            lvar.setDescriptor(tn);
            lvar.setIndex(idx);
            lvar.setLabelStart(methodBeginLabel);
            lvar.setLabelEnd(methodEndLabel);
            method.getMethodByteCodes().add(lvar);
        }
        //endregion

        //размер стека, вычисляется автоматом
        method.getMethodByteCodes().add(new MMaxs());

        method.getMethodByteCodes().add(new MEnd());
        return method;
    }

    protected void compile( StatementAST st ){
        if( st==null )throw new IllegalArgumentException( "st==null" );

        boolean astCompilerWrited = false;
        if( st instanceof ASTCompiler ){
            astCompilerWrited = ((ASTCompiler) st).compile( st,this);
        }
        if( astCompilerWrited )return;

        var ret = st.getReturn();
        if( ret != null ){
            compile(ret);
            return;
        }

        throw new Error("can't compile "+st);
    }
    protected void compile( ReturnAST ast ){
        if( ast==null )throw new IllegalArgumentException( "ast==null" );

        if( ast instanceof ASTCompiler ){
            if( ((ASTCompiler) ast).compile(ast, this) )return;
        }

        var retType = ast.getType();
        if( retType==null )throw new Error("return type undefined");

        var exp = ast.getExpr();
        if( exp==null )throw new Error("return expression not set");
        if( !(exp instanceof TAST) )throw new Error("return expression not instance of TAST");

        compile((TAST<?,?>) exp);

        if( retType==BaseTypes.instance.INT ){
            method.getMethodByteCodes().add(new MInsn(OpCode.IRETURN.code));
            return;
        }

        throw new UnsupportedOperationException("not implemented return type "+retType);
    }
    public void compile( AST<?,?> tast ){
        if( tast==null )throw new IllegalArgumentException( "tast==null" );

        if( tast instanceof ASTCompiler ){
            if( ((ASTCompiler) tast).compile(tast, this) )return;
        }

        if( tast instanceof BinOpAST ){
            compile( (BinOpAST)tast );
        }else if( tast instanceof UnaryOpAST ){
            compile( (UnaryOpAST)tast );
        }else if( tast instanceof AtomValueAST ){
            compile( ((AtomValueAST) tast).getExpr() );
        }else if( tast instanceof AtomAST ){
            compile( ((AtomAST) tast).getExpr() );
        }else if( tast instanceof LiteralAST ){
            compile( (LiteralAST)tast );
        }else if( tast instanceof ParenthesesAST ){
            compile(((ParenthesesAST) tast).getExpr());
        }else if( tast instanceof VarRefAST ){
            compile( (VarRefAST)tast );
        }else{
            throw new UnsupportedOperationException("not implemented compile "+tast.getClass());
        }
    }
    protected void compile( LiteralAST ast ){
        if( ast==null )throw new IllegalArgumentException( "ast==null" );

        if( ast instanceof ASTCompiler ){
            if( ((ASTCompiler) ast).compile(ast,this) )return;
        }

        throw new UnsupportedOperationException("not implemented compile "+ast.getClass());
    }
    protected void compile( VarRefAST ast ){
        if( ast==null )throw new IllegalArgumentException( "ast==null" );

        Integer idx = varIndex.get(ast.getName());
        if( idx==null )throw new Error("for variable "+ast.getName()+" index not found");

        var type = ast.getType();
        if( type==null )throw new Error("for variable "+ast.getName()+" type not defined");

        if( type==BaseTypes.instance.INT ){
            method.getMethodByteCodes().add(new MVarInsn(OpCode.ILOAD.code, idx));
            return;
        }

        if( type==BaseTypes.instance.STRING ){
            method.getMethodByteCodes().add(new MVarInsn(OpCode.ALOAD.code, idx));
            return;
        }

        throw new UnsupportedOperationException("not implemented compile "+ast.getClass()+" for "+type);
    }
    protected void compile( UnaryOpAST ast ){
        if( ast==null )throw new IllegalArgumentException( "ast==null" );

        var opImpl = ast.getOperatorImpl();
        if( opImpl instanceof ASTCompiler ){
            if( ((ASTCompiler) opImpl).compile(ast, this) )return;
        }

        throw new UnsupportedOperationException("not implemented compile "+ast.getClass());
    }
    protected void compile( BinOpAST ast ){
        if( ast==null )throw new IllegalArgumentException( "ast==null" );

        var opImpl = ast.getOperatorImpl();
        if( opImpl instanceof ASTCompiler ){
            if( ((ASTCompiler) opImpl).compile(ast, this) )return;
        }

        var type = ast.getType();
        if( type==null )throw new Error("for operator "+ast+" type not defined");

        var leftType = ast.getLeft() instanceof TAST ? ((TAST<?, ?>) ast.getLeft()).getType() : null;
        var rightType = ast.getRight() instanceof TAST ? ((TAST<?, ?>) ast.getRight()).getType() : null;

        if( leftType==null )throw new Error("left type undefined");
        if( rightType==null )throw new Error("right type undefined");

        if( leftType != BaseTypes.instance.INT )throw new Error("unsupported left type "+leftType);
        if( rightType != BaseTypes.instance.INT )throw new Error("unsupported right type "+rightType);

        compile(ast.getLeft());
        compile(ast.getRight());

        String op = ast.getOperator();
        if( op==null )throw new Error("operator not set");

        switch( op ){
            case "+":
                method.getMethodByteCodes().add(new MInsn(OpCode.IADD.code));
                break;
            case "-":
                method.getMethodByteCodes().add(new MInsn(OpCode.ISUB.code));
                break;
            case "*":
                method.getMethodByteCodes().add(new MInsn(OpCode.IMUL.code));
                break;
            case "/":
                method.getMethodByteCodes().add(new MInsn(OpCode.IDIV.code));
                break;
            default:
                throw new Error("operator "+op+" not implement");
        }
    }
}
