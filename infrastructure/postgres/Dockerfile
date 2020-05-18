FROM postgres:12
# TODO Add PW and USER to environment variables
ENV POSTGRES_USER postgres 
ENV POSTGRES_PASSWORD secretpassword
ENV POSTGRES_DB postgres
ADD init_db /docker-entrypoint-initdb.d/

# Migrations?