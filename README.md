
# Machine manager api

REST API for managing machines.

Swagger UI Documentation: https://machine-manager-production.up.railway.app/swagger-ui/index.html


## Build
Steps to build docker image:

1. Clone this repo:

         git clone https://github.com/tzdv/machine-manager.git

2. Navigate to source folder:

         cd machine-manager
3. Set environment variables for the database connection:

        DB_USERNAME=dbusername
        DB_PASSWORD=dbpassword
        DB_URL=mysql://hostname:port/dbname
        DB_NAME=dbname
        PORT=8080
4. Build the image :

         docker build -t machine-manager .
5. Run the containers:

         docker compose up
6. Once everything is succesfully started up, the application should listen on: http://localhost:8080/        