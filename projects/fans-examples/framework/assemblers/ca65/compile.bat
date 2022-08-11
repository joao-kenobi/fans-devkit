@echo off

cls
set emulator_path=..\..\emulators\bsnes-plus-douglas

set source_code_path=..\..\..\home\output

ca65 %source_code_path%\main.asm -g
ld65 -C lorom.cfg -o %source_code_path%\rom.sfc %source_code_path%\main.o -Ln labels.txt
REM ld65 -C lorom.cfg -o %source_code_path%\ rom.sfc main.o -Ln labels.txt
del %source_code_path%\*.o

REM pause
REM %emulator_path%\bsnes.exe %source_code_path%\rom.sfc

C:\ambiente_desenvolvimento\test\snes-lab\snes-lab\emuladores\Mesen-S\Mesen-S.exe %source_code_path%\rom.sfc
