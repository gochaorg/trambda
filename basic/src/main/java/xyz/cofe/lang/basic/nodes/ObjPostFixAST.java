/*
 * The MIT License
 *
 * Copyright 2021 uzer.
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */
package xyz.cofe.lang.basic.nodes;

import java.util.List;
import java.util.Optional;
import xyz.cofe.lang.basic.BasicParser;

/**
 * доступ к полю / вызов метода
 * @author uzer
 */
public class ObjPostFixAST extends AST<BasicParser.ObjPostFixContext, AST<?, ?>> {
    public ObjPostFixAST( BasicParser.ObjPostFixContext c ){
        super( c );
    }
    
    public String getMemberName(){
        return antlrRule.ID().getText();
    }
    
    protected Optional<CallArgsAST> callArgs;
    public Optional<CallArgsAST> getCallArgs(){
        if( callArgs!=null )return callArgs;
        
        var ca =  antlrRule.callArgs();
        if( ca==null ){
            callArgs = Optional.empty();
        }else{
            callArgs = Optional.of(AST.wrap(ca));
        }
        return callArgs;
    }
    
    public ObjAccessType getAccessType(){
        return getCallArgs().isPresent() ? ObjAccessType.MethodCall : ObjAccessType.FieldRead;
    }

    @Override
    protected List<AST<?, ?>> createChildren() {
        var ca = getCallArgs();
        if( ca.isPresent() )return List.of( ca.get() );
        
        return List.of();
    }
    
    @Override
    public String toString(){
        return ObjPostFixAST.class.getSimpleName()+
                " ("+getAccessType()+") "+
                getMemberName()
                ;
    }
}
