package parser;
import main.*;
import scanner.*;
import java.util.ArrayList;
import static scanner.TokenKind.*;

public class SimpleExpr extends PascalSyntax {
	PrefixOperator prefix; // optional
	ArrayList<Term> termList = new ArrayList<Term>();
	ArrayList<TermOperator> termOprList = new ArrayList<TermOperator>();
	types.Type type;
	
	public SimpleExpr(int lNum) {
		super(lNum);
	}
	
	@Override
	public String identify() {
		return "<simple expr> on line " + lineNum;
	}
	
	public static SimpleExpr parse(Scanner s) {
		enterParser("simple expr");		
		SimpleExpr expr = new SimpleExpr(s.curLineNum());
		
		if(s.curToken.kind.isPrefixOpr()) {
			expr.prefix = PrefixOperator.parse(s);		
		} 
		while(true) {
			expr.termList.add(Term.parse(s));
			if(! s.curToken.kind.isTermOpr()) break;
			expr.termOprList.add(TermOperator.parse(s));
		}
		leaveParser("simple expr");
		return expr;
	}
	
	@Override
	public void prettyPrint() {
		if(prefix != null) prefix.prettyPrint();
		for(int i = 0; i < termList.size(); i++) {
			termList.get(i).prettyPrint();
			if(i < termOprList.size()) {
				Main.log.prettyPrint(" ");
				termOprList.get(i).prettyPrint();
				Main.log.prettyPrint(" ");
			}
		}
	}
	
	@Override
	public void check(Block curScope, Library lib) {
		Term term0 = termList.get(0); // the first term always exists
		term0.check(curScope, lib);
		type = term0.type;
		
		for(int i = 0; i < termOprList.size(); i++) {
			termList.get(i+1).check(curScope, lib);
			TokenKind oprType = termOprList.get(i).oprType;
			types.Type lTermType = termList.get(i).type;   // type of left term
			types.Type rTermType = termList.get(i+1).type; // type of right term
			
			if(oprType == addToken || oprType == subtractToken) {
				lTermType.checkType(lib.integerType, 
					"left " + oprType + " operand", this, 
					"Left operand to " + oprType + " is not a number!");
				rTermType.checkType(lib.integerType, 
					"right " + oprType + " operand", this, 
					"Right operand to " + oprType + " is not a number!");
			}
			else if(oprType == orToken) {
				lTermType.checkType(lib.boolType, 
					"left 'or' operand", this, 
					"Left operand to " + oprType+ " is not a Boolean!");
				rTermType.checkType(lib.boolType, 
					"right 'or' operand", this, 
					"Right operand to " + oprType + " is not a Boolean!");
			}
		}
		if(prefix != null) {
			term0.type.checkType(lib.integerType, 
					"prefix " + prefix.oprType + " operand", this,
					"Prefix + or - may only be applied to Integers.");
		}
	}

	@Override
	public void genCode(CodeFile f) {
		// Handle 1st term and prefix
		termList.get(0).genCode(f); // the first factor always exists
		if(prefix != null && prefix.oprType == subtractToken) {
			f.genInstr("", "negl", "%eax", "  - (prefix)");
		}
		// Handle other terms
		for(int i = 0; i < termOprList.size(); i++) {
			f.genInstr("", "pushl", "%eax", "");
			termList.get(i+1).genCode(f);
			f.genInstr("", "movl", "%eax,%ecx", "");
			f.genInstr("", "popl", "%eax", "");
			
			switch(termOprList.get(i).oprType) {
			case addToken:
				f.genInstr("", "addl", "%ecx,%eax", "  +"); break;
			case subtractToken:
				f.genInstr("", "subl", "%ecx,%eax", "  -"); break;
			case orToken:
				f.genInstr("", "orl", "%ecx,%eax", "  or"); break;
			default: // will never execute
				break;
			}
		}
	}
}