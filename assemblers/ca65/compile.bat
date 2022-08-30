@echo off

cls
set source_code_path=..\..\output

ca65 -g %source_code_path%\main.asm
REM ld65 -C lorom.cfg -o %source_code_path%\rom.sfc %source_code_path%\main.o -Ln labels.txt --dbgfile %source_code_path%\rom.dbg
ld65 -C lorom.cfg -o %source_code_path%\rom.sfc %source_code_path%\main.o -Ln %source_code_path%\rom.sym
REM ld65 -C lorom.cfg -o %source_code_path%\rom.sfc %source_code_path%\main.o --dbgfile %source_code_path%\rom.dbg
del %source_code_path%\*.o