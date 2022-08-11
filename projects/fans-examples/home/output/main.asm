.p816
.smart
.include "../../framework/asm/includes/ca65/variables.asm"
.include "../../framework/asm/includes/ca65/macros.asm"
.include "../../framework/asm/includes/ca65/init.asm"
.segment "CODE"

main:
.a16
.i16
phk
plb
blockMove 288, BG_Palette, palette_buffer 

; === DMA START  === 
stz $4300 ;DMAP0

lda #$22
sta $4301 ;BBAD0

ldx #.loword(palette_buffer)
stx $4302 ;A1T0L

lda #^palette_buffer
sta $4304 ;A1B0

ldx #(palette_buffer_end - palette_buffer)
stx $4305 ;DAS0L

lda #$01
sta $420B ;MDMAEN
; === DMA END  === 

blockMove 12, Sprites, oam_lo_buffer 
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

lda #$01
sta $420B ;MDMAEN
; === DMA END  === 


lda #$80
sta $2115 ;VMAIN

ldx #$4000
stx $2116 ;VMADDL

; === DMA START  === 

lda #$01
sta $4300 ;DMAP0

lda #$18
sta $4301 ;BBAD0

ldx #.loword(Spr_Tiles)
stx $4302 ;A1T0L

lda #^Spr_Tiles
sta $4304 ;A1B0

ldx #(End_Spr_Tiles-Spr_Tiles)
stx $4305 ;DAS0L

lda #$01
sta $420B ;MDMAEN
; === DMA END  === 


lda #$02
sta $2101 ;OBSEL

lda #$01
sta $2105 ;BGMODE

lda #$10
sta $212C ;TM

lda #$80|01
sta $4200 ;NMITIMEN

lda #$0f
sta $2100 ;INIDISP

Infinite_Loop:
sep #$20 ; A 8 BIT MODE
rep #$10 ; X,Y 16 BIT MODE
jsr Wait_NMI

; === DMA START  === 
stz $4300 ;DMAP0

lda #$04
sta $4301 ;BBAD0

ldx #.loword(oam_lo_buffer)
stx $4302 ;A1T0L

lda #^oam_lo_buffer
sta $4304 ;A1B0

ldx #544
stx $4305 ;DAS0L

lda #$01
sta $420B ;MDMAEN
; === DMA END  === 

jsr Pad_Poll
rep #$30 ; A,X,Y 16 BIT MODE
lda pad1
and #$0200
beq @not_left

@left:
sep #$20 ; A 8 BIT MODE
dec oam_lo_buffer
dec oam_lo_buffer+4
dec oam_lo_buffer+8
rep #$20  ; A 16 BIT MODE

@not_left:
lda pad1
and #$0100
beq @not_right

@right:
sep #$20 ; A 8 BIT MODE
inc oam_lo_buffer
inc oam_lo_buffer+4
inc oam_lo_buffer+8
rep #$20  ; A 16 BIT MODE

@not_right:
lda pad1
and #$0800
beq @not_up

@up:
sep #$20 ; A 8 BIT MODE
dec oam_lo_buffer+1
dec oam_lo_buffer+5
dec oam_lo_buffer+9
rep #$20  ; A 16 BIT MODE

@not_up:
lda pad1
and #$0400
beq @not_down

@down:
sep #$20 ; A 8 BIT MODE
inc oam_lo_buffer+1
inc oam_lo_buffer+5
inc oam_lo_buffer+9
rep #$20  ; A 16 BIT MODE

@not_down:
sep #$20 ; A 8 BIT MODE
jmp Infinite_Loop

Wait_NMI:
.a8
.i16
lda in_nmi

@check_again:
WAI
cmp in_nmi
beq @check_again
rts

Pad_Poll:
.a8
.i16
php
sep #$20 ; A 8 BIT MODE

@wait:
lda $4212
lsr a
bcs @wait
rep #$20  ; A 16 BIT MODE

lda pad1
sta temp1

lda $4218 ;JOY1L
sta pad1
eor temp1
and pad1
sta pad1_new

lda pad2
sta temp1

lda $421a ;JOY2L
sta pad2
eor temp1
and pad2
sta pad2_new
plp
rts
SPR_PRIOR_2 = $20

Sprites:
.byte $80, $80, $00, SPR_PRIOR_2
.byte $80, $90, $20, SPR_PRIOR_2
.byte $7c, $90, $22, SPR_PRIOR_2
.segment "RODATA1"

BG_Palette:
.incbin "../includes/graphics/nesdoug/part6/default.pal"
.incbin "../includes/graphics/nesdoug/part6/sprite.pal"

Spr_Tiles:
.incbin "../includes/graphics/nesdoug/part6/sprite.chr"

End_Spr_Tiles:
.include "../../framework/asm/includes/ca65/header.asm"
