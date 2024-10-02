# CS_3152_Team_3

To make a build, follow the instructions below:
## Windows Build:
1. java -jar packr-all-4.0.0.jar --platform windows64 --jdk openjdk-21.0.2_windows-x64_bin.zip --useZgcIfSupportedOs --executable EverLast --classpath CS_3152_Team_3.jar --mainclass com.redpacts.frostpurge.game.DesktopLauncher --output WindowsBuild

## Mac Build:
1. Run Terminal as Admin
2. java -jar packr-all-4.0.0.jar --platform mac --jdk openjdk-21.0.2_macos-x64_bin.tar.gz --useZgcIfSupportedOs --executable EverLast --classpath CS_3152_Team_3.jar --mainclass com.redpacts.frostpurge.game.DesktopLauncher --vmargs XstartOnFirstThread --output MacBuild
3. chmod +x on executable
4. Wrap folder in .app
