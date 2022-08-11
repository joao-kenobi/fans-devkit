package exemplos.tutoriais.wesleyac;

import fans.core.Ca65Base;
import fans.core.enums.BusRegisters;

public class WesleyAcPart1 extends Ca65Base {
	
	public void init() {
		
		stz(BusRegisters.CGADD); // Set up the color palette
		
		
		// Set color zero to red
		// $001f = %0000000000011111
		//           bbbbbgggggrrrrr
		ldaStaTwice("#$001f", BusRegisters.CGDATA);
		
		initScreen();
		
		label("busywait", () -> {
			bra("busywait");
		});
		
		label("nmi", () -> {
			bit(BusRegisters.RDNMI);
		});
		
		label("_rti", () -> {
			rawAsm("rti");
		});	
	}

	public static void main(String[] args) {
		new WesleyAcPart1().buildAsmFile(); 
	}
}
