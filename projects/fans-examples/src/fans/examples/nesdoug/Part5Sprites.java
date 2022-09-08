package fans.examples.nesdoug;

import fans.core.Ca65Base;
import fans.core.constants.BgModeConstants;
import fans.core.constants.DmaConstants;
import fans.core.constants.ScreenDesignationConstants;
import fans.core.constants.VMainConstants;
import fans.core.enums.BusRegisters;

public class Part5Sprites extends Ca65Base {
	
	protected void init() {
		//blockMove("bg_palette", "palette_buffer", "#(bg_palette_end - bg_palette)");
		blockMove("bg_palette", "palette_buffer", 288);
		
		dmaBufferToCgram(DmaConstants.TRANSFER_MODE_0, 0);
		
		blockMove("sprites", "oam_lo_buffer");
		a8Bit(); // block move will put AXY16. Undo that.
		
		
		// COPY just 1 high table number	
		//#$6A = 01 101010 = flip all the size bits to large
		//			will give us 16x16 tiles
		//			leave the 4th sprite small and in negative x
		//ldaSta("#$6A", "oam_hi_buffer");
		ldaSta("#$6A", "oam_hi_buffer");
		
		// DMA from oam_lo_buffer to the OAM RAM
		//stz(BusRegisters.OAMADDL);
		//dma("oam_lo_buffer", "#$"+lowByte(BusRegisters.OAMDATA), 544, DmaPxConstants.TRANSFER_MODE_0, 0);
		
		//jsr("dma_oam"); // in init.asm
		dmaBufferToOam(DmaConstants.TRANSFER_MODE_0, DmaConstants.CHANNEL_0);
		//dmaToOam("oam_lo_buffer", 544, DmaPxConstants.TRANSFER_MODE_0, 0);
		
		
		// DMA from Spr_Tiles to VRAM
		ldaSta(VMainConstants.INCREMENT_MODE_BY_1, BusRegisters.VMAIN);
		
		ldxStx("#$4000", BusRegisters.VMADDL);
		dmaToVram("Spr_Tiles", "#(Spr_Tiles_end - Spr_Tiles)", DmaConstants.TRANSFER_MODE_1, DmaConstants.CHANNEL_0);
		
		
		// $2101 sssnn-bb
		// sss = sprite sizes, 000 = 8x8 and 16x16 sprites
		// nn = displacement for the 2nd set of sprite tiles, 00 = normal
		// bb = where are the sprite tiles, in steps of $2000
		// that upper bit is useless, as usual, so I marked it with a dash -
		//setObjectAndCharacterSize(SpriteConstantes.SIZE_8x8_OR_16x16);
		setObjectAndCharacterSize("#2");
		//setObjectAndCharacterSize("#%00000010");
		setBGMode(BgModeConstants.MODE1);
		enableMainScreenDesignation(ScreenDesignationConstants.SPRITES_ON);
		
		initScreen();
		
		label("Infinite_Loop", () -> {
			a8Bit();
			xy16Bit();
			jsr("wait_nmi");
			jmp("Infinite_Loop");
		});
		
		
		label("wait_nmi", () -> {
			rawAsm(".a8");
			rawAsm(".i16");
			
			lda("in_nmi");
			
			label("@check_again", () -> {
				wai();
				cmp("in_nmi");
				beq("@check_again");
				rts();
			});
		});
		
//		foreverLoop();
		
		
		importGraphics();
	}

	private void importGraphics() {
		rawAsm("SPR_PRIOR_2 = $20");
		
		String gfxPath = "includes/graphics/nesdoug/part5";
		
		labelWithEnd("sprites", () -> {
			// 4 bytes per sprite = x, y, tile #, attribute
			rawAsm(".byte $80, $80, $00, SPR_PRIOR_2");	
			rawAsm(".byte $80, $90, $20, SPR_PRIOR_2");	
			rawAsm(".byte $7c, $90, $22, SPR_PRIOR_2");	
		});
		
		segment("RODATA1");
		
		labelWithEnd("bg_palette", () -> {
			incbin(gfxPath+"/default.pal"); // 256 bytes
			incbin(gfxPath+"/sprite.pal"); // is 32 bytes, 256+32=288	
		});
		
		labelWithEnd("Spr_Tiles", () -> {
			incbin(gfxPath+"/sprite.chr");
		});
	}
	
	public static void main(String[] args) {
		new Part5Sprites().compileAndRun();
	}
}
