package bytecode;

import javaBytecodeGenerator.JavaClassGenerator;

import org.apache.bcel.generic.InstructionList;

import types.NumericalType;

/**
 * A bytecode which multiplies the top two elements of the stack.
 * <br><br>
 * ..., value1, value2 -> ..., value1 * value2
 *
 * @author  <A HREF="mailto:fausto.spoto@univr.it">Fausto Spoto</A>
 */

public class MUL extends ArithmeticBinOpBytecode {

	/**
	 * Constructs a bytecode which
	 * multiplies the top two elements of the stack.
	 *
	 * @param type the semantical type of the values which are multiplied
	 */

	public MUL(NumericalType type) {
		super(type);
	}

	/**
	 * Generates the Java bytecode corresponding to this Kitten bytecode.
	 *
	 * @param classGen the Java class generator to be used for this
	 *                 Java bytecode generation
	 * @return the <tt>imul</tt> Java bytecode if <tt>type</tt> is <tt>int</tt>
	 *         and the <tt>fmul</tt> Java bytecode if <tt>type</tt> is
	 *         <tt>float</tt>
	 */

	public InstructionList generateJavaBytecode(JavaClassGenerator classGen) {
		InstructionList il = new InstructionList();

		getType().mul(il);

		return il;
	}
}