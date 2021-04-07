package xyz.cofe.trambda.tcp;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;
import xyz.cofe.fn.Pair;

/**
 * Сообщение о ошибке
 */
public class ErrMessage implements Message {
    private String message;

    /**
     * Возвращает текст ошибки
     * @return текст ошибки
     */
    public String getMessage(){ return message; }

    /**
     * Указывает текст ошибки
     * @param message текст ошибки
     */
    public void setMessage(String message){ this.message = message; }

    /**
     * Указывает текст ошибки
     * @param message текст ошибки
     * @return SELF ссылка
     */
    public ErrMessage message(String message){
        this.message = message;
        return this;
    }

    /**
     * Формирование описания ошибки
     * @param sb куда записывать описание
     * @param errs список ошибок
     */
    private static void desc(StringBuilder sb, Collection<Pair<Throwable,Integer>> errs) {
        Set<Throwable> visited = new HashSet<>();

        while( !errs.isEmpty() ){
            var err1 = errs.iterator().next();
            errs.remove(err1);

            if( visited.contains(err1.a()) )continue;
            visited.add(err1.a());

            var err = err1.a();

            sb.append("exception: ").append(err.getClass().getName()).append("\n");
            if( err.getMessage() != null ) sb.append("message: ").append(err.getMessage()).append("\n");
            if( err.getLocalizedMessage() != null ) sb.append("localizedMessage: ").append(err.getLocalizedMessage()).append("\n");

            var lvl = err1.b();
            if( lvl==0 ){
                var stack = err.getStackTrace();
                if( stack!=null && stack.length>0 ){
                    sb.append("stack:").append("\n");
                    for( int i=0; i<stack.length; i++ ){
                        sb.append("[").append(i+1).append("/").append(stack.length).append("] ");
                        var se = stack[i];
                        if( se!=null ){
                            sb.append(se.getModuleName()).append("/").append(se.getModuleVersion());
                            sb.append(" ").append("CL{").append(se.getClassLoaderName()).append("}");
                            sb.append(" ").append(se.getFileName()).append(":").append(se.getLineNumber());
                            sb.append(" ").append(se.getClassName()).append("#").append(se.getMethodName());
                        }else{
                            sb.append("null");
                        }
                        sb.append("\n");
                    }
                }
            }

            if( err.getCause()!=null ){
                errs.add(Pair.of(err.getCause(), err1.b()+1));
            }
            if( err.getSuppressed()!=null ){
                for( var e : err.getSuppressed() ){
                    if( e!=null ){
                        errs.add(Pair.of(e, err1.b()+1));
                    }
                }
            }
        }
    }

    /**
     * Указывает текст ошибки
     * @param err описание ошибки
     * @return SELF ссылка
     */
    public ErrMessage error(Throwable err){
        this.message = null;
        if( err!=null ){
            StringBuilder sb = new StringBuilder();

            var ls = new ArrayList<Pair<Throwable,Integer>>();
            ls.add(Pair.of(err,0));
            desc(sb,ls);

            this.message = sb.toString();
        }
        return this;
    }
}
