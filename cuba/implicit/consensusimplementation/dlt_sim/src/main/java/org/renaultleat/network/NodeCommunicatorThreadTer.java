package org.renaultleat.network;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

public class NodeCommunicatorThreadTer extends Thread {
    private NodeCommunicatorTer nodeCommunicatorTer;
    private Socket socket;
    private PrintWriter printWriter;

    public NodeCommunicatorThreadTer(Socket socket, NodeCommunicatorTer nodeCommunicatorTer) {
        this.nodeCommunicatorTer = nodeCommunicatorTer;
        this.socket = socket;
    }

    public void run() {
        try {
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(this.socket.getInputStream()));
            this.printWriter = new PrintWriter(socket.getOutputStream(), true);
            while (true) {
                nodeCommunicatorTer.sendMessage(bufferedReader.readLine());
            }
        } catch (Exception e) {
            nodeCommunicatorTer.getnodeCommunicatorThread().remove(this);
        }
    }

    public PrintWriter getPrintWriter() {
        return printWriter;
    }

}
