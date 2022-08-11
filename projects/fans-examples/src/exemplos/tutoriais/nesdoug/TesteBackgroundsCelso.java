package exemplos.tutoriais.nesdoug;

import fans.core.Ca65Base;
import fans.core.constants.BgModeConstants;
import fans.core.constants.DmaPxConstants;
import fans.core.constants.TmOrTsConstants;
import fans.core.constants.VMainConstants;
import fans.core.enums.BusRegisters;

public class TesteBackgroundsCelso extends Ca65Base {
	
	public void init() {
		a8Bit();
		
		dmaFromPalleteToCGRAM();
		
		ldaSta(VMainConstants.INCREMENT_MODE_BY_1, BusRegisters.VMAIN);
		dmaFromTilesToVRAM();
		dmaFromTilemapToVRAM();
		
		ldaSta(BgModeConstants.MODE1, BusRegisters.BGMODE);
		stz(BusRegisters.BG12NBA);
		setBG1TilemapAddress("#$60");
		ldaSta(TmOrTsConstants.BG1_ON, BusRegisters.TM);
		
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
		
		//segment("RODATA2");
		label("Tilemap", () -> {
			incbin(gfxPath+"/snes.map");
		});
		
		//segment("RODATA3");
		label("Tiles", () -> {
			incbin(gfxPath+"/snes.tiles");
		}, "End_Tiles");
		
		
		
	}
	
	private void dmaFromPalleteToCGRAM() {
		stz(BusRegisters.CGADD);
		
		String source = "BG_Palette";
		//int length = 200;
		//String length = "#$20";
		String length = "#32";
		String transferMode = DmaPxConstants.TRANSFER_MODE_0;
		int channel = 0;
		
		dmaToCgram(source, length, transferMode, channel);
	}
	
	private void dmaFromTilesToVRAM() {
		ldxStx("#$0000", BusRegisters.VMADDL); // set an address in the vram of $0000
		
		String source = "Tiles";
		String length = "#(End_Tiles-Tiles)";
		String transferMode = DmaPxConstants.TRANSFER_MODE_1;
		int channel = 0;
		
		dmaToVram(source, length, transferMode, channel);
	}
	
	private void dmaFromTilemapToVRAM() {
		ldxStx("#$6000", BusRegisters.VMADDL); // set an address in the vram of $0000
		
		String source = "Tilemap";
		String length = "#512";
		//String length = "#(End_Tilemap-Tilemap)";
		String transferMode = DmaPxConstants.TRANSFER_MODE_1;
		int channel = 0;
		
		dmaToVram(source, length, transferMode, channel);
	}

	public static void main(String[] args) {
		new TesteBackgroundsCelso().buildAsmFile();
	}
}