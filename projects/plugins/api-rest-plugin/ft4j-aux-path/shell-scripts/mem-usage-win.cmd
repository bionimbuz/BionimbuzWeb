@echo off

:GetTotalMemory
for /F "skip=1" %%M in ('%SystemRoot%\System32\wbem\wmic.exe memorychip get capacity') do set "TotalMemory=%%M" & goto GetAvailableMemory

:GetAvailableMemory
for /F "skip=1" %%M in ('%SystemRoot%\System32\wbem\wmic.exe OS get FreePhysicalMemory') do set "AvailableMemory=%%M" & goto ProcessValues

rem Total physical memory is in bytes which can be greater 2^31 (= 2 GB)
rem Windows command processor performs arithmetic operations always with
rem 32-bit signed integer. Therefore more than 2 GB installed physical
rem memory exceeds the bit width of a 32-bit signed integer and arithmetic
rem calculations are wrong on more than 2 GB installed memory. To avoid
rem the integer overflow, the last 6 characters are removed from bytes
rem value and the remaining characters are divided by 1073 to get the
rem number of GB. This workaround works only for physical RAM being
rem an exact multiple of 1 GB, i.e. for 1 GB, 2 GB, 4 GB, 8 GB, ...

rem  1 GB =  1.073.741.824 bytes = 2^30
rem  2 GB =  2.147.483.648 bytes = 2^31
rem  4 GB =  4.294.967.296 bytes = 2^32
rem  8 GB =  8.589.934.592 bytes = 2^33
rem 16 GB = 17.179.869.184 bytes = 2^34
rem 32 GB = 34.359.738.368 bytes = 2^35

rem But there is one more problem at least on Windows XP x86. About 50 MB
rem of RAM is subtracted as used by Windows itself. This can be seen in
rem system settings when 1.95 GB is displayed although 2 GB is installed.
rem Therefore add 50 MB before dividing by 1073.

:ProcessValues
set "TotalMemory=%TotalMemory:~0,-6%"
set /A TotalMemory+=50
set /A TotalMemory/=1073

rem The total memory in GB must be multiplied by 1024 to get the
rem total physical memory in MB which is always small enough to
rem be calculated with a 32-bit signed integer.

set /A TotalMemory*=1024

rem The available memory is in KB and therefore there is
rem no problem with value range of 32-bit signed integer.

set /A AvailableMemory/=1024

rem So the used memory in MB can be determined easily.

set /A UsedMemory=TotalMemory - AvailableMemory

rem It is necessary to calculate the percentage value in MB instead of
rem KB to avoid a 32-bit signed integer overflow on 32 GB RAM and nearly
rem entire RAM is available because used is just a small amount of RAM.

set /A UsedPercent=(UsedMemory * 100) / TotalMemory


rem echo Free memory:    %AvailableMemory% MB
rem echo Total memory:   %TotalMemory% MB
rem echo Used memory:    %UsedMemory% MB
rem echo Memory usage:   %UsedPercent% %%

echo %UsedPercent%

rem Discard the current environment variable table and restore previous
rem environment variables. The states of command processor extension
rem (default: ON) and delayed expansion (default: OFF) as well as the
rem original current directory are restored by this command although
rem not modified at all by the commands above.
