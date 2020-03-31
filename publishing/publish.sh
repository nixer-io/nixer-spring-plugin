#!/bin/bash

set -o errexit
set -o pipefail
set -o nounset

main() {

  checkScriptDependencies
  checkParameters

  echo ""
  echo "------------------------------------------"
  echo "==> Attempting to publish tag: ${ENV_TAG_NAME}"
  echo "------------------------------------------"
  echo ""

  prepareSourceCode

  checkTag

  executeGradlePublish

  echo ""
  echo "------------------------------------------"
  echo "==> Tag ${ENV_TAG_NAME} published successfuly."
  echo "------------------------------------------"
  echo ""
}

checkScriptDependencies() {
    if ! command -v git > /dev/null; then
        echo "ERROR: git is not installed."
        exit 1
    fi

    if ! command -v java > /dev/null; then
        echo "ERROR: java is not installed."
        exit 1
    fi
}

checkParameters() {
    if [[ ! -f "${ENV_GRADLE_PROPS_DIR}/gradle.properties" ]]; then
        echo "PUBLISHING ERROR: '${ENV_GRADLE_PROPS_DIR}/gradle.properties' file does not exist."
        help
        exit 1
    fi

    if [[ ! -f ${ENV_SIGNING_SECRET_KEY_FILE} ]]; then
        echo "PUBLISHING ERROR: Signing secret file '${ENV_SIGNING_SECRET_KEY_FILE}' does not exist."
        help
        exit 1
    fi

    if [[ -z ${ENV_TAG_NAME:-} ]]; then
        echo "PUBLISHING ERROR: Missing mandatory environment variable: ENV_TAG_NAME"
        help
        exit 1
    fi
}

prepareSourceCode() {
  git clone --branch "$ENV_TAG_NAME"  https://github.com/nixer-io/nixer-spring-plugin.git

  cd nixer-spring-plugin

  echo ""
  echo "--> Checked out repository: $(git config --get remote.origin.url)"
  echo ""
}

checkTag() {
  CURRENT_TAG=$(git describe --exact-match)

  if [[ "$CURRENT_TAG" != "$ENV_TAG_NAME" ]]; then
    echo ""
    echo "==> ERROR: The checked out tag '${CURRENT_TAG}' does not match the tag to be published: '${ENV_TAG_NAME}'"
    echo "==>        Make sure the specified tag is the latest annotated tag pointing the commit you want to publish."
    echo "==>        Lightweight tags are not supported."
    echo ""

    echo "--> Details:"
    echo "---"
    git log -1
    echo "---"
    echo ""

    echo "==> Publishing aborted."
    exit 1
  else
    echo ""
    echo "--> Publishing tag '${ENV_TAG_NAME}':"
    echo "---"
    git log -1
    echo "---"
    echo ""
  fi
}

executeGradlePublish() {

  DEBUG_MODE=$([ "$ENV_DEBUG" == "true" ] && echo "--debug -S" || echo "")

  # The 'org.gradle.internal.publish.checksums.insecure' flag disables generating sha256 and sha512 checksums
  # as they are not supported by the OSSRH Maven repository and cause issues when uploaded.

  ./gradlew \
    --no-daemon \
    -Dorg.gradle.internal.publish.checksums.insecure=true \
    -Dgradle.user.home="$ENV_GRADLE_PROPS_DIR" \
    -Psigning.secretKeyRingFile="$ENV_SIGNING_SECRET_KEY_FILE" \
    $DEBUG_MODE \
    clean \
    publishNixerSpringPluginPublicationToMavenRepository

}

help() {
    cat << EOF

Description:
    Publishes 'nixer-spring-plugin' to Maven Repository using Gradle.
    Each execution clones from VCS a fresh copy of the source code to be used for publishing.

Usage:
    $(basename $0)
    $(basename $0) --help

    Mandatory environment variables:
    ENV_TAG_NAME                  - name of the git tag to be published
    ENV_GRADLE_PROPS_DIR          - path to the directory with 'gradle.properties' file to be used
    ENV_SIGNING_SECRET_KEY_FILE   - path to the key file used for signing the published artefacts

    Optional environment variables:
    ENV_DEBUG                    - when set to 'true' logging in debug mode is active

    Optional parameters:
    --help                      - displays this help
EOF

}

main "$@"
