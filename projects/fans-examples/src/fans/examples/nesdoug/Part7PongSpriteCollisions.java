package fans.examples.nesdoug;

import fans.core.Ca65Base;
import fans.core.constants.BgModeConstants;
import fans.core.constants.DmaPxConstants;
import fans.core.constants.NmiTIMenConstants;
import fans.core.constants.TmOrTsConstants;
import fans.core.constants.VMainConstants;
import fans.core.enums.BusRegisters;
import fans.core.enums.CpuRegisters;

public class Part7PongSpriteCollisions extends Ca65Base {

	protected void before() {
		
	}
	
	protected void init() {
		blockMove(512, "BG_Palette", "palette_buffer");
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
		
	}
	
	public static void main(String[] args) {
		new Part7PongSpriteCollisions().buildAsmFile();
	}
}
