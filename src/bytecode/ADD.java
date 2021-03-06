package bytecode;

import javaBytecodeGenerator.JavaClassGenerator;

import org.apache.bcel.generic.InstructionList;

import types.NumericalType;

/**
 * A bytecode that adds the top two elements of the stack.
 * <br><br>
 * ..., value1, value2 -> ..., value1 + value2
 *
 * @author <A HREF="mailto:fausto.spoto@univr.it">Fausto Spoto</A>
 */

public class ADD extends ArithmeticBinOpBytecode {

	/**
	 * Constructs a bytecode that adds the top two elements of the stack.
	 *
	 * @param where the method or constructor where this bytecode occurs
	 * @param type the semantical type of the values which are added
	 */

	public ADD(NumericalType type) {
		super(type);
	}

	@Override
	public InstructionList generateJavaBytecode(JavaClassGenerator classGen) {
		InstructionList il = new InstructionList();

		getType().add(il);

		return il;
	}
}