
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
		blockMove("bg_palette", "palette_buffer");
		dmaBufferToCgram(DmaConstants.TRANSFER_MODE_0, 0);
		
		blockMove("sprites", OAM_LO_BUFFER_VARIABLE);
		a8Bit(); // block move will put AXY16. Undo that.
		
		ldaSta("#$00", OAM_HI_BUFFER_VARIABLE);
		
		stz(BusRegisters.OAMADDL);
		dmaBufferToOam(DmaConstants.TRANSFER_MODE_0, DmaConstants.CHANNEL_0);
		
		// DMA from Spr_Tiles to VRAM
		ldaSta(VMainConstants.INCREMENT_MODE_BY_1, BusRegisters.VMAIN);
		
		ldxStx("#$0000", BusRegisters.VMADDL);
		dmaToVram("tiles", DmaConstants.TRANSFER_MODE_1, DmaConstants.CHANNEL_0);
		
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
				wai();
				cmp("in_nmi");
				beq("@check_again");
				rts();
			});
		});
		
		importGraphics();
	}

	private void importGraphics() {
		String gfxPath = "includes/graphics/teste/sprite/iori";
		
		rawAsm("sprite_priority = $20");
		
		smartLabel("sprites", () -> { 
			int qtdLinhas = 13;
			int qtdColunas = 9;
			int tileNumber = 0;
			
			int posicaoX = 10;
			int posicaoXInicial = posicaoX;
			
			int posicaoY = 115;
			
			for (int l = 0; l < qtdLinhas; l++) {
				
				for (int c = 0; c < qtdColunas; c++) {
					setSpriteProperties(posicaoX, posicaoY, tileNumber++);
					// como o tamanho do sprite e 8x8, para posicionar o proximo sprite da horizontal precisamos incrementar o valor 8
					posicaoX += 8;
				}
				
				// aqui chegamos na proxima linha, entao a posicao horizontal tem que voltar ao inicio
				posicaoX = posicaoXInicial;
				// como o tamanho do sprite e 8x8, para posicionar o proximo sprite da vertical precisamos incrementar o valor 8
				posicaoY += 8;
				// incremantando em 7 porque o tile ja foi incrementado em 1 antes no outro loop
				tileNumber += 7;
			}
		});
		
		segment("RODATA1");
		
		smartLabel("bg_palette", () -> {
			incbin(gfxPath+"/default.palette");
			incbin(gfxPath+"/iori.palette");	
		});
		
		smartLabel("tiles", () -> {
			incbin(gfxPath+"/iori.tiles");
		});
	}
	
	
	private void setSpriteProperties(int x, int y, int tileNumber) {
		rawAsm(".byte $"+toHex(x)+", $"+toHex(y)+", $"+toHex(tileNumber)+", sprite_priority");
	}
	
	public static void main(String[] args) {
		new TesteSprite().compileAndRun();
	}
}