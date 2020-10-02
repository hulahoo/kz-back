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
import kz.uco.tsadv.service.RecognitionService;
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

/**
 * @author adilbekov.yernar
 */
@Controller
public class RcgAnswerIconController {

    private MimetypesFileTypeMap mimetypesFileTypeMap = new MimetypesFileTypeMap();

    @RequestMapping(value = "rcg_answer_icon/{answerId}", method = RequestMethod.GET)
    @ResponseBody
    public ResponseEntity<InputStreamResource> loadRcgAnswerIcon(@PathVariable String answerId) {
        Configuration configuration = AppBeans.get(Configuration.class);

        WebAuthConfig webAuthConfig = configuration.getConfig(WebAuthConfig.class);

        TrustedClientService loginService = AppBeans.get(TrustedClientService.class);

        try {
            UserSession userSession = loginService.getSystemSession(webAuthConfig.getTrustedClientPassword());

            AppContext.setSecurityContext(new SecurityContext(userSession));

            FileDescriptor fileDescriptor = AppBeans.get(RecognitionService.class).getRcgAnswerIcon(answerId);

            byte[] image = AppBeans.get(FileStorageService.class).loadFile(fileDescriptor);
            if (image.length != 0) {
                InputStream inputStream = new ByteArrayInputStream(image);

                String mimeType = mimetypesFileTypeMap.getContentType(fileDescriptor.getName());
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
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            AppContext.setSecurityContext(null);
        }

        return ResponseEntity.badRequest().body(null);
    }
}