package app.common;

import static org.assertj.core.api.Assertions.assertThat;

import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;

import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;

import com.google.common.io.Files;

import app.models.CredentialModel;

public class TestUtils {
    
    public static String readCredential() {
        String fileContents = null;        
        try {
            fileContents = 
                    Files.toString(
                        new File(CmdLineArgs.getCredentialFile()),
                        Charset.defaultCharset());
        } catch (IOException e) {
            e.printStackTrace();
        }
        assertThat(fileContents).isNotNull();
        return fileContents;
    }    

    public static <T> HttpEntity<CredentialModel<T>> createEntity(){
        return createEntity(null);
    }
    
    public static <T> HttpEntity<CredentialModel<T>> createEntity(T content){
        CredentialModel<T> credential = 
                new CredentialModel<>(TestUtils.readCredential());        
        credential.setModel(content);
        
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
    
        HttpEntity<CredentialModel<T>> entity = 
                new HttpEntity<>(credential, headers);    
        
        return entity;
    }
}
