package fans.examples.nesdoug;

import fans.core.Ca65Base;
import fans.core.constants.BgModeConstants;
import fans.core.constants.DmaConstants;
import fans.core.constants.ScreenDesignationConstants;
import fans.core.constants.VMainConstants;
import fans.core.enums.BusRegisters;

public class IoriSprites extends Ca65Base {
	
	protected void before() {
		
	}
	
	protected void init() {
		blockMove("palette", "palette_buffer", 32); // COPY PALETTES to palette_buffer
		
		//a8Bit();
		dmaToCgram("palette_buffer", 32, DmaConstants.TRANSFER_MODE_0, 0); // DMA from palette_buffer to CGRAM
		//jsr("DMA_Palette"); // in init.asm
		
		blockMove("sprites", "oam_lo_buffer", 422); // COPY sprites to sprite buffer
		a8Bit(); // block move will put AXY16. Undo that.
		
		
		// COPY just 1 high table number	
		//#$6A = 01 101010 = flip all the size bits to large
		//			will give us 16x16 tiles
		//			leave the 4th sprite small and in negative x
		ldaSta("#$6A", "oam_hi_buffer");
		
		// DMA from oam_lo_buffer to the OAM RAM
//		stz(BusRegisters.OAMADDL);
//		dma("oam_lo_buffer", "#$"+lowByte(BusRegisters.OAMDATA), 544, 0, 0);
		jsr("DMA_OAM"); // in init.asm
		
		
		// DMA from tiles to VRAM
		ldaSta(VMainConstants.INCREMENT_MODE_BY_1, BusRegisters.VMAIN);
		
		ldxStx("#$4000", BusRegisters.VMADDL);
		dmaToVram("tiles", "#(end_tiles-tiles)", DmaConstants.TRANSFER_MODE_1, 0);
		
		ldaSta("#$02", BusRegisters.OBSEL);
		ldaSta(BgModeConstants.MODE1, BusRegisters.BGMODE);
		ldaSta(ScreenDesignationConstants.SPRITES_ON, BusRegisters.TM);
		
		initScreen();
		
		label("Infinite_Loop", () -> {
			a8Bit();
			xy16Bit();
			jsr("Wait_NMI");
			jmp("Infinite_Loop");
		});
		
		
		label("Wait_NMI", () -> {
			rawAsm(".a8");
			rawAsm(".i16");
			
			lda("in_nmi");
			
			label("@check_again", () -> {
				rawAsm("WAI");
				cmp("in_nmi");
				beq("@check_again");
				rts();
			});
		});
		
//		foreverLoop();
		
		
		importGraphics();
	}

	private void importGraphics() {
		//rawAsm("SPR_PRIOR_2 = $20");
		
		String gfxPath = "includes/graphics/teste/sprite";
		
//		label("sprites", () -> {
//			// 4 bytes per sprite = x, y, tile #, attribute
//			rawAsm(".byte $80, $80, $00, SPR_PRIOR_2");	
//			rawAsm(".byte $80, $90, $20, SPR_PRIOR_2");	
//			rawAsm(".byte $7c, $90, $22, SPR_PRIOR_2");	
//		});
		
		label("sprites", () -> {
			incbin(gfxPath+"/sprite.palette"); // 256 bytes
		});
		
		segment("RODATA1");
		
		label("palette", () -> {
			incbin(gfxPath+"/sprite.palette");	
		});
		
		label("tiles", () -> {
			incbin(gfxPath+"/sprite.tiles");
		}, "end_tiles");
	}
	
	public static void main(String[] args) {
		new IoriSprites().compileAndRun();
	}
}
