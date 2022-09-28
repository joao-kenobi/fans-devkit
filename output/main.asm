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
stz oam_hi_buffer
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
ldx #.loword(tiles)
stx $4302 ;A1T0L
lda #^tiles
sta $4304 ;A1B0
ldx #(tiles_end - tiles)
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
wai
cmp in_nmi
beq @check_again
rts
sprite_priority = $20

sprites:
.byte $A, $73, $0, sprite_priority
.byte $12, $73, $1, sprite_priority
.byte $1A, $73, $2, sprite_priority
.byte $22, $73, $3, sprite_priority
.byte $2A, $73, $4, sprite_priority
.byte $32, $73, $5, sprite_priority
.byte $3A, $73, $6, sprite_priority
.byte $42, $73, $7, sprite_priority
.byte $4A, $73, $8, sprite_priority
.byte $A, $7B, $10, sprite_priority
.byte $12, $7B, $11, sprite_priority
.byte $1A, $7B, $12, sprite_priority
.byte $22, $7B, $13, sprite_priority
.byte $2A, $7B, $14, sprite_priority
.byte $32, $7B, $15, sprite_priority
.byte $3A, $7B, $16, sprite_priority
.byte $42, $7B, $17, sprite_priority
.byte $4A, $7B, $18, sprite_priority
.byte $A, $83, $20, sprite_priority
.byte $12, $83, $21, sprite_priority
.byte $1A, $83, $22, sprite_priority
.byte $22, $83, $23, sprite_priority
.byte $2A, $83, $24, sprite_priority
.byte $32, $83, $25, sprite_priority
.byte $3A, $83, $26, sprite_priority
.byte $42, $83, $27, sprite_priority
.byte $4A, $83, $28, sprite_priority
.byte $A, $8B, $30, sprite_priority
.byte $12, $8B, $31, sprite_priority
.byte $1A, $8B, $32, sprite_priority
.byte $22, $8B, $33, sprite_priority
.byte $2A, $8B, $34, sprite_priority
.byte $32, $8B, $35, sprite_priority
.byte $3A, $8B, $36, sprite_priority
.byte $42, $8B, $37, sprite_priority
.byte $4A, $8B, $38, sprite_priority
.byte $A, $93, $40, sprite_priority
.byte $12, $93, $41, sprite_priority
.byte $1A, $93, $42, sprite_priority
.byte $22, $93, $43, sprite_priority
.byte $2A, $93, $44, sprite_priority
.byte $32, $93, $45, sprite_priority
.byte $3A, $93, $46, sprite_priority
.byte $42, $93, $47, sprite_priority
.byte $4A, $93, $48, sprite_priority
.byte $A, $9B, $50, sprite_priority
.byte $12, $9B, $51, sprite_priority
.byte $1A, $9B, $52, sprite_priority
.byte $22, $9B, $53, sprite_priority
.byte $2A, $9B, $54, sprite_priority
.byte $32, $9B, $55, sprite_priority
.byte $3A, $9B, $56, sprite_priority
.byte $42, $9B, $57, sprite_priority
.byte $4A, $9B, $58, sprite_priority
.byte $A, $A3, $60, sprite_priority
.byte $12, $A3, $61, sprite_priority
.byte $1A, $A3, $62, sprite_priority
.byte $22, $A3, $63, sprite_priority
.byte $2A, $A3, $64, sprite_priority
.byte $32, $A3, $65, sprite_priority
.byte $3A, $A3, $66, sprite_priority
.byte $42, $A3, $67, sprite_priority
.byte $4A, $A3, $68, sprite_priority
.byte $A, $AB, $70, sprite_priority
.byte $12, $AB, $71, sprite_priority
.byte $1A, $AB, $72, sprite_priority
.byte $22, $AB, $73, sprite_priority
.byte $2A, $AB, $74, sprite_priority
.byte $32, $AB, $75, sprite_priority
.byte $3A, $AB, $76, sprite_priority
.byte $42, $AB, $77, sprite_priority
.byte $4A, $AB, $78, sprite_priority
.byte $A, $B3, $80, sprite_priority
.byte $12, $B3, $81, sprite_priority
.byte $1A, $B3, $82, sprite_priority
.byte $22, $B3, $83, sprite_priority
.byte $2A, $B3, $84, sprite_priority
.byte $32, $B3, $85, sprite_priority
.byte $3A, $B3, $86, sprite_priority
.byte $42, $B3, $87, sprite_priority
.byte $4A, $B3, $88, sprite_priority
.byte $A, $BB, $90, sprite_priority
.byte $12, $BB, $91, sprite_priority
.byte $1A, $BB, $92, sprite_priority
.byte $22, $BB, $93, sprite_priority
.byte $2A, $BB, $94, sprite_priority
.byte $32, $BB, $95, sprite_priority
.byte $3A, $BB, $96, sprite_priority
.byte $42, $BB, $97, sprite_priority
.byte $4A, $BB, $98, sprite_priority
.byte $A, $C3, $A0, sprite_priority
.byte $12, $C3, $A1, sprite_priority
.byte $1A, $C3, $A2, sprite_priority
.byte $22, $C3, $A3, sprite_priority
.byte $2A, $C3, $A4, sprite_priority
.byte $32, $C3, $A5, sprite_priority
.byte $3A, $C3, $A6, sprite_priority
.byte $42, $C3, $A7, sprite_priority
.byte $4A, $C3, $A8, sprite_priority
.byte $A, $CB, $B0, sprite_priority
.byte $12, $CB, $B1, sprite_priority
.byte $1A, $CB, $B2, sprite_priority
.byte $22, $CB, $B3, sprite_priority
.byte $2A, $CB, $B4, sprite_priority
.byte $32, $CB, $B5, sprite_priority
.byte $3A, $CB, $B6, sprite_priority
.byte $42, $CB, $B7, sprite_priority
.byte $4A, $CB, $B8, sprite_priority
.byte $A, $D3, $C0, sprite_priority
.byte $12, $D3, $C1, sprite_priority
.byte $1A, $D3, $C2, sprite_priority
.byte $22, $D3, $C3, sprite_priority
.byte $2A, $D3, $C4, sprite_priority
.byte $32, $D3, $C5, sprite_priority
.byte $3A, $D3, $C6, sprite_priority
.byte $42, $D3, $C7, sprite_priority
.byte $4A, $D3, $C8, sprite_priority
sprites_end:

.segment "RODATA1"

bg_palette:
.incbin "C:/ambiente_desenvolvimento/test/fans/fans-devkit/projects/fans-examples/home/includes/graphics/teste/sprite/iori/default.palette"
.incbin "C:/ambiente_desenvolvimento/test/fans/fans-devkit/projects/fans-examples/home/includes/graphics/teste/sprite/iori/iori.palette"
bg_palette_end:


tiles:
.incbin "C:/ambiente_desenvolvimento/test/fans/fans-devkit/projects/fans-examples/home/includes/graphics/teste/sprite/iori/iori.tiles"
tiles_end:

.include "../framework/asm/includes/ca65/header.asm"
