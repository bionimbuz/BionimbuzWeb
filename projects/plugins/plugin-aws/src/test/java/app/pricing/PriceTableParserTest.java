package app.pricing;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.Assert.assertNotNull;

import java.io.IOException;
import java.text.ParseException;

import org.junit.Test;

import app.common.SystemConstants;
import app.models.PluginPriceModel;
import app.pricing.exceptions.PriceTableDateInvalidException;
import app.pricing.exceptions.PriceTableVersionException;
import utils.CmdLineArgs;

public class PriceTableParserTest {
    
    @Test
    public void parseTest() {
        PriceTableParser testParser = 
                new PriceTableParser(
                        CmdLineArgs.getPriceTableFile(),
                        SystemConstants.PRICE_TABLE_VERSION);
        assertNotNull(testParser);
        PluginPriceModel priceModel = null;
        try {
            priceModel = testParser.parse();
            assertNotNull(priceModel);
        } catch (IOException | PriceTableVersionException | ParseException
                | PriceTableDateInvalidException e) {
            assertThat(e).isNull();
        }
        
    }
}
