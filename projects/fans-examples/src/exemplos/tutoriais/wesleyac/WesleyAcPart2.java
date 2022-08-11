package exemplos.tutoriais.wesleyac;

import fans.core.Ca65Base;
import fans.core.enums.BusRegisters;

public class WesleyAcPart2 extends Ca65Base {
	
	public void init() {
		
		rawAsm("VRAM_CHARS = $0000");
		rawAsm("VRAM_BG1   = $1000");
		
		
		// Set up the color palette
		stz(BusRegisters.CGADD);
		
		// Color 0 = black
		// Color 1 = red
		// Color 2 = blue
		// Color 3 = green
		String[] colors = {
				"#$0000", 
				"#$001f", 
				"#$03e0", 
				"#$0003"
		};
		
		for (String color : colors) {			
			ldaStaTwice(color, BusRegisters.CGDATA);
		}
		
		// Set Graphics Mode 0, 8x8 tiles
		stz(BusRegisters.BGMODE);
		
		// Set BG1 and tile map and character data
		ldaSta("#>VRAM_BG1", BusRegisters.BG1SC);
		ldaSta("#VRAM_CHARS", BusRegisters.BG12NBA);
		
		
		// Load character data into VRAM
		ldaSta("#$80", BusRegisters.VMAIN);
		
		ldxStx("#VRAM_CHARS", BusRegisters.VMADDL);
						
		loadTiles();
		
		// Write a tile to position (1, 1)
		rawAsm("TILE_X = 1");
		rawAsm("TILE_Y = 1");
		
		ldxStx("#(VRAM_BG1+(TILE_Y * 32)+TILE_X)", BusRegisters.VMADDL);
		
		ldaSta("#$01", BusRegisters.VMDATAL); // tile number
		stz(BusRegisters.VMDATAH);
		
		// Show BG1
		ldaSta("#%00000001", BusRegisters.TM);
		
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
		
		include("wesleyac/BackgroundGraphicsPart2_Tiles.asm");
	}

	private void loadTiles() {
		ldx("#$0");
		
		label("@charset_loop", () -> {			
			ldaSta("charset,x", BusRegisters.VMDATAL);
			inx();
			ldaSta("charset,x", BusRegisters.VMDATAH);
			inx();
			cpx("#(charset_end-charset)");
			bne("@charset_loop");
		});
		
		
	}

	public static void main(String[] args) {
		new WesleyAcPart2().buildAsmFile();
	}
}
