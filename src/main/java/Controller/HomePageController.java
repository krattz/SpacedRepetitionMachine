package Controller;

import com.google.api.client.auth.oauth2.Credential;
import com.google.api.client.googleapis.auth.oauth2.GoogleAuthorizationCodeFlow;
import com.google.api.client.googleapis.auth.oauth2.GoogleAuthorizationCodeRequestUrl;
import com.google.api.client.googleapis.auth.oauth2.GoogleClientSecrets;
import com.google.api.client.googleapis.auth.oauth2.GoogleTokenResponse;
import com.google.api.client.http.FileContent;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.JsonGenerator;
import com.google.api.client.json.JsonParser;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.client.util.Value;
import com.google.api.client.util.store.FileDataStoreFactory;
import com.google.api.services.drive.Drive;
import com.google.api.services.drive.DriveScopes;
import com.google.api.services.drive.model.File;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.annotation.PostConstruct;
import javax.servlet.http.HttpServletResponse;
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
    return isUserAuthenticated?"dashboard.html":"index.html";
    }

    @GetMapping(value={"/googlesignin"})
    public void doGoogleSignIn(HttpServletResponse response) throws IOException {
        GoogleAuthorizationCodeRequestUrl url = flow.newAuthorizationUrl();
        String redirectURL = url.setRedirectUri(CALLBACK_URI).setAccessType("offline").build();
        response.sendRedirect(redirectURL);
    }

    @GetMapping(value={"/oath"})
    public String saveAuthorizationCode(HttpServletResponse request) throws IOException {
        String code = request.getHeader("code");
        if(code!=null){
            saveToken(code);
            return "dashboard.html";
        }else{
            return "index.html";
        }
    }

    private void saveToken(String code) throws IOException {
        GoogleTokenResponse response = flow.newTokenRequest(code).setRedirectUri(CALLBACK_URI).execute();
        flow.createAndStoreCredential(response, USER_iDENTIFIER_KEY);
    }


    @GetMapping(value={"/create"})
    public void createFile(HttpServletResponse response) throws IOException {
        Credential cred = flow.loadCredential(USER_iDENTIFIER_KEY);

        Drive drive = new Drive.Builder(HTTP_TRANSPORT, JSON_FACTORY, cred).setApplicationName("googledrivespringbootexample").build();

        File file = new File();
        file.setName("imagesample.jpg");
        FileContent content = new FileContent("image/jpg", new java.io.File("/home/krattz/Pictures/steam.jpg"));

        File uploadedFile =  drive.files().create(file, content).setFields("id").execute();

        String fileRef = String.format("{fileID: '%s'}", uploadedFile.getId());
        response.getWriter().write(fileRef);
    }
}
