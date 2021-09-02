package kz.uco.tsadv.service;

import com.haulmont.cuba.core.app.FileStorageAPI;
import com.haulmont.cuba.core.entity.FileDescriptor;
import com.haulmont.cuba.core.entity.ScheduledTask;
import com.haulmont.cuba.core.entity.ScheduledTaskDefinedBy;
import com.haulmont.cuba.core.entity.SchedulingType;
import com.haulmont.cuba.core.global.*;
import kz.uco.base.common.BaseCommonUtils;
import kz.uco.tsadv.entity.videoplay.VideoFileForPlay;
import kz.uco.tsadv.entity.videoplay.enums.VideoFileConvertStatus;
import kz.uco.tsadv.global.common.CommonConfig;
import org.springframework.stereotype.Service;

import javax.inject.Inject;
import java.io.*;
import java.util.List;
import java.util.Map;

@Service(VideoService.NAME)
public class VideoServiceBean implements VideoService {
    @Inject
    protected DataManager dataManager;
    @Inject
    protected Metadata metadata;
    @Inject
    protected FileStorageAPI fileStorageAPI;
    @Inject
    protected CommonConfig commonConfig;

    @Override
    public void startScheduler(FileDescriptor fileDescriptor) {
        LoadContext<VideoFileForPlay> videoFileForPlayLoadContext = LoadContext.create(VideoFileForPlay.class)
                .setQuery(LoadContext.createQuery(
                        "select e from tsadv$VideoFileForPlay e " +
                                " where e.source.id = :fileDescriptorId")
                        .setParameter("fileDescriptorId", fileDescriptor.getId()))
                .setView("video-file-for-play");
        List<VideoFileForPlay> videoFileForPlays = dataManager.loadList(videoFileForPlayLoadContext);
        VideoFileForPlay lastVideoFileForPlay = videoFileForPlays.stream()
                .max((o1, o2) -> o1.getCreateTs().after(o2.getCreateTs()) ? 1 : -1).orElse(null);
        if (lastVideoFileForPlay != null) {
            videoFileForPlays.remove(lastVideoFileForPlay);
            videoFileForPlays.forEach(videoFileForPlay -> dataManager.remove(videoFileForPlay));
            lastVideoFileForPlay.setStatus(VideoFileConvertStatus.TO_CONVERT);
            FileDescriptor outputFile = lastVideoFileForPlay.getOutputFile();
            if (outputFile != null) {
                try {
                    fileStorageAPI.removeFile(outputFile);
                    dataManager.remove(outputFile);
                } catch (FileStorageException e) {
                    e.printStackTrace();
                }
                lastVideoFileForPlay.setOutputFile(null);
            }
        } else {
            lastVideoFileForPlay = metadata.create(VideoFileForPlay.class);
            lastVideoFileForPlay.setSource(fileDescriptor);
            lastVideoFileForPlay.setStatus(VideoFileConvertStatus.TO_CONVERT);
        }
        dataManager.commit(lastVideoFileForPlay);
        LoadContext<ScheduledTask> scheduledTaskLoadContext = LoadContext.create(ScheduledTask.class)
                .setQuery(LoadContext.createQuery(
                        "select s from sys$ScheduledTask s " +
                                " where s.beanName = :beanName")
                        .setParameter("beanName", VideoService.NAME))
                .setView(View.LOCAL);
        List<ScheduledTask> scheduledTasks = dataManager.loadList(scheduledTaskLoadContext);
        if (scheduledTasks.isEmpty()) {
            ScheduledTask scheduledTask = metadata.create(ScheduledTask.class);
            scheduledTask.setActive(true);
            scheduledTask.setBeanName(VideoService.NAME);
            scheduledTask.setMethodName("convertVideos");
            scheduledTask.setSchedulingType(SchedulingType.PERIOD);
            scheduledTask.setPeriod(2);
            scheduledTask.setDefinedBy(ScheduledTaskDefinedBy.BEAN);
            scheduledTask.setSingleton(true);
            scheduledTask.setUserName("admin");
            dataManager.commit(scheduledTask);
        } else {
            ScheduledTask scheduledTask = scheduledTasks.stream().findFirst().orElse(null);
            if (scheduledTask != null) {
                scheduledTasks.remove(scheduledTask);
                scheduledTasks.forEach(scheduledTask1 -> dataManager.remove(scheduledTask1));
                if (scheduledTask.getActive()) {
                    return;
                } else {
                    scheduledTask.setActive(true);
                    dataManager.commit(scheduledTask);
                }
            }
        }
    }

    @Override
    public void convertVideos() {
//        System.getProperties().forEach((o, o2) -> System.out.println(o.toString() + " - " + o2.toString()));
        boolean isWindows = System.getProperty("os.name").toLowerCase().contains("windows");
        boolean isLinux = System.getProperty("os.name").toLowerCase().contains("linux");
//        System.out.println(System.getProperty("os.name"));
        boolean error = false;
        String converterPath = commonConfig.getConverterPath();
        String tempDirPath = commonConfig.getTempDirPath();
        if (tempDirPath == null || tempDirPath.isEmpty()) {
            if (isWindows) {
                tempDirPath = "..\\temp\\";
            } else {
                tempDirPath = "/tmp/";
            }
        }
        while (true) {
            LoadContext<VideoFileForPlay> videoFileForPlayLoadContext = LoadContext.create(VideoFileForPlay.class)
                    .setQuery(LoadContext.createQuery(
                            "select e from tsadv$VideoFileForPlay e " +
                                    " where e.status = :status")
                            .setParameter("status", VideoFileConvertStatus.TO_CONVERT))
                    .setView("video-file-for-play");
            List<VideoFileForPlay> videoFileForPlays = dataManager.loadList(videoFileForPlayLoadContext);
            if (videoFileForPlays.isEmpty() || error) {
                LoadContext<ScheduledTask> scheduledTaskLoadContext = LoadContext.create(ScheduledTask.class)
                        .setQuery(LoadContext.createQuery(
                                "select s from sys$ScheduledTask s " +
                                        " where s.beanName = :beanName ")
                                .setParameter("beanName", VideoService.NAME))
                        .setView(View.LOCAL);
                List<ScheduledTask> scheduledTasks = dataManager.loadList(scheduledTaskLoadContext);
                scheduledTasks.forEach(scheduledTask -> scheduledTask.setActive(false));
                scheduledTasks.forEach(scheduledTask -> dataManager.commit(scheduledTask));
                return;
            }
            for (VideoFileForPlay videoFileForPlay : videoFileForPlays) {
                InputStream inputStream = null;
                OutputStream outputStream = null;
                File copy = null;
                try {
                    copy = new File(tempDirPath
                            + videoFileForPlay.getSource().getName());
                    inputStream = fileStorageAPI.openStream(videoFileForPlay.getSource());
                    outputStream = new FileOutputStream(copy);
                    byte[] buffer = new byte[1024];
                    int length;
                    while ((length = inputStream.read(buffer)) > 0) {
                        outputStream.write(buffer, 0, length);
                    }
                } catch (Exception e) {
                    error = true;
                    videoFileForPlay.setStatus(VideoFileConvertStatus.CONVERTED_ERROR);
                    e.printStackTrace();
                } finally {
                    try {
                        if (inputStream != null) {
                            inputStream.close();
                        }
                        if (outputStream != null) {
                            outputStream.close();
                        }
                    } catch (IOException e) {
                        error = true;
                        videoFileForPlay.setStatus(VideoFileConvertStatus.CONVERTED_ERROR);
                        e.printStackTrace();
                    }
                }
                if (!error) {
                    ProcessBuilder processBuilder = new ProcessBuilder();
                    Map<String, String> environment = processBuilder.environment();
//                    System.out.println(environment.get("Path"));
                    boolean ffmpegIsInPath = false;
                    String path = "";
                    if (isWindows) {
                        ffmpegIsInPath = environment.get("Path").contains("ffmpeg");
                        if (ffmpegIsInPath) {
                            path = "ffmpeg.exe -i ";
                        } else if (converterPath != null && !converterPath.isEmpty()) {
                            path = "\"" + converterPath + "ffmpeg.exe\" -i ";
                        } else {
                            System.out.println("Ffmpeg.exe не установлен ");
                            deleteFiles(tempDirPath, videoFileForPlay);
                            error = true;
                            videoFileForPlay.setStatus(VideoFileConvertStatus.CONVERTED_ERROR);
                            break;
                        }
                        processBuilder.command("cmd.exe", "/c",
                                path + "\"" + tempDirPath
                                        + videoFileForPlay.getSource().getName() +
                                        "\" \"" + tempDirPath + videoFileForPlay.getSource().getName() + ".mp4\" -y ");
                    } else if (isLinux) {
                        ffmpegIsInPath = true;
                        ProcessBuilder check = new ProcessBuilder();
                        check.command("/bin/bash", "/c", "ffmpeg");
                        InputStream checkStdout = null;
                        InputStreamReader checkIsrStdout = null;
                        BufferedReader checkBrStdout = null;
                        try {
                            check.redirectErrorStream(true);
                            Process checkProcess = check.start();
                            checkStdout = checkProcess.getInputStream();
                            checkIsrStdout = new InputStreamReader(checkStdout);
                            checkBrStdout = new BufferedReader(checkIsrStdout);
                            String line = null;
                            while ((line = checkBrStdout.readLine()) != null) {
                                if (line.contains("command not found")) {
                                    ffmpegIsInPath = false;
                                }
                            }
                            int exitCode = checkProcess.waitFor();
//                        System.out.println("\nExited with code : " + exitCode);
                        } catch (Exception e) {
                            e.printStackTrace();
                        } finally {
                            try {
                                checkIsrStdout.close();
                                checkStdout.close();
                                checkBrStdout.close();
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        }
                        if (ffmpegIsInPath) {
                            path = "ffmpeg -i ";
                        } else {
                            System.out.println("Ffmpeg не установлен ");
                            error = true;
                            videoFileForPlay.setStatus(VideoFileConvertStatus.CONVERTED_ERROR);
                            break;
                        }
                        processBuilder.command("/bin/bash", "-c",
                                path + "\"" + tempDirPath
                                        + videoFileForPlay.getSource().getName() +
                                        "\" \"" + tempDirPath + videoFileForPlay.getSource().getName() + ".mp4\" -y ");

                    } else {
                        System.out.println("Нейзвестная ОС");
                        error = true;
                        videoFileForPlay.setStatus(VideoFileConvertStatus.CONVERTED_ERROR);
                    }
                    InputStream stdout = null;
                    InputStreamReader isrStdout = null;
                    BufferedReader brStdout = null;
                    try {
                        processBuilder.redirectErrorStream(true);
                        Process process = processBuilder.start();
                        stdout = process.getInputStream();
                        isrStdout = new InputStreamReader(stdout);
                        brStdout = new BufferedReader(isrStdout);
                        String line = null;
                        while ((line = brStdout.readLine()) != null) {
                        }
                        int exitCode = process.waitFor();
//                        System.out.println("\nExited with code : " + exitCode);
                    } catch (Exception e) {
                        error = true;
                        videoFileForPlay.setStatus(VideoFileConvertStatus.CONVERTED_ERROR);
                        e.printStackTrace();
                    } finally {
                        try {
                            isrStdout.close();
                            stdout.close();
                            brStdout.close();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                }
                if (!error) {
                    FileDescriptor fileDescriptor = metadata.create(FileDescriptor.class);
                    fileDescriptor.setCreateDate(BaseCommonUtils.getSystemDate());
                    fileDescriptor.setName(videoFileForPlay.getSource().getName() + ".mp4");
                    fileDescriptor.setExtension("mp4");
                    FileInputStream fileInputStreamOutputFile = null;
                    File file;
                    try {
                        file = new File(tempDirPath
                                + videoFileForPlay.getSource().getName() + ".mp4");
                        fileDescriptor.setSize(file.length());
                        fileInputStreamOutputFile = new FileInputStream(file);
                        fileStorageAPI.saveStream(fileDescriptor, fileInputStreamOutputFile);
                        dataManager.commit(fileDescriptor);
                        videoFileForPlay.setOutputFile(fileDescriptor);
                        videoFileForPlay.setStatus(VideoFileConvertStatus.CONVERTED_SUCCESS);
                    } catch (Exception e) {
                        error = true;
                        videoFileForPlay.setStatus(VideoFileConvertStatus.CONVERTED_ERROR);
                        e.printStackTrace();
                    } finally {
                        try {
                            fileInputStreamOutputFile.close();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                }
                deleteFiles(tempDirPath, videoFileForPlay);
                dataManager.commit(videoFileForPlay);
//                System.out.println("converted");

            }
        }

    }

    protected void deleteFiles(String tempDirPath, VideoFileForPlay videoFileForPlay) {
        File fileSource = new File(tempDirPath
                + videoFileForPlay.getSource().getName());
        File fileOutput = new File(tempDirPath
                + videoFileForPlay.getSource().getName() + ".mp4");
        if (fileSource != null) {
            fileSource.delete();
        }
        if (fileOutput != null) {
            fileOutput.delete();
        }
    }

}