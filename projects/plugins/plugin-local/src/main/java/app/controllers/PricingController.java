package app.controllers;

import java.util.Date;
import java.util.HashMap;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

import app.common.SystemConstants;
import app.models.Body;
import app.models.PluginPriceModel;
import app.models.PluginPriceTableModel;
import app.models.PluginPriceTableStatusModel;
import app.models.pricing.InstanceTypePricing;
import app.models.pricing.StoragePricing;

@RestController
public class PricingController extends AbstractPricingController{

    /*
     * Overwritten Methods
     */

    private static PluginPriceTableStatusModel status;
    private static PluginPriceModel price;

    static {
        Date now = new Date();
        status = PluginPriceTableStatusModel.createOkStatus(now);

        // Fake instance pricing
        final HashMap<String, Double> listRegionPricing = new HashMap<>();
        listRegionPricing.put(SystemConstants.PLUGIN_ZONE, 0d);
        InstanceTypePricing instancePricing =
                new InstanceTypePricing(
                        InfoController.getHostName(),
                        getSystemCPU(),
                        getSystemMemory(),
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

    private static Double getSystemMemory() {
        return Double.valueOf(
                System.getProperty(
                SystemConstants.SYSTEM_PROPERTY_MEM,
                "0"));
    }

    private static Short getSystemCPU() {
        return Short.valueOf(
                System.getProperty(
                SystemConstants.SYSTEM_PROPERTY_CPU,
                String.valueOf(Runtime.getRuntime().availableProcessors())));
    }

    @Override
    protected ResponseEntity<Body<PluginPriceTableModel>> getPricing() throws Exception {
        PluginPriceTableModel res = new PluginPriceTableModel(status, price);
        return ResponseEntity.ok(
                Body.create(res));
    }
    @Override
    protected ResponseEntity<Body<PluginPriceTableStatusModel>> getPricingStatus() throws Exception {

        return ResponseEntity.ok(
                Body.create(status));
    }


}
