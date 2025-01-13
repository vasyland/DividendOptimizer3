# Dividend Optimizer 3

SpringBoot, REST, Spring Security, JWT
July 10, 2024: branch J10-Security - fixed issues with secrurity configurations



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
2. 

```

### Disabling SSH for development
```
1. Comment ssl section in application.xml file
2. Change https to http
 security:
    cors: https://localhost:5004 

```
