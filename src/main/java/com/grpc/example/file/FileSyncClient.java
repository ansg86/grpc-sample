package com.grpc.example.file;

import com.grpc.example.gen.FileGrpc;
import com.grpc.example.gen.FileInfo;
import com.grpc.example.gen.SyncRequest;
import com.grpc.example.gen.SyncResponse;
import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;

import java.util.List;
import java.util.concurrent.TimeUnit;

public class FileSyncClient {

    public static void main (String[] args) throws InterruptedException {
        String url = "localhost:50052";
        ManagedChannel channel = ManagedChannelBuilder.forTarget(url)
                .usePlaintext().build();
        FileSyncClient client = new FileSyncClient();
        SyncRequest syncRequest = client.buildSyncRequest();

        try {
            FileGrpc.FileBlockingStub blockingStub = FileGrpc.newBlockingStub(channel);
            SyncResponse syncResponse = blockingStub.syncFile(syncRequest);
            List<FileInfo> fileInfoList = syncResponse.getFileInfoList();
            System.out.println("--Related directory--");
            fileInfoList.stream().filter(FileInfo::getIsDirectory).forEach(System.out::println);
            System.out.println("--Related files--");
            fileInfoList.stream().filter(fileInfo -> !fileInfo.getIsDirectory()).forEach(System.out::println);
        } finally {
            channel.shutdown().awaitTermination(5, TimeUnit.SECONDS);
        }
    }

    public SyncRequest buildSyncRequest() {
        return SyncRequest.newBuilder().setUserName("user-1").build();
    }
}
