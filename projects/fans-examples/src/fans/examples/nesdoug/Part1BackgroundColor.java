package fans.examples.nesdoug;

import fans.core.Ca65Base;
import fans.core.enums.BusRegisters;

public class Part1BackgroundColor extends Ca65Base {
	
	public void init() {
		stz(BusRegisters.CGADD);
		ldaStaTwice("#$001F", BusRegisters.CGDATA);
		
		initScreen();
		foreverLoop();
	}

	public static void main(String[] args) {
		new Part1BackgroundColor().compileAndRun();
	}
}