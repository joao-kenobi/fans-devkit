@echo off

cls
set emulator_path=..\..\emulators\bsnes-plus-mdc
set source_code_path=..\..\output
%emulator_path%\bsnes.exe %source_code_path%\rom.sfc

REM C:\ambiente_desenvolvimento\test\snes-lab\snes-lab\emuladores\Mesen-S\Mesen-S.exe %source_code_path%\rom.sfc