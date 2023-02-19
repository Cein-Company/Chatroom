# Chatroom

> A simple Chatroom using `Java Socket Programming`

![Chatroom](https://user-images.githubusercontent.com/107719378/185530419-283d43d8-46a9-46dc-85b1-7e8aa1030015.png)

---

## Table of Contents

- [Description](#description)
- [How To Use](#how-to-use)
- [Features](#features)
  - [Signing up & Logging in](#signing-up--logging-in)
  - [Message History](#message-history)
  - [Private Messages](#private-messages)
  - [Chatroom Polls](#chatroom-polls)
  - [Kick & Ban](#kick--ban)
- [Creators Info](#creators-info)

---

## Description

A simple Chatroom which uses `Java Socket Programming` to manage multiple clients inside a server, allowing them to
interact with each other, and allowing the server administrator to manage the chatroom and its clients.

[Back To The Top](#chatroom)

---

## How To Use

To open the chatroom, you must first turn on the server by running `ChatServer`.

Then, each client can enter the chatroom by running the `ChatClientCLI` and signing up or logging into an account.

Multiple clients can join the server and start chatting with each other.

      NOTE: To open multiple clients from the save system, you must configure Intellij Idea
      to "Allow Multiple Instances" of a file to be runned at the same time.

[Back To The Top](#chatroom)

---

## Features

### Signing up & Logging in

<p align="center" width="100%">
    <img width="30%" src="https://user-images.githubusercontent.com/107719378/219869760-0c1ce483-a021-4908-adbb-f9897691f87e.png">
</p>

Each client can create their own personal account with a unique username and a password, and they can log back into
their account after they leave.

        NOTE: Clients cannot enter an account if the account is already online in the server.

### Message History

<p align="center">
    <img width="40%" src="https://user-images.githubusercontent.com/107719378/219869975-40a0f569-3989-41d9-a3fb-86ecbc2788ab.png">
</p>

All messages exchanged in the chatroom are saved so that when a new user enters the chatroom, or the server is turned
off & on, the message history can be accessed.

### Private Messages

Clients can send a private message to each other users using the command `/message username 'message to be sent'`.

They can also send a private message to the Server Administrator by using the
command `/message admin 'message to be sent'`.

Private messages won't be shown to other users, and won't be saved into the log files.

### Chatroom Polls

<p align="center">
    <img width="70%" src="https://user-images.githubusercontent.com/107719378/219870688-be1b5fee-1c26-4c9e-bca5-9656a91bfbc9.png">
</p>

The Server Administrator has the ability to create a poll with multiple options inside the chatroom for the users to
vote to.

### Kick & Ban

The Server Administrator has the ability to kick out or ban any users that don't follow the chatroom rules.

### Chatroom Commands

Both the Server Administrator and the clients have access to a variety of commands to help them do what they want.

> Here's a list of available Admin Commands:
    <details>
      <summary>
        <code style="font-size: 14px;">Admin Commands</code>
      </summary>
          <p><code>/help</code> => To see a list of available commands</p>
          <p><code>/log</code> => To see a list of all messages</p>
          <p><code>/log -s address</code> => To save all messages in a specific address inside the system</p>
          <p><code>/log -c</code> => To clear the message history</p>
          <p><code>/members</code> => To see a list of all chatroom members</p>
          <p><code>/members -o</code> => To see a list of all online chatroom members</p>
          <p><code>/message username 'Your Message'</code> => To send a private message to a user</p>
          <p><code>/message all 'Your Message'</code> => To send a message to everyone in the Chatroom</p>
          <p><code>/kick username</code> => To kick a user from the chatroom temporarily</p>
          <p><code>/ban username</code> => To ban a user from entering the chatroom forever</p>
          <p><code>/ban username -u</code> => To unban a banned user</p>
          <p><code>/poll -create -t {Your Title Here} -o {First Option} -o {Second Option} ... -uname uniqueName</code></p>
          <p><code>/poll -show uniqueName</code> <code>/poll -show pollID</code></p>
          <p><code>/poll -show-all</code> <code>/poll -show-all-details</code></p>
          <p><code>/poll -end uniqueName</code> <code>/poll -end pollID</code> => To create or see Chatroom Polls</p>
          <p><code>/shutdown</code> => To close and shutdown the server</p>
    </details>
> 
> And here are the Commands available to the Clients:
     <details>
      <summary>
        <code style="font-size: 14px;">Client Commands</code>
      </summary>
          <p><code>/help</code> => To see a list of available commands</p>
          <p><code>/message username 'Your Message'</code> => To send a private message to a user</p>
          <p><code>/message admin 'Your Message'</code> => To send a private message to the server administer</p>
          <p><code>/poll -join uniqueName -v optionIndex</code></p>
          <p><code>/poll -show uniqueName</code> <code>/poll -show pollID</code></p>
          <p><code>/poll -show-all</code> <code>/poll -show-all-details</code> => To join or see Chatroom Polls</p>
          <p><code>/exit</code> => To exit the chatroom</p>
    </details>


[Back To The Top](#chatroom)
  
---

## Creators Info

This chatroom was developed by the Cein Company.

Cein (Computer Engineers In Noshirvani) is a company formed by a team of developers inside the Babol Noshirvani
University of Technology (NIT or BNUT) to help each other in learning and developing interesting projects.

We would appreciate any comments or thoughts that would help us improve our program. And if you encounter any issues while using the Chatroom, we would be more than happy to be informed about it. 

[Back To The Top](#chatroom)
