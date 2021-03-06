package bytecode;

import javaBytecodeGenerator.JavaClassGenerator;

import org.apache.bcel.generic.InstructionFactory;
import org.apache.bcel.generic.InstructionList;

import types.Type;

/**
 * A bytecode which reads an element of an array and pushes its value on the
 * stack. If the reference to the array is <tt>nil</tt>, the computation
 * stops.
 * <br><br>
 * ..., array reference, index -> ..., value
 *
 * @author <A HREF="mailto:fausto.spoto@univr.it">Fausto Spoto</A>
 */

public class ARRAYLOAD extends NonCallingSequentialBytecode {

    /**
     * The type of the elements of the array which is accessed.
     */

    private final Type type;

    /**
     * Constructs a bytecode which
     * reads an element of an array and pushes its value on the stack.
     * Note that the index, where the array
     * is read, is provided at run-time through the stack.
     *
     * @param type the type of the elements of the array which
     *             is read by this bytecode
     */

    public ARRAYLOAD(Type type) {
    	this.type = type;
    }

    @Override
    public String toString() {
	return "load from array of " + type;
    }

    /**
     * Yields the type of the elements of the array.
     *
     * @return the type of the elements of the array
     */

    public Type getType() {
	return type;
    }

    /**
     * Generates the Java bytecode corresponding
     * to this Kitten bytecode. Namely, it generates an <tt>iaload</tt>
     * if <tt>type</tt> is <tt>int</tt>, an <tt>faload</tt> if
     * <tt>type</tt> is <tt>float</tt> and an <tt>aaload</tt> if
     * <tt>type</tt> is a class or array type.
     *
     * @param classGen the Java class generator to be used for this
     *                 Java bytecode generation
     * @return the Java <tt>iaload</tt>, <tt>faload</tt> or <tt>aaload</tt>
     *         bytecode, depending on <tt>type</tt>
     */

    public InstructionList generateJavaBytecode(JavaClassGenerator classGen) {
	// we use the instruction factory to simplify the choice among
	// the three possible Java bytecodes
	return new InstructionList(InstructionFactory.createArrayLoad(type.toBCEL()));
    }
}