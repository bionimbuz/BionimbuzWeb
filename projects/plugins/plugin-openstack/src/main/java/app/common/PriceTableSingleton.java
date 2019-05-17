package app.common;

import app.controllers.InfoController;
import app.models.PluginPriceModel;
import app.models.PluginPriceTableStatusModel;
import app.models.pricing.InstanceTypePricing;
import app.models.pricing.StoragePricing;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.HashMap;

@Component
@Scope(value = ConfigurableBeanFactory.SCOPE_SINGLETON)
public class PriceTableSingleton {
    
    /*
     * Overwritten Methods
     */

    private PluginPriceTableStatusModel status;
    private PluginPriceModel price;
    
    @Autowired
    private PriceTableSingleton(ApplicationProperties properties) {
        Date now = new Date();
        status = PluginPriceTableStatusModel.createOkStatus(now);

        // Fake instance pricing
        final HashMap<String, Double> listRegionPricing = new HashMap<>();
        listRegionPricing.put(SystemConstants.PLUGIN_REGION, 0d);
        InstanceTypePricing instancePricing =
                new InstanceTypePricing(
                        "openstack",
                        properties.getSystemCpu(),
                        properties.getSystemMemory(),
                        listRegionPricing);
        final HashMap<String, InstanceTypePricing> listInstancePricing = new HashMap<>();
        listInstancePricing.put(instancePricing.getName(), instancePricing);

        // Fake storage pricing
        StoragePricing storagePricing =
                new StoragePricing(SystemConstants.PLUGIN_REGION, 0d, 0d, 0d);
        final HashMap<String, StoragePricing> listStoragePricing = new HashMap<>();
        listStoragePricing.put(storagePricing.getRegion(), storagePricing);

        price = new PluginPriceModel(
                now, listInstancePricing, listStoragePricing);
    }

    public PluginPriceTableStatusModel getStatus() {
        return status;
    }

    public PluginPriceModel getPrice() {
        return price;
    }   
}
