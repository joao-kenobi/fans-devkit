package fans.examples.nesdoug;

import fans.core.Ca65Base;
import fans.core.constants.DmaConstants;
import fans.core.enums.BusRegisters;

public class Part2DMAPalette extends Ca65Base {
	
	private static final String GFXPATH = "includes/graphics/nesdoug/part2";
	private static final String BG_PALETTE_LABEL = "bg_palette";
	
	
	public void init() {		
		stz(BusRegisters.CGADD);
		
		dmaToCgram(BG_PALETTE_LABEL, DmaConstants.TRANSFER_MODE_0, 0);
		dmaToCgram(BG_PALETTE_LABEL, DmaConstants.TRANSFER_MODE_0, 0);
		initScreen();
		foreverLoop();
	
		
		label("bg_palette", () -> {			
			incbin(GFXPATH+"/default.pal");
		}, "bg_palette_end");
	}

	public static void main(String[] args) {
		new Part2DMAPalette().compileAndRun();
	}
}