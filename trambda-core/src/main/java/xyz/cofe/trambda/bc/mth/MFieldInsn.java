package xyz.cofe.trambda.bc.mth;

import xyz.cofe.trambda.bc.ByteCode;

/**
 * the opcode of the type instruction to be visited. This opcode is either
 * GETSTATIC,
 * PUTSTATIC,
 * GETFIELD or
 * PUTFIELD.
 *
 * <hr>
 *     getstatic
 * <h2 style="font-weight: bold">Operation</h2>
 *
 * Get static field from class
 *<h2 style="font-weight: bold"> Format</h2>
 *
 * <pre>
 * getstatic
 * indexbyte1
 * indexbyte2
 * </pre>
 *
 * <h2 style="font-weight: bold">Forms</h2>
 *
 * getstatic = 178 (0xb2)
 * <h2 style="font-weight: bold">Operand Stack</h2>
 *
 * <pre>
 * ..., →
 * ..., value
 * </pre>
 *
 * <h2 style="font-weight: bold">Description</h2>
 *
 * The unsigned indexbyte1 and indexbyte2 are used to construct an index into the run-time constant pool of the current class (§2.6), where the value of the index is (indexbyte1 &lt;&lt; 8) | indexbyte2. The run-time constant pool item at that index must be a symbolic reference to a field (§5.1), which gives the name and descriptor of the field as well as a symbolic reference to the class or interface in which the field is to be found. The referenced field is resolved (§5.4.3.2).
 *
 * On successful resolution of the field, the class or interface that declared the resolved field is initialized (§5.5) if that class or interface has not already been initialized.
 *
 * The value of the class or interface field is fetched and pushed onto the operand stack.
 * <h2 style="font-weight: bold">Linking Exceptions</h2>
 *
 * During resolution of the symbolic reference to the class or interface field, any of the exceptions pertaining to field resolution (§5.4.3.2) can be thrown.
 *
 * Otherwise, if the resolved field is not a static (class) field or an interface field, getstatic throws an IncompatibleClassChangeError.
 * <h2 style="font-weight: bold">Run-time Exception</h2>
 *
 * Otherwise, if execution of this getstatic instruction causes initialization of the referenced class or interface, getstatic may throw an Error as detailed in §5.5.
 *
 * <hr>
 *
 * putstatic
 * <h2 style="font-weight: bold">Operation</h2>
 *
 * Set static field in class
 * <h2 style="font-weight: bold">Format</h2>
 *
 * <pre>
 * putstatic
 * indexbyte1
 * indexbyte2
 * </pre>
 *
 * <h2 style="font-weight: bold">Forms</h2>
 *
 * putstatic = 179 (0xb3)
 * <h2 style="font-weight: bold">Operand Stack</h2>
 *
 * <pre>
 * ..., value →
 * ...
 * </pre>
 *
 * <h2 style="font-weight: bold">Description</h2>
 *
 * The unsigned indexbyte1 and indexbyte2 are used to construct an index into the run-time constant pool of the current class (§2.6), where the value of the index is (indexbyte1 &lt;&lt; 8) | indexbyte2. The run-time constant pool item at that index must be a symbolic reference to a field (§5.1), which gives the name and descriptor of the field as well as a symbolic reference to the class or interface in which the field is to be found. The referenced field is resolved (§5.4.3.2).
 *
 * <p>
 * On successful resolution of the field, the class or interface that declared the resolved field is initialized (§5.5) if that class or interface has not already been initialized.
 *
 * <p>
 * The type of a value stored by a putstatic instruction must be compatible with the descriptor of the referenced field (§4.3.2). If the field descriptor type is boolean, byte, char, short, or int, then the value must be an int. If the field descriptor type is float, long, or double, then the value must be a float, long, or double, respectively. If the field descriptor type is a reference type, then the value must be of a type that is assignment compatible (JLS §5.2) with the field descriptor type. If the field is final, it must be declared in the current class, and the instruction must occur in the &lt;clinit&gt; method of the current class (§2.9).
 *
 * <p>
 * The value is popped from the operand stack and undergoes value set conversion (§2.8.3), resulting in value'. The class field is set to value'.
 *
 * <h2 style="font-weight: bold">Linking Exceptions</h2>
 *
 * During resolution of the symbolic reference to the class or interface field, any of the exceptions pertaining to field resolution (§5.4.3.2) can be thrown.
 *
 * <p>
 * Otherwise, if the resolved field is not a static (class) field or an interface field, putstatic throws an IncompatibleClassChangeError.
 *
 * <p>
 * Otherwise, if the field is final, it must be declared in the current class, and the instruction must occur in the &lt;clinit&gt; method of the current class. Otherwise, an IllegalAccessError is thrown.
 *
 * <h2 style="font-weight: bold">Run-time Exception</h2>
 *
 * Otherwise, if execution of this putstatic instruction causes initialization of the referenced class or interface, putstatic may throw an Error as detailed in §5.5.
 * <h2 style="font-weight: bold">Notes</h2>
 *
 * A putstatic instruction may be used only to set the value of an interface field on the initialization of that field. Interface fields may be assigned to only once, on execution of an interface variable initialization expression when the interface is initialized (§5.5, JLS §9.3.1).
 *
 * <hr>
 *
 getfield
 <h2 style="font-weight: bold">Operation</h2>

 Fetch field from object
 <h2 style="font-weight: bold">Format</h2>

 <pre>
 getfield
 indexbyte1
 indexbyte2
 </pre>

 <h2 style="font-weight: bold">Forms</h2>

 getfield = 180 (0xb4)
 <h2 style="font-weight: bold">Operand Stack</h2>

 <pre>
 ..., objectref →
 ..., value
 </pre>

 <h2 style="font-weight: bold">Description</h2>

 The objectref, which must be of type reference, is popped from the operand stack. The unsigned indexbyte1 and indexbyte2 are used to construct an index into the run-time constant pool of the current class (§2.6), where the value of the index is (indexbyte1 &lt;&lt; 8) | indexbyte2. The run-time constant pool item at that index must be a symbolic reference to a field (§5.1), which gives the name and descriptor of the field as well as a symbolic reference to the class in which the field is to be found. The referenced field is resolved (§5.4.3.2). The value of the referenced field in objectref is fetched and pushed onto the operand stack.

 The type of objectref must not be an array type. If the field is protected, and it is a member of a superclass of the current class, and the field is not declared in the same run-time package (§5.3) as the current class, then the class of objectref must be either the current class or a subclass of the current class.
 <h2 style="font-weight: bold">Linking Exceptions</h2>

 During resolution of the symbolic reference to the field, any of the errors pertaining to field resolution (§5.4.3.2) can be thrown.

 Otherwise, if the resolved field is a static field, getfield throws an IncompatibleClassChangeError.
 <h2 style="font-weight: bold">Run-time Exception</h2>

 Otherwise, if objectref is null, the getfield instruction throws a NullPointerException.
 <h2 style="font-weight: bold">Notes</h2>

 The getfield instruction cannot be used to access the length field of an array. The arraylength instruction (§arraylength) is used instead.


 <hr>

 putfield
 <h2 style="font-weight: bold">Operation</h2>

 Set field in object
 <h2 style="font-weight: bold">Format</h2>

 <pre>
 putfield
 indexbyte1
 indexbyte2
 </pre>

 <h2 style="font-weight: bold">Forms</h2>

 putfield = 181 (0xb5)

 <h2 style="font-weight: bold">Operand Stack</h2>

 <pre>
 ..., objectref, value →
 ...
 </pre>

 <h2 style="font-weight: bold">Description</h2>

 The unsigned indexbyte1 and indexbyte2 are used to construct an index into the run-time constant pool of the current class (§2.6), where the value of the index is (indexbyte1 &lt;&lt; 8) | indexbyte2. The run-time constant pool item at that index must be a symbolic reference to a field (§5.1), which gives the name and descriptor of the field as well as a symbolic reference to the class in which the field is to be found. The class of objectref must not be an array. If the field is protected, and it is a member of a superclass of the current class, and the field is not declared in the same run-time package (§5.3) as the current class, then the class of objectref must be either the current class or a subclass of the current class.

 <p>
 The referenced field is resolved (§5.4.3.2). The type of a value stored by a putfield instruction must be compatible with the descriptor of the referenced field (§4.3.2). If the field descriptor type is boolean, byte, char, short, or int, then the value must be an int. If the field descriptor type is float, long, or double, then the value must be a float, long, or double, respectively. If the field descriptor type is a reference type, then the value must be of a type that is assignment compatible (JLS §5.2) with the field descriptor type. If the field is final, it must be declared in the current class, and the instruction must occur in an instance initialization method (&lt;init&gt;) of the current class (§2.9).

 <p>
 The value and objectref are popped from the operand stack. The objectref must be of type reference. The value undergoes value set conversion (§2.8.3), resulting in value', and the referenced field in objectref is set to value'.
 <h2 style="font-weight: bold">Linking Exceptions</h2>

 During resolution of the symbolic reference to the field, any of the exceptions pertaining to field resolution (§5.4.3.2) can be thrown.

 <p>
 Otherwise, if the resolved field is a static field, putfield throws an IncompatibleClassChangeError.

 <p>
 Otherwise, if the field is final, it must be declared in the current class, and the instruction must occur in an instance initialization method (&lt;init&gt;) of the current class. Otherwise, an IllegalAccessError is thrown.
 <h2 style="font-weight: bold">Run-time Exception</h2>

 Otherwise, if objectref is null, the putfield instruction throws a NullPointerException.

 */
public class MFieldInsn extends MAbstractBC implements ByteCode {
    private static final long serialVersionUID = 1;

    public MFieldInsn(){
    }
    public MFieldInsn(int opcode, String owner, String name, String descriptor){
        this.opcode = opcode;
        this.owner = owner;
        this.name = name;
        this.descriptor = descriptor;
    }

    //region opcode
    private int opcode;
    public int getOpcode(){
        return opcode;
    }

    public void setOpcode(int opcode){
        this.opcode = opcode;
    }
    //endregion
    //region owner : String
    private String owner;
    public String getOwner(){
        return owner;
    }

    public void setOwner(String owner){
        this.owner = owner;
    }
    //endregion
    //region name : String
    private String name;
    public String getName(){
        return name;
    }

    public void setName(String name){
        this.name = name;
    }
    //endregion
    //region descriptor : String
    private String descriptor;

    public String getDescriptor(){
        return descriptor;
    }

    public void setDescriptor(String descriptor){
        this.descriptor = descriptor;
    }
    //endregion

    @Override
    public String toString(){
        return "FieldInsn{" +
            "opcode=" + OpCode.code(opcode).map(OpCode::name).orElse("?") + " #" + opcode +
            ", owner='" + owner + '\'' +
            ", name='" + name + '\'' +
            ", descriptor='" + descriptor + '\'' +
            '}';
    }
}
