#!/usr/bin/env bash
mkdir Releases

restart = false

function build_mac_intel() {
    {
        echo "BUILDING MAC OS INTEL"
        arch -x86_64 $(which mvn) clean package
        arch -x86_64 /Library/Java/JavaVirtualMachines/temurin-25.jdk/Contents/Home/bin/jpackage --input target/ --name StudentManager --main-jar studentmanager-1.0-SNAPSHOT.jar --main-class Launcher --type dmg --icon ./src/main/resources/assets/images/icon.icns
        mv ./StudentManager-1.0.dmg Releases/StudentManager-1.0-mac-x86.dmg
        echo "SUCCESS BUILDING MAC OS INTEL"
    } || {
        echo "FAILED BUILD MAC OS INTEL"
    }
}

function build_mac_silicon() {
{
        echo "BUILDING MAC OS APPLE SILICON"
        arch -x86_64 $(which mvn) clean package
        jpackage --input target/ --name StudentManager --main-jar studentmanager-1.0-SNAPSHOT.jar --main-class Launcher --type dmg --icon ./src/main/resources/assets/images/icon.icns
        mv ./StudentManager-1.0.dmg Releases/StudentManager-1.0-mac-silicon.dmg
        echo "SUCCESS BUILDING MAC OS APPLE SILICON"
    } || {
        echo "FAILED BUILD MAC OS APPLE SILICON"
    }
}


function build_windows() {
    {   
        echo "BUILDING WINDOWS"
        mvn clean package
	    rm -rf ./StudentManager
        "$JAVA_HOME/bin/jpackage" --type msi --name StudentManager --input target/ --dest ./Releases/ --main-jar studentmanager-1.0-SNAPSHOT.jar --main-class Launcher --win-shortcut --win-menu --app-version "1.0" --icon ./src/main/resources/assets/images/icon.ico
        echo "SUCCESS BUILDING WINDOWS"
    } || {
        echo "FAILED BUILD WINDOWS"
    }
}


function install_jdk() {
    echo "Checking for JDK"
    case "$(uname -s)" in
    Darwin)
        if [ ! -d "/Library/Java/JavaVirtualMachines/temurin-25.jdk" ]; then
            brew install --cask temurin || {
                echo "Please install brew to continue"
                exit 1
            }
            restart=true
        else
            echo "Temurin JDK already installed"
        fi
        ;;
    MINGW64_NT* | MSYS_NT* | CYGWIN_NT*)
        TEMURIN_PATH=$(ls -d "/c/Program Files/Eclipse Adoptium/jdk-"* 2>/dev/null | sort -V | tail -1)
        if [ -z "$TEMURIN_PATH" ]; then
            choco install temurin --yes || {
                echo "Please install choco to continue"
                exit 1
            }
            restart=true
        else
            echo "Temurin JDK already installed at $TEMURIN_PATH"
        fi
        ;;
    *)
        echo "Other OS: $(uname -s). Not supported"
        ;;
    esac
}

function install_maven() {
    echo "Checking for Maven"
    case "$(uname -s)" in
    Darwin)
        
        if ! command -v mvn &>/dev/null; then
            echo "Installing Maven"
            brew install maven
        else
            echo "Maven already installed"
            mvn -version
        fi
        ;;
    MINGW64_NT* | MSYS_NT* | CYGWIN_NT*)
        if ! command -v mvn &>/dev/null; then
            echo "Installing Maven"
            choco install maven --yes
            restart=true
        else
            echo "Maven already installed"
            mvn -version
        fi
        ;;
    *)
        echo "Other OS: $(uname -s). Not supported"
        ;;
    esac
}

function build() {
    case "$(uname -s)" in
    Darwin)
        echo "Running on macOS"
        if [[ $(uname -m) == 'arm64' ]]; then
            echo "User is on Apple Silicon"
            build_mac_intel
            build_mac_silicon
        else
            echo "User is on Intel (or another architecture)"
            build_mac_intel
        fi
        ;;
    MINGW64_NT*|MSYS_NT*|CYGWIN_NT*)
        echo "Running on Windows"
        build_windows
        ;;
    *)
        echo "Other OS: $(uname -s). Not supported"
        ;;
    esac
}

function install_wix() {
     case "$(uname -s)" in
    MINGW64_NT*|MSYS_NT*|CYGWIN_NT*)
        echo "Running on Windows"
        choco install wixtoolset || {
            echo "Failed to install wiztoolset"
            exit
        }

        ;;
    *)
        echo "Other OS: $(uname -s). Not supported"
        ;;
    esac
}

function set_env() {
    case "$(uname -s)" in
    Darwin)
        export JAVA_HOME=/Library/Java/JavaVirtualMachines/temurin-25.jdk/Contents/Home
        ;;
    MINGW64_NT*|MSYS_NT*|CYGWIN_NT*)
        export JAVA_HOME="$(cygpath -u "$(java -XshowSettings:property -version 2>&1 | grep 'java.home' | awk -F '= ' '{print $2}' | tr -d '\r')")"
        TEMURIN_PATH=$(ls -d "/c/Program Files/Eclipse Adoptium/jdk-"* 2>/dev/null | sort -V | tail -1)
        if [ -n "$TEMURIN_PATH" ]; then
            export JAVA_HOME="$TEMURIN_PATH"
        else
            echo "Could not find Temurin JDK"
            exit 1
        fi
        ;;
    *)
        echo "Other OS: $(uname -s). Not supported"
        ;;
    esac
}

# first install jck
install_jdk
# then install maven
install_maven
# install wix
install_wix

if [ "$restart" = "true" ]; then
    echo "Deps installed. Please close the terminal and then rerun the script."
    exit 0
fi

# finally, build the stuff
set_env
build
exit
