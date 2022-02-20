package com.grpc.example.file;

import io.grpc.Server;
import io.grpc.ServerBuilder;

import java.io.IOException;

public class FileSyncServer {

    private void start() throws IOException, InterruptedException {

        /* The port on which the server should run */
        int port = 50052;
        Server server = ServerBuilder.forPort(port)
                .addService(new ServerSyncService())
                .build()
                .start();
        System.out.println("Server started, listening on " + port);
        if (server != null) {
            server.awaitTermination();
        }
    }

    public static void main(String[] args) throws IOException, InterruptedException {
        FileSyncServer fileSyncServer = new FileSyncServer();
        fileSyncServer.start();
    }
}
