FROM postgres:12
ENV POSTGRES_USER postgres
ENV POSTGRES_PASSWORD secretpassword
ENV POSTGRES_DB postgres
ADD init_db /docker-entrypoint-initdb.d/

# Migrations?