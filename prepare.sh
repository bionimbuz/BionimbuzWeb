#!/bin/bash
ROOT_DIR="$( cd "$( dirname "${BASH_SOURCE[0]}" )" && pwd )"
SYSTEM_FOLDER=${ROOT_DIR}/system
WEB_FOLDER=${ROOT_DIR}/projects/web
COMMONS_FOLDER=${ROOT_DIR}/projects/commons
PLUGINS_FOLDER=${ROOT_DIR}/projects/plugins
COORDINATORS_FOLDER=${ROOT_DIR}/projects/coordinators
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
    -p[P]           = Prepare specific folder project, separated by commas (e.g. \"web,plugins,commons,coordinators\") 
    ";
}

# --------------------------------------------------------------------
NeedHelp(){
    echo "Run: '${0##*/} -h' to obtain help."
    exit
}

# --------------------------------------------------------------------
[[ $BASH_ARGC ]] || { echo `NeedHelp`; exit 1; }

while getopts "hHaAeEnNp:P:" OPC 2>/dev/null
do
   case "$OPC" in
      [aA]) PREPARE_ALL="true" ;;
      
      [eE]) PREPARE_ECLIPSE="true" ;;
      
      [nN]) PREPARE_NETBEANS="true" ;;

      [pP]) PROJECTS=$OPTARG ;;

      [hH]) Help ; exit ;;
      *) NeedHelp ;; 
    esac
done

echo "##################################################"
echo "####### Preparing development environment"
echo "##################################################"

if [ ! -z "$PREPARE_ALL" ]; then 

	echo ""
	echo "# ======================================="
    echo "# Installing dependencies"
    echo "# ======================================="

    sudo apt-get install -y \
        postgresql postgresql-client \
        maven \
        openjdk-8-jdk \
        curl \
        zip

	echo ""
	echo "# ======================================="
    echo "# Downloading Playframework"
    echo "# ======================================="

    VERSION=1.5.3
    PACKAGE=play-${VERSION}
    PACKAGE_ZIP=${PACKAGE}.zip

    echo "# Removing current"
    rm -rf ${PACKAGE_ZIP} ${PLAY_FOLDER}

    echo "# Downloading"
    curl https://downloads.typesafe.com/play/${VERSION}/${PACKAGE_ZIP} --output ${PACKAGE_ZIP}
    
    echo "# Unzipping"
    unzip ${PACKAGE_ZIP}
    rm -rf ${PACKAGE_ZIP} 

    echo "# Creating directories"    
    mkdir ${ROOT_DIR}/system
    mv ${PACKAGE} ${PLAY_FOLDER}
    
fi

##################################################

function prepareProjectsFolder {

    INSTALL=$1
    PROJECT_FOLDER=$2
    FOLDER_FILTER=$3

    PREPARE_CMD="mvn clean package -DskipTests"    
    if $INSTALL ; then
        PREPARE_CMD="mvn clean install package -DskipTests"
    fi

    cd ${PROJECT_FOLDER}

    if [ -z "$FOLDER_FILTER" ] ; then
          $PREPARE_CMD
    else
        for d in $FOLDER_FILTER ; do

            echo ""
            echo "# Project found: $d"
            echo ""
            
            cd $d
            mvn clean install package -DskipTests
            cd ..
        done
    fi
}

##################################################

function prepareCommons {
    echo ""
    echo "# ======================================="
    echo "# Preparing commons projects"
    echo "# ======================================="
    prepareProjectsFolder true ${COMMONS_FOLDER} "*-commons/"
}

##################################################

function prepareCoordinators {
    echo ""
    echo "# ======================================="
    echo "# Preparing coordinators projects"
    echo "# ======================================="
    prepareProjectsFolder true ${COORDINATORS_FOLDER} "api-*/"
    prepareProjectsFolder false ${COORDINATORS_FOLDER} "*-coordinator/"
}

##################################################

function preparePlugins {
    echo ""
    echo "# ======================================="
    echo "# Preparing plugins projects"
    echo "# ======================================="
    prepareProjectsFolder true ${PLUGINS_FOLDER} "api-*/"
    prepareProjectsFolder false ${PLUGINS_FOLDER} "plugin-*/"
}

##################################################

function prepareWeb {
    echo ""
    echo "# ======================================="
    echo "# Preparing web project"
    echo "# ======================================="

    cd ${WEB_FOLDER}

    # Use play from path if it was not downloaded before
    if [ -z `command -v ${PLAY_BIN}` ]; then
        echo "# Play folder from system not fount, setting default."
	    PLAY_BIN=play
    fi	

    export PATH=$PATH:${PLAY_FOLDER}/python

    echo "# Computing dependencies"    
    ${PLAY_BIN} deps

    if [ ! $PREPARE_ECLIPSE ] && [ ! $PREPARE_NETBEANS ] ; then
        PREPARE_ECLIPSE=true
        PREPARE_NETBEANS=true
    fi

    if [ ! -z "$PREPARE_ECLIPSE" ]; then 
        echo "# Preparing Eclipse environment"    
        ${PLAY_BIN} eclipsify
    fi    

    if [ ! -z "$PREPARE_NETBEANS" ]; then 
        echo "# Preparing Netbeans environment"    
        ${PLAY_BIN} netbeansify
    fi    
}

##################################################

echo ""
echo "# ======================================="
echo "# Preparing projects"
echo "# ======================================="

if [ -z "$PROJECTS" ]; then 
    prepareCommons
    prepareCoordinators
    preparePlugins
    prepareWeb
else

    PROJECTS=$(echo $PROJECTS | tr "," "\n")

    for PROJECT in $PROJECTS
    do    
        case "$PROJECT" in
            commons) prepareCommons ;;     
            coordinators) prepareCoordinators ;;       
            plugins) preparePlugins ;;
            web) prepareWeb ;;         
        esac
    done
fi

cd ${ROOT_DIR}


