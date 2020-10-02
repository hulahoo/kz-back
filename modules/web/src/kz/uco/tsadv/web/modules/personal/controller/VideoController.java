package kz.uco.tsadv.web.modules.personal.controller;

import com.haulmont.cuba.core.global.AppBeans;
import com.haulmont.cuba.core.global.Configuration;
import com.haulmont.cuba.core.global.FileLoader;
import com.haulmont.cuba.core.sys.AppContext;
import com.haulmont.cuba.core.sys.SecurityContext;
import com.haulmont.cuba.security.app.TrustedClientService;
import com.haulmont.cuba.security.global.UserSession;
import com.haulmont.cuba.web.auth.WebAuthConfig;
import kz.uco.tsadv.modules.recruitment.model.JobRequest;
import kz.uco.base.service.common.CommonService;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import java.io.InputStream;

@Controller
public class VideoController {

    @RequestMapping(value = "video/{jobRequestId}", method = RequestMethod.GET)
    @ResponseBody
    public ResponseEntity<InputStreamResource> downloadVideo(@PathVariable String jobRequestId) {
        Configuration configuration = AppBeans.get(Configuration.class);
        WebAuthConfig webAuthConfig = configuration.getConfig(WebAuthConfig.class);
        TrustedClientService loginService = AppBeans.get(TrustedClientService.class);
        InputStream is = null;
        try {
            UserSession userSession = loginService.getSystemSession(webAuthConfig.getTrustedClientPassword());
            AppContext.setSecurityContext(new SecurityContext(userSession));

            JobRequest jr = AppBeans.get(CommonService.class).getEntity(JobRequest.class, "select e " +
                    "   from tsadv$JobRequest e " +
                    "  where e.id = '" + jobRequestId + "'", null,"jobRequest.full");
            FileLoader fileLoader = AppBeans.get(FileLoader.class);

            is = fileLoader.openStream(jr.getVideoFile());

            return ResponseEntity.ok()
                    .header("Content-Disposition", "form-data; filename=\"" + jr.getVideoFile().getName() + "\"")
                    .header("Content-Type", "video/mp4")
                    .header("Accept-Ranges", "bytes")
                    .header("Content-Range", "bytes")
                    .header("Content-Length", jr.getVideoFile().getSize().toString())
                    .contentType(MediaType.parseMediaType("video/mp4"))
                    .body(new InputStreamResource(is));

        } catch (Exception e) {
            e.printStackTrace();
        }finally {
            AppContext.setSecurityContext(null);
        }
        return ResponseEntity.badRequest().body(null);
    }
}
