package app.common.supliers;

import static com.google.common.base.Preconditions.checkNotNull;

import java.io.BufferedReader;
import java.io.StringReader;

import org.jclouds.domain.Credentials;

import com.google.common.base.Supplier;

public class AWSAccessKeyFromContent implements Supplier<Credentials>{

    private static final String SECRET_STR = "secret";
    private static final String ACCESS_STR = "access";
    private static final String KEY_STR = "key";
    private static final String ERROR_NULL_CONTENT = "AWS Credentials content cannot be null";
    private static final String ERROR_LESS_THAN_2_LINES = "AWS Credential file has less than 2 lines";
    private static final String ERROR_FORMAT = "AWS Credential is in an unsuported format.";

    private String accesKeyId;
    private String secretAccessKey;

    public AWSAccessKeyFromContent(String contentString) throws Exception{
       checkNotNull(contentString, ERROR_NULL_CONTENT);
       parseContentString(contentString);
    }

    private void parseContentString(String contentString) throws Exception {
        try(StringReader strReader =
                new StringReader(contentString);
            BufferedReader reader =
                new BufferedReader(strReader)){
            String line1 = reader.readLine();
            if(line1 == null)
                throw new Exception(ERROR_LESS_THAN_2_LINES);
            String line2 = reader.readLine();
            if(line2 == null)
                throw new Exception(ERROR_LESS_THAN_2_LINES);
            parseLines(line1, line2);
        }
    }

    private void parseLines(final String line1, final String line2) throws Exception {
        String [] line1Splitted = line1.split(",");
        if(line1Splitted.length < 2) {
            parseLinesForNoUserCredential(line1, line2);
            return;
        }
        String [] line2Splitted = line2.split(",");
        if(line1Splitted.length != line2Splitted.length) {
            throw new Exception(ERROR_FORMAT);
        }

        int accesKeyIdPos = -1;
        int secretAccessKeyPos = -1;
        String key="";
        for(int i=0; i<line1Splitted.length; i++) {
            key = line1Splitted[i].replace(" ","").trim().toLowerCase();
            if(!key.contains(KEY_STR))
                continue;
            if(key.contains(SECRET_STR))
                secretAccessKeyPos = i;
            else if(key.contains(ACCESS_STR))
                accesKeyIdPos = i;
        }
        if(accesKeyIdPos < 0 || secretAccessKeyPos < 0) {
            throw new Exception(ERROR_FORMAT);
        }
        this.accesKeyId = line2Splitted[accesKeyIdPos];
        this.secretAccessKey = line2Splitted[secretAccessKeyPos];
    }

    private void parseLinesForNoUserCredential(final String line1, final String line2) throws Exception {
        String [] line1Splitted = line1.split("=");
        if(line1Splitted.length < 2) {
            throw new Exception(ERROR_FORMAT);
        }
        String [] line2Splitted = line2.split("=");
        if(line1Splitted.length != line2Splitted.length) {
            throw new Exception(ERROR_FORMAT);
        }
        this.accesKeyId = line1Splitted[1];
        this.secretAccessKey = line2Splitted[1];
    }

    @Override
    public Credentials get() {
        return new Credentials(
                accesKeyId,
                secretAccessKey);
    }
}
