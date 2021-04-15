# Challenge

##: computer: How to execute

 To run the program you need to be in the Test Inc. Centralized Payment System folder.
 Open the command line and execute this command: docker-compose up
 All necessary applications (includes my application) will be running in containers.
 If you want to produce data from Kafka, use this endpoint: http: // localhost: 9000 / start
 You can use this endpoint to see the logs: http: // localhost: 9000 / logs
 After sending a request to http: // localhost: 9000 / start endpoint, my application will start to process the payments and log the errors.
##: memo: Notes

  To consume data from Kafka, I use Kafka consumer.
  I have 2 scheduled methods that create consumers (for online and offline payments) and consumes the data.
  The application will not send requests to API gateway immediately while consuming the data. Scheduled tasks will persist payments into the database.
  I modified payments table by adding 2 columns (boolean): 1) processed (true / false) 2) valid (true / false)
  Processed means that the payment has been checked by sending a request to validation API (API GATEWAY)
  Valid means; payment has been checked and API GATEWAY responded with 2xx OK. So payment is valid.

  1) For offline payments:

   The application directly stores the offline payment information. It does not use API GATEWAY to validate the payment.
   Offline payment is stored with the values:
   Processed = true
   Valid = true

   There is only one check while storing offline payments;
   If there is no account with the given account id, payment will not be stored and an error log will be stored in the log_history table.
   
   
  2) For online payments:
   The application stores the online payment with the values ​​of processed = false and valid = false.
   Before storing there are 2 checks;
   1) Is there an account with a given account id? If yes, it passes the check. If no, an error log will be stored in the log_history table.
   2) Is payent id unique? If yes, it passes the check. If not, an error log will be stored in the log_history table.
   
   If an online payment passes those 2 controls, that means they are waiting to be processed by another scheduled task.
   
   Another scheduled task takes unprocessed (processed = false) online payments with the given size. Then it sends a request to API GATEWAY.
   If API GATEWAY responses with 2xx, the task updates the PROCESSED value as true and VALID value as true.
   If API GATEWAY doesn't response with 2xx, the task updates the PROCESSED value as true and VALID value as false. Then it creates an error log, stores it in the log_history table.
   
   
   ** For both online and offline payments, if payment is valid last payment date of the related account gets updated with the payment creation date.
   
  3) Logging
   There is a cloumn named posted (true/false) in the log_history table. In the beginning all log_history entries will be saved with processed = false value.
   Another scheduled task checks the log_history table, finds the unposted (posted = false) log entries with a given size and finally it tries to post log information to external log api.
   If external log API responses with 2xx, posted value for that log entry will be updated as true. If it doesn't responses with 2xx value will not be changed and the task will try to post it again in the next execution.
   
   

##: pushpin: Things to improve

1) I would write more unit tests. I could only write unit tests for my main implementation class (PaymentServiceImpl.java). I would write more if had more time.

2) I would spend more time searching and optimize database operations performance.

3) I would learn more about Kafka and Docker to use them in a more efficient way. I had only general knowledge about them before this task.

4) I would build a retry logic for online payments. If API gateway does not respond or gets time out another scheduled task would take failed payments and try again several times.