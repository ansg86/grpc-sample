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
        List<FileInfo> fileInfoList = generateFileInfoList();
        SyncResponse syncResponse = SyncResponse.newBuilder()
                .addAllFileInfo(fileInfoList).build();
        responseObserver.onNext(syncResponse);
        responseObserver.onCompleted();
        System.out.println("REQUESTED DONE " + request.getUserName());
    }

    public List<FileInfo> generateFileInfoList() {
        //TODO to be parameterized
        Path path = Paths.get("D:\\Programming\\grpc-test\\server");
        List<FileInfo> fileInfoList = new ArrayList<>();
        try {
            Files.walk(path).forEach(childPath -> {
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
                        .setFilePath(file.getAbsolutePath())
                        .setFileName(file.getName())
                        .setFileBytes(ByteString.copyFrom(Files.readAllBytes(childPath)))
                        .build();
            } else {
                return FileInfo.newBuilder()
                        .setIsDirectory(true)
                        .setFilePath(file.getAbsolutePath())
                        .setFileName(file.getName())
                        .build();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        return null;
    }


}
