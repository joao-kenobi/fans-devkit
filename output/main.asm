.p816
.smart
.include "../framework/asm/includes/ca65/variables.asm"
.include ".                ./framework/asm/includes/ca65/macros.asm"
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
