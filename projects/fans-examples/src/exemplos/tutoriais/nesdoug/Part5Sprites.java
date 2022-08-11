package exemplos.tutoriais.nesdoug;

import fans.core.Ca65Base;
import fans.core.constants.BgModeConstants;
import fans.core.constants.DmaPxConstants;
import fans.core.constants.TmOrTsConstants;
import fans.core.constants.VMainConstants;
import fans.core.enums.BusRegisters;

public class Part5Sprites extends Ca65Base {
	
	protected void init() {
		blockMove(288, "BG_Palette", "PAL_BUFFER"); // COPY PALETTES to PAL_BUFFER
		
		//a8Bit();
		dmaToCgram("PAL_BUFFER", 288, DmaPxConstants.TRANSFER_MODE_0, 0); // DMA from PAL_BUFFER to CGRAM
		//jsr("DMA_Palette"); // in init.asm
		
		blockMove(12, "Sprites", "oam_lo_buffer"); // COPY sprites to sprite buffer
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
		
		
		// DMA from Spr_Tiles to VRAM
		ldaSta(VMainConstants.INCREMENT_MODE_BY_1, BusRegisters.VMAIN);
		
		ldxStx("#$4000", BusRegisters.VMADDL);
		dmaToVram("Spr_Tiles", "#(End_Spr_Tiles-Spr_Tiles)", DmaPxConstants.TRANSFER_MODE_1, 0);
		
		ldaSta("#$02", BusRegisters.OBSEL);
		ldaSta(BgModeConstants.MODE1, BusRegisters.BGMODE);
		ldaSta(TmOrTsConstants.SPRITES_ON, BusRegisters.TM);
		
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
		rawAsm("SPR_PRIOR_2 = $20");
		
		String gfxPath = "includes/graphics/nesdoug/part5";
		
		label("Sprites", () -> {
			// 4 bytes per sprite = x, y, tile #, attribute
			rawAsm(".byte $80, $80, $00, SPR_PRIOR_2");	
			rawAsm(".byte $80, $90, $20, SPR_PRIOR_2");	
			rawAsm(".byte $7c, $90, $22, SPR_PRIOR_2");	
		});
		
		segment("RODATA1");
		
		label("BG_Palette", () -> {
			incbin(gfxPath+"/default.pal"); // 256 bytes
			incbin(gfxPath+"/sprite.pal"); // is 32 bytes, 256+32=288	
		});
		
		label("Spr_Tiles", () -> {
			incbin(gfxPath+"/sprite.chr");
		}, "End_Spr_Tiles");
	}
	
	public static void main(String[] args) {
		new Part5Sprites().buildAsmFile();
	}
}
