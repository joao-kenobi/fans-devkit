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
sep #$20 ; A 8 BIT MODE
rep #$10 ; X,Y 16 BIT MODE
phk
plb
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
ldx #(tiles_end-tiles)
stx $4305 ;DAS0L
lda #%1
sta $420B ;MDMAEN
; === DMA END  === 

ldx #$3000
stx $2116 ;VMADDL

; === DMA START  === 
ldx #.loword(tiles2)
stx $4302 ;A1T0L
lda #^tiles2
sta $4304 ;A1B0
ldx #(tiles2_end-tiles2)
stx $4305 ;DAS0L
lda #%1
sta $420B ;MDMAEN
; === DMA END  === 

ldx #$6000
stx $2116 ;VMADDL

; === DMA START  === 
ldx #.loword(tilemap)
stx $4302 ;A1T0L
lda #^tilemap
sta $4304 ;A1B0
ldx #(tilemap_end-tilemap)
stx $4305 ;DAS0L
lda #%1
sta $420B ;MDMAEN
; === DMA END  === 

ldx #$6800
stx $2116 ;VMADDL

; === DMA START  === 
ldx #.loword(tilemap2)
stx $4302 ;A1T0L
lda #^tilemap2
sta $4304 ;A1B0
ldx #(tilemap2_end-tilemap2)
stx $4305 ;DAS0L
lda #%1
sta $420B ;MDMAEN
; === DMA END  === 

ldx #$7000
stx $2116 ;VMADDL

; === DMA START  === 
ldx #.loword(tilemap3)
stx $4302 ;A1T0L
lda #^tilemap3
sta $4304 ;A1B0
ldx #(tilemap3_end-tilemap3)
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
lda #$1f
sta $212C ;TM
lda #$0f
sta $2100 ;INIDISP

_foreverLoop:
jmp _foreverLoop
.segment "RODATA1"

bg_palette:
.incbin "C:/ambiente_desenvolvimento/test/fans/fans-devkit/projects/fans-examples/home/includes/graphics/nesdoug/part4/allBG.pal"

bg_palette_end:

tiles:
.incbin "C:/ambiente_desenvolvimento/test/fans/fans-devkit/projects/fans-examples/home/includes/graphics/nesdoug/part4/moon.chr"

tiles_end:

tiles2:
.incbin "C:/ambiente_desenvolvimento/test/fans/fans-devkit/projects/fans-examples/home/includes/graphics/nesdoug/part4/spacebar.chr"

tiles2_end:

tilemap:
.incbin "C:/ambiente_desenvolvimento/test/fans/fans-devkit/projects/fans-examples/home/includes/graphics/nesdoug/part4/moon.map"

tilemap_end:

tilemap2:
.incbin "C:/ambiente_desenvolvimento/test/fans/fans-devkit/projects/fans-examples/home/includes/graphics/nesdoug/part4/bluebar.map"

tilemap2_end:

tilemap3:
.incbin "C:/ambiente_desenvolvimento/test/fans/fans-devkit/projects/fans-examples/home/includes/graphics/nesdoug/part4/spacebar.map"

tilemap3_end:
.include "../framework/asm/includes/ca65/header.asm"
