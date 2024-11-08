package com.byteflipper.hub;

import android.content.Context;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.work.Data;
import androidx.work.Worker;
import androidx.work.WorkerParameters;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.RandomAccessFile;
import java.net.HttpURLConnection;
import java.net.URL;

public class DownloadWorker extends Worker {

    private static final String TAG = "DownloadWorker";
    private static final int BUFFER_SIZE = 1024 * 4;
    private static final String DOWNLOAD_URL = "download_url";
    private static final String FILE_PATH = "file_path";
    private static final String DOWNLOAD_PROGRESS = "download_progress";
    private boolean isPaused = false;
    private boolean isCancelled = false;

    public DownloadWorker(@NonNull Context context, @NonNull WorkerParameters params) {
        super(context, params);
    }

    public static Data createInputData(String downloadUrl, String filePath) {
        return new Data.Builder()
                .putString(DOWNLOAD_URL, downloadUrl)
                .putString(FILE_PATH, filePath)
                .build();
    }

    @NonNull
    @Override
    public Result doWork() {
        String downloadUrl = getInputData().getString(DOWNLOAD_URL);
        String filePath = getInputData().getString(FILE_PATH);

        if (downloadUrl == null || filePath == null) {
            return Result.failure();
        }

        File file = new File(filePath);
        long downloadedBytes = file.exists() ? file.length() : 0;

        try (RandomAccessFile outputFile = new RandomAccessFile(file, "rw")) {
            URL url = new URL(downloadUrl);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestProperty("Range", "bytes=" + downloadedBytes + "-");

            InputStream inputStream = new BufferedInputStream(connection.getInputStream());
            outputFile.seek(downloadedBytes);

            byte[] buffer = new byte[BUFFER_SIZE];
            int bytesRead;
            long totalBytesRead = downloadedBytes;

            while ((bytesRead = inputStream.read(buffer)) != -1) {
                if (isCancelled) {
                    return Result.failure();
                }
                if (isPaused) {
                    Thread.sleep(500);
                    continue;
                }

                outputFile.write(buffer, 0, bytesRead);
                totalBytesRead += bytesRead;
                int progress = (int) (100 * totalBytesRead / connection.getContentLength());
                setProgressAsync(new Data.Builder().putInt(DOWNLOAD_PROGRESS, progress).build());

                Log.d(TAG, "Progress: " + progress + "%");
            }

            return Result.success();

        } catch (Exception e) {
            Log.e(TAG, "Download error", e);
            return Result.failure();
        }
    }

    public void pauseDownload() {
        isPaused = true;
    }

    public void resumeDownload() {
        isPaused = false;
    }

    @Override
    public void onStopped() {
        isCancelled = true;
        super.onStopped();
    }
}