package kz.uco.tsadv.service;


import java.io.IOException;

public interface QRCodeService {
    String NAME = "tsadv_QRCodeService";

    String generateQRCodeImage(String text, int width, int height) throws IOException;

}