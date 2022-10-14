.p816
.smart

; === DEFAULT ZEROPAGE SECTION ===
.segment "ZEROPAGE"
in_nmi: .res 2
temp1: .res 2
pad1: .res 2
pad1_new: .res 2
pad2: .res 2
pad2_new: .res 2
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
bg1_x: .res 1
bg1_y: .res 1
bg2_x: .res 1
bg2_y: .res 1
bg3_x: .res 1
bg3_y: .res 1
temp2: .res 2
temp3: .res 2
temp4: .res 2
temp5: .res 2
temp6: .res 2
sprid: .res 1
map_selected: .res 1
spr_c: .res 1
spr_sz: .res 1
obj1x: .res 1
obj1w: .res 1
obj1y: .res 1
obj1h: .res 1
obj2x: .res 1
obj2w: .res 1
obj2y: .res 1
obj2h: .res 1
collision: .res 1
spr_a: .res 1
spr_x: .res 1
spr_y: .res 1
spr_x2: .res 2
spr_h: .res 2
.include "../framework/asm/includes/ca65/library.asm"
.include "../framework/asm/includes/ca65/init.asm"
.include "../framework/asm/includes/ca65/header.asm"
.segment "CODE"

main:
sep #$20 ; A 8 BIT MODE
rep #$10 ; X,Y 16 BIT MODE
phk
plb
SPR_POS_X = 0
SPR_NEG_X = 1
SPR_SIZE_SM = 0
SPR_SIZE_LG = 2
SPR_PRIOR_2 = $20
SPR_PAL_0  = $00
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
sep #$20 ; A 8 BIT MODE
stz $2121 ;CGADD

; === DMA START  === 
stz $4300 ;DMAP0
lda #$22
sta $4301 ;BBAD0
ldx #.loword(bg_palette)
stx $4302 ;A1T0L
lda #^bg_palette
sta $4304 ;A1B0
ldx #(bg_palette_end-bg_palette)
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
ldx #.loword(tiles)
stx $4302 ;A1T0L
lda #^tiles
sta $4304 ;A1B0
ldx #(tiles_end - tiles)
stx $4305 ;DAS0L
lda #%1
sta $420B ;MDMAEN
; === DMA END  === 

ldx #$3000
stx $2116 ;VMADDL

; === DMA START  === 
lda #$01
sta $4300 ;DMAP0
lda #$18
sta $4301 ;BBAD0
ldx #.loword(tiles_2)
stx $4302 ;A1T0L
lda #^tiles_2
sta $4304 ;A1B0
ldx #(tiles_2_end - tiles_2)
stx $4305 ;DAS0L
lda #%1
sta $420B ;MDMAEN
; === DMA END  === 

ldx #$4000
stx $2116 ;VMADDL

; === DMA START  === 
lda #$01
sta $4300 ;DMAP0
lda #$18
sta $4301 ;BBAD0
ldx #.loword(sprite_tiles)
stx $4302 ;A1T0L
lda #^sprite_tiles
sta $4304 ;A1B0
ldx #(sprite_tiles_end - sprite_tiles)
stx $4305 ;DAS0L
lda #%1
sta $420B ;MDMAEN
; === DMA END  === 

ldx #$6000
stx $2116 ;VMADDL

; === DMA START  === 
lda #$01
sta $4300 ;DMAP0
lda #$18
sta $4301 ;BBAD0
ldx #.loword(tilemap)
stx $4302 ;A1T0L
lda #^tilemap
sta $4304 ;A1B0
ldx #(tilemap_end - tilemap)
stx $4305 ;DAS0L
lda #%1
sta $420B ;MDMAEN
; === DMA END  === 

ldx #$6800
stx $2116 ;VMADDL

; === DMA START  === 
lda #$01
sta $4300 ;DMAP0
lda #$18
sta $4301 ;BBAD0
ldx #.loword(tilemap_2)
stx $4302 ;A1T0L
lda #^tilemap_2
sta $4304 ;A1B0
ldx #(tilemap_2_end - tilemap_2)
stx $4305 ;DAS0L
lda #%1
sta $420B ;MDMAEN
; === DMA END  === 

ldx #$7000
stx $2116 ;VMADDL

; === DMA START  === 
lda #$01
sta $4300 ;DMAP0
lda #$18
sta $4301 ;BBAD0
ldx #.loword(tilemap_3)
stx $4302 ;A1T0L
lda #^tilemap_3
sta $4304 ;A1B0
ldx #(tilemap_3_end - tilemap_3)
stx $4305 ;DAS0L
lda #%1
sta $420B ;MDMAEN
; === DMA END  === 

lda #$1|8
sta $2105 ;BGMODE
stz $210b ;BG12NBA
lda #$03
sta $210c ;BG34NBA
lda #$60
sta $2107 ;BG1SC
lda #$68
sta $2108 ;BG2SC
lda #$70
sta $2109 ;BG3SC
lda #2
sta $2101 ;OBSEL
lda #$1f
sta $212C ;TM
lda #$80|01
sta $4200 ;NMITIMEN
lda #$0f
sta $2100 ;INIDISP

infinite_loop:
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

jsr set_scroll
jsr pad_poll
jsr clear_oam
rep #$30 ; A,X,Y 16 BIT MODE
lda pad1
and #$0200
beq @not_left

@left:
sep #$20 ; A 8 BIT MODE
rep #$20  ; A 16 BIT MODE

@not_left:
lda pad1
and #$0100
beq @not_right

@right:
sep #$20 ; A 8 BIT MODE
rep #$20  ; A 16 BIT MODE

@not_right:
lda pad1
and #$0800
beq @not_up

@up:
sep #$20 ; A 8 BIT MODE
rep #$20  ; A 16 BIT MODE

@not_up:
lda pad1
and #$0400
beq @not_down

@down:
sep #$20 ; A 8 BIT MODE
rep #$20  ; A 16 BIT MODE

@not_down:
sep #$20 ; A 8 BIT MODE
jmp infinite_loop

draw_sprites:
php
stz sprid
lda #10
sta spr_x
sta spr_y
lda map_selected
asl a
sta spr_c
lda #SPR_PAL_0|SPR_PRIOR_2
sta spr_a
lda #SPR_SIZE_LG
sta spr_sz
jsr OAM_Spr
plp
rts

set_scroll:
.a8
.i16
php
lda bg1_x
sta $210D ;BG1HOFS
stz $210D ;BG1HOFS
lda bg1_y
sta $210E ;BG1VOFS
stz $210E ;BG1VOFS
lda bg2_x
sta $210F ;BG2HOFS
stz $210F ;BG2HOFS
lda bg2_y
sta $2110 ;BG2VOFS
stz $2110 ;BG2VOFS
lda bg3_x
sta $2111 ;BG3HOFS
stz $2111 ;BG3HOFS
lda bg3_y
sta $2111 ;BG3HOFS
stz $2111 ;BG3HOFS
plp
rts

wait_nmi:
.a8
.i16
lda in_nmi

@check_again:
wai
cmp in_nmi
beq @check_again
rts

pad_poll:
.a8
.i16
php

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
.segment "RODATA1"

bg_palette:
.incbin "C:/ambiente_desenvolvimento/test/fans/fans-devkit/projects/fans-examples/home/includes/graphics/nesdoug/part8/backgrounds/allBG.pal"
bg_palette_end:


tiles:
.incbin "C:/ambiente_desenvolvimento/test/fans/fans-devkit/projects/fans-examples/home/includes/graphics/nesdoug/part8/backgrounds/moon.chr"
tiles_end:


tilemap:
.incbin "C:/ambiente_desenvolvimento/test/fans/fans-devkit/projects/fans-examples/home/includes/graphics/nesdoug/part8/backgrounds/moon.map"
tilemap_end:


tiles_2:
.incbin "C:/ambiente_desenvolvimento/test/fans/fans-devkit/projects/fans-examples/home/includes/graphics/nesdoug/part8/backgrounds/spacebar.chr"
tiles_2_end:


tilemap_3:
.incbin "C:/ambiente_desenvolvimento/test/fans/fans-devkit/projects/fans-examples/home/includes/graphics/nesdoug/part8/backgrounds/spacebar.map"
tilemap_3_end:


tilemap_2:
.incbin "C:/ambiente_desenvolvimento/test/fans/fans-devkit/projects/fans-examples/home/includes/graphics/nesdoug/part8/backgrounds/bluebar.map"
tilemap_2_end:


sprite_palette:
.incbin "C:/ambiente_desenvolvimento/test/fans/fans-devkit/projects/fans-examples/home/includes/graphics/nesdoug/part8/sprites/Sprites.pal"
sprite_palette_end:


sprite_tiles:
.incbin "C:/ambiente_desenvolvimento/test/fans/fans-devkit/projects/fans-examples/home/includes/graphics/nesdoug/part8/sprites/Numbers.chr"
sprite_tiles_end:

