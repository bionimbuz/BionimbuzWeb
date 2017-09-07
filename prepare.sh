#!/bin/bash
ROOT_DIR="$( cd "$( dirname "${BASH_SOURCE[0]}" )" && pwd )"
SYSTEM_FOLDER=${ROOT_DIR}/system
SOURCE_FOLDER=${ROOT_DIR}/src
PLAY_FOLDER=${SYSTEM_FOLDER}/playframework
PLAY_BIN=${PLAY_FOLDER}/play

# HELP
# --------------------------------------------------------------------
Help() {
    echo "
Help: ${0##*/} [-h] [-a] [-e] [-n] [-ae] [-an]
Where:
    -h[H]           = Show this help
    -a[A]           = Prepare all development environment (without IDEs)
    -e[E]           = Prepare Eclipse IDE environment
    -n[N]           = Prepare Netbeans IDE environment
    ";
}

# --------------------------------------------------------------------
NeedHelp(){
    echo "Run: '${0##*/} -h' to obtain help."
    exit
}

# --------------------------------------------------------------------
[[ $BASH_ARGC ]] || { echo `NeedHelp`; exit 1; }

while getopts "hHaAeEnN" OPC 2>/dev/null
do
   case "$OPC" in
      [aA]) PREPARE_ALL="true" ;;
      
      [eE]) PREPARE_ECLIPSE="true" ;;
      
      [nN]) PREPARE_NETBEANS="true" ;;

      [hH]) Help ; exit ;;
      *) NeedHelp ;; 
    esac
done


echo "##################################################"
echo "####### Preparing development environment"
echo "##################################################"

if [ ! -z "$OPEN_BROWSER" ]; then 

    echo "# Installing dependencies"
    echo "# ======================================="

    sudo add-apt-repository ppa:webupd8team/java -y
    sudo apt-get update

    sudo apt-get install -y \
        oracle-java8-installer \
        postgresql postgresql-client \


    echo "# Downloading Playframework"
    echo "# ======================================="

    VERSION=1.4.4
    PACKAGE=play-${VERSION}
    PACKAGE_ZIP=${PACKAGE}.zip

    echo "# Removing current"
    rm -rf ${PACKAGE_ZIP} ${ROOT_DIR}/system/playframework

    echo "# Downloading"
    wget https://downloads.typesafe.com/play/${VERSION}/${PACKAGE_ZIP}
    
    echo "# Unzipping"
    unzip ${PACKAGE_ZIP}

    echo "# Creating directories"    
    mkdir ${ROOT_DIR}/system
    mv ${PACKAGE} ${PLAY_FOLDER}
    
fi

echo "# Preparing project"
echo "# ======================================="

cd ${SOURCE_FOLDER}

echo "# Computing dependencies"    
${PLAY_BIN} deps

if [ ! -z "$PREPARE_ECLIPSE" ]; then 
    echo "# Preparing Eclipse environment"    
    ${PLAY_BIN} eclipsify
fi    

if [ ! -z "$PREPARE_NETBEANS" ]; then 
    echo "# Preparing Netbeans environment"    
    ${PLAY_BIN} netbeansify
fi    

cd ${ROOT_DIR}
