package fans.examples.nesdoug;

import fans.core.Ca65Base;
import fans.core.constants.BgModeConstants;
import fans.core.constants.DmaConstants;
import fans.core.constants.TmOrTsConstants;
import fans.core.constants.VMainConstants;
import fans.core.enums.BusRegisters;

public class Part4LayersOrPriority extends Ca65Base {

	private static final String GFXPATH = "includes/graphics/nesdoug/part4";
	
	private static final String BG_PALETTE_LABEL = "bg_palette";
	
	private static final String TILES_LABEL = "tiles";
	private static final String TILES2_LABEL = "tiles2";
	
	private static final String TILEMAP_LABEL = "tilemap";
	private static final String TILEMAP2_LABEL = "tilemap2";
	private static final String TILEMAP3_LABEL = "tilemap3";
	
	public void init() {
		dmaFromPalleteToCGRAM();
		
		ldaSta(VMainConstants.INCREMENT_MODE_BY_1, BusRegisters.VMAIN);
		dmaFromTilesToVRAM();
		dmaFromTilemapToVRAM();
		
		setBGMode(BgModeConstants.MODE1_BG3_ON_TOP);
		setBG1And2CharacterAddress("#$00");
		setBG3And4CharacterAddress("#$03");
		setBG1TilemapAddress("#$60");
		setBG2TilemapAddress("#$68");
		setBG3TilemapAddress("#$70");
		enableMainScreenDesignation(TmOrTsConstants.ALL_ON_SCREEN);
		
		initScreen();
		foreverLoop();
		importGraphics();
	}

	private void importGraphics() {
		segment("RODATA1");
		
		
		labelWithEnd(BG_PALETTE_LABEL, () -> {
			incbin(GFXPATH+"/allBG.pal"); // 256 bytes
		});
		
		labelWithEnd(TILES_LABEL, () -> {
			// 4bpp tileset
			incbin(GFXPATH+"/moon.chr");
		});
		
		labelWithEnd(TILES2_LABEL, () -> {
			// 4bpp tileset
			incbin(GFXPATH+"/spacebar.chr");
		});
		
		labelWithEnd(TILEMAP_LABEL, () -> {
			// $700 bytes
			incbin(GFXPATH+"/moon.map");
		});
		
		labelWithEnd(TILEMAP2_LABEL, () -> {
			// $700 bytes
			incbin(GFXPATH+"/bluebar.map");
		});
		
		labelWithEnd(TILEMAP3_LABEL, () -> {
			// $700 bytes
			incbin(GFXPATH+"/spacebar.map");
		});
	}
	
	private void dmaFromPalleteToCGRAM() {
		stz(BusRegisters.CGADD);		
		dmaToCgram(BG_PALETTE_LABEL, DmaConstants.TRANSFER_MODE_0, DmaConstants.CHANNEL_0);
	}
	
	private void dmaFromTilesToVRAM() {
		ldxStx("#$0000", BusRegisters.VMADDL);
		dmaToVram(TILES_LABEL, DmaConstants.TRANSFER_MODE_1, DmaConstants.CHANNEL_0);
		
		ldxStx("#$3000", BusRegisters.VMADDL);		
		dma(TILES2_LABEL, DmaConstants.CHANNEL_0);
	}
	
	private void dmaFromTilemapToVRAM() {
		String[] tilemapLabels = {TILEMAP_LABEL, TILEMAP2_LABEL, TILEMAP3_LABEL};
		int[] vramAddresses = {6000, 6800, 7000};
		int channel = 0;			
		
		
		for (int i = 0; i < tilemapLabels.length; i++) {
			String source = tilemapLabels[i];
			int vramAddress = vramAddresses[i];
			
			ldxStx("#$"+vramAddress, BusRegisters.VMADDL);
			dma(source, channel);
		}	
	}
	
	public static void main(String[] args) {
		new Part4LayersOrPriority().compileAndRun(); 
	}
}