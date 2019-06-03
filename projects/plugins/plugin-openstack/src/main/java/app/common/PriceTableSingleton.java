package app.common;

import app.models.PluginPriceModel;
import app.models.PluginPriceTableStatusModel;
import app.models.pricing.InstanceTypePricing;
import app.models.pricing.StoragePricing;
import org.openstack4j.api.OSClient;
import org.openstack4j.model.compute.Flavor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static app.common.OSClientHelper.getOSClient;
import static app.common.OSClientHelper.retrieveCredentialData;


@Component
@Scope(value = ConfigurableBeanFactory.SCOPE_SINGLETON)
public class PriceTableSingleton {
    
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

    public void updateSingleton(String token, String identity) {
        Date now = new Date();
        status = PluginPriceTableStatusModel.createOkStatus(now);

        Map<String,String> clientData = retrieveCredentialData(identity);
        OSClient.OSClientV3 os = getOSClient(token, clientData.get("host"), clientData.get("project_id"));
        List<? extends Flavor> flavors = os.compute().flavors().list();

        StoragePricing storagePricing =
                new StoragePricing(SystemConstants.PLUGIN_REGION, 0d, 0d, 0d);
        final HashMap<String, StoragePricing> listStoragePricing = new HashMap<>();
        listStoragePricing.put(storagePricing.getRegion(), storagePricing);

        HashMap<String, InstanceTypePricing> models = flavorsToInstanceTypeModel(flavors);
        price = new PluginPriceModel(now, models, listStoragePricing);
    }


    public PluginPriceTableStatusModel getStatus() {
        return status;
    }

    public PluginPriceModel getPrice() {
        return price;
    }

    private HashMap<String, InstanceTypePricing> flavorsToInstanceTypeModel(List<? extends Flavor> flavors) {
        // Fake instance pricing
        final HashMap<String, Double> listRegionPricing = new HashMap<>();
        listRegionPricing.put(SystemConstants.PLUGIN_REGION, 0d);

        HashMap<String, InstanceTypePricing> instanceTypes = new HashMap<>();
        for (Flavor flavor : flavors) {
            InstanceTypePricing instancePricing =
                    new InstanceTypePricing(
                            flavor.getName(),
                            new Integer(flavor.getVcpus()).shortValue(),
                            Double.valueOf(flavor.getRam()/1024) ,
                            listRegionPricing);
            instanceTypes.put(instancePricing.getName(), instancePricing);
        }

        return instanceTypes;
    }
}
