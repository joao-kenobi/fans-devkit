package fans.examples.nesdoug.tests;

import fans.core.Ca65Base;
import fans.core.constants.BgModeConstants;
import fans.core.constants.DmaConstants;
import fans.core.constants.ScreenDesignationConstants;
import fans.core.constants.SpriteConstantes;
import fans.core.constants.VMainConstants;
import fans.core.enums.BusRegisters;

public class TesteSprite extends Ca65Base {
	
	protected void init() {
		//blockMove("bg_palette", "palette_buffer", "#(bg_palette_end - bg_palette)");
		blockMove("bg_palette", "palette_buffer");
		
		
		dmaBufferToCgram(DmaConstants.TRANSFER_MODE_0, 0);
		
		blockMove("sprites", "oam_lo_buffer");
		a8Bit(); // block move will put AXY16. Undo that.
		
		
//		// Set sprite 0 X position
//		ldxStx("#$42", "oam_lo_buffer");
//		
//		// Set sprite 0 Y position
//		ldxStx("#$69", "oam_lo_buffer + 1");
//		// Set sprite 0 to priority 3 and tile 0x01
//		ldxStx("#((%00110000 << 8) | $0001)", "oam_lo_buffer + 2");
//		// Set sprite 0 to be large (16x16)
//		ldaSta("#%00000010", "oam_hi_buffer");
		
		
//		int n = 0;
//		for (int i = 0; i < 128; i++) {
//			// X = xxxxxxxx
//			ldxStx("#"+i, "oam_lo_buffer + "+(n++));
//			// Y = yyyyyyyy
//			ldxStx("#"+i, "oam_lo_buffer + "+(n++));
//			// tile # cccccccc
//			ldxStx("#"+(i+1), "oam_lo_buffer + "+(n++));
//			
//			// priority
//			ldxStx("#$30", "oam_lo_buffer + "+(n++));
//		}
		
		
		// atributes
		//ldxStx("#0", "oam_lo_buffer + 4");
		
//		// Set sprite 0 to be large (16x16)
		//ldxStx("#%00000010", "oam_hi_buffer");
		//ldxStx("#$6A", "oam_hi_buffer");
		
		
//		for (int i = 0; i < 2; i++) {
//			// Set sprite 0 X position
//			ldxStx("#"+i, "oam_lo_buffer");
//			
//			int n = i;
//			
//			// Set sprite 0 Y position
//			ldxStx("#1", "oam_lo_buffer + "+(++n));
//			// Set sprite 0 to priority 3 and tile 0x01
//			ldxStx("#((%00110000 << 8) | $000"+(i+1)+")", "oam_lo_buffer + "+(++n));
//			
////			ldxStx("#"+(i+1), "oam_lo_buffer + "+(++n));
////			ldxStx("#$0003", "oam_lo_buffer + "+(++n));
//		}
//		
//		// Set sprite 0 to be large (16x16)
//		ldxStx("#%00000010", "oam_hi_buffer");
		
		
		// COPY just 1 high table number	
		//#$6A = 01 101010 = flip all the size bits to large
		//			will give us 16x16 tiles
		//			leave the 4th sprite small and in negative x
		ldaSta("#$6A", "oam_hi_buffer");
		
		// DMA from oam_lo_buffer to the OAM RAM
		
//		ldaSta("#$00", BusRegisters.OAMADDH);
		//dma("oam_lo_buffer", "#$"+lowByte(BusRegisters.OAMDATA), 544, DmaPxConstants.TRANSFER_MODE_0, 0);
		
		//jsr("dma_oam"); // in init.asm
		
		stz(BusRegisters.OAMADDL);
		dmaBufferToOam(DmaConstants.TRANSFER_MODE_0, DmaConstants.CHANNEL_0);
		//dmaToOam("oam_lo_buffer", 544, DmaPxConstants.TRANSFER_MODE_0, 0);
		
		
		// DMA from Spr_Tiles to VRAM
		ldaSta(VMainConstants.INCREMENT_MODE_BY_1, BusRegisters.VMAIN);
		
		ldxStx("#$0000", BusRegisters.VMADDL);
		dmaToVram("Spr_Tiles", "#(Spr_Tiles_end - Spr_Tiles)", DmaConstants.TRANSFER_MODE_1, DmaConstants.CHANNEL_0);
		
		//setObjectAndCharacterSize(SpriteConstantes.SIZE_8x8_OR_64x64);
		setObjectAndCharacterSize(SpriteConstantes.SIZE_8x8_OR_16x16);
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
		String gfxPath = "includes/graphics/teste/sprite/iori";
		
		rawAsm("sprite_priority = $20");
//		
		labelWithEnd("sprites", () -> {
			//rawAsm(".byte $28, $38, $10, $20, SPR_PRIOR_2");
			
			
			// 4 bytes per sprite = x, y, tile #, attribute
			//incbin(gfxPath+"/iori_metasprite.bin");
			rawAsm(".byte $02, $03, $03, sprite_priority");
			rawAsm(".byte $0A, $03, $04, sprite_priority");
			rawAsm(".byte $12, $03, $05, sprite_priority");
			rawAsm(".byte $1A, $03, $06, sprite_priority");
			rawAsm(".byte $22, $03, $07, sprite_priority");
			rawAsm(".byte $02, $0B, $13, sprite_priority");
			rawAsm(".byte $0A, $0B, $14, sprite_priority");
			rawAsm(".byte $12, $0B, $15, sprite_priority");
			rawAsm(".byte $1A, $0B, $16, sprite_priority");
			rawAsm(".byte $22, $0B, $17, sprite_priority");
			rawAsm(".byte $2A, $0B, $18, sprite_priority");
			rawAsm(".byte $02, $13, $23, sprite_priority");
			rawAsm(".byte $0A, $13, $24, sprite_priority");
			rawAsm(".byte $12, $13, $25, sprite_priority");
			rawAsm(".byte $1A, $13, $26, sprite_priority");
			rawAsm(".byte $22, $13, $27, sprite_priority");
			rawAsm(".byte $2A, $13, $28, sprite_priority");
			rawAsm(".byte $02, $1B, $33, sprite_priority");
			rawAsm(".byte $0A, $1B, $34, sprite_priority");
			rawAsm(".byte $12, $1B, $35, sprite_priority");
			rawAsm(".byte $1A, $1B, $36, sprite_priority");
			rawAsm(".byte $22, $1B, $37, sprite_priority");
			rawAsm(".byte $2A, $1B, $38, sprite_priority");
			rawAsm(".byte $02, $23, $43, sprite_priority");
			rawAsm(".byte $0A, $23, $44, sprite_priority");
			rawAsm(".byte $12, $23, $45, sprite_priority");
			rawAsm(".byte $1A, $23, $46, sprite_priority");
			rawAsm(".byte $22, $23, $47, sprite_priority");
			rawAsm(".byte $2A, $23, $48, sprite_priority");
			rawAsm(".byte $32, $23, $49, sprite_priority");
			rawAsm(".byte $02, $2B, $53, sprite_priority");
			rawAsm(".byte $0A, $2B, $54, sprite_priority");
			rawAsm(".byte $12, $2B, $55, sprite_priority");
			rawAsm(".byte $1A, $2B, $56, sprite_priority");
			rawAsm(".byte $22, $2B, $57, sprite_priority");
			rawAsm(".byte $2A, $2B, $58, sprite_priority");
			rawAsm(".byte $32, $2B, $59, sprite_priority");
			rawAsm(".byte $3A, $2B, $58, sprite_priority");
			rawAsm(".byte $80 ;end of data       ");
		});
		
		segment("RODATA1");
		
		labelWithEnd("bg_palette", () -> {
			incbin(gfxPath+"/default.palette");
			incbin(gfxPath+"/iori.palette");	
		});
		
		labelWithEnd("Spr_Tiles", () -> {
			incbin(gfxPath+"/iori.sprite");
		});
	}
	
	public static void main(String[] args) {
		new TesteSprite().compileAndRun();
	}
}
