package exemplos.tutoriais.nesdoug;

import fans.core.Ca65Base;
import fans.core.constants.DmaPxConstants;
import fans.core.enums.BusRegisters;

public class Part2DMAPalette extends Ca65Base {
	
	public void init() {		
		a8Bit();
		stz(BusRegisters.CGADD);
		
		dmaFromPalleteToCGRAM();
		dmaFromPalleteToCGRAM();
		initScreen();
		foreverLoop();
		
		label("BG_Palette", () -> {			
			incbin("../pallete/default.pal");
		});
	}

	private void dmaFromPalleteToCGRAM() {
		String source = "BG_Palette";
		String length = "#256"; 
		int channel = 0;
		
		dmaToCgram(source, length, DmaPxConstants.TRANSFER_MODE_0, channel);
	}

	public static void main(String[] args) {
		new Part2DMAPalette().buildAsmFile();
	}
}