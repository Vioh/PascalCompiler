package parser;
import main.*;
import scanner.*;
import static scanner.TokenKind.*;

public class AssignStatm extends Statement {
	Variable var;
	Expression expr;
	
	public AssignStatm(int lNum) {
		super(lNum);
	}
	
	@Override
	public String identify() {
		return "<assign statm> on line " + lineNum;
	}
	
	public static AssignStatm parse(Scanner s) {
		enterParser("assign statm");
		AssignStatm assignment = new AssignStatm(s.curLineNum());
		
		assignment.var = Variable.parse(s);
		s.skip(assignToken);
		assignment.expr = Expression.parse(s);
		
		leaveParser("assign statm");
		return assignment;
	}
	
	@Override
	public void prettyPrint() {
		var.prettyPrint();
		Main.log.prettyPrint(" := ");
		expr.prettyPrint();
	}
	
	@Override
	public void check(Block curScope, Library lib) {
		var.check(curScope, lib);
		expr.check(curScope, lib);
		
		// Check if both sides of assignment are of the same type
		var.type.checkType(expr.type, ":=", this, 
				"Different types in assignment!");
		
		// Check if the variable is assignable
		var.varRef.checkWhetherAssignable(this);
	}
}