package xyz.cofe.trambda.bc;

import org.junit.jupiter.api.Test;

public class TypeDefTest {
/*
    // Ljava/util/List;
    // Ljava/util/List<Ljava/lang/String;>;

    // (Ljava/util/List;)V
    // (Ljava/util/List<Ljava/lang/String;>;)V

    // ()Ljava/util/List<Ljava/lang/String;>;
 */
    @Test
    public void test01(){
        var r = TypeDef.parseOne("Ljava/util/List<Ljava/lang/String;>;",0);
        System.out.println(r.get().a()+" "+r.get().a().toJavaTypeName());
    }

    @Test
    public void test02(){
        var r = TypeDef.parse("(Ljava/util/List<Ljava/lang/String;>;)V",0);
        System.out.println(r.get().a()+" "+r.get().a().toJavaTypeName());
    }
}
