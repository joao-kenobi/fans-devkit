package fans.examples.nesdoug;

import fans.core.Ca65Base;
import fans.core.constants.BgModeConstants;
import fans.core.constants.DmaPxConstants;
import fans.core.constants.NmiTIMenConstants;
import fans.core.constants.TmOrTsConstants;
import fans.core.constants.VMainConstants;
import fans.core.enums.BusRegisters;
import fans.core.enums.CpuRegisters;
import fans.core.interfaces.IJoypadReader;

public class Part7PongSpriteCollisions extends Ca65Base {

	protected void before() {
		
		segment("ZEROPAGE", () -> {
			rawAsm("in_nmi: .res 2");
			
			for (int i = 1; i <= 6; i++) {				
				rawAsm("temp"+i+": .res 2");
			}
			
			// for sprite code
			rawAsm("sprid: .res 1");
			rawAsm("spr_x: .res 2 ; 9 bit");
			rawAsm("spr_y: .res 1");
			rawAsm("spr_c: .res 1 ; tile #");
			rawAsm("spr_a: .res 1 ; attributes");
			rawAsm("spr_sz:	.res 1 ; sprite size, 0 or 2");
			rawAsm("spr_h: .res 1 ; high 2 bits");
			rawAsm("spr_x2:	.res 2 ; for meta sprite code");
			
			// for collision code
			rawAsm("obj1x: .res 1");
			rawAsm("obj1w: .res 1");
			rawAsm("obj1y: .res 1");
			rawAsm("obj1h: .res 1");
			rawAsm("obj2x: .res 1");
			rawAsm("obj2w: .res 1");
			rawAsm("obj2y: .res 1");
			rawAsm("obj2h: .res 1");
			rawAsm("collision: .res 1");
			
			rawAsm("pad1: .res 2");
			rawAsm("pad1_new: .res 2");
			rawAsm("pad2: .res 2");
			rawAsm("pad2_new: .res 2");
			
			rawAsm("ball_active: .res 1");
			rawAsm("ball_x: .res 1");
			rawAsm("ball_y: .res 1");
			rawAsm("ball_x_speed: .res 1");
			rawAsm("ball_y_speed: .res 1");
			
			rawAsm("paddle1_x: .res 1");
			rawAsm("paddle1_y: .res 1");
			
			rawAsm("paddle2_x: .res 1");
			rawAsm("paddle2_y: .res 1");
			
			rawAsm("points_L: .res 1");
			rawAsm("points_R: .res 1");
			
			rawAsm("game_pause: .res 1");
			rawAsm("frame_count: .res 1");
		});
		
		segment("BSS", () -> {			
			rawAsm("palette_buffer: .res 512");
			rawAsm("palette_buffer_end:");

			rawAsm("oam_lo_buffer: .res 512 ;low table ");
			rawAsm("oam_hi_buffer: .res 32 ;high table ");
			rawAsm("oam_buffer_end:");
		});

	}
	
	protected void init() {
		blockMove("BG_Palette", "palette_buffer", 512);
		a8Bit();
		
		dmaToCgram("palette_buffer", "#(palette_buffer_end - palette_buffer)", DmaPxConstants.TRANSFER_MODE_0, 0);
		

		ldaSta(VMainConstants.INCREMENT_MODE_BY_1, BusRegisters.VMAIN);
		
		ldxStx("#$0000", BusRegisters.VMADDL);
		dmaToVram("BG_Tiles", "#(End_BG_Tiles-BG_Tiles)", DmaPxConstants.TRANSFER_MODE_1, 0);
		
		ldxStx("#$3000", BusRegisters.VMADDL);
		dmaToVram("HUD_Tiles", "#(End_HUD_Tiles-HUD_Tiles)", DmaPxConstants.TRANSFER_MODE_1, 0);
		
		ldxStx("#$4000", BusRegisters.VMADDL);
		dmaToVram("Spr_Tiles", "#(End_Spr_Tiles-Spr_Tiles)", DmaPxConstants.TRANSFER_MODE_1, 0);
		
		ldxStx("#$7000", BusRegisters.VMADDL);
		dmaToVram("Map1", "#$700", DmaPxConstants.TRANSFER_MODE_1, 0);
		
		ldxStx("#$7000", BusRegisters.VMADDL);
		dmaToVram("Map3", "#$700", DmaPxConstants.TRANSFER_MODE_1, 0);
		
		setBGMode(BgModeConstants.MODE1_BG3_ON_TOP);
		
		stz(BusRegisters.BG12NBA);
		ldaSta("#$03", BusRegisters.BG34NBA);
		ldaSta("#$60", BusRegisters.BG1SC);
		ldaSta("#$68", BusRegisters.BG2SC);
		ldaSta("#$70", BusRegisters.BG3SC);
		
		ldaSta("#2", BusRegisters.OBSEL);
		ldaSta(TmOrTsConstants.ALL_ON_SCREEN, BusRegisters.TM);
		ldaSta(NmiTIMenConstants.ENABLE_NMI_AND_AUTO_JOYPAD_READ, CpuRegisters.NMITIMEN);
		initScreen();
		
		// set initial values
		stz("ball_active");
		
		ldaSta("#$10", "paddle1_x");
		ldaSta("#$e8", "paddle2_x");
		
		ldaSta("#$70", "paddle1_y", "paddle2_y");
		
		label("Infinite_Loop", () -> {
			a8Bit();
			xy16Bit();
			jsr("Wait_NMI");
			jsr("DMA_OAM");
			jsr("Print_Score");
			jsr("Pad_Poll");
			jsr("Clear_OAM");
			
			inc("frame_count");
			axy16Bit();
			readJoypad1();
			
			label("@ok", () -> {
				lda("points_R");
				cmp("#9");
				bcc("@ok2");
				jsr("Reset_Score");
			});
			
			label("@ok2", () -> {
				a8Bit();
				ldaSta("#1", "ball_active");
				
				ldaSta("#$74", "ball_x", "ball_y");
				lda("frame_count");
				and("#1");
				bne("@sp_x");
				lda("#$ff"); // -1
			});
			
			label("@sp_y", () -> {
				sta("ball_y_speed");
			});
			
			label("@skip_start", () -> {
				// move ball
				a8Bit();
				lda("ball_active");
				bne("@ball_active");
				jmp("ball_not_active");
			});
			
			label("@ball_active", () -> {
				String[] ballVariables = {"ball_x", "ball_y"};
				
				for (String variable : ballVariables) {
					lda(variable);
					clc();
					adc(variable+"_speed");
					sta(variable);					
				}
				
				// bounce off ceilings	
				cmp("#$20");
				bcs("@above20");
				ldaSta("#$20", "ball_y");
				ldaSta("#1", "ball_y_speed");
				jmp("@ball_done");
			});
			
			label("@above20", () -> {
				// bounce off floor
				ldaCmp("ball_y", "#$c7");
				bcc("@ball_done");
				ldaSta("#$c7", "ball_y");
				ldaSta("#$ff", "ball_y_speed");  // -1
			});
			
			
			label("@ball_done", () -> {
				// check collision left
				a8Bit();
				ldaSta("paddle1_x", "obj1x");
				ldaSta("#PADDLE_W", "obj1w");
				ldaSta("paddle1_y", "obj1y");
				ldaSta("#PADDLE_H", "obj1h");
				ldaSta("ball_x", "obj2x");
				ldaSta("#BALL_SIZE", "obj2w");
				ldaSta("ball_y", "obj2y");
				ldaSta("#BALL_SIZE", "obj2h");
				jsr("Check_Collision");
				a8Bit();
				ldaBeq("collision", "@no_collision"); // 1 or 0

				// is left paddle more left than ball?
				ldaCmp("paddle1_x", "ball_x");
				bcs("@no_collision");
				// make ball go right	
				ldaSta("#1", "ball_x_speed");
			});
			
			label("@no_collision", () -> {
				// check collision right
				a8Bit();
				ldaSta("paddle2_x", "obj1x");
				ldaSta("#PADDLE_W", "obj1w");
				ldaSta("paddle2_y", "obj1y");
				ldaSta("#PADDLE_H", "obj1h");
			    // skip ball, still loaded

				jsr("Check_Collision");
				a8Bit();
				ldaBeq("collision", "@past_collisions"); // 1 or 0
				// is left paddle more right than ball?
				ldaCmp("paddle2_x", "ball_x");
				beq("@past_collisions");
				bcc("@past_collisions");
				// make ball go left	
				ldaSta("#$ff", "ball_x_speed"); // -1
				
			});
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
				ldaCmp("paddle1_y", "#$20"); // max up
				beq("@not_up"); // too far up
				bcc("@not_up");
				
				String[] paddles = {"paddle1_y", "paddle2_y"};
				
				for (String paddle : paddles) {
					dec(paddle, 2);
				}
			}
			
			public void onKeyDown() {
				a8Bit();
				lda("paddle1_y");				
				cmp("#$9E"); // max down
				bcs("@not_down"); // too far down
				
				String[] paddles = {"paddle1_y", "paddle2_y"};
				
				for (String paddle : paddles) {
					inc(paddle, 2);
				}
			}
			
			
			public void onKeyNotDown() {
				a8Bit();
				lda("ball_active");
				bne("@skip_start");
				
				a16Bit();
				lda("pad1");
				and("#KEY_START");
				beq("@skip_start");
				
				a8Bit();
				lda("points_L");
				cmp("#9");
				bcc("@ok");
				jsr("Reset_Score");
			}
		});
	}
	
	public static void main(String[] args) {
		new Part7PongSpriteCollisions().buildAsmFile();
	}
}
