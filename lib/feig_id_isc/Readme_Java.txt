Copyright © 2004-2016	FEIG ELECTRONIC GmbH, All Rights Reserved.
			Lange Strasse 4
			D-35781 Weilburg
			Federal Republic of Germany
			phone    : +49 6471 31090
			fax      : +49 6471 310999
			e-mail   : obid-support@feig.de
			Internet : http://www.feig.de

OBID and OBID i-scan are registered trademarks of FEIG ELECTRONIC GmbH


===============================
	ID ISC.SDK.Java

	   V4.07.00

	  2016-09-27
===============================


1. What is new
- Update of namespaces and access constants for reader configuration
- Support of new Reader ID CPR74
- Support of improved Reader ID ISC.LRU1002
- Support for UHF transponder type UCODE DNA
- Support for ISO 14443 transpoder type NXP Ultralight EV1
- All other changes can be found in the SDK manual H31101-e-ID-B.pdf


NOTE: To learn more about OBID i-scan Readers use our Windows Tools ISOStart V9.9.6 (or higher) and for OBID classic-pro Readers use CPRStart V9.9.6 (or higher)


2. Installation

Copy all directories into your local path. For Windows Systems: "C:\Program Files\OBID\" is recommended.
To install the library files on a Linux, follow the installation instructions of the manuals.



NOTES for Netbeans user:
- The projects are built with Netbeans V7.4
- Select the proper path to the native files for your selected JRE in: Project Settings > Run > set the Working Directory to: ..\..\..\..\sw-run\<windows/linux>\<target>.
  <target> must be x86 for 32-Bit JRE and x64 for 64-Bit JRE.


NOTE for Windows:
Cyphered protocol transmission depends on openSSL. Copy libeay32.dll from directory run to your application path, if cyphered protocol transmission is used and pay notice about the openSSL licence in SDK manual H31101-xx-ID-B.pdf. If not, you need not copy libeay32.dll to your application path.

NOTE for Linux:
Cyphered protocol transmission depends on the installation of openSSL V0.9.8 or higher. If cyphered protocol transmission is used you must pay notice about the openSSL licence in SDK manual H31101-e-ID-B.pdf.
When communication with an USB Reader is recommended, please have a look into the manual of FEUSB for proper installation.

NOTE for 64-Bit Operating Systems: all native libraries and also the Java library OBIDISC4J.jar is compiled for 32- and 64-Bit Runtime Environments. This means, on development and on target machines, the 32- resp. 64-Bit Java Runtime Environment must be installed.
Developers must select the 32- resp. 64-Bit JDK for compilation.


3. Sample projects
- FUSample demonstrates the programming for HF/UHF Function Units
- ISOHostSample demonstrates the ISO-Host-Commands with all readers
- BRMSample demonstrates the Buffered-Read-Mode for Readers supporting the Buffered-Read-Mode
- NotifySample demonstrates the Notification-Mode for Readers supporting the Notification-Mode
- ScanSample demonstrates the Scan-Mode for Readers supporting the Scan-Mode with serial port

4. Compiler Suite and Compiler Settings for embedded Linux
 --> moved to seperate ID ISC.SDK.Linux_embedded
