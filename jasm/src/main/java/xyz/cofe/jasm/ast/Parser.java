package xyz.cofe.jasm.ast;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.function.BiFunction;
import java.util.function.Predicate;
import xyz.cofe.jasm.lex.IdToken;
import xyz.cofe.jasm.lex.KeyWord;
import xyz.cofe.jasm.lex.KeyWordToken;
import xyz.cofe.text.tparse.CToken;
import xyz.cofe.text.tparse.GR;
import xyz.cofe.text.tparse.TPointer;

public class Parser {
    //region atomic()
    public static <T extends CToken, A extends AST>
    GR<TPointer,A> atomic(Class<T> target, BiFunction<TPointer,T,A> map ){
        if( target==null )throw new IllegalArgumentException("target == null");
        return ptr -> {
            //noinspection ConstantConditions
            CToken t = ptr.lookup(0).orElseGet( null );
            if( t!=null && target.isAssignableFrom(t.getClass()) ){
                //noinspection unchecked
                return Optional.of( map.apply(ptr,(T)t) );
            }
            return Optional.empty();
        };
    }

    public static <T extends CToken, A extends AST>
    GR<TPointer,A> atomic(Class<T> target, Predicate<T> filter, BiFunction<TPointer, T, A> map ){
        if( target==null )throw new IllegalArgumentException("target == null");
        return ptr -> {
            CToken t = ptr.lookup(0).orElse( null );
            //noinspection unchecked
            if( t!=null && target.isAssignableFrom(t.getClass()) && (filter==null || filter.test((T)t)) ){
                //noinspection unchecked
                return Optional.of( map.apply(ptr,(T)t) );
            }
            return Optional.empty();
        };
    }
    //endregion

    //region binaryOp()
    public static GR<TPointer, BinaryOpAST> binaryOp(
        GR<TPointer,? extends AST> grLeft,
        GR<TPointer,? extends CTokenAST> operator,
        GR<TPointer,? extends AST> grRight
    ) {
        if( grLeft==null )throw new IllegalArgumentException( "grLeft==null" );
        if( operator==null )throw new IllegalArgumentException( "operator==null" );
        if( grRight==null )throw new IllegalArgumentException( "grRight==null" );

        return ptr -> {
            if( ptr==null || ptr.eof() )return Optional.empty();

            //TPointer beginPtr = ptr;

            Optional<? extends AST> left = grLeft.apply(ptr);
            //noinspection SimplifyOptionalCallChains
            if( !left.isPresent() )return Optional.empty();

            BinaryOpAST binOp = null;

            while ( true ) {
                Optional<? extends CTokenAST> op = operator.apply(
                    binOp==null ?
                        left.get().end() :
                        binOp.right().end()
                );
                if ( op.isEmpty() ) break;

                Optional<? extends AST> right = grRight.apply(op.get().end());
                if( right.isEmpty() )break;

                //noinspection ReplaceNullCheck
                if( binOp==null ){
                    binOp = new BinaryOpAST(left.get(), op.get(), right.get());
                }else{
                    binOp = new BinaryOpAST(binOp, op.get(), right.get());
                }
            }

            if( binOp==null )return Optional.empty();

            return Optional.of( binOp );
        };
    }
    //endregion

    //region semicolon ::= ';'
    public static final GR<TPointer, CTokenAST> semicolon = atomic(KeyWordToken.class, k -> k.keyWord==KeyWord.semicolon, CTokenAST::new);
    //endregion
    //region braceOpen ::= '{'
    public static final GR<TPointer, CTokenAST> braceOpen = atomic(KeyWordToken.class, k -> k.keyWord==KeyWord.braceOpen, CTokenAST::new);
    //endregion
    //region braceClose ::= '}'
    public static final GR<TPointer, CTokenAST> braceClose = atomic(KeyWordToken.class, k -> k.keyWord==KeyWord.braceClose, CTokenAST::new);
    //endregion
    //region parenthesisOpen ::= '('
    public static final GR<TPointer, CTokenAST> parenthesisOpen = atomic(KeyWordToken.class, k -> k.keyWord==KeyWord.parenthesisOpen, CTokenAST::new);
    //endregion
    //region parenthesisClose ::= ')'
    public static final GR<TPointer, CTokenAST> parenthesisClose = atomic(KeyWordToken.class, k -> k.keyWord==KeyWord.parenthesisClose, CTokenAST::new);
    //endregion
    //region less ::= '<'
    public static final GR<TPointer, CTokenAST> less = atomic(KeyWordToken.class, k -> k.keyWord==KeyWord.less, CTokenAST::new);
    //endregion
    //region more ::= '>'
    public static final GR<TPointer, CTokenAST> more = atomic(KeyWordToken.class, k -> k.keyWord==KeyWord.more, CTokenAST::new);
    //endregion
    //region comma ::= ','
    public static final GR<TPointer, CTokenAST> comma = atomic(KeyWordToken.class, k -> k.keyWord==KeyWord.comma, CTokenAST::new);
    //endregion
    //region colon ::= ':'
    public static final GR<TPointer, CTokenAST> colon = atomic(KeyWordToken.class, k -> k.keyWord==KeyWord.colon, CTokenAST::new);
    //endregion
    //region _throws ::= 'throws'
    public static final GR<TPointer, CTokenAST> _throws = atomic(KeyWordToken.class, k -> k.keyWord==KeyWord._throws, CTokenAST::new);
    //endregion
    //region classKW ::= 'class'
    public static final GR<TPointer, CTokenAST> classKW = atomic(KeyWordToken.class, k -> k.keyWord==KeyWord.clazz, CTokenAST::new);
    //endregion
    //region varKW ::= 'var'
    public static final GR<TPointer, CTokenAST> varKW = atomic(KeyWordToken.class, k -> k.keyWord==KeyWord._var, CTokenAST::new);
    //endregion
    //region _import ::= 'import'
    public static final GR<TPointer, CTokenAST> _import = atomic(KeyWordToken.class, k -> k.keyWord==KeyWord._import, CTokenAST::new);
    //endregion
    //region _extends ::= 'extends'
    public static final GR<TPointer, CTokenAST> _extends = atomic(KeyWordToken.class, k -> k.keyWord==KeyWord._extends, CTokenAST::new);
    //endregion
    //region _implements ::= 'implements'
    public static final GR<TPointer, CTokenAST> _implements = atomic(KeyWordToken.class, k -> k.keyWord==KeyWord._implements, CTokenAST::new);
    //endregion
    //region as ::= 'as'
    public static final GR<TPointer, CTokenAST> as = atomic(KeyWordToken.class, k -> k.keyWord==KeyWord.as, CTokenAST::new);
    //endregion
    //region id
    public static final GR<TPointer, IdTokenAST> identifier = atomic(IdToken.class, IdTokenAST::new);
    //endregion

    //region javaName ::= id { '.' id }
    public static final GR<TPointer, JavaNameAST> javaName = p -> {
        var idList = new ArrayList<String>();

        var i0 = p.lookup(0);
        if( i0.isEmpty() )return Optional.empty();
        if( i0.get() instanceof IdToken ){
            idList.add(((IdToken) i0.get()).text());
        }else if( (i0.get() instanceof KeyWordToken) && ((KeyWordToken)i0.get()).keyWord.primitive ){
            idList.add( ((KeyWordToken)i0.get()).keyWord.text );
        }else{
            return Optional.empty();
        }

        var begin = p;
        p = p.move(1);
        while( true ){
            var d0 = p.lookup(0);
            var i1 = p.lookup(1);
            if( d0.isEmpty() )break;
            if( !d0.get().text().equals(".") )break;

            if( i1.isEmpty() )break;

            if( i1.get() instanceof IdToken ){
                idList.add(((IdToken) i1.get()).text());
            }else if( (i1.get() instanceof KeyWordToken) && ((KeyWordToken)i1.get()).keyWord.primitive ){
                idList.add( ((KeyWordToken)i1.get()).keyWord.text );
            }else{
                break;
            }

            p = p.move(2);
        }

        var jn = new JavaNameAST(begin,p);
        if( idList.size()==1 ){
            jn.setName( idList.get(0) );
        }else if( idList.size()>1 ){
            jn.setName( idList.stream().reduce((a, b)->a+"."+b).get() );
        }

        return Optional.of(jn);
    };
    //endregion

    //region accFlag
    public static final GR<TPointer, KeyWordAST> accFlag =
        atomic(KeyWordToken.class, k -> k.keyWord.accFlag.isPresent(), (p,k) -> new KeyWordAST(p,k.keyWord));
    //endregion

    //region pkgName ::= 'package' javaName ';'
    public static final GR<TPointer, CTokenAST> pkgKW = atomic(KeyWordToken.class, k -> k.keyWord==KeyWord.pkg, CTokenAST::new);

    public static final GR<TPointer, PackageAST> pkgName =
        pkgKW.next(javaName).next(semicolon)
            .map( (k,jn,s) -> new PackageAST(k.begin(), s.end(), jn.getName()) );
    //endregion
    //region typeName ::= javaName [ '<' javaName { ',' javaName } '>' ]
    /**
     * typeName ::= javaName [ '&lt;' javaName { ',' javaName } '&gt;' ]
     */
    public static final ProxyGR<TypeNameAST> typeName = new ProxyGR<>(null);
    static {
        GR<TPointer,TypeNameAST> tn = ptr -> {
            var jn = javaName.apply(ptr);
            if( jn.isEmpty() )return Optional.empty();

            var b0 = less.apply(jn.get().end());
            if( b0.isEmpty() )return Optional.of(
                new TypeNameAST(jn.get().begin(), jn.get().end())
                    .configure( c -> {
                        c.setName(jn.get().getName());
                    })
            );

            var params = new ArrayList<TypeNameAST>();
            var p = b0.get().end();
            while( true ){
                var tn0 = typeName.apply(p);
                if( tn0.isEmpty() )return Optional.empty();
                params.add(tn0.get());

                var n = comma.apply(tn0.get().end());
                if( n.isPresent() ){
                    p = n.get().end();
                }else {
                    var b1 = more.apply(tn0.get().end());
                    if( b1.isEmpty() ){
                        throw new Error("expect , > at "+tn0.get().end());
                    }
                    p = b1.get().end();
                    break;
                }
            }

            TypeNameAST tn1 = new TypeNameAST(jn.get().begin(), p);
            tn1.setName(jn.get().getName());
            tn1.getParams().addAll(params);

            return Optional.of(tn1);
        };
        typeName.target(tn);
    }
    //endregion

    //region importz ::= 'import' typeName [ 'as' typeName ] ';'
    public static final GR<TPointer, ImportAST> importz = p -> {
        var begin = p;

        var imp1 = _import.apply(p);
        if( imp1.isEmpty() )return Optional.empty();
        p = imp1.get().end();

        var tname = typeName.apply(p);
        if( tname.isEmpty() ){
            throw new Error("expect typename at "+p);
        }
        p = tname.get().end();

        var imp = new ImportAST(begin,p);
        imp.setType(tname.get());

        var as1 = as.apply(p);
        if( as1.isPresent() ){
            p = as1.get().end();

            var alias = typeName.apply(p);
            if( alias.isEmpty() ){
                throw new Error("expect typename at "+p);
            }
            p = alias.get().end();

            imp.setAlias(alias.get());
        }

        var sem = semicolon.apply(p);
        if( sem.isEmpty() ){
            throw new Error("expect ; at "+p);
        }
        p = sem.get().end();

        imp.end(p);

        return Optional.of(imp);
    };
    //endregion

    //region nameTypePair ::= id ':' typeName
    public static final GR<TPointer, NameTypePairAST> nameTypePair =
        identifier.next(colon).next(typeName)
        .map( (id,colon,type) -> new NameTypePairAST(id.begin(), type.end()).configure( c -> {
            c.setName(id.token().text());
            c.setType(type);
        }) );
    //endregion

    //region methodSign ::= id '(' [ nameTypePair { ',' nameTypePair } ] ')' ':' typeName [ 'throws' typeName { ',' typeName } ]
    /**
     * id '(' [ nameTypePair { ',' nameTypePair } ] ')' ':' typeName [ 'throws' typeName { ',' typeName } ]
     */
    public static final GR<TPointer, MethodAST> methodSign = p -> {
        var begin = p;

        var name = identifier.apply(p);
        if( name.isEmpty() )return Optional.empty();
        p = name.get().end;

        var pOpen = parenthesisOpen.apply(p);
        if( pOpen.isEmpty() )return Optional.empty();
        p = pOpen.get().end();

        var args = new ArrayList<NameTypePairAST>();
        while( true ){
            var pClose = parenthesisClose.apply(p);
            if( pClose.isPresent() ){
                p = pClose.get().end();
                break;
            }

            var arg = nameTypePair.apply(p);
            if( arg.isPresent() ){
                p = arg.get().end();
                args.add(arg.get());

                var comm = comma.apply(p);
                if( comm.isPresent() ){
                    p = comm.get().end();
                    continue;
                }

                var pClose1 = parenthesisClose.apply(p);
                if( pClose1.isPresent() ){
                    p = pClose1.get().end();
                    break;
                }
            }else {
                throw new Error("expect argument at "+p);
            }
        }

        var colon_type = colon.apply(p);
        if( colon_type.isEmpty() ){
            throw new Error("expect : at "+p);
        }
        p = colon_type.get().end();

        var retType = typeName.apply(p);
        if( retType.isEmpty() ){
            throw new Error("expect type at "+p);
        }
        p = retType.get().end();

        var thrsList = new ArrayList<TypeNameAST>();
        var thrs = _throws.apply(p);
        if( thrs.isPresent() ){
            p = thrs.get().end();

            while( true ){
                var thType = typeName.apply(p);
                if( thType.isEmpty() ){
                    throw new Error("expect typeName at "+thType);
                }

                thrsList.add(thType.get());
                p = thType.get().end();

                var cm = comma.apply(p);
                if( cm.isPresent() ){
                    p = cm.get().end();
                    continue;
                }

                break;
            }
        }

        MethodAST msign = new MethodAST(begin,p);
        msign.setName(name.get().token.text());
        msign.setArgs(args);
        msign.setExceptions(thrsList);
        msign.setReturnType(retType.get());

        return Optional.of(msign);
    };
    //endregion
    //region methodBody ::= '{' { token } '}'
    /**
     * '{' { token } '}'
     */
    public static final GR<TPointer, MethodBodyAST> methodBody = p -> {
        var pOp = p.lookup(0);

        if( pOp.isEmpty() )return Optional.empty();
        if( !(pOp.get() instanceof KeyWordToken) )return Optional.empty();

        var pOp1 = ((KeyWordToken)pOp.get());
        if( pOp1.keyWord!=KeyWord.braceOpen )return Optional.empty();

        List<CToken> body = new ArrayList<>();
        body.add(pOp.get());

        int level = 1;
        int idx = 0;
        while( true ){
            idx++;
            var op = p.lookup(idx);
            if( op.isEmpty() )break;
            if( op.get() instanceof KeyWordToken ){
                var kw = ((KeyWordToken) op.get()).keyWord;
                if( kw==KeyWord.braceOpen ){
                    level++;
                    body.add(op.get());
                }else if( kw==KeyWord.braceClose ){
                    level--;
                    body.add(op.get());
                    if( level<=0 )break;
                }else{
                    body.add(op.get());
                }
            } else {
                body.add(op.get());
            }
        }

        var mbody = new MethodBodyAST(p, p.move(idx));
        body.forEach( b -> mbody.getBody().add(b) );

        return Optional.of(mbody);
    };
    //endregion

    /**
     * <pre>
     * clazz ::=
     *  { accFlag } 'class' id [ 'extends' typeName ] [ 'implements' typeName { ',' typeName } ]
     *  ( ;
     *  | '{'
     *      classBody
     *    '}'
     *  )
     *
     *  classBody ::= { { accFlag } classEntry }
     *
     *  classEntry ::= nameTypePair ';'
     *               | methodSign ( ';' | methodBody )
     * </pre>
     */
    public static final GR<TPointer, ClassAST> clazz = p -> {
        var begin = p;
        var cls = new ClassAST(begin, begin);

        var accFlags = 0;
        while( true ){
            var af = accFlag.apply(p);
            if( af.isPresent() ){
                p = af.get().end();
                if( af.get().token.accFlag.isPresent() ){
                    accFlags = accFlags | af.get().token.accFlag.get();
                }
            }else {
                break;
            }
        }
        cls.setAccess(accFlags);

        var clsKw = classKW.apply(p);
        if( clsKw.isEmpty() )return Optional.empty();
        p = clsKw.get().end();

        var clsName = identifier.apply(p);
        if( clsName.isEmpty() )return Optional.empty();
        p = clsName.get().end();
        cls.setName(clsName.get().token().text());

        var ext_kw = _extends.apply(p);
        if( ext_kw.isPresent() ){
            var ext = typeName.apply(ext_kw.get().end());
            if( ext.isEmpty() ){
                throw new Error("expect extends at "+p.move(1));
            }
            p = ext.get().end();

            cls.setExtendz(ext.get());
        }

        var impl_kw = _implements.apply(p);
        if( impl_kw.isPresent() ){
            p = impl_kw.get().end();
            var impLs = new ArrayList<TypeNameAST>();
            while( true ){
                var tn = typeName.apply(p);
                if( tn.isEmpty() ){
                    throw new IllegalArgumentException("expect typename at "+p);
                }
                impLs.add(tn.get());
                p = tn.get().end();

                var c = comma.apply(p);
                if( c.isPresent() ){
                    p = c.get().end();
                    continue;
                }

                break;
            }
            cls.setInterfaces(impLs);
        }

        var clsSem1 = semicolon.apply(p);
        if( clsSem1.isPresent() ){
            p = clsSem1.get().end();
        }else {
            var bodyStart = braceOpen.apply(p);
            if( bodyStart.isEmpty() ){
                throw new Error("expect { or ; at "+p);
            }
            p = bodyStart.get().end();

            /////////////

            TPointer entryBegin = null;
            while( true ){
                accFlags = 0;
                while( true ){
                    var af = accFlag.apply(p);
                    if( af.isPresent() ){
                        if( entryBegin==null )entryBegin = p;
                        p = af.get().end();
                        if( af.get().token.accFlag.isPresent() ){
                            accFlags = accFlags | af.get().token.accFlag.get();
                        }
                    }else {
                        break;
                    }
                }

                var namePair = nameTypePair.apply(p);
                if( namePair.isPresent() ){
                    if( entryBegin==null )entryBegin = p;

                    var sem = semicolon.apply(namePair.get().end());
                    if( sem.isEmpty())throw new Error("expect ; at "+namePair.get().end());

                    var fld = new FieldAST(entryBegin, sem.get().end());
                    fld.setName(namePair.get().getName());
                    fld.setType(namePair.get().getType());
                    fld.setAccess(accFlags);

                    cls.getFields().add(fld);

                    p = fld.end();
                }else {
                    var methSig = methodSign.apply(p);
                    if( methSig.isPresent() ){
                        if( entryBegin==null )entryBegin = p;

                        p = methSig.get().end();

                        var sem = semicolon.apply(p);
                        if( sem.isPresent() ){
                            p = sem.get().end();

                            var meth = methSig.get();
                            meth.setAccess(accFlags);
                            meth.begin(entryBegin);

                            cls.getMethods().add(meth);
                            continue;
                        } else {
                            var body = methodBody.apply(p);
                            if( body.isPresent() ){
                                p = body.get().end();

                                var meth = methSig.get();
                                meth.setAccess(accFlags);
                                meth.begin(entryBegin);

                                body.get().getBody().forEach( t -> meth.getBody().add(t) );

                                cls.getMethods().add(meth);
                                continue;
                            } else {
                                throw new Error("expect method body at "+p);
                            }
                        }
                    }
                    break;
                }
            }

            /////////////

            var bodyEnd = braceClose.apply(p);
            if( bodyEnd.isEmpty() ){
                throw new Error("expect } at "+p);
            }
            p = bodyEnd.get().end();
        }

        cls.end(p);
        return Optional.of(cls);
    };

    /**
     * <pre>
     * unit ::= [ pkgName ]
     *          clazz { clazz }
     * </pre>
     */
    public static final GR<TPointer, UnitAST> unit = p -> {
        UnitAST unit = new UnitAST(p,p.move(1));

        var pkg = pkgName.apply(p);
        if( pkg.isPresent() ){
            p = pkg.get().end();
            unit.setPkg(pkg.get());
        }

        while( true ){
            var imp = importz.apply(p);
            if( imp.isEmpty() )break;
            p = imp.get().end();
            unit.getImports().add(imp.get());
        }

        var cls = clazz.apply(p);
        if( cls.isEmpty() )return Optional.empty();

        while( true ){
            unit.getClasses().add(cls.get());
            unit.end(p);
            p = cls.get().end();

            var nextCls = clazz.apply(p);
            if( nextCls.isEmpty() ){
                break;
            } else {
                cls = nextCls;
            }
        }

        return Optional.of(unit);
    };
}
