package fans.examples.nesdoug;

import fans.core.Ca65Base;
import fans.core.constants.BgModeConstants;
import fans.core.constants.DmaConstants;
import fans.core.constants.TmOrTsConstants;
import fans.core.constants.VMainConstants;
import fans.core.enums.BusRegisters;
import fans.core.enums.CpuRegisters;
import fans.core.interfaces.IJoypadReader;

public class Part6ControllersAndNMI extends Ca65Base {
	
	protected void before() {
		segment("ZEROPAGE", () -> {			
			rawAsm("temp1: .res 2");
			rawAsm("pad1: .res 2");
			rawAsm("pad1_new: .res 2");
			rawAsm("pad2: .res 2");
			rawAsm("pad2_new: .res 2");
		});
	}
	
	protected void init() {
		blockMove("bg_palette", "palette_buffer"); // COPY PALETTES to PAL_BUFFER
		
		a8Bit();
		dmaToCgram("palette_buffer", DmaConstants.TRANSFER_MODE_0, 0); // DMA from PAL_BUFFER to CGRAM
		
		blockMove("sprites", "oam_lo_buffer"); // COPY sprites to sprite buffer
		a8Bit();
		
		// COPY just 1 high table number	
		//#$6A = 01 101010 = flip all the size bits to large
		//			will give us 16x16 tiles
		//			leave the 4th sprite small and in negative x
		ldaSta("#$6A", "oam_hi_buffer");
		
		// DMA from oam_lo_buffer to the OAM RAM
		stz(BusRegisters.OAMADDL);
		dmaToOam("oam_lo_buffer", "#(oam_buffer_end - oam_lo_buffer)", DmaConstants.TRANSFER_MODE_0, 0);
		
		// === DMA from sprite_tiles to VRAM ========================================================
		ldaSta(VMainConstants.INCREMENT_MODE_BY_1, BusRegisters.VMAIN);
		
		ldxStx("#$4000", BusRegisters.VMADDL);
		dmaToVram("sprite_tiles", DmaConstants.TRANSFER_MODE_1, 0);
		// ======================================================================================
		
		ldaSta("#$02", BusRegisters.OBSEL);
		setBGMode(BgModeConstants.MODE1);
		ldaSta(TmOrTsConstants.SPRITES_ON, BusRegisters.TM);
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
			dmaToOam("oam_lo_buffer", 544, DmaConstants.TRANSFER_MODE_0, 0);
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
				
				String[] variables = {"oam_lo_buffer", "oam_lo_buffer+4", "oam_lo_buffer+8"};
				
				for (String variable : variables) {
					dec(variable);
				}
				
				a16Bit();
			}
			
			public void onKeyRight() {
				a8Bit();
				
				String[] variables = {"oam_lo_buffer", "oam_lo_buffer+4", "oam_lo_buffer+8"};
				
				for (String variable : variables) {
					inc(variable);
				}
				
				a16Bit();

			}
			public void onKeyUp() {
				a8Bit();
				
				String[] variables = {"oam_lo_buffer+1", "oam_lo_buffer+5", "oam_lo_buffer+9"};
				
				for (String variable : variables) {
					dec(variable);
				}
				
				a16Bit();
			}
			
			public void onKeyDown() {
				a8Bit();
				
				String[] variables = {"oam_lo_buffer+1", "oam_lo_buffer+5", "oam_lo_buffer+9"};
				
				for (String variable : variables) {
					inc(variable);
				}
				
				a16Bit();
			}
			
		});
	}
	
	private void waitNMI() {
		label("wait_nmi", () -> {
			rawAsm(".a8");
			rawAsm(".i16");
			lda("in_nmi");
			
			checkAgain();
		});
	}
	
	private void checkAgain() {
		label("@check_again", () -> {
			rawAsm("WAI");
			cmp("in_nmi");
			beq("@check_again");
			rts();
		});
	}
	
	private void padPoll() {
		label("pad_poll", () -> {			
			rawAsm(".a8");
			rawAsm(".i16");
			// reads both controllers to pad1, pad1_new, pad2, pad2_new
			// auto controller reads done, call this once per main loop
			// copies the current controller reads to these variables
			// pad1, pad1_new, pad2, pad2_new (all 16 bit)
			php();
			a8Bit();
			_wait();
				
			a16Bit();
			ldaSta("pad1", "temp1"); // save last frame
			ldaSta(CpuRegisters.JOY1L, "pad1"); // controller 1
			
			eor("temp1");
			and("pad1");
			sta("pad1_new");
			
			ldaSta("pad2", "temp1"); // save last frame
			
			ldaSta(CpuRegisters.JOY2L, "pad2"); // controller 2
			
			eor("temp1");
			and("pad2");
			sta("pad2_new");
			plp();
			rts();
		});
		
	}
	
	private void _wait() {
		label("@wait", () -> {
			// wait till auto-controller reads are done
			lda(CpuRegisters.HVBJOY);
			lsr("a");
			bcs("@wait");
		});
	}

	private void importGraphics() {
		rawAsm("SPR_PRIOR_2 = $20");
		
		String gfxPath = "includes/graphics/nesdoug/part6";
		
		label("sprites", () -> {
			// 4 bytes per sprite = x, y, tile #, attribute
			rawAsm(".byte $80, $80, $00, SPR_PRIOR_2");	
			rawAsm(".byte $80, $90, $20, SPR_PRIOR_2");	
			rawAsm(".byte $7c, $90, $22, SPR_PRIOR_2");	
		}, "sprites_end");
		
		segment("RODATA1");
		
		label("bg_palette", () -> {
			incbin(gfxPath+"/default.pal"); // 256 bytes
			incbin(gfxPath+"/sprite.pal"); // is 32 bytes, 256+32=288	
		}, "bg_palette_end");
		
		label("sprite_tiles", () -> {
			incbin(gfxPath+"/sprite.chr");
		}, "sprite_tiles_end");
	}
	
	public static void main(String[] args) {
		new Part6ControllersAndNMI().compileAndRun();
	}
}
