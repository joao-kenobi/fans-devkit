@echo off

cls
set emulator_path=..\..\emulators
set source_code_path=..\..\output
%emulator_path%\bsnes-plus-mdc\bsnes.exe %source_code_path%\rom.sfc

REM %emulator_path%\Mesen-S\Mesen-S.exe %source_code_path%\rom.sfc