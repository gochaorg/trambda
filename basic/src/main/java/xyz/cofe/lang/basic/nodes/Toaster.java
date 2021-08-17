package xyz.cofe.lang.basic.nodes;

import xyz.cofe.stsl.types.CallableFn;
import xyz.cofe.stsl.types.TObject;
import xyz.cofe.stsl.types.Type;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

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
                literalType((LiteralAST) node);
            }
        }
    }
    protected Type literalType( LiteralAST ast ){
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
                variableType((VarRefAST) node);
            }
        }
    }
    protected Type variableType( VarRefAST ast ){
        if( ast==null )throw new IllegalArgumentException( "ast==null" );
        if( ast.getType()!=null )return ast.getType();

        var fun = ast.find(FunAST.class);
        if( fun.isPresent() ){
            var args = fun.get().getArgs();
            var arg = args.getChildren().stream().filter(a -> a.getName().equals(ast.getName()) ).findFirst();
            if( arg.isPresent() && arg.get().getType()!=null ){
                ast.setDefinition(arg.get());
                ast.setType(arg.get().getType());
                ok(ast,"type resolved");
                return ast.getType();
            }
        }

        error(ast,"can't resolve type");
        return null;
    }
    //endregion

    //region atomTypes() - разрешение типов для атомарных конструкций, вызвать после variableTypes(), literalTypes
    protected void atomTypes( AST<?,?> ast ){
        if( ast==null )throw new IllegalArgumentException( "ast==null" );
        for( var ts : ast.tree() ){
            var node = ts.getNode();
            if( node instanceof AtomValueAST ){
                atomType((AtomValueAST) node);
            }else if( node instanceof AtomAST ){
                atomType((AtomAST) node);
            }
        }
    }

    @SuppressWarnings("UnusedReturnValue")
    protected Type atomType( AtomValueAST ast ){
        if( ast==null )throw new IllegalArgumentException( "ast==null" );
        if( ast.getType()!=null )return ast.getType();

        var exp = ast.getExpr();
        if( exp instanceof AtomAST ){
            var aa = (AtomAST)exp;
            var t = atomType(aa);
            if( t!=null ){
                ast.setType(t);
                ok(ast,"resolved type");
                return t;
            }
        }

        error(ast,"can't resolve type");
        return null;
    }

    protected Type atomType( AtomAST ast ){
        if( ast==null )throw new IllegalArgumentException( "ast==null" );
        if( ast.getType()!=null )return ast.getType();

        var exp = ast.getExpr();
        if( exp instanceof LiteralAST ){
            var t = literalType((LiteralAST) exp);
            if( t!=null ){
                ast.setType(t);
                ok(ast,"resolved type");
                return t;
            }
        }else if( exp instanceof VarRefAST ){
            var t = variableType((VarRefAST) exp);
            if( t!=null ){
                ast.setType(t);
                ok(ast,"resolved type");
                return t;
            }
        }

        error(ast,"can't resolve type");
        return null;
    }
    //endregion

    //region opTypes() - разрешение типов для операторов, вызывать после atomTypes()
    protected Type expType( TAST<?,?> ast ){
        if( ast==null )throw new IllegalArgumentException( "ast==null" );
        if( ast instanceof AtomAST ){
            return atomType((AtomAST) ast);
        }else if( ast instanceof AtomValueAST ){
            return atomType((AtomValueAST) ast);
        }else if( ast instanceof LiteralAST ){
            return literalType( (LiteralAST) ast);
        }else if( ast instanceof VarRefAST ){
            return variableType( (VarRefAST) ast);
        }else if( ast instanceof BinOpAST ){
            return opType( (BinOpAST) ast );
        }else if( ast instanceof UnaryOpAST ){
            return opType( (UnaryOpAST) ast );
        }

        return null;
    }
    protected void opTypes( AST<?,?> ast ){
        if( ast==null )throw new IllegalArgumentException( "ast==null" );
        for( var ts : ast.tree() ){
            var node = ts.getNode();
            if( node instanceof BinOpAST ){
                opType((BinOpAST) node);
            }else if( node instanceof UnaryOpAST ){
                opType((UnaryOpAST) node);
            }
        }
    }
    protected Type opType( UnaryOpAST ast ){
        if( ast==null )throw new IllegalArgumentException( "ast==null" );
        if( ast.getType()!=null )return ast.getType();
        var exp = ast.getExpr();
        if( !(exp instanceof TAST) ){
            error(ast,"expression not instance of TAST");
            return null;
        }

        Type expt = expType((TAST<?, ?>) exp);
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
    protected Type opType( BinOpAST ast ){
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

        Type leftt = expType((TAST<?, ?>) left);
        if( leftt==null ){
            error(ast,"can't resolve left expression type");
            return null;
        }

        if( !(leftt instanceof TObject) ){
            error(ast,"left expression is not object type");
            return null;
        }
        var tobj = (TObject)leftt;

        Type rightt = expType((TAST<?, ?>) right);

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
    //endregion

    //region returnTypes() - проверка возвращаемого типа
    protected void returnTypes( AST<?,?> ast ){
        if( ast==null )throw new IllegalArgumentException( "ast==null" );
        for( var ts : ast.tree() ){
            var node = ts.getNode();
            if( node instanceof ReturnAST ){
                returnType((ReturnAST) node);
            }
        }

    }
    protected Type returnType( ReturnAST ast ){
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

        var expt = expType((TAST<?, ?>) exp);
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
