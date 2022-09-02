.p816
.smart

; === DEFAULT ZEROPAGE SECTION ===
.segment "ZEROPAGE"
in_nmi: .res 2
; === END DEFAULT ZEROPAGE SECTION ===

; === DEFAULT BSS SECTION ===
.segment "BSS"
palette_buffer: .res 512

palette_buffer_end:
oam_lo_buffer: .res 512
oam_hi_buffer: .res 32

oam_buffer_end:
; === END DEFAULT BSS SECTION ===

.include "../framework/asm/includes/ca65/init.asm"
.segment "CODE"

main:
.a8
.i16
phk
plb
phb
.if .asize = 8
rep #$30 ; A,X,Y 16 BIT MODE
.elseif .isize = 8
.endif
lda #(bg_palette_end-bg_palette)
ldx #.loword(bg_palette)
ldy #.loword(palette_buffer)
.byte $54, ^palette_buffer, ^bg_palette
plb

; === DMA START  === 
stz $4300 ;DMAP0
lda #$22
sta $4301 ;BBAD0
ldx #.loword(palette_buffer)
stx $4302 ;A1T0L
lda #^palette_buffer
sta $4304 ;A1B0
ldx #(palette_buffer_end-palette_buffer)
stx $4305 ;DAS0L
lda #%1
sta $420B ;MDMAEN
; === DMA END  === 

phb
.if .asize = 8
.elseif .isize = 8
.endif
lda #(sprites_end-sprites)
ldx #.loword(sprites)
ldy #.loword(oam_lo_buffer)
.byte $54, ^oam_lo_buffer, ^sprites
plb
sep #$20 ; A 8 BIT MODE
lda #$6A
sta oam_hi_buffer
stz $2102 ;OAMADDL

; === DMA START  === 
stz $4300 ;DMAP0
lda #$04
sta $4301 ;BBAD0
ldx #.loword(oam_lo_buffer)
stx $4302 ;A1T0L
lda #^oam_lo_buffer
sta $4304 ;A1B0
ldx #(oam_buffer_end - oam_lo_buffer)
stx $4305 ;DAS0L
lda #%1
sta $420B ;MDMAEN
; === DMA END  === 

lda #$80
sta $2115 ;VMAIN
stz $2116 ;VMADDL

; === DMA START  === 
lda #$01
sta $4300 ;DMAP0
lda #$18
sta $4301 ;BBAD0
ldx #.loword(Spr_Tiles)
stx $4302 ;A1T0L
lda #^Spr_Tiles
sta $4304 ;A1B0
ldx #(Spr_Tiles_end - Spr_Tiles)
stx $4305 ;DAS0L
lda #%1
sta $420B ;MDMAEN
; === DMA END  === 

stz $2101 ;OBSEL
lda #$01
sta $2105 ;BGMODE
lda #$10
sta $212C ;TM
lda #$0f
sta $2100 ;INIDISP

Infinite_Loop:
rep #$10 ; X,Y 16 BIT MODE
jsr wait_nmi
jmp Infinite_Loop

wait_nmi:
.a8
.i16
lda in_nmi

@check_again:
WAI
cmp in_nmi
beq @check_again
rts
sprite_priority = $20

sprites:
.byte $02, $03, $03, sprite_priority
.byte $0A, $03, $04, sprite_priority
.byte $12, $03, $05, sprite_priority
.byte $1A, $03, $06, sprite_priority
.byte $22, $03, $07, sprite_priority
.byte $02, $0B, $13, sprite_priority
.byte $0A, $0B, $14, sprite_priority
.byte $12, $0B, $15, sprite_priority
.byte $1A, $0B, $16, sprite_priority
.byte $22, $0B, $17, sprite_priority
.byte $2A, $0B, $18, sprite_priority
.byte $02, $13, $23, sprite_priority
.byte $0A, $13, $24, sprite_priority
.byte $12, $13, $25, sprite_priority
.byte $1A, $13, $26, sprite_priority
.byte $22, $13, $27, sprite_priority
.byte $2A, $13, $28, sprite_priority
.byte $02, $1B, $33, sprite_priority
.byte $0A, $1B, $34, sprite_priority
.byte $12, $1B, $35, sprite_priority
.byte $1A, $1B, $36, sprite_priority
.byte $22, $1B, $37, sprite_priority
.byte $2A, $1B, $38, sprite_priority
.byte $02, $23, $43, sprite_priority
.byte $0A, $23, $44, sprite_priority
.byte $12, $23, $45, sprite_priority
.byte $1A, $23, $46, sprite_priority
.byte $22, $23, $47, sprite_priority
.byte $2A, $23, $48, sprite_priority
.byte $32, $23, $49, sprite_priority
.byte $02, $2B, $53, sprite_priority
.byte $0A, $2B, $54, sprite_priority
.byte $12, $2B, $55, sprite_priority
.byte $1A, $2B, $56, sprite_priority
.byte $22, $2B, $57, sprite_priority
.byte $2A, $2B, $58, sprite_priority
.byte $32, $2B, $59, sprite_priority
.byte $80 ;end of data       

sprites_end:
.segment "RODATA1"

bg_palette:
.incbin "C:/ambiente_desenvolvimento/test/fans/fans-devkit/projects/fans-examples/home/includes/graphics/teste/sprite/iori/default.palette"
.incbin "C:/ambiente_desenvolvimento/test/fans/fans-devkit/projects/fans-examples/home/includes/graphics/teste/sprite/iori/iori.palette"

bg_palette_end:

Spr_Tiles:
.incbin "C:/ambiente_desenvolvimento/test/fans/fans-devkit/projects/fans-examples/home/includes/graphics/teste/sprite/iori/iori.sprite"

Spr_Tiles_end:
.include "../framework/asm/includes/ca65/header.asm"
