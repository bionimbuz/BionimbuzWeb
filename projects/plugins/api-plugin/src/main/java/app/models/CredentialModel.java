package app.models;

public class CredentialModel <Model>{
    private String identity;
    private String credential;
    private Model model;
    
    @SuppressWarnings("unused") // Reflection purposes     
    private CredentialModel() {   
    }    
    public CredentialModel(final String identity, final String credential) {
        this.identity = identity;
        this.credential = credential;
    }
    
    public String getCredential() {
        return credential;
    }
    public void setCredential(String credential) {
        this.credential = credential;
    }
    public String getIdentity() {
        return identity;
    }
    public void setIdentity(String identity) {
        this.identity = identity;
    }
    public Model getModel() {
        return model;
    }
    public void setModel(Model model) {
        this.model = model;
    }
}
