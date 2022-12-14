package fans.examples.nesdoug;

import fans.core.Ca65Base;
import fans.core.constants.BgModeConstants;
import fans.core.constants.DmaConstants;
import fans.core.constants.ScreenDesignationConstants;
import fans.core.constants.VMainConstants;
import fans.core.enums.BusRegisters;
import fans.core.interfaces.IJoypadReader;

public class Part6ControllersAndNMI extends Ca65Base {
	
	private static final String GFXPATH = "includes/graphics/nesdoug/part6";
	
	private static final String BG_PALETTE_LABEL = "bg_palette";
	private static final String SPRITES_LABEL = "sprites";
	private static final String SPRITES_TILES_LABEL = "sprites_tiles";
	
	
	private static final String[] HORIZONTAL_VALUES = {OAM_LO_BUFFER_VARIABLE, OAM_LO_BUFFER_VARIABLE+"+4", OAM_LO_BUFFER_VARIABLE+"+8"};
	private static final String[] VERTICAL_VALUES = {OAM_LO_BUFFER_VARIABLE+"+1", OAM_LO_BUFFER_VARIABLE+"+5", OAM_LO_BUFFER_VARIABLE+"+9"};
	
	protected void init() {
		blockMove(BG_PALETTE_LABEL, PALETTE_BUFFER_VARIABLE);
		a8Bit();
		
		dmaBufferToCgram(DmaConstants.TRANSFER_MODE_0, DmaConstants.CHANNEL_0);
		
		blockMove(SPRITES_LABEL, OAM_LO_BUFFER_VARIABLE);
		a8Bit();
		
		// COPY just 1 high table number	
		//#$6A = 01 101010 = flip all the size bits to large
		//			will give us 16x16 tiles
		//			leave the 4th sprite small and in negative x
		ldaSta("#$6A", OAM_HI_BUFFER_VARIABLE);
		
		// DMA from oam_lo_buffer to the OAM RAM
		stz(BusRegisters.OAMADDL);
		dmaBufferToOam(DmaConstants.TRANSFER_MODE_0, DmaConstants.CHANNEL_0);
		
		// === DMA from sprite_tiles to VRAM ========================================================
		ldaSta(VMainConstants.INCREMENT_MODE_BY_1, BusRegisters.VMAIN);
		
		ldxStx("#$4000", BusRegisters.VMADDL);
		dmaToVram(SPRITES_TILES_LABEL, DmaConstants.TRANSFER_MODE_1, DmaConstants.CHANNEL_0);
		// ======================================================================================
		
		setObjectAndCharacterSize("#$02");
		setBGMode(BgModeConstants.MODE1);
		enableMainScreenDesignation(ScreenDesignationConstants.SPRITES_ON);
		enableNmiAndAutoJoypadRead();
		
		initScreen();
		infiniteLoop();
		waitNMI();
		padPoll();
		importGraphics();
	}

	private void infiniteLoop() {
		label("infinite_loop", () -> {
			a8Bit();
			xy16Bit();
			jsr("wait_nmi");
			
			// we are now in v-blank	
			dmaBufferToOam(DmaConstants.TRANSFER_MODE_0, DmaConstants.CHANNEL_0);
			//jsr("DMA_OAM"); // in init.asm
			jsr("pad_poll");
			axy16Bit();
			
			readJoypad1();
			
			jmp("infinite_loop");
		});
	}

	private void readJoypad1() {
		readJoypad1(new IJoypadReader() {
			public void onKeyLeft() {
				a8Bit();
				
				for (String value : HORIZONTAL_VALUES) {
					dec(value);
				}
				
				a16Bit();
			}
			
			public void onKeyRight() {
				a8Bit();
				
				for (String value : HORIZONTAL_VALUES) {
					inc(value);
				}
				
				a16Bit();

			}
			public void onKeyUp() {
				a8Bit();
				
				for (String value : VERTICAL_VALUES) {
					dec(value);
				}
				
				a16Bit();
			}
			
			public void onKeyDown() {
				a8Bit();
				
				for (String value : VERTICAL_VALUES) {
					inc(value);
				}
				
				a16Bit();
			}
			
		});
	}

	private void importGraphics() {
		final String SPR_PRIOR_2 = "$20";
		
		
		smartLabel(SPRITES_LABEL, () -> {
			// 4 bytes per sprite = x, y, tile #, attribute
			rawAsm(".byte $80, $80, $00, "+SPR_PRIOR_2);	
			rawAsm(".byte $80, $90, $20, "+SPR_PRIOR_2);	
			rawAsm(".byte $7c, $90, $22, "+SPR_PRIOR_2);	
		});
		
		segment("RODATA1", () -> {			
			smartLabel(BG_PALETTE_LABEL, () -> {
				incbin(GFXPATH+"/default.pal"); // 256 bytes
				incbin(GFXPATH+"/sprite.pal"); // is 32 bytes, 256+32=288	
			});
			
			smartLabel(SPRITES_TILES_LABEL, () -> {
				incbin(GFXPATH+"/sprite.chr");
			});
		});
	}
	
	public static void main(String[] args) {
		new Part6ControllersAndNMI().compileAndRun();
	}
}