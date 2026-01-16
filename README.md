This car wash project is a spring boot application developed by
James Mwaura.

[Prerequisite](#prerequisite)

[Getting started](#getting-started)

[Work to be done](#work-to-be-done)

[Creating entities](#creating-entities)

[Image storage](#image-storage)

[Google cloud tools](#google-cloud-tools)

[Contribution](#contribution)

[Continuous work](#continuous-work)




# Prerequisite
You will need the following

1. An IDE that you're comfortable with for creating Java applications. If you are new I recommend -  https://www.eclipse.org/downloads/packages/release/2022-03
2. Visual studio IDE for Angular applications - https://code.visualstudio.com/download
3. Node js - https://nodejs.org/en/download/
4. Windows Powershell - https://docs.microsoft.com/en-us/powershell/scripting/install/installing-powershell-on-windows?view=powershell-7.2
5. Angular CLI - https://angular.io/guide/setup-local
6. Git tools - https://gitforwindows.org/
7. Postman - https://www.postman.com/downloads/
8. PostgreSQL for database - https://www.postgresql.org/download/


# Getting started
Please clone the java starter app and run it on your preferred IDE.

# Work to be done
## Creating entities
Create category and product entities that match the database schema.
package (com.mkithub.ecommerce.entity)

# Image storage
The following will help you to process images for the app.
A google cloud bucket has already been created.
Vit the link below to practice on how to create a bucket.
https://console.cloud.google.com

## Google cloud tools
Install google cloud tools
https://cloud.google.com/eclipse/docs/libraries

# Contribution
Please explain to other developers the changes you have made on the app.

James Mwaura
I have used spring initializer to create a spring boot application.

# Continuous work
billing added
gcloud beta billing projects describe gladways-car-wash
$ gcloud alpha billing projects link gladways-car-wash-460113 --billing-account=01539F-0DFFBC-C2233D
billingAccountName: billingAccounts/01539F-0DFFBC-C2233D
billingEnabled: true
name: projects/gladways-car-wash-460113/billingInfo
projectId: gladways-car-wash-460113

Associated the app with existing service account
Crated a new secret key
Deployed 17/05/25

hosting on Heroku on 03/06/25
heroku config:set origins='https://revenue-frontend-50f50aca8eea.herokuapp.com,http://localhost:4200' --app revenue-backend
added google-credentials.json

pom causing trouble

