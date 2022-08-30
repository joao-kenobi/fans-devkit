@echo off

cls
set emulator_path=..\..\emulators
set source_code_path=..\..\output
REM %emulator_path%\bsnes-plus-mdc\bsnes.exe %source_code_path%\rom.sfc

%emulator_path%\Mesen-S\Mesen-S.exe %source_code_path%\rom.sfc