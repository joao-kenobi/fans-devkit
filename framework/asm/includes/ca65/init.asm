.segment "CODE"

NMI:
bit $4210 ;RDNMI
inc in_nmi
rti

IRQ:
bit $4211 ;TIMEUP

IRQ_end:
rti

RESET:
sei
clc
xce
rep #$38
ldx #$1fff
txs
phk
plb
lda #$4200
tcd

lda #$FF00
sta $00
stz $00
stz $02
stz $04
stz $06
stz $08
stz $0A
stz $0C
lda #$2100
tcd

lda #$0080
sta $00
stz $02
stz $05
stz $07
stz $09
stz $0B
stz $16
stz $24
stz $26
stz $28
stz $2A
stz $2C
stz $2E

ldx #$0030
stx $30
ldy #$00E0
sty $32
sep #$20 ; A 8 BIT MODE
sta $15
stz $1A
stz $21
stz $23
.repeat 8, I
 stz $0D+I
 stz $0D+I
.endrepeat
lda #$01
stz $1B
sta $1B
stz $1C
stz $1C
stz $1D
stz $1D
stz $1E
stz $1E
stz $1F
stz $1F
stz $20
stz $20
rep #$30 ; A,X,Y 16 BIT MODE
lda #$0000
tcd
