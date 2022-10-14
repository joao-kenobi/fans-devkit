package fans.examples.nesdoug;

import java.util.ArrayList;
import java.util.List;

import fans.core.Ca65Base;
import fans.core.constants.BgModeConstants;
import fans.core.constants.DmaConstants;
import fans.core.constants.ScreenDesignationConstants;
import fans.core.constants.VMainConstants;
import fans.core.enums.BusRegisters;
import fans.core.interfaces.IJoypadReader;

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

	
	protected void before() {
		zeroPageSegment(() -> {
			for (int i = 1; i <= 3; i++) {
				variable("bg"+i+"_x", 1);
				variable("bg"+i+"_y", 1);
			}
			
			for (int i = 2; i <= 6; i++) {
				variable("temp"+i, 2);
			}
			
			variable("sprid", 1);
			variable("map_selected", 1);
			variable("spr_c", 1);
			variable("spr_sz", 1);
			
			// for collision code
			for (int i = 1; i <= 2; i++) {
				variable("obj"+i+"x", 1);
				variable("obj"+i+"w", 1);
				variable("obj"+i+"y", 1);
				variable("obj"+i+"h", 1);
			}
			
			variable("collision", 1);
			
			variable("spr_a", 1); // attributes
			variable("spr_x", 1);
			variable("spr_y", 1);
			variable("spr_x2", 2); // for meta sprite code
			variable("spr_h", 2); // high 2 bits
		});
		
		rawAsm(".include \"../framework/asm/includes/ca65/library.asm\"");
	}
	
	public void init() {
		// for sprites, high table
		rawAsm("SPR_POS_X = 0");
		rawAsm("SPR_NEG_X = 1");
		// if the upper most bit of x is set, it's as if the sprite is
		// off the screen to the left
		// you can scroll a sprite off the left by setting this
		rawAsm("SPR_SIZE_SM = 0");
		rawAsm("SPR_SIZE_LG = 2");
		// actual dimensions of the size set by 2101, oam_size
		
		rawAsm("SPR_PRIOR_2 = $20");
		rawAsm("SPR_PAL_0  = $00");
		
		
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
		infiniteLoop();
		drawSprites();
		setScroll();
		waitNMI();
		padPoll();
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
	
	
	private void infiniteLoop() {
		label("infinite_loop", () -> {
			a8Bit();
			xy16Bit();
			jsr("wait_nmi");
			
			// we are now in v-blank	
			dmaBufferToOam(DmaConstants.TRANSFER_MODE_0, DmaConstants.CHANNEL_0);
			//jsr("DMA_OAM"); // in init.asm
			
			jsr("set_scroll");
			jsr("pad_poll");
			jsr("clear_oam");
			axy16Bit();
			
			readJoypad1();
			
			jmp("infinite_loop");
		});
	}
	
	private void setScroll() {
		label("set_scroll", ()  -> {
			_a8();
			_i16();
			php();
			a8Bit();
			//scroll registers are write twice, low byte then high byte	
			// the high bytes are always 0 in this demo	
			// because our map is 256x256 always (32x32 map and 8x8 tiles)				

			List<BusRegisters[]> registersMatrix = new ArrayList<BusRegisters[]>();
			registersMatrix.add(new BusRegisters[] {BusRegisters.BG1HOFS, BusRegisters.BG1VOFS});
			registersMatrix.add(new BusRegisters[] {BusRegisters.BG2HOFS , BusRegisters.BG2VOFS});
			registersMatrix.add(new BusRegisters[] {BusRegisters.BG3HOFS , BusRegisters.BG3HOFS});

			int bgNumber = 1; 
			for (BusRegisters[] registersArray : registersMatrix) {
				String coordinate = "x";

				for (BusRegisters register : registersArray) {	
					ldaSta("bg"+bgNumber+"_"+coordinate, register);
					stz(register);

					coordinate = "y";
				}

				bgNumber++;
			}
			
			plp();
			rts();
		});
	}
	
	private void drawSprites() {
		label("draw_sprites", ()  -> {
			php();
			a8Bit();
			stz("sprid");
		// spr_x - x (9 bit)
		// spr_y - y
		// spr_c - tile #
		// spr_a - attributes, flip, palette, priority
		// spr_sz = sprite size, 0 or 2
			
			ldaSta("#10", "spr_x", "spr_y");

			lda("map_selected");
			asl("a");
			sta("spr_c");
			ldaSta("#SPR_PAL_0|SPR_PRIOR_2", "spr_a"); 
			ldaSta("#SPR_SIZE_LG", "spr_sz"); // 16x16  
			jsr("OAM_Spr");
			plp();
			rts();
		});
	}
	
	private void readJoypad1() {
		readJoypad1(new IJoypadReader() {
			public void onKeyLeft() {
				_a16();
				_i16();
				php();
				a8Bit();
				lda("map_selected");
				//lda sets the z flag, if map_selected == 0,
				//so we don't need to cmp #0
				bne("@1or2");

				label("@0", () -> {
					// BG1 (map_selected == 0)
					inc("bg1_x");
					bra("@end");						
				});

				label("@1or2", () -> {						
					cmp("#1"); 
					bne("@2");
				});

				label("@1", () -> {
					// BG2 (map_selected == 1)
					inc("bg2_x");
					bra("@end");
				});

				label("@2", () -> {
					// BG3 (map_selected == 2)
					inc("bg3_x");
					bra("@end");
				});


				label("@end", () -> {
					plp();
					rts();
				});
			}
			
			public void onKeyRight() {
				

			}
			public void onKeyUp() {
				
			}
			
			public void onKeyDown() {
				
			}
			
		});
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