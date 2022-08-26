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
.a16
.i16
phk
plb
sep #$20 ; A 8 BIT MODE
stz $2121 ;CGADD
lda #$1F
sta $2122 ;CGDATA
stz $2122
lda #$0f
sta $2100 ;INIDISP

_foreverLoop:
jmp _foreverLoop
.include "../framework/asm/includes/ca65/header.asm"
