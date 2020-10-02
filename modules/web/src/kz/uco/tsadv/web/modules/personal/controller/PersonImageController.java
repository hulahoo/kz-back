package kz.uco.tsadv.web.modules.personal.controller;


import com.haulmont.cuba.core.app.FileStorageService;
import com.haulmont.cuba.core.entity.FileDescriptor;
import com.haulmont.cuba.core.global.AppBeans;
import com.haulmont.cuba.core.global.Configuration;
import com.haulmont.cuba.core.sys.AppContext;
import com.haulmont.cuba.core.sys.SecurityContext;
import com.haulmont.cuba.core.sys.ServletContextHolder;
import com.haulmont.cuba.security.app.TrustedClientService;
import com.haulmont.cuba.security.global.UserSession;
import com.haulmont.cuba.web.auth.WebAuthConfig;
import kz.uco.tsadv.service.EmployeeService;
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
 * @author veronika.buksha
 * @author adilbekov.yernar
 */
@Controller
public class PersonImageController {

    private MimetypesFileTypeMap mimetypesFileTypeMap = new MimetypesFileTypeMap();

    @RequestMapping(value = "person_image/{personId}", method = RequestMethod.GET)
    @ResponseBody
    public ResponseEntity<InputStreamResource> downloadUserAvatarImage(@PathVariable String personId) {
        Configuration configuration = AppBeans.get(Configuration.class);

        WebAuthConfig webAuthConfig = configuration.getConfig(WebAuthConfig.class);

        TrustedClientService loginService = AppBeans.get(TrustedClientService.class);

        try {
            UserSession userSession = loginService.getSystemSession(webAuthConfig.getTrustedClientPassword());

            AppContext.setSecurityContext(new SecurityContext(userSession));

            InputStream inputStream = null;

            boolean loadDefaultImage = false;

            if (personId == null || personId.equals("") || personId.equalsIgnoreCase("default-avatar")) {
                loadDefaultImage = true;
            } else {
                try {
                    UUID.fromString(personId);
                } catch (Exception ex) {
                    loadDefaultImage = true;
                    // ignore this Exception
                }
            }

            String mimeType = MediaType.APPLICATION_OCTET_STREAM.getType();
            FileDescriptor fileDescriptor = null;

            if (!loadDefaultImage) {
                fileDescriptor = AppBeans.get(EmployeeService.class).getImage(personId);
                if (fileDescriptor == null) {
                    loadDefaultImage = true;
                }
            }

            if (!loadDefaultImage) {
                try {
                    byte[] image = AppBeans.get(FileStorageService.class).loadFile(fileDescriptor);
                    inputStream = new ByteArrayInputStream(image);

                    mimeType = mimetypesFileTypeMap.getContentType(fileDescriptor.getName());
                } catch (Exception ex) {
                    loadDefaultImage = true;
                }
            }

            if (loadDefaultImage) {
                inputStream = ServletContextHolder.getServletContext()
                        .getResourceAsStream("/VAADIN/themes/base/images/no-avatar.png");
                mimeType = MediaType.IMAGE_PNG_VALUE;
            }

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
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            AppContext.setSecurityContext(null);
        }

        return ResponseEntity.badRequest().body(null);
    }

    @RequestMapping(value = "person_image_en/{employeeNumber}", method = RequestMethod.GET)
    @ResponseBody
    public ResponseEntity<InputStreamResource> downloadPersonImage(@PathVariable String employeeNumber) {
        Configuration configuration = AppBeans.get(Configuration.class);

        WebAuthConfig webAuthConfig = configuration.getConfig(WebAuthConfig.class);

        TrustedClientService loginService = AppBeans.get(TrustedClientService.class);

        try {
            UserSession userSession = loginService.getSystemSession(webAuthConfig.getTrustedClientPassword());

            AppContext.setSecurityContext(new SecurityContext(userSession));

            InputStream inputStream = null;

            boolean loadDefaultImage = false;

            if (employeeNumber == null || employeeNumber.equals("") || employeeNumber.equalsIgnoreCase("default-avatar")) {
                loadDefaultImage = true;
            }

            String mimeType = MediaType.APPLICATION_OCTET_STREAM.getType();
            FileDescriptor fileDescriptor = null;

            if (!loadDefaultImage) {
                fileDescriptor = AppBeans.get(EmployeeService.class).getImageByEmployeeNumber(employeeNumber);
                if (fileDescriptor == null) {
                    loadDefaultImage = true;
                }
            }

            if (!loadDefaultImage) {
                try {
                    byte[] image = AppBeans.get(FileStorageService.class).loadFile(fileDescriptor);
                    inputStream = new ByteArrayInputStream(image);

                    mimeType = mimetypesFileTypeMap.getContentType(fileDescriptor.getName());
                } catch (Exception ex) {
                    loadDefaultImage = true;
                }
            }

            if (loadDefaultImage) {
                inputStream = ServletContextHolder.getServletContext()
                        .getResourceAsStream("/VAADIN/themes/base/images/no-avatar.png");
                mimeType = MediaType.IMAGE_PNG_VALUE;
            }

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
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            AppContext.setSecurityContext(null);
        }

        return ResponseEntity.badRequest().body(null);
    }
}