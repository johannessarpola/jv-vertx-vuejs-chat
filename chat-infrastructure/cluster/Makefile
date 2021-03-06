# import config.
# You can change the default config with `make cnf="config_special.env" build`
#cnf ?= config.env
#include $(cnf)

# DOCKER TASKS
# Build the container
start:
	@echo Starting infrastructure
	docker-compose up -d

stop:
	@echo Stopping infrastructure
	docker-compose stop

down:
	@echo Tearing down infrastructure
	docker-compose down