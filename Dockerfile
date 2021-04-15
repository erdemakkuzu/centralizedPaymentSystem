FROM openjdk:8
EXPOSE 8080
ADD target/centralized-payment-system.jar centralized-payment-system.jar
ENTRYPOINT ["java","-jar","/centralized-payment-system.jar"]