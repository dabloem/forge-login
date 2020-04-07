FROM gitpod/workspace-full-vnc:latest

# Docker build does not rebuild an image when a base image is changed, increase this counter to trigger it.
ENV TRIGGER_REBUILD 2
USER gitpod

RUN brew install jboss-forge
