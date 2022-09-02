package fans.examples.nesdoug.tests;

import fans.core.Ca65Base;
import fans.core.constants.BgModeConstants;
import fans.core.constants.DmaConstants;
import fans.core.constants.ScreenDesignationConstants;
import fans.core.constants.VMainConstants;
import fans.core.enums.BusRegisters;

public class IoriBackground extends Ca65Base {
	
	protected void before() {
		
	}
	
	public void init() {
		a8Bit();
		
		dmaFromPalleteToCGRAM();
		
		ldaSta(VMainConstants.INCREMENT_MODE_BY_1, BusRegisters.VMAIN);
		dmaFromTilesToVRAM();
		dmaFromTilemapToVRAM();
		
		setBGMode(BgModeConstants.MODE1);
		setBG1And2CharacterAddress("#$00");
		setBG1TilemapAddress("#$60");
		ldaSta(ScreenDesignationConstants.BG1_ON, BusRegisters.TM); // $01 = only bg 1 is active
		
		initScreen();
		foreverLoop();
		importGraphics();
	}

	private void importGraphics() {
		segment("RODATA1");
		
		String gfxPath = "includes/graphics/teste";
		
		label("BG_Palette", () -> {
			//; 32 bytes
			incbin(gfxPath+"/snes.palette");
		});
		
		label("Tiles", () -> {
			// 4bpp tileset
			incbin(gfxPath+"/snes.tiles");
		}, "End_Tiles");
		
		label("Tilemap", () -> {
			// $700 bytes
			incbin(gfxPath+"/snes.map");
		});
	}
	
	private void dmaFromPalleteToCGRAM() {
		stz(BusRegisters.CGADD);
		
		String source = "BG_Palette";
		String length = "#256";
		String transferMode = DmaConstants.TRANSFER_MODE_0;
		int channel = 0;
		
		dmaToCgram(source, length, transferMode, channel);
	}
	
	private void dmaFromTilesToVRAM() {
		ldxStx("#$0000", BusRegisters.VMADDL); // set an address in the vram of $0000
		
		String source = "Tiles";
		String length = "#(End_Tiles-Tiles)";
		String transferMode = DmaConstants.TRANSFER_MODE_1;
		int channel = 0;
		
		dmaToVram(source, length, transferMode, channel);
	}
	
	private void dmaFromTilemapToVRAM() {
		ldxStx("#$6000", BusRegisters.VMADDL); // set an address in the vram of $0000
		
		String source = "Tilemap";
		String length = "#2048";
		String transferMode = DmaConstants.TRANSFER_MODE_1;
		int channel = 0;
		
		dmaToVram(source, length, transferMode, channel);
	}

	public static void main(String[] args) {
		new IoriBackground().compileAndRun();
	}
}