package parser;
import main.*;
import scanner.*;
import static scanner.TokenKind.*;

class Variable extends Factor {
	String name;
	Expression expr;
	
	public Variable(int lNum) { // MIGZ
		super(lNum);
	}
	
	@Override
	public String identify() {
		return "<variable> on line " + lineNum;
	}
	
	public static Variable parse(Scanner s) {
		enterParser("variable");
		
		Variable v = new Variable(s.curLineNum());
		this.name = s.curToken.id;
		s.readNextToken();
		
		if(s.test(leftParToken)) {
			s.readNextToken(); //skipping the left parenthesis 
			v.expr = new Expression.parse(s)
			s.skip(rightParToken);			
		} else {
			v.expressions = null;
		}
		leaveParser("variable");
		return v;
	}
}
