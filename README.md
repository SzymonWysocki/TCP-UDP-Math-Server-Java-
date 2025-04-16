# TCP-UDP-Math-Server-Java-

This project is a multi-threaded server application built in Java that uses TCP for handling client connections and UDP for service discovery. Clients can send basic arithmetic operations (ADD, SUB, MUL, DIV) with two integers. The server processes the request, returns the result (or an error), and keeps detailed global and 10-second rolling statistics.

Key features:

TCP-based command execution with multi-client support

UDP discovery mechanism (CCS DISCOVER / CCS FOUND)

Real-time and time-window statistics logging

Error handling and operation validation
