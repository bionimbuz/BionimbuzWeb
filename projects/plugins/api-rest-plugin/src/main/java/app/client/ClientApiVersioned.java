package app.client;

import app.common.GlobalConstants;

class ClientApiVersioned<T> extends ClientApi<T> {

    public ClientApiVersioned(String url, Class<T> clazz) {
        super(url, clazz);
    }

    @Override
    public String getAPIVersion() {
        return GlobalConstants.API_VERSION;
    }
}
