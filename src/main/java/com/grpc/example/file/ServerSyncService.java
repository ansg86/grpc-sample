package com.grpc.example.file;

import com.google.protobuf.ByteString;
import com.grpc.example.gen.FileGrpc;
import com.grpc.example.gen.FileInfo;
import com.grpc.example.gen.SyncRequest;
import com.grpc.example.gen.SyncResponse;
import io.grpc.stub.StreamObserver;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

public class ServerSyncService extends FileGrpc.FileImplBase {

    @Override
    public void syncFile(SyncRequest request,
                         StreamObserver<SyncResponse> responseObserver) {
        System.out.println("REQUESTED FROM " + request.getUserName());
        Path serverPath = Paths.get("D:\\Programming\\grpc-test\\server");
        List<FileInfo> fileInfoList = generateFileInfoList(serverPath);
        SyncResponse syncResponse = SyncResponse.newBuilder()
                .addAllFileInfo(fileInfoList).build();
        responseObserver.onNext(syncResponse);
        responseObserver.onCompleted();
        System.out.println("REQUESTED DONE " + request.getUserName());
    }

    public List<FileInfo> generateFileInfoList(Path pathToSeek) {
        List<FileInfo> fileInfoList = new ArrayList<>();
        try {
            Files.walk(pathToSeek, 1)
                    .filter(myPath -> !myPath.equals(pathToSeek))
                    .forEach(childPath -> {
                        FileInfo fileInfo = generateFileInfo(childPath);
                        fileInfoList.add(fileInfo);
                    });
        } catch (IOException e) {
            e.printStackTrace();
        }

        return fileInfoList;
    }

    public FileInfo generateFileInfo(Path childPath) {
        File file = childPath.toFile();
        try {
            if (file.isFile()) {
                return FileInfo.newBuilder()
                        .setIsDirectory(false)
                        .setFileName(file.getName())
                        .setFileBytes(ByteString.copyFrom(Files.readAllBytes(childPath)))
                        .build();
            } else {
                return FileInfo.newBuilder()
                        .setIsDirectory(true)
                        .setFileName(file.getName())
                        .addAllFileInfoChild(generateFileInfoList(childPath))
                        .build();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        return null;
    }


}
