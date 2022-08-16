package fans.examples.nesdoug;

import fans.core.Ca65Base;
import fans.core.constants.BgModeConstants;
import fans.core.constants.DmaPxConstants;
import fans.core.constants.TmOrTsConstants;
import fans.core.constants.VMainConstants;
import fans.core.enums.BusRegisters;

public class Part4LayersOrPriority extends Ca65Base {

	public void init() {
		a8Bit();
		
		dmaFromPalleteToCGRAM();
		
		ldaSta(VMainConstants.INCREMENT_MODE_BY_1, BusRegisters.VMAIN);
		dmaFromTilesToVRAM();
		dmaFromTilemapToVRAM();
		
		ldaSta(BgModeConstants.MODE1_BG3_ON_TOP, BusRegisters.BGMODE);
		
		stz(BusRegisters.BG12NBA); 
		ldaSta("#$03", BusRegisters.BG34NBA);
		ldaSta("#$60", BusRegisters.BG1SC);
		ldaSta("#$68", BusRegisters.BG2SC);
		ldaSta("#$70", BusRegisters.BG3SC);
		
		ldaSta(TmOrTsConstants.ALL_ON_SCREEN, BusRegisters.TM);
		
		initScreen();
		foreverLoop();
		importGraphics();
	}

	private void importGraphics() {
		segment("RODATA1");
		
		String gfxPath = "includes/graphics/nesdoug/part4";
		
		label("BG_Palette", () -> {
			incbin(gfxPath+"/allBG.pal"); // 256 bytes
		});
		
		label("Tiles", () -> {
			// 4bpp tileset
			incbin(gfxPath+"/moon.chr");
		}, "End_Tiles");
		
		label("Tiles2", () -> {
			// 4bpp tileset
			incbin(gfxPath+"/spacebar.chr");
		}, "End_Tiles2");
		
		label("Tilemap", () -> {
			// $700 bytes
			incbin(gfxPath+"/moon.map");
		});
		
		label("Tilemap2", () -> {
			// $700 bytes
			incbin(gfxPath+"/bluebar.map");
		});
		
		label("Tilemap3", () -> {
			// $700 bytes
			incbin(gfxPath+"/spacebar.map");
		});
	}
	
	private void dmaFromPalleteToCGRAM() {
		stz(BusRegisters.CGADD);
		
		String source = "BG_Palette";
		String length = "#256";
		String transferMode = DmaPxConstants.TRANSFER_MODE_0;
		int channel = 0;
		
		dmaToCgram(source, length, transferMode, channel);
	}
	
	private void dmaFromTilesToVRAM() {
		
		// === TILES 1 ================================================================
		ldxStx("#$0000", BusRegisters.VMADDL); // set an address in the vram of $0000
		
		String source = "Tiles";
		String length = "#(End_Tiles-Tiles)";
		String transferMode = DmaPxConstants.TRANSFER_MODE_1;
		int channel = 0;
		
		dmaToVram(source, length, transferMode, channel);
		// ===========================================================================
		
		// === TILES 2 ================================================================
		ldxStx("#$3000", BusRegisters.VMADDL); // set an address in the vram of $3000
				
		// 4300 and 4301 still hold the correct values for transfers to vram
		// and don't need to be rewritten here		
		source = "Tiles2";
		length = "#(End_Tiles2-Tiles2)";
		
		dma(source, length, channel);
		// ===========================================================================
	}
	
	private void dmaFromTilemapToVRAM() {
		String[] tilemapLabels = {"Tilemap", "Tilemap2", "Tilemap3"};
		int[] vramAddresses = {6000, 6800, 7000};
		String length = "#$700";
		int channel = 0;			
		
		
		for (int i = 0; i < tilemapLabels.length; i++) {
			String source = tilemapLabels[i];
			int vramAddress = vramAddresses[i];
			
			ldxStx("#$"+vramAddress, BusRegisters.VMADDL);
			dma(source, length, channel);
		}	
	}
	
	public static void main(String[] args) {
		new Part4LayersOrPriority().buildAsmFile(); 
	}
}