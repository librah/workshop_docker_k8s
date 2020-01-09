IMAGE_NAME := docker-registry.linecorp.com/line-taiwan/gln/docker_workshop/hello_lw#####
IMAGE_TAG := latest

image:
	mvn package
	docker build -t $(IMAGE_NAME):$(IMAGE_TAG) .
	docker push $(IMAGE_NAME):$(IMAGE_TAG)
