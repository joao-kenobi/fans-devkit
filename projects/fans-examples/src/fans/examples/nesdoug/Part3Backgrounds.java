package fans.examples.nesdoug;

import fans.core.Ca65Base;
import fans.core.constants.BgModeConstants;
import fans.core.constants.DmaConstants;
import fans.core.constants.TmOrTsConstants;
import fans.core.constants.VMainConstants;
import fans.core.enums.BusRegisters;

public class Part3Backgrounds extends Ca65Base {
	
	private static final String GFXPATH = "includes/graphics/nesdoug/moon";
	private static final String BG_PALETTE_LABEL = "bg_palette";
	private static final String TILES_LABEL = "tiles";
	private static final String TILEMAP_LABEL = "tilemap";

	
	public void init() {
		dmaFromPalleteToCGRAM();
		
		ldaSta(VMainConstants.INCREMENT_MODE_BY_1, BusRegisters.VMAIN);
		dmaFromTilesToVRAM();
		dmaFromTilemapToVRAM();
		
		setBGMode(BgModeConstants.MODE1);
		setBG1And2CharacterAddress("#$00");
		setBG1TilemapAddress("#$60");
		ldaSta(TmOrTsConstants.BG1_ON, BusRegisters.TM); // $01 = only bg 1 is active
		
		initScreen();
		foreverLoop();
		importGraphics();
	}

	private void importGraphics() {
		segment("RODATA1");
		
		labelWithEnd(BG_PALETTE_LABEL, () -> {
			//; 32 bytes
			incbin(GFXPATH+"/moon.pal");
		});
		
		labelWithEnd(TILES_LABEL, () -> {
			// 4bpp tileset
			incbin(GFXPATH+"/moon.chr");
		});
		
		labelWithEnd(TILEMAP_LABEL, () -> {
			// $700 bytes
			incbin(GFXPATH+"/moon.map");
		});
	}
	
	private void dmaFromPalleteToCGRAM() {
		stz(BusRegisters.CGADD);
		dmaToCgram(BG_PALETTE_LABEL, DmaConstants.TRANSFER_MODE_0, DmaConstants.CHANNEL_0);
	}
	
	private void dmaFromTilesToVRAM() {
		ldxStx("#$0000", BusRegisters.VMADDL); // set an address in the vram of $0000
		dmaToVram(TILES_LABEL, DmaConstants.TRANSFER_MODE_1, DmaConstants.CHANNEL_0);
	}
	
	private void dmaFromTilemapToVRAM() {
		ldxStx("#$6000", BusRegisters.VMADDL); // set an address in the vram of $0000
		dmaToVram(TILEMAP_LABEL, DmaConstants.TRANSFER_MODE_1, DmaConstants.CHANNEL_0);
	}

	public static void main(String[] args) {
		new Part3Backgrounds().compileAndRun();
	}
}