package kz.uco.tsadv.web.learningobjectplayer;

import com.haulmont.cuba.core.entity.FileDescriptor;
import com.haulmont.cuba.core.global.*;
import com.haulmont.cuba.core.sys.AppContext;
import com.haulmont.cuba.gui.components.AbstractWindow;
import com.haulmont.cuba.gui.components.BrowserFrame;
import com.haulmont.cuba.gui.components.StreamResource;
import com.haulmont.cuba.gui.components.UrlResource;
import kz.uco.tsadv.entity.videoplay.VideoFileForPlay;
import kz.uco.tsadv.entity.videoplay.enums.VideoFileConvertStatus;
import kz.uco.tsadv.modules.learning.enums.ContentType;
import kz.uco.tsadv.modules.learning.model.LearningObject;

import javax.inject.Inject;
import java.io.ByteArrayInputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public class LearningObjectPlayer extends AbstractWindow {

    @Inject
    protected BrowserFrame browserFrame;
    protected LearningObject item = null;
    @Inject
    protected FileLoader fileLoader;
    @Inject
    protected DataManager dataManager;
    protected FileDescriptor outputFile = null;
    protected VideoFileForPlay videoFileForPlay = null;


    @Override
    public void init(Map<String, Object> params) {
        if (params.containsKey("item")) {
            item = (LearningObject) params.get("item");
            UUID fileId = null;
            try {
                fileId = item.getFile().getId();
            } catch (Exception e) {
            }
            if (fileId != null) {
                LoadContext<VideoFileForPlay> videoFileForPlayLoadContext = LoadContext.create(VideoFileForPlay.class)
                        .setQuery(LoadContext.createQuery(
                                "select e from tsadv$VideoFileForPlay e " +
                                        " where e.source.id = :fileId ")
                                .setParameter("fileId", item.getFile().getId()))
                        .setView("video-file-for-play");
                List<VideoFileForPlay> videoFileForPlays = dataManager.loadList(videoFileForPlayLoadContext);
                videoFileForPlay = videoFileForPlays.stream()
                        .max((o1, o2) -> o1.getCreateTs().after(o2.getCreateTs()) ? 1 : -1).orElse(null);
                if (videoFileForPlay != null) {
                    outputFile = videoFileForPlay.getOutputFile();
                }
            }
        }
    }

    @Override
    public void ready() {
        if (item != null) {
            if (ContentType.PDF.equals(item.getContentType())
                    && item.getFile() != null) {
                browserFrame.setSource(StreamResource.class)
                        .setStreamSupplier(() -> {
                            try {
                                FileDescriptor file = dataManager.reload(item.getFile(), View.BASE);
                                return fileLoader.openStream(file);
                            } catch (FileStorageException e) {
                                e.printStackTrace();
                                return null;
                            }
                        }).setMimeType("application/pdf");
            } else if (ContentType.HTML.equals(item.getContentType())
                    && item.getHtml() != null) {
                browserFrame.setSource(StreamResource.class)
                        .setStreamSupplier(() ->
                                new ByteArrayInputStream(item.getHtml().getBytes(StandardCharsets.UTF_8)))
                        .setMimeType("text/html");
            } else if (ContentType.VIDEO.equals(item.getContentType())) {
                String html;
                if (videoFileForPlay != null) {
                    if (VideoFileConvertStatus.CONVERTED_SUCCESS.equals(videoFileForPlay.getStatus())) {
                        html = "<!DOCTYPE html> " +
                                "<html> " +
                                " <head> " +
                                "    <title>Видео воспроизведение</title> " +
                                "    <meta charset=\"utf-8\"> " +
                                " </head> " +
                                " <body style=\"padding: 0px;margin: 0px;\"> " +
                                "  <video width=\"100%\" height=\"100%\" controls=\"controls\" preload = \"metadata\" " +
                                "       autoplay=\"autoplay\"> " +
                                "   <source src=\"videourl\" type='video/mp4; codecs=\"avc1.42E01E, mp4a.40.2\"'> " +
                                "   Тег video не поддерживается вашим браузером.  " +
                                "   <a href=\"./22.mp4\">Скачать</a> " +
                                "  </video> " +
                                " </body> " +
                                "</html>";
                        html = html.replace("videourl", AppContext.getProperty("cuba.webAppUrl")
                                + "/dispatch/video-play/" + outputFile.getId().toString());
                    } else if (VideoFileConvertStatus.TO_CONVERT.equals(videoFileForPlay.getStatus())) {
                        html = "<!DOCTYPE html> " +
                                "<html> " +
                                " <head> " +
                                "    <title>Видео воспроизведение</title> " +
                                "    <meta charset=\"utf-8\"> " +
                                " </head> " +
                                " <body style=\"padding: 0px;margin: 0px;\"> " +
                                "  <H1>Видео на обработке</H1>" +
                                " </body> " +
                                "</html>";
                    } else if (VideoFileConvertStatus.CONVERTED_ERROR.equals(videoFileForPlay.getStatus())) {
                        html = "<!DOCTYPE html> " +
                                "<html> " +
                                " <head> " +
                                "    <title>Видео воспроизведение</title> " +
                                "    <meta charset=\"utf-8\"> " +
                                " </head> " +
                                " <body style=\"padding: 0px;margin: 0px;\"> " +
                                "    <H1>Видео файл не возможно обработать. Загрузите другой файл.</H1>" +
                                " </body> " +
                                "</html>";
                    } else {
                        html = "<!DOCTYPE html> " +
                                "<html> " +
                                " <head> " +
                                "    <title>Видео воспроизведение</title> " +
                                "    <meta charset=\"utf-8\"> " +
                                " </head> " +
                                " <body style=\"padding: 0px;margin: 0px;\"> " +
                                "    <H1>Нет видео файла</H1>" +
                                " </body> " +
                                "</html>";
                    }
                } else {
                    html = "<!DOCTYPE html> " +
                            "<html> " +
                            " <head> " +
                            "    <title>Видео воспроизведение</title> " +
                            "    <meta charset=\"utf-8\"> " +
                            " </head> " +
                            " <body style=\"padding: 0px;margin: 0px;\"> " +
                            "  <video width=\"100%\" height=\"100%\" controls=\"controls\" preload = \"metadata\"> " +
                            "   <source src=\"videourl\" type='video/mp4; codecs=\"avc1.42E01E, mp4a.40.2\"'> " +
                            "   Тег video не поддерживается вашим браузером.  " +
                            "   <a href=\"./22.mp4\">Скачать</a> " +
                            "  </video> " +
                            " </body> " +
                            "</html>";
                    html = html.replace("videourl", AppContext.getProperty("cuba.webAppUrl")
                            + "/dispatch/video-play/" + item.getFile().getId());
                }
                byte[] bytes = html.getBytes(StandardCharsets.UTF_8);
                browserFrame.setSource(StreamResource.class)
                        .setStreamSupplier(() -> new ByteArrayInputStream(bytes))
                        .setMimeType("text/html");
            } else if (ContentType.URL.equals(item.getContentType())
                    && item.getUrl() != null) {
                URL url = null;
                try {
                    url = new URL(item.getUrl());
                    browserFrame.setSource(UrlResource.class).setUrl(url);
                } catch (MalformedURLException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}