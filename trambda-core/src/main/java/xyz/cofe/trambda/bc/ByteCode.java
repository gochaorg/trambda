package xyz.cofe.trambda.bc;

import java.io.Serializable;
import xyz.cofe.collection.ImTree;
import xyz.cofe.collection.ImTreeWalk;
import xyz.cofe.iter.Eterable;

/**
 * Байт-код инструкция
 */
public interface ByteCode extends Serializable, ImTree<ByteCode>, ImTreeWalk<ByteCode> {
    /**
     * Возвращает дочерние узлы
     * @return дочерние узлы
     */
    @Override
    default Eterable<ByteCode> nodes(){
        return Eterable.empty();
    }
}
