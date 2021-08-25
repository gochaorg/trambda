package xyz.cofe.lang.basic.nodes;

import xyz.cofe.stsl.types.CallableFn;
import xyz.cofe.stsl.types.TObject;
import xyz.cofe.stsl.types.Type;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import xyz.cofe.fn.Tuple2;
import xyz.cofe.stsl.types.Field;

public class Toaster {
    protected TContext context = new TContext();
    protected void ok( AST<?,?> ast, String message ){
        System.out.println("ok "+message+" "+ast);
    }
    protected void error( AST<?,?> ast, String message ){
        System.out.println("error "+message+" "+ast);
    }

    protected Optional<Type> type(String typeName){
        if( typeName==null )throw new IllegalArgumentException( "typeName==null" );

        var ctx = context;
        if( ctx==null )return Optional.empty();

        var topt = ctx.getTypeScope().get(typeName);
        if( topt.isDefined() )return Optional.of(topt.get());

        return Optional.empty();
    }

    //region funTypes - разрешение типов заданных в сигнатуре функции - можно вызывать в любой момент
    protected void funTypes( AST<?,?> ast ){
        if( ast==null )throw new IllegalArgumentException( "ast==null" );
        for( var ts : ast.tree() ){
            var node = ts.getNode();
            if( node instanceof ArgAST ){
                funTypes((ArgAST) node);
            }
            if( node instanceof FnReturnAST ){
                funTypes((FnReturnAST) node);
            }
        }
    }
    protected void funTypes( ArgAST arg ){
        if( arg==null )throw new IllegalArgumentException( "arg==null" );
        if( arg.getType()!=null )return;
        var t = type(arg.getTypeName());
        if( t.isEmpty() ){
            error(arg,"type "+arg.getTypeName()+" not defined");
            return;
        }

        arg.setType(t.get());
        ok(arg,"resolved type");
    }
    protected void funTypes( FnReturnAST ast ){
        if( ast==null )throw new IllegalArgumentException( "ast==null" );
        if( ast.getType()!=null )return;
        var t = type(ast.getTypeName());
        if( t.isEmpty() ){
            error(ast,"type "+ast.getTypeName()+" not defined");
            return;
        }

        ast.setType(t.get());
        ok(ast,"resolved type");
    }
    //endregion

    //region literalTypes - разрешение типов для литералов - можно вызывать в любой момент
    protected void literalTypes( AST<?,?> ast ){
        if( ast==null )throw new IllegalArgumentException( "ast==null" );
        for( var ts : ast.tree() ){
            var node = ts.getNode();
            if( node instanceof LiteralAST ){
                resolve((LiteralAST) node);
            }
        }
    }
    protected Type resolve( LiteralAST ast ){
        if( ast==null )throw new IllegalArgumentException( "ast==null" );
        if( ast.getType()!=null )return ast.getType();
        if( ast.getAntlrRule().NUMBER()!=null ){
            ast.setType(BaseTypes.instance.INT);
            ok(ast,"type resolved");
            return ast.getType();
        }
        if( ast.getAntlrRule().STRING()!=null ){
            ast.setType(BaseTypes.instance.STRING);
            ok(ast,"type resolved");
            return ast.getType();
        }
        error(ast,"can't resolve type");
        return null;
    }
    //endregion

    //region checkArgDuplicates - проверка дублирования имен аргументов в функции - можно вызывать в любой момент
    protected void checkArgDuplicates( AST<?,?> ast ){
        if( ast==null )throw new IllegalArgumentException( "ast==null" );
        for( var ts : ast.tree() ){
            var node = ts.getNode();
            if( node instanceof ArgsAST ){
                checkArgDuplicates((ArgsAST) node);
            }
        }
    }
    protected void checkArgDuplicates( ArgsAST ast ){
        if( ast==null )throw new IllegalArgumentException( "ast==null" );
        Map<String,Integer> args = new LinkedHashMap<>();
        ast.getChildren().forEach( a -> {
            int cnt = args.getOrDefault(a.getName(), 0);
            cnt++;
            args.put(a.getName(),cnt);
            if( cnt>1 ){
                error(a,"duplcate argument name");
            }
        });
    }
    //endregion

    //region variableTypes - разрешение типов переменных, вызывать после checkArgDuplicates(), funTypes()
    protected void variableTypes( AST<?,?> ast ){
        if( ast==null )throw new IllegalArgumentException( "ast==null" );
        for( var ts : ast.tree() ){
            var node = ts.getNode();
            if( node instanceof VarRefAST ){
                Toaster.this.resolve((VarRefAST) node);
            }
        }
    }
    protected Optional<Tuple2<AST<?,?>,Type>> resolveVarDefAndType( AST<?,?> varRef, String varName ){
        var fun = varRef.find(FunAST.class);
        if( fun.isEmpty())return Optional.empty();
        
        var args = fun.get().getArgs();
        var arg = args.getChildren().stream().filter(a -> a.getName().equals(varName) ).findFirst();
        if( arg.isEmpty() || arg.get().getType()==null )return Optional.empty();
        
        return Optional.of(Tuple2.of(arg.get(), arg.get().getType()));
    }
    protected Type resolve( VarRefAST ast ){
        if( ast==null )throw new IllegalArgumentException( "ast==null" );
        if( ast.getType()!=null )return ast.getType();
        
        var varDefAndType = resolveVarDefAndType(ast, ast.getVarName());
        if( varDefAndType.isEmpty() ){
            error(ast,"can't resolve type");
            return null;
        }
        
        ast.setDefinition(varDefAndType.get().a());
        ast.setType(varDefAndType.get().b());
        ok(ast,"type resolved");

        return ast.getType();
    }
    //endregion

    //region atomTypes() - разрешение типов для атомарных конструкций, вызвать после variableTypes(), literalTypes
    protected void atomTypes( AST<?,?> ast ){
        if( ast==null )throw new IllegalArgumentException( "ast==null" );
        for( var ts : ast.tree() ){
            var node = ts.getNode();
            if( node instanceof AtomValueAST ){
                Toaster.this.resolve((AtomValueAST) node);
            }
        }
    }

    @SuppressWarnings("UnusedReturnValue")
    protected Type resolve( AtomValueAST ast ){
        if( ast==null )throw new IllegalArgumentException( "ast==null" );
        if( ast.getType()!=null )return ast.getType();

        var exp = ast.getExpr();
        if( exp instanceof TAST ){
            var t = Toaster.this.resolve((TAST<?,?>)exp);
            if( t!=null ){
                ast.setType(t);
                ok(ast,"resolved type");
                return t;
            }
        }

        error(ast,"can't resolve type");
        return null;
    }
    
    protected Type resolve( ObjAccessAST ast ){
        if( ast==null )throw new IllegalArgumentException( "ast==null" );
        if( ast.getType()!=null )return ast.getType();
        
        var varName = ast.getVarName();
        var varDefAndType = resolveVarDefAndType(ast, ast.getVarName());
        if( varDefAndType.isEmpty() ){
            error(ast,"unresolved variable type "+varName);
            return null;
        }
        
        var varDef = varDefAndType.get().a();
        var varType = varDefAndType.get().b();
        
        if( ast.getAccessType()==ObjAccessType.FieldRead ){
            return resolveFieldRead(ast,varType,ast.getMemberAccess().getMemberName());
        }
        
        error(ast,"can't resolve type for "+ast.getMemberAccess());
        return null;
    }
    
    protected Type resolveFieldRead(ObjAccessAST ast, Type obj, String fieldName ){
        if( !(obj instanceof TObject) ){
            error(ast, "'this' is not instance of TObject");
            return null;
        }
        
        var tobj = (TObject)obj;
        var fields = tobj.fields();
        Field field = null;
        for( int i=0; i<fields.size(); i++ ){
            var fld = fields.apply(i);
            if( fieldName!=null && fieldName.equals(fld.name()) ){
                field = fld;
                break;
            }
        }
        
        if( field==null ){
            error(ast, "field "+fieldName+" not found in "+tobj+"");
            return null;
        }
        
        if( !(field instanceof ASTCompiler) ){
            error(ast, "field "+fieldName+" not implement ASTCompiler in "+tobj+"");
            return null;
        }
        
        ASTCompiler astCompiler = (ASTCompiler)field;
        ast.setAstCompiler(astCompiler);
        ast.setType(field.tip());
        ok(ast,"resolved type");
        return ast.getType();
    }
    //endregion

    //region opTypes() - разрешение типов для операторов, вызывать после atomTypes()
    protected Type resolve( TAST<?,?> ast ){
        if( ast==null )throw new IllegalArgumentException( "ast==null" );
        if( ast instanceof AtomValueAST ){
            return Toaster.this.resolve((AtomValueAST) ast);
        }else if( ast instanceof LiteralAST ){
            return resolve( (LiteralAST) ast);
        }else if( ast instanceof VarRefAST ){
            return Toaster.this.resolve( (VarRefAST) ast);
        }else if( ast instanceof BinOpAST ){
            return Toaster.this.resolve( (BinOpAST) ast );
        }else if( ast instanceof UnaryOpAST ){
            return Toaster.this.resolve( (UnaryOpAST) ast );
        }else if( ast instanceof ParenthesesAST ){
            return Toaster.this.resolve( (ParenthesesAST)ast );
        }else if( ast instanceof ObjAccessAST ){
            return Toaster.this.resolve( (ObjAccessAST)ast );
        }

        return null;
    }
    protected Type resolve( ParenthesesAST ast ){
        if( ast==null )throw new IllegalArgumentException("ast==null");
        var exp = ast.getExpr();
        if( exp instanceof TAST ){
            var t = Toaster.this.resolve((TAST<?,?>)exp);
            if( t!=null ){
                ok(ast,"resolved type");
                ast.setType(t);
                return t;
            }
        }
        
        error(ast,"can't resolve type");
        return null;
    }    
    protected void opTypes( AST<?,?> ast ){
        if( ast==null )throw new IllegalArgumentException( "ast==null" );
        for( var ts : ast.tree() ){
            var node = ts.getNode();
            if( node instanceof BinOpAST ){
                Toaster.this.resolve((BinOpAST) node);
            }else if( node instanceof UnaryOpAST ){
                Toaster.this.resolve((UnaryOpAST) node);
            }
        }
    }
    protected Type resolve( UnaryOpAST ast ){
        if( ast==null )throw new IllegalArgumentException( "ast==null" );
        if( ast.getType()!=null )return ast.getType();
        var exp = ast.getExpr();
        if( !(exp instanceof TAST) ){
            error(ast,"expression not instance of TAST");
            return null;
        }

        Type expt = Toaster.this.resolve((TAST<?, ?>) exp);
        if( expt==null ){
            error(ast,"can't resolve expression type");
            return null;
        }

        String opName = ast.getOperator();
        if( opName==null || opName.length()<1 ){
            error(ast, "can't resolve unnamed unary operator");
            return null;
        }

        var ctx = context;
        if( ctx==null ){
            error(ast,"context not defined");
            return null;
        }

        if( !(expt instanceof TObject) ){
            error(ast,"expression is not object type");
            return null;
        }
        var tobj = (TObject)expt;

        var callCases = ctx.getTypeScope().callCases(tobj, opName, List.of(tobj));
        var prefCases = callCases.preferred();
        if( prefCases.isEmpty() ){
            error(ast,"for "+tobj+" not found operator "+opName+"("+tobj+")");
            return null;
        }else if( prefCases.size()>1 ){
            error(ast,"for "+tobj+" found multiple ambiguous operators "+opName+"("+tobj+")");
            return null;
        }

        var prefCase = prefCases.apply(0);
        var cf = prefCase.fun() instanceof CallableFn ? ((CallableFn)prefCase.fun()) : null;
        if( cf==null ){
            error(ast, "operator "+opName+" not CallableFn");
            return null;
        }

        var opImpl = cf.call() instanceof OperatorImpl ? ((OperatorImpl)cf.call()) : null;
        if( opImpl==null ){
            error(ast,"operator "+opName+" not has OperatorImpl");
            return null;
        }

        var retType = cf.returns();
        //noinspection UnnecessaryLocalVariable
        var thiz = tobj;
        if( retType==Type.THIS() ){
            retType = thiz;
        }

        ast.setType(retType);
        ast.setOperatorImpl(opImpl);

        ok(ast,"resolved type");
        return retType;
    }
    protected Type resolve( BinOpAST ast ){
        if( ast==null )throw new IllegalArgumentException( "ast==null" );

        if( ast.getType()!=null )return ast.getType();

        var left = ast.getLeft();
        if( !(left instanceof TAST) ){
            error(ast,"left expression not instance of TAST");
            return null;
        }

        var right = ast.getRight();
        if( !(right instanceof TAST) ){
            error(ast,"right expression not instance of TAST");
            return null;
        }

        Type leftt = Toaster.this.resolve((TAST<?, ?>) left);
        if( leftt==null ){
            error(ast,"can't resolve left expression type");
            return null;
        }

        if( !(leftt instanceof TObject) ){
            error(ast,"left expression is not object type");
            return null;
        }
        var tobj = (TObject)leftt;

        Type rightt = Toaster.this.resolve((TAST<?, ?>) right);

        if( rightt==null ){
            error(ast,"can't resolve right expression type");
            return null;
        }

        String opName = ast.getOperator();
        if( opName==null || opName.length()<1 ){
            error(ast, "can't resolve unnamed binary operator");
            return null;
        }

        var ctx = context;
        if( ctx==null ){
            error(ast,"context not defined");
            return null;
        }

        var callCases = ctx.getTypeScope().callCases(tobj, opName, List.of(tobj, rightt));
        var prefCases = callCases.preferred();
        if( prefCases.isEmpty() ){
            error(ast,"for "+tobj+" not found operator "+opName+"("+tobj+")");
            return null;
        }else if( prefCases.size()>1 ){
            error(ast,"for "+tobj+" found multiple ambiguous operators "+opName+"("+tobj+")");
            return null;
        }

        var prefCase = prefCases.apply(0);
        var cf = prefCase.fun() instanceof CallableFn ? ((CallableFn)prefCase.fun()) : null;
        if( cf==null ){
            error(ast, "operator "+opName+" not CallableFn");
            return null;
        }

        var opImpl = cf.call() instanceof OperatorImpl ? ((OperatorImpl)cf.call()) : null;
        if( opImpl==null ){
            error(ast,"operator "+opName+" not has OperatorImpl");
            return null;
        }

        var retType = cf.returns();
        //noinspection UnnecessaryLocalVariable
        var thiz = tobj;
        if( retType==Type.THIS() ){
            retType = thiz;
        }

        ast.setType(retType);
        ast.setOperatorImpl(opImpl);

        ok(ast,"resolved type");
        return retType;
    }
    protected Type resolve( LiteralObjAST ast ){
        if( ast==null )throw new IllegalArgumentException("ast==null");
        
        if( ast.getAccessType()==ObjAccessType.FieldRead ){
            //String
        }
        
        error(ast,"can't resolve type");
        return null;
    }
    //endregion

    //region returnTypes() - проверка возвращаемого типа
    protected void returnTypes( AST<?,?> ast ){
        if( ast==null )throw new IllegalArgumentException( "ast==null" );
        for( var ts : ast.tree() ){
            var node = ts.getNode();
            if( node instanceof ReturnAST ){
                Toaster.this.resolve((ReturnAST) node);
            }
        }

    }
    protected Type resolve( ReturnAST ast ){
        if( ast==null )throw new IllegalArgumentException( "ast==null" );
        if( ast.getType()!=null )return ast.getType();

        var fun = ast.find(FunAST.class);
        if( fun.isEmpty() ){
            error(ast,"statement not bound in function");
            return null;
        }

        var funReqType = fun.get().getReturns().getType();
        if( funReqType==null ){
            error(ast,"bound function undefined required return type");
            return null;
        }

        var exp = ast.getExpr();
        if( !(exp instanceof TAST) ){
            error(ast,"return expression not instance of TAST");
            return null;
        }

        var expt = Toaster.this.resolve((TAST<?, ?>) exp);
        if( expt==null ){
            error(ast,"return expression undefined type");
            return null;
        }

        if( !funReqType.assignable(expt) ){
            error(ast,"return required type "+funReqType+" but accept not assignable "+expt);
            return null;
        }

        ast.setType(funReqType);
        ok(ast,"resolved type");

        return funReqType;
    }
    //endregion

    public void resolve(AST<?,?> ast){
        if( ast==null )throw new IllegalArgumentException( "ast==null" );
        funTypes(ast);
        checkArgDuplicates(ast);
        literalTypes(ast);
        variableTypes(ast);
        atomTypes(ast);
        opTypes(ast);
        returnTypes(ast);
    }
}
