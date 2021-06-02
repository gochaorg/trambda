package xyz.cofe.trambda.tcp;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class PubProxy {
    private static final Logger log = LoggerFactory.getLogger(PubProxy.class);

    public <T> T proxy( Class<T> cls ){
        InvocationHandler handler = new InvocationHandler() {
            @Override
            public Object invoke(Object proxy, Method method, Object[] args) throws Throwable{
                if( cls == method.getDeclaringClass() ){
                    if( method.getParameterCount()>0 )throw hasParamsError(method);

                    var retType = method.getReturnType();
                    if( retType!=Publisher.class ){
                        throw retNotPublisher(method);
                    }

                    //noinspection SuspiciousInvocationHandlerImplementation
                    return publisher(method);
                }

                throw notImpl(method);
            }
        };

        //noinspection unchecked
        return (T)Proxy.newProxyInstance( cls.getClassLoader(), new Class[]{cls}, handler );
    }

    protected Publisher<?> publisher( Method method ){
        log.info("build publisher {}",method);
        return null;
    }
    protected Throwable notImpl( Method method ){
        return new UnsupportedOperationException("not implemented "+method);
    }
    protected Throwable hasParamsError( Method method ){
        return new Error("can't invoke "+method+", has params");
    }
    protected Throwable retNotPublisher( Method method ){
        return new Error("can't invoke "+method+", return not "+Publisher.class);
    }
}
