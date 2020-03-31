#!/bin/bash

set -o errexit
set -o pipefail
set -o nounset

# Paths to be mounted inside the Docker container
CONTAINER_GRADLE_PROPS_FILE=/nixer/gradle/gradle.properties
CONTAINER_SIGNING_SECRET_KEY_FILE=/nixer/signing/private.key

main() {

  checkScriptDependencies
  scanCommandLine "$@"
  checkParameters

  echo ""
  echo "==> Building publisher image..."
  echo ""

  PUBLISHER_IMAGE_ID=$(docker build -q .)

  echo ""
  echo "==> Publisher image built: ${PUBLISHER_IMAGE_ID}"
  echo ""

  PUBLISHER_CONTAINER_NAME="nixer-publish_${TAG_NAME}_$(date +"%FT%H-%M-%S")"

  echo ""
  echo "==> Running publisher container: ${PUBLISHER_CONTAINER_NAME}"
  echo ""
  echo "======================================================================================================================="

  docker container run \
    --volume "${GRADLE_PROPS_FILE}":"${CONTAINER_GRADLE_PROPS_FILE}" \
    --volume "${SIGNING_SECRET_KEY_FILE}":"${CONTAINER_SIGNING_SECRET_KEY_FILE}" \
    --env ENV_TAG_NAME="${TAG_NAME}" \
    --env ENV_GRADLE_PROPS_FILE="${CONTAINER_GRADLE_PROPS_FILE}" \
    --env ENV_SIGNING_SECRET_KEY_FILE="${CONTAINER_SIGNING_SECRET_KEY_FILE}" \
    --env ENV_DEBUG="${DEBUG:-}" \
    --name "${PUBLISHER_CONTAINER_NAME}" \
    "${PUBLISHER_IMAGE_ID}"

  echo "======================================================================================================================="
  echo ""
  echo "==> Publishing done."
  echo "==> The executed container has been preserved and can be accessed by name: ${PUBLISHER_CONTAINER_NAME}"
  echo ""
  echo "==> In order to see the logs use:"
  echo ""
  echo "     docker logs ${PUBLISHER_CONTAINER_NAME}"
  echo ""
}

checkScriptDependencies() {
    if ! command -v docker > /dev/null; then
        echo "ERROR: docker is not installed."
        exit 1
    fi
}

scanCommandLine() {

    # scan command line
    for arg in "$@"; do
        case "$arg" in
            --tag-name=*)
                TAG_NAME="${arg#*=}"
                ;;
            --gradle-props-file=*)
                GRADLE_PROPS_FILE="${arg#*=}"
                ;;
            --signing-secret-key-file=*)
                SIGNING_SECRET_KEY_FILE="${arg#*=}"
                ;;
            --debug)
                DEBUG="true"
                ;;
            --help)
                help
                exit 0
                ;;
            *)
                echo "Unknown parameter: $arg"
                help
                exit 1
        esac
    done

}

checkParameters() {
    if [[ -z ${TAG_NAME:-} ]]; then
        echo "ERROR: Missing mandatory parameter: --tag-name"
        help
        exit 1
    fi

    if [[ -z ${GRADLE_PROPS_FILE:-} ]]; then
        echo "ERROR: Missing mandatory parameter: --gradle-props-file"
        help
        exit 1
    fi

    if [[ -z ${SIGNING_SECRET_KEY_FILE:-} ]]; then
        echo "ERROR: Missing mandatory parameter: --signing-secret-key-file"
        help
        exit 1
    fi

    if [[ ! -f "${GRADLE_PROPS_FILE}" ]]; then
        echo "ERROR: Gradle properties file '${GRADLE_PROPS_FILE}' does not exist."
        help
        exit 1
    fi

    if [[ ! -f ${SIGNING_SECRET_KEY_FILE} ]]; then
        echo "ERROR: Signing secret file '${SIGNING_SECRET_KEY_FILE}' does not exist."
        help
        exit 1
    fi
}

help() {
    cat << EOF

Description:
    Initiates publishing 'nixer-spring-plugin' to Maven Repository.
    Builds and runs a Docker container inside which the actual publication is executed.

Usage:
    $(basename $0) [parameters] [--param=VALUE]
    $(basename $0) --help

    Mandatory parameters:
    --tag-name=[TAG_NAME]                                 - name of the git tag to be published
    --gradle-props-file=[GRADLE_PROPS_FILE]               - path to the 'gradle.properties' file to be used
    --signing-secret-key-file=[SIGNING_SECRET_KEY_FILE]   - path to the key file used for signing the published artefacts

    Optional parameters:
    --debug                     - logging in debug mode
    --help                      - displays this help
EOF

}

main "$@"
