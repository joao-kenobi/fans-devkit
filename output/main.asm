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

.segment "ZEROPAGE"
temp1: .res 2
pad1: .res 2
pad1_new: .res 2
pad2: .res 2
pad2_new: .res 2
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
rep #$30 ; A,X,Y 16 BIT MODE
.endif
lda #(bg_palette_end-bg_palette)
ldx #.loword(bg_palette)
ldy #.loword(palette_buffer)
.byte $54, ^palette_buffer, ^bg_palette
plb
sep #$20 ; A 8 BIT MODE

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
rep #$30 ; A,X,Y 16 BIT MODE
.elseif .isize = 8
rep #$30 ; A,X,Y 16 BIT MODE
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
ldx #$4000
stx $2116 ;VMADDL

; === DMA START  === 
lda #$01
sta $4300 ;DMAP0
lda #$18
sta $4301 ;BBAD0
ldx #.loword(sprites_tiles)
stx $4302 ;A1T0L
lda #^sprites_tiles
sta $4304 ;A1B0
ldx #(sprites_tiles_end - sprites_tiles)
stx $4305 ;DAS0L
lda #%1
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

infinite_loop:
sep #$20 ; A 8 BIT MODE
rep #$10 ; X,Y 16 BIT MODE
jsr wait_nmi

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

jsr pad_poll
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
jmp infinite_loop

wait_nmi:
.a8
.i16
lda in_nmi

@check_again:
WAI
cmp in_nmi
beq @check_again
rts

pad_poll:
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
lda $421A ;JOY2L
sta pad2
eor temp1
and pad2
sta pad2_new
plp
rts
SPR_PRIOR_2 = $20

sprites:
.byte $80, $80, $00, SPR_PRIOR_2
.byte $80, $90, $20, SPR_PRIOR_2
.byte $7c, $90, $22, SPR_PRIOR_2

sprites_end:
.segment "RODATA1"

bg_palette:
.incbin "C:/ambiente_desenvolvimento/test/fans/fans-devkit/projects/fans-examples/home/includes/graphics/nesdoug/part6/default.pal"
.incbin "C:/ambiente_desenvolvimento/test/fans/fans-devkit/projects/fans-examples/home/includes/graphics/nesdoug/part6/sprite.pal"

bg_palette_end:

sprites_tiles:
.incbin "C:/ambiente_desenvolvimento/test/fans/fans-devkit/projects/fans-examples/home/includes/graphics/nesdoug/part6/sprite.chr"

sprites_tiles_end:
.include "../framework/asm/includes/ca65/header.asm"
