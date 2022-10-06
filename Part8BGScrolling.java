package fans.examples.nesdoug;

import fans.core.Ca65Base;
import fans.core.constants.BgModeConstants;
import fans.core.constants.DmaConstants;
import fans.core.constants.ScreenDesignationConstants;
import fans.core.constants.VMainConstants;
import fans.core.enums.BusRegisters;

public class Part8BGScrolling extends Ca65Base {
	
	private static final String GFXPATH = "includes/graphics/nesdoug/part8";
	
	private static final String BG_PALETTE_LABEL = "bg_palette";
	private static final String SPRITE_PALETTE_LABEL = "sprite_palette";
	
	private static final String MOON_TILES_LABEL = "tiles";
	private static final String SPACEBAR_TILES_LABEL = "tiles_2";
	private static final String NUMBERS_TILES_LABEL = "sprite_tiles";
	
	private static final String MOON_TILEMAP_LABEL = "tilemap";
	private static final String BLUEBAR_TILEMAP_LABEL = "tilemap_2";
	private static final String SPACEBAR_TILEMAP_LABEL = "tilemap_3";

	
	public void init() {
		sendPalleteToCGRAM();
		sendTilesToVRAM();
		
		setBGMode(BgModeConstants.MODE1_BG3_ON_TOP);
		setBG1And2CharacterAddress("#$00");
		setBG3And4CharacterAddress("#$03");
		setBG1TilemapAddress("#$60");
		setBG2TilemapAddress("#$68");
		setBG3TilemapAddress("#$70");
		ldaSta("#2", BusRegisters.OBSEL);
		enableMainScreenDesignation(ScreenDesignationConstants.ALL_ON_SCREEN);
		enableNmiAndAutoJoypadRead();
		
		initScreen();
		foreverLoop();
		importGraphics();
	}

	private void importGraphics() {
		segment("RODATA1");
		
		/// === BACKGROUNDS =========================================
		smartLabel(BG_PALETTE_LABEL, () -> {
			// 256 bytes
			incbin(GFXPATH+"/backgrounds/allBG.pal");
		});
		
		smartLabel(MOON_TILES_LABEL, () -> {
			incbin(GFXPATH+"/backgrounds/moon.chr");
		});
		
		smartLabel(MOON_TILEMAP_LABEL, () -> {
			incbin(GFXPATH+"/backgrounds/moon.map");
		});
		
		smartLabel(SPACEBAR_TILES_LABEL, () -> {
			incbin(GFXPATH+"/backgrounds/spacebar.chr");
		});
		
		smartLabel(SPACEBAR_TILEMAP_LABEL, () -> {
			incbin(GFXPATH+"/backgrounds/spacebar.map");
		});
		
		smartLabel(BLUEBAR_TILEMAP_LABEL, () -> {
			incbin(GFXPATH+"/backgrounds/bluebar.map");
		});
		
		// ==========================================================
		
		
		// === SPRITES =============================================
		smartLabel(SPRITE_PALETTE_LABEL, () -> {
			// 256 bytes
			incbin(GFXPATH+"/sprites/Sprites.pal");
		});
		
		smartLabel(NUMBERS_TILES_LABEL, () -> {
			// 256 bytes
			incbin(GFXPATH+"/sprites/Numbers.chr");
		});
		// ========================================================
	}
	
	private void sendPalleteToCGRAM() {
		blockMove(BG_PALETTE_LABEL, PALETTE_BUFFER_VARIABLE);
		a8Bit();
		
		stz(BusRegisters.CGADD);
		dmaToCgram(BG_PALETTE_LABEL, DmaConstants.TRANSFER_MODE_0, DmaConstants.CHANNEL_0);
	}
	
	private void sendTilesToVRAM() {
		ldaSta(VMainConstants.INCREMENT_MODE_BY_1, BusRegisters.VMAIN);
		
		String[] labels = {MOON_TILES_LABEL, SPACEBAR_TILES_LABEL, NUMBERS_TILES_LABEL, MOON_TILEMAP_LABEL, BLUEBAR_TILEMAP_LABEL, SPACEBAR_TILEMAP_LABEL};
		String[] addresses = {"#$0000", "#$3000", "#$4000", "#$6000", "#$6800", "#$7000"};
		
		int i = 0;
		for (String label : labels) {
			ldxStx(addresses[i], BusRegisters.VMADDL);
			dmaToVram(label, DmaConstants.TRANSFER_MODE_1, DmaConstants.CHANNEL_0);
			
			i++;
		}
	}

	public static void main(String[] args) {
		new Part8BGScrolling().compileAndRun();
	}
}