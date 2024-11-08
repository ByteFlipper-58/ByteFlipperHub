package com.byteflipper.hub;

import android.content.Context;

import androidx.work.Data;
import androidx.work.OneTimeWorkRequest;
import androidx.work.WorkManager;

import java.util.UUID;

public class DownloadManager {

    private UUID downloadWorkId;

    public void startDownload(Context context, String url, String filePath) {
        Data inputData = DownloadWorker.createInputData(url, filePath);
        OneTimeWorkRequest downloadRequest = new OneTimeWorkRequest.Builder(DownloadWorker.class)
                .setInputData(inputData)
                .build();
        downloadWorkId = downloadRequest.getId();

        WorkManager.getInstance(context).enqueue(downloadRequest);
    }

    public void pauseDownload() {
        if (downloadWorkId != null) {
            WorkManager.getInstance().cancelWorkById(downloadWorkId);
        }
    }

    public void cancelDownload() {
        if (downloadWorkId != null) {
            WorkManager.getInstance().cancelWorkById(downloadWorkId);
        }
    }

    public void resumeDownload(Context context, String url, String filePath) {
        startDownload(context, url, filePath);
    }
}