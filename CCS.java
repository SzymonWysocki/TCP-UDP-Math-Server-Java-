import java.io.*;
import java.net.*;
import java.util.Arrays;
import java.util.Random;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class CCS {

    public static int usersCounter = 0;
    public static int operNum = 0;
    public static int addNub = 0;
    public static int divNub = 0;
    public static int mulNub = 0;
    public static int subNum = 0;
    public static int errorNum = 0;
    public static int sumNum = 0;
    public static int usersCounter10 = 0;
    public static int operNum10 = 0;
    public static int addNub10 = 0;
    public static int divNub10 = 0;
    public static int mulNub10 = 0;
    public static int subNum10 = 0;
    public static int errorNum10 = 0;
    public static int sumNum10 = 0;

    public static int port;
    public static ExecutorService executorService;
    public static Socket clientSocket;
    public static void main(String[] args) {
        if(args.length == 1){
            port = Integer.parseInt(args[0]);
        }else{
            System.out.println("Nie poprawna ilosc args");
        }
        executorService = Executors.newCachedThreadPool();
        udp();
        tcp();
        stats();

    }
    public static void udp(){
        new Thread(() -> {
            try {
                DatagramSocket socket = new DatagramSocket(port);
                byte[] buffRec = new byte[1024];
                DatagramPacket packet = new DatagramPacket(buffRec, buffRec.length);

                while(true){
                    socket.receive(packet);
                    String received = new String(packet.getData(), 0, packet.getLength());

                    if(received.startsWith("CCS DISCOVER")){
                        int receivedPort = packet.getPort();
                        InetAddress address = packet.getAddress();
                        byte[] buffSend = "CCS FOUND".getBytes();
                        DatagramPacket packetSend = new DatagramPacket(buffSend, buffSend.length, address, receivedPort);
                        socket.send(packetSend);
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }).start();
    }
    public static void tcp(){
        new Thread(() -> {
            try {
                ServerSocket serverSocket = new ServerSocket(port);
                while(true){
                    clientSocket = serverSocket.accept();
                    usersCounter++;
                    usersCounter10++;
                    new Thread(()->{
                        try {
                            InputStream in = clientSocket.getInputStream();
                            OutputStream out = clientSocket.getOutputStream();
                            InputStreamReader isr = new InputStreamReader(in);
                            OutputStreamWriter osw = new OutputStreamWriter(out);
                            BufferedReader br = new BufferedReader(isr);
                            BufferedWriter bw = new BufferedWriter(osw);

                            while (true) {
                                String[] recived;
                                try {
                                    String[] recived1 = br.readLine().split(" ");
                                    recived = recived1;
                                }catch (SocketException e){
                                    break;
                                }
                                boolean errorStatus = false;
                                int arg1 = 0;
                                int arg2 = 0;
                                int resault = 0;

                                if (recived.length != 3) {
                                    System.out.println("Recived: " +  recived[0] + " " + arg1 +" "+ arg2);
                                    System.out.println("result: ERROR");
                                    bw.write("ERROR");
                                    bw.newLine();
                                    bw.flush();
                                } else {
                                    try {
                                        arg1 = Integer.parseInt(recived[1]);
                                        arg2 = Integer.parseInt(recived[2]);
                                    } catch (Exception e) {
                                        errorStatus = true;
                                    }
                                    switch (recived[0]) {
                                        case "ADD": {
                                            addNub++;
                                            addNub10++;
                                            resault = arg1 + arg2;break;
                                        }
                                        case "DIV": {
                                            divNub++;
                                            divNub10++;
                                            if (arg2 != 0) {
                                                resault = arg1 / arg2;
                                            } else {errorStatus = true;}break;
                                        }
                                        case "SUB": {
                                            subNum++;
                                            subNum10++;
                                            resault = arg1 - arg2;break;}
                                        case "MUL": {
                                            mulNub++;
                                            mulNub10++;
                                            resault = arg1 * arg2;break;
                                        }
                                        default: {errorStatus = true;break;
                                        }
                                    }

                                    System.out.println("Recived: " +  recived[0] + " " + arg1 +" "+ arg2);

                                    if(!errorStatus) {
                                        operNum++;
                                        operNum10++;
                                        sumNum += resault;
                                        sumNum10 += resault;
                                        System.out.println("result: " + resault);
                                        bw.write(String.valueOf(resault));
                                        bw.newLine();
                                        bw.flush();
                                    }else{
                                        operNum++;
                                        operNum10++;
                                        errorNum++;
                                        errorNum10++;
                                        System.out.println("result: ERROR");
                                        bw.write("ERROR");
                                        bw.newLine();
                                        bw.flush();
                                    }
                                }
                            }
                        }catch(Exception e){
                            System.out.println("tutaj:D");
                            try {
                                clientSocket.close();
                            } catch (IOException ex) {
                                throw new RuntimeException(ex);
                            }
                            e.printStackTrace();
                        }
                    }).start();
                }
            }catch(Exception e){
                e.printStackTrace();
            }
        }).start();
    }
    public static void stats(){
        new Thread(()->{
            while (true) {
                try {
                    Thread.sleep(10000);
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
                StringBuilder sb = new StringBuilder();

                sb.append("=== Global Statistics ===\n");
                sb.append("Users Counter: ").append(usersCounter).append("\n");
                sb.append("Total Operations: ").append(operNum).append("\n");
                sb.append("Add: ").append(addNub).append("\n");
                sb.append("Div: ").append(divNub).append("\n");
                sb.append("Mul: ").append(mulNub).append("\n");
                sb.append("Sub: ").append(subNum).append("\n");
                sb.append("Errors: ").append(errorNum).append("\n");
                sb.append("Sum of Numbers: ").append(sumNum).append("\n\n");

                sb.append("=== Statistics (Last 10 Seconds) ===\n");
                sb.append("Users Counter (Last 10s): ").append(usersCounter10).append("\n");
                sb.append("Total Operations (Last 10s): ").append(operNum10).append("\n");
                sb.append("Add (Last 10s): ").append(addNub10).append("\n");
                sb.append("Div(Last 10s): ").append(divNub10).append("\n");
                sb.append("Mul (Last 10s): ").append(mulNub10).append("\n");
                sb.append("Sub (Last 10s): ").append(subNum10).append("\n");
                sb.append("Errors (Last 10s): ").append(errorNum10).append("\n");
                sb.append("Sum of Numbers (Last 10s): ").append(sumNum10).append("\n");

                System.out.println(sb);

                usersCounter10 = 0;
                operNum10 = 0;
                addNub10 = 0;
                divNub10 = 0;
                mulNub10 = 0;
                subNum10 = 0;
                errorNum10 = 0;
                sumNum10 = 0;
            }
        }).start();
    }
}