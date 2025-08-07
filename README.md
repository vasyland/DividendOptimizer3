portfolio_calculation checked out

# Dividend Optimizer 3

SpringBoot, REST, Spring Security, JWT
July 10, 2024: branch J10-Security - fixed issues with secrurity configurations


## Docker setup

1. Build jar file using Gradle Tasks: bootJar
2. Navigate to the folder where Dockerfile is
	cd C:\Users\vasyl\git\DividendOptimizer3
3. Build Docker image
	docker build -t optimizer .
4. Run docker image
    docker run --name opt1 -p 8081:8081 optimizer 
    
    docker exec -it opt1 /bin/bash
    
5. TResting local mysql from docker
docker run --rm -it --network host mysql:8 mysql -h host.docker.internal -P 3306 -u root -p





###Tables used by application
```
1. user_info, user_info_seq - user information, encrypted passwords, and role.
   There are two roles ROLE_USER, ROLE_ADMIN. ROLE_ADMIN is not used yet.

```

###Setting SSL Communication
```
1. Generate .jks file using keytool. Keytool is a part of java.
cd C:\AV\Tutorials\w2024\DividendOptimizer3
keytool -genkey -alias divoptimizer3_key -storetype PKCS12 -keyalg RSA -keysize 2048 -keystore divoptimizer3.jks -validity 3650
password: changeit
what is your first and last name? iwm
What is the name of your organizational unit? home
What is the name of your organization? canada ltd
What is the name of your CIty or Locality? north york
What is the name of your State or Province? on
What is the two-letter country code for this unit? ca
Is CN=iwm, OU=home, O=canada ltd, L=north york, ST=on, C=ca correct? y

Generating 2,048 bit RSA key pair and self-signed certificate (SHA384withRSA) with a validity of 3,650 days
        for: CN=iwm, OU=home, O=canada ltd, L=north york, ST=on, C=ca
```

###Setting application to run SSL
```
TBD

```

### ###Setting application to run SSL for local development
```
TBD

```


### Refresh Token Management
```
TBD
```

### System Requirements
```
C:\AV\WorkProjects\DividendOptimizer3>java -version
	openjdk version "21.0.2" 2024-01-16
	OpenJDK Runtime Environment (build 21.0.2+13-58)
	OpenJDK 64-Bit Server VM (build 21.0.2+13-58, mixed mode, sharing)

   Download for your OS from here: https://jdk.java.net/archive/

   set JAVA_HOME=C:\Tools\jdk-21.0.2


Gradle 8.8 it is donloaded during the build but if you want to have it on your laptop to use with eclipse
Download for your OS from here: https://gradle.org/releases/

Eclipse Version: 2024-03 (4.31.0) or any newer for your OS: https://www.eclipse.org/
but it looks like the link is broken. Check it later.
```

### DividendOptimizer3 Build and Run
```
1. Create a folder where you will check DividendOptimizer project from the github: https://github.com/vasyland/DividendOptimizer3

2. Create a folder: C:\tmp\JavaProject
   cd C:\tmp\JavaProject
   git clone git@github.com:vasyland/DividendOptimizer3.git
   
3. Replace application.yml file with provided in skype in the folder: 
   C:\tmp\JavaProject\DividendOptimizer3\src\main\resources
   
4. Run gradle command to buid:
   cd C:\tmp\JavaProject\DividendOptimizer3
   For Linux: ./gradlew build
   For WIndows: gradle.bat build

   The jar file will be built in the folder: C:\tmp\JavaProject\DividendOptimizer3\build\libs

3. Run the project: 
   cd C:\tmp\JavaProject\DividendOptimizer3\build\libs
   java -jar DividendOptimizer3-0.0.3-SNAPSHOT.jar

   Put full path to your java if you don't have JAVA_HOME set.

4. From the Postman test with following: 
   https://localhost:8081/free/greeting
   
   Response: {
    "id": 1,
    "content": "Hello, World!"
   }

5. Test login GET https://localhost:8081/sign-in
	with user: "gleb@gmail.com" with password "password"
	Response:
	{
    "id": 102,
    "access_token": "eyJhbGciOiJSUzI1NiJ9.eyJpc3MiOiJpd20zIiwic3ViIjoiZ2xlYkBnbWFpbC5jb20iLCJleHAiOjE3Mzc5MjQ1NTAsImlhdCI6M.....tiSi1YEWi-5FQFJeqP1t47fNQNwOapsSXv5RNWSvRbK8Frw",
    "access_token_expiry": 900,
    "token_type": "Bearer",
    "user_name": "gleb",
    "subscription_end_date": "2025-01-29"
   }
```

# Test changes

