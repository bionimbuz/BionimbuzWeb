package app.common;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.Assert.assertNotNull;

import java.util.Calendar;

import org.junit.Test;

import app.models.PricingModel;
import app.models.PricingStatusModel.Status;
import app.pricing.PriceTableParser;
import utils.CmdLineArgs;

public class PriceTableParserTest {
    
    @Test
    public void parseTest() {
        PriceTableParser testParser = 
                new PriceTableParser(
                        CmdLineArgs.getPriceTableFile(),
                        SystemConstants.PRICE_TABLE_VERSION);
        Calendar now = Calendar.getInstance();
        PricingModel priceModel = 
                testParser.parse(now.getTime(), null);
        
        assertNotNull(testParser);
        assertThat(priceModel.getStatus().getStatus())
                    .isEqualTo(Status.OK);
    }
}
