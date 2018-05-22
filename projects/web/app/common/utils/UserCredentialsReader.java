package common.utils;

import java.io.IOException;
import java.io.StringWriter;
import java.util.Iterator;
import java.util.List;

import org.apache.commons.io.IOUtils;

import models.InstanceModel.CredentialUsagePolicy;
import models.PluginModel;
import models.VwCredentialModel;

public class UserCredentialsReader implements Iterable <String>{

    private List<VwCredentialModel> credentials;

    public UserCredentialsReader(
            final PluginModel plugin) {
        this(plugin, CredentialUsagePolicy.OWNER_FIRST);
    }
    public UserCredentialsReader(
            final PluginModel plugin,
            final CredentialUsagePolicy credentialPolicy) {
        if(credentialPolicy == CredentialUsagePolicy.ONLY_OWNER) {
            this.credentials = VwCredentialModel.searchForCurrentUserAndPlugin(
                    plugin.getId());
        }
        else {
            this.credentials = VwCredentialModel.searchForCurrentUserAndPluginWithShared(
                    plugin.getId(),
                    credentialPolicy);
        }
    }

    private void initCredentials(
            final PluginModel plugin,
            final CredentialUsagePolicy credentialPolicy) {

    }

    @Override
    public Iterator<String> iterator() {
        return new UserCredentialsReaderIterable(this.credentials);
    }

    public class UserCredentialsReaderIterable implements Iterator<String>{
        private Iterator<VwCredentialModel> it;

        public UserCredentialsReaderIterable(final List<VwCredentialModel> credentials) {
            this.it = credentials.iterator();
        }

        @Override
        public boolean hasNext() {
            return it.hasNext();
        }

        @Override
        public String next() {
            StringWriter writer = new StringWriter();
            try {
                IOUtils.copy(it.next().getCredentialData().get(), writer, "UTF-8");
            } catch (IOException e) {
                e.printStackTrace();
            }
            return writer.toString();
        }
    }
}