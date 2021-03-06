# import config.
# You can change the default config with `make cnf="config_special.env" build`
cnf ?= config.env
include $(cnf)

# DOCKER TASKS
# Build the container
start_infra:
	$(MAKE) -C ${INFRA_FOLDER} start

stop_infra:
	$(MAKE) -C ${INFRA_FOLDER} stop

down_infra:
	$(MAKE) -C ${INFRA_FOLDER} down