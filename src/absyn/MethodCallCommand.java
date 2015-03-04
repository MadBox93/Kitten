package absyn;

import java.io.FileWriter;
import java.util.Set;

import semantical.TypeChecker;
import symbol.Symbol;
import translate.Block;
import types.ClassType;
import types.CodeSignature;
import types.MethodSignature;
import types.Type;
import types.TypeList;
import types.VoidType;
import bytecode.POP;
import bytecode.VIRTUALCALL;

/**
 * A node of abstract syntax representing a method call command, that is,
 * a method call whose returned value (if any) is not used.
 *
 * @author  <A HREF="mailto:fausto.spoto@univr.it">Fausto Spoto</A>
 */

public class MethodCallCommand extends Command {

    /**
     * The abstract syntax of the receiver of the call, that is,
     * of the expression whose value
     * is used to select at run-time the target of the call.
     */

    private Expression receiver;

    /**
     * The name of the method which is called.
     */

    private Symbol name;

    /**
     * The abstract syntax of the actual parameters of the call.
     */

    private ExpressionSeq actuals;

    /**
     * The signature of the <i>static target</i> method of the call.
     * The actual <i>dynamic target</i> method which will be called at
     * run-time will
     * be this static target or one of its redefinitions in subclasses.
     * This field is <tt>null</tt> if type-checking has not been performed yet.
     */

    private MethodSignature method;

    /**
     * Constructs the abstract syntax of a method call command.
     *
     * @param pos the position in the source file where it starts
     *            the concrete syntax represented by this abstract syntax
     * @param receiver the abstract syntax of the receiver of the call,
     *                 that is, of the expression whose value
     *                 is used to select at run-time the target of the call
     * @param name the name of the method which is called
     * @param actuals the abstract syntax of the actual parameters of the call
     */

    public MethodCallCommand(int pos, Expression receiver,
			     Symbol name, ExpressionSeq actuals) {
	super(pos);

	this.receiver = receiver;
	this.name = name;
	this.actuals = actuals;
    }

    /**
     * Yields the abstract syntax of the receiver of the method call command,
     * that is, of the expression whose value
     * is used to select at run-time the target of the call.
     *
     * @return the abstract syntax of the receiver of the method call command,
     *         that is, of the expression whose value
     *         is used to select at run-time the target of the call
     */

    public Expression getReceiver() {
	return receiver;
    }

    /**
     * Yields the name of the method which is called.
     *
     * @return the name of the method which is called
     */

    public Symbol getName() {
	return name;
    }

    /**
     * Yields the abstract syntax of the actual parameters of the call, if any.
     *
     * @return the abstract syntax of the actual parameters of the call
     */

    public ExpressionSeq getActuals() {
	return actuals;
    }

    /**
     * Yields a the string labelling the class
     * of abstract syntax represented by
     * this node. We redefine this method in order to add the method referenced
     * by this call, if it has already been computed by a type-checker.
     *
     * @return a string describing the label of this node of abstract syntax,
     *         followed by the method referenced by this call, if already
     *         computed by a type-checker
     */

    protected String label() {
	// if this command has not been type-checked yet, we do not
	// report the method referenced
	if (method == null) return super.label();
	// otherwise we add the method referenced
	else return super.label() + "\\nreferences " + method;
    }

    /**
     * Adds abstract syntax class-specific information in the dot file
     * representing the abstract syntax of the method call command.
     * This amounts to adding arcs from the node for the method call
     * command to the abstract syntax for its <tt>receiver</tt>,
     * <tt>name</tt> and <tt>actuals</tt> components.
     *
     * @param where the file where the dot representation must be written
     */

    protected void toDotAux(FileWriter where) throws java.io.IOException {
	linkToNode("receiver",receiver.toDot(where),where);
	linkToNode("name",name.toDot(where),where);

	if (actuals != null) linkToNode("actuals",actuals.toDot(where),where);
    }

    /**
     * Performs the type-checking of a method call command
     * by using a given type-checker. It type-checks the receiver and the
     * formal parameters of the call. Then it checks that the receiver has
     * class type and that there is exactly one possible static target
     * method for the call. It returns the type-checker passed as a parameter.
     *
     * @param checker the type-checker to be used for type-checking
     * @return the type-checker <tt>checker</tt>
     */

    protected TypeChecker typeCheck$0(TypeChecker checker) {
	Type receiverType = receiver.typeCheck(checker);
	TypeList actualsTypes = actuals != null ?
	    actuals.typeCheck(checker) : TypeList.EMPTY;

	// the receiver must have class type. Hence we cannot call
	// methods of an array. This is fine since arrays are subclasses
	// of Object which has no methods
	if (!(receiverType instanceof ClassType)) error("class type required");
	else {
	    // we collect the set of methods which are compatible
	    // with the static types of the parameters,
	    // and have no other compatible method which is
	    // more specific than them
	    Set<MethodSignature> methods = ((ClassType) receiverType).methodsLookup(name,actualsTypes);

	    if (methods.isEmpty())
		// there is no matching method!
		error("no matching method for call to \"" + name + "\"");
	    else if (methods.size() >= 2)
		// more than two matching methods, and none of them is
		// more specific of the other? Ambiguous call
		error("call to method \"" + name + "\" is ambiguous");
	    else
		// there is only one candidate, that's fine
		method = (MethodSignature)methods.iterator().next();
	}

	// the type-checker has not been modified
	return checker;
    }

    /**
     * Checks that this method call comand
     * does not contain <i>dead-code</i>, that is,
     * commands which can never be executed. This is always true for
     * method calls.
     *
     * @return false, since this command never terminates with a
     *         <tt>return</tt> command
     */

    public boolean checkForDeadcode() {
	return false;
    }

    /**
     * Translates this command into intermediate
     * Kitten bytecode. Namely, it returns a code which starts with
     * <br>
     * <i>translation of the <tt>receiver</tt><br>
     * <i>translation of the first actual parameter</i><br>
     * ...<br>
     * <i>translation of the last actual parameter</i><br>
     * <tt>virtualcall method</tt><br>
     * <tt>pop</tt> (if the method does not return <tt>void</tt>)<br>
     * <br>
     * and continues with the given <tt>continuation</tt>.
     *
     * @param where the method or constructor where this expression occurs
     * @param continuation the continuation to be executed after this command
     * @return the code executing this command and then
     *         the <tt>continuation</tt>
     */

    public Block translate(CodeSignature where, Block continuation) {
	if (method.getReturnType() != VoidType.INSTANCE)
	    // if the method does return a value, we must throw it away
	    continuation = new POP(where,method.getReturnType())
		.followedBy(continuation);

	// we put an instruction which calls the method
	continuation =
	    new VIRTUALCALL(where,(ClassType)(receiver.getStaticType()),method)
	    .followedBy(continuation);

	// we translate the actual parameters
	if (actuals != null)
	    continuation = actuals.translateAs
		(where,method.getParameters(),continuation);

	// we translate the receiver of the call
	return receiver.translate(where,continuation);
    }
}
