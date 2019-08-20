package origin.spring.messageSend;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.util.logging.Logger;

import static java.util.logging.Logger.getLogger;

/**
 * @Author:qishan
 * @Date: 2019-08-05
 * @Desc:
 **/
@RestController
public class DownloadController {
//    private static final Logger log = getLogger(DownloadController.class);
    @Autowired
    private MockDownloadComponent downloadComponent;

    @GetMapping("/api/download/{type}")
    public String download(@PathVariable String type, HttpServletRequest request) {  // (A)
        HttpSession session = request.getSession();
        String sessionid = session.getId();
        downloadComponent.mockDownload(type, sessionid);  // (B)
        return "success"; // (C)
    }
}
