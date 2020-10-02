package kz.uco.tsadv.web.modules.recognition;


import com.haulmont.cuba.core.app.FileStorageService;
import com.haulmont.cuba.core.entity.FileDescriptor;
import com.haulmont.cuba.core.global.AppBeans;
import com.haulmont.cuba.core.global.Configuration;
import com.haulmont.cuba.core.sys.AppContext;
import com.haulmont.cuba.core.sys.SecurityContext;
import com.haulmont.cuba.security.app.TrustedClientService;
import com.haulmont.cuba.security.global.UserSession;
import com.haulmont.cuba.web.auth.WebAuthConfig;
import kz.uco.tsadv.service.RcgFeedbackService;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.activation.MimetypesFileTypeMap;
import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.util.UUID;

/**
 * @author adilbekov.yernar
 */
@Controller
public class RcgFeedbackTypeImageController {

    private MimetypesFileTypeMap mimetypesFileTypeMap = new MimetypesFileTypeMap();

    @RequestMapping(value = "rf_type_image/{id}", method = RequestMethod.GET)
    @ResponseBody
    public ResponseEntity<InputStreamResource> loadRecognitionTypeImage(@PathVariable String id) {
        Configuration configuration = AppBeans.get(Configuration.class);

        WebAuthConfig webAuthConfig = configuration.getConfig(WebAuthConfig.class);

        TrustedClientService loginService = AppBeans.get(TrustedClientService.class);

        try {
            UserSession userSession = loginService.getSystemSession(webAuthConfig.getTrustedClientPassword());

            AppContext.setSecurityContext(new SecurityContext(userSession));

            InputStream inputStream = null;

            String mimeType = MediaType.APPLICATION_OCTET_STREAM.getType();
            FileDescriptor fileDescriptor = AppBeans.get(RcgFeedbackService.class).feedbackTypeImage(UUID.fromString(id));
            if (fileDescriptor != null) {
                try {
                    byte[] image = AppBeans.get(FileStorageService.class).loadFile(fileDescriptor);
                    inputStream = new ByteArrayInputStream(image);

                    mimeType = mimetypesFileTypeMap.getContentType(fileDescriptor.getName());
                } catch (Exception ex) {
                    //
                }

                if (inputStream != null) {
                    MediaType mediaType = MediaType.APPLICATION_OCTET_STREAM;
                    try {
                        mediaType = MediaType.parseMediaType(mimeType);
                    } catch (Exception ex) {
                        //ignore this Exception
                    }

                    return ResponseEntity.ok()
                            .contentLength(inputStream.available())
                            .contentType(mediaType)
                            .body(new InputStreamResource(inputStream));
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            AppContext.setSecurityContext(null);
        }

        return ResponseEntity.badRequest().body(null);
    }
}