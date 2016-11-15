package parser;
import main.*;

public abstract class PascalSyntax {
	public int lineNum;

	PascalSyntax(int n) {
		lineNum = n;
	}

	boolean isInLibrary() {
		return lineNum < 0;
	}

	public void error(String message) {
		Main.error("Error at line " + lineNum + ": " + message);
	}

	static void enterParser(String nonTerm) {
		Main.log.enterParser(nonTerm);
	}

	static void leaveParser(String nonTerm) {
		Main.log.leaveParser(nonTerm);
	}
	
	abstract public String identify();
	abstract void prettyPrint();
	abstract void check(Block curScope, Library lib);
	abstract void genCode(CodeFile f);
}