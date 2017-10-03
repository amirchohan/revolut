# Money transfer exercise

A RESTful API to transfer money between accounts.  
The accounts and transactions are all stored in an in-memory database and can be created at runtime for demo purposes.  

The API is written in Java 8 and uses Undertow's embedded REST server.

## Libraries used
- **Undertow** 1.4.20 - to create an embedded REST server.
- **Jackson** 2.9.1 - for JSON serialization and deserialization.
- **Jetty** 9.4.7 - Using Jetty's HTTPClient for integration testing of the RESTful API services.
- **JUnit** 4 - Unit testing

## Features
- Create an account.
- Deposit to or withdraw money from an account.
- Transfer money between accounts.
- List all transactions or accounts stored in the DB.
- Get all transactions for a given account.
- In-memory database to store all transactions and accounts.
- Error capturing and returning appropriate HTTP response codes.
- Creation of accounts and transactions at runtime for demo.
- Unit tests using TDD.

## Starting the applicaiton
1. Clone the repo and run the following command:


2. This will create a single fat jar which can be executed:

## Using the REST API
### Accounts
#### Creating an account  
To create an account it's a simple GET request: `http://localhost:8080/account/create`.
The server will respond with information about the created account in a JSON format:
```
{
  "id" : 1775907950,
  "balance" : 0,
  "transactions" : [ ]
}
```

#### Get an account
To get information on an account you can submit a GET request with the account Id in as a parameter in the URL: `http://localhost:8080/account/accId=1775907950`.
The server will respond with information about the quered account, which in this case will be same as above.

#### Get all accounts
To get information on all the accounts simply submit the following GET request: `http://localhost:8080/accounts`  
Example response:
```
{
"accounts" : [
{
  "id" : 1665399452,
  "balance" : 68463.76,
  "transactions" : [ {
    "id" : 795444722,
    "sourceAccId" : 1665399452,
    "destAccId" : 1239884141,
    "amount" : 61222.10,
    "successful" : true
  }, {
    "id" : 1724545278,
    "sourceAccId" : -1,
    "destAccId" : 1665399452,
    "amount" : 93135.15,
    "successful" : true
  } ]
},
{
  "id" : 1615791761,
  "balance" : 172253.41,
  "transactions" : [ {
    "id" : 1876833712,
    "sourceAccId" : 1615791761,
    "destAccId" : 1335519395,
    "amount" : 15815.4,
    "successful" : true
  }, {
    "id" : 1125679834,
    "sourceAccId" : -1,
    "destAccId" : 1615791761,
    "amount" : 88728.84,
    "successful" : true
  } ]
}
]
}
```

## Transfers
#### Deposit money
You can deposit money into an account by making a POST request providing the account Id and the amount you wish to deposit:
```
curl -XPOST http://localhost:8080/account/deposit -d
'{
  "destAccId" : 1791851470,
  "amount" : 180
}'
```
#### Withdraw money
Similarly, you can withdraw money from an account by making a POST request providing the account Id and the amount you wish to withdraw:
```
curl -XPOST http://localhost:8080/account/withdraw -d
'{
  "destAccId" : 1615791761,
  "amount" : 98.30
}'
```

#### Transfer money
To transfer money make a POST request providing the source account Id, destination account Id and the amount to transfer:
```
curl -XPOST http://localhost:8080/transfer -d
'{
  "sourceAccId" : 1665399452,
  "destAccId" : 1615791761,
  "amount" : 98.30
}'
```

#### Get all transactions
Finally, to get all the transactions in the DB simply submit the following GET request: `http://localhost:8080/transactions`

### Future possible improvements
- Using Mockito to perform unit testing using mocks
- Make the progream thread safe
- Implement logging
- Support for different currencies
- Transactions should have a timestamp
