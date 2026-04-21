mkdir Releases

function build_mac_intel() {
    {
        export JAVA_HOME=/Library/Java/JavaVirtualMachines/temurin-25.jdk/Contents/Home
        echo "BUILDING MAC OS INTEL"
        arch -x86_64 $(which mvn) clean package
        arch -x86_64 /Library/Java/JavaVirtualMachines/temurin-25.jdk/Contents/Home/bin/jpackage --input target/ --name StudentManager --main-jar studentmanager-1.0-SNAPSHOT.jar --main-class Launcher --type dmg
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
        jpackage --input target/ --name StudentManager --main-jar studentmanager-1.0-SNAPSHOT.jar --main-class Launcher --type dmg
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
        jpackage --input target/ --name StudentManager --main-jar studentmanager-1.0-SNAPSHOT.jar --main-class Launcher --type msi
        mv ./StudentManager-1.0.msi Releases/StudentManager-1.0-windows.msi
        echo "SUCCESS BUILDING WINDOWS"
    } || {
        echo "FAILED BUILD WINDOWS"
    }
}


function install_jdk() {
    echo "Checking for JDK"
    case "$(uname -s)" in
    Darwin)
        # check if brew is installed
        {
            brew install java
        } || {
            echo "Please install brew to continue"
            exit
        }
        ;;
    MINGW64_NT*|MSYS_NT*|CYGWIN_NT*)
        {
            choco install openjdk
        } || {
            echo "Please install choco to continue"
            exit
        }
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
        # check if maven is installed
        {
            mvn -version
        } || {
            echo "Installing Maven Package Manager"
            brew install maven
        }
        ;;
    MINGW64_NT*|MSYS_NT*|CYGWIN_NT*)
        {
            mvn -version
        } || {
            echo "Installing Maven Package Manager"
            choco install maven
        }
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

# first install jck
install_jdk
# then install maven
install_maven
# finally, build the stuff
build
exit