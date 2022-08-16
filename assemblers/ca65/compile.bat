@echo off

cls
set source_code_path=..\..\output

ca65 %source_code_path%\main.asm -g
ld65 -C lorom.cfg -o %source_code_path%\rom.sfc %source_code_path%\main.o -Ln labels.txt
REM ld65 -C lorom.cfg -o %source_code_path%\ rom.sfc main.o -Ln labels.txt
del %source_code_path%\*.o
