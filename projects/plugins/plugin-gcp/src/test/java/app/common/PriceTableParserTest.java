package app.common;

import static org.junit.Assert.assertNull;

import java.io.IOException;
import java.text.ParseException;

import org.junit.Test;

import utils.CmdLineArgs;

public class PriceTableParserTest {
    
    @Test
    public void parseTest() {
        PriceTableParser testParser = 
                new PriceTableParser(CmdLineArgs.getPriceTableFile());
        try {
            testParser.parse();
        } catch (IOException | ParseException e) {
            e.printStackTrace();
            assertNull(e);
        }
    }
}
