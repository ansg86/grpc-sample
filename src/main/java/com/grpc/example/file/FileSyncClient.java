package com.grpc.example.file;

import com.grpc.example.gen.FileGrpc;
import com.grpc.example.gen.FileInfo;
import com.grpc.example.gen.SyncRequest;
import com.grpc.example.gen.SyncResponse;
import com.grpc.example.util.PropsUtil;
import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Properties;
import java.util.concurrent.TimeUnit;

public class FileSyncClient {

    private final static String CLIENT_PATH = "client.path";

    public static void main(String[] args) throws InterruptedException {
        String url = "localhost:50052";
        ManagedChannel channel = ManagedChannelBuilder.forTarget(url)
                .usePlaintext().build();
        FileSyncClient client = new FileSyncClient();
        SyncRequest syncRequest = client.buildSyncRequest();
        Properties properties = PropsUtil.readProperties();
        Path clientPath = Paths.get(properties.getProperty(CLIENT_PATH));

        try {
            FileGrpc.FileBlockingStub blockingStub = FileGrpc.newBlockingStub(channel);
            SyncResponse syncResponse = blockingStub.syncFile(syncRequest);
            List<FileInfo> fileInfoList = syncResponse.getFileInfoList();
            generateDirAndFile(clientPath, fileInfoList);
            System.out.println("Sync completed");
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            channel.shutdown().awaitTermination(5, TimeUnit.SECONDS);
        }
    }

    private static void generateDirAndFile(Path path, List<FileInfo> fileInfoList) throws IOException {
        for (FileInfo fileInfo : fileInfoList) {
            Path pathToBeCreate = path.resolve(fileInfo.getFileName());
            if (!fileInfo.getIsDirectory()) {
                Files.write(pathToBeCreate, fileInfo.getFileBytes().toByteArray());
            } else {
                Files.createDirectories(pathToBeCreate);
                generateDirAndFile(pathToBeCreate, fileInfo.getFileInfoChildList());
            }
        }
    }

    public SyncRequest buildSyncRequest() {
        return SyncRequest.newBuilder().setUserName("user-1").build();
    }
}
