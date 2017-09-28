package controllers;

import com.google.common.io.Resources;
import java.io.IOException;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;

import static java.nio.charset.StandardCharsets.UTF_8;

@Path("/")
@Produces(MediaType.TEXT_HTML)

public class StaticHtmlController {

    @GET
    public String getIndexPage() throws IOException {
        Resources.getResource("index.html");
        String style = Resources.toString(Resources.getResource("style.css"), UTF_8);
        String bootstrap = Resources.toString(Resources.getResource("bootstrap.css"), UTF_8);
        String bootstrapmin = Resources.toString(Resources.getResource("bootstrap.min.css"), UTF_8);
        String script = Resources.toString(Resources.getResource("script.js"), UTF_8);
        String css = "<style>"+style+"</style>";
        script = "<script>"+script+"</script>";
        String html = Resources.toString(Resources.getResource("index.html"), UTF_8);
        int index = html.indexOf("</head>");
        String html1 = html.substring(0,index);
        String html2 = html.substring(index);
        html = html1+css+script+html2;
        //System.out.println(html);
        return html;
    }

}

