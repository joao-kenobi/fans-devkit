.p816
.smart
.segment "ZEROPAGE"
in_nmi: .res 2
.segment "BSS"
palette_buffer: .res 512
palette_buffer_end:
oam_lo_buffer: .res 512 ;low table 
oam_hi_buffer: .res 32 ;high table 
oam_buffer_end:
.include "../framework/asm/includes/ca65/macros.asm"
.include "../framework/asm/includes/ca65/init.asm"
.segment "CODE"

main:
.a16
.i16
phk
plb
sep #$20 ; A 8 BIT MODE
stz $2121 ;CGADD

; === DMA START  === 
stz $4300 ;DMAP0

lda #$22
sta $4301 ;BBAD0

ldx #.loword(BG_Palette)
stx $4302 ;A1T0L

lda #^BG_Palette
sta $4304 ;A1B0

ldx #256
stx $4305 ;DAS0L

lda #$01
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

ldx #.loword(Tiles)
stx $4302 ;A1T0L

lda #^Tiles
sta $4304 ;A1B0

ldx #(End_Tiles-Tiles)
stx $4305 ;DAS0L

lda #$01
sta $420B ;MDMAEN
; === DMA END  === 


ldx #$3000
stx $2116 ;VMADDL

; === DMA START  === 

ldx #.loword(Tiles2)
stx $4302 ;A1T0L

lda #^Tiles2
sta $4304 ;A1B0

ldx #(End_Tiles2-Tiles2)
stx $4305 ;DAS0L

lda #$01
sta $420B ;MDMAEN
; === DMA END  === 


ldx #$6000
stx $2116 ;VMADDL

; === DMA START  === 

ldx #.loword(Tilemap)
stx $4302 ;A1T0L

lda #^Tilemap
sta $4304 ;A1B0

ldx #$700
stx $4305 ;DAS0L

lda #$01
sta $420B ;MDMAEN
; === DMA END  === 


ldx #$6800
stx $2116 ;VMADDL

; === DMA START  === 

ldx #.loword(Tilemap2)
stx $4302 ;A1T0L

lda #^Tilemap2
sta $4304 ;A1B0

ldx #$700
stx $4305 ;DAS0L

lda #$01
sta $420B ;MDMAEN
; === DMA END  === 


ldx #$7000
stx $2116 ;VMADDL

; === DMA START  === 

ldx #.loword(Tilemap3)
stx $4302 ;A1T0L

lda #^Tilemap3
sta $4304 ;A1B0

ldx #$700
stx $4305 ;DAS0L

lda #$01
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

BG_Palette:
.incbin "C:/ambiente_desenvolvimento/test/fans/fans-devkit/projects/fans-examples/home/includes/graphics/nesdoug/part4/allBG.pal"

Tiles:
.incbin "C:/ambiente_desenvolvimento/test/fans/fans-devkit/projects/fans-examples/home/includes/graphics/nesdoug/part4/moon.chr"

End_Tiles:

Tiles2:
.incbin "C:/ambiente_desenvolvimento/test/fans/fans-devkit/projects/fans-examples/home/includes/graphics/nesdoug/part4/spacebar.chr"

End_Tiles2:

Tilemap:
.incbin "C:/ambiente_desenvolvimento/test/fans/fans-devkit/projects/fans-examples/home/includes/graphics/nesdoug/part4/moon.map"

Tilemap2:
.incbin "C:/ambiente_desenvolvimento/test/fans/fans-devkit/projects/fans-examples/home/includes/graphics/nesdoug/part4/bluebar.map"

Tilemap3:
.incbin "C:/ambiente_desenvolvimento/test/fans/fans-devkit/projects/fans-examples/home/includes/graphics/nesdoug/part4/spacebar.map"
.include "../framework/asm/includes/ca65/header.asm"
