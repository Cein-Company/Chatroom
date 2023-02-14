# Chatroom

>A simple Chatroom using `Java Socket Programming`



![Chatroom](https://user-images.githubusercontent.com/107719378/185530419-283d43d8-46a9-46dc-85b1-7e8aa1030015.png)


---

## Table of Contents

- [Description](#description)
- [How To Use](#how-to-use)
- [Features](#features)
    - [Signing up & Logging in](#signing-up-and-logging-in)
    - [Private Messages](#private-messages)
- [Features For The Future](#features-for-the-future)
- [Contibuters Info](#contibuters-info)

---

## Description

A simple Chatroom which uses `Java Socket Programming` to manage multiple clients inside a server, allowing them to interact with each other, 
and allowing the server administrator to manage the chatroom and its clients.

[Back To The Top](#chatroom)

---

## How To Use

To open the chatroom, you must first turn on the server by running the `ChatServer.class` file.

Then, each client can enter the chatroom by running the `ChatClientCLI.class` file and signing up or logging into an account. 

Multiple clients can join the server and start chatting with each other.

[Back To The Top](#chatroom)

---

## Features

### Signing up and Logging in

Each client can create their own personal account with a unique username and a password, and they can log back into their account after they leave.

        NOTE: Clients cannot enter an account if the account is already online in the server.
        
### Saving the messages

all messages exchanged in the chatroom will be saved in a file, so that when a new uses enters the chatroom, or the server is turned off & on, a message history can be accessed.

### Private Messages

Clients can send a private message to other users using the command `/message username 'message to be sent'`.

They can also send a private message to the server administrator by using the command `/message server 'message to be sent'`.

Private messages won't be shown to other users, and won't be saved into the log files.

[Back To The Top](#chatroom)
  
---

## Features For The Future
  
Here are some of the features that will be added to the chatroom in near future:
    
#### Poll
    A command will be introduced in the near future, allowing the server administrator to create a poll with multiple options, 
    and allowing each client to be able to join or deny entering the poll. 

#### GUI
    Currently, the chatroom uses the command line to operate. In the future, the chatroom might use a Graphical user interface instead.
    
[Back To The Top](#chatroom)
  
---

## Contibuters Info

This chatroom was developed by the Cein Company.

[Back To The Top](#chatroom)
