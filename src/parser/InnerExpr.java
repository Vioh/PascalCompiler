package parser;
import main.*;
import scanner.*;
import static scanner.TokenKind.*;

class InnerExpr extends Factor {
	Expression expr;
	
	public InnerExpr(int lNum) {//MIGZ
		super(lNum);
	}
	
	@Override
	public String identify() {
		return "<inner-expr> on line " + lineNum;
	}
	
	public static InnerExpr parse(Scanner s) {
		enterParser("inner-expr");
		
		InnerExpr ie = new InnerExpr(s.curLineNum());
		
		s.skip(leftParToken);
		ie.expr = Expression.parse(s);
		s.skip(rightParToken);
		
		leaveParser("inner-expr");
		return ie;
	}
	
}