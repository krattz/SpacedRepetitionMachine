package Controller;

import com.google.api.client.auth.oauth2.Credential;
import com.google.api.client.googleapis.auth.oauth2.GoogleAuthorizationCodeFlow;
import com.google.api.client.googleapis.auth.oauth2.GoogleClientSecrets;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.JsonGenerator;
import com.google.api.client.json.JsonParser;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.client.util.Value;
import com.google.api.client.util.store.FileDataStoreFactory;
import com.google.api.services.drive.DriveScopes;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.annotation.PostConstruct;
import java.io.*;
import java.nio.charset.Charset;
import java.util.Collections;
import java.util.List;

@Controller("/pact")
public class HomePageController {

    private static HttpTransport HTTP_TRANSPORT = new NetHttpTransport();
    private static final JsonFactory JSON_FACTORY = new JsonFactory() {
        @Override
        public JsonParser createJsonParser(InputStream in) throws IOException {
            return null;
        }

        @Override
        public JsonParser createJsonParser(InputStream in, Charset charset) throws IOException {
            return null;
        }

        @Override
        public JsonParser createJsonParser(String value) throws IOException {
            return null;
        }

        @Override
        public JsonParser createJsonParser(Reader reader) throws IOException {
            return null;
        }

        @Override
        public JsonGenerator createJsonGenerator(OutputStream out, Charset enc) throws IOException {
            return null;
        }

        @Override
        public JsonGenerator createJsonGenerator(Writer writer) throws IOException {
            return null;
        }
    };
    private static final List<String> SCOPES = Collections.singletonList(DriveScopes.DRIVE);
    private static final String USER_iDENTIFIER_KEY = "MY_DUMMY_USER";

    @Value("${google.oauth.callback.uri}")
    private String CALLBACK_URI;

    @Value("${google.secret.key.path}")
    private Resource credentials;

    @Value("${google.credentials.folder.path}")
    private Resource credentialsFolder;

    private GoogleAuthorizationCodeFlow flow;

    @PostConstruct
    public void init() throws IOException {
        GoogleClientSecrets secrets = GoogleClientSecrets.load(JSON_FACTORY, new InputStreamReader(credentials.getInputStream()));
        flow = new GoogleAuthorizationCodeFlow.Builder(HTTP_TRANSPORT, JSON_FACTORY, secrets, SCOPES)
                .setDataStoreFactory(new FileDataStoreFactory(credentialsFolder.getFile())).build();
    }

    @GetMapping(value={"/authentica"})
    public String homePage() throws IOException {
        boolean isUserAuthenticated = false;

        Credential credential = flow.loadCredential(USER_iDENTIFIER_KEY);
        if(credential != null){
            boolean tokenValid = credential.refreshToken();
            if(tokenValid){
                isUserAuthenticated = true;
            }
        }
    return isUserAuthenticated?"Its Authentic":"Its not Authentic";
    }
}
