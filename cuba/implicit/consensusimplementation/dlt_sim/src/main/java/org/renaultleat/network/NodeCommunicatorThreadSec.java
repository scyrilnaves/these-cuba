package org.renaultleat.network;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

public class NodeCommunicatorThreadSec extends Thread {
    private NodeCommunicatorSec nodeCommunicatorSec;
    private Socket socket;
    private PrintWriter printWriter;

    public NodeCommunicatorThreadSec(Socket socket, NodeCommunicatorSec nodeCommunicatorSec) {
        this.nodeCommunicatorSec = nodeCommunicatorSec;
        this.socket = socket;
    }

    public void run() {
        try {
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(this.socket.getInputStream()));
            this.printWriter = new PrintWriter(socket.getOutputStream(), true);
            while (true) {
                nodeCommunicatorSec.sendMessage(bufferedReader.readLine());
            }
        } catch (Exception e) {
            nodeCommunicatorSec.getnodeCommunicatorThread().remove(this);
        }
    }

    public PrintWriter getPrintWriter() {
        return printWriter;
    }

}
